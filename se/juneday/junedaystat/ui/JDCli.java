package se.juneday.junedaystat.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;


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
import se.juneday.junedaystat.measurement.Measurement;
import se.juneday.junedaystat.measurement.exporter.HtmlExporter;
import se.juneday.junedaystat.domain.Presentation;
import se.juneday.junedaystat.domain.VideoStat;
import se.juneday.junedaystat.domain.exporter.*;
import static se.juneday.junedaystat.net.StatisticsParser.jsonToJunedayStat;
import se.juneday.junedaystat.net.StatisticsParser;
import se.juneday.junedaystat.utils.Utils;

public class JDCli {

  private JDSSession session;

  private enum JDSMode {
    PRINT,
    SUM,
    BOOK,
    DIFF_HTML,
    BOOK_VIDEO,
    SINGLE_BOOK
  };

  private class JDSSession {
    private JDSMode mode;
    private JunedayStat start;
    private JunedayStat stop;
    private Measurement measurement;
    public JDSSession(JDSMode mode,
                      JunedayStat start,
                      JunedayStat stop){
      this.mode = mode;
      this.start = start;
      this.stop = stop;
      measurement = new Measurement(start, stop);    
    }
  }

    public void printSingleBook(String date) {
	JunedayStat start = StatisticsParser.readFile(date);

	for (Book b : start.books()) {
	    System.out.println("- " + b.name());
	    if (b.name().equals("Java Web programming")) {
		for (Chapter c : b.chapters()) {
		    System.out.println("   |- " + c.name());
		}
	    }
	}
    }
    
  public void parseArguments(String[] args) {
    String startString = null;
    String stopString = null;
    JDSMode mode = JDSMode.SUM;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("--books")) {
        mode = JDSMode.BOOK;
      } else if (args[i].equals("--html")) {
        mode = JDSMode.DIFF_HTML;
      } else if (args[i].equals("--single-book")) {
        mode = JDSMode.SINGLE_BOOK;
	printSingleBook(startString);
	System.exit(0);
      } else if (startString == null) {
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
      new JDSSession(mode,
                     StatisticsParser.readFile(startString),
                     StatisticsParser.readFile(stopString));
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


      JsonBookExporter je = new JsonBookExporter();
      SQLBookExporter se = new SQLBookExporter();
      startBook.export(se);
      System.out.println(se.toString());

      System.out.println("-----------------------------------------------------------");
      if (startBook.name().equals("Programming with Java")) {
        startBook.export(je);
        System.out.println("-----------------------------------------------------------");
        System.out.println("-----------------------------------------------------------");
        System.out.println(je.toString());
        System.out.println("-----------------------------------------------------------");
        System.out.println("-----------------------------------------------------------");
        System.out.println("-----------------------------------------------------------");
      }
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
    int diff = session.stop.channels() - session.start.channels() ;
    float channelDaily  =  diff / ((float)dailyDiff);
    System.out.format(formatStrNumbers,
                      "Channel",
                      session.start.channels(),
                      session.stop.channels(),
                      diff,
                      channelDaily);
    
    diff = session.stop.videos() - session.start.videos() ;
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
    System.out.format(" * %5.2f channels\n" , channelDaily );
    System.out.format(" * %5.2f videos\n" , videoDaily );
    System.out.format(" * %5.2f presentations\n", presDaily);
    System.out.format(" * %5.2f presentation pages\n", presPagesDaily);


    

  }

