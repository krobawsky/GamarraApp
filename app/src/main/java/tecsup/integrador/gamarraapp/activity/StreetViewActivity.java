package tecsup.integrador.gamarraapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import tecsup.integrador.gamarraapp.R;

public class StreetViewActivity extends AppCompatActivity {

    private static final String TAG = StreetViewActivity.class.getSimpleName();

    private ImageButton btnBack;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        btnBack = (ImageButton) findViewById(R.id.btnBack);

        String latitud = getIntent().getExtras().getString("latitudSV", "-12.063824839080928");
        String longitud = getIntent().getExtras().getString("longitudSV", "-77.01443206518888");

        final double latitudDouble = Double.parseDouble(latitud);
        final double longitudDouble = Double.parseDouble(longitud);

            Log.d(TAG, "LatLng: " + latitudDouble + " , " + longitudDouble);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        if (savedInstanceState == null) {

                            LatLng streetView = new LatLng(latitudDouble, longitudDouble);

                            panorama.setPosition(streetView);
                        }
                    }
                });

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
    }
}
