package tecsup.integrador.gamarraapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
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

    private ImageButton backIb;
    private ImageButton addIb, smsIb, callIb;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton productosFAB, mapFAB, perfilFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nombreTxt = (TextView) findViewById(R.id.txtNombre);
        puestoTxt = (TextView) findViewById(R.id.txtPuesto);
        telefonoTxt = (TextView) findViewById(R.id.txtNumero);
        categoriasTxt = (TextView) findViewById(R.id.txtCategorias);

        backIb = (ImageButton) findViewById(R.id.backbtn);
        addIb = (ImageButton) findViewById(R.id.add_btn);
        smsIb = (ImageButton) findViewById(R.id.sms_btn);
        callIb = (ImageButton) findViewById(R.id.call_btn);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.menuFAB);
        mapFAB = (FloatingActionButton) findViewById(R.id.floatingBtnMap);

        tienda_id = getIntent().getExtras().getInt("ID");
        Log.e(TAG, "tienda_id:" + tienda_id);

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

                        mapFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DetailActivity.this, GamarraMapsActivity.class);
                                intent.putExtra("nombre",tienda.getNombre());
                                intent.putExtra("latitud",tienda.getLatitud());
                                intent.putExtra("longitud",tienda.getLongitud());
                                startActivity(intent);
                            }
                        });

                        addIb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri number = Uri.parse("tel:"+tienda.getTelefono());
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                startActivity(callIntent);
                            }
                        });

                        smsIb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setType("vnd.android-dir/mms-sms");
                                sendIntent.putExtra("address", tienda.getTelefono());
                                sendIntent.putExtra("sms_body", "");
                                startActivity(sendIntent);
                            }
                        });

                        callIb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE);

                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(
                                            DetailActivity.this,
                                            new String[]{Manifest.permission.CALL_PHONE},
                                            Integer.parseInt("123"));
                                } else {
                                    startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+ tienda.getTelefono())));
                                }
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


        Call<List<tiendaCategoria>> callTienda_has_categorias = service.getTiendaHasCategoria();
        callTienda_has_categorias.enqueue(new Callback<List<tiendaCategoria>>() {
            @Override
            public void onResponse(Call<List<tiendaCategoria>> call, Response<List<tiendaCategoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<tiendaCategoria> tiendaCategorias = response.body();
                        Log.d(TAG, "tiendaCategorias: " + tiendaCategorias);

                        List<Categoria> categorias = CategoriaRepository.list();
                        Log.d(TAG, "categoriasTiendaORM: " + categorias.toString());

                        List<String> values = new ArrayList<>();

                        for (tiendaCategoria tiendaCategoria : tiendaCategorias) {
                            if (tiendaCategoria.getTienda_id().equalsIgnoreCase(String.valueOf(tienda_id))) {
                                for (Categoria categoria : categorias) {
                                    if(tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(categoria.getId()))){
                                        values.add(categoria.getNombre());
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "values: " + values);

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
