package hackathon.device.alexa.amazon.com.locationbasedupsell;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    // this is the deal lists
    private ArrayList<DealModel> mDealListItems = new ArrayList<>();
    private DealItemArrayAdapter mDealListAdapter;
    private ListView mDealListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setupMapView();
        setupFeatureSwitch();
        setupCategorySpinner();
        setupDealListView();
    }

    private void setupDealListView() {
        mDealListItems.add(new DealModel("Your deal is coming up!"));
        mDealListAdapter = new DealItemArrayAdapter(this, mDealListItems);

        mDealListView = findViewById(R.id.dealListView);
        mDealListView.setAdapter(mDealListAdapter);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setupMapView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void setupFeatureSwitch() {
        TextView textView = (TextView) findViewById(R.id.enableLiveDealText);
        Switch featureSwitch = (Switch) findViewById(R.id.enableLiveDealSwitch);
        featureSwitch.setChecked(false);
        featureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // switch to true
                    Toast.makeText(getApplicationContext(), R.string.enabledToast, LENGTH_SHORT).show();

                } else {
                    //switch to false
                    Toast.makeText(getApplicationContext(), R.string.disabledToast, LENGTH_SHORT).show();

                }
            }
        });


    }
    private void setupCategorySpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.categoryList);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int i, long l) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String item = (String) adapter.getItemAtPosition(i);
        Log.i(TAG, "onItemSelected: " + item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
