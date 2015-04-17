package main;

public class Logger {

  public static void log(String str) {
    System.out.println(System.currentTimeMillis() + ": " + str);
  }

}
