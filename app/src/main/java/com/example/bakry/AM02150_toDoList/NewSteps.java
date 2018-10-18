package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/5/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewSteps extends AppCompatActivity {

    private Button stepsComplete = null;
    private ArrayList<Step> steps = new ArrayList<>();
    private ArrayList<String> stepsNames = new ArrayList<>();
    private ArrayList<String> stepsData = new ArrayList<>();
    private TextView stepTitle = null;
    private ListView stepsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_steps);



        /*  Initialising the ListView to its relative IDs in layout.  */
        stepsList = (ListView) findViewById(R.id.stepsListView);


        /*  Initialising the Button to its relative ID in layout.  */
        stepsComplete = (Button) findViewById(R.id.button);

        /*  Initialising the TextView to its relative IDs in layout.  */
        stepTitle = (TextView) findViewById(R.id.stepsTitleId2);


        /**
         * Creating an instance Intent then getting the value passed by the previous Activity,
         * which has the number of steps.
         */
        Intent myIntent = getIntent();
        int numberOfSteps = myIntent.getIntExtra("numberOfSteps", 0);


        /**
         * For Loop that gives each of the steps a default name following the format:
         * Step  + order.
         */
        for (int i = 1; i <= numberOfSteps; i++) {

            Step newStep = new Step();
            newStep.setName("Step " + i);
            stepsNames.add("Step " + i);
            steps.add(newStep);

        }


        /*   Initialising the resourceId with the layout of the Steps.  */
        int resourceId = getResources().getIdentifier("steps_layout", "layout", getPackageName());

        /*  Creating an adapter that displays the list of the Steps and their stepNames.  */
        final NewStepsArrayAdapter adapter = new NewStepsArrayAdapter(this, resourceId, stepsNames);
        stepsList.setAdapter(adapter);

        /**
         * OnClickListener for the stepsComplete button, updates the Task's steps and returns the result
         * for Activity NewTask.
         */
        stepsComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*  For Loop that adds the information in the editText   */
                for (int i = 0; i < steps.size(); i++) {

                    View view1 = stepsList.getChildAt(i);
                    EditText stepContent = view1.findViewById(R.id.stepData);
                    steps.get(i).setContent(stepContent.getText().toString());
                    stepsData.add(stepContent.getText().toString());

                }

                /**
                 * Creating an Intent that returns back from the result to the previous Activity (NewTask)
                 * and passes the stepsData and StepsNames to be retrieved.
                 */
                Intent returnIntent = new Intent();
                returnIntent.putStringArrayListExtra("stepsNames", stepsNames);
                returnIntent.putStringArrayListExtra("stepsData", stepsData);
                setResult(NewTask.RESULT_OK, returnIntent);
                finish();


            }
        });


    }


}
