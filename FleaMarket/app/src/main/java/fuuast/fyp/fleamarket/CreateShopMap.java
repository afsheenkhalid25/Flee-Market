package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CreateShopMap extends FragmentActivity {

    private GoogleMap mMap;
    private Button btn_done,btn_edit;
    private Firebase firebase;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private ShopDataModelSingleTon shopDataModelSingleTon = ShopDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop_map);
        setUpMapIfNeeded();

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            setShopData();
            firebase.child("ShopData").push().setValue(shopDataModel, new Firebase.CompletionListener()
            {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError!=null){
                    Toast.makeText(CreateShopMap.this, "Try Creating Shop Later", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(CreateShopMap.this, "Your shop is now in Pendiing for admin acceptance..", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(CreateShopMap.this,ShopkeeperPanel.class);
                    startActivity(i);
                }
                }
            });
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
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

    public void setShopData(){
        shopDataModel.setName(shopDataModelSingleTon.getName().toString());
        shopDataModel.setMarket_id(shopDataModelSingleTon.getMarket_id().toString());
        shopDataModel.setUser_id(shopDataModelSingleTon.getUser_id().toString());
        shopDataModel.setLength(shopDataModelSingleTon.getLength().toString());
        shopDataModel.setWidth(shopDataModelSingleTon.getWidth().toString());
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
        if(shopDataModelSingleTon.getCategory2().equals("")){
            shopDataModel.setCtgry_one(shopDataModelSingleTon.getCategory1().toString());
        }else if(shopDataModelSingleTon.getCategory3().equals("")){
            shopDataModel.setCtgry_one(shopDataModelSingleTon.getCategory1().toString());
            shopDataModel.setCtgry_two(shopDataModelSingleTon.getCategory2().toString());
        }else{
            shopDataModel.setCtgry_one(shopDataModelSingleTon.getCategory1().toString());
            shopDataModel.setCtgry_two(shopDataModelSingleTon.getCategory2().toString());
            shopDataModel.setCtgry_three(shopDataModelSingleTon.getCategory3().toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(CreateShopMap.this,ShopkeeperPanel.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
