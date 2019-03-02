package hackathon.device.alexa.amazon.com.locationbasedupsell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DealItemArrayAdapter extends ArrayAdapter<DealModel> {

    public DealItemArrayAdapter(@NonNull Context context, ArrayList<DealModel> deals) {
        super(context, 0, deals);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DealModel deal = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.deals_listview, parent, false);
        }
        // Lookup view for data population
        TextView dealText = (TextView) convertView.findViewById(R.id.dealText);

        // Populate the data into the template view using the data object
        dealText.setText(deal.dealText);

        // Return the completed view to render on screen
        return convertView;
    }
}
