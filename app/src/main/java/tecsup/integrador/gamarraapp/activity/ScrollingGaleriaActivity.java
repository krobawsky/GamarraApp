package tecsup.integrador.gamarraapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class ScrollingGaleriaActivity extends AppCompatActivity {

    private static final String TAG = ScrollingGaleriaActivity.class.getSimpleName();

    private ApiService service;

    private Integer tienda_id;

    private TextView nombreTxt;
    private TextView telefonoTxt;
    private TextView ubicacionTxt;
    private TextView puestoTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_galeria);

        nombreTxt = (TextView) findViewById(R.id.txtNombre);
        telefonoTxt = (TextView) findViewById(R.id.txtNumero);
        ubicacionTxt = (TextView) findViewById(R.id.txtUbicacion);
        puestoTxt = (TextView) findViewById(R.id.txtPuesto);

        tienda_id = getIntent().getExtras().getInt("ID");
        Log.e(TAG, "tienda_id:" + tienda_id);

        initialize();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void initialize() {

        service = ApiServiceGenerator.createService(ApiService.class);

        Call<Tienda> call = service.showTienda(tienda_id);

        call.enqueue(new Callback<Tienda>() {
            @Override
            public void onResponse(Call<Tienda> call, Response<Tienda> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        final Tienda tienda = response.body();
                        Log.d(TAG, "tienda: " + tienda);

                        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                        collapsingToolbar.setTitle(tienda.getNombre());

                        telefonoTxt.setText(tienda.getTelefono());
                        ubicacionTxt.setText(tienda.getUbicacion());
                        puestoTxt.setText(tienda.getPuesto());


                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(ScrollingGaleriaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<Tienda> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ScrollingGaleriaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }
}
