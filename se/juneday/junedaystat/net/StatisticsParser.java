package se.juneday.junedaystat.net;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import se.juneday.junedaystat.domain.Book;
import se.juneday.junedaystat.domain.BooksSummary;
import se.juneday.junedaystat.domain.Chapter;
import se.juneday.junedaystat.domain.CodeSummary;
import se.juneday.junedaystat.domain.JunedayStat;
import se.juneday.junedaystat.domain.PodStat;
import se.juneday.junedaystat.domain.Presentation;
import se.juneday.junedaystat.domain.VideoStat;

public class StatisticsParser {


  private static final String LOG_TAG = StatisticsParser.class.getSimpleName() ;

  private static int getIntValue(JSONObject object, String key, int defaultValue) {
    int ret = defaultValue;
    try {
      ret = object.getInt(key);
    } catch (JSONException e) {
      ;
    }
    return ret;
  }

  private static String getStringValue(JSONObject object, String key, String defaultValue) {
    String ret = defaultValue;
    try {
      ret = object.getString(key);
    } catch (JSONException e) {
      ;
    }
    return ret;
  }

  public static BooksSummary parseBookSummary(JSONObject summary) {
    int books = getIntValue(summary, JunedayStat.JDSTAT_BOOK_SUMMARY_BOOKS, 0);
    int pages = getIntValue(summary, JunedayStat.JDSTAT_BOOK_SUMMARY_PAGES, 0);
    int channels = getIntValue(summary, JunedayStat.JDSTAT_BOOK_SUMMARY_CHANNELS, 0);
    int presentations = getIntValue(summary, JunedayStat.JDSTAT_BOOK_SUMMARY_PRESENTATIONS, 0);
    int presPages = getIntValue(summary, JunedayStat.JDSTAT_BOOK_SUMMARY_PRES_PAGES, 0);
    int videos = getIntValue(summary, JunedayStat.JDSTAT_BOOK_SUMMARY_VIDEOS, 0);

    BooksSummary bookSummary = new BooksSummary(books, pages, channels, presentations, presPages,
        videos);
    return bookSummary;
  }


  public static VideoStat parseVideoSummary(JSONObject videoObject) {
    int videos = getIntValue(videoObject, JunedayStat.JDSTAT_VIDEO_VIDEOS, 0);
    return new VideoStat(videos);
  }

  public static PodStat parsePodStat(JSONObject videoObject) {
    int podCasts = getIntValue(videoObject, JunedayStat.JDSTAT_POD_PODCASTS, 0);
    return new PodStat(podCasts);
  }

