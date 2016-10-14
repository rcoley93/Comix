package me.ryancoley.comix;

import com.orm.SugarRecord;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ryan on 8/7/2016.
 */
public class ComicBook extends SugarRecord implements Comparable<ComicBook> {
    private String issueTitle;
    private String writer;
    private String inker;
    private String colorist;
    private String letterer;
    private String editor;
    private String coverArtist;
    private String penciller;
    private String locationAcquired;
    private String comicLocation, issueNumber;
    private int coverMonth, aquiredMonth, aquiredYear, coverYear, grade, storageMethod, readUnread;
    private float coverPrice, pricePaid;
    private ComicBookSeries series;

    public ComicBook() {
    }

    public ComicBook(String iIssueNumber, ComicBookSeries cbs) {
        this.series = cbs;
        this.issueNumber = iIssueNumber;
    }

    //TODO change to MAP??
    public ComicBook(ComicBookSeries sSeries, String iIssueNumber, String sIssueTitle, int iCoverMonth,
                     int iCoverYear, float fltCoverPrice, int iGrade, int iStorageMethod, float fltPricePaid, String sWriter,
                     String sPenciller, String sInker, String sColorist, String sLetterer, String sEditor, String sCoverArtist,
                     int iReadUnread, int iAquiredYear, int iAquiredMonth, String sLocationAcquired, String sComicLocation) {
        this.series = sSeries;
        this.issueNumber = iIssueNumber;
        this.issueTitle = sIssueTitle;
        this.coverMonth = iCoverMonth;
        this.coverYear = iCoverYear;
        this.coverPrice = fltCoverPrice;
        this.grade = iGrade;
        this.storageMethod = iStorageMethod;
        this.pricePaid = fltPricePaid;
        this.writer = sWriter;
        this.penciller = sPenciller;
        this.inker = sInker;
        this.colorist = sColorist;
        this.letterer = sLetterer;
        this.editor = sEditor;
        this.coverArtist = sCoverArtist;
        this.readUnread = iReadUnread;
        this.aquiredYear = iAquiredYear;
        this.aquiredMonth = iAquiredMonth;
        this.locationAcquired = sLocationAcquired;
        this.comicLocation = sComicLocation;
    }

    //TODO Change to Map
    public ComicBook(String[] row) {
        String[] categories = {"Title", "Issue Number", "Issue Name", "Publisher", "Cover Date Month",
                "Cover Date Year", "Cover Price", "Condition", "Storage Method", "Storage Location", "Price Paid",
                "Writer(s)", "Penciller(s)", "Inker(s)", "Colorist(s)", "Letterer(s)", "Editor(s)",
                "Cover Artist(s)", "Read/Unread", "Date Acquired", "Location Acquired"};
        this.series = ComicBookSeries.findSeries(row[0], row[3]);
        if (this.series == null) this.series = new ComicBookSeries(row[0], row[3]);
        if (row[1].matches("^[\\w+[ ]*\\w+]+$")) this.issueNumber = row[1];
        else if (row[1].contains("#")) {
            if (row[1].contains(" "))
                this.issueNumber = row[1].substring(row[1].indexOf("#") + 1, row[1].indexOf(" "));
            else
                this.issueNumber = row[1].substring(row[1].indexOf("#") + 1);
        } else {
            this.issueNumber = "Other";
        }
        this.issueTitle = row[2];
        this.coverMonth = HelperFunctions.getMonthNumber(row[4].trim());
        this.coverYear = Integer.parseInt(row[5]);
        this.coverPrice = (row[6].contains("$")) ? Float.parseFloat(row[6].substring(row[6].indexOf("$") + 1)) : Float.parseFloat(row[6]);
        this.grade = HelperFunctions.getGrade(row[7]);
        this.storageMethod = HelperFunctions.getStorage(row[8]);
        this.comicLocation = row[9];
        this.pricePaid = (row[10].contains("$")) ? Float.parseFloat(row[10].substring(row[10].indexOf("$") + 1)) : Float.parseFloat(row[10]);
        this.writer = row[11];
        this.penciller = row[12];
        this.inker = row[13];
        this.colorist = row[14];
        this.letterer = row[15];
        this.editor = row[16];
        this.coverArtist = row[17];
        this.readUnread = HelperFunctions.getReadUnread(row[18]);
        Date d;
        DateFormat sdf = new SimpleDateFormat("MMMM dd yyyy");
        try {
            d = sdf.parse(row[19]);
            this.aquiredMonth = d.getMonth();
            this.aquiredYear = d.getYear();
            //TODO
        } catch (ParseException ex) {
            this.aquiredMonth = 14;
            this.aquiredYear = -1;
        }

        this.locationAcquired = row[20];
    }

