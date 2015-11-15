package fuuast.fyp.fleamarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter_RequestList extends BaseAdapter {

    Context context;
    ArrayList shop_names,shop_categories;
    TextView shop_name,shop_category;

    public CustomAdapter_RequestList(Context context, ArrayList names, ArrayList email) {
        this.context = context;
        this.shop_names = names;
        this.shop_categories = email;
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
        View v = inflater.inflate(R.layout.shoprequest_listview,null);

        shop_name = (TextView) v.findViewById(R.id.shop_name);
        shop_name.setText(shop_names.get(position).toString());

        shop_category = (TextView) v.findViewById(R.id.shop_category);
        shop_category.setText(shop_categories.get(position).toString());

        return v;
    }
}
