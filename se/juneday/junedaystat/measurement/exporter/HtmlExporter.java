package se.juneday.junedaystat.measurement.exporter;

import se.juneday.junedaystat.domain.Presentation;

import se.juneday.junedaystat.measurement.Measurement;
import se.juneday.junedaystat.measurement.Measurement.MStat;
import se.juneday.junedaystat.measurement.Measurement.MPod;
import se.juneday.junedaystat.measurement.Measurement.MVideo;
import se.juneday.junedaystat.measurement.Measurement.MBook;
import se.juneday.junedaystat.measurement.Measurement.MCode;
import se.juneday.junedaystat.measurement.Measurement.MChapter;

import se.juneday.junedaystat.domain.CodeSummary;
import se.juneday.junedaystat.domain.JunedayStat;

import se.juneday.junedaystat.utils.Utils;

import java.util.Map;
import java.util.Collections;
import java.time.LocalDate;
import java.io.File;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

public class HtmlExporter {

  private static final String WIKI_URL = "http://wiki.juneday.se/mediawiki/index.php";
  private static final String JSON_URL = "http://rameau.sandklef.com/junedaywiki-stats/";
  private static final String PDF_URL = "http://rameau.sandklef.com/juneday-pdf/";
    public static final String JDS_LINKS =     "[ <a href=\"https://programmingpedagogy.wordpress.com/\">English blog</a> | <a href=\"https://programmeringspedagogik.wordpress.com\">Swedish blog</a> | <a href=\"http://www.juneday.se\"> Juneday</a> | <a href=\"https://juneday.podbean.com\"> Swedish podcast</a> | <a href=\"https://github.com/progund\"> git repositories (github.com/progund)</a> | <a href=\"https://twitter.com/prog_edu\"> Twitter </a> | <a href=\"https://vimeo.com/user52531669\">Vimeo</a> | <a href=\"http://stat.juneday.se\">Juneday stats</a> ] <br>";



    
  private final static String CSS_STYLE="  <style type=\"text/css\">       .rTable {      display: table;      }      .rTableRow { display: table-row;      }      .rTableHeading {      display: table-header-group;      background-color: #988;      }      .rTableCell, .rTableHead {   display: table-cell;      padding: 3px 10px;      border: 1px solid #999999;      }      .rTableHeading {      display: table-header-group;      background-color: #ddd;      font-weight: bold; }      .rTableFoot {      display: table-footer-group;      font-weight: bold;      background-color: #ddd;      }      .rTableBody {      display: table-row-group;} .collapsible {  background-color: #eee;  color: black;  cursor: pointer;  padding: 0px;  width: 100%;  border: none;  text-align: left;  outline: none;  font-size: 15px; }.active, .collapsible:hover {  background-color: #ddd;}.content {  padding: 0 18px;  display: none;  overflow: hidden;  background-color: #eee;}  </style>";

  private final static String SCRIPT = "<script>var coll = document.getElementsByClassName(\"collapsible\");var i;for (i = 0; i < coll.length; i++) {  coll[i].addEventListener(\"click\", function() {    this.classList.toggle(\"active\");    var content = this.nextElementSibling;    if (content.style.display === \"block\") {      content.style.display = \"none\";    } else {      content.style.display = \"block\";    }  });}</script>";

  private Measurement measurement;
  private long days;

  private static final String ratioFormat = "%d (%.2f)";
  private static final String smallRatioFormat = " (%.2f)";
  
  
  public HtmlExporter(Measurement measurement) {
      this.measurement = measurement;
      days = DAYS.between(measurement.startJunedayStat().date(),
			  measurement.stopJunedayStat().date());
  }

    public String compareLink(String date) {
	return " - compare this version to: [ "
	    + "<a href=\"/search?start=daily&stop=" + date + "\">the day before</a> | "
	    + "<a href=\"/search?start=week&stop=" + date + "\">a week before</a> | "
	    + "<a href=\"/search?start=month&stop=" + date + "\">a month before</a> | "
	    + "<a href=\"/search?start=year&stop=" + date + "\">a year before</a> ]";
    }
    
