package tecsup.integrador.gamarraapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private Button tipoBtn;
    private String item = "";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamarra_maps);

        tipoBtn = (Button) findViewById(R.id.btnTipo);

        tipoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

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

        mMap.addPolyline(new PolylineOptions().geodesic(true).width(4).color(R.color.colorPrimaryDark)
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

        if (ContextCompat.checkSelfPermission(GamarraMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(GamarraMapsActivity.this, "Permiso no concedido.", Toast.LENGTH_LONG).show();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String nombre = extras.getString("nombre", null);
            String latitud = extras.getString("latitud", null);
            String longitud = extras.getString("longitud", null);

            double latitudDouble = Double.parseDouble(latitud);
            double longitudDouble = Double.parseDouble(longitud);

            Log.d(TAG, "LatLng: " + latitudDouble + " , " + longitudDouble);

            LatLng gamarraTienda = new LatLng(latitudDouble, longitudDouble);
            mMap.addMarker(new MarkerOptions().position(gamarraTienda).title(nombre).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marcador_b)));

        } else {


        }

    }

    private void showDialog() {

        final String[] listItems = new String[4];
        listItems[0] = ("Limpio");
        listItems[1] = ("Satelite");
        listItems[2] = ("Mapa");
        listItems[3] = ("Predeterminado");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GamarraMapsActivity.this);
        mBuilder.setTitle("Escoga un tipo de mapa");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String it = listItems[i];
                item = it;
            }
        });

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                Log.e(TAG, "itemChoice: " + item);
                Long categoria_id = null;

                if (item.isEmpty()){
                    Toast.makeText(GamarraMapsActivity.this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();

                } else {
                    if (item.equalsIgnoreCase("Limpio")){
                        item = "";
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mMap.addPolyline(new PolylineOptions().geodesic(true).width(4).color(Color.RED)
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

                    } else if (item.equalsIgnoreCase("Satelite")){
                        item = "";
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mMap.addPolyline(new PolylineOptions().geodesic(true).width(4).color(Color.RED)
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

                    } else if (item.equalsIgnoreCase("Mapa")){
                        item = "";
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        mMap.addPolyline(new PolylineOptions().geodesic(true).width(4).color(Color.RED)
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
                    } else if (item.equalsIgnoreCase("Predeterminado")){
                        item = "";
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.addPolyline(new PolylineOptions().geodesic(true).width(4).color(Color.RED)
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
                    }

                }
            }
        });

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
}
