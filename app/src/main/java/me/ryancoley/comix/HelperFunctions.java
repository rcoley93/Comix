package me.ryancoley.comix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.suredigit.inappfeedback.FeedbackDialog;
import com.suredigit.inappfeedback.FeedbackSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ryan on 7/18/2015.
 */
public class HelperFunctions {

    public static boolean isOnline(Context c) {
        ConnectivityManager connMgr = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());


    }


    public static List<ComicBook> sortComics(List<ComicBook> unsortedList) {
        List<ComicBook> sortedList = new LinkedList<>();
        List<ComicBook> comicIssueNumbers = new LinkedList<>();
        List<ComicBook> comicIssueOther = new LinkedList<>();
        //separate the lists
        for (ComicBook c : unsortedList) {
            try {
                Integer.parseInt(c.getIssueNumber());
                comicIssueNumbers.add(c);
            } catch (NumberFormatException ex) {
                comicIssueOther.add(c);
            }
        }
        //sort the lists
        Collections.sort(comicIssueNumbers);
        Collections.sort(comicIssueOther);

        sortedList.addAll(comicIssueNumbers);
        sortedList.addAll(comicIssueOther);

        return sortedList;
    }

    public static List<ComicBookSeries> sortSeries(List<ComicBookSeries> cbs) {
        List<ComicBookSeries> sortedList = new LinkedList<>();
        sortedList.addAll(cbs);
        Collections.sort(sortedList);
        return sortedList;
    }


    public static String getMonth(String s) {
        switch (s) {
            case "1":
                return "January";
            case "2":
                return "February";
            case "3":
                return "March";
            case "4":
                return "April";
            case "5":
                return "May";
            case "6":
                return "June";
            case "7":
                return "July";
            case "8":
                return "August";
            case "9":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            case "13":
                return "Annual";
        }
        return "Unknown";
    }

    public static ArrayList<String> getYearList() {
        int iCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        ArrayList<String> years = new ArrayList<>();
        for (int i = iCurrentYear; i >= 1922; i--) {
            years.add(Integer.toString(i));
        }
        return years;
    }

    public static int getMonthNumber(String s) {
        switch (s) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
            case "Annual":
                return 13;

        }
        return 0;
    }

    public static int getGrade(String str) {
        switch (str) {
            case "Near Mint":
            case "Near Mint/Mint":
            case "Mint":
            case "NM Near Mint":
                return 0;
            case "Very Fine/Near Mint":
            case "Very Fine":
            case "VF Very Fine":
                return 1;
            case "Fine/Very Fine":
            case "Fine":
            case "FN Fine":
                return 2;
            case "Very Good/Fine":
            case "Very Good":
            case "VG Very Good":
                return 3;
            case "Good/Very Good":
            case "Good":
            case "GD Good":
                return 4;
            case "Fair/Good":
            case "Fair":
            case "FR Fair":
                return 5;
            case "Poor":
            case "PR Poor":
                return 6;
        }
        return 7;
    }

    public static String getGrade(int i) {
        switch (i) {
            case 1:
                return "NM Near Mint";
            case 2:
                return "VF Very Fine";
            case 3:
                return "FN Fine";
            case 4:
                return "VG Very Good";
            case 5:
                return "GD Good";
            case 6:
                return "FR Fair";
            case 7:
                return "PR Poor";
        }
        return "Unknown";
    }

    public static String getStorage(int i) {
        switch (i) {
            case 1:
                return "Bagged";
            case 2:
                return "Bagged/Boarded";
            default:
                return "None";
        }
    }

    public static int getStorage(String i) {
        if (i.toLowerCase().equals("bagged")) return 1;
        else if (i.toLowerCase().equals("bagged/boarded")) return 2;
        return 0;
    }

    public static String getReadUnread(int i) {
        if (i == 1) return "Read";
        return "Unread";
    }

    public static int getReadUnread(String i) {
        if (i.toLowerCase().equals("read")) return 1;
        return 0;
    }

    public static int getYearNumber(String year) {
        ArrayList<String> years = getYearList();
        for (int i = 0; i < years.size(); i++) {
            if (year.equals(years.get(i))) return i;
        }
        return 0;
    }

    public static void activityMenu(Activity a, Context c, int id) {
        DialogCreator dt = new DialogCreator(c);
        Intent i;
        switch (id) {
            case R.id.nav_about_app:
                //dt.createAboutDialog();
                new LibsBuilder()
                        //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                        //.withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        //start the activity
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        //.withAboutDescription("This")
                        .withAboutDescription("Add and Barcode Icon made by<br />Madebyoliver from <a href=\"http://flaticon.com\">www.flaticon.com</a>.")
                        .start(c);
                break;
            case R.id.nav_add_comic:
                i = new Intent(c, AddComic.class);
                i.putExtra("extra_data", false);
                c.startActivity(i);
                break;
            case R.id.nav_delete_all_comics:
                dt.createDeleteAllComicsDialog();
                break;
            case R.id.nav_export_comics:
                ExportComics ec = new ExportComics(c);
                ec.createDialog();
                break;
            case R.id.nav_import_comics:
                new MaterialFilePicker()
                        .withActivity(a)
                        .withRequestCode(1)
                        .withFilterDirectories(true) // Set directories filterable (false by default)
                        .withHiddenFiles(false) // Show hidden files and folders
                        .start();
                break;
            case R.id.nav_search_comics:
                i = new Intent(c, SearchComics.class);
                c.startActivity(i);
                break;
            case R.id.nav_view_series:
                i = new Intent(c, ViewSeries.class);
                c.startActivity(i);
                break;
            case R.id.nav_feedback:
                FeedbackDialog feedBack;

                FeedbackSettings feedbackSettings = new FeedbackSettings();

//SUBMIT-CANCEL BUTTONS
                feedbackSettings.setCancelButtonText("Cancel");
                feedbackSettings.setSendButtonText("Send");

//DIALOG TEXT
                feedbackSettings.setText("Hey, would you like to provide some feedback? That way we can improve the app!");
                feedbackSettings.setYourComments("Question/Comment");
                feedbackSettings.setTitle("Feedback");

//TOAST MESSAGE
                //TODO Change to top down notification MAYBE?
                feedbackSettings.setToast("Thank you for the Feedback!");
                feedbackSettings.setToastDuration(Toast.LENGTH_SHORT);  // Default

//RADIO BUTTONS
                feedbackSettings.setRadioButtons(false); // Disables radio buttons

//SET DIALOG MODAL
                feedbackSettings.setModal(true); //Default is false

//DEVELOPER REPLIES
                feedbackSettings.setReplyTitle("Message from the Developer!");
                feedbackSettings.setReplyCloseButtonText("Close");
                feedbackSettings.setReplyRateButtonText("RATE!");

//DEVELOPER CUSTOM MESSAGE (NOT SEEN BY THE END USER)
                feedbackSettings.setDeveloperMessage("comix-feedback");

                feedBack = new FeedbackDialog(a, "AF-262066FE03D4-BE", feedbackSettings);
                feedBack.show();


        }
    }


}
