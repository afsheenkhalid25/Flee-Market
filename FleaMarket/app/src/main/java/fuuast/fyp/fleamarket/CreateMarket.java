package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateMarket extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    private String lat=null,lon=null,address=null,name=null,day;
    private double adminLocation_lat,adminLocation_lon,marketLocation_lat,marketLocation_lon;
    private EditText et_name,et_radius;
    private Spinner sp;
    private ImageView img;
    private MarketDataModel marketDataModel;
    private UserDataModelSingleTon userDataModelSingleTon;
    private Firebase firebase,markets;
    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_market);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        progressDialog = new ProgressDialog(CreateMarket.this);

        et_name = (EditText) findViewById(R.id.et_marketname);
        et_radius = (EditText) findViewById(R.id.et_radius);

        //getLocation();
        adminLocation_lat = 24.942163;
        adminLocation_lon = 67.110005;

        img = (ImageView) findViewById(R.id.cm_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("In CreateMarket","Create market button clicked");
                if (lat!=null&&lon!=null&&!et_name.getText().toString().equals("")&&!et_radius.getText().toString().equals("")&&!sp.getSelectedItem().equals("<Select Market Day>")) {
                    img.setEnabled(false);
                    marketDataModel.setAdminID(userDataModelSingleTon.getId());
                    marketDataModel.setName(et_name.getText().toString());
                    marketDataModel.setRadius(et_radius.getText().toString());
                    marketDataModel.setLatitude(lat);
                    marketDataModel.setLongitude(lon);
                    marketDataModel.setAddress(address);
                    marketDataModel.setDay(day);
                    createMarket(marketDataModel);
                }else
                    Toast.makeText(CreateMarket.this,"Data Incomplete",Toast.LENGTH_LONG).show();
            }
        });

        marketDataModel=new MarketDataModel();
        userDataModelSingleTon=UserDataModelSingleTon.getInstance();

        setSpinner();

        et_name.setEnabled(false);
        et_radius.setEnabled(false);
        img.setEnabled(false);
        sp.setEnabled(false);

        Toast.makeText(this,"Please Wait For Map",Toast.LENGTH_LONG).show();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.createmarket_map)).getMapAsync(this);
    }

    public void getLocation() {
        Log.d("In CreateMarket", "Getting current location");
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            adminLocation_lat = currentLocation.getLatitude();
            adminLocation_lon = currentLocation.getLongitude();
        }else{
            Toast.makeText(CreateMarket.this,"Network is not Enabled",Toast.LENGTH_SHORT).show();
        }
    }

    public void setSpinner(){
        sp = (Spinner) findViewById(R.id.sp_day);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = sp.getSelectedItem().toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> list = new ArrayList<String>();
        list.add("<Select Market Day>");
        list.add("Monday");
        list.add("Tuesday");
        list.add("Wednesday");
        list.add("Thursday");
        list.add("Friday");
        list.add("Saturday");
        list.add("Sunday");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("In CreateMarket", "Map Ready..");
        mMap=googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        et_name.setEnabled(true);
        et_radius.setEnabled(true);
        img.setEnabled(true);
        sp.setEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        marketLocation_lat = latLng.latitude;
        marketLocation_lon = latLng.longitude;

        GetAddress objGetAddress = new GetAddress(CreateMarket.this,marketLocation_lat,marketLocation_lon);
        objGetAddress.get();

        while (objGetAddress.parsingComplete);
        address=objGetAddress.getCity();
        if (address==null)
            address="Unable to get address";
        Log.d("In CreateMarket","MAP CLICKED.."+" "+lat+" "+lon+" "+address);
    }

    private void createMarket(MarketDataModel marketDataModel) {
        progressDialog.setMessage("\tCreating...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        markets=firebase.child("Markets").push();
        markets.setValue(marketDataModel, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError!=null){
                    img.setEnabled(true);
                    progressDialog.dismiss();
                    Log.d("In CreateMarket",firebaseError.getMessage());
                    Toast.makeText(CreateMarket.this,"Error Creating Market",Toast.LENGTH_SHORT).show();
                }
                else{
                    addMarketToAdmin(userDataModelSingleTon.getId(),markets.getKey());
                }
            }
        });
    }

    private void addMarketToAdmin(String adminID, final String marketID) {
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
                    progressDialog.dismiss();
                    Toast.makeText(CreateMarket.this,"Market Created",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(CreateMarket.this,AdminPanel.class);
                    startActivity(i);
                }
            }
        });
    }

    private void removeMarket() {
        markets.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError!=null){
                    Log.d("REMOVE MARKET.....",firebaseError.getMessage());
                } else {
                    Log.d("REMOVE MARKET.....","successfully removed");
                }
            }
        });
        img.setEnabled(true);
        progressDialog.dismiss();
        Toast.makeText(CreateMarket.this,"Error Creating Market",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(CreateMarket.this,AdminPanel.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
