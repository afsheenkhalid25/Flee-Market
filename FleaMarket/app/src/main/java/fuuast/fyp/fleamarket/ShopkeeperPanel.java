package fuuast.fyp.fleamarket;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
    private Firebase firebase,shopkeeper_shop,shop_details;
    private ProgressDialog progressDialog;
    private String user_id,category1,category2,category3,categories;
    private ArrayList shop_id,shop_name,shop_category,market_id,market_name;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private MarketDataModel marketDataModel = new MarketDataModel();
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopkeeper_panel);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        progressDialog = new ProgressDialog(ShopkeeperPanel.this);
        progressDialog.setMessage("\tDeleting...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        tv_name = (TextView) findViewById(R.id.shoppanel_tv_name);
        tv_email = (TextView) findViewById(R.id.shoppanel_tv_email);
        tv_phone=(TextView) findViewById(R.id.shoppanel_tv_phone);
        shops_status = (TextView) findViewById(R.id.shoppanel_tv_status);
        shops_status.setVisibility(View.VISIBLE);

        img_options = (ImageView) findViewById(R.id.shoppanel_img_options);
        img_options.setOnClickListener(this);

        shop_list=(ListView) findViewById(R.id.shoppanel_lv_shops);
        shop_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final PopupMenu popup = new PopupMenu(ShopkeeperPanel.this,view, Gravity.RIGHT);
                popup.inflate(R.menu.menu_shop_list);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        popup.dismiss();
                        switch (item.getItemId()) {
                            case R.id.view:
                                Intent i = new Intent(ShopkeeperPanel.this,ShopDetails.class);
                                i.putExtra("shopID",shop_id.get(position).toString());
                                i.putExtra("marketID",market_id.get(position).toString());
                                i.putExtra("parentActivity", "ShopkeeperPanel");
                                startActivity(i);
                                return true;
                            case R.id.delete:
                                DeleteShop(shop_id.get(position).toString(), market_id.get(position).toString());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        tv_name.setText(userDataModelSingleTon.getName());
        tv_email.setText(userDataModelSingleTon.getEmail_id());
        tv_phone.setText(userDataModelSingleTon.getPhone());

        user_id = userDataModelSingleTon.getId().toString();

        shop_id = new ArrayList();
        shop_name = new ArrayList();
        shop_category = new ArrayList();
        market_id = new ArrayList();
        market_name = new ArrayList();

        getShops();
    }

    private void getShops() {
        shop_id.clear();
        shop_name.clear();
        shop_category.clear();
        market_id.clear();
        market_name.clear();
        shop_list.setAdapter(null);
        firebase.child("Shopkeeper_Shops").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shop_id.clear();
                shop_name.clear();
                shop_category.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        shop_id.add(d.getKey());
                        shopDataModel = d.getValue(ShopDataModel.class);
                        market_id.add(shopDataModel.getMarket_id());
                        Log.d("Shop_ID", d.getKey().toString());
                        Log.d("market_ID", shopDataModel.getMarket_id());
                        getShopDetails(d.getKey(),shopDataModel.getMarket_id());
                        shops_status.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getShopDetails(final String shopID, final String marketID){
        firebase.child("Market_Shops").child(marketID).child(shopID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Position", "Getting shop details");
                shopDataModel = dataSnapshot.getValue(ShopDataModel.class);
                         shop_name.add(shopDataModel.getName());
                category1 = (shopDataModel.getCategory1());
                category2 = (shopDataModel.getCategory2());
                category3 = (shopDataModel.getCategory3());
                if(category2.equals("-"))
                    categories = category1;
                else if(category3.equals("-"))
                    categories = category1 + ", " + category2;
                else
                    categories = category1 + ", " + category2 + ", " + category3;

                shop_category.add(categories);
                getMarketName(marketID);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getMarketName(String marketID){
        firebase.child("Markets").child(marketID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Position", "Getting Market Name");
                marketDataModel = dataSnapshot.getValue(MarketDataModel.class);
                market_name.add(marketDataModel.getName());
                if(shop_name.size()==market_name.size())
                    shop_list.setAdapter(new CustomAdapter_ShopsList(ShopkeeperPanel.this,shop_name,shop_category,market_name));
                else
                    Log.d("Shopkeeper panel", "waiting for shops_list");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void DeleteShop(final String shopID, final String marketID){
        new AlertDialog.Builder(ShopkeeperPanel.this)
                .setTitle("Delete Shop!!")
                .setMessage("Do you want to delete this shop request")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        deleteShopDetails(shopID,marketID);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Delete Dialog", "Cancel click");
                    }
                })
                .show();
    }

    private void deleteShopDetails(final String shopID,final String marketID){
        shop_details = firebase.child("Market_Shops").child(marketID).child(shopID);
        shop_details.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(), "Retrying Again...");
                    deleteShopDetails(shopID,marketID);
                } else {
                    Log.d("Position", "Record is deleted from market shop table...");
                    deleteShopkeeperShop(shopID, marketID);
                    Toast.makeText(ShopkeeperPanel.this, "Shop is successfully deleted..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteShopkeeperShop(final String shop_ID,final String market_ID) {
        shopkeeper_shop = firebase.child("Shopkeeper_Shops").child(user_id).child(shop_ID);
        shopkeeper_shop.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    deleteShopkeeperShop(shop_ID, market_ID);
                } else{
                    Log.d("Position", "Record is deleted from shopkeeper shop table...");
                    //getShops();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shoppanel_img_options:
                final PopupMenu popup = new PopupMenu(this, view);
                popup.inflate(R.menu.menu_shopkeeper_panel);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        popup.dismiss();
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
                Intent l = new Intent(ShopkeeperPanel.this,PendingShops.class);
                startActivity(l);
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
