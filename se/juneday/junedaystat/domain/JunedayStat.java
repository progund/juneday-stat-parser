package se.juneday.junedaystat.domain;

import java.util.List;
import java.time.LocalDate;

public class JunedayStat {

  public static final String JDSTAT_BOOK_SUMMARY = "book-summary";
  public static final String JDSTAT_BOOK_SUMMARY_BOOKS = "books";
  public static final String JDSTAT_BOOK_SUMMARY_PAGES = "pages";
  public static final String JDSTAT_BOOK_SUMMARY_CHANNELS = "uniq-channels";
  public static final String JDSTAT_BOOK_SUMMARY_PRESENTATIONS = "uniq-presentations";
  public static final String JDSTAT_BOOK_SUMMARY_PRES_PAGES = "uniq-presentations-pages";
  public static final String JDSTAT_BOOK_SUMMARY_VIDEOS = "uniq-videos";


  public static final String JDSTAT_SOURCE_CODE = "source-code";
  public static final String JDSTAT_SOURCE_CODE_LOC = "lines-of-code";
  public static final String JDSTAT_SOURCE_CODE_FILES = "number-of-files";
  public static final String JDSTAT_SOURCE_CODE_TYPE = "type";

  public static final String JDSTAT_VIDEO = "vimeo-stats";
  public static final String JDSTAT_VIDEO_VIDEOS = "videos";

  public static final String JDSTAT_POD = "pod";
  public static final String JDSTAT_POD_PODCASTS = "podcasts";

  public static final String JDSTAT_BOOKS = "books";
  public static final String JDSTAT_BOOK_NAME = "title";

  public static final String JDSTAT_CHAPTERS = "chapters";
  public static final String JDSTAT_CHAPTER_NAME = "name";
  public static final String JDSTAT_CHAPTER_PAGES = "pages";
  public static final String JDSTAT_VIDEO_COUNT = "video-count";
  public static final String JDSTAT_VIDEOS = "videos";
  public static final String JDSTAT_CHANNEL_COUNT = "channel-count";
  public static final String JDSTAT_CHANNELS = "channels";

  public static final String JDSTAT_PRESENTATIONS = "presentations";
  public static final String JDSTAT_PRESENTATION_NAME = "name";
  public static final String JDSTAT_PRESENTATION_PAGES = "pages";

  private LocalDate date;
  private BooksSummary booksSummary;
  private CodeSummary codeSummary;
  private VideoStat videoSummary;
  private PodStat podStat;
  private List<Book> books;
  
  public PodStat podStat() {
    return podStat;
  }


  @Override
  public String toString() {
    return "JunedayStat{" +
      "booksSummary=" + booksSummary +
      "videoSummary=" + videoSummary +
      "codeSummary=" + codeSummary +
      "\n" + books ;
  }

  public VideoStat videoSummary() {
    return videoSummary;
  }

  public JunedayStat(LocalDate date, BooksSummary booksSummary, CodeSummary codeSummary, VideoStat videoSummary, PodStat podStat, List<Book> books) {
    this.date = date;
    this.booksSummary = booksSummary;
    this.codeSummary = codeSummary;
    this.videoSummary = videoSummary;
    this.podStat = podStat;
    this.books = books;
  }

  public LocalDate date() {
    return date;
  }
  
  public void date(LocalDate date) {
    this.date = date ;
  }
  
  public BooksSummary booksSummary() {
    return booksSummary;
  }

  public CodeSummary codeSummary() {
    return codeSummary;
  }

  public List<Book> books() {
    return books;
  }

  public int pages() {
    int sum=0;
    for (Book b : books) {
      sum += b.pages();
    }
    return sum;
  }
  
  public int presentations() {
    int sum=0;
    for (Book b : books) {
      sum += b.presentations();
    }
    return sum;
  }
  
  public int presentationsPages() {
    int sum=0;
    for (Book b : books) {
      sum += b.presentationsPages();
    }
    return sum;
  }
  
  public int channels() {
    int sum=0;
    for (Book b : books) {
      sum += b.channels();
    }
    return sum;
  }
  
  public int videos() {
    int sum=0;
    for (Book b : books) {
      sum += b.videos();
    }
    return sum;
  }
  

}
