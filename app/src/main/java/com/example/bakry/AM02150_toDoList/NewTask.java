package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/5/18.
 */

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NewTask extends AppCompatActivity {

    private Button bAddTask = null;
    private Button bAddLocation = null;
    private EditText etOverview = null;
    private DatePicker datePicker = null;
    private Calendar dueDate = null;
    private Task task = null;
    private ArrayList<Task> tasks = new ArrayList<>();
    private Spinner numberOfSteps = null;
    private FloatingActionButton editSteps = null;
    private ArrayList<Task> allTasks = null;
    private ArrayList<String> numberOfStepsList = null;
    private ArrayList<String> stepsData = null;
    private ArrayList<String> stepsNames = null;
    private double lat = 0.0;
    private double lng = 0.0;
    private Location location = null;
    private int code = 0;
    private Button homeButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);


        /*  Initialising the DatePicker to its relative ID in layout.  */
        this.datePicker = (DatePicker) findViewById(R.id.datePicker);


        /*  Initialising the TextViews to their relative IDs in layout.  */
        this.etOverview = (EditText) findViewById(R.id.etOverviewID);


        /*  Initialising the Buttons to their relative IDs in layout.  */
        this.bAddTask = (Button) findViewById(R.id.bAddNewTaskID);
        this.bAddLocation = findViewById(R.id.bAddLocationId);
        this.editSteps = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        /*  Initialising the Spinner to its relative ID in layout  */
        this.numberOfSteps = (Spinner) findViewById(R.id.numberOfStepsID);


        /*  Initialising the List that holds the values for the spinner.  */
        numberOfStepsList = new ArrayList<>();
        numberOfStepsList.add("number of");

        /*  For Loop that is limited to only 10 steps Maximum (Could be easily modified)  */
        for (int i = 0; i < 11; i++) {

            numberOfStepsList.add(Integer.toString(i));
        }

        homeButton = (Button) findViewById(R.id.backButtonId);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startMainAc = new Intent(NewTask.this, MainActivity.class);
                startMainAc.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(startMainAc);

            }
        });


        /*  Creating an Array Adapter that displays the list of the items in the Spinner.  */
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, numberOfStepsList) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    /*  Disable the first item in the spinner as it is a hint.  */
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {


                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {

                    /*  Setting the color of the hint item to be gray.   */
                    tv.setTextColor(Color.GRAY);
                } else {

                    /*  Setting the color of all the other items in the Spinner to Black.  */
                    tv.setTextColor(Color.BLACK);
                }

                return view;

            }

        };

        /*  Setting the Spinner to the spinnerArrayAdapter customized above.  */
        numberOfSteps.setAdapter(spinnerArrayAdapter);


        /**
         * Setting onClick Listener on bSetSteps button to implement action when pressed.
         *
         */
        this.editSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!numberOfSteps.getSelectedItem().toString().equals("number of")) {


                    /**
                     * If statement that checks if the number of Steps is greater than 0.
                     */
                    if (Integer.valueOf(numberOfSteps.getSelectedItem().toString()) > 0) {


                        /**
                         *  Initialising the intent which is to start NewSteps Activity and passing
                         *  to it the number of steps. Then the activity is started for result; which is
                         *  each of the Step object.
                         */
                        Intent startNewStepsAc = new Intent(NewTask.this, NewSteps.class);
                        startNewStepsAc.putExtra("numberOfSteps", Integer.valueOf(numberOfSteps.getSelectedItem().toString()));
                        startActivityForResult(startNewStepsAc, 1);

                    }

                } else {

                    Toast.makeText(NewTask.this, "Please make a selection for the Number of Steps!",
                            Toast.LENGTH_SHORT).show();


                }
            }


        });


        this.bAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etOverview.getText().toString().matches("")) {

                    Animation shake = AnimationUtils.loadAnimation(NewTask.this, R.anim.shake);
                    etOverview.startAnimation(shake);


                } else if (numberOfSteps.getSelectedItem() == "number of") {

                    Animation shake = AnimationUtils.loadAnimation(NewTask.this, R.anim.shake);
                    numberOfSteps.startAnimation(shake);
                    numberOfSteps.setBackgroundResource(R.drawable.background_empty_spinner);

                } else {

                    /**
                     * Initialising and setting the fields that hold the date marked by the user in the
                     * DatePicker, then setting the dueDate to the DatePicker's values.
                     */
                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth() + 1;
                    int year = datePicker.getYear();

                    Calendar dueDate = new GregorianCalendar();

                    dueDate.set(Calendar.YEAR, year);             // August  16th,    0 AD
                    dueDate.set(Calendar.DAY_OF_MONTH, day);      // January  1st,    0 AD
                    dueDate.set(Calendar.MONTH, month);


                    /*  Getting the text in the etOverview EditText and storing it locally.  */
                    String overview = etOverview.getText().toString();



                    if (location == null) {


                        Toast.makeText(NewTask.this, "Add location first!",
                                Toast.LENGTH_LONG).show();

                    } else if (stepsNames == null) {

                        Toast.makeText(NewTask.this, "Set steps first!",
                                Toast.LENGTH_LONG).show();
                    } else {

                        int stepSize = Integer.valueOf(numberOfSteps.getSelectedItem().toString());
                        ArrayList<Step> steps = setSteps(stepsNames, stepsData, ((1 / stepSize) * 100));

                        addTask(dueDate, overview, steps.size(), steps, location);


                        Toast.makeText(NewTask.this, "Task added successfully!",
                                Toast.LENGTH_LONG).show();

                        /**
                         *  Initialising and then starting the intent which is to start MainActivity Activity.
                         */
                        Intent startMainAc = new Intent(NewTask.this, MainActivity.class);
                        startMainAc.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(startMainAc);


                    }

                    /*  Adding the newly created task to the list in SharedPreferences by making a call on the instance */


                }


            }
        });


        bAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /**
                 *  Initialising and then starting the intent which is to start MainActivity Activity.
                 */
                Intent startNewLocation = new Intent(NewTask.this, NewLocation.class);
                startActivityForResult(startNewLocation, 2);


            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first


    }

    public void addTask(Calendar dueDate, String overview, int numberOfSteps, ArrayList<Step> steps, Location location) {


        /**
         * Createing a firebase object to add the task that was just created
         */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference table = database.getReference("tasks");
        DatabaseReference newTask = table.push();


        newTask.child("due_date").child("day").setValue(dueDate.get(Calendar.DAY_OF_MONTH));
        newTask.child("due_date").child("month").setValue(dueDate.get(Calendar.MONTH));
        newTask.child("due_date").child("year").setValue(dueDate.get(Calendar.YEAR));

        newTask.child("overview").setValue(overview);
        newTask.child("number_of_steps").setValue(numberOfSteps);
        newTask.child("steps_list").setValue(steps);
        newTask.child("is_complete").setValue(false);


        newTask.child("location").child("lat").setValue(location.getLatitude());
        newTask.child("location").child("alt").setValue(location.getAltitude());


    }

    /**
     * Method that sets the steps for a specific task.
     *
     * @param stepNames
     * @param stepData
     * @param weight
     */
    public ArrayList<Step> setSteps(ArrayList<String> stepNames, ArrayList<String> stepData, double weight) {

        ArrayList<Step> steps = new ArrayList<>(stepNames.size());

        Step s = new Step();


        /**
         * For Loop that adds each of the individual data received in the parameters into a step object
         * realted to the Task.
         */
        for (int i = 0; i < stepNames.size(); i++) {


            steps.add(s);
            steps.get(i).setName(stepNames.get(i));
            steps.get(i).setContent(stepData.get(i));



        }

        return steps;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == NewTask.RESULT_OK) {


                stepsData = data.getStringArrayListExtra("stepsData");
                stepsNames = data.getStringArrayListExtra("stepsNames");


            }
            if (resultCode == NewTask.RESULT_CANCELED) {


            }


        } else if (requestCode == 2) {

            if (resultCode == NewTask.RESULT_OK) {

                location = new Location("");


                location.setLatitude(data.getDoubleExtra("lat", 0));
                location.setAltitude(data.getDoubleExtra("lng", 0));


            }
            if (resultCode == NewTask.RESULT_CANCELED) {


            }


        }
    }


}
