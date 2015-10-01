package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class Login extends ActionBarActivity {

    Firebase fb;

    String email_id,password,message=null,ac_type=null;

    EditText et_email,et_pass;
    ImageView img;
    ArrayList array_list;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);
        fb = new Firebase("https://flee-market.firebaseio.com/");
        array_list = new ArrayList();

        pd = new ProgressDialog(Login.this);

        et_email = (EditText) findViewById(R.id.email);
        et_pass = (EditText) findViewById(R.id.password);

        img = (ImageView) findViewById(R.id.img2);
        img.setOnClickListener(new img_OnClickListener());
    }

    public class img_OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {            
            email_id = et_email.getText().toString();
            password = et_pass.getText().toString();

            if (email_id.equals("") || password.equals("")){
                 Toast.makeText(Login.this, "Please Enter Complete Details", Toast.LENGTH_SHORT).show();}
            else
                Login();
        }
    }

    public void Login()
    {
        img.setEnabled(false);
        pd.setMessage("\tProcessing...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        fb.authWithPassword(email_id, password, new Firebase.AuthResultHandler()
        {
            @Override
            public void onAuthenticated(final AuthData authData)
            {
                Log.d("LOGIN TASK........","ON AUTHENTICATED");
                fb.child("Users").child(authData.getUid()).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Log.d("LOGIN TASK........","ON CHILD ADDED");

                        Users_Data ud = dataSnapshot.getValue(Users_Data.class);
                        Data_SingleTon data = Data_SingleTon.getInstance();

                        data.setId(authData.getUid());
                        data.setName(ud.getName());
                        data.setEmail_id(ud.getEmail_id());
                        data.setPassword(ud.getPassword());
                        data.setPhone(ud.getPhone());
                        data.setType(ud.getType());
                        data.setAddress(ud.getAddress());
                        data.setNic(ud.getNic());
                        data.setImage_url(ud.getImage_url());
                        data.setOrg_name(ud.getOrg_name());
                        data.setOrg_typ(ud.getOrg_typ());
                        data.setOrg_cntct(ud.getOrg_cntct());

                        ac_type = ud.getType().toString();

                        if (ac_type.equals("Admin")) {
                            //....Admin Panel.....
                            pd.dismiss();
                            Intent i=new Intent(Login.this,AdminPanel.class);
                            startActivity(i);
                        }else if (ac_type.equals("Shopkeeper")) {
                            //....Shopkeeper Panel.....
                            pd.dismiss();
                            Toast.makeText(Login.this, "Welcome to Shopkeeper Panel", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        pd.dismiss();
                        message = firebaseError.getMessage();
                        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        img.setEnabled(true);
                    }

                });
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                Log.d("LOGIN TASK........","ON ERROR");
                pd.dismiss();
                message = error.getMessage();
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                img.setEnabled(true);
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        if(ac_type==null){
            Intent i=new Intent(Login.this,MainActivity.class);
            startActivity(i);
        }
    }
}
