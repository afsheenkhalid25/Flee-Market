package fuuast.fyp.fleamarket;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminPanel_CustomAdapter extends BaseAdapter{

    Context context;
    ArrayList market_names,market_area,market_imageUrl;
    TextView marketName,marketArea;
    ImageView img;


    public AdminPanel_CustomAdapter(Context context, ArrayList names, ArrayList area, ArrayList imageUrl) {
        this.context = context;
        this.market_names = names;
        this.market_area = area;
        this.market_imageUrl = imageUrl;
    }

    @Override
    public int getCount() {
        return market_area.size();
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
        View v = inflater.inflate(R.layout.adminpanel_listview,null);

        marketName = (TextView) v.findViewById(R.id.adminpanel_tv_marketname);
        marketArea = (TextView) v.findViewById(R.id.adminpanel_tv_marketarea);

        img = (ImageView) v.findViewById(R.id.adminpanel_img_marketdetails);
        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,MarketDetails.class);
                context.startActivity(i);
            }
        });

        marketName.setText(market_names.get(position).toString());
        marketArea.setText(market_area.get(position).toString());

        return v;
    }
}
