package se.juneday.junedaystat.ui;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import se.juneday.junedaystat.domain.Book;
import se.juneday.junedaystat.domain.BooksSummary;
import se.juneday.junedaystat.domain.Chapter;
import se.juneday.junedaystat.domain.CodeSummary;
import se.juneday.junedaystat.domain.JunedayStat;
import se.juneday.junedaystat.domain.PodStat;
import se.juneday.junedaystat.domain.Presentation;
import se.juneday.junedaystat.domain.VideoStat;
import static se.juneday.junedaystat.net.StatisticsParser.jsonToJunedayStat;
import se.juneday.junedaystat.utils.Utils;

public class JDCli {

  public static JunedayStat readFile(String dateStr) {
    String fileName = "data/jd-stats-" + dateStr + ".json";
    LocalDate date = Utils.stringToLocalDate(dateStr);
    try {
      return jsonToJunedayStat(date,
                               new String(Files.readAllBytes(Paths.get(fileName))));
    } catch (Exception e) {
      System.err.println("Failed, parsing: " + fileName);
    }
    return null;
  }
  
  public static void main(String[] args) {
    JunedayStat startStats = readFile("20181107");
    JunedayStat stopStats = readFile("20190114");

    //System.out.println(startStats.books().get(0));
    for (Book b : stopStats.books()) {
      System.out.println(b.name() + " " + b.pages());
    }
           //    System.out.println(stopStats.books().get(0));
    
  }
  
}
