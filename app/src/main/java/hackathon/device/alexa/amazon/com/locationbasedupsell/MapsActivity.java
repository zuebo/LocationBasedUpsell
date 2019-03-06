package hackathon.device.alexa.amazon.com.locationbasedupsell;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.LENGTH_SHORT;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private DealFinder dealFinder;
    private int mElapsedTime = 0;
    private List<LatLng> mWayPoints;
    private Timer mTimer;
    private boolean mIsFeatureOn = false;
    private boolean mIsTimerRunning = false;
    // start and end destination
    private String mStartingPoint = null;
    private String mEndPoint = null;

    // selected type.
    private String mDealType = "restaurants";

    // this is the deal lists
    private ArrayList<Deal> mDealListItems = new ArrayList<>();
    private DealItemArrayAdapter mDealListAdapter;
    private ListView mDealListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setupStartEndInputs();
        setupMapView();
        setupFeatureSwitch();
        setupCategorySpinner();
        setupDealListView();

        dealFinder = new DealFinder();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Define list to get all latlng for the route
        calculateWayPoints();
        LatLng src = mWayPoints.get(0);
        drawNedim(src);
        //mMap.addMarker(new MarkerOptions().position(dst).title("Marker in dst"));

        //Draw the polyline
//        if (path.size() > 0) {
//            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//            mMap.addPolyline(opts);
//        }

        mMap.getUiSettings().setZoomControlsEnabled(true);


    }
    private void calculateWayPoints() {
        if (mStartingPoint == null && mEndPoint == null) {
            mWayPoints = getRoute("SJC10", "Sunnyvale Downtown");
        } else {
            mWayPoints = getRoute(mStartingPoint, mEndPoint);
        }
    }
    private void drawNedim(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).icon(getIcon()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
    private void startSimulation() {
        // draw marker in interval
        // update deals based the waypoints
        startTimer();

    }
    private void stopSimulation() {
        // stop timer. reset mElapsedTime.
        stopTimer();
        mElapsedTime = 0;

    }
    private void startTimer() {
        if (!mIsTimerRunning) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    mElapsedTime += 1; //increase every sec
                    mHandler.obtainMessage(1).sendToTarget();
                    Log.i(TAG, "run: scheduleAtFixedRate");
                }
            }, 0, 200);
            mIsTimerRunning = true;
        }

    }
    private void stopTimer() {
        if (mIsTimerRunning) {
            mTimer.cancel();
            mTimer.purge();
            mIsTimerRunning = false;
        }
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mWayPoints != null && mWayPoints.size() != 0) {
                LatLng dst = mWayPoints.get(mElapsedTime % mWayPoints.size());
                mMap.clear();
                drawNedim(dst);
                Log.i(TAG, "handling message");

                // find deals
                mDealListItems = dealFinder.findDeals(dst, mDealType);
                updateDealList();
            }
        }
    };

    private BitmapDescriptor getIcon() {
        Drawable dr = getResources().getDrawable(R.drawable.nedim);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 80, 100, true);
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap);

    }
    List<LatLng> getRoute(String src, String dst) {
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();


        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBM6pPBIAuZzzPVfGEn2ZFbKkH33301fsk")
                .build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, src, dst)
                .mode(TravelMode.DRIVING);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        Log.i(TAG, "path: " + path);
        return path;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int i, long l) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        mDealType = (String) adapter.getItemAtPosition(i);
        Log.i(TAG, "onItemSelected: " + mDealType);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void updateDealList() {
        mDealListAdapter.clear();
        mDealListAdapter.addAll(mDealListItems);

    }

    private void setupStartEndInputs() {
        final EditText startText = (EditText) findViewById(R.id.startPoint);
        final EditText endText = (EditText) findViewById(R.id.endPoint);
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartingPoint = startText.getText().toString();
            }
        });
        endText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEndPoint = endText.getText().toString();
            }
        });
        endText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    mStartingPoint = startText.getText().toString();
                    mEndPoint = endText.getText().toString();
                    calculateWayPoints();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupDealListView() {
        // initialize
        mDealListItems.add(getDealObject());
        mDealListItems.add(getDealObject());
        mDealListAdapter = new DealItemArrayAdapter(this, mDealListItems);

        mDealListView = findViewById(R.id.dealListView);
        mDealListView.setAdapter(mDealListAdapter);

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
                    mIsFeatureOn = true;
                    startSimulation();

                } else {
                    //switch to false
                    Toast.makeText(getApplicationContext(), R.string.disabledToast, LENGTH_SHORT).show();
                    mIsFeatureOn = false;
                    stopSimulation();

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
        spinner.setSelection(2); // restaurants
    }



    private Deal getDealObject() {
        DealFinder dealFinder = new DealFinder();
        return dealFinder.getTestDeal();
    }

}
