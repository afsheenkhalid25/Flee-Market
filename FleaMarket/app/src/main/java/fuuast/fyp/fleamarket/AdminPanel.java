package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AdminPanel extends ActionBarActivity {

    TextView txtname,txtemail;
    String admin_id;
    Data_SingleTon ds = Data_SingleTon.getInstance();
    ImageView img;
    ListView listView;
    ArrayList mrkt_names,mrkt_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        txtname = (TextView) findViewById(R.id.textView);
        txtemail = (TextView) findViewById(R.id.textView2);
        img = (ImageView) findViewById(R.id.img);
        img.setOnClickListener(new img_OnClickListener());

        txtname.setText(ds.getName());
        txtemail.setText(ds.getEmail_id());
        admin_id = ds.getId();

        getMarketsData();

        CustomAdapter arrayAdapter=new CustomAdapter(this,mrkt_names,mrkt_area);
        listView.setAdapter(arrayAdapter);
    }

    public void getMarketsData(){

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.actions);
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        action("settings");
                        return true;
                    case R.id.logout:
                        action("logout");
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void action (String s) {
        if (s.equals("settings")) {
            Log.d("menu item...", "settings");
        } else if (s.equals("logout")) {
            Log.d("menu item...", "logout");
        }
    }

    public class img_OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i=new Intent(AdminPanel.this,CreateMarket.class);
            startActivity(i);
        }
    }
}
