package layout;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.adapter.TiendasAdapter;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.models.tiendaCategoria;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class TiendasFragment extends Fragment {

    public TiendasFragment() {
        // Required empty public constructor
    }

    private static final String TAG = TiendasFragment.class.getSimpleName();

    private ProgressDialog pDialog;

    private RecyclerView tiendasList;
    private SearchView tiendasSearch;

    private Button categoriaBtn;

    private String item = "";
    String[] listItems;

    private TiendasAdapter adapter;
    List<Tienda> tiendas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        tiendasSearch = (SearchView) view.findViewById(R.id.tiendas_sv);
        tiendasList = (RecyclerView) view.findViewById(R.id.tiendas_rv);

        categoriaBtn = (Button) view.findViewById(R.id.btnCategoria);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        tiendasList.setLayoutManager(new LinearLayoutManager(getActivity()));
        tiendasList.setAdapter(new TiendasAdapter(this));

        categoriaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCategorias();
            }
        });

        initialize();

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

        Call<List<Tienda>> call = service.getTiendas();

        call.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, Response<List<Tienda>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        tiendas = response.body();
                        Log.d(TAG, "tiendas: " + tiendas);

                        adapter = (TiendasAdapter) tiendasList.getAdapter();
                        adapter.setTiendas(tiendas);
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
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
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
        tiendasSearch.setQueryHint("Busca la tienda aquí");

        tiendasSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        tiendasSearch.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose()
                {
                    initialize();
                    return true;
                }
        });

    }

    private void showDialogCategorias() {

        //Lista
        final List<String> values = new ArrayList<>();

        final List<Categoria> categorias = CategoriaRepository.listCategoriasTienda();
        Log.d(TAG, "categoriasTiendaORM: " + categorias.toString());

        for (Categoria categoria : categorias) {
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

                    for (Categoria categoria : categorias) {
                        if(categoria.getNombre().equalsIgnoreCase(item)){
                            categoria_id = categoria.getId();
                        }
                    }

                    item = "";

                    pDialog.setMessage("Espere ...");
                    showDialog();

                    final Long finalCategoria_id = categoria_id;

                    ApiService service = ApiServiceGenerator.createService(ApiService.class);

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

                                    List<Tienda> values = null;
                                    values = new ArrayList<>();

                                    for (tiendaCategoria tiendaCategoria : tiendaCategorias) {
                                        if (tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(finalCategoria_id))) {
                                            for (Tienda tienda : tiendas) {
                                                if (tienda.getId() == Integer.parseInt(tiendaCategoria.getTienda_id())){
                                                    values.add(tienda);
                                                }
                                            }
                                        }
                                    }

                                    adapter = (TiendasAdapter) tiendasList.getAdapter();
                                    adapter.setTiendas(values);
                                    adapter.notifyDataSetChanged();

                                    hideDialog();

                                } else {
                                    hideDialog();
                                    Log.e(TAG, "onError: " + response.errorBody().string());
                                    throw new Exception("Error en el servicio");
                                }

                            } catch (Throwable t) {
                                try {
                                    hideDialog();
                                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                }catch (Throwable x){}
                            }
                        }

                        @Override
                        public void onFailure(Call<List<tiendaCategoria>> call, Throwable t) {
                            hideDialog();
                            Log.e(TAG, "onFailure: " + t.toString());
                            //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    });
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
