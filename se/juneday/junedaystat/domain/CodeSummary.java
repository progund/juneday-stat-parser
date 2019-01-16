package se.juneday.junedaystat.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CodeSummary {

  public enum ProgLang {
    Java,
    Bash,
    C,
    Doc,
    Build
  };


  public static class Stat {
    ProgLang lang;
    int loc;

    public ProgLang lang() {
      return lang;
    }

    public int loc() {
      return loc;
    }

    public int files() {
      return files;
    }

    int files;

    public Stat(ProgLang lang, int loc, int files) {
      this.loc = loc;
      this.files = files;
      this.lang = lang;
    }

    public Stat(String langString, int loc, int files) {
      ProgLang lang = ProgLang.valueOf(langString);
      this.loc = loc;
      this.files = files;
      this.lang = lang;
    }

    @Override
    public String toString() {
      return "Stat{" +
        ", lang=" + lang +
        ", loc=" + loc +
        ", files=" + files +
        '}';
    }
  }

  private Map<ProgLang, Stat> langStat;

  public int loc(ProgLang lang) {
    Stat stat = langStat.get(lang);
    if (stat == null) {
      return 0;
    }
    return stat.loc();
  }
  
  public int files(ProgLang lang) {
    Stat stat = langStat.get(lang);
    if (stat == null) {
      return 0;
    }
    return stat.files();
  }
  
  public Stat stat(String lang) {
    return langStat.get(lang);
  }

  public CodeSummary() {
    langStat = new HashMap<>();
  }

  public void addLanguage(ProgLang lang, Stat stat) {
    langStat.put(lang, stat);
  }

  @Override
  public String toString() {
    return "CodeSummary{" +
      "langStat=" + langStat +
      '}';
  }


}
