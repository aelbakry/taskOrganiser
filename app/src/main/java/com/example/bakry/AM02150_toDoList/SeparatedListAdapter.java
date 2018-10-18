package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry) on 3/9/18.
 */

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;


/**
 * SeparatedListAdapter manages the creation of the whole list along with ViewTasksArrayAdapter. Manages
 * the layout of the headers for sections.
 */
public class SeparatedListAdapter extends BaseAdapter {

    public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
    public final ArrayAdapter<String> headers;
    public final static int TYPE_SECTION_HEADER = 0;

    public SeparatedListAdapter(Context context) {
        headers = new ArrayAdapter<String>(context, R.layout.list_header);
    }

    /**
     *
     * @param section
     * @param adapter
     */
    public void addSection(String section, Adapter adapter) {


        this.headers.add(section);
        this.sections.put(section, adapter);

    }

    /**
     *
     * @param position
     * @return
     */
    public Object getItem(int position) {

        return null;
    }

    /**
     * Method that takes in the position that is given by the ListView and modifies it
     * to be the positionInList which is the actual position of the task in the allTasks list or
     * in the completedTasks list.
     *
     * @param position
     * @return
     */
    public int getOffset(int position) {


        int positionInList = position;


        /** Foreach Loop that adjusts the positionInList variable accordingly. */
        for (Object section : this.sections.keySet()) {

            /*  Getting the size of the current adapter.  */
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;


            if (!section.equals("Today") && !section.equals("Complete")) {

                if (position <= size) {

                    positionInList--;
                    return positionInList;

                } else if (position > size) {


                    position = (position - size);
                    positionInList--;

                }


                // otherwise jump into next section
            } else if (section.equals("Complete")) {


                positionInList = 181999;
                return positionInList;

            } else {

                if (position <= size) {

                    positionInList--;

                    return positionInList;

                }

                position = (position - size);
                positionInList--;
            }

        }

        return positionInList;
    }


    /**
     *
     * @return total
     */
    public int getCount() {
        // total together all sections, plus one for each section header
        int total = 0;
        for (Adapter adapter : this.sections.values())
            total += adapter.getCount() + 1;
        return total;
    }

    @Override
    public int getViewTypeCount() {
        // assume that headers count as one, then total all sections
        int total = 1;
        for (Adapter adapter : this.sections.values())
            total += adapter.getViewTypeCount();
        return total;
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) return TYPE_SECTION_HEADER;
            if (position < size) return type + adapter.getItemViewType(position - 1);

            // otherwise jump into next section
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionnum = 0;
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) return headers.getView(sectionnum, convertView, parent);
            if (position < size) return adapter.getView(position - 1, convertView, parent);

            // otherwise jump into next section
            position -= size;
            sectionnum++;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}