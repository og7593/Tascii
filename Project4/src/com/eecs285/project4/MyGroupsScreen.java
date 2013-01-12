package com.eecs285.project4;

/**
 * This activity class represents all of the groups that the current user is
 * a member of. Shows a list of the groups, and when you click on one, takes
 * the user to a screen with the tasks in that group
 */

import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.*;

public class MyGroupsScreen extends Activity {
  
  public static String GROUP_MESSAGE;
   
    
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.mygroupsscreen);

    String parseGroup = GlobalVariables.CURRENT_USER + "_groups";
    GROUP_MESSAGE = parseGroup;
    // Fetches a list of the groups that the current user is a member of
    ParseQuery query = new ParseQuery(parseGroup);
    query.findInBackground(new FindCallback() {
      @Override
      public void done(List<ParseObject> arg0, ParseException arg1) {
        String[] values = new String[arg0.size()];
        int i = 0;
        // Converts the list of ParseObjects into an array of strings
        for (ParseObject obj : arg0) {
          values[i] = obj.getString("GroupName");
          i++;
        }
        setUpList(values);
        
      }
    });
  }
  
  /**
   *  Initializes the list with the array of strings
   * @param values array of values to populate the list
   */
  private void setUpList(final String[] values) {
    ListView listView = (ListView) findViewById(R.id.mygroups);
    
    //First parameter - Context
    //Second parameter - Layout for the row
    //Third parameter - ID of the TextView to which the data is written
    //Forth - the Array of data
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, android.R.id.text1, values);
    
    //Assign adapter to ListView
    listView.setAdapter(adapter);
    
    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, 
                int position, long id) {
            showGroupTaskScreen(values[position]);    
         }
    };
    
    listView.setOnItemClickListener(itemClickListener);
  }
  
  /**
   * Shows the group task screen
   * @param next_group String variable that holds the group name
   */
  private void showGroupTaskScreen(String next_group) {
    Intent intent = new Intent(MyGroupsScreen.this, GroupTaskScreen.class);
    intent.putExtra(GROUP_MESSAGE, next_group.toString());
    this.startActivity(intent);
    return;
  }
  
  /**
   * Shows the home screen
   */
  private void showHomeScreen() {
    Intent intent = new Intent(MyGroupsScreen.this, HomeScreen.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    this.startActivity(intent);
    return;
  }
  
  public void onBackPressed() {
    showHomeScreen();
  } 
}