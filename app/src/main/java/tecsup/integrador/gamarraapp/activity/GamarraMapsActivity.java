package tecsup.integrador.gamarraapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import java.util.List;

import layout.StoreFragment;
import retrofit2.Call;
import retrofit2.Callback;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;

public class GamarraMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = StoreFragment.class.getSimpleName();

    private List<Tienda> tiendas;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamarra_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        LatLng gamarra = new LatLng(-12.065770361373355, -77.01431976127282);
        float zoomlevel = 16;
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

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String nombre = extras.getString("nombre", null);
            String latitud = extras.getString("latitud", null);
            String longitud = extras.getString("longitud", null);

            double latitudDouble = Double.parseDouble(latitud);
            double longitudDouble = Double.parseDouble(longitud);

            Log.d(TAG, "LatLng: " + latitudDouble + " , " + longitudDouble);

            LatLng gamarraTienda = new LatLng(latitudDouble, longitudDouble);
            mMap.addMarker(new MarkerOptions().position(gamarraTienda).title(nombre).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        } else {

            ApiService service = ApiServiceGenerator.createService(ApiService.class);
            Call<List<Tienda>> tiendas = service.getTiendas();
            tiendas.enqueue(new Callback<List<Tienda>>() {
                @Override
                public void onResponse(Call<List<Tienda>> call, retrofit2.Response<List<Tienda>> response) {
                    try {

                        int statusCode = response.code();
                        Log.d(TAG, "HTTP status code: " + statusCode);

                        if (response.isSuccessful()) {

                            final List<Tienda> tiendas = response.body();
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
                                            .make(findViewById(R.id.map), "Desea ver mas detalles de " +title, Snackbar.LENGTH_LONG)
                                            .setAction("Ver", new View.OnClickListener(){
                                                @Override
                                                public void onClick(View view) {

                                                    for (Tienda tienda : tiendas) {

                                                        if(tienda.getNombre().equalsIgnoreCase(title)){
                                                            Log.d(TAG, "marker_id: " + tienda.getId());
                                                            Intent intent = new Intent(GamarraMapsActivity.this, DetailActivity.class);
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
                            Toast.makeText(GamarraMapsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }catch (Throwable x){}
                    }
                }

                @Override
                public void onFailure(Call<List<Tienda>> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    Toast.makeText(GamarraMapsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
