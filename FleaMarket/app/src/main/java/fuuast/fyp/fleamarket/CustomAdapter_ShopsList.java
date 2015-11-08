package fuuast.fyp.fleamarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter_ShopsList extends BaseAdapter{

    Context context;
    ArrayList shop_names,shop_market;
    TextView shopName,shopMarket;

    public CustomAdapter_ShopsList(Context context, ArrayList names, ArrayList shop) {
        this.context = context;
        this.shop_names = names;
        this.shop_market = shop;
    }

    @Override
    public int getCount() {
        return shop_market.size();
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.shopslist_listview,null);

        shopName = (TextView) v.findViewById(R.id.shoppanel_tv_shopname);
        shopMarket = (TextView) v.findViewById(R.id.shoppanel_tv_shopmarket);

        shopName.setText(shop_names.get(position).toString());
        shopMarket.setText(shop_market.get(position).toString());

        return v;
    }
}
