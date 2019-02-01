package se.juneday.junedaystat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.File;
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

public class DiffAPI extends HttpServlet{

    private boolean fileExists(String dateStr, String chapterName) {
	File f = new File("/var/www/html/juneday-pdf/"
			  + dateStr
			  + "/junedaywiki/"
			  + chapterName
			  + ".pdf");
	return f.exists();
    }
    
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException{
    request.setCharacterEncoding(UTF_8.name());
    PrintWriter out =
      new PrintWriter(new OutputStreamWriter(response.getOutputStream(),
                                             UTF_8), true);
    String startStr = request.getParameter("start");
    String stopStr = request.getParameter("stop");
    String file = request.getParameter("file");

    if ( fileExists(startStr, file) &&
	 fileExists(stopStr, file) ) {
	try { 
	    ProcessBuilder pb =
		new ProcessBuilder("bin/create-pdf-diff.sh",
				   startStr,
				   stopStr,
				   file);
	    Process p = pb.start();
	} catch (Exception e) {
	    System.err.println("Failed creating a process for creating diff pdf" + e);
	}
    }

    out.println("<html><body>");
    out.println(HtmlExporter.JDS_LINKS);
    out.println("We creating a diff file for you.... wait a few seconds and click");
    out.println("<a href=\"/diff-pdf/" + startStr + "-" + stopStr + "-" + file  + ".png\">here</a>");
    out.println("</body></html>");

    //http://rameau.sandklef.com:9997/diff-pdf/20181229-20190129-Database:Introduction_to_Databases.png
    //                                diff-pdf/20181229-20190129-Database:Introduction_to_Databases.pdf.pdf.png 
    System.err.println("DiffAPI: "
		       + startStr
		       + " " 
		       + fileExists(startStr, file)
		       + " " 
		       + stopStr
		       + " " 
		       + fileExists(stopStr, file)
		       + " " 
		       + file);
    
    
    
    out.close();
  }
}
