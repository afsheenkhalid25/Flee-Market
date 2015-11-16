package fuuast.fyp.fleamarket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class CreateShop extends ActionBarActivity {

    private String name,width,length,market_Id,category1,category2,category3,category1_url,category2_url,category3_url;
    private double lat,lon,NW_lat,NW_lon,NE_lat,NE_lon,SW_lat,SW_lon,SE_lat,SE_lon;
    private ArrayList market_id,market_names,market_address,category_name,category_url,select_ct_name,select_ct_url;
    private EditText et_name,et_width,et_length;
    private Button btn_cancle,btn_next;
    private ImageView img_add;
    private Firebase firebase;
    private Spinner mySpinner;
    private ListView category_listview;
    private AlertDialog category_dialog,alert;

    private CustomAdapter_CategoriesList category_adapter,dialog_adapter;
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

        market_id = new ArrayList();
        market_names = new ArrayList();
        market_address = new ArrayList();
        getMarketList();

        category_name = new ArrayList();
        category_url = new ArrayList();
        select_ct_name = new ArrayList();
        select_ct_url = new ArrayList();

        btn_cancle = (Button)findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopDataModelSingleTon.setEdit_Check(false);
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
        category_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(CreateShop.this);
                builder.setTitle("Delete Category!!");
                builder.setMessage("Are you sure you want to delete this category?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                      category_adapter = new CustomAdapter_CategoriesList(CreateShop.this, select_ct_name, select_ct_url);
                      dialog_adapter = new CustomAdapter_CategoriesList(CreateShop.this,category_name,category_url);

                      //selected listView item will be deleted here and added back to category listView....
                      category_name.add(select_ct_name.get(position).toString());
                      category_url.add(select_ct_url.get(position).toString());
                      dialog_adapter.notifyDataSetChanged();

                      select_ct_name.remove(position);
                      select_ct_url.remove(position);
                      category_listview.setAdapter(category_adapter);
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                    }
                });
                alert = builder.create();
                alert.show();
            }
        });

        setEditState();
        getCategoryList();
    }

    private void getMarketList() {

        firebase.child("Markets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                market_id.clear();
                market_names.clear();
                market_address.clear();
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    market_id.add(d.getKey());
                    market_names.add(((HashMap<String,String>)d.getValue()).get("name"));
                    market_address.add(((HashMap<String,String>)d.getValue()).get("address"));
                    mySpinner.setAdapter(new CustomAdapter_MarketsList(CreateShop.this,market_names,market_address));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getCategoryList() {

        firebase.child("Catagories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                category_name.clear();
                category_url.clear();
                for(DataSnapshot d:dataSnapshot.getChildren()) {
                    if (shopDataModelSingleTon.isEdit_Check()) {
                        for(int i=0;i<select_ct_name.size();i++){
                            if(d.getKey().equals(select_ct_name.get(i).toString())){
                                Log.d("Position","do nothing with "+d.getKey());
                                break;
                            }else{
                                category_name.add(d.getKey());
                                category_url.add(((HashMap<String, String>) d.getValue()).get("IMG"));
                                break;
                            }
                        }
                    } else {
                        Log.d("Position","Adding in normal state");
                        category_name.add(d.getKey());
                        category_url.add(((HashMap<String, String>) d.getValue()).get("IMG"));
                    }
                }
                setCategoryDialog();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setCategoryDialog() {

        LayoutInflater inflater = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View customView = inflater.inflate(R.layout.category_dialog, null, false);

        dialog_adapter = new CustomAdapter_CategoriesList(CreateShop.this,category_name,category_url);
        ListView list = (ListView) customView.findViewById(R.id.category_listview);
        list.setAdapter(dialog_adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                select_ct_name.add(category_name.get(position));
                select_ct_url.add(category_url.get(position));
                category_dialog.dismiss();

                category_name.remove(position);
                category_url.remove(position);
                dialog_adapter.notifyDataSetChanged();

                category_listview.setAdapter(new CustomAdapter_CategoriesList(CreateShop.this,select_ct_name,select_ct_url));
            }
        });

        AlertDialog.Builder builder =new AlertDialog.Builder(CreateShop.this);
        builder.setTitle("Select Category!!");
        builder.setView(customView);
        category_dialog = builder.create();
        category_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                switch (select_ct_name.size()){
                    case 1:
                        category1 = select_ct_name.get(0).toString();
                        category1_url = select_ct_url.get(0).toString();
                        category2 = "-";
                        category2_url = "-";
                        category3 = "-";
                        category3_url = "-";
                        break;
                    case 2:
                        category1 = select_ct_name.get(0).toString();
                        category1_url = select_ct_url.get(0).toString();
                        category2 = select_ct_name.get(1).toString();
                        category2_url = select_ct_url.get(1).toString();
                        category3 = "-";
                        category3_url = "-";
                        break;
                    case 3:
                        category1 = select_ct_name.get(0).toString();
                        category1_url = select_ct_url.get(0).toString();
                        category2 = select_ct_name.get(1).toString();
                        category2_url = select_ct_url.get(1).toString();
                        category3 = select_ct_name.get(2).toString();
                        category3_url = select_ct_url.get(2).toString();
                        break;
                }
            }
        });
    }

    public void setCategories() {
        if(select_ct_name.size()<3)
            category_dialog.show();
        else
            Toast.makeText(CreateShop.this, "You have enough categories selected....", Toast.LENGTH_SHORT).show();
    }

    public void setParameters() {

        name = et_name.getText().toString();
        width = et_width.getText().toString();
        length = et_length.getText().toString();
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
        if (name.equals("") || width.equals("") || length.equals("")||select_ct_name.size()==0) {
            Toast.makeText(CreateShop.this, "First fill complete details..", Toast.LENGTH_SHORT).show();
        } else {
            setShopDataSingleTon();
            Intent j = new Intent(CreateShop.this, CreateShopMap.class);
            startActivity(j);
        }
    }

    public void getLocation() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lat = loc.getLatitude();
            lon = loc.getLongitude();
        }else{
            Toast.makeText(CreateShop.this,"Network is not Enabled",Toast.LENGTH_SHORT).show();
        }
    }

    public void setEditState() {
        if(shopDataModelSingleTon.isEdit_Check()) {

            et_name.setText(shopDataModelSingleTon.getName().toString(), TextView.BufferType.EDITABLE);
            et_width.setText(shopDataModelSingleTon.getWidth().toString(), TextView.BufferType.EDITABLE);
            et_length.setText(shopDataModelSingleTon.getLength().toString(), TextView.BufferType.EDITABLE);

            if(shopDataModelSingleTon.getCategory2().toString().equals("-")) {
                select_ct_name.add(shopDataModelSingleTon.getCategory1().toString());
                select_ct_url.add(shopDataModelSingleTon.getCategory1_url().toString());

            }else if(shopDataModelSingleTon.getCategory3().toString().equals("-")) {
                select_ct_name.add(shopDataModelSingleTon.getCategory1().toString());
                select_ct_url.add(shopDataModelSingleTon.getCategory1_url().toString());
                select_ct_name.add(shopDataModelSingleTon.getCategory2().toString());
                select_ct_url.add(shopDataModelSingleTon.getCategory2_url().toString());

            }else {
                select_ct_name.add(shopDataModelSingleTon.getCategory1().toString());
                select_ct_url.add(shopDataModelSingleTon.getCategory1_url().toString());
                select_ct_name.add(shopDataModelSingleTon.getCategory2().toString());
                select_ct_url.add(shopDataModelSingleTon.getCategory2_url().toString());
                select_ct_name.add(shopDataModelSingleTon.getCategory3().toString());
                select_ct_url.add(shopDataModelSingleTon.getCategory3_url().toString());
            }
            category_listview.setAdapter(new CustomAdapter_CategoriesList(CreateShop.this,select_ct_name,select_ct_url));
        }
    }

    public void setShopDataSingleTon() {

        shopDataModelSingleTon.setEdit_Check(true);
        shopDataModelSingleTon.setName(name);
        shopDataModelSingleTon.setMarket_id(market_Id);
        shopDataModelSingleTon.setUser_id(userDataModelSingleTon.getId().toString());
        shopDataModelSingleTon.setLength(length);
        shopDataModelSingleTon.setWidth(width);
        shopDataModelSingleTon.setCategory1(category1);
        shopDataModelSingleTon.setCategory2(category2);
        shopDataModelSingleTon.setCategory3(category3);
        shopDataModelSingleTon.setCategory1_url(category1_url);
        shopDataModelSingleTon.setCategory2_url(category2_url);
        shopDataModelSingleTon.setCategory3_url(category3_url);
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
