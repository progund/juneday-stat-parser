package se.juneday.junedaystat.domain;

public class PodStat {

  public PodStat(int podCasts) {
    this.podCasts = podCasts;
  }

  public int podCasts() {
    return podCasts;
  }

  private int podCasts;

}
