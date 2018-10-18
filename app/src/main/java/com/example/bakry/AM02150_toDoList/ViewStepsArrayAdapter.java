package com.example.bakry.AM02150_toDoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/5/18.
 */

/**
 * ViewStepsArrayAdapter manages the creation of the items in the list in ViewSteps Activity. Displays
 * button of a Step in the list according to its status. Updates the steps for the Task through SharedPreferences.
 */
public class ViewStepsArrayAdapter extends ArrayAdapter<Step> {

    private int layoutResourceId;
    private TextView stepName = null;
    private TextView stepData = null;
    private ArrayList<Step> steps = new ArrayList<>();
    private ArrayList<String> stepsNames = new ArrayList<>();
    private ArrayList<String> stepsData = new ArrayList<>();


    public ViewStepsArrayAdapter(Context context, int layoutResourceId, ArrayList<Step> steps) {
        super(context, layoutResourceId, steps);

        this.layoutResourceId = layoutResourceId;
        this.steps = steps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.edit_steps_listview, parent, false);

        /**
         * For Loop that gets the Steps names and data to be displayed by the TextViews from the steps
         * initialised by the constructor.
         */
        for (int i = 0; i < steps.size(); i++) {

            stepsNames.add(steps.get(i).getName());
            stepsData.add(steps.get(i).getContent());

        }


        /*  Initialising the TextViews to their relative IDs in layout.  */
        stepName = (TextView) rowView.findViewById(R.id.stepNameId);
        stepData = (TextView) rowView.findViewById(R.id.stepContentId);

        /*  Setting the TextViews to their relative values.  */
        stepName.setText(stepsNames.get(position));
        stepData.setText(stepsData.get(position));

        /*  Initialising the Button to its relative IDs in layout.  */
        Button isTaskCompleteButton = (Button) rowView.findViewById(R.id.isCompleteId);


        /**
         * Checks if the step for the task is marked as complete and sets the button's layout
         * respectively, if not the default layout is set already.
         */
        if( steps.get(position).getIsComplete()){

            isTaskCompleteButton.setText("✓");
            isTaskCompleteButton.setBackgroundResource(R.drawable.edit_step_complete_button);

        }


        /**
         * OnClickListener for the button to mark a task as complete, and updates the steps with the
         * result. Respectively updating the relevant task.
         */
        isTaskCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isTaskCompleteButton.setText(R.string.check_mark);
                isTaskCompleteButton.setBackgroundResource(R.drawable.edit_step_complete_button);
                steps.get(position).setIsComplete(true);
//                sp.updateSteps(ViewStepsArrayAdapter.super.getContext(), taskId, steps);


            }
        });


        return rowView;
    }


}

