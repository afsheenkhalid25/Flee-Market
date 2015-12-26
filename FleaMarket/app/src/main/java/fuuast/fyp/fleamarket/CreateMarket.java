package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateMarket extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private String address=null,day,currentCity,marketCity;
    private EditText et_name,et_radius;
    private Spinner sp;
    private ImageView img;
    private MarketDataModel marketDataModel;
    private UserDataModelSingleTon userDataModelSingleTon;
    private Firebase firebase,markets;
    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private Location currentLocation,marketLocation;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_market);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        progressDialog = new ProgressDialog(CreateMarket.this);

        et_name = (EditText) findViewById(R.id.et_marketname);
        et_radius = (EditText) findViewById(R.id.et_radius);

        img = (ImageView) findViewById(R.id.cm_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("In CreateMarket","Create market button clicked");
                if (marketLocation!=null&&!et_name.getText().toString().equals("")&&!et_radius.getText().toString().equals("")&&!sp.getSelectedItem().equals("<Select Market Day>")) {
                    img.setEnabled(false);
                    new GetCurrentCity1().execute(currentLocation);
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

        Toast.makeText(this,"Getting Your Location",Toast.LENGTH_LONG).show();

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
        marketLocation=new Location("market_location");
        marketLocation.setLatitude(latLng.latitude);
        marketLocation.setLongitude(latLng.longitude);
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
        mGoogleApiClient.disconnect();
        Intent i=new Intent(CreateMarket.this,AdminPanel.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        finish();
    }

    @Override
    public void onConnected(Bundle bundle) {
        currentLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (currentLocation!=null){
            Toast.makeText(this,"Location Found",Toast.LENGTH_LONG).show();
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.createmarket_map)).getMapAsync(this);
        }
        else {
            Toast.makeText(this,"Location Not Found",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

    private class GetCurrentCity1 extends AsyncTask<Location,Boolean,Boolean>{

        @Override
        protected Boolean doInBackground(Location... params) {
            Location location=params[0];
            Geocoder geocoder= new Geocoder(CreateMarket.this, Locale.getDefault());
            List<Address> addresses=null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                currentCity = address.getLocality();
                return true;
            }
            else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean check) {
            super.onPostExecute(check);
            if (check){
                Toast.makeText(CreateMarket.this,currentCity,Toast.LENGTH_LONG).show();
                new GetCurrentCity2().execute(marketLocation);
            }
            else {
                Toast.makeText(CreateMarket.this,"Unable to find you city",Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GetCurrentCity2 extends AsyncTask<Location,Boolean,Boolean>{

        @Override
        protected Boolean doInBackground(Location... params) {
            Location location=params[0];
            Geocoder geocoder= new Geocoder(CreateMarket.this, Locale.getDefault());
            List<Address> addresses=null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                marketCity = address.getLocality();
                return true;
            }
            else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean check) {
            if (check){
                super.onPostExecute(check);
                Toast.makeText(CreateMarket.this,marketCity,Toast.LENGTH_SHORT).show();
                if (currentCity.toUpperCase().equals(marketCity.toUpperCase())){
                    marketDataModel.setAdminID(userDataModelSingleTon.getId());
                    marketDataModel.setName(et_name.getText().toString());
                    marketDataModel.setRadius(et_radius.getText().toString());
                    marketDataModel.setLatitude(marketLocation.getLatitude()+"");
                    marketDataModel.setLongitude(marketLocation.getLongitude()+"");
                    marketDataModel.setAddress(address);
                    marketDataModel.setDay(day);
                    createMarket(marketDataModel);
                }
                else {
                    Toast.makeText(CreateMarket.this,"You Cannot Create Market Out Of Your City",Toast.LENGTH_LONG).show();
                }

            }
            else {
                Toast.makeText(CreateMarket.this,"Unable To Find Market's City",Toast.LENGTH_LONG).show();
            }
        }
    }
}
