package me.ryancoley.comix;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.ads.InMobiInterstitial;

import java.util.ArrayList;

/**
 * Created by Ryan on 5/24/2016.
 */
public class DialogCreator {
    private Context cApp;

    public DialogCreator(Context c) {
        this.cApp = c;
    }

    public static void updateProgressDialog(AlertDialog d, int val) {
        ((ProgressBar) d.findViewById(R.id.pbComics)).setProgress(val);
    }

    public static void setProgressBarMax(AlertDialog d, int val) {
        ((ProgressBar) d.findViewById(R.id.pbComics)).setMax(val);
    }

    public static void dismisDialog(AlertDialog d) {
        d.dismiss();
    }

    public void createDeleteAllComicsDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(cApp).create();
        alertDialog.setTitle("Delete All Comics");
        alertDialog.setMessage("Are you sure you want to delete all comics?");
        alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ComicBook.deleteAll(ComicBook.class);
                ComicBookSeries.deleteAll(ComicBookSeries.class);
                ComicBookPublisher.deleteAll(ComicBookPublisher.class);
                Toast.makeText(cApp, "All Comics Deleted!", Toast.LENGTH_SHORT).show();
                AdGenerator ag = new AdGenerator(cApp);
                ag.createInterstitialAd();
                MainActivity.loadStats();
            }
        });
        alertDialog.show();
    }

    //TODO add Top dow notification on completion
    public void createDeleteComicDialog(final long id, final ViewComic c) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(cApp);
        alertDialog.setTitle("Delete Comic");
        alertDialog.setMessage("Are you sure you want to this comic? THIS CANNOT BE UNDONE!");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ComicBook.delete(ComicBook.find(ComicBook.class, "id=?", new String[]{String.valueOf(id)}).get(0));
                Toast.makeText(cApp, "Comic Deleted!", Toast.LENGTH_SHORT).show();
                AdGenerator ag = new AdGenerator(cApp);
                ag.createInterstitialAd();
                c.finish();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.create().show();
    }

    //TODO add Top Down notification on completion
    public void createDeleteSeriesDialog(final String id, final ViewIssues c) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(cApp);
        alertDialog.setTitle("Delete Comic");
        alertDialog.setMessage("Are you sure you want to delete all the comics in this series? THIS CANNOT BE UNDONE!");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ComicBook.deleteAll(ComicBook.class, "series=?", new String[]{id});
                ComicBookSeries.deleteAll(ComicBookSeries.class, "id=?", new String[]{id});
                Toast.makeText(cApp, "Series Deleted!", Toast.LENGTH_SHORT).show();
                AdGenerator ag = new AdGenerator(cApp);
                ag.createInterstitialAd();
                c.finish();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.create().show();
    }

    public void createRangeDialog(final String strSeries, final String strPublisher) {
        LayoutInflater li = LayoutInflater.from(cApp);
        View v = li.inflate(R.layout.range_prompt, null);

        AlertDialog.Builder prompt = new AlertDialog.Builder(cApp);

        prompt.setView(v);

        final EditText range = (EditText) v.findViewById(R.id.range);
        range.setHint("ex. 1,2,4-6,8,10");

        prompt.setCancelable(false);

        prompt.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<Integer> aRange = parseRange(range.getText().toString());
                        if (aRange.size() == 0) {
                            Toast.makeText(cApp, "Error! Unable to parse the range! Please try again!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        addComics(aRange, strSeries, strPublisher);
                        AdGenerator ag = new AdGenerator(cApp);
                        ag.createInterstitialAd();
                        dialog.cancel();
                    }
                });

        prompt.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        prompt.create().show();
    }

    private ArrayList<Integer> parseRange(String strRange) {
        ArrayList<Integer> result = new ArrayList<>();

        String[] strRangeSplit = strRange.split(",");

        for (int i = 0; i < strRangeSplit.length; ++i) {
            if (strRangeSplit[i].contains("-")) {
                String[] strRangeSplitSplit = strRangeSplit[i].split("-");
                if (strRangeSplitSplit.length != 2) {
                    result.clear();
                    return result;
                }
                int start, end;
                try {
                    start = Integer.parseInt(strRangeSplitSplit[0]);
                    end = Integer.parseInt(strRangeSplitSplit[1]);
                } catch (NumberFormatException ex) {
                    result.clear();
                    return result;
                }
                for (int j = start; j <= end; ++j) {
                    result.add(j);
                }
            } else {
                int intAdd;
                try {
                    intAdd = Integer.parseInt(strRangeSplit[i]);
                } catch (NumberFormatException ex) {
                    result.clear();
                    return result;
                }
                if (intAdd < 0) {
                    result.clear();
                    return result;
                } else {
                    result.add(intAdd);
                }
            }
        }

        return result;
    }

    private void addComics(ArrayList<Integer> aRange, String series, String publisher) {
        ComicBookSeries cbs = ComicBookSeries.findSeries(series, publisher);
        for (int i : aRange) {
            if(!comicAlreadyExists(String.valueOf(i),cbs)){
                ComicBook newComic = new ComicBook(String.valueOf(i), cbs);
                newComic.save();
            }else{
                //TODO Change to top down notification
                Toast.makeText(cApp,"Comic Already Exists. The Ability to add more will be available in the future.",Toast.LENGTH_LONG).show();
            }

            //Toast.makeText(cApp, series + " issue number " + aRange.get(i) + " was added to the collection!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean comicAlreadyExists(String strIssueNumber, ComicBookSeries cbs) {
        String where = "issue_number=?, series=?";
        String[] whereArgs = {strIssueNumber,String.valueOf(cbs.getId())};
        return ComicBook.find(ComicBook.class,where,whereArgs).size() == 0;
    }

    public AlertDialog createProgressDialog(String caption) {
        LayoutInflater li = LayoutInflater.from(cApp);
        View v = li.inflate(R.layout.dialog_progress_bar, null);

        AlertDialog.Builder prompt = new AlertDialog.Builder(cApp);

        prompt.setView(v);

        prompt.setCancelable(false);

        ((TextView) v.findViewById(R.id.tvCaption)).setText(caption);


        AlertDialog dialog = prompt.create();
        dialog.show();
        return dialog;
    }
}
