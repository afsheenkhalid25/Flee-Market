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
    private ValueEventListener VEL1,VEL2;
    private ProgressDialog progressDialog;

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
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        status = (TextView)findViewById(R.id.status);
        status.setVisibility(View.INVISIBLE);
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
                popup = new PopupMenu(ShopsRequest.this, view, Gravity.RIGHT);
                popup.inflate(R.menu.menu_request_list);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
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
                VEL1 = this;
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Log.d("In ShopRequest", "Getting shops request list");
                        shop_id.add(d.getKey());
                        user_id.add(((HashMap<String,String>)d.getValue()).get("user_id"));
                        shop_name.add(((HashMap<String,String>)d.getValue()).get("name"));
                        category1 = ((HashMap<String,String>)d.getValue()).get("category1");
                        category2 = ((HashMap<String,String>)d.getValue()).get("category2");
                        category3 = ((HashMap<String,String>)d.getValue()).get("category3");
                        if(category2.equals("-"))
                            categories = category1;
                        else if(category3.equals("-"))
                            categories = category1 + ", " + category2;
                        else
                            categories = category1 + ", " + category2 + ", " + category3;

                        shop_categories.add(categories);
                        request_listview.setAdapter(new CustomAdapter_ShopsList(ShopsRequest.this,shop_name,shop_categories,null));
                        status.setVisibility(View.INVISIBLE);
                    }
                } else {
                    status.setVisibility(View.VISIBLE);
                    request_listview.setBackgroundResource(R.drawable.ic_action_done);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void onAction(String item) {
        switch (item){
            case "view":
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
        Log.d("In ShopRequest", "Approve Dialog");
        new AlertDialog.Builder(ShopsRequest.this)
                .setTitle("Approve Shop!!")
                .setMessage("Do you want to approve this shop for Market?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Approve Dialog", "OK click");
                        progressDialog.setMessage("\tApproving...");
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
                VEL2 = this;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (Shop_Id.equals(d.getKey().toString())) {
                        Log.d("In ShopRequest", "Getting shop details to approve");
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
                    Log.d("In ShopRequest","Record is inserted in Market shop table...");
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
                    Log.d("In ShopRequest", "Record is inserted in shopkeeper shop table");
                    deleteShopDetails(Shop_Id);
                    Toast.makeText(ShopsRequest.this, "Shop is approved..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void DeleteShop() {
        Log.d("In ShopRequest", "Delete Dialog");
        new AlertDialog.Builder(ShopsRequest.this)
                .setTitle("Delete Shop!!")
                .setMessage("Do you want to delete this shop request")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Delete Dialog", "OK click");
                        progressDialog.setMessage("\tDeleting...");
                        progressDialog.show();
                        deleteShopDetails(Shop_Id);
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
        shop_details = firebase.child("Shop_Requests").child(market_id).child(id);
        shop_details.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(firebaseError.toString(),"Retrying Again...");
                    deleteShopDetails(id);
                } else {
                    Log.d("In ShopRequest", "Record is deleted from shop request table...");
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
                    Log.d("In ShopRequest", "Record is deleted from shopkeeper pending shop table...");
                    progressDialog.dismiss();
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
        if(VEL1!=null)
            request_list.removeEventListener(VEL1);
        if(VEL2!=null)
            shop_details.removeEventListener(VEL2);
        finish();
    }
}
