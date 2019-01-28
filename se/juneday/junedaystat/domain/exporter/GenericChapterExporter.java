package se.juneday.junedaystat.domain.exporter;

import java.util.List;

import se.juneday.junedaystat.domain.Chapter;
import se.juneday.junedaystat.domain.Presentation;

public class GenericChapterExporter implements Chapter.Exporter {

  String name;
  int pages;
  List<String> channelUrls;
  List<String> videoUrls;
  List<Presentation> presentations;

  public void addName(String name){
    this.name = name;
  }
  public void addPages(int pages){
    this.pages = pages;
  }
  public void addChannelUrls(List<String> channels){
    this.channelUrls = channels;
  }
  public void videoUrls(List<String> videos){
    this.videoUrls = videos;
  }
  public void presentations(List<Presentation> presentations){
    this.presentations = presentations;
  }

}
