package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class CreateShopMap extends FragmentActivity {

    private String user_id,market_id,shop_id;
    private GoogleMap mMap;
    private Button btn_done,btn_edit;
    private Firebase firebase,shops_details,pending_shops;
    private ProgressDialog progressDialog;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private ShopDataModelSingleTon shopDataModelSingleTon = ShopDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop_map);
        setUpMapIfNeeded();

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        progressDialog = new ProgressDialog(CreateShopMap.this);

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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
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
        shopDataModel.setWidth(shopDataModelSingleTon.getWidth().toString());
        shopDataModel.setLength(shopDataModelSingleTon.getLength().toString());
        shopDataModel.setUser_id(user_id);
        shopDataModel.setMarket_id(market_id);
        shopDataModel.setCategory1(shopDataModelSingleTon.getCategory1().toString());
        shopDataModel.setCategory2(shopDataModelSingleTon.getCategory2().toString());
        shopDataModel.setCategory3(shopDataModelSingleTon.getCategory3().toString());
        shopDataModel.setLat(shopDataModelSingleTon.getLat());
        shopDataModel.setLon(shopDataModelSingleTon.getLon());
        shopDataModel.setNE_lat(shopDataModelSingleTon.getNE_lat());
        shopDataModel.setNE_lon(shopDataModelSingleTon.getNE_lon());
        shopDataModel.setSE_lat(shopDataModelSingleTon.getSE_lat());
        shopDataModel.setSE_lon(shopDataModelSingleTon.getSE_lon());
        shopDataModel.setNW_lat(shopDataModelSingleTon.getNW_lat());
        shopDataModel.setNW_lon(shopDataModelSingleTon.getNW_lon());
        shopDataModel.setSW_lat(shopDataModelSingleTon.getSW_lat());
        shopDataModel.setSW_lon(shopDataModelSingleTon.getSW_lon());

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
