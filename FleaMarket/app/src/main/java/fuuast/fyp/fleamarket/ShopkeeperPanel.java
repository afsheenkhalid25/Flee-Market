package fuuast.fyp.fleamarket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopkeeperPanel extends ActionBarActivity implements View.OnClickListener {

    private TextView tv_name,tv_email,tv_phone,shops_status;
    private ImageView img_options;
    private ListView shop_list;
    private ArrayList shop_id,shop_names,shop_market,market_id,market_names,market_address,market_imgURL;
    private Firebase firebase;

    Dialog dialog;
    private MarketDataModel marketDataModel;
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopkeeper_panel);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        tv_name = (TextView) findViewById(R.id.shoppanel_tv_name);
        tv_email = (TextView) findViewById(R.id.shoppanel_tv_email);
        tv_phone=(TextView) findViewById(R.id.shoppanel_tv_phone);
        shops_status = (TextView) findViewById(R.id.shoppanel_tv_status);

        img_options = (ImageView) findViewById(R.id.shoppanel_img_options);
        img_options.setOnClickListener(this);

        shop_list=(ListView) findViewById(R.id.shoppanel_lv_shops);
        shop_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent i = new Intent(ShopkeeperPanel.this,MarketDetails.class);
                //startActivity(i);
            }
        });

        tv_name.setText(userDataModelSingleTon.getName());
        tv_email.setText(userDataModelSingleTon.getEmail_id());
        tv_phone.setText(userDataModelSingleTon.getPhone());

        market_names=new ArrayList();
        market_address=new ArrayList();
        market_imgURL=new ArrayList();
        market_id=new ArrayList();
        shop_id=new ArrayList();
        shop_names=new ArrayList();
        shop_market=new ArrayList();

        //getShops();
    }

    private void getShops(){
        /*firebase.child("shop_Market").child(userDataModelSingleTon.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                market_imgURL.clear();
                market_address.clear();
                market_names.clear();
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        market_id.add(((HashMap<String,Object>)d.getValue()).get("marketID").toString());
                        getMarketData(((HashMap<String,Object>)d.getValue()).get("marketID").toString());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
    }

    private void getShopData(final String marketID){
        /*firebase.child("Markets").child(marketID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                marketDataModel = dataSnapshot.getValue(MarketDataModel.class);
                market_names.add(marketDataModel.getName());
                market_address.add(marketDataModel.getAddress());
                market_imgURL.add(marketDataModel.getImageURL());

                //setting values of market data in MarketDataModelSingleTon class for using it in market details class....
                marketDataModelSingleTon.setshop_id(userDataModelSingleTon.getId());
                marketDataModelSingleTon.setMarket_id(marketID);
                marketDataModelSingleTon.setMarket_address(marketDataModel.getAddress());
                marketDataModelSingleTon.setMarket_name(marketDataModel.getName());

                shops_status.setVisibility(View.INVISIBLE);
                market_list.setAdapter(new ShopkeeperPanel_CustomAdapter(ShopkeeperPanel.this,shop_names,shop_market,shop_imgURL));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
    }

    private void onAction (String s) {
        switch (s){
            case "edit_profile":
                Log.d("menu item...", "Edit Profile");
            Intent i = new Intent(this,EditProfile.class );
                startActivity(i);
                break;
            case "logout":
                Log.d("menu item...", "Logout");
                firebase.unauth();
                Intent j = new Intent(this,Login.class);
                startActivity(j);
                break;
            case "create":
                Log.d("menu item...", "Create Shop");
                createShop();
                break;
            case "pending":
                Log.d("menu item...", "Pending Shops");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shoppanel_img_options:
                PopupMenu popup = new PopupMenu(this, view);
                popup.inflate(R.menu.menu_shopkeeper_panel);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.shoppanel_opt_editprofile:
                                onAction("edit_profile");
                                return true;
                            case R.id.shoppanel_opt_logout:
                                onAction("logout");
                                return true;
                            case R.id.shoppanel_opt_createshop:
                                onAction("create");
                                return true;
                            case R.id.shoppanel_opt_pending_shop:
                                onAction("pending");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            break;
        }
    }

    public void createShop(){
        getMarketList();
        View view = getLayoutInflater().inflate(R.layout.selectmarket_listview, null);
        ListView lv = (ListView) view.findViewById(R.id.listView1);
        AdminPanel_CustomAdapter customAdapter = new AdminPanel_CustomAdapter(ShopkeeperPanel.this, market_names,market_address,market_imgURL);
        lv.setAdapter(customAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Position","On item clicked");
                Log.d("Name",market_names.get(position).toString());
                marketDataModelSingleTon.setMarket_id(market_id.get(position).toString());
                marketDataModelSingleTon.setMarket_name(market_names.get(position).toString());
                marketDataModelSingleTon.setMarket_address(market_address.get(position).toString());
                dialog.dismiss();
                Intent i = new Intent(ShopkeeperPanel.this,CreateShop.class);
                startActivity(i);
            }
        });
        dialog = new Dialog(ShopkeeperPanel.this);
        dialog.setTitle("Select Market");
        dialog.setContentView(view);
        dialog.show();
    }

    private void getMarketList()
    {
        market_id.clear();
        market_names.clear();
        market_address.clear();
        market_imgURL.clear();
        firebase.child("Markets").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                market_id.add(dataSnapshot.getKey().toString());
                marketDataModel = dataSnapshot.getValue(MarketDataModel.class);
                market_names.add(marketDataModel.getName());
                market_address.add(marketDataModel.getAddress());
                market_imgURL.add(marketDataModel.getImageURL());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
