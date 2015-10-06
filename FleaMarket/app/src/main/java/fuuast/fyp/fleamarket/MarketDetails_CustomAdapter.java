package fuuast.fyp.fleamarket;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MarketDetails_CustomAdapter extends BaseAdapter
{
    Context context;
    ArrayList shop_Name,shop_Owner,shop_Category,shop_imageUrl;
    TextView shopName,shopOwner,shopCategory;

    public MarketDetails_CustomAdapter(Context context, ArrayList names, ArrayList owner, ArrayList category, ArrayList imageUrl) {
        this.context = context;
        this.shop_Name = names;
        this.shop_Owner = owner;
        this.shop_Category = category;
        this.shop_imageUrl = imageUrl;
    }

    @Override
    public int getCount() {
        return shop_Name.size();
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
        View v = inflater.inflate(R.layout.marketdetails_listview,null);

        shopName = (TextView) v.findViewById(R.id.marketdetails_tv_shopname);
        shopOwner = (TextView) v.findViewById(R.id.marketdetails_tv_shopowner);
        shopCategory = (TextView) v.findViewById(R.id.marketdetails_tv_shopcategory);

        shopName.setText(shop_Name.get(position).toString());
        shopOwner.setText(shop_Owner.get(position).toString());
        shopCategory.setText(shop_Category.get(position).toString());

        return v;
    }
}
