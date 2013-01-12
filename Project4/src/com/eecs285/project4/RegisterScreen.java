package com.eecs285.project4;

/**
 * This activity class represents the user registering for the app. Upon 
 * registering, the user info is stored on the Parse database.
 */

import java.util.List;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class RegisterScreen extends Activity {
    
    // UI elements
    private EditText userNameText;
    private EditText userPasswordText;
    private View statusView;
    private View userNameView;
    private View userPasswordView;
    //private final String REGEX_USERNAME

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.registerscreen);
        
        userNameView = (EditText) findViewById(R.id.enter_username_text_field);
        userPasswordView = (EditText) findViewById(
                R.id.enter_password_text_field);
        
        findViewById(R.id.register_user_button).setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    registerUser();
                }
        });
        statusView = findViewById(R.id.register_status);
        statusView.setVisibility(View.INVISIBLE);
    }
    
    /**
     *  This method saves the username and password onto the Parse database
     * @param username username of user
     * @param password password of user
     */
    private void saveUserName(final String username, final String password) {
        GlobalVariables.CURRENT_USER = username;
        ParseObject new_user = new ParseObject("Users_List");
        new_user.put("username", username);
        new_user.put("password", password);
        showProgress(true);
        new_user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException arg0) {
                showProgress(false);
                if (arg0 == null)
                    goToHomeScreen();
                else
                    Toast.makeText(getBaseContext(), "Error saving username." +
                    		" Try again", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     *  This method does error checking on the username and password, 
     *  and if there are no errors, saves to the database and goes to the
     *   Home screen
     */
    private void registerUser() {
        userNameText = (EditText)findViewById(R.id.enter_username_text_field);
        final String username = userNameText.getText().toString();
        userPasswordText = (EditText)findViewById(
                R.id.enter_password_text_field);
        final String password = userPasswordText.getText().toString();
        
        // Check for a valid and nonempty email address.
        if (username == null || username.compareTo("") == 0) {
          userNameText.setError(getString(R.string.error_field_required));
          Toast.makeText(getBaseContext(), "Enter a username and password! " +
          		"Please...", Toast.LENGTH_LONG).show();
          userNameView.requestFocus();
          return;
        }
        
        // Check for a nonempty password.
        if (password == null || password.compareTo("") == 0) {
          userPasswordText.setError(getString(R.string.error_field_required));
          Toast.makeText(getBaseContext(), "Enter a username and password! " +
          		"Please...", Toast.LENGTH_LONG).show();
          userPasswordView.requestFocus();
          return;
        } 
        
        //This makes sure that the group name is letter, number, or underscore.
        //It also makes sure that the group name is at least 3 characters.
        if (!username.matches(GlobalVariables.REGEX_PARSE)) {
          userNameText.setError(getString(R.string.username_invalid));
          Toast.makeText(getBaseContext(), "Please enter a letter, number," +
          " or underscore. The username must be at least 3 characters", 
              Toast.LENGTH_LONG).show();
          userNameView.requestFocus();
          return;
        }
        
        // Make sure that name doesn't already exist
        ParseQuery query = new ParseQuery("Users_List");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List<ParseObject> arg0, ParseException arg1) {
                if (arg0.size() != 0) {
                    // there is already a user with that name
                  userNameText.setError(getString(
                          R.string.username_error_exists));
                  userNameView.requestFocus();
                  Toast.makeText(getBaseContext(), "Username is already taken",
                          Toast.LENGTH_LONG).show();
                }
                else {
                    saveUserName(username, password);
                }   
            }
        });
    }
        
  /**
   *  Method returns the user to the homescreen
   */
  private void goToHomeScreen() {
      Intent intent = new Intent(RegisterScreen.this, HomeScreen.class);
      this.startActivity(intent);
  }
  
  /**
   *  Method takes the user back to the LoginOrRegister screen upon canceling
   */
  private void goToLoginOrRegisterScreen() {
    Intent intent = new Intent(RegisterScreen.this, LoginOrRegister.class);
    this.finish();
    this.startActivity(intent);
  }
  
  // Shows a progress spinner
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    //Hiding the register button
    Button registerButton = (Button) findViewById(R.id.register_user_button);
    registerButton.setVisibility(View.INVISIBLE);
    
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(
          android.R.integer.config_shortAnimTime);

      statusView.setVisibility(View.VISIBLE);
      statusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              statusView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
          });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      statusView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
  }
  
  
  public void onBackPressed() {
    goToLoginOrRegisterScreen();
  }
  
}