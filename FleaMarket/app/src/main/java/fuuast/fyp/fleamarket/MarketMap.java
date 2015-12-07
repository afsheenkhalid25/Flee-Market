package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MarketMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarketDataModelSingleTon marketDataModelSingleTon;
    Firebase firebase;
    Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_map);
        marketDataModelSingleTon=MarketDataModelSingleTon.getInstance();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");
    }

    private void getAllShops() {
        firebase.child("Market_Shops").child(marketDataModelSingleTon.getMarket_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        ShopDataModel sdm = d.getValue(ShopDataModel.class);
                        shop = new Shop(sdm.getLat(), sdm.getLon(), Double.parseDouble(sdm.getWidth()), Double.parseDouble(sdm.getLength()));
                        createShop(shop);
                        mMap.addMarker(new MarkerOptions().position(shop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon_c)).title(sdm.getName()));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(this,MarketDetails.class);
        startActivity(i);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(marketDataModelSingleTon.getMarket_lat()), Double.parseDouble(marketDataModelSingleTon.getMarket_lon())), 20));
        getAllShops();
    }

    public void createShop(Shop shop){
        PolygonOptions rectOptions = new PolygonOptions()
                .add(shop.getNorthWest())
                .add(shop.getNorthEast())
                .add(shop.getSouthEast())
                .add(shop.getSouthWest())
                .add(shop.getNorthWest())
                .strokeWidth(2)
                .strokeColor(Color.parseColor("#D9FFF8DC"))
                .fillColor(Color.parseColor("#D9F0E68C"));

        Polygon polygon = mMap.addPolygon(rectOptions);
    }
}
