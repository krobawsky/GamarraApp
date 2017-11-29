package tecsup.integrador.gamarraapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class RegisterTiendaActivity extends AppCompatActivity {

    private static final String TAG = RegisterTiendaActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private List<Tienda> tiendas;
    private String comerciante_id;

    private EditText inputNombre;
    private EditText inputTelefono;
    private EditText inputPuesto;

    private ImageButton btnBack;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tienda);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // get id from SharedPreferences
        comerciante_id = sharedPreferences.getString("id", null);
        Log.d(TAG, "comerciante_id: " + comerciante_id);

        listTienda();

        inputNombre = (EditText) findViewById(R.id.txtNombre);
        inputPuesto = (EditText) findViewById(R.id.txtPuesto);
        inputTelefono = (EditText) findViewById(R.id.txtPhone);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

        // Next Button Click event
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                final String nombre = inputNombre.getText().toString().trim();
                final String telefono = inputTelefono.getText().toString().trim();
                final String puesto = inputPuesto.getText().toString().trim();

                if (!nombre.isEmpty() && !puesto.isEmpty() && !telefono.isEmpty()) {

                    for (Tienda tienda : tiendas) {
                        if (tienda.getNombre().equalsIgnoreCase(nombre)) {
                            Toast.makeText(getApplication(), "Esta galería ya está registrada.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    for (Tienda tienda : tiendas) {
                        if (!tienda.getNombre().equalsIgnoreCase(nombre)) {
                            Intent i = new Intent(RegisterTiendaActivity.this, GamarraUbiActivity.class);
                            i.putExtra("comerciante_id", comerciante_id);
                            i.putExtra("nombre", nombre);
                            i.putExtra("telefono", telefono);
                            i.putExtra("puesto", puesto);
                            startActivity(i);
                            return;
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Por favor complete todos los campos!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Carga la lista de tiendas
     */

    private void listTienda() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<List<Tienda>> call = service.getTiendas();
        call.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, retrofit2.Response<List<Tienda>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        tiendas = response.body();
                        Log.d(TAG, "tiendas: " + tiendas);

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(RegisterTiendaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {}
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(RegisterTiendaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
