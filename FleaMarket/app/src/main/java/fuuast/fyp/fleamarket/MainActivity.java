package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
{
    ImageView img1,img2,img3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img1=(ImageView) findViewById(R.id.img);
        img1.setOnClickListener(new img1_OnClickListener());

        img2=(ImageView) findViewById(R.id.img2);
        img2.setOnClickListener(new img2_OnClickListener());

        img3=(ImageView) findViewById(R.id.img3);
        img3.setOnClickListener(new img3_OnClickListener());
    }

    private class img1_OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            //....create account panel.....
            Intent i=new Intent(MainActivity.this,CreateAccount.class);
            startActivity(i);
        }
    }

    private class img2_OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            //....login panel....
            Intent i=new Intent(MainActivity.this,Login.class);
            startActivity(i);
        }
    }

    private class img3_OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            //....market activity....
            Toast.makeText(MainActivity.this,"Ready to view Market?",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}