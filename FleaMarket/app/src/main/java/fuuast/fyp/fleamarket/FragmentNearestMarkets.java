package fuuast.fyp.fleamarket;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentNearestMarkets extends Fragment {

    private ArrayList market_names,market_area,market_id;
    private ArrayList<MarketDataModel> dataModelList;
    private Firebase firebase;
    private ListView market_list;
    private LocationManager locationManager;
    private Location location;
    private double cr_lat=25.3667,cr_lon=68.3667;

    private MarketDataModel marketDataModel = new MarketDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Firebase.setAndroidContext(getActivity());
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        //getLocation();

        dataModelList = new ArrayList<>();
        market_names = new ArrayList();
        market_area = new ArrayList();
        market_id = new ArrayList();

        View view =  inflater.inflate(R.layout.fragment_nearest_markets, container, false);
        market_list = (ListView) view.findViewById(R.id.market_list);
        market_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setMarketDataModelSingleTon(position);
                Intent i = new Intent(getActivity(),MarketView.class);
                startActivity(i);
            }
        });
        setDetails();
        return view;
    }

    public void setDetails(){
        firebase.child("Markets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    marketDataModel = d.getValue(MarketDataModel.class);
                    if(checkDistance(marketDataModel.getLatitude(), marketDataModel.getLongitude())) {
                        market_id.add(d.getKey().toString());
                        market_names.add(marketDataModel.getName());
                        market_area.add(marketDataModel.getAddress());
                        dataModelList.add(marketDataModel);
                        market_list.setAdapter(new CustomAdapter_MarketsList(getActivity(), market_names, market_area));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public Boolean checkDistance(String lat,String lon){
        Location CL = new Location("Current Location");
        CL.setLatitude(cr_lat);
        CL.setLongitude(cr_lon);
        Location ML = new Location("Market Location");
        ML.setLatitude(Double.parseDouble(lat));
        ML.setLongitude(Double.parseDouble(lon));
        double distance = CL.distanceTo(ML);
        double dist = distance/1000;
        if(dist<200)
            return true;
        else
            return false;
    }

    public void getLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            cr_lat = location.getLatitude();
            cr_lon = location.getLongitude();
        }else{
            Toast.makeText(getActivity(), "Network is not Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    public void setMarketDataModelSingleTon(int i){
        marketDataModelSingleTon.setMarket_id(market_id.get(i).toString());
        marketDataModelSingleTon.setAdmin_id(dataModelList.get(i).getAdminID());
        marketDataModelSingleTon.setMarket_address(dataModelList.get(i).getAddress());
        marketDataModelSingleTon.setMarket_lat(dataModelList.get(i).getLatitude());
        marketDataModelSingleTon.setMarket_lon(dataModelList.get(i).getLongitude());
        marketDataModelSingleTon.setMarket_name(dataModelList.get(i).getName());
    }
}
