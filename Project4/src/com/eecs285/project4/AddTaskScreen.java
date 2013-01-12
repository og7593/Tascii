package com.eecs285.project4;

/**
 * This activity class represents adding a test to a group.
 */

import java.util.List;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddTaskScreen extends Activity{
    
    // attributes
    String current_group;
    String group_users_string[];
    Spinner spinner;
    EditText text;
    
    String user; // update from data
    String task; // update from data
    String upload; // if we need this, use it to upload to parse.
    
    public final static String GET_GROUP_USERS = "users";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Parse.initialize(this, "jwpTnJ23ceTSLrX5v7ziDOVuOKzDYdxn7CMmne91", 
                "TlJ8FXAVByNFtHnothKz8UwWk8eKbPpQwus36cf5"); 
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.addtaskscreen);
        
        Intent intent = getIntent();
        current_group = intent.getStringExtra(MyGroupsScreen.GROUP_MESSAGE);
        

        ParseQuery query = new ParseQuery(current_group + "_members");
        query.findInBackground(new FindCallback() {
            public void done(List<ParseObject> objects, 
                    com.parse.ParseException arg1) {
                if (arg1 == null) {
                    
                    group_users_string = new String[objects.size()];
                    int i = 0;
                    // Converts list of ParseObjects to array of Strings
                    for (ParseObject obj : objects) {
                        group_users_string[i] = obj.getString(GET_GROUP_USERS);
                        i++;
                    }           
                    loadScreen(group_users_string);
                }
                else {
                    Toast.makeText(getBaseContext(), "ERROR", 
                            Toast.LENGTH_SHORT).show(); 
                    Toast.makeText(getBaseContext(), arg1.getMessage(), 
                            Toast.LENGTH_SHORT).show(); 
                }
            }
        });
        
    }
    
    /**
     * Loads the screen based on passed in array
     * @param users_strings array of users to choose who is responsible for the
     * task
     */
    private void loadScreen(String[] users_strings)
    {
        spinner = (Spinner) findViewById(R.id.set_user_spinner);
        
        text = (EditText) findViewById(R.id.task_name_input);
        
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, 
                android.R.id.text1, users_strings);
        spinner_adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        

        findViewById(R.id.finish_add_task_button).setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      
                      if (text.getText().toString().compareTo("") == 0)
                      {
                        text.setError(getString(R.string.empty_task_name));
                          Toast.makeText(getBaseContext(),
                                  "Please enter a task.", 
                                  Toast.LENGTH_SHORT).show(); 
                      } else {
                          addCurrentData();
                          showGroupTaskScreen();
                      } 
                  }
                });
    }
    
    /**
     * Uploads the new task to Parse
     */
    private void addCurrentData()
    {
        user = spinner.getItemAtPosition(
                    spinner.getSelectedItemPosition()).toString();
        task = text.getText().toString();
        
        ParseObject updateTasks = new ParseObject(current_group);
        updateTasks.put("assignee", user);
        updateTasks.put("status", "notinprogress");
        updateTasks.put("task", task);
        updateTasks.saveInBackground(); 
        
    }
    
    /**
     * Shows the group task screen
     */
    private void showGroupTaskScreen() {
      Intent intent = new Intent(AddTaskScreen.this, GroupTaskScreen.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.putExtra(MyGroupsScreen.GROUP_MESSAGE, current_group);
      this.finish();
      this.startActivity(intent);
      return;
    }
}
