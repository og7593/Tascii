package com.eecs285.project4;

/**
 * This class represents the global variables used throughout the project.
 * The one global variable, CURRENT_USER represents the username of the current
 * user who is logged into the app.
 *  
 * REGEX_PARSE makes sure that the username or group name is letter, number, 
 * or underscore. It also makes sure that it atleast 3 characters.
 */

public class GlobalVariables {
  public static String CURRENT_USER;
  public final static String REGEX_PARSE = "[a-zA-Z0-9_]{3,}";
}
