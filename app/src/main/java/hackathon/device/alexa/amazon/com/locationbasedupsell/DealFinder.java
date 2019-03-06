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


    ArrayList<Deal> findDeals(LatLng latLng, String dealType) {
        ArrayList<Deal> deals = new ArrayList<>();
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
            record = new JSONObject("{\"southwest\":{\"latitude\":37.390322,\"longitude\":-122.031695},\"northeast\":{\"latitude\":37.3982652,\"longitude\":-122.0284754},\"dealType\":\"restaurant\",\"deals\":[{\"location\":{\"latitude\":37.394321,\"longitude\":-122.0310037},\"company\":\"Dish n' Dash\",\"description\":\"Free salad/fries with lunch!\",\"provider\":\"Yelp\"},{\"location\":{\"latitude\":37.394321,\"longitude\":-122.0310037},\"company\":\"Hobee's\",\"description\":\"Free drink with lunch/dinner!\",\"provider\":\"Yelp\"}]}");
            dealDatabase.add(record);
            record = new JSONObject("{\"southwest\":{\"latitude\":37.3824864,\"longitude\":-122.0352584},\"northeast\":{\"latitude\":37.390221,\"longitude\":-122.021473},\"dealType\":\"restaurant\",\"deals\":[{\"location\":{\"latitude\":37.3865267,\"longitude\":-122.0331342},\"company\":\"Le Boulanger\",\"description\":\"15% off lunch! Valid today only\",\"provider\":\"Yelp\"},{\"location\":{\"latitude\":37.3865267,\"longitude\":-122.0331342},\"company\":\"Kal's Bar-B-Q\",\"description\":\"Free drink with lunch/dinner!\",\"provider\":\"Yelp\"}]}");
            dealDatabase.add(record);
            record = new JSONObject("{\"southwest\":{\"latitude\":37.376171,\"longitude\":-122.037151},\"northeast\":{\"latitude\":37.379361,\"longitude\":-122.026205},\"dealType\":\"restaurant\",\"deals\":[{\"location\":{\"latitude\":37.37,\"longitude\":-122.03},\"company\":\"Inchin's Bamboo Garden\",\"description\":\"Free drink with dinner!\",\"provider\":\"Yelp\"},{\"location\":{\"latitude\":37.3762487,\"longitude\":-122.0332551},\"company\":\"Thai Basil\",\"description\":\"Free Thai iced tea with lunch!\",\"provider\":\"Yelp\"}]}");
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
