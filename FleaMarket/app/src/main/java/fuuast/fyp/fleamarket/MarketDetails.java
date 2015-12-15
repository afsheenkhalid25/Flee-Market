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
    private ListView shop_list;
    private Firebase firebase,shopkeeper_shop,shop_details,shops_list;
    private ValueEventListener VEL;
    private ImageView options;
    private ProgressDialog progressDialog;

    private ShopDataModel shopDataModel = new ShopDataModel();
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
        shops_status.setVisibility(View.INVISIBLE);

        options = (ImageView) findViewById(R.id.marketdetails_img_options);
        options.setOnClickListener(this);

        tv_market_name.setText(marketDataModelSingleTon.getMarket_name());
        tv_market_address.setText(marketDataModelSingleTon.getMarket_address());
        tv_market_day.setText(marketDataModelSingleTon.getDay());

        market_id = marketDataModelSingleTon.getMarket_id().toString();

        shop_id = new ArrayList();
        user_id = new ArrayList();
        shop_name = new ArrayList();
        shop_category = new ArrayList();

        shop_list = (ListView)findViewById(R.id.mv_lv_shops);
        shop_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        getShopList();
    }

    private void getShopList() {
        shops_list = firebase.child("Market_Shops").child(market_id);
        shops_list.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VEL = this;
                user_id.clear();
                shop_id.clear();
                shop_name.clear();
                shop_category.clear();
                shop_list.setAdapter(null);
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Log.d("In MarketDetails", "Getting shops list");
                        shop_id.add(d.getKey());
                        shopDataModel = d.getValue(ShopDataModel.class);
                        user_id.add(shopDataModel.getUser_id());
                        shop_name.add(shopDataModel.getName());
                        category1 = (shopDataModel.getCategory1());
                        category2 = (shopDataModel.getCategory2());
                        category3 = (shopDataModel.getCategory3());
                        if (category2.equals("-"))
                            categories = category1;
                        else if (category3.equals("-"))
                            categories = category1 + ", " + category2;
                        else
                            categories = category1 + ", " + category2 + ", " + category3;

                        shop_category.add(categories);
                        shop_list.setAdapter(new CustomAdapter_ShopsList(MarketDetails.this, shop_name, shop_category, null));
                        shops_status.setVisibility(View.INVISIBLE);
                    }
                }else
                    shops_status.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void DeleteShop(final String shopID, final String marketID){
        Log.d("In MarketDetails","Delete shop dialog");
        new AlertDialog.Builder(MarketDetails.this)
                .setTitle("Delete Shop!!")
                .setMessage("Do you want to delete this shop request")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Delete Dialog", "OK click");
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
                    Log.d("In MarketDetails", "Record is deleted from market shop table...");
                    deleteShopkeeperShop(shopID, marketID);
                }
            }
        });
    }

    private void deleteShopkeeperShop(final String shopID,final String marketID) {
        shopkeeper_shop = firebase.child("Shopkeeper_Shops").child(userID).child(shopID);
        shopkeeper_shop.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    deleteShopkeeperShop(shopID, marketID);
                } else{
                    Log.d("In MarketDetails", "Record is deleted from shopkeeper shop table...");
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
                Intent i3 = new Intent(this,EditMarket.class);
                startActivity(i3);
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
        if(VEL != null)
            shops_list.removeEventListener(VEL);
        finish();
    }
}
