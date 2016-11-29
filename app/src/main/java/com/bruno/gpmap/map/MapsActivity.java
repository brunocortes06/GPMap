package com.bruno.gpmap.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bruno.gpmap.AdditionalInfo;
import com.bruno.gpmap.R;
import com.bruno.gpmap.manage.CompleteRegister;
import com.bruno.gpmap.manage.LoginActivity;
import com.bruno.gpmap.model.User;
import com.bruno.gpmap.util.GPSTracker;
import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings ("ResourceType") public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GeoQueryEventListener, GoogleMap.OnCameraChangeListener, GoogleMap.InfoWindowAdapter
{

    private GoogleMap mMap;
    private LocationManager locationmanager;
    private DatabaseReference mDatabase;

    private String uid;
    private double lat;
    private double lng;
    private GeoQuery geoQuery;
    private HashMap<String, Marker> markers;
    private GeoFire geoFire;
    private Map<String, User> currentUserData;
    private Map<String, User> allOtherUserData;
    private Map<Marker, User> otherUsersData;
    private AdView adView;

    private Toolbar mToolbar;

    //private static final String GEO_FIRE_REF = "https://gpmap.firebaseio.com/locations";
    private LocationListener locationCallback = new LocationListener()
    {

        @Override
        public void onLocationChanged (Location location)
        {
            updateFirebaseLocation(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged (String provider, int status, Bundle extras)
        {

        }

        @Override
        public void onProviderEnabled (String provider)
        {

        }

        @Override
        public void onProviderDisabled (String provider)
        {

        }
    };

    private void updateFirebaseLocation (double latitude, double longitude)
    {
        GeoFire geoFire = new GeoFire(mDatabase);
        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
        geoFire.setLocation(uid, geoLocation);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MapsInitializer.initialize(this);
        getAds();


        currentUserData = new HashMap<>();
        allOtherUserData = new HashMap<>();
        otherUsersData = new HashMap<>();

        GPSTracker gps = new GPSTracker(this);
        // check if GPS enabled
        if(gps.canGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("locations");

        uid = getIntent().getExtras().getString("uid");

        // setup GeoFire
        this.geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("locations"));
        // radius in km
//        GeoLocation geoLocation = new GeoLocation(lat, lng);
        this.geoQuery = this.geoFire.queryAtLocation(new GeoLocation(lat, lng), 100);

        // setup markers
        this.markers = new HashMap<>();

        mToolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(mToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.maps, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        if (item.getItemId() == R.id.logout_option)
        {
            logout();
        }
        if (item.getItemId() == R.id.complete_reg)
        {
            finish();
            Intent complete_reg = new Intent(MapsActivity.this, CompleteRegister.class);
            complete_reg.putExtra("uid", uid);
            startActivity(complete_reg);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout ()
    {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        finish();
        LoginActivity.start(this);
    }

    @Override
    public void onMapReady (GoogleMap googleMap)
    {
        mMap = googleMap;
        LatLng local = new LatLng(lat,lng);
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, locationCallback);
        LatLng latlng = new LatLng(lat, lng);
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local,15));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 15));

        mMap.setInfoWindowAdapter(this);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, AdditionalInfo.class);
                intent.putExtra("key", marker.getSnippet());
                startActivity(intent);
            }
        });

        updateFirebaseLocation(lat, lng);
    }

    public static void start (Context context, String uid)
    {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // add an event listener to start updating locations again
        this.geoQuery.addGeoQueryEventListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // remove all event listeners to stop updating in the background
        this.geoQuery.removeAllListeners();
        for (Marker marker: this.markers.values()) {
            marker.remove();
        }
        this.markers.clear();
    }

    //Metodos do listener  GeoQuery
    @Override
    public void onKeyEntered(final String key, final GeoLocation location) {
        DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUser.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                try {
                    currentUserData.put(snapshot.getKey(),snapshot.getValue(User.class));
                }catch (Exception e){
                    e.printStackTrace();
                }
                // se showBoys for falso, mostrara mulheres
                boolean showBoys = false;
                User user = snapshot.getValue(User.class);
                if(user == null)
                    return;

                if(user.getGender().equals("Feminino")){
                    showBoys = true;
                }
                searchAllOtherUsersData(showBoys);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void searchAllOtherUsersData(final boolean showBoys) {
        DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                allOtherUserData.put(dataSnapshot.getKey(),dataSnapshot.getValue(User.class));
                // se showBoys for falso, mostrara mulheres
                if(showBoys && dataSnapshot.getValue(User.class).getGender().equals("Masculino")) {
                    //printo os markers no mapa, apenas o sexo oposto do usuário
                    printMarkers(dataSnapshot.getKey());
                }else if(!showBoys && dataSnapshot.getValue(User.class).getGender().equals("Feminino")) {
                    printMarkers(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
//                Comment newComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
//                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
//                Comment movedComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//                Toast.makeText(mContext, "Failed to load comments.",
//                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void printMarkers(final String key) {

        geoFire.getLocation(key, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    Marker marker;
                    if(key.equals(uid)) {
                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        marker.setSnippet(key);
                    }else{
                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
                        marker.setSnippet(key);
                    }
                    markers.put(key, marker);
                    getUserData(marker, key);
                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });
    }

    @Override
    public void onKeyExited(String key) {
        // Remove any old marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            marker.remove();
            this.markers.remove(key);
        }
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        // Move the marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            this.animateMarkerTo(marker, location.latitude, location.longitude);
        }
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // Update the search criteria for this geoQuery and the circle on the map
        LatLng center = cameraPosition.target;
        double radius = zoomLevelToRadius(cameraPosition.zoom);
        this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
        // radius in km
        this.geoQuery.setRadius(radius/1000);
    }

    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000/Math.pow(2, zoomLevel);
    }

    // Animation handler for old APIs without animation support
    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {

        return null;
    }

    private View prepareInfoView(Marker marker){
        //prepare InfoView programmatically
        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        TextView textView = new TextView(MapsActivity.this);
        Drawable backGround = getResources().getDrawable(R.drawable.background_popup);
        textView.setBackgroundDrawable(backGround);
        textView.setText(otherUsersData.get(marker).getName());
        textView.setTextSize(20);
        textView.setTextColor(Color.RED);
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        infoView.addView(textView);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        return infoView;
    }

    public void getUserData(final Marker marker, String key) {
        DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUser.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                otherUsersData.put(marker, snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void getAds() {
//        adView = new AdView(this);
        adView = (AdView) findViewById(R.id.adView);
//        adView.setAdUnitId("ca-app-pub-9461541042807906/9717414805");
//        adView.setAdSize(AdSize.SMART_BANNER);

        AdRequest adRequest = new AdRequest.Builder()
//                  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
//        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

//    private void showAds() {
//        LinearLayout linear2 = new LinearLayout(Votations.this);
//        linear2.setOrientation(LinearLayout.VERTICAL);
//        linear2.addView(adView);
//        linearlayout.addView(linear2);
//    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }
}