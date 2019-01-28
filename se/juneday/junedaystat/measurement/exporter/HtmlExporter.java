package se.juneday.junedaystat.measurement.exporter;

import se.juneday.junedaystat.measurement.Measurement;

import se.juneday.junedaystat.measurement.Measurement.MStat;
import se.juneday.junedaystat.measurement.Measurement.MPod;
import se.juneday.junedaystat.measurement.Measurement.MVideo;
import se.juneday.junedaystat.measurement.Measurement.MBook;
import se.juneday.junedaystat.measurement.Measurement.MCode;
import se.juneday.junedaystat.measurement.Measurement.MChapter;

import se.juneday.junedaystat.domain.CodeSummary;
import se.juneday.junedaystat.domain.JunedayStat;

import java.util.Map;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

public class HtmlExporter {

  String CSS_STYLE="  <style type=\"text/css\">       .rTable {      display: table;      width: 100%;      }      .rTableRow {      display: table-row;      }      .rTableHeading {      display: table-header-group;      background-color: #ddd;      }      .rTableCell, .rTableHead {      display: table-cell;      padding: 3px 10px;      border: 1px solid #999999;      }      .rTableHeading {      display: table-header-group;      background-color: #ddd;      font-weight: bold;      }      .rTableFoot {      display: table-footer-group;      font-weight: bold;      background-color: #ddd;      }      .rTableBody {      display: table-row-group;      }</style>";

  private Measurement measurement;
  private long days;

  private static final String ratioFormat = "%d (%.2f)";
  
  
  public HtmlExporter(Measurement measurement) {
    this.measurement = measurement;
    days = DAYS.between(measurement.startJunedayStat().date(),
                        measurement.stopJunedayStat().date());
  }
  
  public String export()  {
    StringBuilder builder = new StringBuilder();
    builder
      .append("<html>")
      .append("  <body>")
      .append(CSS_STYLE)
      .append(" start: " + measurement.startJunedayStat().date() + "<br>")
      .append(" stop: " + measurement.stopJunedayStat().date() + "<br>")
      .append(" days: " + days + "<br>");
    
    builder.append(export(measurement.stat()));

    builder
      .append("  </body>")
      .append("</html>");

    return builder.toString();
  }

  public String export(MStat stat)  {
    StringBuilder builder = new StringBuilder();
    builder
      .append(export(stat.code()));

    builder.append("Books<br>");
    for (MBook book : stat.books()) {
      builder.append("Book " + book.name() + "<br>");
      builder.append(export(book));
    }
    
    return builder.toString();
  }

  public String export(MPod pod)  {
    return "";

  }

  public String export(MVideo video)  {
    return "";

  }

  public String export(MBook book)  {
    StringBuilder builder = new StringBuilder();
    builder
      .append("  <div class=\"rTable\">\n\n")
      .append("    <div class=\"rTableRow\">\n")
      .append("      <div class=\"rTableHead\"><strong>Name</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Channels</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Videos</strong></div>\n")
      .append("      <div class=\"rTableHead\"><strong>Presentation</strong></div>\n")
      .append("    </div>\n\n");

    for (MChapter mchapter: book.chapters()) {
      builder.append(export(mchapter));
    }
    builder.append("  </div>\n\n");
    
    return builder.toString();
    
  }

  private String statPerDay(int value) {
    double ratio = value/((double) days);
    return String.format(ratioFormat, value, ratio);
  }

  public String export(MCode code)  {
    StringBuilder builder = new StringBuilder();
    Map<CodeSummary.ProgLang, CodeSummary.Stat> langStat =
        code.langStat();
    int filesSum = 0;
    int locSum = 0;
    
    builder
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

  public String export(MChapter chapter)  {
    StringBuilder builder = new StringBuilder();
    
    builder
      .append("    <div class=\"rTableRow\">\n")
      .append("      <div class=\"rTableHead\">" + chapter.name() + "</div>\n")
      .append("      <div class=\"rTableHead\">" 
              + statPerDay(( chapter.diffChannelsStop().size() - chapter.diffChannelsStart().size() ))
              + "</div>\n")
      .append("      <div class=\"rTableHead\">" 
              + statPerDay(( chapter.diffVideosStop().size() - chapter.diffVideosStart().size() ))
              + "</div>\n")
      .append("      <div class=\"rTableHead\">" 
              + statPerDay(( chapter.diffPresentationsStop().size() - chapter.diffPresentationsStart().size() ))
              + "</div>\n")
      .append("    </div>\n\n");
    
    return builder.toString();
  }



}
