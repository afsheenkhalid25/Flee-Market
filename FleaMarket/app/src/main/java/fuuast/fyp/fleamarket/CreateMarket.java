package fuuast.fyp.fleamarket;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class CreateMarket extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

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
    }
}
