package se.juneday.junedaystat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Predicate;
import javax.servlet.*;
import javax.servlet.http.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import se.juneday.junedaystat.measurement.Measurement;
import se.juneday.junedaystat.domain.JunedayStat;
import static se.juneday.junedaystat.net.StatisticsParser.jsonToJunedayStat;
import se.juneday.junedaystat.net.StatisticsParser;
import se.juneday.junedaystat.measurement.exporter.HtmlExporter;

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

    
    JunedayStat start = StatisticsParser.readFile(startStr);
    JunedayStat stop = StatisticsParser.readFile(stopStr);
    Measurement measurement = new Measurement(start, stop);    
    
    response.setContentType("text/html;charset="+UTF_8.name());
    HtmlExporter he = new HtmlExporter(measurement);
    
    out.println(he.export());
    out.close();
  }
}
