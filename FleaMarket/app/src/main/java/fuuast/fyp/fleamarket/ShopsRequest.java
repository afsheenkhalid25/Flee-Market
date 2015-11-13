package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class ShopsRequest extends ActionBarActivity {

    ListView request_list;
    ArrayList market_id,shopkeeper_id;
    private Firebase firebase,shop_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops_request);

        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        market_id = new ArrayList();
        shopkeeper_id = new ArrayList();
        getRequestList();

        request_list = (ListView)findViewById(R.id.requests_list);
        request_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popup = new PopupMenu(ShopsRequest.this, request_list);
                popup.getMenuInflater().inflate(R.menu.menu_shop_request, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d("Position", "menu item clicked"+item);
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    public void getRequestList() {

        firebase.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                market_id.clear();
                shopkeeper_id.clear();
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    market_id.add(d.getKey());
                    Log.d("Data",d.getValue().toString());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

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
