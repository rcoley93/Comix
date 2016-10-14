package me.ryancoley.comix;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.inmobi.ads.InMobiInterstitial;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by Ryan on 8/8/2016.
 */
public class ImportComics extends AsyncTask<String, Integer, String> {
    List<String[]> list = new ArrayList<>();
    Context c;
    //alert dialog
    //AlertDialog.Builder dialogBuild;
    AlertDialog dialog;
    private String resp, fileName;
    private InMobiInterstitial ad;

    ImportComics(String filename, Context context) {
        fileName = filename;
        c = context;
    }

    private void processFile(String file) {
        CSVReader csv;
        try {
            csv = new CSVReader(new InputStreamReader(new FileInputStream(file)));
            while (true) {
                String[] next = csv.readNext();
                if (next != null) list.add(next);
                else break;
            }
        } catch (FileNotFoundException ex) {
           /* runOnUiThread(new Runnable() {
                public void run() {
                //TODO Change to top down notification
                    Toast.makeText(getApplicationContext(), "Error: File not found!", Toast.LENGTH_LONG).show();
                }
            });*/
        } catch (final IOException ex) {
            /*runOnUiThread(new Runnable() {
                public void run() {
                //TODO Change to top down notification
                    Toast.makeText(getApplicationContext(), "Error: I/O exception " + ex.toString() + "!", Toast.LENGTH_LONG).show();
                }
            });*/
        }
        DialogCreator.setProgressBarMax(dialog, list.size() - 1);
    }

    @Override
    protected String doInBackground(String... params) {

        String[] row = list.get(0);

        if (row.length != 21) {
            /*runOnUiThread(new Runnable() {
                public void run() {
                //TODO Change to top down notification
                    Toast.makeText(getApplicationContext(), "Error: Make sure that file is formatted correctly! Categories!", Toast.LENGTH_SHORT).show();
                }
            });*/
            return resp;
        } else {
            String[] categories = {"Title", "Issue Number", "Issue Name", "Publisher", "Cover Date Month",
                    "Cover Date Year", "Cover Price", "Condition", "Storage Method", "Storage Location", "Price Paid",
                    "Writer(s)", "Penciller(s)", "Inker(s)", "Colorist(s)", "Letterer(s)", "Editor(s)",
                    "Cover Artist(s)", "Read/Unread", "Date Acquired", "Location Acquired"};
            for (int i = 0; i < 21; ++i) {
                if (!categories[i].equals(row[i])) {
                   /* runOnUiThread(new Runnable() {
                        public void run() {
                        //TODO Change to top down notification
                            Toast.makeText(getApplicationContext(), "Error! Import File not formatted correctly!", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    return resp;
                }
            }
            for (int i = 1; i < list.size(); ++i) {
                row = list.get(i);
                for (int j = 0; j < row.length; ++j) {
                    row[j] = row[j].replace("\"", "");
                    row[j].trim();
                }

                ComicBook newComic = new ComicBook(row);
                newComic.save();

                publishProgress(i);//, String.valueOf(list.size() - 1));
            }
        }
        resp = "Added Comics to database!";
        return resp;
    }

    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        DialogCreator.dismisDialog(dialog);
        //TODO Change to top down notification
        Toast.makeText(c, "Completed Import of Comics!", Toast.LENGTH_SHORT).show();
        AdGenerator ag = new AdGenerator(c);
        ag.createInterstitialAd();
        //dismissDialog();
        MainActivity.loadStats();
        /*finish();*/
    }

    @Override
    protected void onPreExecute() {
        // Things to be done before execution of long running operation. For
        // example showing ProgessDialog
        dialog = (new DialogCreator(c)).createProgressDialog("Importing Comics");
        processFile(fileName);
    }

    @Override
    protected void onProgressUpdate(Integer... text) {
        // Things to be done while execution of long running operation is in
        // progress. For example updating ProgessDialog
        /*int totalComics = Integer.parseInt(text[1]);
        int currentComic = Integer.parseInt(text[0]);
        int percentImported = currentComic/totalComics;
        if(percentImported%2 == 0) MainActivity.loadStats();*/
        //setImportDialogMessage("Added Comic " + text[0] + " of " + text[1] + "!");
        DialogCreator.updateProgressDialog(dialog, text[0]);
    }
}
