package fuuast.fyp.fleamarket;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class FragmentMapView extends Fragment {

    private String market_id,search_text,search_type;
    private ArrayList shop_id,category_names;
    private ArrayList<ShopDataModel> dataModelList,selected_shopList;
    private ImageView img_next,img_back,img_search;
    private TextView tv_type;
    private EditText et_search;
    private Firebase firebase;
    private Boolean Check = true;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Firebase.setAndroidContext(getActivity());
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        shop_id = new ArrayList();
        category_names = new ArrayList();
        dataModelList = new ArrayList<>();
        selected_shopList = new ArrayList<>();

        market_id = marketDataModelSingleTon.getMarket_id();

        View view =  inflater.inflate(R.layout.fragment_map_view, container, false);
        tv_type = (TextView)view.findViewById(R.id.txt_type);
        et_search = (EditText)view.findViewById(R.id.et_search);
        img_next = (ImageView)view.findViewById(R.id.next_item);
        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_next.setEnabled(false);
                img_next.setImageResource(R.drawable.ic_action_next_item);
                img_back.setEnabled(true);
                img_back.setImageResource(R.drawable.ic_action_previous_item_dark);
                tv_type.setText("Category");
            }
        });
        img_back = (ImageView)view.findViewById(R.id.back_item);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_back.setEnabled(false);
                img_back.setImageResource(R.drawable.ic_action_previous_item);
                img_next.setEnabled(true);
                img_next.setImageResource(R.drawable.ic_action_next_item_dark);
                tv_type.setText("Name");
            }
        });
        img_search = (ImageView)view.findViewById(R.id.search);
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_text = et_search.getText().toString();
                search_type = tv_type.getText().toString();
                et_search.setEnabled(false);
                searchItem(search_text, search_type);
            }
        });
        getShopList();
        return view;
    }

    public void getShopList(){
        shop_id.clear();
        dataModelList.clear();
        firebase.child("Market_Shops").child(market_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    shop_id.add(d.getKey());
                    shopDataModel = d.getValue(ShopDataModel.class);
                    dataModelList.add(shopDataModel);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void searchItem(String text,String type){
        selected_shopList.clear();
        if(text.equals("")) {
            Check = false;
            et_search.setEnabled(true);
            //yahan py jb bagair kch dalay search k btn py click ho tw sari shops ajaen by default
        }else {
            for (int i = 0; i < dataModelList.size(); i++) {
                if (type.equals("Name")) {
                    Log.d("Position", "In search by name");
                    String name = dataModelList.get(i).getName();
                    if (name.equals(text)) {
                        Log.d("Position", "Name found");
                        Check = false;
                        et_search.setEnabled(true);
                        selected_shopList.add(dataModelList.get(i));
                        //yahan py jb name wise shop mil jae tw wo map py show karani hai
                    }
                } else {
                    Log.d("Position", "In search by category");
                    category_names.clear();
                    category_names.add(dataModelList.get(i).getCategory1());
                    category_names.add(dataModelList.get(i).getCategory2());
                    category_names.add(dataModelList.get(i).getCategory3());
                    for (int j = 0; j < category_names.size(); j++) {
                        if (category_names.get(j).equals(text)) {
                            Log.d("Position", "category found ");
                            Check = false;
                            et_search.setEnabled(true);
                            selected_shopList.add(dataModelList.get(i));
                            //yahan py jb category wise shop mil jae tw wo map py show karani hai.
                            break;
                        }
                    }
                }
            }
        }
        if(Check){
            Log.d("Position", "no result found");
            et_search.setEnabled(true);
            //yahan py jb enter kiye hue text k hsb sy koi b shop na hw tw map blank show karana hai
        }
    }
}
