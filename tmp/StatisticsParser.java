package se.juneday.junedaystat.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import se.juneday.junedaystat.domain.BooksSummary;
import se.juneday.junedaystat.domain.CodeSummary;
import se.juneday.junedaystat.domain.JunedayStat;
import se.juneday.junedaystat.domain.VideoSummary;

public class StatisticsParser {


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

        BooksSummary bookSummary = new BooksSummary(books, pages, channels, presentations, presPages, videos);
        return bookSummary;
    }


    public static VideoSummary parseVideoSummary(JSONObject videoObject) {
        int videos = getIntValue(videoObject, JunedayStat.JDSTAT_VIDEO_VIDEOS, 0);
        return new VideoSummary(videos);
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
            codeSummary.addLanguage(type, new CodeSummary.Stat(type, loc, files));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return codeSummary ;
    }

    public static JunedayStat jsonToJunedayStat(JSONObject json) {
        JSONObject videoSummary ;
        JSONObject bookSummary ;
        JSONArray codeSummary;
        try {
            bookSummary = json.getJSONObject(JunedayStat.JDSTAT_BOOK_SUMMARY);
            codeSummary = json.getJSONArray(JunedayStat.JDSTAT_SOURCE_CODE);
            videoSummary = json.getJSONObject(JunedayStat.JDSTAT_VIDEO);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        BooksSummary bSum = parseBookSummary(bookSummary);
        CodeSummary cSum = parseCodeSummary(codeSummary);
        VideoSummary vSum = parseVideoSummary(videoSummary);
        JunedayStat jds = new JunedayStat(bSum, cSum, vSum);

        return jds;
    }

}
