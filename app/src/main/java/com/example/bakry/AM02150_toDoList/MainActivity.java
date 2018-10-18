package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry) on 2/15/18.
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * MainActivity that handles many background operations such as retreiveing the upocoming tasks number,m current location etc..
 */
public class MainActivity extends AppCompatActivity {


    private Button bViewTask = null;
    private Button bNewTask = null;
    private TextView upcomingTasks = null;
    private TextView todayTasks = null;
    private TextView percentageCompleted = null;
    private ProgressBar overallProgress = null;
    private TextView dayNameDisplay = null;
    private TextView dayDisplay = null;
    private TextView monthDisplay = null;
    private TextView yearDisplay = null;
    private TextView footerDisplay = null;
    private ArrayList<Task> allTasks = null;
    private ArrayList<Task> completedTasks = null;
    private TextView addressTextView = null;
    private TextView durationTextView = null;
    private Task upcomingTask = null;
    private BroadcastReceiver broadcastReceiver;
    private double currentLng = 0;
    private double currentLat = 0;
    private String destionationAddress = null;
    private Button upcomingTaskButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Starting teh service to get the current location
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);

        runtimePermission();



        /*  Initialising the Buttons to their relative IDs in layout.  */
        bViewTask = (Button) findViewById(R.id.bViewTasks);
        bNewTask = (Button) findViewById(R.id.bNewTaskReminder);
        upcomingTaskButton = findViewById(R.id.bViewNextTaskId);



        /*  Initialising the TextViews to their relative IDs in layout.  */
        todayTasks = (TextView) findViewById(R.id.todayTasksId);
        upcomingTasks = (TextView) findViewById(R.id.upcomingTasksId);
        percentageCompleted = (TextView) findViewById(R.id.percentageCompletedId);
        footerDisplay = (TextView) findViewById(R.id.footerId);
        durationTextView = findViewById(R.id.reachInTextViewId);
        addressTextView = findViewById(R.id.currentLocationTextViewId);


        /*  Initialising the ProgressBar to its relative ID in layout.  */
        overallProgress = (ProgressBar) findViewById(R.id.circularProgressbar);


        /*  Setting onClick Listener on bViewTask button to implement action when pressed.  */
        bViewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allTasks != null || completedTasks != null) {

                    /*  Initialising the intent which is to start ViewTasks Activity.  */
                    Intent startViewTasksAc = new Intent(MainActivity.this, ViewTasks.class);
                    startActivity(startViewTasksAc);

                } else {


                    Toast.makeText(MainActivity.this, "Maybe add a task first?",
                            Toast.LENGTH_LONG).show();
                }

            }
        });



        /*  Setting onClick Listener on bNewTask button to implement action when pressed.  */
        bNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*  Initialising and starting the intent which is to start newReminderOrActivity Activity.  */
                Intent intent = new Intent(MainActivity.this, NewTask.class);
                startActivity(intent);


            }
        });

        upcomingTaskButton.setEnabled(false);
        upcomingTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*  Initialising and starting the intent which is to start newReminderOrActivity Activity.  */
                Intent intent = new Intent(MainActivity.this, EditTask.class);
                intent.putExtra("taskId", upcomingTask.getId());
                startActivity(intent);

            }
        });


        /*
        Creating a Query that will return the values needed for allTaks and Completed Tasks using the ValueEventListener to wait for the
        result and use it accordingly.
         */
        Query queryAll = FirebaseDatabase.getInstance().getReference("tasks").orderByChild("is_complete").equalTo(false);
        queryAll.addListenerForSingleValueEvent(valueEventListener);


        Query queryComplete = FirebaseDatabase.getInstance().getReference("tasks").orderByChild("is_complete").equalTo(true);
        queryComplete.addListenerForSingleValueEvent(valueEventListener2);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Performs the necessary check ups on the fields, texts and widgets present in this activity,
     * and sets their values to the correct (updated) ones.
     */
    public void updateBackground() {

        Calendar today = Calendar.getInstance();

        dayNameDisplay = (TextView) findViewById(R.id.dayNameId);
        dayDisplay = (TextView) findViewById(R.id.dayNumberId);
        monthDisplay = (TextView) findViewById(R.id.monthNumberId);
        yearDisplay = (TextView) findViewById(R.id.yearNumberId);

        dayNameDisplay.setText(today.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
        dayDisplay.setText(Integer.toString(today.get(Calendar.DAY_OF_MONTH)));
        monthDisplay.setText(Integer.toString(today.get(Calendar.MONTH)));
        yearDisplay.setText(Integer.toString(today.get(Calendar.YEAR)));


    }

    /**
     * ValueEventListener for retrieving allTasks non completed from the database
     */
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            allTasks = new ArrayList<>();


            //Iterating through all tasks in the table
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                ArrayList<Step> steps = new ArrayList<>();
                Task task1 = childSnapshot.getValue(Task.class);
                Calendar dueDate = new GregorianCalendar();

                for (DataSnapshot stepsSnapshot : childSnapshot.child("steps_list").getChildren()) {

                    Step step = stepsSnapshot.getValue(Step.class);
                    steps.add(step);

                }

                dueDate.set(Calendar.DAY_OF_MONTH, childSnapshot.child("due_date").child("day").getValue(Integer.class));
                dueDate.set(Calendar.MONTH, childSnapshot.child("due_date").child("month").getValue(Integer.class));
                dueDate.set(Calendar.YEAR, childSnapshot.child("due_date").child("year").getValue(Integer.class));

                task1.setDueDate(dueDate);
                task1.setSteps(steps);
                task1.setId(childSnapshot.getKey());

                task1.setLng(childSnapshot.child("location").child("alt").getValue(Double.class));
                task1.setLat(childSnapshot.child("location").child("lat").getValue(Double.class));


                allTasks.add(task1);

            }


            if (completedTasks != null && allTasks != null) {


                double progressTasks = (completedTasks.size() / (allTasks.size() + completedTasks.size())) * 100;
                overallProgress.setProgress((int) progressTasks);

                /* Displaying the actual percentage along with the ProgressBar created previously*/
                percentageCompleted.setText(progressTasks + " % ");

                footerDisplay.setText(completedTasks.size() + "  out of  " + (allTasks.size() + completedTasks.size()) + " complete! ");
            }





            int day = 0;
            Calendar dueDate = null;

            for (int i = 0; i < allTasks.size(); i++) {

                dueDate = allTasks.get(i).getDueDate();

                if (i == 0) {


                    upcomingTask = allTasks.get(i);

                } else {


                    if (dueDate.get(Calendar.DAY_OF_MONTH) > upcomingTask.getDueDate().get(Calendar.DAY_OF_MONTH)) {


                        upcomingTask = allTasks.get(i);


                    }


                }



            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };


    /**
     * ValueEventListener for retrieving completedTasks from the database
     */
    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            completedTasks = new ArrayList<>();


            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                ArrayList<Step> steps = new ArrayList<>();
                Task task1 = childSnapshot.getValue(Task.class);
                Calendar dueDate = new GregorianCalendar();

                for (DataSnapshot stepsSnapshot : childSnapshot.child("steps_list").getChildren()) {

                    Step step = stepsSnapshot.getValue(Step.class);
                    steps.add(step);

                }

                dueDate.set(Calendar.DAY_OF_MONTH, childSnapshot.child("due_date").child("day").getValue(Integer.class));
                dueDate.set(Calendar.MONTH, childSnapshot.child("due_date").child("month").getValue(Integer.class));
                dueDate.set(Calendar.YEAR, childSnapshot.child("due_date").child("year").getValue(Integer.class));

                task1.setDueDate(dueDate);
                task1.setSteps(steps);
                task1.setId(childSnapshot.getKey());


                completedTasks.add(task1);

            }

            if (completedTasks != null && allTasks != null) {


                double progressTasks = (completedTasks.size() / (allTasks.size() + completedTasks.size())) * 100;
                overallProgress.setProgress((int) progressTasks);

                /* Displaying the actual percentage along with the ProgressBar created previously*/
                percentageCompleted.setText(progressTasks + " % ");

                footerDisplay.setText(completedTasks.size() + "  out of  " + (allTasks.size() + completedTasks.size()) + " complete! ");
            }



            /*  A call to the updateBackground() method which updates this Activity's background accordingly - OnCreation  */
            updateBackground();


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };

    /**
     * AsyncTask that updates the UI with Todays Tasks
     *
     * @return todayTasks
     */
    private class TodaysTasks extends AsyncTask<String, Void, Integer> {


        @Override
        protected Integer doInBackground(String... strings) {
            /*  Creating a calendar instance with today's date.  */
            Calendar todaysDate = Calendar.getInstance();


            /* Initialising and setting the allTasks list from the one saved in the SharedPrefrences.  */

            /**
             * Initialising and setting the fields of Today's date into (day, month, year),
             * along with the todayTasks variable that hold the total number of tasks due for today.
             */
            int todayTasks = 0;
            int dayToday = todaysDate.get(Calendar.DAY_OF_MONTH);
            int monthToday = todaysDate.get(Calendar.MONTH) + 1;
            int yearToday = todaysDate.get(Calendar.YEAR);


            if (allTasks != null) {

                for (int i = 0; i < allTasks.size(); i++) {

                    if (allTasks.get(i) != null) {

                        int dayTask = allTasks.get(i).getDueDate().get(Calendar.DAY_OF_MONTH);
                        int monthTask = allTasks.get(i).getDueDate().get(Calendar.MONTH);
                        int yearTask = allTasks.get(i).getDueDate().get(Calendar.YEAR);

                        if ((dayToday == dayTask && monthToday == monthTask && yearToday == yearTask)) {

                            todayTasks++;

                        }
                    }

                }

                return todayTasks;

            } else {

                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer todaysTasksNumber) {

            todayTasks.setText(String.valueOf(todaysTasksNumber));
            updateBackground();

        }
    }


    /**
     * AsyncTask that updates the UI with Upcoming Tasks
     *
     * @return todayTasks
     */
    private class UpcomingTasks extends AsyncTask<String, Void, Integer> {


        @Override
        protected Integer doInBackground(String... strings) {
            Calendar todaysDate = Calendar.getInstance();

            /**
             * Initialising and setting the fields of Today's date into (day, month, year),
             * along with the todayTasks variable that hold the total number of tasks due for today.
             */
            int upcomingTasks = 0;
            int dayToday = todaysDate.get(Calendar.DAY_OF_MONTH);
            int monthToday = todaysDate.get(Calendar.MONTH) + 1;
            int yearToday = todaysDate.get(Calendar.YEAR);

            if (allTasks != null) {


                for (int i = 0; i < allTasks.size(); i++) {

                    if (allTasks.get(i) != null) {

                        int dayTask = allTasks.get(i).getDueDate().get(Calendar.DAY_OF_MONTH);
                        int monthTask = allTasks.get(i).getDueDate().get(Calendar.MONTH);
                        int yearTask = allTasks.get(i).getDueDate().get(Calendar.YEAR);

                        if (yearTask == yearToday) {

                            if (monthTask == monthToday) {


                                if (dayTask == dayToday) {


                                } else if (dayTask > dayToday) {

                                    upcomingTasks++;

                                } else if (dayTask < dayToday) {


                                }


                            } else if (monthTask > monthToday) {

                                upcomingTasks++;

                            } else if (monthTask < monthToday) {


                            }


                        } else if (yearTask > yearToday) {


                            upcomingTasks++;


                        } else if (yearTask < yearToday) {


                        }
                    }

                }

                return upcomingTasks;

            } else {


                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer upcomingTasksNumber) {

            upcomingTasks.setText(String.valueOf(upcomingTasksNumber));

            updateBackground();

        }
    }


    /**
     * AsyncTask that updates the UI with distance of teh upComingTask
     */
    private class DistanceData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... jsonData) {

            if (upcomingTask != null) {
                try {

                    //Creating the URL to retreive the JSON objects by Google Distance Matrix Api
                    String strUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + currentLat + "," + currentLng + "&destinations=" + upcomingTask.getLat() + "," + upcomingTask.getLng();
                    URL url = new URL(strUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.connect();
                    int statuscode = con.getResponseCode();
                    if (statuscode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();
                        while (line != null) {
                            sb.append(line);
                            line = br.readLine();
                        }
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
                        return root.getString("destination_addresses") + "/" + object_duration.getString("text");

                    }
                } catch (MalformedURLException e) {
                    Log.d("error", "error1");
                } catch (IOException e) {
                    Log.d("error", "error2");
                } catch (JSONException e) {
                    Log.d("error", "error3");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            int cutOff = 0;
            String time = null;


            if (result != null) {

                for (int i = 0; i < result.length(); i++) {

                    if (result.substring(i, i + 1).equals("/")) {

                        cutOff = i;

                    }


                }


            }


        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateBackground();




        /*

        Setting a broadcastReceiver to get the value of the service started before to retrieve the current location GPS_Service,
        getting the lat and lng value.
         */
        if (broadcastReceiver == null) {

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    currentLng = intent.getDoubleExtra("lng", 0);
                    currentLat = intent.getDoubleExtra("lat", 0);

                    DistanceData distanceData = new DistanceData();
                    UpcomingTasks upcomingTasks = new UpcomingTasks();
                    TodaysTasks todaysTasks = new TodaysTasks();

                    distanceData.execute();
                    upcomingTasks.execute();
                    todaysTasks.execute();


                }
            };
        }

        //Registering the receiver with the intent filter to extract data easily
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {

            unregisterReceiver(broadcastReceiver);
        }
    }

    private boolean runtimePermission() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;

        }

        return false;

    }

    /**
     * Checking for user permission to access location.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {


            } else {

                runtimePermission();
            }

        }

    }


}

