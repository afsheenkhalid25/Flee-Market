package fuuast.fyp.fleamarket;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class CreateMarket extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String lat,lon,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_market);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.createmarket_map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP......", "Ready");
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                lat= ""+latLng.latitude;
                lon= ""+latLng.longitude;

            }
        });
    }
}
