package se.juneday.junedaystat.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import se.juneday.junedaystat.domain.Book;
import se.juneday.junedaystat.domain.BooksSummary;
import se.juneday.junedaystat.domain.Chapter;
import se.juneday.junedaystat.domain.CodeSummary;
import se.juneday.junedaystat.domain.JunedayStat;
import se.juneday.junedaystat.domain.PodStat;
import se.juneday.junedaystat.domain.Presentation;
import se.juneday.junedaystat.domain.VideoStat;
import se.juneday.junedaystat.utils.Utils;

public class Measurement {

  private JunedayStat start;
  private JunedayStat stop;
  public List<MBook> mbooks;

  public Measurement(JunedayStat start, JunedayStat stop) {
     this.start = start;
     this.stop = stop;
     diffBooks(start.books(), stop.books());
  }

  public Measurement() {
  }

  /*  public Measurement(LocalDate startDate, LocalDate stopDate) {
    Measurement measurement = new Measurement();
    measurement.startJunedayStat().date(startDate);
    measurement.stopJunedayStat().date(stopDate);
  }

  public Measurement createFromDate(String startDate, String stopDate) {
    Measurement measurement =
      new Measurement(Utils.stringToLocalDate(startDate),
                      Utils.stringToLocalDate(stopDate));
  }
  */
  
  private LocalDate now() {
    return LocalDate.now();
  }
  /*
  public Measurement(LocalDate startDate) {
    Measurement measurement = new Measurement(startDate, now());
    return measurement;
  }


  public Measurement(int amount, ChronoUnit unit) {
    String startDate;
    String stopDate;
    LocalDate now = now();
    LocalDate then = now.minus(amount, unit);
    Measurement measurement = new Measurement(then, now);

    return measurement;
  }
  */
  
  public static Set<String>  bookTitlesUnion(List<Book> b1, List<Book> b2) {
    Set<String> bookSet = new HashSet<>();

    for (Book b : b1) {
      bookSet.add(b.name());
    }
    for (Book b : b2) {
      bookSet.add(b.name());
    }
    return bookSet;
  }

  public static Set<String> chapterTitlesUnion(List<Chapter> c1, List<Chapter> c2) {
    Set<String> chapterSet = new HashSet<>();

    for (Chapter c : c1) {
      chapterSet.add(c.name());
    }
    for (Chapter c : c2) {
      chapterSet.add(c.name());
    }
    return chapterSet;
  }

  public static Book findBook(List<Book> books, String title) {
    for (Book b : books) {
      if (b.name().equals(title)) {
        return b;
      }
    }
    Book b = new Book(title, new ArrayList<Chapter>());
    return b;
  }

  public Chapter findChapter(List<Chapter> chapters, String title) {
    for (Chapter c : chapters) {
      if (c.name().equals(title)) {
        return c;
      }
    }
    Chapter c = new Chapter(title, 0);
    return c;
  }

  private void diffBooks(List<Book> startBooks, List<Book> stopBooks) {
     mbooks = new ArrayList<MBook>();
     Set<String> bookTitles = bookTitlesUnion(start.books(), stop.books());

     for (String title : bookTitles) {
       Book startBook = findBook(startBooks, title);
       Book stopBook = findBook(stopBooks, title);
       MBook mbook = new MBook();
       mbook.name = title;
       mbook.chapters = new ArrayList<MChapter>();

       List<Chapter> startChapters = startBook.chapters();
       List<Chapter> stopChapters = stopBook.chapters();
       Set<String> chapterTitles = chapterTitlesUnion(startChapters, stopChapters);
       for (String chapter : chapterTitles) {
         Chapter startChapter = findChapter(startChapters, chapter);
         Chapter stopChapter = findChapter(stopChapters, chapter);

         MChapter m = new MChapter();
         m.name = chapter;
         System.out.println(chapter);

         m.pages[0] = startChapter.pages();
         m.pages[1] = stopChapter.pages();
         m.pages[2] = m.pages[1] - m.pages[0];

         m.channels[0] = startChapter.channelUrls().size();
         m.channels[1] = stopChapter.channelUrls().size();
         m.channels[2] = m.channels[1] - m.channels[0];
         
         m.videos[0] = startChapter.videoUrls().size();
         m.videos[1] = stopChapter.videoUrls().size();
         m.videos[2] = m.videos[1] - m.videos[0];
         
         m.presentationPages[0] = startChapter.presentations().size();
         m.presentationPages[1] = stopChapter.presentations().size();
         m.presentations[2] = m.presentations[1] - m.presentations[0] ;
         
         m.presentationPages[0] = startChapter.presentationsPages();
         m.presentationPages[1] = stopChapter.presentationsPages();
         m.presentationPages[2] = m.presentationPages[1] - m.presentationPages[0] ;
         
         mbook.chapters.add(m);
       }
       mbooks.add(mbook);
     }
  }

  public JunedayStat startJunedayStat() {
    return start;
  }
  
  public JunedayStat stopJunedayStat() {
    return stop;
  }
  
  public void startJunedayStat(JunedayStat start) {
    this.start = start;
  }
  
  public void stopJunedayStat(JunedayStat stop) {
    this.stop = stop;
  }
  
  public static int bookPages(Book b) {
    if (b!=null) {
      return b.pages();
    }
    return 0;
  }
  
  public static class MBook {
    public List<MChapter> chapters;
    public String name;
  }
  
  public static class MChapter {
    public String name;
    public int pages[] = new int[3];
    int channels[] = new int[3];
    int videos[] = new int[3];
    int presentations[] = new int[3];
    int presentationPages[] = new int[3];

 }

  public MBook findMBook(String title) {
    for (MBook mb : mbooks) {
      if (mb.name.equals(title)) {
        return mb;
      }
    }
    MBook b = new MBook();
    b.name = title;
    b.chapters = new ArrayList<MChapter>();
    return b;
  }

    
}
