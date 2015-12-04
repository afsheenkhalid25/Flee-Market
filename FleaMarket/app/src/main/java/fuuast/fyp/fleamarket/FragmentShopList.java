package fuuast.fyp.fleamarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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

    private String market_id,category1,category2,category3,categories,search_text,search_type;
    private ArrayList shop_id,shop_name,shop_category,st_shop_name,st_shop_category,category_names;
    private ArrayList<ShopDataModel> dataModelList;
    private ProgressDialog progressDialog;
    private ImageView img_next,img_back,img_search;
    private TextView tv_type,tv_status;
    private EditText et_search;
    private ListView shops_list;
    private Firebase firebase;

    private ShopDataModel shopDataModel = new ShopDataModel();
    private MarketDataModelSingleTon marketDataModelSingleTon = MarketDataModelSingleTon.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Firebase.setAndroidContext(getActivity());
        firebase=new Firebase("https://flee-market.firebaseio.com/");

        shop_id = new ArrayList();
        shop_name = new ArrayList();
        shop_category = new ArrayList();
        category_names = new ArrayList();
        st_shop_name = new ArrayList();
        st_shop_category = new ArrayList();
        dataModelList = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("\tProcessing...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        market_id = marketDataModelSingleTon.getMarket_id();

        View view =  inflater.inflate(R.layout.fragment_shop_list, container, false);
        tv_type = (TextView)view.findViewById(R.id.txt_type);
        tv_status = (TextView)view.findViewById(R.id.tv_status);
        tv_status.setVisibility(View.VISIBLE);
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
                progressDialog.show();
                searchItem(search_text, search_type);
            }
        });
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
        shop_id.clear();
        shop_name.clear();
        shop_category.clear();
        firebase.child("Market_Shops").child(market_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    shop_id.add(d.getKey());
                    shopDataModel = d.getValue(ShopDataModel.class);
                    dataModelList.add(shopDataModel);
                    shop_name.add(shopDataModel.getName());
                    category1 = shopDataModel.getCategory1();
                    category2 = shopDataModel.getCategory2();
                    category3 = shopDataModel.getCategory3();
                    if(category2.equals("-")){
                        categories = category1;
                    } else if(category3.equals("-")){
                        categories = category1 + ", " + category2;
                    } else {
                        categories = category1 + ", " + category2 + ", " + category3;
                    }
                    shop_category.add(categories);
                    shops_list.setAdapter(new CustomAdapter_ShopsList(getActivity(),shop_name,shop_category));
                    tv_status.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void searchItem(String text,String type){
        Boolean Check = true;
        shops_list.setAdapter(null);
        for(int i=0;i<dataModelList.size();i++) {
            if (type.equals("Name")) {
                String name = dataModelList.get(i).getName();
                if(name.equals(text)){
                    Log.d("Position", "In search by name");
                    Check = false;
                    st_shop_name.add(dataModelList.get(i).getName());
                    st_shop_category.add(shop_category.get(i).toString());
                    shops_list.setAdapter(new CustomAdapter_ShopsList(getActivity(),st_shop_name,st_shop_category));
                    progressDialog.dismiss();
                    et_search.setEnabled(true);
                    tv_status.setVisibility(View.INVISIBLE);
                }
            } else {
                category_names.add(dataModelList.get(i).getCategory1());
                category_names.add(dataModelList.get(i).getCategory2());
                category_names.add(dataModelList.get(i).getCategory3());
                for(int j=0;j<category_names.size();j++){
                    if(category_names.get(j).equals(text)){
                        Log.d("Position", "In search by category");
                        Check = false;
                        st_shop_name.add(dataModelList.get(i).getName());
                        st_shop_category.add(shop_category.get(i).toString());
                        shops_list.setAdapter(new CustomAdapter_ShopsList(getActivity(),st_shop_name,st_shop_category));
                        progressDialog.dismiss();
                        et_search.setEnabled(true);
                        tv_status.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        if(Check){
            Log.d("Position", "no result found");
            progressDialog.dismiss();
            et_search.setEnabled(true);
            tv_status.setVisibility(View.VISIBLE);
        }
    }
}
