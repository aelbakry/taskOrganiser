package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/5/18.
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * EditTask Activity that pops up for the user when selected a task from the ViewTasks Activity. Enables
 * the user to mark a task as complete and update its data structures accordingly. Displays the Tasks
 * details including the progress of Steps completed.
 */
public class EditTask extends AppCompatActivity {

    private TextView overviewTextView = null;
    private Button viewStepsButton = null;
    private Button taskCompletedButton = null;
    private Button viewLocationButton = null;
    private TextView dayDisplay = null;
    private TextView monthDisplay = null;
    private TextView yearDisplay = null;
    private TextView stepsComplete = null;
    private ProgressBar taskProgress = null;
    private double progress = 0;
    private ArrayList<Task> allTasks = new ArrayList<>();
    private Task task = null;
    private BroadcastReceiver broadcastReceiver;
    private GoogleMap mMap;
    private double currentLng = 0;
    private double currentLat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);



        /*  Initialising the TextViews to their relative IDs in layout.  */
        this.stepsComplete = (TextView) findViewById(R.id.footerId2);
        this.overviewTextView = (TextView) findViewById(R.id.overviewData);


        /*  Initialising the Buttons to their relative IDs in layout.  */
        this.viewStepsButton = (Button) findViewById(R.id.viewStepsID);
        this.taskCompletedButton = (Button) findViewById(R.id.taskCompletedId);
        this.viewLocationButton = findViewById(R.id.checkLocationButtonId);


        /*  Initialising the ProgressBar to its relative ID in layout.  */
        this.taskProgress = (ProgressBar) findViewById(R.id.progressBar2);

        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);

        runtimePermission();



        /*  Initialising the position and status of the Task object to be obtained from the Intent  */
        String taskId = String.valueOf(getIntent().getStringExtra("taskId"));



        Query queryComplete = FirebaseDatabase.getInstance().getReference("tasks").child(String.valueOf(taskId));
        queryComplete.addListenerForSingleValueEvent(valueEventListener);

        viewLocationButton.setEnabled(false);

        viewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                Intent startEditStepsAc = new Intent(EditTask.this, ViewLocation.class);
                startEditStepsAc.putExtra("taskId", taskId);
                startEditStepsAc.putExtra("lat", currentLat);
                startEditStepsAc.putExtra("lng", currentLng);


                startActivity(startEditStepsAc);



            }
        });



        /**
         * OnClickListener for the ViewSteps button, starts ViewSteps Activity, and passes the Task's
         * position through the Intent.
         */
        viewStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startEditStepsAc = new Intent(EditTask.this, ViewSteps.class);
                startEditStepsAc.putExtra("taskId", taskId);

                startActivity(startEditStepsAc);


            }
        });

        /**
         * OnClickListener for the taskCompletedButton, marks a Task as complete and starts
         * the ViewTasks Activity.
         */
        taskCompletedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*  Displaying a Toast when the Task is marked as complete */
                Toast.makeText(EditTask.this, "Task marked as complete!",
                        Toast.LENGTH_SHORT).show();

                Intent startViewTasksAc = new Intent(EditTask.this, ViewTasks.class);
                startActivity(startViewTasksAc);


            }
        });


        /*  Initialising the TextViews that hold the Due Date to their relative IDs in layout.  */
        dayDisplay = (TextView) findViewById(R.id.dueDateDataId);
        monthDisplay = (TextView) findViewById(R.id.dueDateDataId2);
        yearDisplay = (TextView) findViewById(R.id.dueDateDataId3);





    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            task = new Task();


            ArrayList<Step> steps = new ArrayList<>();
            Task task1 = dataSnapshot.getValue(Task.class);
            Calendar dueDate = new GregorianCalendar();

            overviewTextView.setText(task1.getOverview());

            for (DataSnapshot stepsSnapshot : dataSnapshot.child("steps_list").getChildren()) {

                Step step = stepsSnapshot.getValue(Step.class);
                steps.add(step);

                }



                if(dataSnapshot.hasChild("due_date/day") && dataSnapshot.hasChild("due_date/month")
                        && dataSnapshot.hasChild("due_date/year")) {

                    dayDisplay.setText(Integer.toString(dataSnapshot.child("due_date/day").getValue(Integer.class)));
                    monthDisplay.setText(Integer.toString(dataSnapshot.child("due_date/month").getValue(Integer.class)));
                    yearDisplay.setText(Integer.toString(dataSnapshot.child("due_date/year").getValue(Integer.class)));
                }


                task1.setId(dataSnapshot.getKey());

                task = task1;

                if(steps != null) {


                    /**
                     * For loop that checks each step in the Task if it is complete, and updates the progress variable
                     * accordingly.
                     */
                    for (int i = 0; i < steps.size(); i++) {

                        /*  If Statement that checks if the current Step is complete*/
                        if (steps.get(i).getIsComplete()) {


                            /* Add one to the progress */
                            progress++;

                        }


                    }
                }

            /*  Initialising the variable stepSize which holds the number of steps for each task.  */


            /*  Calculating the progress  */
            double s = (progress / steps.size()) * 100;
            int x = (int) s;


            /*  Setting and displaying the progress of the Task for both the progress bar and the TextView displaying progress  */
            taskProgress.setProgress(x);
            stepsComplete.setText(progress + "  out of  " + steps.size() + " steps complete");


            }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };

    @Override
    protected void onResume() {
        super.onResume();

        if(broadcastReceiver == null){

            broadcastReceiver= new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    currentLng = intent.getDoubleExtra("lng", 0);
                    currentLat = intent.getDoubleExtra("lat", 0);


                        viewLocationButton.setEnabled(true);





                }
            };
        }

        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));






    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(broadcastReceiver != null){

            unregisterReceiver(broadcastReceiver);
        }
    }

    private boolean runtimePermission(){

        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;

        }

        return false;

    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ){


            } else {

                runtimePermission();
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("overview", overviewTextView.getText().toString());
        savedInstanceState.putString("day", dayDisplay.getText().toString());
        savedInstanceState.putString("month", monthDisplay.getText().toString());
        savedInstanceState.putString("year", yearDisplay.getText().toString());
        savedInstanceState.putDouble("progress", taskProgress.getProgress());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }



}
