package se.juneday.junedaystat.domain.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import se.juneday.junedaystat.domain.*;
import se.juneday.junedaystat.net.*;

public class TestJunedayStat {

  public static void main(String[] args) throws IOException {
    String jsonString =
      new String(Files.readAllBytes(Paths.get("data/jd-stats.json")));

    JSONObject jsonData = new JSONObject(jsonString);
    
    JunedayStat jds = StatisticsParser.jsonToJunedayStat(jsonData);

    System.out.println("Books");
    System.out.println(jds.booksSummary());

    System.out.println("Code");
    System.out.println(jds.codeSummary());

    System.out.println("Video");
    System.out.println(jds.videoSummary());
    
  }    
}


