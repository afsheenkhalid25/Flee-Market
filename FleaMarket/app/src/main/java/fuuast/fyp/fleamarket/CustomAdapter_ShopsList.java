package fuuast.fyp.fleamarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter_ShopsList extends BaseAdapter {

    Context context;
    ArrayList shop_names,shop_categories,market_names;
    TextView shop_name,shop_category,market_name;

    public CustomAdapter_ShopsList(Context context, ArrayList names, ArrayList categories, ArrayList market_name) {
        this.context = context;
        this.shop_names = names;
        this.shop_categories = categories;
        this.market_names = market_name;
    }

    @Override
    public int getCount() {
        return shop_names.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_shop_list,null);

        shop_name = (TextView) v.findViewById(R.id.shop_name);
        shop_name.setText(shop_names.get(position).toString());

        shop_category = (TextView) v.findViewById(R.id.shop_category);
        shop_category.setText(shop_categories.get(position).toString());

        market_name = (TextView) v.findViewById(R.id.mt_name);
        if(market_names == null)
            market_name.setVisibility(View.INVISIBLE);
        else
            market_name.setText(market_names.get(position).toString());

        return v;
    }
}
