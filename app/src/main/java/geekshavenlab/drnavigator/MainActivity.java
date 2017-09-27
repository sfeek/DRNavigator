package geekshavenlab.drnavigator;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.lang.Math;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.WindowManager;
import android.widget.Toast;



public class MainActivity extends Activity implements OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );



        // **** Setup Update Button ****
        final Button buttonUpdate = (Button) findViewById(R.id.updateButton);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                double destLat = 0.0;
                double destLong = 0.0;
                double currentLat = 0.0;
                double currentLong = 0.0;
                double newHeading = 0.0;
                double newDistance = 0.0;
                double declination = 0.0;
                double paceDistance = 0.0;

                // Get Distance Unit
                Spinner sp = (Spinner) findViewById(R.id.paces_feet_meters);
                String distanceUnits = sp.getSelectedItem().toString();

                // Get Pace Distance Unit
                sp = (Spinner) findViewById(R.id.pace_distance);
                String paceUnits = sp.getSelectedItem().toString();

                // Get Pace Distance Unit
                sp = (Spinner) findViewById(R.id.truemag);
                String trueMag = sp.getSelectedItem().toString();

                // Get Destination Latitude
                EditText et = (EditText) findViewById(R.id.destLat);
                String tmp = et.getText().toString();
                if (!tmp.equals("")) {destLat = Double.valueOf(et.getText().toString());}
                if (destLat < -90.0 || destLat > 90.0)
                {
                    Toast.makeText(v.getContext(), "Dest. Lat. must be >= -90 and <= 90", Toast.LENGTH_LONG).show();
                    return;
                }

                // Get Destination Longitude
                et = (EditText) findViewById(R.id.destLong);
                tmp = et.getText().toString();
                if (!tmp.equals("")) {destLong = Double.valueOf(et.getText().toString());}
                if (destLong < -180.0 || destLong > 180.0)
                {
                    Toast.makeText(v.getContext(), "Dest. Long. must be >= -180 and <= 180", Toast.LENGTH_LONG).show();
                    return;
                }

                // Get Current Latitude
                et = (EditText) findViewById(R.id.currentLat);
                tmp = et.getText().toString();
                if (!tmp.equals("")) {currentLat = Double.valueOf(et.getText().toString());}
                if (currentLat < -90.0 || currentLat > 90.0)
                {
                    Toast.makeText(v.getContext(), "Current Lat. must be >= -90 and <= 90", Toast.LENGTH_LONG).show();
                    return;
                }

                // Get Current Longitude
                et = (EditText) findViewById(R.id.currentLong);
                tmp = et.getText().toString();
                if (!tmp.equals("")) {currentLong = Double.valueOf(et.getText().toString());}
                if (currentLong < -180.0 || currentLong > 180.0)
                {
                    Toast.makeText(v.getContext(), "Current Long. must be >= -180 and <= 180", Toast.LENGTH_LONG).show();
                    return;
                }

                // Get Destination Heading Objects
                EditText destHeading = (EditText) findViewById(R.id.destHeading);

                // Get Destination Distance Objects
                EditText destDistance = (EditText) findViewById(R.id.destDistance);

                //Get New Heading
                et = (EditText) findViewById(R.id.headingNew);
                tmp = et.getText().toString();
                if (!tmp.equals("")) {newHeading = Double.valueOf(et.getText().toString());}
                if (newHeading < 0 || newHeading > 360.0)
                {
                    Toast.makeText(v.getContext(), "New Heading must be >= 0 and <= 360", Toast.LENGTH_LONG).show();
                    return;
                }

                //Get New Distance
                et = (EditText) findViewById(R.id.distanceNew);
                tmp = et.getText().toString();
                if (!tmp.equals("")) {newDistance = Double.valueOf(et.getText().toString());}
                if (newDistance < 0)
                {
                    Toast.makeText(v.getContext(), "New Distance cannot be negative", Toast.LENGTH_LONG).show();
                    return;
                }

                //Get Declination
                et = (EditText) findViewById(R.id.declination);
                tmp = et.getText().toString();
                if (!tmp.equals("")) {declination = Double.valueOf(et.getText().toString());}
                declination = -declination;
                if (declination < -180.0 || declination > 180.0)
                {
                    Toast.makeText(v.getContext(), "Declination must be >= -180 and <= 180", Toast.LENGTH_LONG).show();
                    return;
                }

                //Get Pace Distance
                et = (EditText) findViewById(R.id.paceDistance);
                tmp = et.getText().toString();
                if (!tmp.equals("")) {paceDistance = Double.valueOf(et.getText().toString());}
                if (paceDistance < 0)
                {
                    Toast.makeText(v.getContext(), "Pace Distance cannot be negative", Toast.LENGTH_LONG).show();
                    return;
                }

                // Correct units for distance
                if (paceUnits.equals("Feet")) {paceDistance = paceDistance / 3.28084;}
                if (distanceUnits.equals("Paces")) {newDistance = newDistance * paceDistance;}
                if (distanceUnits.equals("Feet")) {newDistance = newDistance / 3.28084;}

                // Correct Heading
                if (trueMag.equals("Magnetic")) newHeading = newHeading - declination;

                // Calculate our new position
                double newLat = CalculateLatitude(currentLat,newHeading,newDistance);
                double newLong = CalculateLongitude(currentLat,currentLong,newLat,newHeading,newDistance);

                double heading = CalculateBearing(newLat,newLong,destLat,destLong);
                double distance = CalculateDistance(newLat,newLong,destLat,destLong);

                // Update the display
                setDestHeading(destHeading,heading,declination);
                setDestDistance(destDistance,distance,paceDistance);
                setLatitude((EditText) findViewById(R.id.currentLat),newLat);
                setLongitude((EditText) findViewById(R.id.currentLong),newLong);

                // Clear the Heading and Distance Fields
                et = (EditText) findViewById(R.id.distanceNew);
                et.setText("");
                et = (EditText) findViewById(R.id.headingNew);
                et.setText("");
                et.requestFocus();

                // Update waypoints
                DateFormat df = new SimpleDateFormat("h:mm a",Locale.US);
                String date = df.format(Calendar.getInstance().getTime());

                StringBuilder sb = new StringBuilder();
                Formatter fm = new Formatter(sb, Locale.US);
                et = (EditText) findViewById(R.id.waypoints);
                et.append(fm.format("%s: %.6f,%.6f\n",date,newLat,newLong).toString());
            }
        });

        // **** Setup Clear Button ****
        final Button buttonClear = (Button) findViewById(R.id.clearButton);
        buttonClear.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  EditText et = (EditText) findViewById(R.id.waypoints);
                  et.setText("");
              }
        });

        // **** Setup Units spinners ****

        // Spinner element 1
        Spinner spinner1 = (Spinner) findViewById(R.id.paces_feet_meters);

        // Spinner click listener
        spinner1.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories1 = new ArrayList<>();
        categories1.add("Paces");
        categories1.add("Feet");
        categories1.add("Meters");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories1);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach data adapter to spinner
        spinner1.setAdapter(dataAdapter1);

        // Spinner element 2
        Spinner spinner2 = (Spinner) findViewById(R.id.pace_distance);

        // Spinner click listener
        spinner2.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories2 = new ArrayList<>();
        categories2.add("Feet");
        categories2.add("Meters");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories2);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach data adapter to spinner
        spinner2.setAdapter(dataAdapter2);

        // Spinner element 3
        Spinner spinner3 = (Spinner) findViewById(R.id.truemag);

        // Spinner click listener
        spinner3.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories3 = new ArrayList<>();
        categories3.add("True");
        categories3.add("Magnetic");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories3);

        // Drop down layout style - list view with radio button
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach data adapter to spinner
        spinner3.setAdapter(dataAdapter3);
    }

    // Overrides for onItemSelected event
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    // Display correct heading
    private void setDestHeading(EditText e, double heading, double dec)
    {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb, Locale.US);

        double mag = heading + dec;
        if (mag < 0.0) mag = 360.0 + mag;
        if (mag >= 360.0) mag = mag - 360.0;

        e.setText(fm.format("True: %.1f  Magnetic: %.1f",heading,mag).toString());
    }

    // Update Current Latitude
    private void setLatitude(EditText e, double latitude)
    {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb, Locale.US);

         e.setText(fm.format("%.6f",latitude).toString());
    }

    // Update Current Longitude
    private void setLongitude(EditText e, double longitude)
    {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb, Locale.US);

        e.setText(fm.format("%.6f",longitude).toString());
    }

    // Display correct distance
    private void setDestDistance(EditText e, double distance, double pace)
    {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb, Locale.US);
        double feet = distance * 3.28084;
        double paces = distance / pace;

        e.setText(fm.format("Paces: %.1f  Feet: %.1f  Meters: %.1f",paces,feet,distance).toString());
    }

    // Calculate the distance
    private double CalculateDistance (double latitude1, double longitude1, double latitude2, double longitude2)
    {
        double R = 6371e3;
        double lat1 = DegreeToRadian(latitude1);
        double lat2 = DegreeToRadian(latitude2);
        double deltaLat = DegreeToRadian(latitude2 - latitude1);
        double deltaLong = DegreeToRadian(longitude2 - longitude1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    // Calculate bearing
    private double CalculateBearing(double latitude1, double longitude1, double latitude2, double longitude2)
    {
        double lat1 = DegreeToRadian(latitude1);
        double lat2 = DegreeToRadian(latitude2);
        double long1 = DegreeToRadian(longitude1);
        double long2 = DegreeToRadian(longitude2);

        double y = Math.sin(long2 - long1) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(long2 - long1);

        double bearing = RadianToDegree(Math.atan2(y, x));

        // Correct "wrap around" as necessary
        if (bearing < 0) bearing = 360.0 + bearing;
        if (bearing > 360.0) bearing = bearing - 360.0;

        return bearing;
    }

    // Calculate Latitude
    private double CalculateLatitude(double latitude1, double bearing, double distance)
    {
        double R = 6371e3;
        double lat1 = DegreeToRadian(latitude1);
        bearing = DegreeToRadian(bearing);
        double lat2;

        lat2 = RadianToDegree(Math.asin(Math.sin(lat1) * Math.cos(distance / R) + Math.cos(lat1) * Math.sin(distance / R) * Math.cos(bearing)));

        return lat2;
    }

    // Calculate Longitude
    private double CalculateLongitude(double latitude1, double longitude1, double latitude2, double bearing, double distance)
    {
        double R = 6371e3;
        double lat1 = DegreeToRadian(latitude1);
        double lat2 = DegreeToRadian(latitude2);
        double long1 = DegreeToRadian(longitude1);
        bearing = DegreeToRadian(bearing);
        double long2;

        long2 = long1 + Math.atan2(Math.sin(bearing) * Math.sin(distance / R) * Math.cos(lat1), Math.cos(distance / R) - Math.sin(lat1) * Math.sin(lat2));

        long2 = (long2 + 540) % 360 - 180;

        long2 = RadianToDegree(long2);

        return long2;
    }

    // Convert Degrees to Radians
    private double DegreeToRadian(double angle)
    {
        return Math.PI * angle / 180.0;
    }

    // Convert Radians to Degrees
    private double RadianToDegree(double angle)
    {
        return angle * (180.0 / Math.PI);
    }
}