  public String export()  {
    StringBuilder builder = new StringBuilder();
    builder
      .append("<html>")
      .append("<head>")
      .append(CSS_STYLE)
      .append("</head>")
      .append("  <body>")
      .append(JDS_LINKS)
      .append("<br>")
      .append("<h1>Information about the search</h1>")
      .append("<ul>")
	.append("  <li>start: " + measurement.startJunedayStat().date() + compareLink(Utils.dateToString(measurement.startJunedayStat().date()))+ "</li>")
	.append("  <li>stop: " + measurement.stopJunedayStat().date() + compareLink(Utils.dateToString(measurement.stopJunedayStat().date()))+ "</li>")
	.append("  <li> days: " + days + "</li>")
	.append("</ul>");
    
    builder
      .append(export(measurement.stat()))
      .append("\n")
      .append(SCRIPT)
      .append("\n");
    
    builder
      .append("  </body>")
      .append("</html>");

    return builder.toString();
  }

  public String export(MStat stat)  {
    StringBuilder builder = new StringBuilder();
    builder
      .append(export(stat.code()));

    int pageSum=0;
    int channelSum=0;
    int videoSum=0;
    int presSum=0;
    Collections.sort(stat.books(), (b1, b2) -> (b2.pages() - b1.pages()));
    for (MBook book : stat.books()) {
      pageSum += book.pages();
      channelSum += book.channels();
      videoSum += book.videos();
      presSum += book.presentations();
    }
    
    builder
      .append("<h1>Books</h1>")
      .append("Books [")
      .append(intToColorString(pageSum))
      .append(smallStatPerDay(pageSum))
      .append(" pages ")
      .append(" | ")
      .append(intToColorString(channelSum))
      .append(smallStatPerDay(channelSum))
      .append(" channels ")
      .append(" | ")
      .append(intToColorString(videoSum))
      .append(smallStatPerDay(videoSum))
      .append(" videos ")
      .append(" | ")
      .append(intToColorString(presSum))
      .append(smallStatPerDay(presSum))
      .append(" presentations] ");
    
    for (MBook book : stat.books()) {
      builder.append("<p>Book: ")
        .append(book.name())
        .append("  [ ")
        .append(intToColorString(book.pages()))
        .append(" pages ")
        .append(" | ")
        .append(intToColorString(book.channels()))
        .append(" channels ")
        .append(" | ")
        .append(intToColorString(book.videos()))
        .append(" videos ")
        .append(" | ")
        .append(intToColorString(book.presentations()))
        .append(" presentations ")
        .append(" ]")
        .append(" [ <a href=\"" + WIKI_URL+"/"+book.name() + "\">current </a>")
        .append(" | <a href=\"" + WIKI_URL+"?title=" + book.name() + "&action=history\"> history</a> | ")
        .append(" [ JSON:")
        .append("<a href=\"" + JSON_URL + "/" + Utils.dateToString(measurement.startJunedayStat().date()) + "/jd-stats.json\"> " + Utils.dateToString(measurement.startJunedayStat().date()) + "</a> |")
        .append("<a href=\"" + JSON_URL + "/" + Utils.dateToString(measurement.stopJunedayStat().date()) + "/jd-stats.json\"> " + Utils.dateToString(measurement.stopJunedayStat().date()) + "</a> ]")
        .append(" ]")
        .append("</p>")
        .append(export(book));
    }
    return builder.toString();
  }

  public String export(MPod pod)  {
    return "";

  }

  public String export(MVideo video)  {
    return "";

  }

  private String intToColorString(int i) {
    if (i<0) {
      return "<span style=\"color:red; float:none;\" >" + i + "</span>";
    } else if (i>0) {
      return "<span style=\"color:green; float:none;\">" + i + "</span>";
    } 
    return "<span style=\"color:black; float:none;\">" + i + "</span>";
  }
  
