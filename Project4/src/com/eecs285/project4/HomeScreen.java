package com.eecs285.project4;

/**
 * This activity class represents the Home Screen of the app, containing the 
 * option of creating a group, joining a group, and viewing the users current
 * groups.
 */

import com.parse.Parse;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HomeScreen extends Activity {
    
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.setContentView(R.layout.homescreen);
      Parse.initialize(this, "jwpTnJ23ceTSLrX5v7ziDOVuOKzDYdxn7CMmne91", 
              "TlJ8FXAVByNFtHnothKz8UwWk8eKbPpQwus36cf5"); 
      
      Toast.makeText(this, "Press the back button to sign out", 
          Toast.LENGTH_LONG).show();

      // Set click listener for CreateGroup button
      findViewById(R.id.create_group_button).setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              showCreateGroupScreen();
            }
          });
      
      // Set click listener for JoinGroup button
      findViewById(R.id.join_group_button).setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              showJoinGroupScreen();
            }
          });
      
      // Set click listener for MyGroups button
      findViewById(R.id.my_groups_button).setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              showMyGroupsScreen();
            }
          });
  }
  

  /**
   * Shows the create group screen
   */
  private void showCreateGroupScreen() {
    Intent intent = new Intent(HomeScreen.this, CreateGroupScreen.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    this.startActivity(intent);
    this.finish();
    return;
  }
  
  
  /**
   * Shows the join group screen
   */
  private void showJoinGroupScreen() {
    Intent intent = new Intent(HomeScreen.this, JoinGroupScreen.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    this.startActivity(intent);
    this.finish();
    return;
  }
  
  /**
   * Shows the my groups screen
   */
  private void showMyGroupsScreen() {
    Intent intent = new Intent(HomeScreen.this, MyGroupsScreen.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    this.startActivity(intent);
    this.finish();
    return;
  }
  
  /**
   * Shows the login or register screen
   */
  private void showLoginOrRegisterScreen() {
    Intent intent = new Intent(HomeScreen.this, LoginOrRegister.class);
    this.startActivity(intent);
    this.finish();
    return;
  }
  
  public void onBackPressed() {
    showLoginOrRegisterScreen();
    Toast.makeText(getBaseContext(), 
        "You have been signed out", Toast.LENGTH_LONG).show();
  }
}
