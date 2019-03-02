package hackathon.device.alexa.amazon.com.locationbasedupsell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DealItemArrayAdapter extends ArrayAdapter<Deal> {

    public DealItemArrayAdapter(@NonNull Context context, ArrayList<Deal> deals) {
        super(context, 0, deals);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Deal deal = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.deals_listview, parent, false);
        }
        // Lookup view for data population
        TextView dealTitle = (TextView) convertView.findViewById(R.id.dealTitle);
        TextView dealSubTitle = (TextView) convertView.findViewById(R.id.dealSubTitle);

        // Populate the data into the template view using the data object
        dealTitle.setText(deal.getDescription());
        dealSubTitle.setText("From " + deal.getProvider());

        // Return the completed view to render on screen
        return convertView;
    }
}
