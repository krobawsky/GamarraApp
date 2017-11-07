package tecsup.integrador.gamarraapp.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.models.tiendaCategoria;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private Integer tienda_id;

    private TextView nombreTxt;
    private TextView puestoTxt;
    private TextView telefonoTxt;
    private TextView categoriasTxt;
    private ListView categoriasListV;
    private FloatingActionButton mapBtn;

    private List<Categoria> categoriaTiendas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nombreTxt = (TextView) findViewById(R.id.txtNombre);
        puestoTxt = (TextView) findViewById(R.id.txtPuesto);
        telefonoTxt = (TextView) findViewById(R.id.txtNumero);
        categoriasTxt = (TextView) findViewById(R.id.txtCategorias);
        categoriasListV = (ListView) findViewById(R.id.listview);
        mapBtn = (FloatingActionButton) findViewById(R.id.floatingBtnMap);

        tienda_id = getIntent().getExtras().getInt("ID");
        Log.e(TAG, "tienda_id:" + tienda_id);

        initialize();
    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<Tienda> call = service.showProducto(tienda_id);

        call.enqueue(new Callback<Tienda>() {
            @Override
            public void onResponse(Call<Tienda> call, Response<Tienda> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        final Tienda tienda = response.body();
                        Log.d(TAG, "tienda: " + tienda);

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

        Call<List<Categoria>> call2 = service.getCategoriaTienda();
        call2.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {
                        categoriaTiendas = response.body();
                        Log.d(TAG, "categoriaTienda: " + categoriaTiendas);

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
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        Call<List<tiendaCategoria>> call3 = service.getTiendaHasCategoria();
        call3.enqueue(new Callback<List<tiendaCategoria>>() {
            @Override
            public void onResponse(Call<List<tiendaCategoria>> call, Response<List<tiendaCategoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<tiendaCategoria> tiendaCategorias = response.body();
                        Log.d(TAG, "tiendaCategorias: " + tiendaCategorias);


                        List<String> values = new ArrayList<>();

                        for (tiendaCategoria tiendaCategoria : tiendaCategorias) {
                            if (tiendaCategoria.getTienda_id().equalsIgnoreCase(String.valueOf(tienda_id))) {
                                for (Categoria categoria : categoriaTiendas) {
                                    if(tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(categoria.getId()))){
                                        values.add(categoria.getNombre());
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "values: " + values);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(DetailActivity.this, android.R.layout.simple_list_item_1 , values);
                        categoriasListV.setAdapter(adapter);

                        if (values.size() == 0){
                            categoriasTxt.setText("No tiene");

                        } else {
                            categoriasTxt.setText(values.toString());
                        }


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
            public void onFailure(Call<List<tiendaCategoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }
}
