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
    ArrayList shop_names,shopkeeper_emails;
    TextView shop_name,shopkeeper_email;

    public CustomAdapter_RequestList(Context context, ArrayList names, ArrayList email) {
        this.context = context;
        this.shop_names = names;
        this.shopkeeper_emails = email;
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
        View v = inflater.inflate(R.layout.category_listview,null);

        shop_name = (TextView) v.findViewById(R.id.tv_name);
        shop_name.setText(shop_names.get(position).toString());

        shopkeeper_email = (TextView) v.findViewById(R.id.tv_name);
        shopkeeper_email.setText(shopkeeper_emails.get(position).toString());

        return v;
    }
}