  private void printBook() {
    JunedayStat startStats = session.start;
    JunedayStat stopStats = session.stop;
    
    for (Measurement.MBook mbook : session.measurement.stat().books()) {
      if (mbook.name().equals("")) {
        continue;
      }
      System.out.println(" - " + mbook.name());
      //      System.out.println(" - " + mbook.chapters.size() + " chapters");
      for (Measurement.MChapter mchapter : mbook.chapters()) { 
        //System.out.println(" --- " + mchapter.name);
        int videoDiffCount = mchapter.diffVideosStart().size() + mchapter.diffVideosStop().size();
        int channelDiffCount = mchapter.diffChannelsStart().size() + mchapter.diffChannelsStop().size();
        int diff = videoDiffCount + channelDiffCount;
        
        if ( diff > 0) {
          System.out.println("  " + mchapter.name());
        }
        if (channelDiffCount>0) {
          System.out.println("    channels");
          for (String c : mchapter.diffChannelsStop()) {
            System.out.println("    + " + c);
          }
          for (String c : mchapter.diffChannelsStart()) {
            System.out.println("    - " + c);
          }
        }
        if (videoDiffCount>0) {
          System.out.println("    videos");
          for (String v : mchapter.diffVideosStop()) {
            System.out.println("    + " + v);
          }
          for (String v : mchapter.diffVideosStart()) {
            System.out.println("    - " + v);
          }
        }
      }
      Map<CodeSummary.ProgLang, CodeSummary.Stat> langStat =
        session.measurement.stat().code().langStat();
      for (Map.Entry<CodeSummary.ProgLang, CodeSummary.Stat> entry : langStat.entrySet())
        {
          System.out.println(" --- " + entry.getKey() + " | " + entry.getValue());
        }
    }

    /*
    for (String bookTitle : Measurement.bookTitlesUnion(startStats.books(), stopStats.books())) {
      if (bookTitle.equals("")) { continue; }
      Book startBook = Measurement.findBook(startStats.books(), bookTitle);
      Book stopBook = Measurement.findBook(stopStats.books(), bookTitle);
      System.out.println(bookTitle);
      for (String chapterTitle : Measurement.chaptersUnion(startBook.chapters(), stopBook.chapters())) { 
        
        List<String> diffVideosStart = 
          session.measurement.measuredStringList.listDiff(Measurement.videos(startBook, chapterTitle),
                                                          Measurement.videos(stopBook, chapterTitle));
        List<String> diffVideosStop = 
          session.measurement.measuredStringList.listDiff(Measurement.videos(stopBook, chapterTitle),
                                                          Measurement.videos(startBook, chapterTitle));
        List<String> diffChannelsStart =
          session.measurement.measuredStringList.listDiff(Measurement.channels(startBook, chapterTitle),
                                                          Measurement.channels(stopBook, chapterTitle));
        List<String> diffChannelsStop =
          session.measurement.measuredStringList.listDiff(Measurement.channels(stopBook, chapterTitle),
                                                          Measurement.channels(startBook, chapterTitle));

        int videoDiffCount = diffVideosStart.size() + diffVideosStop.size();
        int channelDiffCount = diffChannelsStart.size() + diffChannelsStop.size();
        int diff = videoDiffCount + channelDiffCount;
        
        if ( diff > 0) {
          System.out.println("  " + chapterTitle);
        }
        if (channelDiffCount>0) {
          System.out.println("    channels");
          for (String c : diffChannelsStop) {
            System.out.println("    + " + c);
          }
          for (String c : diffChannelsStart) {
            System.out.println("    - " + c);
          }
        }
        if (videoDiffCount>0) {
          System.out.println("    videos");
          for (String v : diffVideosStop) {
            System.out.println("    + " + v);
          }
          for (String v : diffVideosStart) {
            System.out.println("    - " + v);
          }
        }
        
      } 
      }*/
  }

  private void printDiffHtml() {
    JunedayStat startStats = session.start;
    JunedayStat stopStats = session.stop;
    HtmlExporter he = new HtmlExporter(session.measurement);
    
    System.out.println(he.export());

    /*
    
    for (Measurement.MBook mbook : session.measurement.stat().books()) {
      if (mbook.name().equals("")) {
        continue;
      }
      System.out.println(" ? " + mbook.name());
      //      System.out.println(" - " + mbook.chapters.size() + " chapters");
      for (Measurement.MChapter mchapter : mbook.chapters()) { 
        //System.out.println(" --- " + mchapter.name);
        int videoDiffCount = mchapter.diffVideosStart().size() + mchapter.diffVideosStop().size();
        int channelDiffCount = mchapter.diffChannelsStart().size() + mchapter.diffChannelsStop().size();
        int diff = videoDiffCount + channelDiffCount;
        
        if ( diff > 0) {
          System.out.println("  " + mchapter.name());
        }
        if (channelDiffCount>0) {
          System.out.println("    channels");
          for (String c : mchapter.diffChannelsStop()) {
            System.out.println("    + " + c);
          }
          for (String c : mchapter.diffChannelsStart()) {
            System.out.println("    - " + c);
          }
        }
        if (videoDiffCount>0) {
          System.out.println("    videos");
          for (String v : mchapter.diffVideosStop()) {
            System.out.println("    + " + v);
          }
          for (String v : mchapter.diffVideosStart()) {
            System.out.println("    - " + v);
          }
        }
      }
      Map<CodeSummary.ProgLang, CodeSummary.Stat> langStat =
        session.measurement.stat().code().langStat();
      for (Map.Entry<CodeSummary.ProgLang, CodeSummary.Stat> entry : langStat.entrySet())
        {
          System.out.println(" --- " + entry.getKey() + " | " + entry.getValue());
        }
    }
    */
    
  }

  
  private void startSession() {
    switch (session.mode) {
    case PRINT:
      printMeasurement();
      break;
    case SUM:
      printSummary();
      break;
    case BOOK:
      printBook();
      break;
    case DIFF_HTML:
      printDiffHtml();
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
