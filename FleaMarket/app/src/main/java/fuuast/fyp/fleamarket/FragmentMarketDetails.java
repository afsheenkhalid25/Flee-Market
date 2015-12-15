package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class FragmentMarketDetails extends Fragment {

    private TextView mt_area,mt_day,mt_shops,name,contact,org;
    private String admin_id,market_id;
    private ImageView imageView;
    private ArrayList<ShopDataModel> dataModelList;
    private Firebase firebase;
    private ShopDataModel shopDataModel = new ShopDataModel();
    private UserDataModel userDataModel = new UserDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_details, container, false);

        Firebase.setAndroidContext(getActivity());
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        imageView= (ImageView) view.findViewById(R.id.fmd_mrkt_btn_img);

        mt_area = (TextView) view.findViewById(R.id.mt_area);
        mt_day = (TextView) view.findViewById(R.id.mt_day);
        mt_shops = (TextView) view.findViewById(R.id.mt_shops);
        name = (TextView) view.findViewById(R.id.name);
        contact = (TextView) view.findViewById(R.id.contact);
        org = (TextView) view.findViewById(R.id.org);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),MarketMapCustomer.class);
                startActivity(i);
            }
        });

        mt_area.setText(marketDataModelSingleTon.getMarket_address());
        mt_day.setText(marketDataModelSingleTon.getDay());

        admin_id = marketDataModelSingleTon.getAdmin_id();
        market_id = marketDataModelSingleTon.getMarket_id();

        dataModelList = new ArrayList<>();

        getAdminDetails();
        return view;
    }

    public void getAdminDetails(){
        firebase.child("Users").child(admin_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDataModel = dataSnapshot.getValue(UserDataModel.class);
                name.setText(userDataModel.getName());
                contact.setText(userDataModel.getPhone());
                org.setText(userDataModel.getOrg_name());
                getTotalShops();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getTotalShops(){
        dataModelList.clear();
        firebase.child("Market_Shops").child(market_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        shopDataModel = d.getValue(ShopDataModel.class);
                        dataModelList.add(shopDataModel);
                    }
                }
                mt_shops.setText(""+dataModelList.size());
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
