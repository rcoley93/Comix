package me.ryancoley.comix;

import com.orm.SugarRecord;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ryan on 8/7/2016.
 */
public class ComicBookSeries extends SugarRecord implements Comparable<ComicBookSeries> {
    private ComicBookPublisher publisher;
    private String name, barcode = "";

    public ComicBookSeries() {
    }

    public ComicBookSeries(String series, String sPub) {
        this.name = series;
        ComicBookPublisher cbp = new ComicBookPublisher();
        ComicBookPublisher cbpPub = cbp.findPublisher(sPub);
        if (cbpPub == null) this.publisher = new ComicBookPublisher(sPub);
        else this.publisher = cbpPub;
        this.save();
    }

    public ComicBookSeries(String series, String bc, String sPub) {
        this.name = series;
        this.barcode = bc;
        ComicBookPublisher cbp = new ComicBookPublisher();
        ComicBookPublisher cbpPub = cbp.findPublisher(sPub);
        if (cbpPub == null) this.publisher = new ComicBookPublisher(sPub);
        else this.publisher = cbpPub;
        this.save();
    }

    public static ComicBookSeries findSeries(String s, String p) {
        if (p != null) {
            List<ComicBookSeries> cbs = find(ComicBookSeries.class, "name=?", s);
            for (ComicBookSeries series : cbs) {
                if (series.getPublisherName().equals(p)) return series;
            }
            return null;
        }
        return ComicBookSeries.find(ComicBookSeries.class, "id=?", new String[]{s}).get(0);
    }

    public static ComicBookSeries findSeries(String barcode) {
        List<ComicBookSeries> series = ComicBook.listAll(ComicBookSeries.class);
        for (ComicBookSeries s : series) {
            if (s.getBarcode().equals(barcode)) return s;
        }
        return null;
    }

    public static List<ComicBookSeries> getPublishers(String strSeriesName) {
        List<ComicBookSeries> series = ComicBookSeries.listAll(ComicBookSeries.class);
        List<ComicBookSeries> result = new LinkedList<>();
        for (ComicBookSeries s : series) {
            if (s.getSeriesName().equals(strSeriesName)) result.add(s);
        }
        return result;
    }

    public String[] getSeriesListElement() {
        return new String[]{getSeriesName(), getPublisherName(), String.valueOf(this.getId())};
    }


   /* public void setSeriesName(String s){
        this.name = s;
        this.save();
    }*/

    public String getPublisherName() {
        return this.publisher.getName();
    }

    /*public void setPublisherName(String p){
        this.publisher.setName(p);
    }*/

    public String getSeriesName() {
        return this.name;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(String b) {
        this.barcode = b;
        this.save();
    }

    public Map<String, String> getSearchListElement() {
        Map<String, String> searchListElementDetails = new HashMap<>();

        searchListElementDetails.put("title", this.name);
        searchListElementDetails.put("publisher", this.getPublisherName());
        searchListElementDetails.put("id", String.valueOf(this.getId()));

        return searchListElementDetails;
    }

    public String countIssues() {
        return String.valueOf(ComicBook.find(ComicBook.class, "series = ?", String.valueOf(this.getId())).size());
    }

    @Override
    public int compareTo(ComicBookSeries another) {
        String thisTitle, anotherTitle;
        thisTitle = (this.name.substring(0, 4).toLowerCase().equals("the ")) ? this.name.substring(4) : this.name;
        anotherTitle = (another.getSeriesName().substring(0, 4).toLowerCase().equals("the ")) ? another.getSeriesName().substring(4) : another.getSeriesName();
        return thisTitle.compareTo(anotherTitle);
    }
}
