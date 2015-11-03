package fuuast.fyp.fleamarket;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


public class CreateShop extends ActionBarActivity implements View.OnClickListener{

    private String name,width,length,market_Id,user_Id,ctgry_one,ctgry_two,ctgry_three;
    private double lat,lon,NW_lat,NW_lon,NE_lat,NE_lon,SW_lat,SW_lon,SE_lat,SE_lon;
    private ArrayList market_id,market_names,market_address;
    private EditText et_name,et_width,et_length;
    private Firebase firebase;
    private Spinner mySpinner;

    private MarketDataModel marketDataModel = new MarketDataModel();
    private ShopDataModel shopDataModel = new ShopDataModel();
    private ShopDataModelSingleTon shopDataModelSingleTon = ShopDataModelSingleTon.getInstance();
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        et_name = (EditText)findViewById(R.id.cs_et_name);
        et_width = (EditText)findViewById(R.id.cs_et_width);
        et_length = (EditText)findViewById(R.id.cs_et_length);

        mySpinner = (Spinner) findViewById(R.id.spinner);
        mySpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                market_Id = market_id.get(position).toString();
                Log.d("Position",market_Id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        market_names=new ArrayList();
        market_address=new ArrayList();
        market_id=new ArrayList();

        getMarketList();
        getCategoryList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                Intent i = new Intent(CreateShop.this, ShopkeeperPanel.class);
                startActivity(i);
                break;
            case R.id.btn_next:
                setParameters();
                break;
        }
    }

    private void getMarketList()
    {
        market_id.clear();
        market_names.clear();
        market_address.clear();
        firebase.child("Markets").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                market_id.add(dataSnapshot.getKey().toString());
                marketDataModel = dataSnapshot.getValue(MarketDataModel.class);
                market_names.add(marketDataModel.getName());
                market_address.add(marketDataModel.getAddress());
                mySpinner.setAdapter(new AdminPanel_CustomAdapter(CreateShop.this,market_names,market_address));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getCategoryList(){

    }

    public void setParameters(){

        name = et_name.getText().toString();
        width = et_width.getText().toString();
        length = et_length.getText().toString();
        user_Id = userDataModelSingleTon.getId().toString();
        lat=32.445005;
        lon=122.344445;

        //getLocation();
        setShopData();

        if(name.equals("")||width.equals("")||length.equals("")){
            Toast.makeText(CreateShop.this,"First fill complete details..",Toast.LENGTH_SHORT).show();
        } else{
            firebase.child("ShopData").push().setValue(shopDataModel, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if(firebaseError!=null){
                        Toast.makeText(CreateShop.this,"Try Creating Shop Later",Toast.LENGTH_SHORT).show();
                    } else{
                        Intent j = new Intent(CreateShop.this, CreateShopMap.class);
                        startActivity(j);
                    }
                }
            });
        }
    }

    public void getLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lat = loc.getLatitude();
            lon = loc.getLongitude();
        }else{
            Toast.makeText(CreateShop.this,"Network is not Enabled",Toast.LENGTH_SHORT).show();
        }
    }

    public void setShopData(){
        shopDataModel.setName(name.toString());
        shopDataModel.setMarket_id(market_Id.toString());
        shopDataModel.setUser_id(user_Id.toString());
        shopDataModel.setLength(length.toString());
        shopDataModel.setWidth(width.toString());
        shopDataModel.setLat(lat);
        shopDataModel.setLon(lon);

        shopDataModelSingleTon.setName(shopDataModel.getName());
        shopDataModelSingleTon.setMarket_id(shopDataModel.getMarket_id());
        shopDataModelSingleTon.setUser_id(shopDataModel.getUser_id());
        shopDataModelSingleTon.setLength(shopDataModel.getLength());
        shopDataModelSingleTon.setWidth(shopDataModel.getWidth());
        shopDataModelSingleTon.setLat(shopDataModel.getLat());
        shopDataModelSingleTon.setLon(shopDataModel.getLon());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(CreateShop.this,ShopkeeperPanel.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
