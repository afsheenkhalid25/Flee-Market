package fuuast.fyp.fleamarket;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class CreateMarket extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapClickListener{

    private String lat=null,lon=null,address=null,name=null;
    private EditText et_name;
    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_market);

        et_name= (EditText) findViewById(R.id.createmarket_et_marketname);
        create= (Button) findViewById(R.id.createmarket_btn_create);
        create.setOnClickListener(this);

        et_name.setEnabled(false);
        create.setEnabled(false);

        Toast.makeText(this,"Please Wait For Map",Toast.LENGTH_LONG).show();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.createmarket_map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP......", "Ready");
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(this);
        et_name.setEnabled(true);
        create.setEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        lat= ""+latLng.latitude;
        lon= ""+latLng.longitude;
        GetAddress objGetAddress=new GetAddress(CreateMarket.this,Double.parseDouble(lat),Double.parseDouble(lon));
        objGetAddress.get();
        while (objGetAddress.parsingComplete);
        address=objGetAddress.getCity();
        if (address==null)
            address="Unable to get address";
        Log.d("ON MAP CLICK.....","\n"+lat+"\n"+lon+"\n"+address);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.createmarket_btn_create:
                if (lat!=null&&lon!=null){

                }
                break;
        }
    }
}
