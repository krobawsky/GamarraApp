package layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.RegisterTiendaActivity;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class MiTiendaFragment extends Fragment {

    public MiTiendaFragment() {
        // Required empty public constructor
    }

    private static final String TAG = MiTiendaFragment.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private String usuario_id;

    private Button btnRegister;
    private TextView nameTienda, telfTienda, ubiTienda, puestoTienda;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        nameTienda = (TextView) view.findViewById(R.id.nameTienda);
        telfTienda = (TextView) view.findViewById(R.id.telfTienda);
        ubiTienda = (TextView) view.findViewById(R.id.ubiTienda);
        puestoTienda = (TextView) view.findViewById(R.id.puestoTienda);

        verficarTienda();

        return view;
    }

    /**
     * Verifica si el usuario ya registro una tienda
     */

    private void verficarTienda() {

        //id de usuario SharedPreferences
        usuario_id = sharedPreferences.getString("id", null);
        Log.d(TAG, "usuario_id: " + usuario_id);

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Tienda>> callTiendas = service.getTiendas();
        callTiendas.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, Response<List<Tienda>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Tienda> tiendas = response.body();
                        Log.d(TAG, "tiendas: " + tiendas);

                        for (Tienda tienda : tiendas) {
                            if (String.valueOf(tienda.getComerciante_id()).equalsIgnoreCase(usuario_id)) {

                                nameTienda.setText(tienda.getNombre());
                                telfTienda.setText("Telf: "+tienda.getTelefono());
                                ubiTienda.setText("Ubicación: "+tienda.getUbicacion());
                                puestoTienda.setText("Puesto: "+tienda.getPuesto());

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("tienda_id", String.valueOf(tienda.getId())).commit();
                                Log.d(TAG, "tienda_id: " + tienda.getId());

                                return;
                            }
                        }

                        for (Tienda tienda : tiendas) {
                            if (!String.valueOf(tienda.getComerciante_id()).equalsIgnoreCase(usuario_id)) {

                                nameTienda.setVisibility(View.GONE);
                                telfTienda.setVisibility(View.GONE);
                                ubiTienda.setVisibility(View.GONE);
                                puestoTienda.setVisibility(View.GONE);
                                btnRegister.setVisibility(View.VISIBLE);

                                btnRegister.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View view) {
                                        Intent i = new Intent(getActivity(), RegisterTiendaActivity.class);
                                        startActivity(i);
                                    }
                                });
                            }
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * Extrae los datos de la tienda
     */

    public void initialize(){

        final ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Tienda>> call = service.getTiendas();

        call.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, retrofit2.Response<List<Tienda>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Tienda> tiendas = response.body();
                        Log.d(TAG, "tiendas: " + tiendas);

                        for (Tienda tienda: tiendas){

                            if (usuario_id.equalsIgnoreCase(tienda.getComerciante_id())){
                                nameTienda.setText(tienda.getNombre());
                                telfTienda.setText("Telf: "+tienda.getTelefono());
                                ubiTienda.setText("Ubicación: "+tienda.getUbicacion());
                                puestoTienda.setText("Puesto: "+tienda.getPuesto());
                            }
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {}
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
