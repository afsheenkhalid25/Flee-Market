package fuuast.fyp.fleamarket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


public class CreateShop extends ActionBarActivity {

    private String name,width,length,market_Id,user_Id,ctgry_one,ctgry_two,ctgry_three;
    private double lat,lon,NW_lat,NW_lon,NE_lat,NE_lon,SW_lat,SW_lon,SE_lat,SE_lon;
    private int Ct_check=0;
    private ArrayList market_id,market_names,market_address,ctgry_id,ctgry_name,ctgry_url,slct_ctgry_name,slct_ctgry_url;
    private EditText et_name,et_width,et_length;
    private Button btn_cancle,btn_next;
    private ImageView img_add;
    private Firebase firebase;
    private Spinner mySpinner;
    private ListView category_listview;
    private AlertDialog category_dialog;

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

        btn_cancle = (Button)findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(CreateShop.this, ShopkeeperPanel.class);
                startActivity(j);
            }
        });

        btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setParameters();
            }
        });

        img_add =(ImageView)findViewById(R.id.cs_img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategories();
            }
        });

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

        category_listview = (ListView) findViewById(R.id.listView);

        market_names = new ArrayList();
        market_address = new ArrayList();
        market_id = new ArrayList();
        getMarketList();

        ctgry_id = new ArrayList();
        ctgry_name = new ArrayList();
        ctgry_url = new ArrayList();
        slct_ctgry_name = new ArrayList();
        slct_ctgry_url = new ArrayList();
        setCategoryDialog();
        //getCategoryList();
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

    public void getCategoryList()
    {
        ctgry_id.clear();
        ctgry_name.clear();
        ctgry_url.clear();
        firebase.child("Categories").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ctgry_id.add(dataSnapshot.getKey().toString());

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

    public void setCategories(){
        if(Ct_check<=2){
            category_dialog.show();
        }else{
            Toast.makeText(CreateShop.this, "You have enough categories selected....", Toast.LENGTH_SHORT).show();
        }
        Ct_check++;
    }

    public void setCategoryDialog()
    {
        ctgry_name.add("ABC SHOP");
        ctgry_url.add(R.drawable.marketpic);
        ctgry_name.add("MNO Shop");
        ctgry_url.add(R.drawable.marketpic);
        ctgry_name.add("XYZ Shop");
        ctgry_url.add(R.drawable.marketpic);

        LayoutInflater inflater = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View customView = inflater.inflate(R.layout.category_dialog, null, false);
        AlertDialog.Builder builder =new AlertDialog.Builder(CreateShop.this);
        builder.setTitle("Select Category");
        builder.setView(customView);
        ListView list = (ListView) customView.findViewById(R.id.category_listview);
        final CategoryList_CustomAdapter adapter = new CategoryList_CustomAdapter(CreateShop.this,ctgry_name,ctgry_url);
        list.setAdapter(adapter );
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slct_ctgry_name.add(ctgry_name.get(position));
                slct_ctgry_url.add(ctgry_url.get(position));
                category_dialog.dismiss();
                ctgry_name.remove(position);
                ctgry_url.remove(position);
                adapter.notifyDataSetChanged();

                category_listview.setAdapter(new CategoryList_CustomAdapter(CreateShop.this,slct_ctgry_name,slct_ctgry_url));
            }
        });
        category_dialog = builder.create();
    }

    public void setParameters() {
        name = et_name.getText().toString();
        width = et_width.getText().toString();
        length = et_length.getText().toString();
        user_Id = userDataModelSingleTon.getId().toString();
        ctgry_one = "-";
        ctgry_two = "-";
        ctgry_three = "-";
        lat = 32.445005;
        lon = 122.344445;
        NW_lat = 0.0;
        NW_lon = 0.0;
        SW_lat = 0.0;
        SW_lon = 0.0;
        NE_lat = 0.0;
        NE_lon = 0.0;
        SE_lat = 0.0;
        SE_lon = 0.0;

        //getLocation();

        if (name.equals("") || width.equals("") || length.equals("") || ctgry_one.equals("") || ctgry_two.equals("") || ctgry_three.equals("")) {
            Toast.makeText(CreateShop.this, "First fill complete details..", Toast.LENGTH_SHORT).show();
        } else {
            setShopDataSingleTon();
            Intent j = new Intent(CreateShop.this, CreateShopMap.class);
            startActivity(j);
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

    public void setShopDataSingleTon()
    {
        shopDataModelSingleTon.setName(name);
        shopDataModelSingleTon.setMarket_id(market_Id);
        shopDataModelSingleTon.setUser_id(user_Id);
        shopDataModelSingleTon.setLength(length);
        shopDataModelSingleTon.setWidth(width);
        shopDataModelSingleTon.setLat(lat);
        shopDataModelSingleTon.setLon(lon);
        shopDataModelSingleTon.setNE_lat(NE_lat);
        shopDataModelSingleTon.setNE_lon(NE_lon);
        shopDataModelSingleTon.setSE_lat(SE_lat);
        shopDataModelSingleTon.setSE_lon(SE_lon);
        shopDataModelSingleTon.setNW_lat(NW_lat);
        shopDataModelSingleTon.setNW_lon(NW_lon);
        shopDataModelSingleTon.setSW_lat(SW_lat);
        shopDataModelSingleTon.setSW_lon(SW_lon);
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
