package com.eecs285.project4;

/**
 * This activity class represents a user joining a group. Checks to make sure
 * the group exists on Parse, and validates the password. Notifies user if 
 * there are any errors.
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

public class JoinGroupScreen extends Activity {
  
  // UI elements
  private EditText GroupNameView;
  private EditText PasswordView;
  private String GroupName;
  private String Password;
  private View statusView;
  private View joinButton;
  private View cancelButton;
  boolean success;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.setContentView(R.layout.joingroupscreen);

      // Setting up the UI elements
      GroupNameView=(EditText) findViewById(R.id.group_name_text);
      PasswordView=(EditText) findViewById(R.id.password_text);
      statusView = findViewById(R.id.join_group_status);
      statusView.setVisibility(View.INVISIBLE);
      joinButton = (Button) findViewById(R.id.join_button);
      cancelButton = (Button) findViewById(R.id.cancel_button);
      
      // Set click listener for the Join button
      findViewById(R.id.join_button).setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  attemptJoin();                  
                }
              });
      
      // Set click listener for Cancel button
      findViewById(R.id.cancel_button).setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View view) {                  
                  showHomeScreen();
                }
              });
  }
  
  /**
   *  This method does error checking, and if no errors, joins the group
   */
  private void attemptJoin() {
    success=true;
    statusView.setVisibility(View.VISIBLE);
    GroupName=GroupNameView.getText().toString();
    Password=PasswordView.getText().toString();
    joinButton.setVisibility(View.INVISIBLE);
    cancelButton.setVisibility(View.INVISIBLE);
    
    //Checks if the group name field is empty
    if (TextUtils.isEmpty(GroupName)) {
        statusView.setVisibility(View.INVISIBLE);
        joinButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        success=false;
        GroupNameView.setError(getString(R.string.error_field_required));
        GroupNameView.requestFocus();
        return;
    }
    
    //Checks if the password field is empty
    if (TextUtils.isEmpty(Password)) {
      statusView.setVisibility(View.INVISIBLE);
      joinButton.setVisibility(View.VISIBLE);
      cancelButton.setVisibility(View.VISIBLE);
      success=false;
      PasswordView.setError(getString(R.string.error_field_required));
      PasswordView.requestFocus();
      return;
    }
    
    // Check to make sure the group name exists
    ParseQuery query = new ParseQuery("Groups");
    query.whereEqualTo("GroupName",GroupName);
    query.whereEqualTo("Password",Password);
    query.findInBackground(new FindCallback() {
        public void done(List<ParseObject> a,ParseException e)
        {
            //No group name or wrong password
            if(a.size()==0)
            {
              statusView.setVisibility(View.INVISIBLE);
              joinButton.setVisibility(View.VISIBLE);
              cancelButton.setVisibility(View.VISIBLE);
              GroupNameView.setError(getString(
                      R.string.invalid_group_password));
              PasswordView.setError(getString(
                      R.string.invalid_group_password));
              GroupNameView.requestFocus();
                Toast.makeText(getBaseContext(), "Invalid GroupName or " +
                		"Wrong Password...", Toast.LENGTH_LONG).show();
                success=false;
            }
            
            // Checks to make sure the current user isn't already in that group
            ParseQuery query = new ParseQuery(GlobalVariables.CURRENT_USER + 
                    "_groups");
            query.whereEqualTo("GroupName", GroupName);
            query.findInBackground(new FindCallback() {
              @Override
              public void done(List<ParseObject> arg0, ParseException arg1) {
                statusView.setVisibility(View.INVISIBLE);
                if (arg0.size() != 0)
                {
                  joinButton.setVisibility(View.VISIBLE);
                  cancelButton.setVisibility(View.VISIBLE);
                  success = false;
                  GroupNameView.setError(getString(
                          R.string.group_name_error_ingroup));
                  GroupNameView.requestFocus();
                  Toast.makeText(getBaseContext(), "Already in that group", 
                          Toast.LENGTH_LONG).show();
                }
                
                // Add the user to the group
                if(success)
                {
                    ParseObject NewUserGroup=new ParseObject(
                            GlobalVariables.CURRENT_USER+"_groups");
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
        }
    });
    return;
  }
  
  /**
   * Shows the home screen
   */
  private void showHomeScreen() {
    Intent intent = new Intent(JoinGroupScreen.this, HomeScreen.class);
    this.finish();
    this.startActivity(intent);
    return;
  }
  
  public void onBackPressed() {
    showHomeScreen();
  }
}
