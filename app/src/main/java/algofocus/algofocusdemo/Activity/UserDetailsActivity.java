package algofocus.algofocusdemo.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import algofocus.algofocusdemo.Http.HttpDataHandler;
import algofocus.algofocusdemo.Model.UserInfo;
import algofocus.algofocusdemo.R;

public class UserDetailsActivity extends AppCompatActivity implements LocationListener {


    ImageView profileImage;
    TextView userID, userName, firstName, middleName, lastName, linkURI, address_TV, lati_TV, longi_TV;

    Button btnShow;
    TextView textView;
    LocationManager locationManager;
    String provider;
    final int My_PERMISSION_REQUEST_CODE = 7171;
    Double lat, lng;

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(provider, 10000, 1, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case My_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation();
                break;

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_activity);

        initializeView();
        displayData();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(UserDetailsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserDetailsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(UserDetailsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        My_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Permission has already been granted
        }



        final LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (lManager!=null){
            lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 1, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double lati=location.getLatitude();
                            double longi=location.getLongitude();
                            //lat=lati;
                            //lng=longi;
                            Log.d("TEST", "-------->"+longi+"   "+lati);
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
                    });
        }

    }

    //Initializing all view
    private void initializeView() {

        profileImage = (ImageView) findViewById(R.id.profile_pic_id);
        userID = (TextView) findViewById(R.id.userid_id);
        userName = (TextView) findViewById(R.id.user_name_id);
        firstName = (TextView) findViewById(R.id.first_name_id);
        middleName = (TextView) findViewById(R.id.mid_name_id);
        lastName = (TextView) findViewById(R.id.last_name_id);
        linkURI = (TextView) findViewById(R.id.linkuri_id);
        address_TV=(TextView)findViewById(R.id.locationtv_id);
        lati_TV=(TextView)findViewById(R.id.lati_id) ;
        longi_TV=(TextView)findViewById(R.id.longi_id) ;

        btnShow = (Button) findViewById(R.id.getAddress);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, My_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"Location is Getting...",Toast.LENGTH_SHORT).show();

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (myLocation!=null){
                    lat=myLocation.getLatitude();
                    lng=myLocation.getLongitude();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please On Location",Toast.LENGTH_SHORT).show();
                }

                Log.d("TEST", "LAT --->:" + lat);
                Log.d("TEST", "LNG --->:" + lng);

                /*lat=28.4747613;
                lng=77.0835375;*/
                new GetAddress().execute(String.format("%.4f,%.4f", lat, lng));
            }
        });

    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        final Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.d("TEST", "Location is Null");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        new GetAddress().execute(String.format("%.4f,%.4f", lat, lng));
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

    private class GetAddress extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                double lat = Double.parseDouble(strings[0].split(",")[0]);
                double lng = Double.parseDouble(strings[0].split(",")[1]);
                String response;
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.4f,%.4f&sensor=false", lat, lng);
                response = http.GetHttpData(url);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String address = ((JSONArray) jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
                Log.d("TEST", address);

                if (lat!=null && lng!=null){
                    lati_TV.setText("Latitude : "+String.valueOf(lat));
                    longi_TV.setText("Longitude : "+String.valueOf(lng));
                    address_TV.setText("Address : "+address);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    //Display All Dynamic data
    private void displayData() {
        userID.setText("User ID :" + getIntent().getStringExtra("user_id"));
        userName.setText(getIntent().getStringExtra("user_name"));
        firstName.setText("First Name :" + getIntent().getStringExtra("first_name"));
        middleName.setText("Middle Name :" + getIntent().getStringExtra("mid_name"));
        lastName.setText("Last Name :" + getIntent().getStringExtra("lst_name"));
        linkURI.setText("Link URI :" + getIntent().getStringExtra("lnk_uri"));
        //Seting Profile Pic by Gide lib.
        Glide.with(getApplicationContext()).load(getIntent().getStringExtra("pro_url")).into(profileImage);

    }//End displayData()

    //Logout
    public void logOut(View view){
        Intent i =new Intent(UserDetailsActivity.this,LogInActivity.class);
        startActivity(i);
        finish();

    }



}
