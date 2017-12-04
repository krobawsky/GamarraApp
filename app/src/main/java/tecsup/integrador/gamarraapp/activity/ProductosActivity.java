package tecsup.integrador.gamarraapp.activity;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import layout.OfertasFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.adapter.ProductosAdapterActivity;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class ProductosActivity extends AppCompatActivity {

    private static final String TAG = OfertasFragment.class.getSimpleName();

    private RecyclerView productosList;
    private SearchView productosSearch;

    private ProductosAdapterActivity adapter;
    List<Producto> productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        productosSearch = (SearchView) findViewById(R.id.productos_sv);
        productosList = (RecyclerView) findViewById(R.id.productos_rv);

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

        initialize();
    }

    private void initialize(){

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

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

                        int tienda_id = getIntent().getExtras().getInt("tienda_id");
                        Log.e(TAG, "tienda_id:" + tienda_id);

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
                        Toast.makeText(ProductosActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ProductosActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
}
