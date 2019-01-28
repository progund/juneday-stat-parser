package se.juneday.junedaystat.domain.exporter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.juneday.junedaystat.domain.Presentation;

public class JsonPresentationExporter extends GenericPresentationExporter {

  public String toString() {
    JSONObject presentationJson = new JSONObject();
    presentationJson.put("name", name);
    presentationJson.put("pages",pages);
    return presentationJson.toString();
  }
  
}
