package aexyn.theperfect.salon;

import android.text.format.Time;

/**
 * Clock utility.
 */
public class TimeStamp {

  /**
   * Get current time in human-readable form.
   *
   * @return current time as a string.
   */
  public static String getNow() {
    Time now = new Time();
    now.setToNow();
    return now.format("%Y_%m_%d %T");
  }

  /**
   * Get current time in human-readable form without spaces and special characters.
   * The returned value may be used to compose a file name.
   *
   * @return current time as a string.
   */
  public static String getTimeStamp() {
    Time now = new Time();
    now.setToNow();
    return now.format("%Y_%m_%d_%H_%M_%S");
  }

  public static String getDate() {
    Time now = new Time();
    now.setToNow();
    return now.format("%Y-%m-%d");
  }

  public static String getTime() {
    Time now = new Time();
    now.setToNow();
    return now.format("%H:%M:%S");
  }
}