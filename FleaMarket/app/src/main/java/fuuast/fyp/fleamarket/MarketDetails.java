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

import com.firebase.client.Firebase;

import java.util.ArrayList;

public class MarketDetails extends ActionBarActivity implements View.OnClickListener {

    private TextView tv_market_name,tv_market_address,shops_status;
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();
    private ListView shops_list;
    private ArrayList market_id,market_names,market_address,market_imgURL;
    private Firebase firebase;
    private Boolean CHECK_FINISH=false;
    private ImageView options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_details);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        tv_market_name = (TextView) findViewById(R.id.marketview_tv_name);
        tv_market_address = (TextView) findViewById(R.id.marketview_tv_area);
        shops_status = (TextView) findViewById(R.id.marketview_tv_status);

        options = (ImageView) findViewById(R.id.marketdetails_img_options);
        options.setOnClickListener(this);

        tv_market_name.setText(marketDataModelSingleTon.getMarket_name());
        tv_market_address.setText(marketDataModelSingleTon.getMarket_address());
    }

    private void onAction (String s) {
        switch (s){
            case "shopRequest":
                Log.d("menu item...", "Shop Requests");
                //Intent i = new Intent(this,EditAdminProfile.class);
                //startActivity(i);
                break;
            case "view_on_map":
                Log.d("menu item...", "View On Map");
                //CHECK_FINISH = true;
                //Intent j = new Intent(this,Login.class);
                //startActivity(j);
                break;
            case "edit":
                Log.d("menu item...", "Edit Market");
                //CHECK_FINISH = true;
                //Intent k = new Intent(this,CreateMarket.class);
                //startActivity(k);
                break;
            case "delete":
                Log.d("menu item...", "Delete Market");
                //CHECK_FINISH = true;
                //Intent k = new Intent(this,CreateMarket.class);
                //startActivity(k);
                break;
        }
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
}
