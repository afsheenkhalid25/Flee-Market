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

public class MarketDetails extends ActionBarActivity implements View.OnClickListener {

    private String market_id,category1,category2,category3,categories,userID;
    private ArrayList user_id,shop_id,shop_name,shop_category;
    private TextView tv_market_name,tv_market_address,tv_market_day,shops_status;
    private ListView shops_list;
    private Firebase firebase,shopkeeper_shop,shop_details;
    private ImageView options;
    private ProgressDialog progressDialog;

    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_details);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://flee-market.firebaseio.com/");

        progressDialog = new ProgressDialog(MarketDetails.this);
        progressDialog.setMessage("\tDeleting...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        tv_market_name = (TextView) findViewById(R.id.marketview_tv_name);
        tv_market_address = (TextView) findViewById(R.id.marketview_tv_area);
        tv_market_day = (TextView) findViewById(R.id.tv_day);
        shops_status = (TextView) findViewById(R.id.marketview_tv_status);
        shops_status.setVisibility(View.VISIBLE);

        options = (ImageView) findViewById(R.id.marketdetails_img_options);
        options.setOnClickListener(this);

        tv_market_name.setText(marketDataModelSingleTon.getMarket_name());
        tv_market_address.setText(marketDataModelSingleTon.getMarket_address());
        tv_market_day.setText(marketDataModelSingleTon.getDay());

        market_id = marketDataModelSingleTon.getMarket_id().toString();
        shops_list = (ListView)findViewById(R.id.mv_lv_shops);
        shops_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                userID = user_id.get(position).toString();
                final PopupMenu popup = new PopupMenu(MarketDetails.this,view, Gravity.RIGHT);
                popup.inflate(R.menu.menu_shop_list);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        popup.dismiss();
                        switch (item.getItemId()) {
                            case R.id.view:
                                Intent i = new Intent(MarketDetails.this,ShopDetails.class);
                                i.putExtra("shopID",shop_id.get(position).toString());
                                i.putExtra("marketID",market_id);
                                i.putExtra("parentActivity","MarketDetails");
                                startActivity(i);
                                return true;
                            case R.id.delete:
                                DeleteShop(shop_id.get(position).toString(), market_id);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        shop_id = new ArrayList();
        user_id = new ArrayList();
        shop_name = new ArrayList();
        shop_category = new ArrayList();

        getShopList();
    }

    public void getShopList(){
        user_id.clear();
        shop_id.clear();
        shop_name.clear();
        shop_category.clear();
        firebase.child("Market_Shops").child(market_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    shop_id.add(d.getKey());
                    user_id.add(((HashMap<String,String>)d.getValue()).get("user_id"));
                    shop_name.add(((HashMap<String,String>)d.getValue()).get("name"));
                    category1 = ((HashMap<String,String>)d.getValue()).get("category1");
                    category2 = ((HashMap<String,String>)d.getValue()).get("category2");
                    category3 = ((HashMap<String,String>)d.getValue()).get("category3");
                    if(category2.equals("-")){
                        categories = category1;
                    } else if(category3.equals("-")){
                        categories = category1 + ", " + category2;
                    } else {
                        categories = category1 + ", " + category2 + ", " + category3;
                    }
                    shop_category.add(categories);
                    shops_list.setAdapter(new CustomAdapter_ShopsList(MarketDetails.this,shop_name,shop_category,null));
                    shops_status.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void DeleteShop(final String shopID, final String marketID){
        new AlertDialog.Builder(MarketDetails.this)
                .setTitle("Delete Shop!!")
                .setMessage("Do you want to delete this shop request")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        deleteShopkeeperShop(shopID, marketID);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Delete Dialog", "Cancel click");
                    }
                })
                .show();
    }

    private void deleteShopkeeperShop(final String shop_ID,final String market_ID) {
        shopkeeper_shop = firebase.child("Shopkeeper_Shops").child(userID).child(shop_ID);
        shopkeeper_shop.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    deleteShopkeeperShop(shop_ID, market_ID);
                } else{
                    Log.d("Position", "Record is deleted from shopkeeper shop table...");
                    deleteShopDetails(shop_ID,market_ID);
                }
            }
        });
    }

    private void deleteShopDetails(final String shop_ID,final String market_ID){
        shop_details = firebase.child("Market_Shops").child(market_ID).child(shop_ID);
        shop_details.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(), "Retrying Again...");
                    deleteShopDetails(shop_ID,market_ID);
                } else {
                    Log.d("Position", "Record is deleted from market shop table...");
                    progressDialog.dismiss();
                    Toast.makeText(MarketDetails.this, "Shop is successfully deleted..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.marketdetails_img_options:
                PopupMenu popup = new PopupMenu(this, view);
                popup.inflate(R.menu.menu_market_details);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.marketdetails_opt_shoprequest:
                                onAction("shopRequest");
                                return true;
                            case R.id.marketdetails_opt_viewonmap:
                                onAction("view_on_map");
                                return true;
                            case R.id.marketdetails_opt_edit:
                                onAction("edit");
                                return true;
                            case R.id.marketdetails_opt_delete:
                                onAction("delete");
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
            case "shopRequest":
                Log.d("menu item...", "Shop Requests");
                Intent i = new Intent(this,ShopsRequest.class);
                startActivity(i);
                break;
            case "view_on_map":
                Log.d("menu item...", "View On Map");
                Intent i2 = new Intent(this,MarketMap.class);
                startActivity(i2);
                break;
            case "edit":
                Log.d("menu item...", "Edit Market");
                //Intent k = new Intent(this,CreateMarket.class);
                //startActivity(k);
                break;
            case "delete":
                Log.d("menu item...", "Delete Market");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(this,AdminPanel.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