  public String export(MBook book)  {
    StringBuilder builder = new StringBuilder();
    builder
      .append("  <button class=\"collapsible\">Show details</button>")
      .append("  <div class=\"content\">")
      .append("  <div class=\"rTable\">\n\n")
      .append("    <div class=\"rTableRow\">\n")
      .append("      <div class=\"rTableHead\"><strong>Chapter</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Pages</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Channels</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Videos</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Presentation</strong></div>\n")
      .append("    </div>\n\n");

    for (MChapter mchapter: book.chapters()) {
      builder.append(export(mchapter));
    }

    builder
      .append("    <div class=\"rTableRow\">\n")
      .append("      <div class=\"rTableHead\"><strong>Total</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>" + statPerDay(book.pages()) + "</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>" + statPerDay(book.channels()) + "</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>" + statPerDay(book.videos()) + "</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>" + statPerDay(book.presentations()) + "</strong></div>\n")
      .append("  </div>");

      builder
      .append("  </div>")
      .append("  </div>");
    
    return builder.toString();
    
  }

  private String statPerDay(int value) {
    double ratio = value/((double) days);
    return String.format(ratioFormat, value, ratio);
  }

  private String smallStatPerDay(int value) {
    double ratio = value/((double) days);
    return String.format(smallRatioFormat, ratio);
  }

  public String export(MCode code)  {
    StringBuilder builder = new StringBuilder();
    Map<CodeSummary.ProgLang, CodeSummary.Stat> langStat =
        code.langStat();
    int filesSum = 0;
    int locSum = 0;
    
    builder
      .append("<h1>Soure code</h1>")
      .append("  <div class=\"rTable\">\n\n")
      .append("    <div class=\"rTableRow\">\n")
      .append("      <div class=\"rTableHead\"><strong>Language</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Files</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Lines of code</strong></div>\n")
      .append("    </div>\n\n");
    for (Map.Entry<CodeSummary.ProgLang, CodeSummary.Stat> entry : langStat.entrySet())
      {
        CodeSummary.Stat stat = entry.getValue();
        builder
          .append("    <div class=\"rTableRow\">\n")
          .append("      <div class=\"rTableCell\">" + stat.lang() + "</div>\n")
          .append("      <div class=\"rTableCell\">" + statPerDay(stat.files()) + "</div>\n")
          .append("      <div class=\"rTableCell\">" + statPerDay(stat.loc()) + "</div>\n")
          .append("    </div>\n");
        filesSum += stat.files();
        locSum += stat.loc();
      }
    builder
      .append("    <div class=\"rTableRow\">\n")
      .append("      <div class=\"rTableCell\"><strong>Total</strong></div>\n")
      .append("      <div class=\"rTableCell\"><strong>" + statPerDay(filesSum) + "</strong></div>\n")
      .append("      <div class=\"rTableCell\"><strong>" + statPerDay(locSum) + "</strong></div>\n")
      .append("    </div>\n");
    builder.append("  </div>\n\n");
    
    return builder.toString();
  }

    private String pdfLink(LocalDate date, String fileName) {

	String dateStr = Utils.dateToString(date);
	File f = new File("/var/www/html/juneday-pdf/" + dateStr + "/junedaywiki/" + fileName + ".pdf");
	//	System.out.println("Checking file: " + f);
	if (f.exists()) {
	    //	    System.err.println(" - Exist existing: " +f );
	    return " <a href=\""
		+ PDF_URL
		+ "/"
		+ dateStr
		+ "/junedaywiki/"
		+ fileName
		+ ".pdf\">"
		+ date
		+ "</a> ";
	} /*else {
	    System.err.print(", " +f );
	    }*/
	return " NA ";
    }

