package se.juneday.junedaystat.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import se.juneday.junedaystat.domain.Book;
import se.juneday.junedaystat.domain.BooksSummary;
import se.juneday.junedaystat.domain.Chapter;
import se.juneday.junedaystat.domain.CodeSummary;
import se.juneday.junedaystat.domain.JunedayStat;
import se.juneday.junedaystat.domain.PodStat;
import se.juneday.junedaystat.domain.Measurement;
import se.juneday.junedaystat.domain.Presentation;
import se.juneday.junedaystat.domain.VideoStat;
import static se.juneday.junedaystat.net.StatisticsParser.jsonToJunedayStat;
import se.juneday.junedaystat.utils.Utils;

public class JDCli {

  public static JunedayStat readFile(String dateStr) {
    String fileName = "data/jd-stats-" + dateStr + ".json";
    LocalDate date = Utils.stringToLocalDate(dateStr);
    try {
      return jsonToJunedayStat(date,
                               new String(Files.readAllBytes(Paths.get(fileName))));
    } catch (Exception e) {
      System.err.println("Failed, parsing: " + fileName);
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    JunedayStat startStats = readFile("20181213");
    JunedayStat stopStats = readFile("20190112");

      System.out.print(" * " + startStats);
      System.out.print(" * " + stopStats);
    Measurement measurement = new Measurement(startStats, stopStats);

    int diffSum=0;
    int sumStart=0;
    int sumStop=0;
    for (String title : Measurement.bookTitlesUnion(startStats.books(), stopStats.books())) {
      System.out.print(" * " + title);
      Book startBook = Measurement.findBook(startStats.books(), title);
      Book stopBook = Measurement.findBook(stopStats.books(), title);

      int startPages = Measurement.bookPages(startBook);
      int stopPages = Measurement.bookPages(stopBook);
      
      int diff = stopPages - startPages;
      sumStart += startPages;
      sumStop += stopPages;
      diffSum += diff;
      System.out.println(" " + diff + "   ---> " + diffSum );
    }
    System.out.println(" " + sumStart + " " + sumStop );

  }
  
}
