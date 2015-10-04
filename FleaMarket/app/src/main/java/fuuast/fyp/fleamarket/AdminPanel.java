package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

    private TextView txtname,txtemail,txtphone,markets_status;
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();
    private ImageView profilepic,options;
    private ListView marketlist;
    private ArrayList mrkt_id,mrkt_names,mrkt_address,mrkt_imgURL;
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        txtname = (TextView) findViewById(R.id.adminpanel_tv_name);
        txtemail = (TextView) findViewById(R.id.adminpanel_tv_email);
        txtphone=(TextView) findViewById(R.id.adminpanel_tv_phone);
        markets_status = (TextView) findViewById(R.id.adminpanel_tv_status);

        options = (ImageView) findViewById(R.id.adminpanel_img_options);
        options.setOnClickListener(this);

        marketlist=(ListView) findViewById(R.id.adminpanel_lv_markets);

        txtname.setText(userDataModelSingleTon.getName());
        txtemail.setText(userDataModelSingleTon.getEmail_id());
        txtphone.setText(userDataModelSingleTon.getPhone());

        mrkt_names=new ArrayList();
        mrkt_address=new ArrayList();
        mrkt_imgURL=new ArrayList();
        mrkt_id=new ArrayList();

        getMarkets();
    }

    private void getMarkets(){
        firebase.child("Admin_Market").child(userDataModelSingleTon.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mrkt_imgURL.clear();
                mrkt_address.clear();
                mrkt_names.clear();
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        mrkt_id.add(((HashMap<String,Object>)d.getValue()).get("marketID").toString());
                        getMarketData(((HashMap<String,Object>)d.getValue()).get("marketID").toString());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getMarketData(String marketID){
        firebase.child("Markets").child(marketID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MarketDataModel marketDataModel=dataSnapshot.getValue(MarketDataModel.class);
                mrkt_names.add(marketDataModel.getName());
                mrkt_address.add(marketDataModel.getAddress());
                mrkt_imgURL.add(marketDataModel.getImageURL());
                markets_status.setVisibility(View.INVISIBLE);
                marketlist.setAdapter(new CustomAdapter(AdminPanel.this,mrkt_names,mrkt_address,mrkt_imgURL));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void action (String s) {
        if (s.equals("settings")) {
            Log.d("menu item...", "settings");
        } else if (s.equals("logout")) {
            Log.d("menu item...", "logout");
        }
        switch (s){
            case "settings":
                Log.d("menu item...", "settings");
                break;
            case "logout":
                Log.d("menu item...", "logout");
                firebase.unauth();
                Intent i = new Intent(this,Login.class);
                startActivity(i);
                break;
            case "create":
                Log.d("menu item...", "create market");
                Intent j = new Intent(this,CreateMarket.class);
                startActivity(j);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.adminpanel_img_options:
                PopupMenu popup = new PopupMenu(this, view);
                popup.inflate(R.menu.actions);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.adminpanel_opt_settings:
                                action("settings");
                                return true;
                            case R.id.adminpanel_opt_logout:
                                action("logout");
                                return true;
                            case R.id.adminpanel_opt_createmarket:
                                action("create");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                break;
            case R.id.adminpanel_img_marketdetails:
                Intent i=new Intent(AdminPanel.this,MarketView.class);
                startActivity(i);
        }
    }
}
