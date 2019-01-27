package se.juneday.junedaystat.domain.exporter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.juneday.junedaystat.domain.Book;
import se.juneday.junedaystat.domain.Chapter;

public class JsonBookExporter extends GenericBookExporter {

  public String toString() {
    JSONObject bookJson = new JSONObject();
    JSONArray chaptersJson = new JSONArray();
    JsonChapterExporter ce = new JsonChapterExporter();
    for (Chapter c : chapters) {
      c.export(ce);
      chaptersJson.put(new JSONObject(ce.toString()));
    }
    bookJson.put("name", name);
    bookJson.put("chapters",chaptersJson);
    return bookJson.toString();
  }
  
}
