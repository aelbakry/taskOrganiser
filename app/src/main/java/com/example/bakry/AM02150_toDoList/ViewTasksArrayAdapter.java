package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/5/18.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * viewTasksArrayAdapter manages the creation of the sections of adapters present in the SeparatedListAdapter
 * in the list in ViewTasks Activity. Manages the layout of the Tasks itself.
 */
public class ViewTasksArrayAdapter extends ArrayAdapter<Task> {

    private int layoutResourceId = 0;
    private ArrayList<Task> tasks = null;
    private ProgressBar taskProgress = null;
    TextView taskOverview = null;
    TextView taskDueDate = null;


    public ViewTasksArrayAdapter(Context context, int layoutResourceId, ArrayList<Task> tasks) {
        super(context, layoutResourceId, tasks);

        this.layoutResourceId = layoutResourceId;
        this.tasks = tasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /** Creating a LayoutInflater that for the task items in the list displayed along with the view.  */
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.view_tasks_listview_layout, parent, false);

        /*  Initialising the ProgressBar to its relative ID in layout.  */
        taskProgress = (ProgressBar) rowView.findViewById(R.id.taskProgressId);

        /*  Initialising and setting progress which holds the Task's progress*/
        double progress = 0;

        /**
         * For loop that checks each step in the Task if it is complete, and updates the progress variable
         * accordingly.
         */
        for (int i = 0; i < tasks.get(position).getSteps().size(); i++) {

            /*  If Statement that checks if the current Step is complete*/
            if (tasks.get(position).getSteps().get(i).getIsComplete()) {

                /* Add one to the progress */
                progress++;

            }


        }

        /*  Initialising the variable stepSize which holds the number of steps for each task.  */
        double stepSize = tasks.get(position).getSteps().size();


        /*  Calculating the progress  */
        double s = (progress / stepSize) * 100;
        int x = (int) s;


        /*  Setting and displaying the progress of the Task for the progress bar.  */
        taskProgress.setProgress(x);

        /**
         *  Initialising a Calendar Object which holds the Due Date of a specific task to be
         *  displayed in Activity and setting the values accordingly.
         */
        Calendar dueDate = tasks.get(position).getDueDate();
        String display = dueDate.get(Calendar.DAY_OF_MONTH) + "  -  " + dueDate.get(Calendar.MONTH) + "  -  " + dueDate.get(Calendar.YEAR);


        /*  Initialising the TextViews to their relative IDs in layout.  */
        taskOverview = (TextView) rowView.findViewById(R.id.taskOverviewId);
        taskDueDate = (TextView) rowView.findViewById(R.id.taskDueDateId);


        /*  Setting the values in the TextView accordingly  */
        taskOverview.setText(tasks.get(position).getOverview());
        taskDueDate.setText(display);


        return rowView;
    }

    @Nullable
    @Override
    public Task getItem(int position) {

         Task task = super.getItem(position);
         System.out.print(task.getNumberOfSteps());




         return super.getItem(position);
    }
}
