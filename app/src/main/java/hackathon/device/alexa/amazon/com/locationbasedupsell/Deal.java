package hackathon.device.alexa.amazon.com.locationbasedupsell;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Deal {

    private String company;

    private LatLng location;

    private String description;

    private String provider;

    public Deal() {

    }

    public Deal(String company, LatLng location, String description, String provider) {
        this.company = company;
        this.location = location;
        this.description = description;
        this.provider = provider;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    static List<Deal> fromJson(JSONArray jsonArray) {
        JSONObject dealJson;
        ArrayList<Deal> deals = new ArrayList<Deal>(jsonArray.length());
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                dealJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Deal deal = Deal.fromJson(dealJson);
            if (deal != null) {
                deals.add(deal);
            }
        }
        return deals;
    }

    static Deal fromJson(JSONObject jsonObject) {
        Deal deal = new Deal();
        try {
            deal.company = jsonObject.getString("company");

            JSONObject locationJson = jsonObject.getJSONObject("location");
            deal.location = new LatLng(locationJson.getDouble("latitude"), locationJson.getDouble("longitude"));

            deal.description = jsonObject.getString("description");

            deal.provider = jsonObject.getString("provider");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return deal;
    }

}
