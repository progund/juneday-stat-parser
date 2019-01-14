package se.juneday.junedaystat.domain;

public class Presentation {

  private int pages;
  private String name;

  public Presentation(String name, int pages) {
    this.pages = pages;
    this.name = name;
  }

  public int pages() {
    return pages;
  }

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return "" + name + " (" + pages + ")";
  }
}
