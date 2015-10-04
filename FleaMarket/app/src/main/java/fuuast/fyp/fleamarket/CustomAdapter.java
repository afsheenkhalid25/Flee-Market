package fuuast.fyp.fleamarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter{

    Context context;
    ArrayList mrkt_names,mrkt_area,mrkt_imageUrl;
    TextView marketName,marketArea;
    ImageView img;


    public CustomAdapter(Context context, ArrayList names, ArrayList area, ArrayList imageUrl) {
        this.context = context;
        this.mrkt_names = names;
        this.mrkt_area = area;
        this.mrkt_imageUrl = imageUrl;
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
                Toast.makeText(context, "this shows market details", Toast.LENGTH_SHORT).show();
            }
        });

        marketName.setText(mrkt_names.get(position).toString());
        marketArea.setText(mrkt_area.get(position).toString());

        return v;
    }
}
