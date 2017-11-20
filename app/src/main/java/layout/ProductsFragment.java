package layout;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.adapter.ProductosAdapter;
import tecsup.integrador.gamarraapp.adapter.ProductosAdapter2;
import tecsup.integrador.gamarraapp.adapter.TiendasAdapter;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.Categoria2;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;


public class ProductsFragment extends Fragment {

    public ProductsFragment() {
        // Required empty public constructor
    }

    private static final String TAG = ProductsFragment.class.getSimpleName();

    private RecyclerView productosList;
    private SearchView productosSearch;

    private Button categoriaBtn;

    private String item;
    String[] listItems;

    private ProductosAdapter adapter;
    List<Producto> productos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        productosSearch = (SearchView) view.findViewById(R.id.productos_sv);
        productosList = (RecyclerView) view.findViewById(R.id.productos_rv);

        categoriaBtn = (Button) view.findViewById(R.id.btnCategoria);

        productosList.setLayoutManager(new LinearLayoutManager(getActivity()));
        productosList.setAdapter(new ProductosAdapter(this));

        categoriaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        initialize();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipelayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,R.color.colorAccent,R.color.colorPrimaryDark);
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

                        adapter = (ProductosAdapter) productosList.getAdapter();
                        adapter.setProductos(productos);
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

                Context context = getActivity().getApplicationContext();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_conecion, null);

                Toast customtoast = new Toast(context);

                customtoast.setView(layout);
                customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
                customtoast.setDuration(Toast.LENGTH_LONG);
                customtoast.show();
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

    public void showDialog() {

        //Lista
        final List<String> values = new ArrayList<>();

        final List<Categoria2> categorias = CategoriaRepository.listCategoriasProducto();
        Log.d(TAG, "categoriasTiendaORM: " + categorias.toString());

        for (Categoria2 categoria : categorias) {
            values.add(categoria.getNombre());
        }

        listItems = new String[values.size()];
        Log.e(TAG, "listItems: " + listItems);
        listItems = values.toArray(listItems);
        Log.e(TAG, "listItems: " + listItems);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
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
                    Toast.makeText(getActivity(), "Seleccione una categoría", Toast.LENGTH_SHORT).show();

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
                        if (producto.getCategoria_producto_id().equalsIgnoreCase(String.valueOf(categoria_id))){
                            values.add(producto);
                        }
                    }

                    adapter = (ProductosAdapter) productosList.getAdapter();
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

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

}
