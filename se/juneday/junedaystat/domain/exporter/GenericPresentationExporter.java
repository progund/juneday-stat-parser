package se.juneday.junedaystat.domain.exporter;

import java.util.List;

import se.juneday.junedaystat.domain.Presentation;

public class GenericPresentationExporter implements Presentation.Exporter {

  int pages;
  String name;

  public void addName(String name) {
    this.name = name;
  }

  public void addPages(int pages){
    this.pages = pages;
  }

}
