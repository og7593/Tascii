package com.eecs285.project4;

/**
 * This activity class represents changing the state of a task. Can make it
 * 'Done', 'Not started', or 'In progress'
 */

import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ChangeTaskStateScreen extends Activity {
    
    // Attributes
    String[] get_data;
    String current_task;
    String current_group;
    String current_assignee;
    String current_state;
    String current_objectid;
    ParseObject modify;
    RadioGroup states;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.changetaskstatescreen);
        setIntent();
        this.setTitle(current_task);
        
        states = (RadioGroup) findViewById(R.id.choose_state_radio);
        setInitialRadio();
                
        ParseQuery query = new ParseQuery(current_group);
        query.whereEqualTo(GroupTaskScreen.GET_OBJECTID, current_objectid);
        query.findInBackground(new FindCallback() {
            public void done(List<ParseObject> objects, 
                    com.parse.ParseException arg1) {
                if (arg1 == null) {
                    modify = objects.get(0);
                    executeChangeTasks();
                }
                else {
                    Toast.makeText(getBaseContext(), arg1.getMessage(), 
                            Toast.LENGTH_SHORT).show(); 
                }
            }
        });
    }
    
    /**
     * This function takes the data of the change tasks and uploads it to parse
     * on the finish_button click.  If the user selects delete class, it will
     * delete the task from parse.  It listens to both button pushes, and
     * after executing the data base change, it changes the screen back.
     */
    private void executeChangeTasks() {
        // finish the task
        findViewById(R.id.finish_button).setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      modify.put(
                              GroupTaskScreen.GET_CURRENT_STATE, getNewState());
                      modify.saveInBackground();
                      showGroupTaskScreen();
                  }
                });  
        
        // remove the task
        findViewById(R.id.remove_task_button).setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      modify.deleteInBackground();
                      showGroupTaskScreen();
                  }
                });  
    }
    
    /**
     * shows the group task screen
     */
    private void showGroupTaskScreen() {
        Intent intent = new Intent(ChangeTaskStateScreen.this,
                GroupTaskScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MyGroupsScreen.GROUP_MESSAGE, current_group);        
        this.finish();
        this.startActivity(intent);
    }
    
    /**
     *  Sets up the view based on the data that gets passed along
     */
    private void setIntent() {
        Intent intent = getIntent();
        get_data = intent.getStringArrayExtra(GroupTaskScreen.TASK_MESSAGE);
        current_group = get_data[0];
        current_assignee = get_data[1];
        current_task = get_data[2];
        current_state = get_data[3];
        current_objectid = get_data[4];
    }
    
    /**
     * Sets the initial radio state based on what gets passed to this class
     */
    private void setInitialRadio() {
        if (current_state.equals("notinprogress")) {
            states.check(R.id.not_started_task_radio);
        } 
        else if (current_state.equals("inprogress")) {
            states.check(R.id.in_progress_task_radio);
        }
        else {
            states.check(R.id.finished_task_radio);
        }
    }
    
    /**
     * Checks what the state is based on input of user
     * @return string version of the state
     */
    private String getNewState() {
        if (states.getCheckedRadioButtonId() == R.id.not_started_task_radio) {
            return "notinprogress";
        } 
        else if (states.getCheckedRadioButtonId() ==
                R.id.in_progress_task_radio) {
            return "inprogress";
        }
        else {
            return "done";
        }
    }
}