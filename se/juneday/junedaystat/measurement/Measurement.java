package se.juneday.junedaystat.measurement;

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
  private MStat mstat;
  public MeasurementObject<String> measuredStringList ;
  public MeasurementObject<Presentation> measuredPresentationList ;

  public Measurement(JunedayStat start, JunedayStat stop) {
    this.start = start;
    this.stop = stop;
    measuredStringList = new MeasurementObject<String>();
    measuredPresentationList = new MeasurementObject<Presentation>();
  }
  
  public Measurement() {
    ;
  }

  public MStat stat(){
    if (mstat==null) {
      mstat = new MStat();
      diffBooks(start.books(), stop.books());
      mstat.code = new MCode();
      mstat.code.langStat = diffCode();
      mstat.pod = new MPod();
      mstat.pod.podcasts = stop.podStat().podCasts() - start.podStat().podCasts();
      mstat.video = new MVideo();
      mstat.video.videos = stop.videoSummary().videos() - start.videoSummary().videos();
    }
    return mstat;
  }

  
  
  private Map<CodeSummary.ProgLang, CodeSummary.Stat> diffCode() {
    CodeSummary startCode = start.codeSummary();
    CodeSummary stopCode = stop.codeSummary();
    Map<CodeSummary.ProgLang, CodeSummary.Stat> diffedCode
      = new HashMap<>();
    
    for (CodeSummary.ProgLang lang : CodeSummary.ProgLang.values()) {
      CodeSummary.Stat startStat = startCode.stat(lang.toString());
      CodeSummary.Stat stopStat = stopCode.stat(lang.toString());
      int startLoc = 0;
      int startFiles = 0;
      if (startStat != null ) {
         startLoc = startStat.loc();
         startFiles = startStat.files();
      }

      int stopLoc = 0;
      int stopFiles = 0;
      if (stopStat != null ) {
         stopLoc = stopStat.loc();
         stopFiles = stopStat.files();
      }

      int loc = stopLoc - startLoc ;
      int files = stopFiles - startFiles; 
      diffedCode.put(lang, new CodeSummary.Stat(lang, loc, files));
    }
    return diffedCode;
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

  public static Set<String> chaptersUnion(List<Chapter> c1, List<Chapter> c2) {
    Set<String> chapterSet = new HashSet<>();

    for (Chapter c : c1) {
      chapterSet.add(c.name());
    }
    for (Chapter c : c2) {
      chapterSet.add(c.name());
    }
    return chapterSet;
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
    mstat.books = new ArrayList<>();
    Set<String> bookTitles = bookTitlesUnion(start.books(), stop.books());

     // Loop through books
     for (String title : bookTitles) {
       Book startBook = findBook(startBooks, title);
       Book stopBook = findBook(stopBooks, title);
       MBook mbook = new MBook();
       mbook.name = title;
       mbook.chapters = new ArrayList<MChapter>();

       List<Chapter> startChapters = startBook.chapters();
       List<Chapter> stopChapters = stopBook.chapters();
       
       Set<String> chapterTitles = chapterTitlesUnion(startChapters, stopChapters);
       // Loop through chapters
       int pagesSum = 0;
       int channelsSum = 0;
       int videosSum = 0;
       int presentationsSum = 0;
       int presentationsPagesSum = 0;
       for (String chapterTitle : chapterTitles) {
         Chapter startChapter = findChapter(startChapters, chapterTitle);
         Chapter stopChapter = findChapter(stopChapters, chapterTitle);
         MChapter mchapter = new MChapter();

         mchapter.pages = stopChapter.pages() - startChapter.pages();

         mchapter.diffVideosStart = 
           measuredStringList.listDiff(videos(startBook, chapterTitle),
                                       videos(stopBook, chapterTitle));
         mchapter.diffVideosStop = 
           measuredStringList.listDiff(videos(stopBook, chapterTitle),
                                       videos(startBook, chapterTitle));
         mchapter.diffChannelsStart =
           measuredStringList.listDiff(channels(startBook, chapterTitle),
                                       channels(stopBook, chapterTitle));
         mchapter.diffChannelsStop =
           measuredStringList.listDiff(channels(stopBook, chapterTitle),
                                       channels(startBook, chapterTitle));
         
         mchapter.diffPresentationsStart =
           measuredPresentationList.listDiff(presentations(startBook, chapterTitle),
                                             presentations(stopBook, chapterTitle));
         
         mchapter.diffPresentationsStop =
           measuredPresentationList.listDiff(presentations(stopBook, chapterTitle),
                                             presentations(startBook, chapterTitle));
         
         pagesSum += mchapter.pages;
         channelsSum += mchapter.diffChannelsStop.size() - mchapter.diffChannelsStart.size();
         videosSum += mchapter.diffVideosStop.size() - mchapter.diffVideosStart.size();
         presentationsSum += mchapter.diffPresentationsStop.size() - mchapter.diffPresentationsStart.size();;
         presentationsPagesSum += 0;


         // Only add chapters with a diff to the book
         if ( mchapter.diffVideosStart.size() != 0 
              || mchapter.diffVideosStop.size() != 0
              || mchapter.diffChannelsStart.size() != 0
              || mchapter.diffChannelsStop.size() != 0
              || mchapter.diffPresentationsStart.size() != 0
              || mchapter.diffPresentationsStop.size() != 0
              || mchapter.pages != 0) {
           mchapter.name = chapterTitle;
           mbook.chapters.add(mchapter);
           //           System.err.println(" add chapter: " + chapterTitle);
         } else {
           // System.err.println(" NOT add chapter: " + chapterTitle
           //                    + " " + mchapter.diffVideosStart.size()
           //                    + " " + mchapter.diffVideosStop.size()
           //                    + " " + mchapter.diffChannelsStart.size()
           //                    + " " + mchapter.diffChannelsStop.size()
           //                    + " " + mchapter.diffPresentationsStart.size()
           //                    + " " + mchapter.diffPresentationsStop.size()
           //                    );
         }
         
         
       }
       mbook.pages = pagesSum;
       mbook.channels = channelsSum;
       mbook.videos = videosSum;
       mbook.presentations = presentationsSum;
       mbook.presentationsPages = presentationsPagesSum;
       mstat.books.add(mbook);
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

  public static class MStat {
    private MPod pod;
    private MVideo video;
    private List<MBook> books;
    private MCode code;

    public MPod pod(){
      return pod;
    }
    public MVideo video(){
      return video;
    }
    public List<MBook> books(){
      return books;
    }
    public MCode code(){
      return code;
    }
    
  }
  
  public static class MPod {
    private int podcasts;
    public int podcasts() {
      return podcasts;
    }
  }
  
  public static class MVideo {
    private int videos;
    public int videos() {
      return videos;
    }
  }
  
  public static class MBook {
    private String name;
    private int pages;
    private int videos;
    private int channels;
    private int presentations;
    private int presentationsPages;
    private List<MChapter> chapters;
    public String name() {
      return name;
    }
    public List<MChapter> chapters() {
      return chapters;
    }

    public int pages(){
      return this.pages;
    }
    
    public int videos(){
      return this.videos;
    }
    
    public int channels(){
      return this.channels;
    }
    
    public int presentations(){
      return this.presentations;
    }
    
    public int presentationsPages(){
      return this.presentationsPages;
    }
        
  }
  
  public static class MCode {
    private Map<CodeSummary.ProgLang, CodeSummary.Stat> langStat;
    public Map<CodeSummary.ProgLang, CodeSummary.Stat> langStat() {
      return langStat;
    }
  }
  
  public static class MChapter {
    private String name;
    private int pages;
    private List<String> diffVideosStart ;
    private List<String> diffVideosStop ;
    private List<String> diffChannelsStart ;
    private List<String> diffChannelsStop;
    private List<Presentation> diffPresentationsStart ;
    private List<Presentation> diffPresentationsStop;
    public String name(){
      return name;
    }
    public int pages(){
      return pages;
    }
    public List<String> diffVideosStart() {
      return diffVideosStart;
    }
    public List<String> diffVideosStop() {
      return diffVideosStop;
    }
    public List<String> diffChannelsStart() {
      return diffChannelsStart;
    }
    public List<String> diffChannelsStop() {
      return diffChannelsStop;
    }
    public List<Presentation> diffPresentationsStart() {
      return diffPresentationsStart;
    }
    public List<Presentation> diffPresentationsStop() {
      return diffPresentationsStop;
    }
  }

  /*
  public static class Presentation {
    private String name;
    private int pages;
    public String name() {
      return name;
    }
    public int pages() {
      return pages;
    }
  }
  */
  public MBook findMBook(String title) {
    for (MBook mb : mstat.books) {
      if (mb.name.equals(title)) {
        return mb;
      }
    }
    MBook b = new MBook();
    b.name = title;
    b.chapters = new ArrayList<MChapter>();
    return b;
  }

  public static Chapter findChapter(Book b, String chapterTitle) {
    for (Chapter c : b.chapters()) {
      if (c.name().equals(chapterTitle)) {
        return c;
      }
    }
    return null;
  }
  
  public static List<String> videos(Book b, String chapterTitle) {
    Chapter c = findChapter(b, chapterTitle);
    if (c==null) {
      return new ArrayList<>();
    }
    return c.videoUrls();
  }
  
  public static List<String> channels(Book b, String chapterTitle) {
    Chapter c = findChapter(b, chapterTitle);
    if (c==null) {
      return new ArrayList<>();
    }
    return c.channelUrls();
  }

  public static List<Presentation> presentations(Book b, String chapterTitle) {
    Chapter c = findChapter(b, chapterTitle);
    if (c==null) {
      return new ArrayList<>();
    }
    return c.presentations();
  }

  public class MeasurementObject<T> {

    public List<T> listDiff(List<T> first, List<T> second) {
      List<T> diffList = new ArrayList<>();
      for (T o : first) {
        if ( ! second.contains(o) ) {
          diffList.add(o);
        }
      }
      return diffList;
    }
  }
  /*
  public interface Exporter {
    void addName(String name);
    void addChapters(List<Chapter> chapters);
  }

  public void export(Exporter exp) {
    exp.addName(name);
    exp.addChapters(chapters);
  }
  */

  
}
