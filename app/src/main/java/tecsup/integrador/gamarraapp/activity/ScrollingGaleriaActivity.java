package tecsup.integrador.gamarraapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.adapter.ProductosAdapter;
import tecsup.integrador.gamarraapp.adapter.ProductosAdapterActivity;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.Categoria2;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.models.Usuario;
import tecsup.integrador.gamarraapp.models.tiendaCategoria;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class ScrollingGaleriaActivity extends AppCompatActivity {

    private static final String TAG = ScrollingGaleriaActivity.class.getSimpleName();

    private Integer tienda_id;
    private String nombreTienda, telfTienda, ubiTienda, puestoTienda, encargado_id;
    private String latitudTienda, longitudTienda;

    private TextView telefonoTxt;
    private TextView ubicacionTxt;
    private TextView puestoTxt;

    private ImageButton btnCat;
    private ImageButton addIb, smsIb, callIb;

    private Menu collapsedMenu;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton mapFAB;

    private RecyclerView productosList;
    private SearchView productosSearch;

    private ProductosAdapterActivity adapter;
    List<Producto> productos;

    private ApiService service;
    private boolean appBarExpanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_galeria);

        telefonoTxt = (TextView) findViewById(R.id.txtNumero);
        ubicacionTxt = (TextView) findViewById(R.id.txtUbicacion);
        puestoTxt = (TextView) findViewById(R.id.txtPuesto);

        addIb = (ImageButton) findViewById(R.id.add_btn);
        smsIb = (ImageButton) findViewById(R.id.sms_btn);
        callIb = (ImageButton) findViewById(R.id.call_btn);
        btnCat = (ImageButton) findViewById(R.id.btnCat);

        mapFAB = (FloatingActionButton) findViewById(R.id.floatingBtnMap);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //  Vertical offset == 0 indicates appBar is fully  expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });

        productosSearch = (SearchView) findViewById(R.id.productos_sv);
        productosList = (RecyclerView) findViewById(R.id.productos_rv);
        productosList.setHasFixedSize(true);

        productosList.setLayoutManager(new LinearLayoutManager(this));
        productosList.setAdapter(new ProductosAdapterActivity(this));

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        initialize();

                    }
                },1500);
            }
        });

        btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCategorias();
            }
        });

        // setting the action on clicking on the back button ..
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initialize();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null
                && (!appBarExpanded || collapsedMenu.size() != 1)) {
            //collapsed
            collapsedMenu.add("Map")
                    .setIcon(R.drawable.ic_map_white)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        } else {
            //expanded
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling_galeria, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_dueño:
                Comerciante(Integer.parseInt(encargado_id));
                return true;
        }

        if (item.getTitle() == "Map") {
            Intent intent = new Intent(ScrollingGaleriaActivity.this, GamarraMapsActivity.class);
            intent.putExtra("nombre", nombreTienda);
            intent.putExtra("latitud", latitudTienda);
            intent.putExtra("longitud", longitudTienda);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize() {

        tienda_id = getIntent().getExtras().getInt("ID");
        nombreTienda = getIntent().getExtras().getString("nombre");
        telfTienda = getIntent().getExtras().getString("telefono");
        ubiTienda = getIntent().getExtras().getString("ubicacion");
        puestoTienda = getIntent().getExtras().getString("puesto");
        latitudTienda = getIntent().getExtras().getString("latitud");
        longitudTienda = getIntent().getExtras().getString("longitud");
        encargado_id = getIntent().getExtras().getString("encargado_id");

        Log.e(TAG, "tienda_id:" + tienda_id);

        collapsingToolbar.setTitle(nombreTienda);
        //nombreTxt.setText(nombreTienda);
        telefonoTxt.setText("Telf: "+telfTienda);
        ubicacionTxt.setText("Ubicación: "+ubiTienda);
        puestoTxt.setText("Puesto: "+puestoTienda);

        mapFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScrollingGaleriaActivity.this, GamarraMapsActivity.class);
                intent.putExtra("nombre", nombreTienda);
                intent.putExtra("latitud", latitudTienda);
                intent.putExtra("longitud", longitudTienda);
                startActivity(intent);
            }
        });


        addIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:"+ telfTienda);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        smsIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("vnd.android-dir/mms-sms");
                sendIntent.putExtra("address", telfTienda);
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
                            ScrollingGaleriaActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            Integer.parseInt("123"));
                } else {
                    startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+ telfTienda)));
                }
            }
        });

        service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Producto>> call = service.getProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        productos = response.body();
                        Log.d(TAG, "productos: " + productos);

                        List<Producto> values = null;
                        values = new ArrayList<Producto>();

                        for(Producto producto: productos){
                            if (producto.getTienda_id().equalsIgnoreCase(String.valueOf(tienda_id))){
                                values.add(producto);
                            }
                        }

                        adapter = (ProductosAdapterActivity) productosList.getAdapter();
                        adapter.setProductos(values);
                        adapter.notifyDataSetChanged();

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
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ScrollingGaleriaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

        //Buscador
        productosSearch.setQueryHint("Busca el producto aquí");

        productosSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                return true;
            }
        });

        productosSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose()
            {
                initialize();
                return true;
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

                        final Dialog productDialog = new Dialog(ScrollingGaleriaActivity.this);
                        productDialog.setContentView(R.layout.alert_comerciante);

                        ImageView userImg = ( ImageView ) productDialog.findViewById(R.id.imageView);

                        TextView userName = ( TextView ) productDialog.findViewById(R.id.name);
                        TextView userEmail = ( TextView ) productDialog.findViewById(R.id.email);
                        TextView userDni = ( TextView ) productDialog.findViewById(R.id.dni);

                        userName.setText(usuario.getNombre());
                        userEmail.setText(usuario.getEmail());
                        userDni.setText(usuario.getDni());

                        String photoUrl = ApiService.API_BASE_URL + "/perfiles/" + usuario.getImg();
                        Picasso.with(ScrollingGaleriaActivity.this).load(photoUrl).resize(72, 72)
                                .centerCrop().into(userImg);

                        productDialog.show();

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
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ScrollingGaleriaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ScrollingGaleriaActivity.this);
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
                        Toast.makeText(ScrollingGaleriaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<tiendaCategoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ScrollingGaleriaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private String item;
    String[] listItems;

    public void showDialogCategorias() {

        //Lista
        final List<String> values = new ArrayList<>();

        final List<Categoria2> categorias = CategoriaRepository.listCategoriasProducto();
        Log.d(TAG, "categoriasProductoORM: " + categorias.toString());

        for (Categoria2 categoria : categorias) {
            values.add(categoria.getNombre());
        }

        listItems = new String[values.size()];
        Log.e(TAG, "listItems: " + listItems);
        listItems = values.toArray(listItems);
        Log.e(TAG, "listItems: " + listItems);

        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(ScrollingGaleriaActivity.this);
        mBuilder.setTitle("Escoga una categoría");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String it = listItems[i];
                item = it;
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                Log.e(TAG, "itemChoice: " + item);
                Long categoria_id = null;

                if (item.isEmpty()){
                    Toast.makeText(ScrollingGaleriaActivity.this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();

                } else {

                    for (Categoria2 categoria : categorias) {
                        if(categoria.getNombre().equalsIgnoreCase(item)){
                            categoria_id = categoria.getId();
                        }
                    }

                    item = "";

                    List<Producto> values = null;
                    values = new ArrayList<Producto>();

                    for(Producto producto: productos){
                        if (producto.getTienda_id().equalsIgnoreCase(String.valueOf(tienda_id))){
                            if (producto.getCategoria_producto_id().equalsIgnoreCase(String.valueOf(categoria_id))){
                                values.add(producto);
                            }
                        }
                    }

                    adapter = (ProductosAdapterActivity) productosList.getAdapter();
                    adapter.setProductos(values);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("Todos", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                initialize();

            }
        });

        android.support.v7.app.AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

}
