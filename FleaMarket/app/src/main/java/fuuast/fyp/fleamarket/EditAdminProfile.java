package fuuast.fyp.fleamarket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class EditAdminProfile extends ActionBarActivity{

    private Firebase firebase;
    private TextView tv_email,tv_others;
    private ImageView img_done,img_sd;
    private RelativeLayout rl_slider;
    private SlidingDrawer sd_chngOthr;
    private EditText et_name,et_crpass,et_repass,et_phn,et_add,et_nic,et_On,et_Oc;
    private String admin_id,email_id,name,password,new_pass=null,re_pass=null,phone,nic,address,org_name,org_cntct;
    private AlertDialog alertD;
    private AlertDialog.Builder alertDialogBuilder;
    private ProgressDialog progressDialog;

    private UserDataModel userDataModel = new UserDataModel();
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin_profile);

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://flee-market.firebaseio.com/");

        et_name = (EditText) findViewById(R.id.et_name);
        et_phn = (EditText) findViewById(R.id.ca_et_phone);
        et_add = (EditText) findViewById(R.id.ca_et_address);
        et_nic = (EditText) findViewById(R.id.ca_et_nic);
        et_On = (EditText) findViewById(R.id.ca_et_org_name);
        et_Oc = (EditText) findViewById(R.id.ca_et_org_cntct);
        et_crpass = (EditText) findViewById(R.id.et_newpass);
        et_repass = (EditText) findViewById(R.id.et_retypPass);

        img_sd = (ImageView) findViewById(R.id.img_sdButton);
        img_done = (ImageView) findViewById(R.id.img_done);
        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EDIT PROFILE", "Button clicked");
                alertD.show();
            }
        });

        tv_email = (TextView) findViewById(R.id.ep_tv_email);
        tv_others = (TextView) findViewById(R.id.ep_tv_others);
        rl_slider = (RelativeLayout) findViewById(R.id.rl_chngOthr);

        progressDialog = new ProgressDialog(EditAdminProfile.this);

        setPopup();
        setSlidingDrawers();
        showData();
    }

    public void showData() {
        tv_email.setText(userDataModelSingleTon.getEmail_id().toString());
        admin_id = userDataModelSingleTon.getId().toString();
        password = userDataModelSingleTon.getPassword();
        et_phn.setText(userDataModelSingleTon.getPhone().toString(),TextView.BufferType.EDITABLE);
        et_add.setText(userDataModelSingleTon.getAddress().toString(),TextView.BufferType.EDITABLE);
        et_nic.setText(userDataModelSingleTon.getNic().toString(),TextView.BufferType.EDITABLE);
        et_On.setText(userDataModelSingleTon.getOrg_name().toString(),TextView.BufferType.EDITABLE);
        et_Oc.setText(userDataModelSingleTon.getOrg_cntct().toString(),TextView.BufferType.EDITABLE);
        et_name.setText(userDataModelSingleTon.getName().toString(),TextView.BufferType.EDITABLE);
    }

    private void setSlidingDrawers() {
        sd_chngOthr = (SlidingDrawer) findViewById(R.id.sd_chngOthr);
        sd_chngOthr.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                img_sd.setImageResource(R.drawable.ic_action_collapse);
                img_sd.setPadding(600,0,0,0);
                tv_others.setText("Done");
                sd_chngOthr.getLayoutParams().height = 850;

            }
        });

        sd_chngOthr.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                img_sd.setImageResource(R.drawable.ic_action_expand);
                img_sd.setPadding(600,0,0,0);
                tv_others.setText("Change Others");
                sd_chngOthr.getLayoutParams().height = 90;
            }
        });
    }

    public void setPopup() {
        LayoutInflater layoutInflater = LayoutInflater.from(EditAdminProfile.this);
        View v = layoutInflater.inflate(R.layout.password_popup, null);

        alertDialogBuilder = new AlertDialog.Builder(EditAdminProfile.this);
        alertDialogBuilder.setView(v);

        final EditText input = (EditText) v.findViewById(R.id.userInput);

        // setup a dialog window
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String dl_pass = input.getText().toString();
                    if (dl_pass.equals(password)) {
                        dataUpdate();
                    }else
                        Toast.makeText(EditAdminProfile.this, "Insert your current password correctly", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        //create a dialog box
        alertD = alertDialogBuilder.create();
    }

    public void dataUpdate() {
        email_id = tv_email.getText().toString();
        name = et_name.getText().toString();
        phone = et_phn.getText().toString();
        address = et_add.getText().toString();
        nic = et_nic.getText().toString();
        org_name = et_On.getText().toString();
        org_cntct = et_Oc.getText().toString();
        new_pass=et_crpass.getText().toString();
        re_pass=et_repass.getText().toString();

        if (name.equals("")||phone.equals("")||address.equals("")||nic.equals("")||org_name.equals("")||org_cntct.equals(""))
            Toast.makeText(EditAdminProfile.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        else
        {
            progressDialog.setMessage("\tUpdating Profile...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            if (new_pass.equals("")&&re_pass.equals("")) {
                progressDialog.show();
                userDataModel.setPassword(password);
                setData();
                firebase.child("Users").child(admin_id).setValue(userDataModel, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        progressDialog.dismiss();
                        if (firebaseError == null) {
                            Log.d("Position........", "on complete ");
                            setDataModel();
                            Toast.makeText(EditAdminProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Position........", "in data saving error");
                            Toast.makeText(EditAdminProfile.this, "Error!! Try to update later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else  if (new_pass.equals(re_pass)) {
                progressDialog.show();
                firebase.changePassword(email_id,password,new_pass, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        userDataModel.setPassword(new_pass);
                        setData();
                        firebase.child("Users").child(admin_id).setValue(userDataModel, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                progressDialog.dismiss();
                                if (firebaseError == null) {
                                    Log.d("Position........", "on complete ");
                                    setDataModel();
                                    Toast.makeText(EditAdminProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("Position........", "in data saving error");
                                    Toast.makeText(EditAdminProfile.this, "Error!! Try to update later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Toast.makeText(EditAdminProfile.this, "Error!! Password is not reset. Try Again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }else
                Toast.makeText(EditAdminProfile.this, "Password must be reset correctly", Toast.LENGTH_SHORT).show();
        }
    }


    public void setData() {
        userDataModel.setEmail_id(email_id);
        userDataModel.setType("Admin");
        userDataModel.setName(name);
        userDataModel.setPhone(phone);
        userDataModel.setAddress(address);
        userDataModel.setNic(nic);
        userDataModel.setOrg_name(org_name);
        userDataModel.setOrg_cntct(org_cntct);
        userDataModel.setOrg_typ(userDataModelSingleTon.getOrg_typ().toString());
    }

    public void setDataModel() {
        userDataModelSingleTon.setName(userDataModel.getName());
        userDataModelSingleTon.setPassword(userDataModel.getPassword());
        userDataModelSingleTon.setPhone(userDataModel.getPhone());
        userDataModelSingleTon.setAddress(userDataModel.getAddress());
        userDataModelSingleTon.setNic(userDataModel.getNic());
        userDataModelSingleTon.setOrg_name(userDataModel.getOrg_name());
        userDataModelSingleTon.setOrg_cntct(userDataModel.getOrg_cntct());
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

