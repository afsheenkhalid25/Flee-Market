package fuuast.fyp.fleamarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter{

    Context context;
    ArrayList mrkt_names,mrkt_area;
    TextView tv_names,tv_area;
    ImageView img;

    public CustomAdapter(Context context, ArrayList names, ArrayList area) {
        this.context = context;
        this.mrkt_names = names;
        this.mrkt_area = area;
    }

    @Override
    public int getCount() {
        return mrkt_area.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.adminpanel_listview,null);
        return v;
    }
}
