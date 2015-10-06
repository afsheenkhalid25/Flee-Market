package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class EditAdminProfile extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if(CHECK_FINISH){
            finish();
        }*/
        Intent i=new Intent(EditAdminProfile.this,AdminPanel.class);
        startActivity(i);
    }
}
