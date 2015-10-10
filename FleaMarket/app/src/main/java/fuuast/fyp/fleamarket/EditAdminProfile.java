package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;


public class EditAdminProfile extends ActionBarActivity implements View.OnClickListener{

    private String admin_id,name,password,cur_password,re_password,phone,nic,address,org_name,org_cntct;
    private Firebase fb;
    private EditText et_name,et_pass,et_crpass,et_repass,et_phn,et_add,et_nic,et_On,et_Oc;
    private TextView tv_email;
    private ImageView img_done,img_edit;
    private UserDataModelSingleTon userDataModelSingleTon = UserDataModelSingleTon.getInstance();
    private UserDataModel userDataModel = new UserDataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Firebase.setAndroidContext(this);
        fb = new Firebase("https://flee-market.firebaseio.com/");

        et_name = (EditText) findViewById(R.id.ca_et_name);
        et_crpass = (EditText) findViewById(R.id.et_curpass);
        et_pass = (EditText) findViewById(R.id.ca_et_password);
        et_repass = (EditText) findViewById(R.id.ca_et_repassword);
        et_phn = (EditText) findViewById(R.id.ca_et_phone);
        et_add = (EditText) findViewById(R.id.ca_et_address);
        et_nic = (EditText) findViewById(R.id.ca_et_nic);
        et_On = (EditText) findViewById(R.id.ca_et_org_name);
        et_Oc = (EditText) findViewById(R.id.ca_et_org_cntct);

        img_done = (ImageView) findViewById(R.id.ep_img_done);
        img_edit = (ImageView) findViewById(R.id.ep_img_edit);

        tv_email = (TextView) findViewById(R.id.ep_tv_email);

        showData();

    }

    public void showData()
    {
        tv_email.setText(userDataModelSingleTon.getEmail_id().toString());
        et_name.setText(userDataModelSingleTon.getName().toString());
        et_phn.setText(userDataModelSingleTon.getPhone().toString());
        et_add.setText(userDataModelSingleTon.getAddress().toString());
        et_nic.setText(userDataModelSingleTon.getNic().toString());
        et_On.setText(userDataModelSingleTon.getOrg_name().toString());
        et_Oc.setText(userDataModelSingleTon.getOrg_cntct().toString());
        admin_id = userDataModelSingleTon.getId().toString();
    }

    public void dataUpdate(){

        name = et_name.getText().toString();
        password = et_pass.getText().toString();
        re_password = et_repass.getText().toString();
        cur_password = et_crpass.getText().toString();
        phone = et_phn.getText().toString();
        address = et_add.getText().toString();
        nic = et_nic.getText().toString();
        org_name = et_On.getText().toString();
        org_cntct = et_Oc.getText().toString();

        if (name.equals("")||cur_password.equals("")||password.equals("")||re_password.equals("")||phone.equals("")||address.equals("")||nic.equals("")||org_name.equals("")||org_cntct.equals(""))
            Toast.makeText(EditAdminProfile.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        else{

            userDataModel.setName(name);
            userDataModel.setPassword(password);
            userDataModel.setPhone(phone);
            userDataModel.setAddress(address);
            userDataModel.setNic(nic);
            userDataModel.setOrg_name(org_name);
            userDataModel.setOrg_cntct(org_cntct);
        }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ep_img_done:
                Log.d("EDIT PROFILE","Button clicked");
                dataUpdate();
                break;
            case R.id.ep_img_edit:
                Log.d("EDIT PROFILE","edit Button clicked");
                et_crpass.setEnabled(true);
                et_pass.setEnabled(true);
                et_repass.setEnabled(true);
                break;
        }
    }
}
