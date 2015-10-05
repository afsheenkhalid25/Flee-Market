package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;


public class MarketDetails extends ActionBarActivity implements View.OnClickListener {

    ImageView options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_details);

        options = (ImageView) findViewById(R.id.marketdetails_img_options);
        options.setOnClickListener(this);
    }

    private void action (String s) {
        switch (s){
            case "shoprequest":
                Log.d("menu item...", "shoprequest");
                //Intent i = new Intent(this,EditAdminProfile.class);
                //startActivity(i);
                break;
            case "viewonmap":
                Log.d("menu item...", "viewonmap");
                //CHECK_FINISH = true;
                //Intent j = new Intent(this,Login.class);
                //startActivity(j);
                break;
            case "edit":
                Log.d("menu item...", "edit market");
                //CHECK_FINISH = true;
                //Intent k = new Intent(this,CreateMarket.class);
                //startActivity(k);
                break;
            case "delete":
                Log.d("menu item...", "delete market");
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
                                action("shoprequest");
                                return true;
                            case R.id.marketdetails_opt_viewonmap:
                                action("viewonmap");
                                return true;
                            case R.id.marketdetails_opt_edit:
                                action("edit");
                                return true;
                            case R.id.marketdetails_opt_delete:
                                action("delete");
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
