package com.eecs285.project4;

/**
 * This activity class represents creating a new group, and setting the current
 * user to be a member of that group
 */

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;


public class CreateGroupScreen extends Activity {
  
  // UI elements
  private EditText GroupNameView;
  private EditText PasswordView;
  private String GroupName;
  private String Password;
  private View statusView;
  private View finishButton;
  private View cancelButton;
  boolean success;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.setContentView(R.layout.creategroupscreen);
      Parse.initialize(this, "jwpTnJ23ceTSLrX5v7ziDOVuOKzDYdxn7CMmne91", "TlJ8FXAVByNFtHnothKz8UwWk8eKbPpQwus36cf5");
      GroupNameView=(EditText) findViewById(R.id.group_name_text);
      PasswordView=(EditText) findViewById(R.id.password_text);
      statusView = findViewById(R.id.create_group_status);
      statusView.setVisibility(View.INVISIBLE);
      finishButton = (Button) findViewById(R.id.finish_button);
      cancelButton = (Button) findViewById(R.id.cancel_button);
      
      findViewById(R.id.finish_button).setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  attemptFinish();                  
                }
              });
      
      findViewById(R.id.cancel_button).setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View view) {                  
                  showHomeScreen();
                }
              });
  }
  
  /**
   *  Gets called when the user is done entering the information for the group
   */
  private void attemptFinish() {
    success=true;
    GroupName=GroupNameView.getText().toString();
    Password=PasswordView.getText().toString();
    statusView.setVisibility(View.VISIBLE);
    finishButton.setVisibility(View.INVISIBLE);
    cancelButton.setVisibility(View.INVISIBLE);
    if (TextUtils.isEmpty(GroupName)) {
        success=false;
        statusView.setVisibility(View.INVISIBLE);
        finishButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        GroupNameView.setError(getString(R.string.error_field_required));
        GroupNameView.requestFocus();
        return;
    }
    
    //Checks if the password field is empty and adds the error
    if (TextUtils.isEmpty(Password)) {
        success=false;
        statusView.setVisibility(View.INVISIBLE);
        finishButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        PasswordView.setError(getString(R.string.error_field_required));
        PasswordView.requestFocus();
        return;
    }
    
    
    //This makes sure that the group name is letter, number, or underscore.
    //It also makes sure that the group name is at least 3 characters.
    if (!GroupName.matches(GlobalVariables.REGEX_PARSE)) {
      success = false;
      statusView.setVisibility(View.INVISIBLE);
      finishButton.setVisibility(View.VISIBLE);
      cancelButton.setVisibility(View.VISIBLE);
      GroupNameView.setError(getString(R.string.group_name_invalid));
      Toast.makeText(getBaseContext(), "Please enter a letter, number," +
      " or underscore. The Group Name must be at least 3 characters", 
          Toast.LENGTH_LONG).show();
      GroupNameView.requestFocus();
      return;
    }
    
    // Makes sure the group name does not already exists in the database
    ParseQuery query = new ParseQuery("Groups");
    query.whereEqualTo("GroupName",GroupName);
    query.findInBackground(new FindCallback() {
        public void done(List<ParseObject> a,ParseException e) {
          //showProgress(false);
          statusView.setVisibility(View.INVISIBLE);
            //group already exists
            if(a.size()!=0) {
              finishButton.setVisibility(View.VISIBLE);
              cancelButton.setVisibility(View.VISIBLE);
                GroupNameView.setError(
                        getString(R.string.group_name_exists_error));
                GroupNameView.requestFocus();
                Toast.makeText(getBaseContext(), "Group already Exists...", 
                        Toast.LENGTH_LONG).show();
                success=false;
            }
            
            if(success) {
                //save the new group to parse
                ParseObject NewGroup=new ParseObject("Groups");
                NewGroup.put("GroupName", GroupName);
                NewGroup.put("Password", Password);
                NewGroup.saveInBackground();
                
                ParseObject NewUserGroup=new ParseObject(
                        GlobalVariables.CURRENT_USER +"_groups");
                NewUserGroup.put("GroupName", GroupName);
                NewUserGroup.put("Password", Password);
                NewUserGroup.saveInBackground();
                
                ParseObject newGroupMembers = new ParseObject(
                        GroupName + "_members");
                newGroupMembers.put("users", GlobalVariables.CURRENT_USER);
                newGroupMembers.saveInBackground();
                showHomeScreen();
            }
        }
    });

    return;
  }
  
  /**
   * Shows the home screen
   */
  private void showHomeScreen() {
    Intent intent = new Intent(CreateGroupScreen.this, HomeScreen.class);
    this.finish();
    this.startActivity(intent);
    return;
  }
  
  public void onBackPressed() {
    showHomeScreen();
  }
}