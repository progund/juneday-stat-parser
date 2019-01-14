package se.juneday.junedaystat.domain;

public class BooksSummary {

    public BooksSummary(int books, int pages, int channels, int presentations, int presentationsPages, int videos) {
        this.books = books;
        this.pages = pages;
        this.channels = channels;
        this.presentations = presentations;
        this.presentationsPages = presentationsPages;
        this.videos = videos;
    }

    private int books;
    private int pages;
    private int channels;
    private int presentations;
    private int presentationsPages;

    public int getBooks() {
        return books;
    }

    @Override
    public String toString() {
        return "BooksSummary{" +
                "books=" + books +
                ", pages=" + pages +
                ", channels=" + channels +
                ", presentations=" + presentations +
                ", presentationsPages=" + presentationsPages +
                ", videos=" + videos +
                '}';
    }

    public int getPages() {
        return pages;
    }

    public int getChannels() {
        return channels;
    }

    public int getPresentations() {
        return presentations;
    }

    public int getPresentationsPages() {
        return presentationsPages;
    }

    public int getVideos() {
        return videos;
    }

    private int videos;



}
