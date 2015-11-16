package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;


public class PendingShops extends ActionBarActivity {

    private String market_id,category1,category2,category3,categories,Shop_Id,User_Id;
    private TextView user_name,status;
    private ListView request_listview;
    private ArrayList user_id,shop_id,shop_name,shop_categories;
    private Firebase firebase,request_list,market_shop,pending_shop;
    private ProgressDialog progressDialog;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_shops);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        progressDialog = new ProgressDialog(PendingShops.this);
        progressDialog.setMessage("\tApproving...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        status = (TextView)findViewById(R.id.status);
        user_name = (TextView) findViewById(R.id.tv_shopkeeper_name);
        user_name.setText(userDataModelSingleTon.getName().toString());

        user_id = new ArrayList();
        shop_id = new ArrayList();
        shop_name = new ArrayList();
        shop_categories = new ArrayList();
        getPendingList();
    }

    private void getPendingList(){

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
