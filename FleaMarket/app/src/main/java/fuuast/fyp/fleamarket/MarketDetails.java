package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

    private String market_id,category1,category2,category3,categories;
    private ArrayList shop_id,shop_name,shop_category;
    private TextView tv_market_name,tv_market_address,shops_status;
    private ListView shops_list;
    private Firebase firebase;
    private ImageView options;

    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_details);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://flee-market.firebaseio.com/");

        tv_market_name = (TextView) findViewById(R.id.marketview_tv_name);
        tv_market_address = (TextView) findViewById(R.id.marketview_tv_area);
        shops_status = (TextView) findViewById(R.id.marketview_tv_status);
        shops_status.setVisibility(View.VISIBLE);

        options = (ImageView) findViewById(R.id.marketdetails_img_options);
        options.setOnClickListener(this);

        tv_market_name.setText(marketDataModelSingleTon.getMarket_name());
        tv_market_address.setText(marketDataModelSingleTon.getMarket_address());

        market_id = marketDataModelSingleTon.getMarket_id().toString();
        shops_list = (ListView)findViewById(R.id.mv_lv_shops);

        shop_id = new ArrayList();
        shop_name = new ArrayList();
        shop_category = new ArrayList();

        getShopList();
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
                Intent j = new Intent(this,MarketMapView.class);
                startActivity(j);
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

    public void getShopList(){
        shop_id = new ArrayList();
        shop_name = new ArrayList();
        shop_category = new ArrayList();
        firebase.child("Market_Shops").child(market_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    shop_id.add(d.getKey());
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
                    shops_list.setAdapter(new CustomAdapter_ShopsList(MarketDetails.this,shop_name,shop_category));
                }
                shops_status.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

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
