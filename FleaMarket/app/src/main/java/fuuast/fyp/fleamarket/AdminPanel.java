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

    private TextView txtname,txtemail,txtphone,markets_status;
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();
    private ImageView profilepic,options;
    private ListView marketlist;
    private ArrayList mrkt_id,mrkt_names,mrkt_address,mrkt_imgURL;
    private Firebase firebase;
    private Boolean CHECK_FINISH=false;

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
        marketlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(AdminPanel.this,MarketDetails.class);
                startActivity(i);
            }
        });

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
                marketlist.setAdapter(new AdminPanel_CustomAdapter(AdminPanel.this,mrkt_names,mrkt_address,mrkt_imgURL));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void action (String s) {
        switch (s){
            case "settings":
                Log.d("menu item...", "settings");
                break;
            case "logout":
                Log.d("menu item...", "logout");
                firebase.unauth();
                CHECK_FINISH = true;
                Intent i = new Intent(this,Login.class);
                startActivity(i);
                break;
            case "create":
                Log.d("menu item...", "create market");
                CHECK_FINISH = true;
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
                popup.inflate(R.menu.menu_admin_panel);
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
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(CHECK_FINISH){
            finish();
        }
    }
}
