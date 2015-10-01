package fuuast.fyp.fleamarket;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        tv_names=(TextView) v.findViewById(R.id.textView1);
        tv_area=(TextView) v.findViewById(R.id.textView2);

        tv_names.setText(mrkt_names.get(position).toString());
        tv_area.setText(mrkt_area.get(position).toString());

        img = (ImageView) v.findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("DATA.....","imageview");
                Toast.makeText(context, "Show Market Activity", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
