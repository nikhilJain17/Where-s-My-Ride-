package com.hello.learning.wheresmyride;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.sendgrid.*;

public class PassengerMap extends FragmentActivity implements LocationListener {



    @Override
    public void onLocationChanged(Location location) {

        LatLng dick = new LatLng(location.getLatitude(), location.getLongitude());

        // Update the camera position
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(dick, 13);
        mMap.moveCamera(update);

        // Show a popup
        Toast.makeText(this, "Moving", Toast.LENGTH_SHORT).show();

        // Log the latitude
        Log.i("Latitude: ", Double.toString(dick.latitude));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    SharedPreferences mapPrefs;
    SharedPreferences.Editor sharedPrefsEditor;

    String globalCurrentAddress = "..";
    String globalDestination = "";

    String url = "";

    String globalDuration = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);
        setUpMapIfNeeded();


        mapPrefs = getSharedPreferences("mapPrefs", 0);
        sharedPrefsEditor = mapPrefs.edit();






    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.add_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addContactMenuItem) {


            DialogFragment addContactFragment = new addContactFragment();
            addContactFragment.show(getFragmentManager(), "");
            // 1st. Open up busNameFragment (ask Swap)
            // 2nd. Open up addContactFragment

            busNameFragment bus = new busNameFragment();
            bus.show(getFragmentManager(), "");

            /////////////////////////////////////////////////////////////



            // Set the global address


        }

        else if (id == R.id.refresh) {


            Log.i("Current Address Before: ", globalCurrentAddress);
            Log.i("Destination Address Before: ", globalDestination);


            // The destination becoems the new current address
            globalCurrentAddress = globalDestination;

            // THe destination becomes the new entered address
            globalDestination = mapPrefs.getString("street", "") + mapPrefs.getString("city", "") + mapPrefs.getString("zip", "");


            // If in fact OK was clicked
            boolean clicked = mapPrefs.getBoolean("clicked", false);

            if (clicked) {

                // Geocode
                try {
                    geocodeAndPlot(globalDestination);
                } catch (IOException io) {
                    io.printStackTrace();
                }

                // At the end, set the clicked value to false so that if we go to the dialogfragment and dont click "ok", it wont display anything
                sharedPrefsEditor.putBoolean("clicked", false);

            }
        }

        else if (id == R.id.search_button) {

            //SendGridTask sgtask = new SendGridTask();
           // sgtask.execute();

            SearchForBus search = new SearchForBus();
            search.show(getFragmentManager(), "");

        }

        return true;
    }

    public void geocodeAndPlot(String address) throws IOException{

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(address, 1);
        Address add = list.get(0);


        double lat = add.getLatitude();
        double longd = add.getLongitude();

        LatLng busStop = new LatLng(lat, longd);

        // Add the marker
        mMap.addMarker(new MarkerOptions().position(busStop).title(globalDestination).draggable(false));

        // Zoom in onto place
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(busStop, 13);
        mMap.moveCamera(update);

        buildRoute();

    }

    // Start the route - will calculate tiems, etc
    // Called when u say "refresh" button
    public void buildRoute() {

        // If the next destination is empty, do nothing
        if (globalDestination == "") {
            return;
        }


        String baseURL = "https://maps.googleapis.com/maps/api/directions/json?";

        String origin = "origin=" + globalCurrentAddress;
        String destination = "&destination=" + globalDestination;

        Log.i("Current Address After: ", globalCurrentAddress);
        Log.i("Destination After: ", globalDestination);


        url = baseURL + origin + destination;

        Log.i("Google Directions URL: ", url);

        GoogleDirectionsApiTask gtask = new GoogleDirectionsApiTask();
        gtask.execute();

        // Toast
        Toast.makeText(this, globalDuration, Toast.LENGTH_SHORT).show();

    }

    // Connect to Google Directions API

    public class GoogleDirectionsApiTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resultsJsonStr = null;

            try {

                URL url1 = new URL(url);

                // Connect and open the request
                urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // read the input into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                    // nothing to do
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // add a newline to make reading a lot easier
                    buffer.append(line + "\n");
                }


                if (buffer.length() == 0) {
                    // stream was empty
                    return null;
                }

                resultsJsonStr = buffer.toString();


            } catch (IOException io) {

                Log.e("Network Connection - ", "error", io);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();

                }

                if (reader != null) {

                    try {
                        reader.close();

                    } catch (final IOException e) {
                        Log.e("Error closing stream", " errr", e);
                    }
                }
            }

            try {
                getDurationFromJson(resultsJsonStr);
            } catch (JSONException e) {
                Log.i("ERROR: ", "JSON Error in resultsJsonStr");
//                e.printStackTrace();
            }


            return resultsJsonStr;


        }


        public void getDurationFromJson(String resultsJsonStr) throws JSONException {

            JSONObject results = new JSONObject(resultsJsonStr);

            JSONArray resultan = results.getJSONArray("routes");

            JSONArray legs = resultan.getJSONArray(2);

            JSONObject duration = legs.getJSONObject(1);

            globalDuration = duration.getString("text");

            Log.i("Global Duration: ", globalDuration);

        }
    }

   public class UpdateMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Location location = mMap.getMyLocation();

            LatLng driveRight = new LatLng(location.getLatitude(), location.getLongitude());

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(driveRight, 13);

            mMap.moveCamera(update);



            return null;
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }
    }

