package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopDetails extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap=null;
    private Firebase firebase;
    private ArrayList category_names,category_url;
    private ImageView img_cat1,img_cat2,img_cat3;
    private TextView tv_shop_name,tv_shop_market,tv_owner_name,tv_owner_contact;
    private String shop_id,shop_name,user_id,user_name,user_contact,user_org,market_id,market_name,parentActivity;

    Shop currentShop;
    ArrayList<Shop> allShops;
    private ShopDataModel shopDataModel = new ShopDataModel();
    private ShopDataModel shopDataModel2 = new ShopDataModel();
    private UserDataModel userDataModel = new UserDataModel();
    private MarketDataModel marketDataModel = new MarketDataModel();
    private UserDataModelSingleTon userDataModelSingleTon=UserDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        img_cat1 = (ImageView)findViewById(R.id.img_cat1);
        img_cat2 = (ImageView)findViewById(R.id.img_cat2);
        img_cat3 = (ImageView)findViewById(R.id.img_cat3);
        img_cat1.setVisibility(View.INVISIBLE);
        img_cat2.setVisibility(View.INVISIBLE);
        img_cat3.setVisibility(View.INVISIBLE);

        tv_shop_name = (TextView)findViewById(R.id.ll);
        tv_shop_market = (TextView)findViewById(R.id.textView3);
        tv_owner_name = (TextView)findViewById(R.id.sd_owner);
        tv_owner_contact = (TextView)findViewById(R.id.sd_contact);

        allShops=new ArrayList<Shop>();

        Bundle bundle=getIntent().getExtras();
        shop_id = bundle.getString("shopID");
        market_id = bundle.getString("marketID");
        parentActivity=bundle.getString("parentActivity");
        Log.d("Values",shop_id+" "+market_id);

        category_names = new ArrayList();
        category_url = new ArrayList();

        if(parentActivity.equals("PendingShops")||parentActivity.equals("ShopRequest"))
            getPendingShopDetails();
        else
            getShopDetails();
    }

    public void getPendingShopDetails(){
        firebase.child("Shop_Requests").child(market_id).child(shop_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.d("Position", "Getting shop details");
                    shopDataModel = dataSnapshot.getValue(ShopDataModel.class);
                    shop_name = shopDataModel.getName().toString();
                    if (shopDataModel.getCategory2().equals("-")) {
                        category_names.add(shopDataModel.getCategory1());
                    } else if (shopDataModel.getCategory3().equals("-")) {
                        category_names.add(shopDataModel.getCategory1());
                        category_names.add(shopDataModel.getCategory2());
                    } else {
                        category_names.add(shopDataModel.getCategory1());
                        category_names.add(shopDataModel.getCategory2());
                        category_names.add(shopDataModel.getCategory3());
                    }
                    user_id = shopDataModel.getUser_id();
                    getCategoryImages();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getShopDetails(){
        firebase.child("Market_Shops").child(market_id).child(shop_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Position", "Getting shop details");
                category_names.clear();
                shopDataModel = dataSnapshot.getValue(ShopDataModel.class);
                shop_name = shopDataModel.getName();
                if (shopDataModel.getCategory2().equals("-")) {
                    category_names.add(shopDataModel.getCategory1());
                } else if (shopDataModel.getCategory3().equals("-")) {
                    category_names.add(shopDataModel.getCategory1());
                    category_names.add(shopDataModel.getCategory2());
                } else {
                    category_names.add(shopDataModel.getCategory1());
                    category_names.add(shopDataModel.getCategory2());
                    category_names.add(shopDataModel.getCategory3());
                }
                user_id = shopDataModel.getUser_id();
                getCategoryImages();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public  void getCategoryImages(){
       firebase.child("Catagories").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               category_url.clear();
               for (DataSnapshot d : dataSnapshot.getChildren()) {
                   for (int i = 0; i < category_names.size(); i++) {
                       if (d.getKey().equals(category_names.get(i).toString())) {
                           category_url.add(((HashMap<String, String>) d.getValue()).get("IMG"));
                           Log.d("Category", d.getKey());
                           Log.d("Category URL", ((HashMap<String, String>) d.getValue()).get("IMG"));
                           break;
                       }
                   }
                   if (category_url.size() == 3)
                       break;
               }
               if (shopDataModel.getUser_id().toString().equals(userDataModelSingleTon.getId().toString())) {
                   user_name = userDataModelSingleTon.getName();
                   user_contact = userDataModelSingleTon.getPhone();
                   user_org = userDataModelSingleTon.getOrg_name();
                   getMarketName();
               } else {
                   getUserDetails();
               }
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {

           }
       });
    }

    public void getUserDetails(){
        firebase.child("Users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDataModel = dataSnapshot.getValue(UserDataModel.class);
                user_name = userDataModel.getName();
                user_contact = userDataModel.getPhone();
                user_org = userDataModel.getOrg_name();
                getMarketName();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getMarketName(){
        firebase.child("Markets").child(market_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Position", "Getting Market Name");
                marketDataModel = dataSnapshot.getValue(MarketDataModel.class);
                market_name = marketDataModel.getName();
                setData();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setData(){
        for(int i=0;i<category_url.size();i++){
            if(i==0){
                Log.d("Position","In 1");
                Picasso.with(ShopDetails.this).load(category_url.get(i).toString()).into(img_cat1);
                img_cat1.setVisibility(View.VISIBLE);
            } else if(i==1){
                Log.d("Position","In 2");
                Picasso.with(ShopDetails.this).load(category_url.get(i).toString()).into(img_cat2);
                img_cat2.setVisibility(View.VISIBLE);
            } else if(i==2){
                Log.d("Position","In 3");
                Picasso.with(ShopDetails.this).load(category_url.get(i).toString()).into(img_cat3);
                img_cat3.setVisibility(View.VISIBLE);
            }
        }
        tv_shop_name.setText(shop_name.toString());
        tv_shop_market.setText(market_name.toString());
        tv_owner_name.setText(user_name.toString());
        tv_owner_contact.setText(user_contact.toString());
    }

    @Override
    public void onBackPressed() {
        switch (parentActivity){
            case "ShopkeeperPanel":
                Intent i1=new Intent(ShopDetails.this,ShopkeeperPanel.class);
                startActivity(i1);
                break;
            case "PendingShops":
                Intent i2=new Intent(ShopDetails.this,PendingShops.class);
                startActivity(i2);
                break;
            case "MarketDetails":
                Intent i3=new Intent(ShopDetails.this,MarketDetails.class);
                startActivity(i3);
                break;
            case "ShopRequest":
                Intent i4=new Intent(ShopDetails.this,ShopsRequest.class);
                startActivity(i4);
                break;
        }
    }

    private void getAllShops(){
        firebase.child("Market_Shops").child(market_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if(!d.getKey().equals(shop_id)){
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) d.getValue();
                        Shop shop = new Shop(Double.parseDouble(hashMap.get("lat").toString()), Double.parseDouble(hashMap.get("lon").toString()), Double.parseDouble(hashMap.get("width").toString()), Double.parseDouble(hashMap.get("length").toString()));
                        createShop(shop, 1);
                        mMap.addMarker(new MarkerOptions().position(shop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.allshopflag)).title(hashMap.get("name").toString()+"\n"+hashMap.get("category1").toString()+"\n"+hashMap.get("category2").toString()+"\n"+hashMap.get("category3").toString()));
                    }
                }
                getCurrentShop();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                getAllShops();
            }
        });
    }

    private void getCurrentShop(){
        if(parentActivity.equals("PendingShops")||parentActivity.equals("ShopRequest"))
            firebase.child("Shop_Requests").child(market_id).child(shop_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    shopDataModel2=dataSnapshot.getValue(ShopDataModel.class);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(shopDataModel2.getLat(), shopDataModel2.getLon())).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon_c)).title(shopDataModel2.getName()+"\n"+shopDataModel2.getCategory1()+"\n"+shopDataModel2.getCategory2()+"\n"+shopDataModel2.getCategory3()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(shopDataModel2.getLat(), shopDataModel2.getLon()), 20));
                    currentShop=new Shop(shopDataModel2.getLat(),shopDataModel2.getLon(),Double.parseDouble(shopDataModel2.getWidth()),Double.parseDouble(shopDataModel2.getLength()));
                    createShop(currentShop, 2);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    getCurrentShop();
                }
            });
        else{
            firebase.child("Market_Shops").child(market_id).child(shop_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    shopDataModel2 = dataSnapshot.getValue(ShopDataModel.class);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(shopDataModel2.getLat(), shopDataModel2.getLon())).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon_c)).title(shopDataModel2.getName()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(shopDataModel2.getLat(), shopDataModel2.getLon()), 20));
                    currentShop=new Shop(shopDataModel2.getLat(),shopDataModel2.getLon(),Double.parseDouble(shopDataModel2.getWidth()),Double.parseDouble(shopDataModel2.getLength()));
                    createShop(currentShop, 2);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    getCurrentShop();
                }
            });
        }
    }

    public void createShop(Shop shop,int i){
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

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        getAllShops();
    }
}
