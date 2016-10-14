package me.ryancoley.comix;

/**
 * Created by Ryan on 5/28/2015.
 */
public class SeriesListElement {
    private long issueCount;
    private String series, publisher, id;

    //TODO change String[] to Map
    SeriesListElement(long count, String[] values) {
        this.issueCount = count;
        this.publisher = values[1];
        this.series = values[0];
        this.id = values[2];


    }

    public long getIssueCount() {
        return issueCount;
    }

    public String getSeries() {
        return series;
    }

    public String getSeriesID() {
        return id;
    }

    public String getPublisher() {
        return publisher;
    }

    public String toString() {
        return this.series;
    }
}
