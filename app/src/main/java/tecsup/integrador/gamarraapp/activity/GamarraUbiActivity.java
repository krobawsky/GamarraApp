package tecsup.integrador.gamarraapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import tecsup.integrador.gamarraapp.R;
import tecsup.integrador.gamarraapp.helper.HttpDataHandler;
import tecsup.integrador.gamarraapp.servicios.ApiService;
import tecsup.integrador.gamarraapp.servicios.ApiServiceGenerator;
import tecsup.integrador.gamarraapp.servicios.ResponseMessage;

public class GamarraUbiActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback, LocationListener {

    private static final String TAG = GamarraUbiActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    private GoogleMap mMap;
    private TextView txtUbicacion;

    private ImageButton btnBack;

    private String comerciante_id, nombreTienda, telefonoTienda, puestoTienda;

    private String address;
    private double latitud = 0;
    private double longitud = 0;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamarra_maps_ubi);

        txtUbicacion = (TextView) findViewById(R.id.txtUbicacion);
        btnBack = (ImageButton) findViewById(R.id.btnBack);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        initialize();

        comerciante_id = getIntent().getExtras().getString("comerciante_id", null);
        nombreTienda = getIntent().getExtras().getString("nombre", null);
        telefonoTienda = getIntent().getExtras().getString("telefono", null);
        puestoTienda = getIntent().getExtras().getString("puesto", null);

        Log.d(TAG, "Datos tienda: " + nombreTienda+ ","+ telefonoTienda +", "+ puestoTienda);

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initialize(){
        if (mMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUpMap();

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        LatLng gamarra = new LatLng(-12.065770361373355, -77.01431976127282);
        float zoomlevel = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gamarra, zoomlevel));

        mMap.addPolyline(new PolylineOptions().geodesic(true).width(4).color(getResources().getColor(R.color.colorPrimary1))
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

        if (ContextCompat.checkSelfPermission(GamarraUbiActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(GamarraUbiActivity.this, "Permiso no concedido.", Toast.LENGTH_LONG).show();
        }
    }

    public void Registrar(View view){

        if(latitud == 0 && longitud == 0){
            Toast.makeText(getApplicationContext(),"Marque su tienda antes de guardar.", Toast.LENGTH_LONG).show();

        } else {
            ApiService service = ApiServiceGenerator.createService(ApiService.class);
            Call<ResponseMessage> call = null;

            pDialog.setMessage("Registrando ...");
            showDialog();

            call = service.createTienda(nombreTienda, puestoTienda, telefonoTienda, String.valueOf(latitud), String.valueOf(longitud), address, comerciante_id);

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

                            Toast.makeText(getApplicationContext(), "Tienda registrada correctamente!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(GamarraUbiActivity.this, UserComercianteActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.e(TAG, "onError: " + response.errorBody().string());
                            throw new Exception("Error en el servicio");
                        }

                    } catch (Throwable t) {
                        try {
                            Log.e(TAG, "onThrowable: " + t.toString(), t);
                            hideDialog();
                            Toast.makeText(GamarraUbiActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Throwable x) {
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessage> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    Toast.makeText(GamarraUbiActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }
    }


    private void setUpMap()
    {
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {

        mMap.clear();
        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(new PolylineOptions().geodesic(true).width(2).color(getResources().getColor(R.color.colorPrimary1))
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

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Tu tienda")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marcador_b)));

        latitud = point.latitude;
        longitud = point.longitude;

        Log.d(TAG, "LatLng: " + latitud + ", "+ longitud);
        new GetAddress().execute(String.format("%.4f,%.4f",latitud,longitud));
        Log.d(TAG, "address: " + address);

    }

    @Override
    public void onMapLongClick(LatLng point) {

        mMap.clear();
        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(new PolylineOptions().geodesic(true).width(2).color(getResources().getColor(R.color.colorPrimary1))
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

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Tu tienda")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marcador_b)));

        latitud = point.latitude;
        longitud = point.longitude;

        Log.d(TAG, "LatLng: " + latitud + ", "+ longitud);
        new GetAddress().execute(String.format("%.4f,%.4f",latitud,longitud));
        Log.d(TAG, "address: " + address);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        new GetAddress().execute(String.format("%.4f,%.4f",lat,lng));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private class GetAddress extends AsyncTask<String,Void,String> {

        ProgressDialog dialog = new ProgressDialog(GamarraUbiActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Espere ...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                double lat = Double.parseDouble(strings[0].split(",")[0]);
                double lng = Double.parseDouble(strings[0].split(",")[1]);
                String response;
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.4f,%.4f&sensor=false",lat,lng);
                response = http.GetHTTPData(url);
                return response;
            }
            catch (Exception ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                address = ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
                txtUbicacion.setVisibility(View.VISIBLE);
                txtUbicacion.setText("Ubicación: "+address);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(dialog.isShowing())
                dialog.dismiss();
        }
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
