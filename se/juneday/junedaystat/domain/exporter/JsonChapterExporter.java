package se.juneday.junedaystat.domain.exporter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.juneday.junedaystat.domain.Chapter;
import se.juneday.junedaystat.domain.Presentation;

public class JsonChapterExporter extends GenericChapterExporter {

    public String toString() {
      JSONObject chapterJson = new JSONObject();
      JSONArray channelsJson = new JSONArray();
      JSONArray videosJson = new JSONArray();
      JSONArray presentationsJson = new JSONArray();
      chapterJson.put("name", name);
      chapterJson.put("pages", pages);
      for (String s : channelUrls) {
        channelsJson.put(s);
      }
      for (String s : videoUrls) {
        videosJson.put(s);
      }
      JsonPresentationExporter pe = new JsonPresentationExporter();
      for (Presentation p : presentations) {
        p.export(pe);
        // TODO: fix presentation exporter
        presentationsJson.put(new JSONObject(pe.toString()));
      }
      chapterJson.put("channels", channelsJson);
      chapterJson.put("videos", videosJson);
      chapterJson.put("presentations", presentationsJson);
      return chapterJson.toString();
    }

}
