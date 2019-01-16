package se.juneday.junedaystat.domain;

import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

import se.juneday.junedaystat.utils.Utils;

public class Chapter {

  private String name;
  private int pages;
  private List<String> channelUrls;
  private List<String> videoUrls;
  private List<Presentation> presentations;

  public Chapter(String name, int pages, List<String> channelUrls,
      List<String> videoUrls,
      List<Presentation> presentations) {
    this.name = name;
    this.pages = pages;
    this.channelUrls = channelUrls;
    this.videoUrls = videoUrls;
    this.presentations = presentations;
  }

  public Chapter(String name, int pages) {
    this.name = name;
    this.pages = pages;
    channelUrls = new ArrayList<>();
    videoUrls = new ArrayList<>();
    presentations = new ArrayList<>();
  }

  public List<String> channelUrls() {
    return channelUrls;
  }

  public List<String> videoUrls() {
    return videoUrls;
  }

  public List<Presentation> presentations() {
    if (presentations!=null) {
      return presentations;
    }
    return new ArrayList<Presentation>();
  }

  public int presentationsPages() {
    int sum=0;
    for (Presentation p : presentations) {
      sum += p.pages();
    }
    return sum;
  }

  public void addChannelUrl(String channel) {
    channelUrls.add(channel);
  }

  public void addVideoUrl(String video) {
    videoUrls.add(video);
  }

  public void addPresentation(Presentation presentation) {
    presentations.add(presentation);
  }

  public int pages() {
    return pages;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder
      .append(name)
      .append(" (").append(pages()).append(")").append(Utils.NEWLINE);
    builder.append("      Channels:").append(Utils.NEWLINE);
    for (String s : channelUrls) {
      builder
        .append("        ")
        .append(s)
        .append(Utils.NEWLINE);
    }
    builder.append("      Videos:").append(Utils.NEWLINE);
    for (String s : videoUrls) {
      builder
        .append("        ")
        .append(s)
        .append(Utils.NEWLINE);
    }
    builder.append("      Presentations:").append("("+presentations.size()+")").append(Utils.NEWLINE);
    for (Presentation p : presentations) {
      builder
        .append("        ")
        .append(p)
        .append(Utils.NEWLINE);
    }

    return builder.toString();
  }


  public String name() {
    return name;
  }


}
