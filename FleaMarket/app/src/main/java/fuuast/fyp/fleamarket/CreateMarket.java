package fuuast.fyp.fleamarket;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class CreateMarket extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapClickListener{

    private String lat=null,lon=null,address=null,name=null;
    private EditText et_name;
    private ImageView img;
    private MarketDataModel marketDataModel;
    private UserDataModelSingleTon userDataModelSingleTon;
    private Firebase firebase,markets;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_market);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        et_name= (EditText) findViewById(R.id.et_marketname);
        img = (ImageView) findViewById(R.id.cm_img);
        img.setOnClickListener(this);

        et_name.setEnabled(false);
        img.setEnabled(false);

        marketDataModel=new MarketDataModel();
        userDataModelSingleTon=UserDataModelSingleTon.getInstance();

        Toast.makeText(this,"Please Wait For Map",Toast.LENGTH_LONG).show();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.createmarket_map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP......", "Ready");
        mMap=googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        et_name.setEnabled(true);
        img.setEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        lat= ""+latLng.latitude;
        lon= ""+latLng.longitude;
        GetAddress objGetAddress=new GetAddress(CreateMarket.this,Double.parseDouble(lat),Double.parseDouble(lon));
        objGetAddress.get();
        while (objGetAddress.parsingComplete);
        address=objGetAddress.getCity();
        if (address==null)
            address="Unable to get address";
        Log.d("ON MAP CLICK.....","\n"+lat+"\n"+lon+"\n"+address);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cm_img:
                Log.d("CREATE MARKET.....","CLICKED");
                if (lat!=null&&lon!=null&&!et_name.getText().toString().equals("")){
                    marketDataModel.setAdminID(userDataModelSingleTon.getId());
                    marketDataModel.setName(et_name.getText().toString());
                    marketDataModel.setLatitude(lat);
                    marketDataModel.setLongitude(lon);
                    marketDataModel.setAddress(address);
                    marketDataModel.setImageURL("n/a");
                    createMarket(marketDataModel);
                }
                else {
                    Toast.makeText(this,"Data Incomplete",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void createMarket(MarketDataModel marketDataModel){
        markets=firebase.child("Markets").push();

        markets.setValue(marketDataModel, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError!=null){
                    Log.d("CREATE MARKET.....",firebaseError.getMessage());
                    Toast.makeText(CreateMarket.this,"Error Creating Market",Toast.LENGTH_SHORT).show();
                }
                else{
                    addMarkettoadmin(userDataModelSingleTon.getId(),markets.getKey());
                }
            }
        });
    }

    private void addMarkettoadmin(String adminID, final String marketID){
        HashMap<String,Object> hashMap=new HashMap<String, Object>();
        hashMap.put("marketID",marketID);
        firebase.child("Admin_Market").child(adminID).push().setValue(hashMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError!=null){
                    Log.d("ADD MARKET TO ADMIN.....",firebaseError.getMessage());
                    removeMarket();
                }
                else{
                    Toast.makeText(CreateMarket.this,"Market Created",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeMarket(){
        markets.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError!=null){
                    Log.d("REMOVE MARKET.....",firebaseError.getMessage());
                }
                else {
                    Toast.makeText(CreateMarket.this,"Error Creating Market",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