    private String pdfLink(LocalDate start, LocalDate stop, String chapter) {
	
	String fileName = chapter.replace(" ","_") ;
	String startRef = pdfLink(start, fileName);
	String stopRef = pdfLink(stop, fileName);
	String diffLink = "";
	String startDateStr = Utils.dateToString(start);
	String stopDateStr = Utils.dateToString(stop);
	
	if (!startRef.equals(" NA ") && !stopRef.equals(" NA ") ) {
	    diffLink = " | <a href=\"/diff?start=" + startDateStr + "&stop=" + stopDateStr + "&file=" + fileName + "\">diff</a>" ;
	}
	
	return startRef + " | " + stopRef + diffLink;
    } 
    
    
  public String export(MChapter chapter)  {
    StringBuilder builder = new StringBuilder();

          //      .append("      <div class=\"rTableHead\"><a href=\"#collapse-" + chapter.name() + "\" data-toggle=\"collapse\"> " + chapter.name() + "</a><div id=\"collapse-" + chapter.name() + "\" class=\"collapse\"> Lorem ipsum dolor text.... </div></div> \n")

    builder
      .append("    <div class=\"rTableRow\">\n")
	.append("      <div class=\"rTableHead\">" + chapter.name() + "<br> [Wiki: <a href=\"" + WIKI_URL+"/" + chapter.name() + "\">current</a> | <a href=\"" + WIKI_URL+"?title=" + chapter.name() + "&action=history\">history</a>]")
        .append("<br>[PDF: " + pdfLink(measurement.startJunedayStat().date(),measurement.stopJunedayStat().date(),chapter.name())+ "]")
	.append("</div>\n")
      .append("      <div class=\"rTableHead\">" + statPerDay(chapter.pages()) + "</div>\n");

    // gen:  http://rameau.sandklef.com/juneday-pdf//20181229/junedaywiki/Assignment:Exposing_data_over_http_lab2_Android_Client.pdf
    // real: http://rameau.sandklef.com/juneday-pdf//20190128/junedaywiki/Assignment:Exposing_data_over_http_lab2_Android_Client.pdf
    
    builder.append("      <div class=\"rTableHead\">" 
                   + statPerDay(( chapter.diffChannelsStop().size() - chapter.diffChannelsStart().size() )));;
    if ( (chapter.diffChannelsStop().size() > 0)
         ||
         (chapter.diffChannelsStart().size() > 0) )  {
      builder.append("<br>");
    }
    for (String url : chapter.diffChannelsStop()) {
      builder.append(" + <a href=\"" + url + "\">" + url + "</a><br>");
    }
    for (String url : chapter.diffChannelsStart()) {
      builder.append(" - <a href=\"" + url + "\">" + url + "</a><br>");
    }
    builder.append("</div>\n");
    
    builder
      .append("      <div class=\"rTableHead\">" 
              + statPerDay(( chapter.diffVideosStop().size() - chapter.diffVideosStart().size() )));
    if ( (chapter.diffVideosStop().size() > 0)
         ||
         (chapter.diffVideosStart().size() > 0) )  {
      builder.append("<br>");
    }
    for (String url : chapter.diffVideosStop()) {
      builder.append(" + <a href=\"" + url + "\">" + url + "</a><br>");
    }
    for (String url : chapter.diffVideosStart()) {
      builder.append(" - <a href=\"" + url + "\">" + url + "</a><br>");
    }
    builder.append("</div>\n");

    builder.append("      <div class=\"rTableHead\">" 
                   + statPerDay(( chapter.diffPresentationsStop().size() - chapter.diffPresentationsStart().size() )));
    if ( (chapter.diffPresentationsStop().size() > 0)
         ||
         (chapter.diffPresentationsStart().size() > 0) )  {
      builder.append("<br>");
    }
    for (Presentation p : chapter.diffPresentationsStop()) {
      builder.append(" + " + p.name() + "  (" + p.pages()+ " pages)<br>");
    }
    for (Presentation p : chapter.diffPresentationsStart()) {
      builder.append(" - " + p.name() + "  (" + p.pages()+ " pages)<br>");
    }
    
    builder.append("</div>\n")
      .append("    </div>\n\n");
    
    return builder.toString();
  }



}
