package fuuast.fyp.fleamarket;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class PendingShops extends ActionBarActivity {

    private String user_id,category1,category2,category3,categories;
    private TextView user_name,status;
    private ListView pending_list;
    private ArrayList shop_id,shop_name,shop_categories,market_name,market_id;
    private Firebase firebase,pending_shop,shop_details,delete_pending_shop;
    private ValueEventListener VEL;
    private ProgressDialog progressDialog;
    private PopupMenu popup;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private MarketDataModel marketDataModel = new MarketDataModel();
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_shops);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        progressDialog = new ProgressDialog(PendingShops.this);
        progressDialog.setMessage("\tDeleting...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        status = (TextView)findViewById(R.id.status);
        status.setVisibility(View.INVISIBLE);
        user_name = (TextView) findViewById(R.id.tv_shopkeeper_name);
        user_name.setText(userDataModelSingleTon.getName().toString());

        user_id = userDataModelSingleTon.getId().toString();

        shop_id = new ArrayList();
        shop_name = new ArrayList();
        shop_categories = new ArrayList();
        market_id = new ArrayList();
        market_name = new ArrayList();

        pending_list = (ListView)findViewById(R.id.pendingShops_list);
        pending_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                popup = new PopupMenu(PendingShops.this, view, Gravity.RIGHT);
                popup.inflate(R.menu.menu_shop_list);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view:
                                Intent i = new Intent(PendingShops.this,ShopDetails.class);
                                i.putExtra("shopID",shop_id.get(position).toString());
                                i.putExtra("marketID",market_id.get(position).toString());
                                i.putExtra("parentActivity","PendingShops");
                                startActivity(i);
                                return true;
                            case R.id.delete:
                                DeleteShop(shop_id.get(position).toString(),market_id.get(position).toString());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        getPendingList();
    }

    private void getPendingList() {
        pending_shop = firebase.child("Shopkeeper_Pending_Shops").child(user_id);
        pending_shop.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VEL = this;
                shop_id.clear();
                shop_name.clear();
                shop_categories.clear();
                market_id.clear();
                market_name.clear();
                pending_list.setAdapter(null);
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Log.d("In PendingShop", "Getting pending shops list");
                        shop_id.add(d.getKey());
                        String marketID = ((HashMap<String, String>) d.getValue()).get("market_id");
                        market_id.add(marketID);
                        Log.d("Shop_ID and Market_ID", d.getKey().toString()+" "+marketID);
                        getShopDetails(d.getKey(),marketID);

                    }
                } else {
                    status.setVisibility(View.VISIBLE);
                    pending_list.setBackgroundResource(R.drawable.ic_action_done);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getShopDetails(final String shop_ID,final String market_ID) {
        firebase.child("Shop_Requests").child(market_ID).child(shop_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.d("In PendingShop", "Getting shop details");
                    shopDataModel = dataSnapshot.getValue(ShopDataModel.class);
                    shop_name.add(shopDataModel.getName().toString());
                    category1 = (shopDataModel.getCategory1().toString());
                    category2 = (shopDataModel.getCategory2().toString());
                    category3 = (shopDataModel.getCategory3().toString());
                    if (category2.equals("-")) {
                        categories = category1;
                    } else if (category3.equals("-")) {
                        categories = category1 + ", " + category2;
                    } else {
                        categories = category1 + ", " + category2 + ", " + category3;
                    }
                    shop_categories.add(categories);
                    getMarketName(market_ID);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getMarketName(final String marketID){
        firebase.child("Markets").child(marketID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("In PendingShop", "Getting Market Name");
                marketDataModel = dataSnapshot.getValue(MarketDataModel.class);
                market_name.add(marketDataModel.getName());
                Log.d("Market_name", marketDataModel.getName());
                if(shop_name.size()==market_name.size())
                    pending_list.setAdapter(new CustomAdapter_ShopsList(PendingShops.this,shop_name,shop_categories,market_name));
                else
                    Log.d("In PendingShop","waiting for pending_list");
                status.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void DeleteShop(final String shopID, final String marketID) {
        Log.d("In PendingShop","Delete shop dialog");
        new AlertDialog.Builder(PendingShops.this)
                .setTitle("Delete Shop!!")
                .setMessage("Do you want to delete this shop request")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Delete Dialog", "OK click");
                        progressDialog.show();
                        deletePendingShop(shopID,marketID);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Delete Dialog", "Cancel click");
                    }
                })
                .show();
    }

    private void deletePendingShop(final String shopID,final String marketID) {
        delete_pending_shop = firebase.child("Shopkeeper_Pending_Shops").child(user_id).child(shopID);
        delete_pending_shop.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    deletePendingShop(shopID,marketID);
                } else{
                    Log.d("In PendingShop", "Record is deleted from shopkeeper pending shop table...");
                    deleteShopDetails(shopID,marketID);
                }
            }
        });
    }

    private void deleteShopDetails(final String shopID,final String marketID){
        shop_details = firebase.child("Shop_Requests").child(marketID).child(shopID);
        shop_details.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(), "Retrying Again...");
                    deleteShopDetails(shopID,marketID);
                } else {
                    Log.d("In PendingShop", "Record is deleted from shop request table...");
                    progressDialog.dismiss();
                    Toast.makeText(PendingShops.this, "Shop request is successfully deleted..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PendingShops.this,ShopkeeperPanel.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(VEL != null)
            pending_shop.removeEventListener(VEL);
        finish();
    }
}
