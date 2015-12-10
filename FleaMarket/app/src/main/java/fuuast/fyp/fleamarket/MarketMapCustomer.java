package fuuast.fyp.fleamarket;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;

public class MarketMapCustomer extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String market_id, search_text, search_type;
    private ArrayList all_shop_id, selected_shop_id;
    private ArrayList<ShopDataModel> all_shop_list, selected_shop_list;
    private ImageView img_next, img_back, img_search;
    private TextView tv_type;
    private EditText et_search;
    private Firebase firebase;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_map_customer);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://flee-market.firebaseio.com/");

        all_shop_id = new ArrayList();
        selected_shop_id = new ArrayList();
        all_shop_list = new ArrayList<ShopDataModel>();
        selected_shop_list = new ArrayList<ShopDataModel>();

        market_id = marketDataModelSingleTon.getMarket_id();

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ammc_map)).getMapAsync(this);

        tv_type = (TextView) findViewById(R.id.ammc_txt_type);
        et_search = (EditText) findViewById(R.id.ammc_et_search);

        img_next = (ImageView) findViewById(R.id.ammc_next_item);
        img_back = (ImageView) findViewById(R.id.ammc_back_item);
        img_search = (ImageView) findViewById(R.id.ammc_search);

        img_back.setEnabled(false);
        img_back.setImageResource(R.drawable.ic_action_previous_item);
        img_next.setEnabled(true);
        img_next.setImageResource(R.drawable.ic_action_next_item_dark);
        tv_type.setText("Name");

        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_next.setEnabled(false);
                img_next.setImageResource(R.drawable.ic_action_next_item);
                img_back.setEnabled(true);
                img_back.setImageResource(R.drawable.ic_action_previous_item_dark);
                tv_type.setText("Category");
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_back.setEnabled(false);
                img_back.setImageResource(R.drawable.ic_action_previous_item);
                img_next.setEnabled(true);
                img_next.setImageResource(R.drawable.ic_action_next_item_dark);
                tv_type.setText("Name");
            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_text = et_search.getText().toString();
                search_type = tv_type.getText().toString();
                et_search.setEnabled(false);
                searchItem(search_text, search_type);
            }
        });
        img_search.setEnabled(false);
    }

    public void getShopList() {
        firebase.child("Market_Shops").child(market_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                all_shop_id.clear();
                all_shop_list.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    all_shop_id.add(d.getKey());
                    shopDataModel = d.getValue(ShopDataModel.class);
                    all_shop_list.add(d.getValue(ShopDataModel.class));
                    Shop shop = new Shop(shopDataModel.getLat(), shopDataModel.getLon(), Double.parseDouble(shopDataModel.getWidth()), Double.parseDouble(shopDataModel.getLength()));
                    createShop(shop);
                    mMap.addMarker(new MarkerOptions().position(shop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon_c)).title(shopDataModel.getName() + "\n" + shopDataModel.getCategory1() + "\n" + shopDataModel.getCategory2() + "\n" + shopDataModel.getCategory3()));
                }
                img_search.setEnabled(true);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                getShopList();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        //mMap.setMyLocationEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(marketDataModelSingleTon.getMarket_lat()), Double.parseDouble(marketDataModelSingleTon.getMarket_lon())), 20));
        getShopList();
    }

    public void searchItem(String text, String type) {
        selected_shop_id.clear();
        selected_shop_list.clear();

        if (!et_search.getText().toString().equals("")) {

            if (tv_type.getText().toString().equals("Name")) {
                String tempShopName = et_search.getText().toString().toUpperCase();
                for (int i = 0; i < all_shop_list.size(); i++) {
                    if (tempShopName.equals(all_shop_list.get(i).getName().toUpperCase())) {
                        selected_shop_id.add(all_shop_id.get(i).toString());
                        selected_shop_list.add(all_shop_list.get(i));
                    }
                }
            }
            if(tv_type.getText().toString().equals("Category")){
                String tempCatName = et_search.getText().toString().toUpperCase();
                for (int i = 0; i < all_shop_list.size(); i++) {
                    if (tempCatName.equals(all_shop_list.get(i).getCategory1().toUpperCase())||tempCatName.equals(all_shop_list.get(i).getCategory2().toUpperCase())||tempCatName.equals(all_shop_list.get(i).getCategory3().toUpperCase())) {
                        selected_shop_id.add(all_shop_id.get(i).toString());
                        selected_shop_list.add(all_shop_list.get(i));
                    }
                }
            }
            mMap.clear();
            for (int i = 0; i < all_shop_list.size(); i++) {
                Boolean check = false;
                for (int j = 0; j < selected_shop_list.size(); j++) {
                    if (all_shop_id.get(i).toString().equals(selected_shop_id.get(j).toString())) {
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    Shop shop = new Shop(all_shop_list.get(i).getLat(), all_shop_list.get(i).getLon(), Double.parseDouble(all_shop_list.get(i).getWidth()), Double.parseDouble(all_shop_list.get(i).getLength()));
                    mMap.addMarker(new MarkerOptions().position(shop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.allshopflag)).title(all_shop_list.get(i).getName() + "\n" + all_shop_list.get(i).getCategory1() + "\n" + all_shop_list.get(i).getCategory2() + "\n" + all_shop_list.get(i).getCategory3()));
                    createShop(shop);
                }
            }
            for (int i = 0; i < selected_shop_list.size(); i++) {
                Shop shop = new Shop(selected_shop_list.get(i).getLat(), selected_shop_list.get(i).getLon(), Double.parseDouble(selected_shop_list.get(i).getWidth()), Double.parseDouble(selected_shop_list.get(i).getLength()));
                mMap.addMarker(new MarkerOptions().position(shop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon_c)).title(selected_shop_list.get(i).getName() + "\n" + selected_shop_list.get(i).getCategory1() + "\n" + selected_shop_list.get(i).getCategory2() + "\n" + selected_shop_list.get(i).getCategory3()));
                createShop(shop);
            }
        } else {
            mMap.clear();
            for (int i = 0; i < all_shop_list.size(); i++) {
                Shop shop = new Shop(all_shop_list.get(i).getLat(), all_shop_list.get(i).getLon(), Double.parseDouble(all_shop_list.get(i).getWidth()), Double.parseDouble(all_shop_list.get(i).getLength()));
                mMap.addMarker(new MarkerOptions().position(shop.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.shopicon_c)).title(all_shop_list.get(i).getName() + "\n" + all_shop_list.get(i).getCategory1() + "\n" + all_shop_list.get(i).getCategory2() + "\n" + all_shop_list.get(i).getCategory3()));
                createShop(shop);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(marketDataModelSingleTon.getMarket_lat()), Double.parseDouble(marketDataModelSingleTon.getMarket_lon())), 20));
        }
        et_search.setEnabled(true);
    }

    public void createShop(Shop shop) {
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
}
