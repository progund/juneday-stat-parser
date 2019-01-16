package se.juneday.junedaystat.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.SimpleFormatter;

public class Utils {


  public static final String DATE_FORMAT = "yyyyMMdd";
  public static final String TWO_DIGITS = "%02d";
  public static final String NEWLINE = "\n";

  public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
  public static DecimalFormat decimalFormat = new DecimalFormat("#0.00");

  public static String dateToString(LocalDate date) {
    return date.format(dateFormatter);
  }

  public static String formatMonth(int m) {
    return String.format(TWO_DIGITS, m);
  }

  public static String formatDay(int d) {
    return String.format(TWO_DIGITS, d);
  }

  public static LocalDate stringToLocalDate(String dateString) {
    return LocalDate.parse(dateString, dateFormatter);
  }

  public static String doubleToString(double d) {
    return decimalFormat.format(d);
  }


}
