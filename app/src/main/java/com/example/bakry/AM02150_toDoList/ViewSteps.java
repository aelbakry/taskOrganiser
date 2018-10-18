package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/5/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * ViewSteps Activity that pops up for the user when viewing the steps for a task. Enables the
 * user to mark a Step as complete. The data is updated through the ViewStepsArrayAdapter in
 * the SharedPreference Class.
 */
public class ViewSteps extends AppCompatActivity {


    private ArrayList<Step> steps = null;
    private Task thisTask = null;
    private ListView stepsList;
    private Button doneButton = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_steps);

        steps = new ArrayList<>();


        doneButton = (Button) findViewById(R.id.doneButtonId);


        /*  Creating an intent to get the position of the task to be used later for updating task's fields  */
        Intent startEditStepsAc = getIntent();
        String taskId = startEditStepsAc.getStringExtra("taskId");


        Query querySteps = FirebaseDatabase.getInstance().getReference("tasks").child(String.valueOf(taskId));
        querySteps.addListenerForSingleValueEvent(valueEventListener);

        stepsList = (ListView) findViewById(R.id.listviewId);

        int resourceId = getResources().getIdentifier("edit_steps_listview.xml", "layout", getPackageName());
        ViewStepsArrayAdapter adapter = new ViewStepsArrayAdapter(this, resourceId, steps);
        stepsList.setAdapter(adapter);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startEditTaskAc = new Intent(ViewSteps.this, EditTask.class);
                startActivity(startEditTaskAc);

            }
        });

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            for (DataSnapshot stepsSnapshot : dataSnapshot.child("steps_list").getChildren()) {

                Step step = stepsSnapshot.getValue(Step.class);
                steps.add(step);

            }

            int resourceId = getResources().getIdentifier("edit_steps_listview.xml", "layout", getPackageName());
            ViewStepsArrayAdapter adapter = new ViewStepsArrayAdapter(ViewSteps.this, resourceId, steps);
            stepsList.setAdapter(adapter);





        } @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };


}
