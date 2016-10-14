package me.ryancoley.comix;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Font;

/**
 * Created by Ryan on 9/18/2016.
 */
public class ExportComics {

    private static final String fileName = "collection";
    private static final String csvFileName = fileName + ".csv";
    private static final String excelFileName = fileName + ".xls";
    private static final String htmlFileName = fileName + ".html";
    private static final String pdfFileName = fileName + ".pdf";
    private static final String exportFolder = "Exports";
    private static final String SDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + exportFolder;

    private final String[] categories = {"Title", "Issue Number", "Issue Name", "Publisher", "Cover Date Month",
            "Cover Date Year", "Cover Price", "Condition", "Storage Method", "Storage Location", "Price Paid",
            "Writer(s)", "Penciller(s)", "Inker(s)", "Colorist(s)", "Letterer(s)", "Editor(s)",
            "Cover Artist(s)", "Read/Unread", "Date Acquired", "Location Acquired"};


    Context cApp;
    AlertDialog dialog;

    ExportComics(Context c) {
        this.cApp = c;
    }

    public void createDialog() {
        LayoutInflater li = LayoutInflater.from(cApp);
        final View v = li.inflate(R.layout.dialog_export_comics, null);

        AlertDialog.Builder prompt = new AlertDialog.Builder(cApp);

        prompt.setView(v);

        /*final EditText range = (EditText) v.findViewById(R.id.range);
        range.setHint("ex. 1,2,4-6,8,10");*/

        prompt.setCancelable(false);

        prompt.setPositiveButton("Export",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Spinner spnExport = (Spinner) v.findViewById(R.id.export_type);
                        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                            if (ComicBook.count(ComicBook.class) > 0) {
                                Exporter ec = new Exporter(spnExport.getSelectedItem().toString());
                                ec.execute();
                            } else {
                                //TODO Change to top down notification
                                Toast.makeText(cApp, "Error! No Comics to Export!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //TODO Change to top down notification
                            Toast.makeText(cApp, "Error! Unable to write to external storage!", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
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

    private class Exporter extends AsyncTask<String, Integer, String> {
        //TODO sort comics within each individual series
        private List<ComicBook> Comics;

        private String exportType;
        private Storage exportFile;

        Exporter(String strExportType) {
            exportType = strExportType;
        }

        @Override
        protected String doInBackground(String... strings) {
            Comics = ComicBook.listAll(ComicBook.class);

            if (Comics.size() == 0) {
               /* runOnUiThread(new Runnable() {
                    public void run() {
                    //TODO Change to top down notification
                        Toast.makeText(getApplicationContext(), "Error! There are no Comics to export!", Toast.LENGTH_SHORT).show();
                    }
                });*/
                return null;
            }

            DialogCreator.setProgressBarMax(dialog, Comics.size());

            switch (exportType) {
                case "CSV":
                    csvExport();
                    break;
                case "HTML":
                    htmlExport();
                    break;
                case "PDF":
                    pdfExport();
                    break;
                case "Excel":
                    excelExport();
                    break;
            }
            return null;
        }

        private void excelExport() {
            /*//set up the workbook
            Workbook wb = new HSSFWorkbook();

            //set up the sheet
            final Sheet sheet = wb.createSheet("Comic Book Collection");
            PrintSetup printSetup = sheet.getPrintSetup();
            printSetup.setLandscape(true);
            //the following three statements are required only for HSSF
            sheet.setAutobreaks(true);
            printSetup.setFitHeight((short) 1);
            printSetup.setFitWidth((short) 1);

            //set up the styles
            //title
            CellStyle title = wb.createCellStyle();
            Font titleFont = wb.createFont();
            titleFont.setFontHeightInPoints((short) 36);
            titleFont.setBold(true);
            title.setFont(titleFont);
            title.setAlignment(CellStyle.ALIGN_CENTER);
            //header
            CellStyle header = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setBold(true);
            header.setFont(headerFont);
            header.setAlignment(CellStyle.ALIGN_CENTER);
            //values
            CellStyle values = wb.createCellStyle();
            Font valuesFont = wb.createFont();
            valuesFont.setFontHeightInPoints((short) 12);
            values.setFont(valuesFont);

            //set up the title
            Keyboard.Row row = sheet.createRow(0);
            row.setHeightInPoints((float) 46.50);
            Cell cell = row.createCell(0);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
            cell.setCellValue("Comic Book Collection");
            cell.setCellStyle(title);


            //create the header
            row = sheet.createRow(1);

            for (int i = 0; i < categories.length; ++i) {
                cell = row.createCell(i);
                cell.setCellValue(categories[i]);
                cell.setCellStyle(header);
            }

            //add the rest of the values
            int rowCount = 2;
            int currentComic = 0;

            for (ComicBook c : Comics) {
                row = sheet.createRow(rowCount++);
                Map<String, String> details = c.getExportDetails();


                for (int i = 0; i < 21; i++) {
                    cell = row.createCell(i);
                    cell.setCellStyle(values);
                    cell.setCellValue(details.get(categories[i]));
                }

                publishProgress(++currentComic);
            }


                    /*for(int i = 0;i<categories.length;++i){
                        sheet.autoSizeColumn(i);
                    }*/

                    /*runOnUiThread(new Runnable() {
                        public void run() {
                        //TODO add tio down notification
                            setExportDialogMessage("Saving Excel file!");
                        }
                    });

            //save file
            File xls = new File(SDCardPath, excelFileName);
            try {
                FileOutputStream out = new FileOutputStream(xls);
                wb.write(out);
                out.close();
            } catch (FileNotFoundException ex) {
                //TODO add error top down notification
                Log.wtf("EXCEL", "FILE NOT FOUND! " + ex.toString());
            } catch (IOException ex) {
                Log.wtf("EXCEL", "IO ERROR! " + ex.toString());
            }*/
        }

        private void pdfExport() {
            htmlExport();
            final File pdf = new File(SDCardPath, pdfFileName);
            ((ProgressBar) dialog.findViewById(R.id.pbComics)).setIndeterminate(true);

            String SERVER = "http://coley.sytes.net/comix/api/v1.0/pdf";

            try {
                Map<String, String> data = new HashMap<>();
                //data.put("user", "A User");
                data.put("html_string", exportFile.readTextFile(exportFolder, htmlFileName));
                HttpRequest request = HttpRequest.post(SERVER).form(data);
                if (request.ok()) {
                    request.receive(pdf);
                }
            } catch (HttpRequest.HttpRequestException exception) {
            }

            exportFile.deleteFile(exportFolder, htmlFileName);
        }

        private void htmlExport() {
            final String htmlHeader = "<!DOCTYPE HTML>\n" +
                    "<html>\n" +
                    "\t<head>\n" +
                    "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf=8\" />\n" +
                    "\t\t<title>Comic Book Collection</title>\n" +
                    "\t</head>\n" +
                    "\t<body>\n" +
                    "\t\t<center><h1>Comic Book Collection</h1></center><br />\n";// +
            //"\t\t<table border=\"1\">\n" +
            //    "\t\t\t<tr>";
            final String htmlFooter = "\t</body>\n</html>";

            exportFile.createFile(exportFolder, htmlFileName, htmlHeader);

            int currentComic = 0;
            String currentTitle = "";
            for (ComicBook c : Comics) {
                Map<String, String> details = c.getExportDetails();
                String comicSeriesHeader = "";
                boolean printSeriesHeader = false;
                if (!currentTitle.equals(details.get("Title"))) {
                    currentTitle = details.get("Title");
                    comicSeriesHeader = "\t\t<center><h1>" + currentTitle + "</h1></center>\n";
                    comicSeriesHeader += "\t\t<center><h2>" + details.get("Publisher") + "</h2></center>\n";
                    printSeriesHeader = true;
                }

                if (printSeriesHeader)
                    exportFile.appendFile(exportFolder, htmlFileName, comicSeriesHeader);

                String html = "\t\t<table border=\"1\">\n";
                for (int i = 0; i < 21; i++) {
                    if (i == 0 || i == 3) continue;
                    html += "\t\t\t<tr>\n";
                    html += "\t\t\t\t<th>" + categories[i] + "</th>\n";
                    html += "\t\t\t\t<td>" + details.get(categories[i]) + "</td>\n";
                }
                html += "\t\t</table><br />\n";

                exportFile.appendFile(exportFolder, htmlFileName, html);

                publishProgress(++currentComic);
            }

            exportFile.appendFile(exportFolder, htmlFileName, htmlFooter);
        }

        private void csvExport() {
            exportFile.createFile(exportFolder, csvFileName, "");

            String csv = "";
            //setup header
            for (int i = 0; i < 21; i++) {
                if (i != 20) {
                    csv += categories[i] + ",";
                } else {
                    csv += categories[i] + "\n";
                }
            }

            exportFile.appendFile(exportFolder, csvFileName, csv);

            //print out the values
            int currentComic = 0;
            for (ComicBook c : Comics) {
                Map<String, String> details = c.getExportDetails();
                csv = "";
                for (int i = 0; i < details.size(); i++) {
                    csv += "\"";
                    if (i != details.size() - 1) csv += details.get(categories[i]) + "\",";
                    else csv += details.get(categories[i]) + "\"\n";
                }
                exportFile.appendFile(exportFolder, csvFileName, csv);
                publishProgress(++currentComic);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //TODO Change to top down notification
            // Toast.makeText(getApplicationContext(), "Completed Export of Comics!", Toast.LENGTH_LONG).show();
            DialogCreator.dismisDialog(dialog);
        }

        @Override
        protected void onPreExecute() {
            // createExportDialog();
            dialog = (new DialogCreator(cApp)).createProgressDialog("Exporting Comics");
            exportFile = SimpleStorage.getExternalStorage();
            exportFile.createDirectory(exportFolder);
            //Comics = ComicBook.listAll(ComicBook.class);
            if(this.exportType.equals("PDF")) Toast.makeText(cApp,"This feature is available in later release!",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Integer... text) {
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
            //setExportDialogMessage("Exported Comic " + text[0] + " of " + text[1] + "!");
            DialogCreator.updateProgressDialog(dialog, text[0]);
        }
    }
}
