package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MarketMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarketDataModelSingleTon marketDataModelSingleTon;
    Firebase firebase;
    ArrayList<Shop> shops;
    ArrayList<ShopDataModel> allShops;
    Shop shop;
    ArrayList shopIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_map);
        marketDataModelSingleTon=MarketDataModelSingleTon.getInstance();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");
        shops=new ArrayList<Shop>();
        allShops=new ArrayList<ShopDataModel>();
        shopIDs=new ArrayList();
    }

    private void getAllShops() {
        firebase.child("Market_Shops").child(marketDataModelSingleTon.getMarket_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        ShopDataModel sdm=d.getValue(ShopDataModel.class);
                        shop=new Shop(sdm.getLat(),sdm.getLon(),Double.parseDouble(sdm.getWidth()),Double.parseDouble(sdm.getLength()));
                        shops.add(shop);
                        allShops.add(sdm);
                        shopIDs.add(d.getKey());
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
    }
}
