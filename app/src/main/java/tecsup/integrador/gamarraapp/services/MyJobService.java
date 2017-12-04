package tecsup.integrador.gamarraapp.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.LoginActivity;
import tecsup.integrador.gamarraapp.activity.UserActivity;
import tecsup.integrador.gamarraapp.adapter.ProductosAdapter;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class MyJobService extends JobService {

    private static final String TAG = MyJobService.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onStartJob(JobParameters job) {

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Log.d(TAG, "Calling onStartJob...");

        // TODO...

        // Notification Builder
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Intent
        Intent intent =  new Intent(this, UserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Producto>> call = service.getProductos();
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Producto> productos = response.body();
                        Log.d(TAG, "ofertas_size r: " + productos.size());

                        //id de usuario SharedPreferences
                        int ofertas_size = sharedPreferences.getInt("ofertas_size", 0);
                        Log.d(TAG, "ofertas_size g: " + ofertas_size);

                        if (productos.size() < ofertas_size || ofertas_size == productos.size()){

                            Log.d(TAG, "ofertas_size, no hay nuevas ofertas: " + productos.size());
                            // Save to SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("ofertas_size", productos.size()).apply();

                        } else if (productos.size() > ofertas_size) {

                            /*
                            //Lista
                            List<String> values = new ArrayList<>();

                            for (Producto producto : productos) {
                                values.add(producto.getNombre());
                            }

                            Iterator<String> iterator = values.iterator();
                            while(iterator.hasNext()){
                                //System.out.println(iterator.next());
                                //System.out.println(iterator.previous());

                                Log.e(TAG, "Iterator Next: " + iterator.next());
                                //Log.e(TAG, "Iterator Previous: " + iterator.previous());

                                for(int i = 0; i <= productos.size(); i++) {

                                    Log.e(TAG, "Iterator val: " +i+" - "+ productos.size());

                                    if (i == productos.size()-1){
                                        //System.out.println("at end of the list");
                                        Log.e(TAG, "Iterator End: " + iterator.next());
                                    }
                                }

                            }*/

                            // Notification
                            Notification notification = builder
                                    .setContentTitle("Gamarra App")
                                    .setContentText("Se a registrado una nueva oferta, ingresa y aprovecha esta oferton.")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .build();

                            // Notification manager
                            NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(1, notification);

                            // Play sound
                            RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).play();

                            // Save to SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("ofertas_size", productos.size()).apply();
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        //throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        //Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
            }

        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
