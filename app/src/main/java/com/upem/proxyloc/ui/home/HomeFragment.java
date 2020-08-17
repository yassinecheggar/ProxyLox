package com.upem.proxyloc.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.upem.proxyloc.R;
import com.upem.proxyloc.models.MyLocations;
import com.upem.proxyloc.services.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gmap;

    private List<MarkerOptions> markers;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private LatLng Cor;
    private Activity ctx;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker;
    LocationListener locationListener;

    private int temp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(root.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    //get the location name from latitude and longitude
                    //Geocoder geocoder = new Geocoder(getContext());
                    // List<Address> addresses =
                    //   geocoder.getFromLocation(latitude, longitude, 1);
                    //String result = addresses.get(0).getLocality()+":";
                    //result += addresses.get(0).getCountryName();
                    LatLng latLng = new LatLng(latitude, longitude);
                    Cor = latLng;

                    if (marker != null) {
                        marker.remove();
                        marker = gmap.addMarker(new MarkerOptions().position(latLng).title("Me").icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        // gmap.setMaxZoomPreference(7);
                        //gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //gmap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));

                    } else {
                        marker = gmap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        gmap.setMaxZoomPreference(7);
                        gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        gmap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                    }
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
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        }


        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        //  markers = HomeFragment.markers;
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gmap = googleMap;
        mapView.onStart();
        boolean success = false;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.mapstyle));

            if (!success) {
                Log.e("tab", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }

        if (!success) {
            Log.e("ll", "Style parsing failed.");
        }

        getMarkers markers = new getMarkers();
        JSONArray jsonArray = null;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


        try {
            jsonArray = markers.execute(getString(R.string.link )+"/jstst").get();

            if (jsonArray != null) {
                update(gmap, jsonArray);
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
/*
        temp=0;
       Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("received", "size "+Global.MarkerObjects.size() );
                        if(temp<Global.changes){
                        update(gmap);
                        temp=Global.changes;
                        Log.e("map", "Update" );}
                    }
                })
               ;
            }
        }, 6000, 2000);*/
//-------------------------------------------------------------------------------------------------


      /*   for (JSONObject loc : Global.MarkerObjects) {

           try {
                if (loc.getString("UsrStatus").equals("1")) {
                    LatLng latLng = new LatLng(loc.getDouble("latitude"), loc.getDouble("longitude"));

                    marker = gmap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                } else {
                    LatLng latLng = new LatLng(loc.getDouble("latitude"), loc.getDouble("longitude"));

                    marker = gmap.addMarker(new MarkerOptions().position(latLng));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        ArrayList<MyLocations> locations =   loadJSONFromAsset();

        for (MyLocations loc: locations) {

            if(loc.getStatuts()==1){
                LatLng latLng = new LatLng(loc.getAlt(), loc.getLongi());

                marker = gmap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            }else{
                LatLng latLng = new LatLng(loc.getAlt(), loc.getLongi());

                marker = gmap.addMarker(new MarkerOptions().position(latLng));
            }
*/


    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&key=AIzaSyAVu4BSdJAG9EOnbpjyTXtERVhtsdeRKxg";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String data = "";
            try {

                data = downloadUrl(strings[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
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

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(strings[0]);
                Log.d("ParserTask", strings[0].toString());
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
                gmap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }


    class DataParser {
        List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<>();
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;
            try {
                jRoutes = jObject.getJSONArray("routes");
                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<>();
                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude));
                                hm.put("lng", Double.toString((list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return routes;
        }

        /**
         * Method to decode polyline points
         */
        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;
                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //get the location name from latitude and longitude
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        List<Address> addresses =
                                geocoder.getFromLocation(latitude, longitude, 1);
                        String result = addresses.get(0).getLocality() + ":";
                        result += addresses.get(0).getCountryName();
                        LatLng latLng = new LatLng(latitude, longitude);
                        if (marker != null) {
                            marker.remove();
                            marker = gmap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                            gmap.setMaxZoomPreference(20);
                            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                        } else {
                            marker = gmap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                            gmap.setMaxZoomPreference(20);
                            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21.0f));
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }


    public ArrayList<MyLocations> loadJSONFromAsset() {
        ArrayList<MyLocations> locList = new ArrayList<>();
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("datas.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONArray m_jArry = new JSONArray(json);


            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                MyLocations location = new MyLocations();
                location.setAlt(jo_inside.getDouble("latitude"));
                location.setLongi(jo_inside.getDouble("longitude"));
                location.setStatuts(jo_inside.getInt("status"));
                location.setId(jo_inside.getInt("ID"));


                //Add your values in your `ArrayList` as below:
                locList.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  Log.e("loadJSONFromAsset", ""+locList);
        return locList;
    }

    public void update(final GoogleMap googleMap, JSONArray jsonArray) {

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject loc = jsonArray.getJSONObject(i);
                if (loc != null) {
                    if (loc.getString("UsrStatus").equals("1")) {
                        LatLng latLng = new LatLng(loc.getDouble("latitude"), loc.getDouble("longitude"));

                        marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    } else {
                        LatLng latLng = new LatLng(loc.getDouble("latitude"), loc.getDouble("longitude"));

                        marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // get  json  data  from  server

    private class getMarkers extends AsyncTask<Object, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Object... Object) {


            OkHttpClient client = new OkHttpClient();

            String url = Object[0].toString();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")
                    .build();

            try {
                final String myResponse = client.newCall(request).execute().body().string();

                JSONArray jsonArray = new JSONArray(myResponse);

                return jsonArray;
            } catch (JSONException | IOException x) {
                Log.e("getMarkersHTTP", x.getMessage());
                System.out.println(x.getMessage());
            }

            return null;
        }
    }


}
