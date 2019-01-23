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
    SUM,
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

    // System.out.println(" start: " + startString);
    // System.out.println(" stop:  " + stopString);
    
    session =
      new JDSSession(JDSMode.SUM,
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

  private void printSummary() {
    JunedayStat startStats = session.start;
    JunedayStat stopStats = session.stop;
    int diffSum=0;
    int sumStart=0;
    int sumStop=0;
    String formatStr = " %-30s %5s %5s %5s %5s\n";
    String formatStrNumbers = " %-30s %5d %5d %5d %5.2f\n";
    long dailyDiff = DAYS.between(session.start.date(), session.stop.date());

    System.out.println(" Date information");
    System.out.println("===========================================================");
    System.out.println(" Start: " + startStats.date());
    System.out.println(" Stop:  " + stopStats.date());
    System.out.println(" Days:  " + dailyDiff);
    System.out.println();

    
    System.out.println(" Books");
    System.out.println("===========================================================");
    System.out.format(formatStr, "Title", "Start", "Stop", "Diff", "Daily");
    System.out.println("-----------------------------------------------------------");
    for (String title : Measurement.bookTitlesUnion(startStats.books(), stopStats.books())) {
      if (title.equals("")) { continue; }
      Book startBook = Measurement.findBook(startStats.books(), title);
      Book stopBook = Measurement.findBook(stopStats.books(), title);
      
      int startPages = Measurement.bookPages(startBook);
      int stopPages = Measurement.bookPages(stopBook);
      
      int diff = stopPages - startPages;
      sumStart += startPages;
      sumStop += stopPages;
      diffSum += diff;
      System.out.format(formatStrNumbers, title, startPages, stopPages, diff, diff/((float)dailyDiff));
      // System.out.print(" * " + title);
      // System.out.println(" " + diff + "   ---> " + diffSum );
    }
    System.out.println("-----------------------------------------------------------");
    System.out.format(formatStrNumbers, "Total", sumStart, sumStop, (sumStop - sumStart), (sumStop - sumStart)/((float)dailyDiff));

    
    System.out.println();
    System.out.println(" Misc");
    System.out.println("===========================================================");
    System.out.format(formatStr,
                      "Artefact",
                      "Start",
                      "Stop",
                      "Diff",
                      "Daily");
    System.out.println("-----------------------------------------------------------");
    int diff = session.stop.videos() - session.start.videos() ;
    float videoDaily  =  diff / ((float)dailyDiff);
    System.out.format(formatStrNumbers,
                      "Video",
                      session.start.videos(),
                      session.stop.videos(),
                      diff,
                      videoDaily);
    
    diff = session.stop.presentations() - session.start.presentations() ;
    float presDaily  =  diff / ((float)dailyDiff);
    System.out.format(formatStrNumbers,
                      "Presentations",
                      session.start.presentations(),
                      session.stop.presentations(),
                      diff,
                      presDaily);

    diff = session.stop.presentationsPages() - session.start.presentationsPages() ;
    float presPagesDaily  =  diff / ((float)dailyDiff);
    System.out.format(formatStrNumbers,
                      "Presentation pages",
                      session.start.presentationsPages(),
                      session.stop.presentationsPages(),
                      diff,
                      presPagesDaily);

    
    System.out.println();
    System.out.println();
    System.out.println("Daily production over the period:");
    System.out.format(" * %5.2f pages\n",  (sumStop - sumStart)/((float)dailyDiff) );
    System.out.format(" * %5.2f videos\n" , videoDaily );
    System.out.format(" * %5.2f presentations\n", presDaily);
    System.out.format(" * %5.2f presentation pages\n", presPagesDaily);
    

  }

  private void startSession() {
    switch (session.mode) {
    case PRINT:
      printMeasurement();
      break;
    case SUM:
      printSummary();
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
