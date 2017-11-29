package layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.RegisterProductoActivity;
import tecsup.integrador.gamarraapp.adapter.ProductosAdapterComerciante;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;


public class MisOfertasFragment extends Fragment {

    public MisOfertasFragment() {
        // Required empty public constructor
    }

    private static final String TAG = MisOfertasFragment.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    private String tienda_id;

    private RecyclerView productosList;
    private FloatingActionButton RegisterTienda;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mis_ofertas, container, false);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        RegisterTienda = (FloatingActionButton) view.findViewById(R.id.btnRegisterTienda);
        productosList = (RecyclerView) view.findViewById(R.id.recyclerview);
        productosList.setLayoutManager(new LinearLayoutManager(getActivity()));

        productosList.setAdapter(new ProductosAdapterComerciante(this));

        initialize();

        tienda_id = sharedPreferences.getString("tienda_id", null);
        Log.d(TAG, "tienda_id: " + tienda_id);

        RegisterTienda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (tienda_id != null){
                    Intent i = new Intent(getActivity(), RegisterProductoActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "Primero debes registrar su tienda.", Toast.LENGTH_LONG).show();
                }
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipelayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary,R.color.colorPrimaryDark);
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

        return view;
    }

    private void initialize() {

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
                        Log.d(TAG, "productos: " + productos);

                        List<Producto> values = null;
                        values = new ArrayList<Producto>();

                        for (Producto producto : productos) {
                            if (String.valueOf(producto.getTienda_id()).equalsIgnoreCase(tienda_id)) {
                                values.add(producto);
                            }
                        }

                        ProductosAdapterComerciante adapter = (ProductosAdapterComerciante) productosList.getAdapter();
                        adapter.setProductos(values);
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

}
