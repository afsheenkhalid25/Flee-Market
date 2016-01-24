package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;

public class CreateShopMap extends FragmentActivity implements OnMapReadyCallback {

    private String user_id,market_id,shop_id;
    private GoogleMap mMap;
    private Button btn_done,btn_edit;
    private Firebase firebase,shops_details,pending_shops;
    private ProgressDialog progressDialog;
    Shop shop,newShop;
    Checker checker;

    ArrayList<Shop> shopsArrayList;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private ShopDataModelSingleTon shopDataModelSingleTon = ShopDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop_map);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        checker=new Checker();

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        progressDialog = new ProgressDialog(CreateShopMap.this);

        shopsArrayList=new ArrayList<Shop>();

        btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            createShop();
            }
        });
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateShopMap.this,CreateShop.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(CreateShopMap.this,"Map Ready",Toast.LENGTH_SHORT).show();
        mMap=googleMap;
        getAllShops();
    }

    private void getAllShops() {
        firebase.child("Market_Shops").child(shopDataModelSingleTon.getMarket_id().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(CreateShopMap.this,"Getting All Shops",Toast.LENGTH_SHORT).show();
                shopsArrayList.clear();
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        ShopDataModel sdm=d.getValue(ShopDataModel.class);
                        shop=new Shop(sdm.getLat(),sdm.getLon(),Double.parseDouble(sdm.getWidth()),Double.parseDouble(sdm.getLength()));
                        shopsArrayList.add(shop);
                        createShop(shop,1);
                        mMap.addMarker(new MarkerOptions().position(shop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.allshopflag)).title(sdm.getName()));
                    }
                }
                newShop=new Shop(shopDataModelSingleTon.getLat(),shopDataModelSingleTon.getLon(),Double.parseDouble(shopDataModelSingleTon.getWidth()),Double.parseDouble(shopDataModelSingleTon.getLength()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(shopDataModelSingleTon.getLat(), shopDataModelSingleTon.getLon()), 23));
                if (shopsArrayList.size() > 0) {
                    checkOverlapping();
                } else {
                    //createShop();
                    createShop(newShop,3);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void checkOverlapping() {
        Shop temp=newShop;
        for (int i = 0; i < shopsArrayList.size(); i++){
            Log.d("Message", "Comparing Shop" + i);
            Shop oldShop = shopsArrayList.get(i);
            if(checker.shopOverlapping(newShop, oldShop))
            {
                Toast.makeText(CreateShopMap.this, "Can not create shop within another shop.\nChange the location and try again", Toast.LENGTH_LONG).show();
                break;
            }
            else
            {
                if (checker.NWOverlapping(newShop, oldShop)) {
                    Log.d("Message","IN NW OVERLAPPING");
                    Toast.makeText(CreateShopMap.this, "NW Resizing your shop...", Toast.LENGTH_SHORT).show();
                    newShop=checker.NWResizing(newShop,oldShop);
                } else {
                    if (checker.NEOverlapping(newShop, oldShop)) {
                        Log.d("Message","IN NE OVERLAPPING");
                        Toast.makeText(CreateShopMap.this, "NE Resizing your shop...", Toast.LENGTH_SHORT).show();
                        newShop=checker.NEResizing(newShop,oldShop);
                    } else {
                        if (checker.SEOverlapping(newShop, oldShop)) {
                            Log.d("Message","IN SE OVERLAPPING");
                            Toast.makeText(CreateShopMap.this, "SE Resizing your shop...", Toast.LENGTH_SHORT).show();
                            newShop=checker.SEResizing(newShop,oldShop);
                        } else {
                            if (checker.SWOverlapping(newShop, oldShop)) {
                                Log.d("Message","IN SW OVERLAPPING");
                                Toast.makeText(CreateShopMap.this, "SW Resizing your shop...", Toast.LENGTH_SHORT).show();
                                newShop=checker.SWResizing(newShop,oldShop);
                            } else {
                                //checkIntersecting();
                            }
                        }
                    }
                }
            }
        }
        createShop(temp,2);
        createShop(newShop,3);
        mMap.addMarker(new MarkerOptions().position(newShop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon_c)).title(shopDataModelSingleTon.getName()));
    }

    public void createShop(Shop shop,int i){
        PolygonOptions rectOptions = new PolygonOptions()
                .add(shop.getNorthWest())
                .add(shop.getNorthEast())
                .add(shop.getSouthEast())
                .add(shop.getSouthWest())
                .add(shop.getNorthWest());

        if (i==1){
            rectOptions.strokeWidth(2);
            rectOptions.strokeColor(Color.parseColor("#D9FFF8DC"));
            rectOptions.fillColor(Color.parseColor("#D9F0E68C"));
        }
        if (i==2){
            rectOptions.strokeWidth(2);
            rectOptions.strokeColor(Color.parseColor("#AEBEC4BF"));
            rectOptions.fillColor(Color.GRAY);
        }
        if (i==3){
            rectOptions.strokeWidth(4);
            rectOptions.strokeColor(Color.parseColor("#D9FFF8DC"));
            rectOptions.fillColor(Color.parseColor("#D9F0E68C"));
        }

        Polygon polygon = mMap.addPolygon(rectOptions);
    }

    public void createShop() {

        progressDialog.setMessage("\tCreating Shop...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        setShopData();

        shops_details = firebase.child("Shop_Requests").child(market_id).push();
        shops_details.setValue(shopDataModel, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError!=null){
                    Toast.makeText(CreateShopMap.this, "Try Creating Shop Later", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(CreateShopMap.this,ShopkeeperPanel.class);
                    startActivity(i);
                } else{
                    shopDataModelSingleTon.setShop_id(shops_details.getKey());
                    setPendingRequest();
                }
            }
        });
    }

    public void setPendingRequest() {
        shop_id = shopDataModelSingleTon.getShop_id().toString();
        HashMap<String,Object> hashMap=new HashMap<String, Object>();
        hashMap.put("Status","Pending");
        hashMap.put("market_id",market_id);
        pending_shops = firebase.child("Shopkeeper_Pending_Shops").child(user_id).child(shop_id);
        pending_shops.setValue(hashMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError!=null){
                    removeRequest();
                }
                else{
                    shopDataModelSingleTon.setEdit_Check(false);
                    progressDialog.dismiss();
                    Toast.makeText(CreateShopMap.this, "Your shop is now in Pending for Admin acceptance..", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(CreateShopMap.this,ShopkeeperPanel.class);
                    startActivity(i);
                }
            }
        });
    }

    public void removeRequest() {
        shops_details.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError!=null){
                    Log.d("REMOVE REQUEST.....",firebaseError.getMessage());
                } else {
                    Log.d("REMOVE REQUEST.....","successfully removed");
                }
            }
        });
        progressDialog.dismiss();
        Toast.makeText(CreateShopMap.this, "Try Creating Shop Later", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(CreateShopMap.this,ShopkeeperPanel.class);
        startActivity(i);
    }

    public void setShopData() {

        market_id = shopDataModelSingleTon.getMarket_id().toString();
        user_id = shopDataModelSingleTon.getUser_id().toString();

        shopDataModel.setName(shopDataModelSingleTon.getName().toString());
        shopDataModel.setWidth(newShop.getWidth().toString());
        shopDataModel.setLength(newShop.getHeight().toString());
        shopDataModel.setUser_id(user_id);
        shopDataModel.setMarket_id(market_id);
        shopDataModel.setCategory1(shopDataModelSingleTon.getCategory1().toString());
        shopDataModel.setCategory2(shopDataModelSingleTon.getCategory2().toString());
        shopDataModel.setCategory3(shopDataModelSingleTon.getCategory3().toString());
        shopDataModel.setLat(newShop.getLocation().latitude);
        shopDataModel.setLon(newShop.getLocation().longitude);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        shopDataModelSingleTon.setEdit_Check(false);
        Intent i = new Intent(CreateShopMap.this,ShopkeeperPanel.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
