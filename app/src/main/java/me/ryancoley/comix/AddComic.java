package me.ryancoley.comix;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.inmobi.ads.InMobiInterstitial;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddComic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    long lngID;
    boolean extraData = false;
    List<String> years;
    //all of the views on the page
    AutoCompleteTextView actvSeries, actvPublisher;
    EditText etIssueNumber, etIssueTitle, etPricePaid, etBarcode,
            etWriter, etPenciller, etInker, etColorist, etLetterer, etEditor,
            etCoverArtist, etLocationAcquired, etCoverPrice, etComicLocation;
    Spinner spnCoverYear, spnAquiredYear, spnCoverMonth, spnAquiredMonth,
            spnGrade, spnStorageMethod, spnReadUnread;
    TextView tvBarcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SETTING UP ACTIVITY
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SugarContext.init(AddComic.this);

        //SETTING UP LAYOUT
        actvSeries = (AutoCompleteTextView) findViewById(R.id.actvSeries);
        actvPublisher = (AutoCompleteTextView) findViewById(R.id.actvPublisher);

        etColorist = (EditText) findViewById(R.id.colorist);
        etComicLocation = (EditText) findViewById(R.id.comicLocation);
        etCoverArtist = (EditText) findViewById(R.id.coverArtist);
        etCoverPrice = (EditText) findViewById(R.id.coverPrice);
        //etBarcode = (EditText) findViewById(R.id.barcode);
        etPenciller = (EditText) findViewById(R.id.penciller);
        etPricePaid = (EditText) findViewById(R.id.pricePaid);
        etInker = (EditText) findViewById(R.id.inker);
        etIssueNumber = (EditText) findViewById(R.id.issueNumber);
        etIssueTitle = (EditText) findViewById(R.id.issueTitle);
        etLetterer = (EditText) findViewById(R.id.letterer);
        etLocationAcquired = (EditText) findViewById(R.id.locationAcquired);
        etEditor = (EditText) findViewById(R.id.editor);
        etWriter = (EditText) findViewById(R.id.writer);

        spnGrade = (Spinner) findViewById(R.id.condition);
        spnStorageMethod = (Spinner) findViewById(R.id.storageMethod);
        spnReadUnread = (Spinner) findViewById(R.id.readUnread);
        spnAquiredYear = (Spinner) findViewById(R.id.spnAquiredYear);
        spnCoverYear = (Spinner) findViewById(R.id.spnCoverYear);
        spnAquiredMonth = (Spinner) findViewById(R.id.spnAquiredMonth);
        spnCoverMonth = (Spinner) findViewById(R.id.spnCoverMonth);

        tvBarcode = (TextView) findViewById(R.id.tvBarcode);

        tvBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });

        //get all the years up to current

        int iCurrentMonth = Calendar.getInstance().get(Calendar.MONTH);

        spnAquiredMonth.setSelection(iCurrentMonth);


        years = HelperFunctions.getYearList();

        //get all publishers
        final List<String> publishers = new ArrayList<>();
        List<ComicBookPublisher> listPublishers = ComicBookPublisher.listAll(ComicBookPublisher.class);
        for (ComicBookPublisher pub : listPublishers) publishers.add(pub.getName());

        //set the lists to the actv
        final ArrayAdapter aPublishers = new ArrayAdapter(this, android.R.layout.simple_list_item_1, publishers);
        actvPublisher.setAdapter(aPublishers);

        //get all series
        List<String> series = new ArrayList<>();
        List<ComicBookSeries> listSeries = ComicBookPublisher.listAll(ComicBookSeries.class);
        for (ComicBookSeries pub : listSeries) series.add(pub.getSeriesName());

        //set the lists to the actv
        ArrayAdapter aSeries = new ArrayAdapter(this, android.R.layout.simple_list_item_1, series);
        actvSeries.setAdapter(aSeries);
        actvSeries.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<ComicBookSeries> cbs = ComicBookSeries.getPublishers(actvSeries.getText().toString());
                if (cbs.size() != 0) {
                    publishers.clear();
                    for (ComicBookSeries p : cbs) publishers.add(p.getPublisherName());
                    aPublishers.notifyDataSetChanged();
                }
            }
        });


        //set the years to the spinners
        ArrayAdapter<String> aYear = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        aYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCoverYear.setAdapter(aYear);
        spnAquiredYear.setAdapter(aYear);

        //GETTING INTENT DATA
        Intent intentData = getIntent();
        extraData = intentData.getBooleanExtra("extra_data", false);
        if (extraData) {
            lngID = intentData.getLongExtra("id", -1);
            if (lngID != -1) {
                ComicBook c = ComicBook.find(ComicBook.class, "id=?", new String[]{String.valueOf(lngID)}).get(0);
                String[] details = c.getAllDetails();
                actvSeries.setText(details[0]);
                etIssueNumber.setText(details[1]);
                etIssueTitle.setText(details[2]);
                actvPublisher.setText(details[3]);
                spnCoverMonth.setSelection(HelperFunctions.getMonthNumber(details[4]));
                spnCoverYear.setSelection(HelperFunctions.getYearNumber(details[5]));
                etCoverPrice.setText(details[6]);
                spnGrade.setSelection(Integer.parseInt(details[7]));
                spnStorageMethod.setSelection(Integer.parseInt(details[8]));
                etPricePaid.setText(details[9]);
                etWriter.setText(details[10]);
                etPenciller.setText(details[11]);
                etInker.setText(details[12]);
                etColorist.setText(details[13]);
                etLetterer.setText(details[14]);
                etEditor.setText(details[15]);
                etCoverArtist.setText(details[16]);
                spnReadUnread.setSelection(Integer.parseInt(details[17]));
                spnAquiredMonth.setSelection(HelperFunctions.getMonthNumber(details[18]));
                spnAquiredYear.setSelection(HelperFunctions.getYearNumber(details[19]));
                etLocationAcquired.setText(details[20]);
                etComicLocation.setText(details[21]);
                tvBarcode.setText(details[22]);
            } else if (!intentData.getBooleanExtra("barcode", false)) {
                ComicBookSeries s = ComicBookSeries.findSeries(intentData.getStringExtra("series"), null);
                actvSeries.setText(s.getSeriesName());
                actvPublisher.setText(s.getPublisherName());
                actvPublisher.setEnabled(false);
                actvSeries.setEnabled(false);
                tvBarcode.setText(s.getBarcode());
            } else startScan();
        }
    }


    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(AddComic.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        tvBarcode.setText(barcode.displayValue);
                        getSeriesAndPublisherInfo(barcode.displayValue);
                    }
                })

                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_comic, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        HelperFunctions.activityMenu(this, AddComic.this, id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goHome(View V) {
        Intent i = new Intent(AddComic.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_comic:
                finish();
                return true;
            case R.id.save_comic:
                addComic();
                return true;
            case R.id.barcode_comic:
                startScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getSeriesAndPublisherInfo(String barcode) {
        ComicBookSeries series;
        if (barcode != "") {
            series = ComicBookSeries.findSeries(barcode);
            if (series == null) return;
            actvSeries.setText(series.getSeriesName());
            actvPublisher.setText(series.getPublisherName());
            tvBarcode.setText(series.getBarcode());
            actvSeries.setEnabled(false);
            actvPublisher.setEnabled(false);
            tvBarcode.setEnabled(false);
        }
    }

    public void addComic() {
        //first check to see if series,publisher and issue number are filled in
        if (actvSeries.getText().toString().equals("")) {
            //TODO Change to top down notification
            Toast.makeText(this, "Make sure that the series is filled in!", Toast.LENGTH_LONG).show();
            return;
        } else if (etIssueNumber.getText().toString().equals("")) {
            //TODO Change to top down notification
            Toast.makeText(this, "Make sure that the issue number is filled in!", Toast.LENGTH_LONG).show();
            return;
        } else if (actvPublisher.getText().toString().equals("")) {
            //TODO Change to top down notification
            Toast.makeText(this, "Make sure that the publisher is filled in!", Toast.LENGTH_LONG).show();
            return;
        } else if (!etIssueNumber.getText().toString().matches("^[\\w+[ ]*\\w+]+$")) {
            //TODO Change to top down notification
            Toast.makeText(this, "Make sure that the issue number uses only alphanumeric characters!", Toast.LENGTH_LONG).show();
            return;
        }

        //need to get series and publisher
        ComicBookSeries series = ComicBookSeries.findSeries(actvSeries.getText().toString(), actvPublisher.getText().toString()); //= new ComicBookSeries(actvSeries.getText().toString(),tvBarcode.getText().toString(),actvPublisher.getText().toString());//cbs.findSeries(actvSeries.getText().toString(),actvPublisher.getText().toString());
        if (series == null && tvBarcode.getText().toString().equals("Click to add Barcode")) {
            series = new ComicBookSeries(actvSeries.getText().toString(), actvPublisher.getText().toString());
        } else if (series == null && !tvBarcode.getText().toString().equals("Click to add Barcode")) {
            series = new ComicBookSeries(actvSeries.getText().toString(), tvBarcode.getText().toString(), actvPublisher.getText().toString());
        }
        if (series.getBarcode().equals("") && !tvBarcode.getText().toString().equals("Click to add Barcode")) {
            series.setBarcode(tvBarcode.getText().toString());
        }

        Float fltCoverPrice = (etCoverPrice.getText().toString().equals("")) ? 0.0f : Float.parseFloat(etCoverPrice.getText().toString());
        Float fltPricePaid = (etPricePaid.getText().toString().equals("")) ? 0.0f : Float.parseFloat(etPricePaid.getText().toString());

        ComicBook newComic = new ComicBook(series, etIssueNumber.getText().toString(),
                etIssueTitle.getText().toString(), spnCoverMonth.getSelectedItemPosition(),
                Integer.parseInt(spnCoverYear.getSelectedItem().toString()), fltCoverPrice, spnGrade.getSelectedItemPosition(),
                spnStorageMethod.getSelectedItemPosition(), fltPricePaid, etWriter.getText().toString(),
                etPenciller.getText().toString(), etInker.getText().toString(), etColorist.getText().toString(), etLetterer.getText().toString(),
                etEditor.getText().toString(), etCoverArtist.getText().toString(), spnReadUnread.getSelectedItemPosition(),
                Integer.parseInt(spnAquiredYear.getSelectedItem().toString()), spnAquiredMonth.getSelectedItemPosition(),
                etLocationAcquired.getText().toString(), etComicLocation.getText().toString());

        if (extraData && lngID != -1) {
            ComicBook.deleteAll(ComicBook.class, "id=?", String.valueOf(lngID));
            newComic.setId(lngID);
        }

        newComic.save();

        AdGenerator ag = new AdGenerator(this);
        ag.createInterstitialAd();

        finish();
    }
}
