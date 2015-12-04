package fuuast.fyp.fleamarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentShopList extends Fragment {

    private String market_id,category1,category2,category3,categories;
    private ArrayList user_id,shop_id,shop_name,shop_category;
    private TextView tv_market_name,tv_market_address,shops_status;
    private ListView shops_list;
    private Firebase firebase;

    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Firebase.setAndroidContext(getActivity());
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        shop_id = new ArrayList();
        user_id = new ArrayList();
        shop_name = new ArrayList();
        shop_category = new ArrayList();

        market_id = marketDataModelSingleTon.getMarket_id();

        View view =  inflater.inflate(R.layout.fragment_shop_list, container, false);
        shops_list = (ListView) view.findViewById(R.id.shop_list);
        shops_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(),ShopDetails.class);
                startActivity(i);
            }
        });
        getShopList();
        return view;
    }

    public void getShopList(){
        user_id.clear();
        shop_id.clear();
        shop_name.clear();
        shop_category.clear();
        firebase.child("Market_Shops").child(market_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    shop_id.add(d.getKey());
                    user_id.add(((HashMap<String,String>)d.getValue()).get("user_id"));
                    shop_name.add(((HashMap<String,String>)d.getValue()).get("name"));
                    category1 = ((HashMap<String,String>)d.getValue()).get("category1");
                    category2 = ((HashMap<String,String>)d.getValue()).get("category2");
                    category3 = ((HashMap<String,String>)d.getValue()).get("category3");
                    if(category2.equals("-")){
                        categories = category1;
                    } else if(category3.equals("-")){
                        categories = category1 + ", " + category2;
                    } else {
                        categories = category1 + ", " + category2 + ", " + category3;
                    }
                    shop_category.add(categories);
                    shops_list.setAdapter(new CustomAdapter_ShopsList(getActivity(),shop_name,shop_category));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
