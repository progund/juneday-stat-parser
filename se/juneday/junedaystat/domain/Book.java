package se.juneday.junedaystat.domain;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

import se.juneday.junedaystat.utils.Utils;

public class Book {

  private List<Chapter> chapters;
  private String name;

  public Book(String name, List<Chapter> chapters) {
    this.name = name;
    this.chapters = chapters;
  }

  public List<Chapter> chapters() {
    return chapters;
  }

  public String name() {
    return name;
  }

  public int pages() {
    int sum=0;
    for (Chapter c : chapters) {
      sum += c.pages();
    }
    return sum;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder
      .append(name)
      .append(" (").append(pages()).append(")").append(Utils.NEWLINE);
    builder.append("  Chapters:").append(Utils.NEWLINE);
    for (Chapter c : chapters) {
      builder
        .append("    ")
        .append(c)
        .append(Utils.NEWLINE);
    }
    return builder.toString();
  }

}
