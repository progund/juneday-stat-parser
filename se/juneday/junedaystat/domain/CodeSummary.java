package se.juneday.junedaystat.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CodeSummary {

    public final static String LANG_JAVA = "Java";
    public final static String LANG_BASH = "Bash";
    public final static String LANG_C = "C";
    public static final String LANG_BUILD = "Build";


    public static class Stat {
        String lang;
        int loc;

        public String getLang() {
            return lang;
        }

        public int getLoc() {
            return loc;
        }

        public int getFiles() {
            return files;
        }

        int files;

        public Stat(String lang, int loc, int files) {
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

    private Map<String, Stat> langStat;

    public Stat stat(String lang) {
        return langStat.get(lang);
    }

    public CodeSummary() {
        langStat = new HashMap<>();
    }

    public void addLanguage(String lang, Stat stat) {
        langStat.put(lang, stat);
    }

    @Override
    public String toString() {
        return "CodeSummary{" +
                "langStat=" + langStat +
                '}';
    }

}
