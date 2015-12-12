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
    private SlidingDrawer sd_chngOthr;

    private Button b1,b2;

    private EditText et_name,et_oldpass,et_crpass,et_repass,et_phn,et_add,et_nic,et_On,et_Oc,et_Ot;

    private String user_id,email_id,name,phone,nic,address,org_name,org_cntct,org_type,user_type;

    private ProgressDialog progressDialog;

    private UserDataModel userDataModel;
    private UserDataModelSingleTon userDataModelSingleTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://flee-market.firebaseio.com/");

        userDataModel = new UserDataModel();
        userDataModelSingleTon = UserDataModelSingleTon.getInstance();

        et_name = (EditText) findViewById(R.id.et_name);
        et_phn = (EditText) findViewById(R.id.ca_et_phone);
        et_add = (EditText) findViewById(R.id.ca_et_address);
        et_nic = (EditText) findViewById(R.id.ca_et_nic);
        et_On = (EditText) findViewById(R.id.ca_et_org_name);
        et_Oc = (EditText) findViewById(R.id.ca_et_org_cntct);
        et_Ot = (EditText) findViewById(R.id.ca_et_org_type);
        et_crpass = (EditText) findViewById(R.id.et_newpass);
        et_repass = (EditText) findViewById(R.id.et_retypPass);
        et_oldpass = (EditText) findViewById(R.id.et_oldpass);

        b1=(Button) findViewById(R.id.ep_btn_chgpass);
        b2= (Button) findViewById(R.id.ep_btn_updateinfo);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });

        img_sd = (ImageView) findViewById(R.id.img_sdButton);

        progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setMessage("\tUpdating Profile...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        getData();
        showData();
        setSlidingDrawers();
    }

    private void setSlidingDrawers() {
        sd_chngOthr = (SlidingDrawer) findViewById(R.id.sd_chngOthr);
        sd_chngOthr.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                img_sd.setImageResource(R.drawable.ic_action_collapse);
                sd_chngOthr.getLayoutParams().height = 950;
            }
        });

        sd_chngOthr.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                img_sd.setImageResource(R.drawable.ic_action_expand);
                sd_chngOthr.getLayoutParams().height = 30;
            }
        });
    }

    private void getData(){
        user_id=userDataModelSingleTon.getId();
        email_id=userDataModelSingleTon.getEmail_id();
        name=userDataModelSingleTon.getName();
        phone=userDataModelSingleTon.getPhone();
        address=userDataModelSingleTon.getAddress();
        nic=userDataModelSingleTon.getNic();
        org_name=userDataModelSingleTon.getOrg_name();
        org_cntct=userDataModelSingleTon.getOrg_contact();
        org_type=userDataModelSingleTon.getOrg_typ();
        user_type=userDataModelSingleTon.getType();
    }

    private void showData(){
        et_name.setText(name);
        et_phn.setText(phone);
        et_add.setText(address);
        et_nic.setText(nic);
        et_On.setText(org_name);
        et_Oc.setText(org_cntct);
        et_Ot.setText(org_type);

        if(user_type.equals("Shopkeeper")){
            et_On.setEnabled(false);
            et_Oc.setEnabled(false);
            et_Ot.setEnabled(false);
        }

        ((TextView) findViewById(R.id.ep_tv_email)).setText(email_id);
    }

    private void updatePassword(){
            if (!et_crpass.getText().toString().equals("")&&!et_repass.getText().toString().equals("")&&et_crpass.getText().toString().equals(et_repass.getText().toString())){
                new AlertDialog.Builder(EditProfile.this)
                        .setTitle("Update Password")
                        .setMessage("Are you sure you want to update password?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                firebase.changePassword(email_id,et_oldpass.getText().toString(),et_crpass.getText().toString(), new Firebase.ResultHandler() {
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
                                        et_oldpass.setText("");
                                        et_crpass.setText("");
                                        et_repass.setText("");
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                et_oldpass.setText("");
                                et_crpass.setText("");
                                et_repass.setText("");
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
        org_cntct = et_Oc.getText().toString();
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
        userDataModel.setOrg_contact(org_cntct);
        userDataModel.setOrg_typ(org_type);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

