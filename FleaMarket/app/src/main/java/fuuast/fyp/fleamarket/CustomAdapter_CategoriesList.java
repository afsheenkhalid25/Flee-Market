package fuuast.fyp.fleamarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter_CategoriesList extends BaseAdapter {

    Context context;
    ArrayList category_id,category_names,category_Urls;
    TextView category_name;
    ImageView category_image;

    public CustomAdapter_CategoriesList(Context context, ArrayList names, ArrayList url) {
        this.context = context;
        this.category_names = names;
        this.category_Urls = url;
    }

    @Override
    public int getCount() {
        return category_names.size();
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

        category_name = (TextView) v.findViewById(R.id.tv_name);
        category_name.setText(category_names.get(position).toString());

        category_image = (ImageView) v.findViewById(R.id.pic);
        //category_image.setImageResource(category_Urls.get(position));

        return v;
    }
}
