package se.juneday.junedaystat.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {


  public static final String DATE_FORMAT = "yyyyMMdd";

  public static String dateToString(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    return date.format(formatter);
  }

  public static LocalDate stringToLocalDate(String dateString) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    return LocalDate.parse(dateString, dateTimeFormatter);
  }

  public static String doubleToString(double d) {
    NumberFormat formatter = new DecimalFormat("#0.00");
    return formatter.format(d);
  }


}
