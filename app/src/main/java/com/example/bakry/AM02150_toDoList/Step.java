package com.example.bakry.AM02150_toDoList;

/**
 * Created by Ahmed Mohamed (bakry)  on 2/17/18.
 */

/**
 *  Step Class that manages the creation and the maintenance of the Steps objects.
 */
public class Step {


    private String name = null;
    private String content = null;
    private boolean isComplete = false;




    /**
     * Getter for the content of the step.
     *
     * @return this.content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Getter for isComplete.
     *
     * @return this.isComplete
     */
    public boolean getIsComplete() {
        return this.isComplete;
    }


    /**
     * Setter for the content of the steps.
     *
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Setter for the name of the step.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for the name of the step.
     *
     * @return this.name
     */
    public String getName() {
        return this.name;
    }



    /**
     * Setter for the isComplete.
     *
     * @param complete
     */
    public void setIsComplete(boolean complete) {
        isComplete = complete;
    }


}