  public static Chapter extractChapter(JSONObject jsonChapter) {
    String name = getStringValue(jsonChapter, JunedayStat.JDSTAT_CHAPTER_NAME, "");
    int pages = getIntValue(jsonChapter, JunedayStat.JDSTAT_CHAPTER_PAGES, 0);
    int channelCount = getIntValue(jsonChapter, JunedayStat.JDSTAT_CHANNEL_COUNT, 0);
    int videoCount = getIntValue(jsonChapter, JunedayStat.JDSTAT_VIDEO_COUNT, 0);


    // Get video list
    List<String> videos = new ArrayList<>();
    try {
      JSONArray videosJson = jsonChapter.getJSONArray(JunedayStat.JDSTAT_VIDEOS);
      for (int i = 0; i < videosJson.length(); i++) {
        try {
          String url = videosJson.getString(i);
          videos.add(url);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    } catch (JSONException e) {
      ;
    }

    // Get channel list
    List<String> channels = new ArrayList<>();
    try {
      JSONArray channelsJson = jsonChapter.getJSONArray(JunedayStat.JDSTAT_CHANNELS);
      for (int i = 0; i < channelsJson.length(); i++) {
        try {
          String url = channelsJson.getString(i);
          channels.add(url);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    } catch (JSONException e) {
      ;
    }

    // Get presentation list
    List<Presentation> presentations = new ArrayList<>();
    try {
      JSONArray presentationsJson = jsonChapter.getJSONArray(JunedayStat.JDSTAT_PRESENTATIONS);
      for (int i = 0; i < presentationsJson.length(); i++) {
        try {
          JSONObject pres = presentationsJson.getJSONObject(i);
          String presName = getStringValue(pres, JunedayStat.JDSTAT_PRESENTATION_NAME, "");
          int presPages = getIntValue(pres, JunedayStat.JDSTAT_PRESENTATION_PAGES, 0);
          Presentation p = new Presentation(presName, presPages);
          presentations.add(p);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    } catch (JSONException e) {
      ;
    }

    Chapter c = new Chapter(name, pages, channels, videos, presentations);
    return c;
  }

  public static List<Chapter> extractChapters(JSONArray jsonChapters) {
    List<Chapter> chapters = new ArrayList<>();
    for (int i = 0; i < jsonChapters.length(); i++) {
      try {
        chapters.add(extractChapter(jsonChapters.getJSONObject(i)));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return chapters;
  }

  public static Book parseBook(JSONObject jsonObject) {
    String name = getStringValue(jsonObject, JunedayStat.JDSTAT_BOOK_NAME, "");
    List<Chapter> chapters;
    try {
      JSONArray jsonChapters = jsonObject.getJSONArray(JunedayStat.JDSTAT_CHAPTERS);
      chapters = extractChapters(jsonChapters);
    } catch (JSONException e) {
      chapters = new ArrayList<>();
    }

    Book book = new Book(name, chapters);
    return book;
  }

  public static List<Book> parseBooks(JSONArray jsonBooks) {
    List<Book> books = new ArrayList<>();
    for (int i = 0; i < jsonBooks.length(); i++) {
      try {
        books.add(parseBook(jsonBooks.getJSONObject(i)));
      } catch (JSONException e) {
        ;
      }
    }
    return books;
  }

  public static CodeSummary parseCodeSummary(JSONArray codeList) {
    CodeSummary codeSummary = new CodeSummary();
    for (int i=0; i<codeList.length(); i++) {
      JSONObject lang = null;
      try {
        lang = codeList.getJSONObject(i);
        String type = getStringValue(lang, JunedayStat.JDSTAT_SOURCE_CODE_TYPE, "unknown");
        int loc = getIntValue(lang, JunedayStat.JDSTAT_SOURCE_CODE_LOC, 0);
        int files = getIntValue(lang, JunedayStat.JDSTAT_SOURCE_CODE_FILES, 0);
        codeSummary.addLanguage(CodeSummary.ProgLang.valueOf(type), new CodeSummary.Stat(CodeSummary.ProgLang.valueOf(type), loc, files));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return codeSummary ;
  }


  public static JunedayStat jsonToJunedayStat(LocalDate date, String jsonData) {
    return jsonToJunedayStat(date, new JSONObject(jsonData));
  }
  
  public static JunedayStat jsonToJunedayStat(LocalDate date, JSONObject json) {
    JSONObject videoSummary;
    JSONObject bookSummary;
    JSONArray codeSummary;
    JSONObject podStat;
    JSONArray bookArray;
    try {
      bookSummary = json.getJSONObject(JunedayStat.JDSTAT_BOOK_SUMMARY);
      codeSummary = json.getJSONArray(JunedayStat.JDSTAT_SOURCE_CODE);
      videoSummary = json.getJSONObject(JunedayStat.JDSTAT_VIDEO);
      podStat = json.getJSONObject(JunedayStat.JDSTAT_POD);
      bookArray = json.getJSONArray(JunedayStat.JDSTAT_BOOKS);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }

    BooksSummary bSum = parseBookSummary(bookSummary);
    CodeSummary cSum = parseCodeSummary(codeSummary);
    VideoStat vSum = parseVideoSummary(videoSummary);
    PodStat pSum = parsePodStat(podStat);

    List<Book> books = parseBooks(bookArray);

    JunedayStat jds = new JunedayStat(date, bSum, cSum, vSum, pSum, books);

    return jds;
  }


}
