package se.juneday.junedaystat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.time.LocalDate;
import java.util.function.Predicate;
import javax.servlet.*;
import javax.servlet.http.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

import se.juneday.junedaystat.measurement.Measurement;
import se.juneday.junedaystat.domain.JunedayStat;
import static se.juneday.junedaystat.net.StatisticsParser.jsonToJunedayStat;
import se.juneday.junedaystat.net.StatisticsParser;
import se.juneday.junedaystat.measurement.exporter.HtmlExporter;
import se.juneday.junedaystat.utils.Utils;

public class JunedayStatAPI extends HttpServlet{

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException{
    request.setCharacterEncoding(UTF_8.name());
    PrintWriter out =
      new PrintWriter(new OutputStreamWriter(response.getOutputStream(),
                                             UTF_8), true);

    String startStr = request.getParameter("start");
    String stopStr = request.getParameter("stop");

    if (stopStr ==null || stopStr.equals("") || stopStr.equals("today")) {
      stopStr = Utils.dateToString(LocalDate.now().minus(1, DAYS)); // TODO: TODAY
    }

    if (startStr.equals("") || startStr.equals("daily")) {
      startStr = Utils.dateToString(LocalDate.now().minus(2, DAYS)); // TODO: -1 .... not -2
    } else if (startStr.equals("") || startStr.equals("week")) {
      startStr = Utils.dateToString(LocalDate.now().minus(1, WEEKS));
    } else if (startStr.equals("month")) {
      startStr = Utils.dateToString(LocalDate.now().minus(1, MONTHS));
    } else if (startStr.equals("year")) {
      startStr = Utils.dateToString(LocalDate.now().minus(1, YEARS));
    } else if (startStr.equals("2017")) {
      startStr = "20170313";
      stopStr = "20171231";
    } else if (startStr.equals("2018")) {
      startStr = "20180101";
      stopStr = "20181231";
    } else if (startStr.equals("2019")) {
      startStr = "20190101";
      stopStr = Utils.dateToString(LocalDate.now().minus(1, DAYS));
    }
    
    System.out.println(" --=====| " + startStr + " " + stopStr + " |=====------");

    try {
      JunedayStat start = StatisticsParser.readFile(startStr);
      JunedayStat stop = StatisticsParser.readFile(stopStr);
      Measurement measurement = new Measurement(start, stop);    
      
      response.setContentType("text/html;charset="+UTF_8.name());
      HtmlExporter he = new HtmlExporter(measurement);
      
      out.println(he.export());
    } catch (java.time.format.DateTimeParseException e) {
      out.println("Bad parameters.... <a href=\"/search.html\">search again</a>");
    }
    out.close();
  }
}