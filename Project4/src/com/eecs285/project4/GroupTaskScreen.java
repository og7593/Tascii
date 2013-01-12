package com.eecs285.project4;

/**
 * This activity class represents the screen for viewing and changing tasks
 * within a group. Contains a list of all of the tasks, along with their
 *  status, and the user who is assigned to each task
 */

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GroupTaskScreen extends Activity{
    
    // pieces of data
    private String group;
    private String[] task_string;
    private String[] assignee_string;
    private String[] current_state_string;
    private String[] objectid_string;
    private String[] display_tasks;

    // in order to communicate between screens
    public final static String TASK_MESSAGE = "THE TASK IS";
    
    // CONSTANT PARSE STRINGS
    public final static String GET_TASKS = "task";
    public final static String GET_ASSIGNEE = "assignee";
    public final static String GET_CURRENT_STATE = "status";
    public final static String GET_OBJECTID = "objectId";
    
    // Array of data being sent to the ChangeTaskStateScreen
    String[] obj_data = new String[5];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.grouptaskscreen);
        Parse.initialize(this, "jwpTnJ23ceTSLrX5v7ziDOVuOKzDYdxn7CMmne91", 
                "TlJ8FXAVByNFtHnothKz8UwWk8eKbPpQwus36cf5");  
        
        // get the group that we are working on.
        Intent intent = getIntent();
        group = intent.getStringExtra(MyGroupsScreen.GROUP_MESSAGE);
        this.setTitle(group);
        
        ParseQuery query = new ParseQuery(group);
        query.findInBackground(new FindCallback() {
            public void done(List<ParseObject> objects, 
                    com.parse.ParseException arg1) {
                if (arg1 == null) {

                    task_string = new String[objects.size()];
                    assignee_string = new String[objects.size()];
                    display_tasks = new String[objects.size()];
                    current_state_string = new String[objects.size()];
                    objectid_string = new String[objects.size()];
                    int i = 0;
                    for (ParseObject obj : objects)
                    {
                        task_string[i] = obj.getString(GET_TASKS);
                        assignee_string[i] = obj.getString(GET_ASSIGNEE);
                        current_state_string[i] = obj.getString(
                                GET_CURRENT_STATE);
                        objectid_string[i] = obj.getObjectId();
                        display_tasks[i] = assignee_string[i] + ": " + 
                        task_string[i] +" - " + 
                                convertStatus(current_state_string[i]);
                        i++;
                    }
                    fillTheTableWithTasks(display_tasks);
                }
                else {
                    Toast.makeText(getBaseContext(), arg1.getMessage(), 
                            Toast.LENGTH_SHORT).show(); 
                }
            }
        });        
    }
    
    /**
     * shows the add task screen
     */
    private void showAddTaskScreen() {
      Intent intent = new Intent(GroupTaskScreen.this, AddTaskScreen.class);
      intent.putExtra(MyGroupsScreen.GROUP_MESSAGE, group);
      this.startActivity(intent);
      return;
    }
    
    /**
     * @param data array that holds that task information
     * Goes to the modify task screen
     */
    private void showChangeTaskStateScreen(String[] data) {
        Intent intent = new Intent(GroupTaskScreen.this, 
                                                   ChangeTaskStateScreen.class);
        intent.putExtra(TASK_MESSAGE, data);
        this.startActivity(intent);
        return;
    }
    
    /**
     * shows the my groups screen
     */
    private void showMyGroupsScreen() {
      Intent intent = new Intent(GroupTaskScreen.this, MyGroupsScreen.class);
      this.finish();
      this.startActivity(intent);
      return;
    }

    /**
     *  Method populates the table with an array of tasks, which have been 
     *  fetched from Parse
     * @param fill_tasks an array of srings that contains all of the tasks in 
     * a group
     */
    private void fillTheTableWithTasks(String[] fill_tasks) {
        ListView listView = (ListView) findViewById(R.id.mytasks);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, 
                fill_tasks);
        listView.setAdapter(adapter);
        
        OnItemClickListener listViewItemClickListener = new
                OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, 
                    int position, long id) {
 
                obj_data[0] = group; //group
                obj_data[1] = assignee_string[position]; // assignee
                obj_data[2] = task_string[position]; // task
                obj_data[3] = current_state_string[position]; // current state
                obj_data[4] = objectid_string[position]; // current objectid
                
                if (checkUser(obj_data)) {
                  showChangeTaskStateScreen(obj_data);
                }
                else {
                    Toast.makeText(getBaseContext(), 
                            "You can only modify your own tasks", 
                                    Toast.LENGTH_SHORT).show();
                }
             }
        };
        
        findViewById(R.id.add_task_button).setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    showAddTaskScreen();
                  }
                });
        listView.setOnItemClickListener(listViewItemClickListener);
    }
    
    /**
     *  Checks to see that the selected user is the same as the current user 
     * @param check is an array of data, with the user being at index 1 
     * @return a bool based on weather that user is the same as the current
     * user
     */
    private boolean checkUser(String[] check) {
        return GlobalVariables.CURRENT_USER.equals(check[1]);
    }
    
    /**
     *  Method converts the status, based on how we are storing it on Parse to
     *   a readable status. ie. notinprogress -> Not in progress
     * @param status the status based on how it's represented on Parse
     * @return a readable string
     */
    private String convertStatus(String status) {
      if (status.compareTo("notinprogress") == 0)
        return "Not Started";
      else if (status.compareTo("inprogress") == 0)
        return "In Progress";
      else
        return "Finished";
    }
    
    public void onBackPressed() {
      showMyGroupsScreen();
    }
}
