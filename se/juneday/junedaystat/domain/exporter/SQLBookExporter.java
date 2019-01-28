package se.juneday.junedaystat.domain.exporter;
import java.util.List;

import se.juneday.junedaystat.domain.Book;
import se.juneday.junedaystat.domain.Chapter;

public class SQLBookExporter extends GenericBookExporter {

  public String toString() {
    return "INSERT INTO BOOK";
  }
  
}
