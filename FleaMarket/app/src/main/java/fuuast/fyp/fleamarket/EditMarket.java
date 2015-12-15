package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class EditMarket extends FragmentActivity implements OnMapReadyCallback {

    private Shop shop;
    private GoogleMap mMap;
    private Firebase firebase;
    private String market_id;
    private ImageView img_done;
    private ProgressDialog progressDialog;
    private EditText et_name,et_radius,et_day;

    private MarketDataModel marketDataModel = new MarketDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_market);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://flee-market.firebaseio.com/");
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        progressDialog = new ProgressDialog(EditMarket.this);
        progressDialog.setMessage("\tUpdating...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        et_name = (EditText)findViewById(R.id.et_name);
        et_radius = (EditText)findViewById(R.id.et_radius);
        et_day = (EditText)findViewById(R.id.et_day);

        et_name.setText(marketDataModelSingleTon.getMarket_name().toString());
        et_radius.setText(marketDataModelSingleTon.getRadius().toString());
        et_day.setText(marketDataModelSingleTon.getDay());

        market_id = marketDataModelSingleTon.getMarket_id();

        img_done = (ImageView)findViewById(R.id.img_done);
        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("In EditMarket", "Update image clicked ");
                updateMarket();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(marketDataModelSingleTon.getMarket_lat()), Double.parseDouble(marketDataModelSingleTon.getMarket_lon())), 20));
        getAllShops();
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
                        mMap.addMarker(new MarkerOptions().position(shop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon_c)).title(sdm.getName()+"\n"+sdm.getCategory1()+"\n"+sdm.getCategory2()+"\n"+sdm.getCategory3()));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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

    private void updateMarket(){
        if (!et_name.getText().toString().equals("")&&!et_radius.getText().toString().equals("")&&!et_day.getText().toString().equals("")) {
            img_done.setEnabled(false);
            progressDialog.show();
            marketDataModel.setName(et_name.getText().toString());
            marketDataModel.setRadius(et_radius.getText().toString());
            marketDataModel.setDay(et_day.getText().toString());
            marketDataModel.setAdminID(marketDataModelSingleTon.getAdmin_id());
            marketDataModel.setLatitude(marketDataModelSingleTon.getMarket_lat());
            marketDataModel.setLongitude(marketDataModelSingleTon.getMarket_lon());
            marketDataModel.setAddress(marketDataModelSingleTon.getMarket_address());

            firebase.child("Markets").child(market_id).setValue(marketDataModel, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        Log.d("In EditMarket", firebaseError.getMessage());
                        img_done.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(EditMarket.this, "Error! In Updating Market", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("In EditMarket", "Market Updated");
                        img_done.setEnabled(true);
                        progressDialog.dismiss();
                        marketDataModelSingleTon.setMarket_name(marketDataModel.getName());
                        marketDataModelSingleTon.setRadius(marketDataModel.getRadius());
                        marketDataModelSingleTon.setDay(marketDataModel.getDay());
                        Toast.makeText(EditMarket.this, "Market is updated successfully..", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EditMarket.this,MarketDetails.class);
                        startActivity(i);
                    }
                }
            });
        } else
            Toast.makeText(this, "Data Incomplete", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(EditMarket.this,MarketDetails.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
