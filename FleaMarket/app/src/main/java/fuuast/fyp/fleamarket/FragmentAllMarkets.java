package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private ArrayList market_names,market_area,market_day,market_id;
    private ArrayList<MarketDataModel> dataModelList;
    private Firebase firebase;
    private ListView market_list;

    private MarketDataModel marketDataModel = new MarketDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Firebase.setAndroidContext(getActivity());
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        dataModelList = new ArrayList<>();
        market_names = new ArrayList();
        market_area = new ArrayList();
        market_day = new ArrayList();
        market_id = new ArrayList();

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
        market_id.clear();
        market_names.clear();
        market_area.clear();
        market_day.clear();
        firebase.child("Markets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    market_id.add(d.getKey().toString());
                    marketDataModel = d.getValue(MarketDataModel.class);
                    dataModelList.add(marketDataModel);
                    market_names.add(marketDataModel.getName());
                    market_area.add(marketDataModel.getAddress());
                    market_day.add(marketDataModel.getDay());
                    market_list.setAdapter(new CustomAdapter_MarketsList(getActivity(),market_names,market_area,market_day));
                }
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
        marketDataModelSingleTon.setDay(dataModelList.get(i).getDay());
    }
}
