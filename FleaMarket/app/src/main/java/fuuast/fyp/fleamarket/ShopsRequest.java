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


public class ShopsRequest extends ActionBarActivity {

    private String market_id,category1,category2,category3,categories,Shop_Id,User_Id;
    private TextView market_name,status;
    private ListView request_listview;
    private ArrayList user_id,shop_id,shop_name,shop_categories;
    private Firebase firebase,request_list,market_shop,pending_shop,shop_details;
    private ProgressDialog progressDialog;
    private Boolean DataCheck = true;

    private PopupMenu popup;
    private ShopDataModel shopDataModel = new ShopDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops_request);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        progressDialog = new ProgressDialog(ShopsRequest.this);
        progressDialog.setMessage("\tApproving...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        status = (TextView)findViewById(R.id.status);
        market_name = (TextView) findViewById(R.id.tv_market_name);
        market_name.setText(marketDataModelSingleTon.getMarket_name());

        market_id = marketDataModelSingleTon.getMarket_id().toString();

        user_id = new ArrayList();
        shop_id = new ArrayList();
        shop_name = new ArrayList();
        shop_categories = new ArrayList();

        request_listview = (ListView)findViewById(R.id.requests_list);
        request_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Shop_Id = shop_id.get(position).toString();
                User_Id = user_id.get(position).toString();
                Log.d("Shop_Id",Shop_Id);
                popup = new PopupMenu(ShopsRequest.this, view, Gravity.RIGHT);
                popup.inflate(R.menu.menu_request_list);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d("menu item clicked"," "+item);
                        switch (item.getItemId()) {
                            case R.id.view:
                                onAction("view");
                                return true;
                            case R.id.approve:
                                onAction("approve");
                                return true;
                            case R.id.delete:
                                onAction("delete");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        getRequestList();
    }

    public void getRequestList() {
        request_list = firebase.child("Shop_Requests").child(market_id);
        request_list.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_id.clear();
                shop_id.clear();
                shop_name.clear();
                shop_categories.clear();
                request_listview.setAdapter(null);
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
                    shop_categories.add(categories);
                    request_listview.setAdapter(new CustomAdapter_ShopsList(ShopsRequest.this,shop_name,shop_categories,null));
                    status.setVisibility(View.INVISIBLE);
                    DataCheck = false;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        if(DataCheck){
            status.setVisibility(View.VISIBLE);
            request_listview.setBackgroundResource(R.drawable.ic_action_done);
        }
    }

    private void onAction(String item) {
        switch (item){
            case "view":
                Log.d("menu item...", "View");
                Intent i = new Intent(this,ShopDetails.class);
                i.putExtra("shopID",Shop_Id);
                i.putExtra("marketID",market_id);
                i.putExtra("parentActivity","ShopRequest");
                startActivity(i);
                break;
            case "approve":
                ApproveShop();
                break;
            case "delete":
                DeleteShop();
                break;
        }
    }

    private void ApproveShop() {
        new AlertDialog.Builder(ShopsRequest.this)
                .setTitle("Approve Shop!!")
                .setMessage("Do you want to approve this shop for Market?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Approve Dialog", "Clicked");
                        progressDialog.show();
                        getShopDetails();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Approve Dialog", "Cancel click");
                    }
                })
                .show();
    }

    private void getShopDetails() {
        shop_details = firebase.child("Shop_Requests").child(market_id);
        shop_details.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (Shop_Id.equals(d.getKey().toString())) {
                        Log.d("Position", "In Shop Details");
                        shopDataModel = d.getValue(ShopDataModel.class);
                        setMarketShopTable();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(ShopsRequest.this, "Network Error!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMarketShopTable() {
        market_shop = firebase.child("Market_Shops").child(market_id).child(Shop_Id);
        market_shop.setValue(shopDataModel, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError!=null){
                    Toast.makeText(ShopsRequest.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                } else{
                    Log.d("Position","Record is inserted in Market shop table...");
                    setShopkeeperShopTable();
                }
            }
        });
    }

    private void setShopkeeperShopTable() {
        HashMap<String,Object> hashMap=new HashMap<String, Object>();
        hashMap.put("market_id",market_id);
        firebase.child("Shopkeeper_Shops").child(User_Id).child(Shop_Id).setValue(hashMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError!=null){
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    setShopkeeperShopTable();
                }else {
                    Log.d("Position", "Record is inserted in shopkeeper shop table");
                    deleteShopDetails(Shop_Id);
                    getRequestList();
                    progressDialog.dismiss();
                    Toast.makeText(ShopsRequest.this, "Shop is approved..", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void DeleteShop() {
        new AlertDialog.Builder(ShopsRequest.this)
                .setTitle("Delete Shop!!")
                .setMessage("Do you want to delete this shop request")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Delete Dialog", "Clicked");
                        progressDialog.show();
                        deleteShopDetails(Shop_Id);
                        progressDialog.dismiss();
                        Toast.makeText(ShopsRequest.this, "Shop request is successfully deleted..", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Delete Dialog", "Cancel click");
                    }                      })
                .show();
    }

    private void deleteShopDetails(final String id) {
        shop_details.child(id).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    deleteShopDetails(id);
                } else {
                    Log.d("Position", "Record is deleted from shop request table...");
                    deletePendingShop(id);
                }
            }
        });
    }

    private void deletePendingShop(final String id) {
        pending_shop = firebase.child("Shopkeeper_Pending_Shops").child(User_Id).child(id);
        pending_shop.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    deletePendingShop(id);
                } else {
                    Log.d("Position", "Record is deleted from shopkeeper pending shop table...");
                    getRequestList();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ShopsRequest.this,MarketDetails.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
