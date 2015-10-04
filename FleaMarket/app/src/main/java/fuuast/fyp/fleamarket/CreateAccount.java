package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CreateAccount extends ActionBarActivity
{
    private String name,email_id,password,re_password,phone,type,nic,address,org_name,org_cntct,org_typ,image_url,message=null;

    private Firebase fb;

    private EditText et_name,et_email,et_pass,et_repass,et_phn,et_add,et_nic,et_On,et_Oc,et_Ot;
    private ImageView img;
    private Spinner sp;
    private ScrollView sv;
    private ProgressDialog pd;
    private UserDataModelSingleTon data = UserDataModelSingleTon.getInstance();
    private UserDataModel ud = new UserDataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Firebase.setAndroidContext(this);
        fb = new Firebase("https://flee-market.firebaseio.com/");

        pd = new ProgressDialog(CreateAccount.this);

        et_name = (EditText) findViewById(R.id.ca_et_name);
        et_email = (EditText) findViewById(R.id.ca_et_email);
        et_pass = (EditText) findViewById(R.id.ca_et_password);
        et_repass = (EditText) findViewById(R.id.ca_et_repassword);
        et_phn = (EditText) findViewById(R.id.ca_et_phone);
        et_add = (EditText) findViewById(R.id.ca_et_address);
        et_nic = (EditText) findViewById(R.id.ca_et_nic);
        et_On = (EditText) findViewById(R.id.ca_et_org_name);
        et_Oc = (EditText) findViewById(R.id.ca_et_org_cntct);
        et_Ot = (EditText) findViewById(R.id.ca_et_org_type);
        sv = (ScrollView) findViewById(R.id.scrollView);

        img = (ImageView) findViewById(R.id.ca_img);
        img.setOnClickListener(new img_OnClickListener());

        sp = (Spinner) findViewById(R.id.ca_spinner);

        List<String> list = new ArrayList<String>();
        list.add("Admin");
        list.add("Shopkeeper");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);
    }

    public class img_OnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            type = sp.getSelectedItem().toString();
            name = et_name.getText().toString();
            email_id = et_email.getText().toString();
            password = et_pass.getText().toString();
            re_password = et_repass.getText().toString();
            phone = et_phn.getText().toString();
            address = et_add.getText().toString();
            nic = et_nic.getText().toString();
            org_name = et_On.getText().toString();
            org_cntct = et_Oc.getText().toString();
            org_typ = et_Ot.getText().toString();

            if(type.equals("Admin"))
            {
                if (name.equals("") || email_id.equals("") || password.equals("") || re_password.equals("") || phone.equals("")||address.equals("")||nic.equals("")
                        ||org_name.equals("")||org_cntct.equals("")||org_typ.equals(""))
                    Toast.makeText(CreateAccount.this, "Please Enter Complete Details", Toast.LENGTH_SHORT).show();
                else if(password.equals(re_password))
                    CreateAccount();
                else
                    Toast.makeText(CreateAccount.this, "Password Does not Match", Toast.LENGTH_SHORT).show();
            }
            else if(type.equals("Shopkeeper"))
            {
                if (name.equals("") || email_id.equals("") || password.equals("") || re_password.equals("") || phone.equals("")||address.equals("")||nic.equals(""))
                    Toast.makeText(CreateAccount.this, "Please Enter Complete Details", Toast.LENGTH_SHORT).show();
                else if(password.equals(re_password)){
                    org_name="-";
                    org_typ="-";
                    org_cntct="-";
                    CreateAccount();
                }
                else
                    Toast.makeText(CreateAccount.this, "Password Does not Match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void CreateAccount()
    {
        Log.d("Position........", "in create account function");
        img.setEnabled(false);
        pd.setMessage("\tProcessing...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        Log.d("Position........","in create user");
        fb.createUser(email_id, password, new Firebase.ValueResultHandler<Map<String, Object>>()
        {
            @Override
            public void onSuccess(final Map<String, Object> result)
            {
                Log.d("Position........","on success");
                fb.authWithPassword(email_id, password, new Firebase.AuthResultHandler()
                {
                    @Override
                    public void onAuthenticated(final AuthData authData)
                    {
                        Log.d("Position........","on authenticated");

                        ud.setName(name);
                        ud.setEmail_id(email_id);
                        ud.setPassword(password);
                        ud.setType(type);
                        ud.setPhone(phone);
                        ud.setAddress(address);
                        ud.setNic(nic);
                        ud.setOrg_name(org_name);
                        ud.setOrg_typ(org_typ);
                        ud.setOrg_cntct(org_cntct);

                        //setting value in UserDataModelSingleTon class for using in admin panel class....
                        data.setId(authData.getUid());
                        setUserDataModelSingleTon();

                        fb.child("Users").child(authData.getUid()).setValue(ud, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                pd.dismiss();
                                if(firebaseError==null){
                                    Log.d("Position........","on complete ");
                                    Toast.makeText(CreateAccount.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    if (type.equals("Admin")) {
                                        //....Admin Panel.....
                                        Toast.makeText(CreateAccount.this, "Welcome to Admin Panel", Toast.LENGTH_SHORT).show();
                                        Intent i=new Intent(CreateAccount.this,AdminPanel.class);
                                        startActivity(i);
                                    } else if (type.equals("Shopkeeper")) {
                                        //....Shopkeeper Panel.....
                                        Toast.makeText(CreateAccount.this, "Welcome to Shopkeeper Panel", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Log.d("Position........","in data saving error");
                                    fb.removeUser(email_id,password, new Firebase.ResultHandler() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(CreateAccount.this, "Try again to Create Account", Toast.LENGTH_SHORT).show();
                                        }
                                        @Override
                                        public void onError(FirebaseError firebaseError) {

                                        }
                                    });
                                }
                            }

                        });
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        Log.d("Position........", "in authenticated error ");
                        pd.dismiss();
                        message = error.getMessage();
                        Toast.makeText(CreateAccount.this, message, Toast.LENGTH_SHORT).show();
                        img.setEnabled(true);
                    }
                });
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Log.d("Position........", "create user error ");
                pd.dismiss();
                message = firebaseError.getMessage();
                Toast.makeText(CreateAccount.this, message, Toast.LENGTH_SHORT).show();
                img.setEnabled(true);
            }
        });
    }

    private void setUserDataModelSingleTon()
    {
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
