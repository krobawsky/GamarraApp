package layout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.activity.DetailActivity;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.CategoriaRepository;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.models.tiendaCategoria;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = StoreFragment.class.getSimpleName();

    private GoogleMap mMap;

    private FloatingActionButton categoriasFAB;

    private String item;
    String[] listItems;
    boolean[] checkedItems;

    private List<Tienda> tiendas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoriasFAB = (FloatingActionButton) view.findViewById(R.id.btnCategoria);

        categoriasFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        initialize();
    }

    private void initialize(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        LatLng gamarra = new LatLng(-12.065770361373355, -77.01431976127282);
        float zoomlevel = 15;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gamarra, zoomlevel));

        mMap.addPolyline(new PolylineOptions().geodesic(true).width(3).color(Color.RED)
                .add(new LatLng(-12.061628, -77.018032))  // 1
                .add(new LatLng(-12.068700, -77.017075))  // 2
                .add(new LatLng(-12.068641, -77.016098))  // 3
                .add(new LatLng(-12.069630, -77.015940))  // 4
                .add(new LatLng(-12.069368, -77.013823))  // 5
                .add(new LatLng(-12.071609, -77.013477))  // 6
                .add(new LatLng(-12.071552, -77.013095))  // 7
                .add(new LatLng(-12.072325, -77.012881))  // 8
                .add(new LatLng(-12.071772, -77.011634))  // 9
                .add(new LatLng(-12.061044, -77.013112))  // 10
                .add(new LatLng(-12.061628, -77.018032))  // 11
        );


        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<List<Tienda>> callTiendas = service.getTiendas();
        callTiendas.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, retrofit2.Response<List<Tienda>> response) {
                try {
                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);
                    if (response.isSuccessful()) {

                        tiendas = response.body();
                        Log.d(TAG, "tiendas: " + tiendas);

                        for (Tienda tienda : tiendas) {

                            double latitudDouble = Double.parseDouble(tienda.getLatitud());
                            double longitudDouble = Double.parseDouble(tienda.getLongitud());
                            Log.d(TAG, "LatLng: " + latitudDouble + " , " + longitudDouble);

                            LatLng gamarraTienda = new LatLng(latitudDouble, longitudDouble);
                            mMap.addMarker(new MarkerOptions().position(gamarraTienda).
                                    title(tienda.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(final Marker marker) {
                                final String title = marker.getTitle();
                                Snackbar snackbar = Snackbar
                                        .make(getView().findViewById(R.id.map), "Desea ver mas detalles de " +title, Snackbar.LENGTH_LONG)
                                        .setAction("Ver", new View.OnClickListener(){
                                            @Override
                                            public void onClick(View view) {
                                                for (Tienda tienda : tiendas) {

                                                    if(tienda.getNombre().equalsIgnoreCase(title)){
                                                        Log.d(TAG, "marker_id: " + tienda.getId());
                                                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                                                        intent.putExtra("ID", tienda.getId());
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        });

                                snackbar.show();

                                return false;
                            }
                        });


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
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showDialog() {

        //Lista
        final List<String> values = new ArrayList<>();

        List<Categoria> categorias = CategoriaRepository.list();
        Log.d(TAG, "categoriasTiendaORM: " + categorias.toString());

        for (Categoria categoria : categorias) {
            values.add(categoria.getNombre());
        }

        listItems = new String[values.size()];
        Log.e(TAG, "listItems: " + listItems);
        listItems = values.toArray(listItems);
        Log.e(TAG, "listItems: " + listItems);
        checkedItems = new boolean[listItems.length];

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



                Log.e(TAG, "itemChoices: " + item);
                Long categoria_id = null;

                List<Categoria> categorias = CategoriaRepository.list();
                Log.d(TAG, "categoriasTiendaORM: " + categorias.toString());

                for (Categoria categoria: categorias){
                    if (categoria.getNombre().equalsIgnoreCase(item)){
                        categoria_id = categoria.getId();
                    }
                }

                final Long finalCategoria_id = categoria_id;
                Log.e(TAG, "categoria_id: " + categoria_id);

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

                                mMap.clear();

                                mMap.addPolyline(new PolylineOptions().geodesic(true).width(3).color(Color.RED)
                                        .add(new LatLng(-12.061628, -77.018032))  // 1
                                        .add(new LatLng(-12.068700, -77.017075))  // 2
                                        .add(new LatLng(-12.068641, -77.016098))  // 3
                                        .add(new LatLng(-12.069630, -77.015940))  // 4
                                        .add(new LatLng(-12.069368, -77.013823))  // 5
                                        .add(new LatLng(-12.071609, -77.013477))  // 6
                                        .add(new LatLng(-12.071552, -77.013095))  // 7
                                        .add(new LatLng(-12.072325, -77.012881))  // 8
                                        .add(new LatLng(-12.071772, -77.011634))  // 9
                                        .add(new LatLng(-12.061044, -77.013112))  // 10
                                        .add(new LatLng(-12.061628, -77.018032))  // 11
                                );

                                for (tiendaCategoria tiendaCategoria : tiendaCategorias) {
                                    if (tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(finalCategoria_id))) {

                                        for (Tienda tienda : tiendas) {

                                            if (tienda.getId() == Integer.parseInt(tiendaCategoria.getTienda_id())){
                                                double latitudDouble = Double.parseDouble(tienda.getLatitud());
                                                double longitudDouble = Double.parseDouble(tienda.getLongitud());
                                                Log.d(TAG, "LatLng: " + latitudDouble + " , " + longitudDouble);

                                                LatLng gamarraTienda = new LatLng(latitudDouble, longitudDouble);
                                                mMap.addMarker(new MarkerOptions().position(gamarraTienda).
                                                        title(tienda.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                            }
                                        }
                                    }
                                }

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
                    public void onFailure(Call<List<tiendaCategoria>> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.toString());
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

            }
        });

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("Limpiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                initialize();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

}
