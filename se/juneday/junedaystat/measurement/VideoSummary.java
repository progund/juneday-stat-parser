package se.juneday.junedaystat.domain;

public class VideoSummary {

  private int videos;

  public int videos() {
    return videos;
  }

  public VideoSummary(int videos) {
    this.videos = videos;
  }

  public String toString() {
    return String.valueOf(videos);
  }
  
}
