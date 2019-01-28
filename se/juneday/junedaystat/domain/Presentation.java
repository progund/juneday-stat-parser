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

  @Override
  public boolean equals(Object o) {
    if (o ==null) {
      return false;
    }
    if (! (o instanceof Presentation) ) {
      return false;
    }
    Presentation other = (Presentation) o;
    if (name.equals(other.name) && pages == other.pages ) {
      return true;
    }
    return false;
  }
  
  public interface Exporter {
    void addName(String name);
    void addPages(int pages);
  }

  public void export(Exporter exp) {
    exp.addName(name);
    exp.addPages(pages);
  }
}