    public String getSeriesName() {
        return this.series.getSeriesName();
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    //TODO remove
    public String[] getAllDetails() {
        return new String[]{
                this.series.getSeriesName(),            //0
                String.valueOf(this.issueNumber),                       //1
                this.issueTitle,                        //2
                this.series.getPublisherName(),         //3
                String.valueOf(this.coverMonth),    //4
                String.valueOf(this.coverYear),     //5
                String.valueOf(this.coverPrice),        //6
                String.valueOf(this.grade),         //7
                String.valueOf(this.storageMethod),     //8
                String.valueOf(this.pricePaid),         //9
                this.writer,            //10
                this.penciller,         //11
                this.inker,             //12
                this.colorist,          //13
                this.letterer,          //14
                this.editor,            //15
                this.coverArtist,       //16
                String.valueOf(this.readUnread),        //17
                String.valueOf(this.aquiredMonth),      //18
                String.valueOf(this.aquiredYear),       //19
                this.locationAcquired,  //20
                this.comicLocation,      //21
                this.series.getBarcode() //22
        };
    }

    public Map<String, String> getExportDetails() {
        Map<String, String> details = new HashMap<>();

        String[] categories = {"Title", "Issue Number", "Issue Name", "Publisher", "Cover Date Month",
                "Cover Date Year", "Cover Price", "Condition", "Storage Method", "Storage Location", "Price Paid",
                "Writer(s)", "Penciller(s)", "Inker(s)", "Colorist(s)", "Letterer(s)", "Editor(s)",
                "Cover Artist(s)", "Read/Unread", "Date Acquired", "Location Acquired"};

        details.put(categories[0], this.getSeriesName());
        details.put(categories[1], String.valueOf(this.issueNumber));
        details.put(categories[2], this.issueTitle);
        details.put(categories[3], this.series.getPublisherName());
        details.put(categories[4], HelperFunctions.getMonth(String.valueOf(this.coverMonth)));
        details.put(categories[5], String.valueOf(this.coverYear));
        details.put(categories[6], String.valueOf(this.coverPrice));
        details.put(categories[7], HelperFunctions.getGrade(this.grade));
        details.put(categories[8], HelperFunctions.getStorage(this.storageMethod));
        details.put(categories[9], this.comicLocation);
        details.put(categories[10], String.valueOf(this.pricePaid));
        details.put(categories[11], this.writer);
        details.put(categories[12], this.penciller);
        details.put(categories[13], this.inker);
        details.put(categories[14], this.colorist);
        details.put(categories[15], this.letterer);
        details.put(categories[16], this.editor);
        details.put(categories[17], this.coverArtist);
        details.put(categories[18], HelperFunctions.getReadUnread(this.readUnread));
        if (HelperFunctions.getMonth(String.valueOf(this.aquiredMonth)).equals("Unknown"))
            details.put(categories[19], HelperFunctions.getMonth(String.valueOf(this.aquiredMonth)));
        else
            details.put(categories[19], HelperFunctions.getMonth(String.valueOf(this.aquiredMonth)) + " " + this.aquiredYear);
        details.put(categories[20], this.locationAcquired);

        return details;

    }

    //TODO Change to MAP
    public String[] getComicListElement() {
        return new String[]{this.issueTitle, String.valueOf(this.issueNumber)};
    }

    public Map<String, String> getSearchListElement() {
        Map searchListElementDetails = new HashMap();
        searchListElementDetails.put("title", this.issueTitle);
        searchListElementDetails.put("publisher", this.series.getPublisherName());
        searchListElementDetails.put("series", this.series.getSeriesName());
        searchListElementDetails.put("issue_number", this.issueNumber);
        return searchListElementDetails;
    }

    @Override
    public int compareTo(ComicBook another) {
        try {
            int l = Integer.parseInt(this.getIssueNumber());
            int r = Integer.parseInt(another.getIssueNumber());
            return Integer.compare(l, r);
        } catch (NumberFormatException e) {
        }

        return this.getIssueNumber().compareTo(another.getIssueNumber());
    }
}
