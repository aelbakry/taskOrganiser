package com.example.bakry.AM02150_toDoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/5/18.
 */

/**
 * ViewStepsArrayAdapter manages the creation of the items in the list in ViewSteps Activity. Displays
 * button of a Step in the list according to its status. Updates the steps for the Task through SharedPreferences.
 */
public class NewStepsArrayAdapter extends ArrayAdapter<String>{

    private int layoutResourceId;
    private ArrayList<String> values;
    private TextView stepNumber = null;



    public NewStepsArrayAdapter(Context context, int layoutResourceId, ArrayList<String> values)
    {
        super(context, layoutResourceId, values);

        this.layoutResourceId = layoutResourceId;
        this.values = values;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent)
    {
        /** Creating a LayoutInflater that for the step items in the list displayed along with its view.  */
        LayoutInflater inflater = ( LayoutInflater )this.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View rowView = inflater.inflate( R.layout.steps_layout, parent, false );


        /*  Initialising the TextView to its relative IDs in layout and setting its value accordingly.  */
        stepNumber = ( TextView )rowView.findViewById( R.id.stepNumber );
        stepNumber.setText( values.get( position ) );


        return rowView;
    }


}
