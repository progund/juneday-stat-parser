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

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

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

  private JDSSession session;

  private enum JDSMode {
    PRINT,
    BOOK
  };

  private class JDSSession {
    private JDSMode mode;
    private JunedayStat start;
    private JunedayStat stop;
    Measurement measurement;
    public JDSSession(JDSMode mode,
                      JunedayStat start,
                      JunedayStat stop){
      this.mode = mode;
      this.start = start;
      this.stop = stop;
    }
  }

  private void initMeasurement() {
    session.measurement = new Measurement(session.start, session.stop);
  }
    
  
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

  public void parseArguments(String[] args) {
    String startString = null;
    String stopString = null;
    for (int i=0; i<args.length; i++) {
      if (startString == null) {
        startString = args[i];
      } else if (stopString == null){
        stopString = args[i];
      } else {
        System.err.println("Too many arguments, bailing out");
        System.exit(1);
      }
    }
    if (stopString==null) {
      stopString = Utils.dateToString(LocalDate.now());
    }
    if (startString==null) {
      startString = Utils.dateToString(LocalDate.now().minus(1, WEEKS));
    }

    System.out.println(" start: " + startString);
    System.out.println(" stop:  " + stopString);
    
    session =
      new JDSSession(JDSMode.PRINT,
                     readFile(startString),
                     readFile(stopString));
    
  }

  private void printMeasurement() {
    System.out.println(session.start);
    System.out.println(session.stop);
    /*
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
    */
  }

  private void startSession() {
    switch (session.mode) {
    case PRINT:
      printMeasurement();
      break;
    default:
      System.err.println("No or faulty mode found...");
      System.exit(2);
    }
  }
  
  public static void main(String[] args) {
    JDCli cli = new JDCli();
    cli.parseArguments(args);
    cli.startSession();
  }
  
}
