package fuuast.fyp.fleamarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter_MarketsList extends BaseAdapter {

    Context context;
    ArrayList market_names,market_area,request;
    TextView marketName,marketArea,marketRequest;
    ImageView img;

    public CustomAdapter_MarketsList(Context context, ArrayList names, ArrayList area, ArrayList request) {
        this.context = context;
        this.market_names = names;
        this.market_area = area;
        this.request = request;
    }

    @Override
    public int getCount() {
        return market_names.size();
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
        View v = inflater.inflate(R.layout.layout_market_list,null);

        marketName = (TextView) v.findViewById(R.id.tv_name);
        marketArea = (TextView) v.findViewById(R.id.tv_area);
        marketRequest = (TextView) v.findViewById(R.id.no_req);

        marketName.setText(market_names.get(position).toString());
        marketArea.setText(market_area.get(position).toString());

        img = (ImageView)v.findViewById(R.id.img_next);

        if(request==null) {
            marketRequest.setVisibility(View.INVISIBLE);
            img.setVisibility(View.INVISIBLE);
        } else
            marketRequest.setText(request.get(position).toString());

        return v;
    }
}
