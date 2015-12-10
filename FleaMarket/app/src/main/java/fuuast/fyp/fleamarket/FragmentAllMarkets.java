package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


public class FragmentAllMarkets extends Fragment {

    private ArrayList market_names,market_area,market_id,distance,sorted_market_names,sorted_market_area,sorted_market_id;
    private ArrayList<MarketDataModel> dataModelList;
    private Double[] distanceArray;
    private String[] market_names_array,market_area_array,market_id_array;
    private MarketDataModel[] datamodelArray;
    private Firebase firebase;
    private ListView market_list;
    Location currentLocation,marketLocation;

    private MarketDataModel marketDataModel = new MarketDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Firebase.setAndroidContext(getActivity());
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        dataModelList = new ArrayList<MarketDataModel>();
        market_names = new ArrayList();
        market_area = new ArrayList();
        market_id = new ArrayList();
        distance=new ArrayList();
        sorted_market_names = new ArrayList();
        sorted_market_area = new ArrayList();
        sorted_market_id = new ArrayList();

        currentLocation=new Location("myLocation");
        currentLocation.setLatitude(24.848040);
        currentLocation.setLongitude(67.169034);

        View view =  inflater.inflate(R.layout.fragment_all_markets, container, false);
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

                dataModelList.clear();
                market_id.clear();
                market_names.clear();
                market_area.clear();
                distance.clear();

                for(DataSnapshot d:dataSnapshot.getChildren()){

                    marketDataModel = d.getValue(MarketDataModel.class);

                    dataModelList.add(marketDataModel);
                    market_id.add(d.getKey().toString());
                    market_names.add(marketDataModel.getName());
                    market_area.add(marketDataModel.getAddress());

                    marketLocation=new Location("marketLocation");
                    marketLocation.setLatitude(Double.parseDouble(marketDataModel.getLatitude()));
                    marketLocation.setLongitude(Double.parseDouble(marketDataModel.getLongitude()));

                    Log.d("MARKET",marketDataModel.getName());
                    Log.d("DISTANCE",(currentLocation.distanceTo(marketLocation))+"");
                    distance.add(currentLocation.distanceTo(marketLocation));
                }

                distanceArray=new Double[dataModelList.size()];
                market_id_array=new String[dataModelList.size()];
                market_names_array=new String[dataModelList.size()];
                market_area_array=new String[dataModelList.size()];
                datamodelArray=new MarketDataModel[dataModelList.size()];

                for(int i=0;i<dataModelList.size();i++){
                    distanceArray[i]=Double.parseDouble(distance.get(i).toString());
                    market_id_array[i]=market_id.get(i).toString();
                    market_names_array[i]=market_names.get(i).toString();
                    market_area_array[i]=market_area.get(i).toString();
                    datamodelArray[i]=dataModelList.get(i);
                }

                int i,j;
                for(i=0;i<distanceArray.length;i++){
                    Double temp=distanceArray[i];
                    int position=i;
                    for (j=i+1;j<distanceArray.length;j++){
                        if (distanceArray[j]<temp){
                            temp=distanceArray[j];
                            position=j;
                        }
                    }
                    if (position!=i){
                        Double tempDist;
                        String tempId,tempName,tempArea;
                        MarketDataModel tempDatamodel;

                        tempDist=distanceArray[i];
                        tempId=market_id_array[i];
                        tempName=market_names_array[i];
                        tempArea=market_area_array[i];
                        tempDatamodel=datamodelArray[i];

                        distanceArray[i]=distanceArray[position];
                        market_id_array[i]=market_id_array[position];
                        market_names_array[i]=market_names_array[position];
                        market_area_array[i]=market_area_array[position];
                        datamodelArray[i]=datamodelArray[position];

                        distanceArray[position]=tempDist;
                        market_id_array[position]=tempId;
                        market_names_array[position]=tempName;
                        market_area_array[position]=tempArea;
                        datamodelArray[position]=tempDatamodel;
                    }
                }

                market_id.clear();
                market_names.clear();
                market_area.clear();
                distance.clear();
                dataModelList.clear();

                for (int k=0;k<datamodelArray.length;k++){
                    dataModelList.add(datamodelArray[k]);
                    market_id.add(market_id_array[k]);
                    market_names.add(market_names_array[k]);
                    market_area.add(market_area_array[k]);
                    distance.add(distanceArray[k]);
                }
                market_list.setAdapter(new CustomAdapter_MarketsList(getActivity(),market_names,market_area,null));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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
