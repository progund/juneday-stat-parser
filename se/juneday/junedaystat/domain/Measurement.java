package se.juneday.junedaystat.domain;

import java.util.List;

public class Measurement {

  private JunedayStat startStat;
  private JunedayStat stopStat;

  public Measurement(JunedayStat startStat, 
                     JunedayStat stopStat) {
    this.startStat = startStat;
    this.stopStat = stopStat;
  }

  
  
}
