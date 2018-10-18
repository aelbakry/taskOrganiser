package com.example.bakry.AM02150_toDoList;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


/**
 * ViewLocation class enables the user to view their location and displays the rroute to the destination.
 */
public class ViewLocation extends AppCompatActivity implements OnMapReadyCallback {

    private double destinationLng = 0;
    private double destinationLat = 0;
    private double originLat = 0;
    private double originLng = 0;
    private LatLng origin = null;
    private GoogleMap mMap;
    private TextView distanceTextView = null;
    private TextView durationTextView = null;
    private Button homeButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.distanceTextView = findViewById(R.id.distanceTextViewId);
        this.durationTextView = findViewById(R.id.durationTextViewId);
        this.homeButton = findViewById(R.id.home);

        /*  Creating an intent to get the position of the task to be used later for updating task's fields  */
        Intent startEditStepsAc = getIntent();
        String taskId = startEditStepsAc.getStringExtra("taskId");

        originLat = startEditStepsAc.getDoubleExtra("lat", 0);
        originLng = startEditStepsAc.getDoubleExtra("lng", 0);


        Query querySteps = FirebaseDatabase.getInstance().getReference("tasks").child(String.valueOf(taskId) + "/location");
        querySteps.addListenerForSingleValueEvent(valueEventListener);

        homeButton = (Button) findViewById(R.id.backButtonId);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startMainAc = new Intent(ViewLocation.this, MainActivity.class);
                startMainAc.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(startMainAc);

            }
        });

    }



    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            destinationLng = dataSnapshot.child("alt").getValue(Double.class);
            destinationLat = dataSnapshot.child("lat").getValue(Double.class);


            LatLng dest = new LatLng(destinationLat, destinationLng);

            addMarker(dest, 2);


            // Getting URL to the Google Directions API
            String urlRoute = getUrlRoute(origin, dest);
            Log.d("onMapClick", urlRoute.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(urlRoute);
            //move map camera

            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(origin, dest), 0));
            } catch (Exception e) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(dest, origin), 0));


            }

            DistanceData distanceData = new DistanceData();
            distanceData.execute();


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        /*  Creating an intent to get the position of the task to be used later for updating task's fields  */
        Intent startEditStepsAc = getIntent();

        originLat = startEditStepsAc.getDoubleExtra("lat", 0);
        originLng = startEditStepsAc.getDoubleExtra("lng", 0);

        LatLng latLng = new LatLng(originLat, originLng);
        addMarker(latLng, 1);

        origin = latLng;


    }

    private String getUrlRoute(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


    public void addMarker(LatLng latLng, int code) {

        if (code == 1) {

            MarkerOptions markerOptions = new MarkerOptions();

            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

        } else if (code == 2) {

            MarkerOptions markerOptions = new MarkerOptions();

            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));


        }


    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrlRoute(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }

        private String downloadUrlRoute(String strUrl) throws IOException {
            String data = null;
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }


    }

    private class DistanceData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... jsonData) {


            try {

                //Creating the URL that is related to returning the distance in JSON Format
                String strUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + originLat + "," + originLng + "&destinations=" + destinationLat + "," + destinationLng;
                URL url = new URL(strUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();


                int statuscode = con.getResponseCode();

                if (statuscode == HttpURLConnection.HTTP_OK) {

                    //Creating new buffered reader to store the stream input.
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = br.readLine();
                    }

                    //Going through the path set by the database to extract nodes/ children
                    String json = sb.toString();
                    Log.d("JSON", json);
                    JSONObject root = new JSONObject(json);
                    JSONArray array_rows = root.getJSONArray("rows");
                    Log.d("JSON", "array_rows:" + array_rows);
                    JSONObject object_rows = array_rows.getJSONObject(0);
                    Log.d("JSON", "object_rows:" + object_rows);
                    JSONArray array_elements = object_rows.getJSONArray("elements");
                    Log.d("JSON", "array_elements:" + array_elements);
                    JSONObject object_elements = array_elements.getJSONObject(0);
                    Log.d("JSON", "object_elements:" + object_elements);
                    JSONObject object_duration = object_elements.getJSONObject("duration");
                    JSONObject object_distance = object_elements.getJSONObject("distance");

                    Log.d("JSON", "object_duration:" + object_duration);
                    return object_distance.getString("text") + "/" + object_duration.getString("text");

                }
            } catch (MalformedURLException e) {
                Log.d("error", "error1");
            } catch (IOException e) {
                Log.d("error", "error2");
            } catch (JSONException e) {
                Log.d("error", "error3");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(ViewLocation.this, String.valueOf(result),
                    Toast.LENGTH_LONG).show();

            int cutOff = 0;

            if (result != null) {

                for (int i = 0; i < result.length(); i++) {

                    if (result.substring(i, i + 1).equals("/")) {

                        cutOff = i;

                    }


                }

                String distance = result.substring(0, cutOff);
                String time = result.substring(cutOff + 1, result.length() - 1);

                //Setting the values in the UI
                distanceTextView.setText(distance);
                durationTextView.setText(time);
            }


        }


    }


}


