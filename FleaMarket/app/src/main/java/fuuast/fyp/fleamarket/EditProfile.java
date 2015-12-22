package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class EditProfile extends ActionBarActivity{

    private Firebase firebase;
    private ImageView img_sd;
    private SlidingDrawer sd_Others;
    private ProgressDialog progressDialog;
    private Button b1,b2;
    private EditText et_name,et_oldPass,et_crPass,et_rePass,et_phn,et_add,et_nic,et_On,et_Oc,et_Ot;
    private String user_id,email_id,name,phone,nic,address,org_name,org_contact,org_type,user_type,parentActivity;

    private UserDataModel userDataModel = new UserDataModel();
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance(); ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://flee-market.firebaseio.com/");

        Bundle bundle = getIntent().getExtras();
        parentActivity = bundle.getString("ParentActivity");

        progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setMessage("\tUpdating Profile...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        et_name = (EditText) findViewById(R.id.et_name);
        et_phn = (EditText) findViewById(R.id.ca_et_phone);
        et_add = (EditText) findViewById(R.id.ca_et_address);
        et_nic = (EditText) findViewById(R.id.ca_et_nic);
        et_On = (EditText) findViewById(R.id.ca_et_org_name);
        et_Oc = (EditText) findViewById(R.id.ca_et_org_cntct);
        et_Ot = (EditText) findViewById(R.id.ca_et_org_type);
        et_crPass = (EditText) findViewById(R.id.et_newpass);
        et_rePass = (EditText) findViewById(R.id.et_retypPass);
        et_oldPass = (EditText) findViewById(R.id.et_oldpass);

        b1=(Button) findViewById(R.id.ep_btn_chgpass);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });

        b2= (Button) findViewById(R.id.ep_btn_updateinfo);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });

        setSlidingDrawers();

        user_id=userDataModelSingleTon.getId();
        email_id=userDataModelSingleTon.getEmail_id();
        user_type=userDataModelSingleTon.getType();

        showData();
    }

    private void setSlidingDrawers() {
        sd_Others = (SlidingDrawer) findViewById(R.id.sd_chngOthr);
        img_sd = (ImageView) findViewById(R.id.img_sdButton);
        sd_Others.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                img_sd.setImageResource(R.drawable.ic_action_collapse);
                sd_Others.getLayoutParams().height = 950;
            }
        });

        sd_Others.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                img_sd.setImageResource(R.drawable.ic_action_expand);
                sd_Others.getLayoutParams().height = 30;
            }
        });
    }

    private void showData() {
        et_name.setText(userDataModelSingleTon.getName());
        et_phn.setText(userDataModelSingleTon.getPhone());
        et_add.setText(userDataModelSingleTon.getAddress());
        et_nic.setText(userDataModelSingleTon.getNic());
        et_On.setText(userDataModelSingleTon.getOrg_name());
        et_Oc.setText(userDataModelSingleTon.getOrg_contact());
        et_Ot.setText(userDataModelSingleTon.getOrg_typ());

        ((TextView) findViewById(R.id.tv_email)).setText(email_id);
        ((TextView) findViewById(R.id.tv_type)).setText("Account type: "+user_type);

        if(user_type.equals("Shopkeeper")){
            et_On.setEnabled(false);
            et_Oc.setEnabled(false);
            et_Ot.setEnabled(false);
        }
    }

    private void updatePassword(){
            if (!et_crPass.getText().toString().equals("")&&!et_rePass.getText().toString().equals("")&&et_crPass.getText().toString().equals(et_rePass.getText().toString())){
                new AlertDialog.Builder(EditProfile.this)
                        .setTitle("Update Password")
                        .setMessage("Are you sure you want to update password?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                firebase.changePassword(email_id,et_oldPass.getText().toString(),et_crPass.getText().toString(), new Firebase.ResultHandler() {
                                    @Override
                                    public void onSuccess() {
                                        progressDialog.dismiss();
                                        Log.d("MESSAGE.......","Password Changed");
                                        firebase.unauth();
                                        Intent i=new Intent(EditProfile.this,Login.class);
                                        startActivity(i);
                                    }

                                    @Override
                                    public void onError(FirebaseError firebaseError) {
                                        progressDialog.dismiss();
                                        Toast.makeText(EditProfile.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        et_oldPass.setText("");
                                        et_crPass.setText("");
                                        et_rePass.setText("");
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                et_oldPass.setText("");
                                et_crPass.setText("");
                                et_rePass.setText("");
                            }
                        }).show();
            }
            else{
                Toast.makeText(EditProfile.this, "Password Doesn't Match Or Fields Empty", Toast.LENGTH_SHORT).show();
            }
    }

    private void updateInfo() {
        name = et_name.getText().toString();
        phone = et_phn.getText().toString();
        address = et_add.getText().toString();
        nic = et_nic.getText().toString();
        org_name = et_On.getText().toString();
        org_contact = et_Oc.getText().toString();
        org_type = et_Ot.getText().toString();

        if (et_name.getText().toString().equals("")||et_nic.getText().toString().equals("")||et_phn.getText().toString().equals("")||et_add.getText().toString().equals("")||et_On.getText().toString().equals("")||et_Oc.getText().toString().equals("")||et_Ot.getText().toString().equals(""))
            Toast.makeText(EditProfile.this, "Error:"+"\n"+"Some Fields Are Empty", Toast.LENGTH_SHORT).show();
        else
        {
            new AlertDialog.Builder(EditProfile.this)
                .setTitle("Update Profile")
                .setMessage("Are you sure you want to update profile?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        setData();
                        firebase.child("Users").child(user_id).setValue(userDataModel, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError == null) {
                                    Log.d("Position........", "Data Inserted");
                                    progressDialog.dismiss();
                                    Toast.makeText(EditProfile.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                    firebase.unauth();
                                    Intent i=new Intent(EditProfile.this,Login.class);
                                    startActivity(i);
                                } else {
                                    Log.d("Position........", "in data saving error");
                                    progressDialog.dismiss();
                                    Toast.makeText(EditProfile.this, "Error!! Try to update later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
            }).show();
        }
    }

    public void setData() {
        userDataModel.setEmail_id(email_id);
        userDataModel.setType(user_type);
        userDataModel.setName(name);
        userDataModel.setPhone(phone);
        userDataModel.setAddress(address);
        userDataModel.setNic(nic);
        userDataModel.setOrg_name(org_name);
        userDataModel.setOrg_contact(org_contact);
        userDataModel.setOrg_typ(org_type);
    }

    @Override
    public void onBackPressed() {
        switch (parentActivity){
            case "ShopkeeperPanel":
                Intent i1=new Intent(EditProfile.this,ShopkeeperPanel.class);
                startActivity(i1);
                break;
            case "AdminPanel":
                Intent i2=new Intent(EditProfile.this,AdminPanel.class);
                startActivity(i2);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

