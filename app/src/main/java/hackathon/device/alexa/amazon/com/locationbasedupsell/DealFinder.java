package hackathon.device.alexa.amazon.com.locationbasedupsell;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DealFinder {


    private List<JSONObject> dealDatabase;

    public DealFinder() {
        dealDatabase = new ArrayList<>();
        populateDealDatabase();
    }


    List<Deal> findDeals(LatLng latLng, String dealType) {
        List<Deal> deals = new ArrayList<>();
        try {
            for (JSONObject record : dealDatabase) {
                if (dealType.toLowerCase().equals(record.getString("dealType")) && inGeofence(latLng, record)) {
                    List<Deal> geofenceDeals = Deal.fromJson(record.getJSONArray("deals"));
                    deals.addAll(geofenceDeals);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return deals;
    }

    private void populateDealDatabase() {
        JSONObject record = null;
        try {

            //restaurants
            record = new JSONObject("{\"southwest\":{\"latitude\":37.394321,\"longitude\":-122.0310037},\"northeast\":{\"latitude\":37.3956763,\"longitude\":-122.0300917},\"dealType\":\"restaurants\",\"deals\":[{\"location\":{\"latitude\":37.394321,\"longitude\":-122.0310037},\"company\":\"Dish n' Dash\",\"description\":\"736 N Mathilda Ave, Sunnyvale, CA 94085\\n Free salad/fries with lunch!\",\"provider\":\"Yelp\"},{\"location\":{\"latitude\":37.394321,\"longitude\":-122.0310037},\"company\":\"Hobee's\",\"description\":\"800 W Ahwanee Ave, Sunnyvale, CA 94085\\n Free drink with lunch/dinner!\",\"provider\":\"Yelp\"}]}");
            dealDatabase.add(record);
            record = new JSONObject("{\"southwest\":{\"latitude\":37.3880074,\"longitude\":-122.028055},\"northeast\":{\"latitude\":37.3900599,\"longitude\":-122.0231831},\"dealType\":\"restaurants\",\"deals\":[{\"location\":{\"latitude\":37.3879137,\"longitude\":-122.0261667},\"company\":\"Pho Lovers\",\"description\":\"253 E Maude Ave, Sunnyvale, CA 94085\\n 15% off any pho dish! Valid today only.\",\"provider\":\"Yelp\"},{\"location\":{\"latitude\":37.3879137,\"longitude\":-122.0261667},\"company\":\"Gombei Bento\",\"description\":\"155 E Maude Ave, Sunnyvale, CA 94085\\n Free drink with lunch!\",\"provider\":\"Yelp\"}]}");
            dealDatabase.add(record);
            record = new JSONObject("{\"southwest\":{\"latitude\":37.3762487,\"longitude\":-122.0332551},\"northeast\":{\"latitude\":37.3774849,\"longitude\":-122.0319676},\"dealType\":\"restaurants\",\"deals\":[{\"location\":{\"latitude\":37.37,\"longitude\":-122.03},\"company\":\"Inchin's Bamboo Garden\",\"description\":\"151 W Washington Ave, Sunnyvale, CA 94087\\n Free drink with dinner!\",\"provider\":\"Yelp\"},{\"location\":{\"latitude\":37.3762487,\"longitude\":-122.0332551},\"company\":\"Thai Basil\",\"description\":\"101 S Murphy Ave, Sunnyvale, CA 94086\\n Free Thai iced tea with lunch!\",\"provider\":\"Yelp\"}]}");
            dealDatabase.add(record);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean inGeofence(LatLng latLng, JSONObject record) {
        LatLngBounds geofence = null;
        try {
            JSONObject southwest = record.getJSONObject("southwest");
            JSONObject northeast = record.getJSONObject("northeast");
            geofence = new LatLngBounds(new LatLng(southwest.getDouble("latitude"), southwest.getDouble("longitude")),
                    new LatLng(northeast.getDouble("latitude"), northeast.getDouble("longitude")));
            return geofence.contains(latLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Deal getTestDeal() {
        return new Deal("Saks", "One day only surprising sales on women's shoes!");
    }
}
