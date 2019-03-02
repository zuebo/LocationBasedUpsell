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
        List<Deal> deals = null;
        try {
            for (JSONObject record : dealDatabase) {
                if (dealType.equals(record.getString("dealType")) && inGeofence(latLng, record)) {
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
            record = new JSONObject("{\"southwest\":{\"latitude\":37.37,\"longitude\":-122.01},\"northeast\":{\"latitude\":37.4,\"longitude\":-121.98},\"dealType\":\"housing\",\"deals\":[{\"location\":{\"latitude\":37.38,\"longitude\":-121.98},\"company\":\"The Arches\",\"description\":\"1235 Wildwood Ave, Sunnyvale, CA 94089\\n 2 bedroom, $2600/month\",\"provider\":\"Zillow\"}]}");
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
}
