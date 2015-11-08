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
    private ArrayList shop_id,shop_names,shop_market;
    private Firebase firebase;
    private String shop_user_id;

    private ShopDataModel shopDataModel = new ShopDataModel();
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


        shop_id=new ArrayList();
        shop_names=new ArrayList();
        shop_market=new ArrayList();

        getShops();
    }

    private void getShops(){
        firebase.child("ShopData").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                shop_id.add(dataSnapshot.getKey().toString());
                shopDataModel = dataSnapshot.getValue(ShopDataModel.class);
                shop_user_id = shopDataModel.getUser_id().toString();
                if(userDataModelSingleTon.getId().equals(shop_user_id)){
                    shop_names.add(shopDataModel.getName());
                    shop_market.add(shopDataModel.getMarket_id());

                    shops_status.setVisibility(View.INVISIBLE);
                    ShopkeeperPanel_CustomAdapter adapter = (new ShopkeeperPanel_CustomAdapter(ShopkeeperPanel.this,shop_names,shop_market));
                    shop_list.setAdapter(adapter);
                }
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
                Intent k = new Intent(ShopkeeperPanel.this,CreateShop.class);
                startActivity(k);
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
