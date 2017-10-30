package tecsup.integrador.gamarraapp.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.datos.Tienda;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private Integer id;

    private TextView nombreTxt;
    private TextView puestoTxt;
    private TextView telefonoTxt;
    private FloatingActionButton mapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nombreTxt = (TextView) findViewById(R.id.txtNombre);
        puestoTxt = (TextView) findViewById(R.id.txtNumero);
        telefonoTxt = (TextView) findViewById(R.id.txtPuesto);
        mapBtn = (FloatingActionButton) findViewById(R.id.floatingBtnMap); 

        id = getIntent().getExtras().getInt("ID");
        Log.e(TAG, "id:" + id);

        initialize();
    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<Tienda> call = service.showProducto(id);

        call.enqueue(new Callback<Tienda>() {
            @Override
            public void onResponse(Call<Tienda> call, Response<Tienda> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        final Tienda tienda = response.body();
                        Log.d(TAG, "producto: " + tienda);

                        nombreTxt.setText(tienda.getNombre());
                        puestoTxt.setText(tienda.getPuesto());
                        telefonoTxt.setText(tienda.getTelefono());

                        mapBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DetailActivity.this, GamarraMapsActivity.class);
                                intent.putExtra("nombre",tienda.getNombre());
                                intent.putExtra("latitud",tienda.getLatitud());
                                intent.putExtra("longitud",tienda.getLongitud());
                                startActivity(intent);
                            }
                        });

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<Tienda> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
}
