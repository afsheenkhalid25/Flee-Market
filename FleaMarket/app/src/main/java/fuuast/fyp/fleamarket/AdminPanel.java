package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;


public class AdminPanel extends ActionBarActivity implements View.OnClickListener {

    TextView txtname,txtemail,txtphone;
    UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();
    ImageView profilepic,options;
    ListView marketlist;
    ArrayList mrkt_names,mrkt_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        txtname = (TextView) findViewById(R.id.adminpanel_tv_name);
        txtemail = (TextView) findViewById(R.id.adminpanel_tv_email);
        txtphone=(TextView) findViewById(R.id.adminpanel_tv_phone);

        options = (ImageView) findViewById(R.id.adminpanel_img_options);
        options.setOnClickListener(this);

        marketlist=(ListView) findViewById(R.id.adminpanel_lv_markets);

        txtname.setText(userDataModelSingleTon.getName());
        txtemail.setText(userDataModelSingleTon.getEmail_id());
        txtphone.setText(userDataModelSingleTon.getPhone());

        getMarketsData();
    }

    public void getMarketsData(){

    }

    public void action (String s) {
        if (s.equals("settings")) {
            Log.d("menu item...", "settings");
        } else if (s.equals("logout")) {
            Log.d("menu item...", "logout");
        }
        switch (s){
            case "settings":
                Log.d("menu item...", "settings");
                break;
            case "logout":
                Log.d("menu item...", "logout");
                break;
            case "create":
                Log.d("menu item...", "create market");
                Intent i=new Intent(this,CreateMarket.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.adminpanel_img_options:
                PopupMenu popup = new PopupMenu(this, view);
                popup.inflate(R.menu.actions);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.adminpanel_opt_settings:
                                action("settings");
                                return true;
                            case R.id.adminpanel_opt_logout:
                                action("logout");
                                return true;
                            case R.id.adminpanel_opt_createmarket:
                                action("create");
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
