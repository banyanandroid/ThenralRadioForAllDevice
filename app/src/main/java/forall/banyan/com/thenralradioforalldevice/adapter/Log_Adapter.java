package forall.banyan.com.thenralradioforalldevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import forall.banyan.com.thenralradioforalldevice.MainActivity;
import forall.banyan.com.thenralradioforalldevice.R;

/**
 * Created by Jothiprabhakar on 6/21/2016.
 */

public class Log_Adapter extends BaseAdapter implements View.OnClickListener {
    
    private Context activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    
    public Log_Adapter(Context context, ArrayList<HashMap<String, String>> d) {
        activity = context;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item_schedule, null);

        //final TextView m_id = (TextView)vi.findViewById(R.id.m_id); 
        TextView name = (TextView)vi.findViewById(R.id.item_list_schedule_pgm);
        TextView date = (TextView)vi.findViewById(R.id.item_list_schedule_time);
        
      //  ImageView team2 = (ImageView)vi.findViewById(R.id.img_match_team2);
        
        HashMap<String, String> match = new HashMap<String, String>();
        match = data.get(position);
        // Setting all values in listview  
        //m_id.setText(match.get(MatchFragment.TAG_M_ID));
        name.setText(match.get(MainActivity.TAG_PGM_NAME));
        date.setText(match.get(MainActivity.TAG_PGM_TIME));
       
        return vi;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }
    
}
