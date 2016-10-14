package me.ryancoley.comix;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ryan on 5/30/2015.
 */
public class SearchListElement {

    private String title, publisher, series, issueNumber;
    private long id;
    private boolean isSeries;


    SearchListElement(long lID, Map<String, String> mValues, boolean blnIsSeries) {
        this.id = lID;
        this.title = mValues.get("title");
        this.publisher = mValues.get("publisher");
        this.series = (!blnIsSeries) ? mValues.get("series") : "";
        this.issueNumber = (!blnIsSeries) ? mValues.get("issue_number") : ComicBookSeries.findSeries(this.title, this.publisher).countIssues();
        this.isSeries = blnIsSeries;
    }

    public Map<String, String> getDetails() {
        Map<String, String> details = new HashMap<>();
        details.put("title", this.title);
        details.put("publisher", this.publisher);
        details.put("series", this.series);
        details.put("issue_number", this.issueNumber);
        details.put("id", String.valueOf(id));
        return details;
    }

    public boolean isSeries() {
        return this.isSeries;
    }
}
