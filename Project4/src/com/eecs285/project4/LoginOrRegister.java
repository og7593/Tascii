package com.eecs285.project4;

/** 
 * This activity class represents the Login or Register screen. It is the 
 * first screen presented when the program begins, and prompts the user to 
 * either log in or register.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class LoginOrRegister extends Activity {
    
  // Values for email and password at the time of the login attempt.
  private String mEmail;
  private String mPassword;

  // UI references.
  private EditText mEmailView;
  private EditText mPasswordView;
  private View mLoginFormView;
  private View mLoginStatusView;

  @Override
  // Method gets called to initialize the activity
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Configure the app with Parse
    Parse.initialize(this, "jwpTnJ23ceTSLrX5v7ziDOVuOKzDYdxn7CMmne91",
            "TlJ8FXAVByNFtHnothKz8UwWk8eKbPpQwus36cf5"); 

    setContentView(R.layout.activity_login_or_register);

    // Set up the login form.
    mEmailView = (EditText) findViewById(R.id.email);
    mEmailView.setText(mEmail);
    
    // Hiding the keyboard initially
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    // Set an action listener for the password text field
    mPasswordView = (EditText) findViewById(R.id.password);
    mPasswordView
        .setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView textView, int id,
              KeyEvent keyEvent) {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
              attemptLogin();
              return true;
            }
            return false;
          }
        });

    mLoginFormView = findViewById(R.id.login_form);
    mLoginStatusView = findViewById(R.id.login_status);

    // Set the click listeners for when the user taps the sign in button
    findViewById(R.id.sign_in_button).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            attemptLogin();
          }
        });
    
    // Set the click listener for when the user taps the register button
    findViewById(R.id.register_button).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            attemptRegister();
          }
        });
  }

  /**
   * Attempts to sign in or register the account specified by the login form. 
   * If there are form errors (invalid email, missing fields, etc.), the errors
   * are  presented and no actual login attempt is made.
   */
  private void attemptLogin() {
    // Reset errors
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt
    mEmail = mEmailView.getText().toString();
    mPassword = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a nonempty password.
    if (TextUtils.isEmpty(mPassword)) {
      mPasswordView.setError(getString(R.string.error_field_required));
      focusView = mPasswordView;
      cancel = true;
    } 

    // Check for a valid and nonempty email address.
    if (TextUtils.isEmpty(mEmail)) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    }
    
    if (cancel) {
      // There was an error; don't attempt login and focus the first
      focusView.requestFocus();
    } 
    else {
      //Hides the keyboard
      InputMethodManager imm = (InputMethodManager)getSystemService(
          Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
      
      //Validate the email and password with Parse
      ParseQuery validater = new ParseQuery("Users_List");
      validater.whereEqualTo("username", mEmail);
      showProgress(true);
      validater.getFirstInBackground(new GetCallback() {
        public void done(ParseObject obj, ParseException arg1) {
            showProgress(false);
            if (arg1 == null) {
                if (obj.getString("password").compareTo(mPassword) == 0) {
                  GlobalVariables.CURRENT_USER = mEmail;
                    goToHomeScreen();
                }
                else {
                    mPasswordView.setError(
                            getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    Toast.makeText(getBaseContext(), "Invalid password",
                            Toast.LENGTH_LONG).show();
                }
            }
            else {
                mEmailView.setError(getString(R.string.username_error));
                mEmailView.requestFocus();
                Toast.makeText(getBaseContext(), "Username not found. " +
                		"Try again", Toast.LENGTH_LONG).show();
            }
        }
    });

    }
  }
 
  /**
   * Method attempts to register the user, by taking them to the Register 
   * screen
   */
  private void attemptRegister() {
    Intent intent = new Intent(LoginOrRegister.this, RegisterScreen.class);
    this.startActivity(intent);
    return;
  }
  
  /**
   * Method takes the user to the home screen
   */
  private void goToHomeScreen() {
      Intent intent = new Intent(LoginOrRegister.this, HomeScreen.class);
      this.startActivity(intent);
  }
  
  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(
          android.R.integer.config_shortAnimTime);

      mLoginStatusView.setVisibility(View.VISIBLE);
      mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
          });

      mLoginFormView.setVisibility(View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
          });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }
  
  public void onBackPressed() {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_HOME);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
}
