package layout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.RegisterTiendaActivity;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.models.tiendaCategoria;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;
import tecsup.integrador.gamarraapp.servicios.ResponseMessage;

public class MiTiendaFragment extends Fragment {

    public MiTiendaFragment() {
        // Required empty public constructor
    }

    private static final String TAG = MiTiendaFragment.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private String usuario_id, tienda_id;

    private ProgressDialog pDialog;

    private Button btnRegister;
    private Button btnCat;
    private TextView nameTienda, telfTienda, ubiTienda, puestoTienda;

    private List<tiendaCategoria> tiendaHasCategorias;
    private List<Categoria> categorias;
    private MultiAutoCompleteTextView multiCategorias;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        btnCat = (Button) view.findViewById(R.id.btnCat);
        nameTienda = (TextView) view.findViewById(R.id.nameTienda);
        telfTienda = (TextView) view.findViewById(R.id.telfTienda);
        ubiTienda = (TextView) view.findViewById(R.id.ubiTienda);
        puestoTienda = (TextView) view.findViewById(R.id.puestoTienda);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        verficarTienda();

        return view;
    }

    /**
     * Verifica si el usuario ya registro una tienda
     */

    private void verficarTienda() {

        //id de usuario SharedPreferences
        usuario_id = sharedPreferences.getString("id", null);
        Log.d(TAG, "usuario_id: " + usuario_id);

        // get tienda_id from SharedPreferences
        tienda_id = sharedPreferences.getString("tienda_id", null);
        Log.d(TAG, "tienda_id: " + tienda_id);

        final ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Tienda>> callTiendas = service.getTiendas();
        callTiendas.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, Response<List<Tienda>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Tienda> tiendas = response.body();
                        Log.d(TAG, "tiendas: " + tiendas);

                        for (Tienda tienda : tiendas) {
                            if (String.valueOf(tienda.getComerciante_id()).equalsIgnoreCase(usuario_id)) {

                                nameTienda.setText(tienda.getNombre());
                                telfTienda.setText("Telf: "+tienda.getTelefono());
                                ubiTienda.setText("Ubicación: "+tienda.getUbicacion());
                                puestoTienda.setText("Puesto: "+tienda.getPuesto());

                                categorias = CategoriaRepository.listCategoriasTienda();
                                Log.d(TAG, "categoriasTiendaORM: " + categorias.toString());

                                Call<List<tiendaCategoria>> callCT = service.getTiendaHasCategoria();
                                callCT.enqueue(new Callback<List<tiendaCategoria>>() {
                                    @Override
                                    public void onResponse(Call<List<tiendaCategoria>> call, Response<List<tiendaCategoria>> response) {
                                        try {

                                            int statusCode = response.code();
                                            Log.d(TAG, "HTTP status code: " + statusCode);

                                            if (response.isSuccessful()) {

                                                tiendaHasCategorias = response.body();
                                                Log.d(TAG, "tiendaHasCategorias: " + tiendaHasCategorias);

                                            } else {
                                                Log.e(TAG, "onError: " + response.errorBody().string());
                                                throw new Exception("Error en el servicio");
                                            }

                                        } catch (Throwable t) {
                                            try {
                                                Log.e(TAG, "onThrowable: " + t.toString(), t);
                                                //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                            }catch (Throwable x){}
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<tiendaCategoria>> call, Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.toString());
                                        //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                });

                                btnCat.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(final View v) {

                                        View view = (LayoutInflater.from(getActivity())).inflate(R.layout.alert_categorias, null);

                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                                        alertBuilder.setView(view);

                                        ListView misCategorias = (ListView) view.findViewById(R.id.misCategorias);
                                        multiCategorias = (MultiAutoCompleteTextView) view.findViewById(R.id.multiAutoCompleteCat);

                                        List<String> valuesSet = new ArrayList<String>();
                                        for (tiendaCategoria tiendaCategoria : tiendaHasCategorias) {
                                            if (tiendaCategoria.getTienda_id().equalsIgnoreCase(tienda_id)) {
                                                for (Categoria categoria : categorias) {
                                                    if(tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(categoria.getId()))){
                                                        valuesSet.add(categoria.getNombre());
                                                    }
                                                }
                                            }
                                        }
                                        Log.d(TAG, "values: " + valuesSet);

                                        String cadena = valuesSet.toString();
                                        int cadena1 = cadena.length();//ubico el tamaño de la cadena
                                        String extraerp = cadena.substring(0,1); // Extraigo laprimera letra
                                        String extraeru = cadena.substring(cadena1-1); //Extraigo la ultima letra letra

                                        String remplazado = cadena.replace(extraerp,""); // quitamos el primer caracter
                                        String remplazadofinal = remplazado.replace(extraeru, "");// se quita el ultimo caracter
                                        Log.d(TAG, "valuesSet: " + remplazadofinal);
                                        //multiCategorias.setText(remplazadofinal);
                                        ArrayAdapter<String> adaptador;
                                        adaptador = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, valuesSet);
                                        misCategorias.setAdapter(adaptador);
                                        misCategorias.setVisibility(View.VISIBLE);

                                        List<String> values = new ArrayList<String>();

                                        for (Categoria categoria : categorias) {
                                            values.add(categoria.getNombre());
                                        }

                                        ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, values);
                                        multiCategorias.setAdapter(adp);
                                        multiCategorias.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                                        multiCategorias.setThreshold(1);

                                        alertBuilder.setCancelable(false);
                                        alertBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                String[] str = multiCategorias.getText().toString().split(", ");

                                                for (Categoria categoria : categorias) {

                                                    for(int i=0; i<str.length; i++) {
                                                        if(categoria.getNombre().equalsIgnoreCase(str[i])){
                                                            agregarCategorias(categoria.getNombre() ,tienda_id, String.valueOf(categoria.getId()));
                                                        }
                                                    }
                                                }
                                            }
                                        });

                                        alertBuilder.setNegativeButton("Quitar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                String[] str = multiCategorias.getText().toString().split(", ");

                                                for (final Categoria categoria : categorias) {

                                                    for(int i=0; i<str.length; i++) {
                                                        if(categoria.getNombre().equalsIgnoreCase(str[i])){

                                                            for (tiendaCategoria tiendaCategoria : tiendaHasCategorias) {

                                                                if (tiendaCategoria.getTienda_id().equalsIgnoreCase(tienda_id)
                                                                        && tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(categoria.getId()))) {

                                                                    quitarCategorias(tiendaCategoria.getId());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        });

                                        alertBuilder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.cancel();

                                            }
                                        });

                                        Dialog dialog = alertBuilder.create();
                                        dialog.show();
                                    }
                                });

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("tienda_id", String.valueOf(tienda.getId())).commit();
                                Log.d(TAG, "tienda_id: " + tienda.getId());

                                return;
                            }
                        }

                        for (Tienda tienda : tiendas) {
                            if (!String.valueOf(tienda.getComerciante_id()).equalsIgnoreCase(usuario_id)) {

                                nameTienda.setVisibility(View.GONE);
                                telfTienda.setVisibility(View.GONE);
                                ubiTienda.setVisibility(View.GONE);
                                puestoTienda.setVisibility(View.GONE);
                                btnCat.setVisibility(View.GONE);
                                btnRegister.setVisibility(View.VISIBLE);

                                btnRegister.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View view) {
                                        Intent i = new Intent(getActivity(), RegisterTiendaActivity.class);
                                        startActivity(i);
                                    }
                                });
                            }
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * Extrae los datos de la tienda
     */

    public void initialize(){

        final ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Tienda>> call = service.getTiendas();
        call.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, retrofit2.Response<List<Tienda>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Tienda> tiendas = response.body();
                        Log.d(TAG, "tiendas: " + tiendas);

                        for (Tienda tienda: tiendas){

                            if (usuario_id.equalsIgnoreCase(tienda.getComerciante_id())){
                                nameTienda.setText(tienda.getNombre());
                                telfTienda.setText("Telf: "+tienda.getTelefono());
                                ubiTienda.setText("Ubicación: "+tienda.getUbicacion());
                                puestoTienda.setText("Puesto: "+tienda.getPuesto());
                            }
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {}
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void agregarCategorias(String nombre ,final String tienda_id, final String categoria_tienda_id) {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call = null;

        pDialog.setMessage("Agregando "+ nombre + " ...");
        showDialog();


        for(tiendaCategoria tiendaCategoria: tiendaHasCategorias){
            if(tienda_id.equalsIgnoreCase(tiendaCategoria.getTienda_id()) && categoria_tienda_id.equals(tiendaCategoria.getCategoria_tienda_id())){

                Toast.makeText(getActivity(), "La categoria "+nombre+" ya esta registrada", Toast.LENGTH_LONG).show();
                hideDialog();
                return;
            }
        }

        for(tiendaCategoria tiendaCategoria: tiendaHasCategorias){

            if(Integer.parseInt(tiendaCategoria.getTienda_id()) != Integer.parseInt(tienda_id) &&
                    Integer.parseInt(tiendaCategoria.getCategoria_tienda_id()) != Integer.parseInt(categoria_tienda_id)){

                call = service.createTiendaHasCategoria(tienda_id, categoria_tienda_id);

                call.enqueue(new Callback<ResponseMessage>() {
                    @Override
                    public void onResponse(Call<ResponseMessage> call, retrofit2.Response<ResponseMessage> response) {
                        try {

                            int statusCode = response.code();
                            Log.d(TAG, "HTTP status code: " + statusCode);
                            hideDialog();

                            if (response.isSuccessful()) {

                                ResponseMessage responseMessage = response.body();
                                Log.d(TAG, "responseMessage: " + responseMessage);

                                Toast.makeText(getActivity(), "Categorias Agregadas!", Toast.LENGTH_LONG).show();
                                MiTiendaFragment tiendaFragment = new MiTiendaFragment();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.content, tiendaFragment);
                                transaction.commit();

                            } else {
                                Log.e(TAG, "onError: " + response.errorBody().string());
                                throw new Exception("Error en el servicio");
                            }

                        } catch (Throwable t) {
                            try {
                                Log.e(TAG, "onThrowable: " + t.toString(), t);
                                hideDialog();
                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (Throwable x) {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessage> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.toString());
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

                return;
            }
        }
    }

    protected void quitarCategorias(Integer id) {

        final ApiService service = ApiServiceGenerator.createService(ApiService.class);

        pDialog.setMessage("Quitando categorías ...");
        showDialog();

        Call<ResponseMessage> call = service.destroyTiendaHasCategoria(id);
        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, retrofit2.Response<ResponseMessage> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);
                    hideDialog();

                    if (response.isSuccessful()) {

                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "responseMessage: " + responseMessage);

                        Toast.makeText(getActivity(), "Categorias Quitadas!", Toast.LENGTH_LONG).show();
                        MiTiendaFragment tiendaFragment = new MiTiendaFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, tiendaFragment);
                        transaction.commit();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        hideDialog();
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * Progress Dialog
     */

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
