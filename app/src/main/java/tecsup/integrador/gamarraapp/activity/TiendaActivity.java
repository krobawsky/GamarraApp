package tecsup.integrador.gamarraapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import tecsup.integrador.gamarraapp.models.Usuario;
import tecsup.integrador.gamarraapp.models.tiendaCategoria;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class TiendaActivity extends AppCompatActivity {

    private static final String TAG = TiendaActivity.class.getSimpleName();

    private ApiService service;

    private Integer tienda_id;

    private TextView nombreTxt;
    private TextView telefonoTxt;
    private TextView ubicacionTxt;
    private TextView puestoTxt;

    private ImageButton backIb, listIb;
    private ImageButton addIb, smsIb, callIb;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton productosFAB, mapFAB, perfilFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);

        nombreTxt = (TextView) findViewById(R.id.txtNombre);
        telefonoTxt = (TextView) findViewById(R.id.txtNumero);
        ubicacionTxt = (TextView) findViewById(R.id.txtUbicacion);
        puestoTxt = (TextView) findViewById(R.id.txtPuesto);

        listIb = (ImageButton) findViewById(R.id.listbtn);
        backIb = (ImageButton) findViewById(R.id.backbtn);
        addIb = (ImageButton) findViewById(R.id.add_btn);
        smsIb = (ImageButton) findViewById(R.id.sms_btn);
        callIb = (ImageButton) findViewById(R.id.call_btn);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.menuFAB);
        perfilFAB = (FloatingActionButton) findViewById(R.id.floatingBtnPerfil);
        mapFAB = (FloatingActionButton) findViewById(R.id.floatingBtnMap);
        productosFAB = (FloatingActionButton) findViewById(R.id.floatingBtnProductos);

        tienda_id = getIntent().getExtras().getInt("ID");
        Log.e(TAG, "tienda_id:" + tienda_id);

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Categorias();
            }
        });

        initialize();
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

                        nombreTxt.setText(tienda.getNombre());
                        telefonoTxt.setText(tienda.getTelefono());
                        ubicacionTxt.setText(tienda.getUbicacion());
                        puestoTxt.setText(tienda.getPuesto());


                        perfilFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Comerciante(Integer.parseInt(tienda.getComerciante_id()));
                            }
                        });

                        mapFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TiendaActivity.this, GamarraMapsActivity.class);
                                intent.putExtra("nombre",tienda.getNombre());
                                intent.putExtra("latitud",tienda.getLatitud());
                                intent.putExtra("longitud",tienda.getLongitud());
                                startActivity(intent);
                            }
                        });

                        productosFAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TiendaActivity.this, ProductosActivity.class);
                                intent.putExtra("tienda_id", tienda_id);
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
                                            TiendaActivity.this,
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
                        Toast.makeText(TiendaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<Tienda> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void Comerciante(int id){

        Call<Usuario> call = service.showUsuario(id);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        final Usuario usuario = response.body();
                        Log.d(TAG, "usuario: " + usuario);

                        final Dialog productDialog = new Dialog(TiendaActivity.this);
                        productDialog.setContentView(R.layout.alert_comerciante);

                        TextView userName = ( TextView ) productDialog.findViewById(R.id.name);
                        TextView userEmail = ( TextView ) productDialog.findViewById(R.id.email);
                        TextView userDni = ( TextView ) productDialog.findViewById(R.id.dni);

                        userName.setText(usuario.getNombre());
                        userEmail.setText(usuario.getEmail());
                        userDni.setText(usuario.getDni());

                        productDialog.show();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(TiendaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void Categorias(){

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

                        List<Categoria> categorias = CategoriaRepository.listCategoriasTienda();
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

                        String[] listItems = new String[values.size()];
                        Log.e(TAG, "listItems: " + listItems);
                        listItems = values.toArray(listItems);
                        Log.e(TAG, "listItems: " + listItems);

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TiendaActivity.this);
                        mBuilder.setTitle("Categorías:");
                        mBuilder.setItems(listItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(TiendaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<tiendaCategoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
}
