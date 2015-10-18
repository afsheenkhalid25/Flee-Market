package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;

import java.util.ArrayList;

public class Login extends ActionBarActivity implements View.OnClickListener{

    private Firebase fb;
    private String email_id,password,ac_type=null;
    private EditText et_email,et_pass;
    private ImageView img_login,img_create_account;
    private View v;
    private AuthData loginAuthData;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);
        fb = new Firebase("https://flee-market.firebaseio.com/");

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("\tProcessing...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        et_email = (EditText) findViewById(R.id.login_et_email);
        et_pass = (EditText) findViewById(R.id.login_et_password);

        img_login = (ImageView) findViewById(R.id.login_img_login);
        img_login.setOnClickListener(this);
        img_create_account = (ImageView) findViewById(R.id.login_btn_createaccount);
        img_create_account.setOnClickListener(this);

        v = findViewById(R.id.login_rl_customers);
        v.setOnClickListener(this);

        loginAuthData = fb.getAuth();
        if (loginAuthData != null) {
            img_login.setEnabled(false);
            et_email.setEnabled(false);
            et_pass.setEnabled(false);
            progressDialog.show();
            loginTask();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_img_login:
                email_id = et_email.getText().toString();
                password = et_pass.getText().toString();
                if (email_id.equals("") || password.equals("")){
                    Toast.makeText(Login.this, "Please Enter Complete Details", Toast.LENGTH_SHORT).show();}
                else
                    Login();
                break;
            case R.id.login_btn_createaccount:
                Intent i=new Intent(Login.this,CreateAccount.class);
                startActivity(i);
                break;
            case R.id.login_rl_customers:
                Toast.makeText(Login.this,"Ready to view Market?",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void Login()
    {
        img_login.setEnabled(false);
        et_email.setEnabled(false);
        et_pass.setEnabled(false);
        progressDialog.show();
        fb.authWithPassword(email_id, password, new Firebase.AuthResultHandler()
        {
            @Override
            public void onAuthenticated(final AuthData authData)
            {
                Log.d("LOGIN TASK........","ON AUTHENTICATED");
                loginAuthData = authData;
                loginTask();
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                Log.d("LOGIN TASK........","ON ERROR"+error);
                progressDialog.dismiss();
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                et_email.setText("");
                et_pass.setText("");
                et_email.setEnabled(true);
                et_pass.setEnabled(true);
                img_login.setEnabled(true);
            }
        });
    }

    public void loginTask()
    {
        fb.child("Users").child(loginAuthData.getUid()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("LOGIN TASK........","ON CHILD ADDED");

                UserDataModel ud = dataSnapshot.getValue(UserDataModel.class);
                UserDataModelSingleTon data = UserDataModelSingleTon.getInstance();

                data.setId(loginAuthData.getUid());
                data.setName(ud.getName());
                data.setEmail_id(ud.getEmail_id());
                data.setPassword(password);
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
                    progressDialog.dismiss();
                    Intent i=new Intent(Login.this,AdminPanel.class);
                    startActivity(i);
                }else if (ac_type.equals("Shopkeeper")) {
                    progressDialog.dismiss();
                    Intent i=new Intent(Login.this,ShopkeeperPanel.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("LOGIN TASK........","ON CANCELLED "+firebaseError.getMessage());
                progressDialog.dismiss();
                Toast.makeText(Login.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                et_email.setText("");
                et_pass.setText("");
                img_login.setEnabled(true);
                et_email.setEnabled(true);
                et_pass.setEnabled(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
            finish();
    }
}
