package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
    private int item_id;
    private TextView user_name,status;
    private ListView pending_list;
    private ArrayList shop_id,shop_name,shop_categories,market_name,market_id;
    private Firebase firebase,pending_shop,shop_details;
    private ProgressDialog progressDialog;

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
        user_name = (TextView) findViewById(R.id.tv_shopkeeper_name);
        user_name.setText(userDataModelSingleTon.getName().toString());

        user_id = userDataModelSingleTon.getId().toString();

        pending_list = (ListView)findViewById(R.id.pendingShops_list);
        pending_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(PendingShops.this)
                        .setTitle("Delete Shop!!")
                        .setMessage("Do you want to delete this shop request")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                item_id = position;
                                deletePendingShop(shop_id.get(position).toString(),market_id.get(position).toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Delete Dialog", "Cancel click");
                            }
                        })
                        .show();
            }
        });

        shop_id = new ArrayList();
        shop_name = new ArrayList();
        shop_categories = new ArrayList();
        market_id = new ArrayList();
        market_name = new ArrayList();
        getPendingList();
    }

    private void getPendingList() {
        shop_id.clear();
        shop_name.clear();
        shop_categories.clear();
        market_id.clear();
        market_name.clear();
        firebase.child("Shopkeeper_Pending_Shops").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Log.d("Position", "Getting Shop id and market id");
                        shop_id.add(d.getKey());
                        Log.d("Shop_ID", d.getKey());
                        String market_ID = ((HashMap<String, String>) d.getValue()).get("market_id");
                        Log.d("Market_ID", market_ID);
                        market_id.add(market_ID);
                        getShopDetails(d.getKey(),market_ID);
                        status.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void getShopDetails(final String shop_ID,final String market_ID) {
        firebase.child("Shop_Requests").child(market_ID).child(shop_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.d("Position", "Getting shop details");
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

    private void getMarketName(final String market_ID){
        firebase.child("Markets").child(market_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Position", "Getting Market Name");
                marketDataModel = dataSnapshot.getValue(MarketDataModel.class);
                market_name.add(marketDataModel.getName());
                Log.d("Market_name", marketDataModel.getName());

                if(shop_name.size()==market_name.size())
                    pending_list.setAdapter(new CustomAdapter_PendingList(PendingShops.this,shop_name,shop_categories,market_name));
                else
                    Log.d("Position","waiting for listview");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void deletePendingShop(final String shop_ID,final String market_ID) {
        pending_shop = firebase.child("Shopkeeper_Pending_Shops").child(user_id).child(shop_ID);
        pending_shop.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    deletePendingShop(shop_ID,market_ID);
                } else{
                    Log.d("Position", "Record is deleted from shopkeeper pending shop table...");
                    deleteShopDetails(shop_ID,market_ID);
                }
            }
        });
    }

    private void deleteShopDetails(final String shop_ID,final String market_ID){
        shop_details = firebase.child("Shop_Requests").child(market_ID).child(shop_ID);
        shop_details.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(), "Retrying Again...");
                    deleteShopDetails(shop_ID,market_ID);
                } else {
                    Log.d("Position", "Record is deleted from shop request table...");
                    shop_name.remove(item_id);
                    shop_categories.remove(item_id);
                    market_name.remove(item_id);
                    pending_list.setAdapter(new CustomAdapter_PendingList(PendingShops.this,shop_name,shop_categories,market_name));
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
        finish();
    }
}
