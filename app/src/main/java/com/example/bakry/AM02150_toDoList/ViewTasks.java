package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/5/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * ViewTasks Activity that pops up for the user to display a list of all Tasks. Enables the user to
 * make a selection to one of the tasks or delete . Sorts the Tasks accordingly with appropriate headers.
 */
public class ViewTasks extends AppCompatActivity {

    private ListView tasksList = null;
    private ArrayList<Task> allTasks = null;
    private ArrayList<Task> completedTasks = null;
    private SeparatedListAdapter mainAdapter;
    private TextView deleteMessage = null;
    private Button deleteButton = null;
    private Button cancelButton = null;
    private ArrayList<Integer> inNDays = null;
    private Button homeButton = null;
    private long size = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks);



        /*  Initialising the ListView to its relative IDs in layout.  */
        tasksList = (ListView) findViewById(R.id.lv2);


        /*  Setting the value of inNDays List by making a call on the SharedPreferences Object.  */


        /*  Setting the mainAdapter to the SeparatedList adapter.  */
        mainAdapter = new SeparatedListAdapter(ViewTasks.this);

        homeButton = (Button) findViewById(R.id.backButtonId);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startMainAc = new Intent(ViewTasks.this, MainActivity.class);
                startMainAc.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(startMainAc);

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



        /*  Setting the tasksList to the mainAdapter to display Tasks.  */
        tasksList.setAdapter(mainAdapter);


        /** OnClickListener for the taskList to detect the task pressed in the List. */
        tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



                /** Setting the values of the allTasks and the completedTasks to the ones saved in the SharedPreferences. *



                /*  Initialising the field that has the position of the task in either List. */
                int offset = mainAdapter.getOffset(i);

                int x = 0;

                if (inNDays.size() == 0) {

                    x = 1;

                } else {

                    x = inNDays.size() + 1;
                }

                /**
                 * If Statement that checks if the task is one that is Completed or Not by checking against
                 * the key = 181999.
                 */
                if (offset == 181999) {


                    /**
                     * Gets the relevant Completed Task and passes the data to the intent that starts
                     * the EditTask Activity. The status is set as true (task is complete).
                     */
                    Task task = completedTasks.get(i - (x  + allTasks.size()) );


                    Intent startEditTaskAc = new Intent(ViewTasks.this, EditTask.class);
                    startEditTaskAc.putExtra("position", i);
                    startEditTaskAc.putExtra("status", true);
                    startEditTaskAc.putExtra("taskId", task.getId());
                    startActivity(startEditTaskAc);


                } else {

                    /**
                     * Gets the relevant Task and passes the data to the intent that starts
                     * the EditTask Activity. The status is set as false (task is incomplete).
                     */
                    Task task = allTasks.get(offset);
                    Intent startEditTaskAc = new Intent(ViewTasks.this, EditTask.class);
                    startEditTaskAc.putExtra("position", offset);
                    startEditTaskAc.putExtra("status", false);
                    startEditTaskAc.putExtra("taskId", task.getId());
                    startActivity(startEditTaskAc);


                }


            }

        });

        /**
         * OnClickListener for the taskList to detect the task long pressed in the List,
         * that performs a delete for the task.
         * */
        tasksList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {


                /**
                 * Creating a LayoutInflater that has the delete AlertDialog to be displayed.
                 */
                LayoutInflater layoutInflater = LayoutInflater.from(ViewTasks.this);
                View promptView = layoutInflater.inflate(R.layout.delete_dialog, null);
                AlertDialog alertDialog = new AlertDialog.Builder(ViewTasks.this).create();


                /*  Initialising the TextViews to their relative IDs in layout.  */
                deleteMessage = (TextView) promptView.findViewById(R.id.deleteMessageId);


                /*  Initialising the Buttons to their relative IDs in layout.  */
                deleteButton = (Button) promptView.findViewById(R.id.deleteButtonId);
                cancelButton = (Button) promptView.findViewById(R.id.cancelButtonId);


                /** Setting the view of the Dialog then displaying it.  */
                alertDialog.setView(promptView);
                alertDialog.show();

                /** OnClickListener for the deleteButton which performs the delete action on the Task list. */
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int offset = mainAdapter.getOffset(index);


                        if (offset == 181999) {


                            int x = 0;

                            if (inNDays.size() == 0) {

                                x = 1;

                            } else {

                                x = inNDays.size() + 2;
                            }


                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("tasks");

                            /** Initialising the field that has the position of the task in the either List, then deleting it.  */
                            myRef.child(allTasks.get(index - x).getId()).removeValue();





                            /* Showing a Toast that the task was deleted. */
                            Toast.makeText(ViewTasks.this, "Task deleted successfully!",
                                    Toast.LENGTH_LONG).show();


                            /*  Hiding the Dialog again. */
                            alertDialog.dismiss();

                            /*  Refreshing the Activity to show the updates. Task should be added to the complete section at the bottom.  */
                            finish();
                            startActivity(getIntent());
                        } else {



                            /** Initialising the field that has the position of the task in the either List, then deleting it.  */
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("tasks");

                            /** Initialising the field that has the position of the task in the either List, then deleting it.  */
                            myRef.child(allTasks.get(offset).getId()).removeValue();//                            sp.saveTasks(getBaseContext(), allTasks);

                            /* Showing a Toast that the task was deleted. */
                            Toast.makeText(ViewTasks.this, "Task deleted successfully!",
                                    Toast.LENGTH_LONG).show();


                            /*  Hiding the Dialog again. */
                            alertDialog.dismiss();

                            /*  Refreshing the Activity to show the updates. Task should be added to the complete section at the bottom.  */
                            finish();
                            startActivity(getIntent());


                        }


                    }
                });

                /** OnClickListener for the cancelbutton which cancels the operation. */
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();


                    }
                });


                return true;
            }
        });

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            allTasks = new ArrayList<>();


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


                    allTasks.add(task1);

            }

            inNDays = arrangeDates(allTasks);


            /**
             * For Loop that adds the relevant sections in the mainAdapter. Goes through inNDays List which
             * includes headers.
             */
            for (int i = 0; i < inNDays.size(); i++) {

                /*  Checks if the dueDate is Today. */
                if (inNDays.get(i) == 0) {

                    /** Creating a new adapter that holds the tasks for Today then adds it to its section in the mainAdapter. */
                    ArrayAdapter<Task> listadapter = new ViewTasksArrayAdapter(ViewTasks.this, R.layout.view_tasks_listview_layout, getTasksIn(allTasks, inNDays.get(i)));
                    mainAdapter.addSection("Today", listadapter);


                } else {

                    /** Creating a new adapter that holds the tasks for the specific due date to be added to the section in the mainAdapter. */
                    ArrayAdapter<Task> listadapter = new ViewTasksArrayAdapter(ViewTasks.this, R.layout.view_tasks_listview_layout, getTasksIn(allTasks, inNDays.get(i)));
                    mainAdapter.addSection("In " + inNDays.get(i) + " days", listadapter);

                }


            }

            /*  Setting the tasksList to the mainAdapter to display Tasks.  */
            tasksList.setAdapter(mainAdapter);


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };

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





            /** If Statement that chekcs if the Completed Tasks list is not empty. */
            if (completedTasks.size() != 0) {

                /** Creating a new adapter that holds the completed tasks then adds it to its section in the mainAdapter. */
                ArrayAdapter<Task> listadapter = new ViewTasksArrayAdapter(ViewTasks.this, R.layout.view_tasks_listview_layout, completedTasks);
                mainAdapter.addSection("Complete", listadapter);
            }

            /*  Setting the tasksList to the mainAdapter to display Tasks.  */
            tasksList.setAdapter(mainAdapter);


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };


    public ArrayList<Task> getTasksIn(ArrayList<Task> allTasks, int n) {

        ArrayList<Task> tasksOnDay = new ArrayList<>();
        Calendar today = Calendar.getInstance();


        for (int j = 0; j < allTasks.size(); j++) {


            int to = today.get(Calendar.DAY_OF_MONTH);
            int so = allTasks.get(j).getDueDate().get(Calendar.DAY_OF_MONTH);

            int difference = so - to;

            if (n == difference) {

                tasksOnDay.add(allTasks.get(j));

            }
        }
        return tasksOnDay;


    }

    public ArrayList<Integer> arrangeDates(ArrayList<Task> allTasks) {

        Calendar todaysDate = Calendar.getInstance();

        List<Integer> inNDays = new ArrayList<>();
        List<Integer> inNDaysF = new ArrayList<>();


        int checkValue = 0;


        /**
         * Initialising and setting the fields of Today's date into (day, month, year),
         * along with the todayTasks variable that hold the total number of tasks due for today.
         */
        int dayToday = todaysDate.get(Calendar.DAY_OF_MONTH);
        int monthToday = todaysDate.get(Calendar.MONTH) + 1;
        int yearToday = todaysDate.get(Calendar.YEAR);

        for (int i = 0; i < allTasks.size(); i++) {

            int dayTask = allTasks.get(i).getDueDate().get(Calendar.DAY_OF_MONTH);
            int monthTask = allTasks.get(i).getDueDate().get(Calendar.MONTH);
            int yearTask = allTasks.get(i).getDueDate().get(Calendar.YEAR);

            inNDays.add(dayTask - dayToday);


        }

        Collections.sort(inNDays);


        for (int i = 0; i < inNDays.size(); i++) {

            if (inNDaysF.size() == 0) {


                inNDaysF.add(inNDays.get(0));


            } else {

//                if (inNDaysF != null) {

                checkValue = 0;

                for (int j = 0; j < inNDaysF.size(); j++) {


                    if (inNDays.get(i) != inNDaysF.get(j)) {

                        checkValue++;


                    }


                }

                if (checkValue == inNDaysF.size()) {

                    inNDaysF.add(inNDays.get(i));


                }


            }
        }


        return (ArrayList<Integer>) inNDaysF;


    }



}
