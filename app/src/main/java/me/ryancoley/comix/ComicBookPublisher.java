package me.ryancoley.comix;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by Ryan on 8/7/2016.
 */
public class ComicBookPublisher extends SugarRecord {
    private String name;

    public ComicBookPublisher() {
    }

    public ComicBookPublisher(String pub) {
        this.name = pub;
        this.save();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String p) {
        this.name = p;
        this.save();
    }

    /*public List<String> getAllSeries(){
        List<ComicBookSeries> series = ComicBookSeries.listAll(ComicBookSeries.class);
        List<String> seriesList = new LinkedList<>();
        for(ComicBookSeries s:series){
            if(s.getPublisherName().equals(this.name)) seriesList.add(s.getPublisherName());
        }
        return seriesList;
    }*/

    public ComicBookPublisher findPublisher(String p) {
        final String[] search = {p};
        List<ComicBookPublisher> publisher = ComicBookPublisher.find(ComicBookPublisher.class, "name=?", search);
        if (publisher.size() == 0) return null;
        return publisher.get(0);
    }
}
