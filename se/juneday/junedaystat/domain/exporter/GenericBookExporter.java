package se.juneday.junedaystat.domain.exporter;

import java.util.List;

import se.juneday.junedaystat.domain.Book;
import se.juneday.junedaystat.domain.Chapter;

public class GenericBookExporter implements Book.Exporter {

  List<Chapter> chapters;
  String name;

  public void addName(String name) {
    this.name = name;
  }

  public void addChapters(List<Chapter> chapters){
    this.chapters = chapters;
  }

}
