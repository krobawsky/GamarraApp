package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;


public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    private static final String TAG = SearchFragment.class.getSimpleName();

    private ListView listview;
    private SearchView searchView;
    private List<String> values;
    private List<Categoria> categoriaTiendas;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        listview = (ListView) view.findViewById(R.id.listview);
        searchView = (SearchView) view.findViewById(R.id.searchview);

        //Lista
        values = new ArrayList<>();

        List<Categoria> categorias = CategoriaRepository.list();
        Log.d(TAG, "categoriasTiendaORM: " + categorias.toString());

        for (Categoria categoria : categorias) {
            values.add(categoria.getNombre());
        }

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1 , values);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), adapter.getItem(position) + " seleccionado!", Toast.LENGTH_SHORT).show();
            }
        });

        //Buscador
        searchView.setQueryHint("Busca el tipo de ropa aquí");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), query + " encontrado!", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

}
