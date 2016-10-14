package me.ryancoley.comix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

public class ViewSeries extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MyCustomListAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_series);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SugarContext.init(ViewSeries.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setup the list view
        //aList = new ArrayList<>();
        aa = new MyCustomListAdapter(this);
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();

        //get the list of comics
        getListOfSeries();

       // AdGenerator ag = new AdGenerator(ViewSeries.this);
        //ag.createNativeAd(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListOfSeries();
    }

    private void getListOfSeries() {
        aa.reset();

        List<ComicBookSeries> series = HelperFunctions.sortSeries(ComicBookSeries.listAll(ComicBookSeries.class));

        int i=0;
        for (ComicBookSeries cbs : series) {
            String[] search = {String.valueOf(cbs.getId())};
            Long count = ComicBookSeries.count(ComicBook.class, "series = ?", search);
            if (count != 0) {
                aa.addSeriesItem(new SeriesListElement(count, cbs.getSeriesListElement()));
                if((++i%7) == 0) aa.addAdItem();
            }
        }

        aa.notifyDataSetChanged();

        //aList = mergeSortAlpha(aList);

        //update the list view
       //aa.notifyDataSetChanged();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        HelperFunctions.activityMenu(this, ViewSeries.this, id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goHome(View V) {
        Intent i = new Intent(ViewSeries.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_series, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_barcode_comic:
                startScan();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ViewSeries.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        ComicBookSeries cbs = ComicBookSeries.findSeries(barcode.rawValue);
                        if (cbs != null) {
                            Intent intent = new Intent(ViewSeries.this, ViewIssues.class);
                            intent.putExtra("series", cbs.getSeriesListElement()[2]);
                            startActivity(intent);
                            //TODO Change to top down notification
                        } else
                            Toast.makeText(ViewSeries.this, "Comic Book Series Not Found!", Toast.LENGTH_LONG).show();

                    }
                })

                .build();
        materialBarcodeScanner.startScan();
    }

}