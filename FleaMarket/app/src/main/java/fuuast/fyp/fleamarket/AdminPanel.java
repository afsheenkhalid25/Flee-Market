package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminPanel extends ActionBarActivity implements View.OnClickListener {

    private TextView tv_name,tv_email,tv_phone,markets_status;
    private ImageView options;
    private ListView market_list;
    private ArrayList market_id,market_names,market_address;
    private ArrayList<MarketDataModel> allMarkets;
    private Firebase firebase;

    private MarketDataModel marketDataModel;
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();
    private MarketDataModelSingleTon marketDataModelSingleTon=MarketDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        tv_name = (TextView) findViewById(R.id.adminpanel_tv_name);
        tv_email = (TextView) findViewById(R.id.adminpanel_tv_email);
        tv_phone=(TextView) findViewById(R.id.adminpanel_tv_phone);
        markets_status = (TextView) findViewById(R.id.adminpanel_tv_status);

        options = (ImageView) findViewById(R.id.adminpanel_img_options);
        options.setOnClickListener(this);

        market_list=(ListView) findViewById(R.id.adminpanel_lv_markets);
        market_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setMarketData(position);
                Intent i = new Intent(AdminPanel.this,MarketDetails.class);
                startActivity(i);
            }
        });

        tv_name.setText(userDataModelSingleTon.getName());
        tv_email.setText(userDataModelSingleTon.getEmail_id());
        tv_phone.setText(userDataModelSingleTon.getPhone());

        market_names=new ArrayList();
        market_address=new ArrayList();
        market_id=new ArrayList();
        allMarkets=new ArrayList<MarketDataModel>();

        getMarkets();
    }

    private void getMarkets(){
        firebase.child("Admin_Market").child(userDataModelSingleTon.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                market_address.clear();
                market_names.clear();
                allMarkets.clear();
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
        });
    }

    private void getMarketData(final String marketID) {
        firebase.child("Markets").child(marketID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                marketDataModel = dataSnapshot.getValue(MarketDataModel.class);
                market_names.add(marketDataModel.getName());
                market_address.add(marketDataModel.getAddress());

                allMarkets.add(marketDataModel);

                markets_status.setVisibility(View.INVISIBLE);
                market_list.setAdapter(new CustomAdapter_MarketsList(AdminPanel.this,market_names,market_address,null));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setMarketData(int i) {
        marketDataModelSingleTon.setMarket_id(market_id.get(i).toString());
        marketDataModelSingleTon.setAdmin_id(userDataModelSingleTon.getId());
        marketDataModelSingleTon.setMarket_address(allMarkets.get(i).getAddress());
        marketDataModelSingleTon.setMarket_lat(allMarkets.get(i).getLatitude());
        marketDataModelSingleTon.setMarket_lon(allMarkets.get(i).getLongitude());
        marketDataModelSingleTon.setMarket_name(allMarkets.get(i).getName());
        marketDataModelSingleTon.setDay(allMarkets.get(i).getDay());
        marketDataModelSingleTon.setRadius(allMarkets.get(i).getRadius());
    }

    private void onAction (String s) {
        switch (s){
            case "edit_profile":
                Log.d("menu item...", "Edit Profile");
                Intent i = new Intent(this,EditProfile.class);
                startActivity(i);
                break;
            case "logout":
                Log.d("menu item...", "Logout");
                firebase.unauth();
                Intent j = new Intent(this,Login.class);
                startActivity(j);
                break;
            case "create":
                Log.d("menu item...", "Create Market");
                Intent k = new Intent(this,CreateMarket.class);
                startActivity(k);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.adminpanel_img_options:
                PopupMenu popup = new PopupMenu(this, view);
                popup.inflate(R.menu.menu_admin_panel);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.adminpanel_opt_editprofile:
                                onAction("edit_profile");
                                return true;
                            case R.id.adminpanel_opt_logout:
                                onAction("logout");
                                return true;
                            case R.id.adminpanel_opt_createmarket:
                                onAction("create");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                break;
        }
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
