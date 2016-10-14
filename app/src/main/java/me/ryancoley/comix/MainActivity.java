package me.ryancoley.comix;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.orm.SugarContext;
import com.sromku.simple.storage.SimpleStorage;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static TextView tvTotalComics, tvSeries, tvPublishers, tvReadUnread, tvBagged, tvBoarded;
    static TextView etSeries1, etIssueNumber1, etSeries2, etIssueNumber2, etSeries3, etIssueNumber3,
            etSeries4, etIssueNumber4, etSeries5, etIssueNumber5;
    static TextView tvRA;
    static TableLayout tlRA;
    static Display display;
    static Context c;

    public static void loadStats() {
        tvTotalComics.setText(String.valueOf(ComicBook.count(ComicBook.class)));
        tvSeries.setText(String.valueOf(ComicBook.count(ComicBookSeries.class)));
        tvPublishers.setText(String.valueOf(ComicBook.count(ComicBookPublisher.class)));
        tvReadUnread.setText(String.valueOf(ComicBook.count(ComicBook.class, "read_unread=1", null)));
        long bagged = ComicBook.count(ComicBook.class, "storage_method=1", null);
        long boarded = ComicBook.count(ComicBook.class, "storage_method=2", null);
        tvBagged.setText(String.valueOf(bagged + boarded));
        tvBoarded.setText(String.valueOf(boarded));
        getRecentlyAdded();
    }

    private static void getRecentlyAdded() {
        TextView tvRecent[] = {etSeries1, etIssueNumber1, etSeries2, etIssueNumber2, etSeries3, etIssueNumber3, etSeries4, etIssueNumber4, etSeries5, etIssueNumber5};
        List<ComicBook> comics = ComicBook.find(ComicBook.class, null, null, null, "id DESC", "5");

        for (int i = 0; i < 10; i++) {
            //clear the text just in case all comics deleted
            tvRecent[i].setText("");
        }
        if (comics.size() == 0) etSeries1.setText("No Comics in Database!");
        else {
            for (int i = 0, j = 0; i < 5; i++) {
                if (i < comics.size()) {
                    ComicBook c = comics.get(i);
                    tvRecent[j++].setText(c.getSeriesName());
                    tvRecent[j++].setText(String.valueOf(c.getIssueNumber()));
                } else break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ads
        InMobiSdk.init(this, "0206d6da23ef48f0bd4b15b28ac6ae60"); //'this' is used specify context

        //db
        SugarContext.init(MainActivity.this);

        //layout & UI Setup
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        tvTotalComics = (TextView) findViewById(R.id.totalComics);
        tvSeries = (TextView) findViewById(R.id.totalSeries);
        tvPublishers = (TextView) findViewById(R.id.totalPublishers);
        tvReadUnread = (TextView) findViewById(R.id.totalRead);
        tvBagged = (TextView) findViewById(R.id.totalBagged);
        tvBoarded = (TextView) findViewById(R.id.totalBoarded);

        etSeries1 = (TextView) findViewById(R.id.series1);
        etIssueNumber1 = (TextView) findViewById(R.id.issueNumber1);
        etSeries2 = (TextView) findViewById(R.id.series2);
        etIssueNumber2 = (TextView) findViewById(R.id.issueNumber2);
        etSeries3 = (TextView) findViewById(R.id.series3);
        etIssueNumber3 = (TextView) findViewById(R.id.issueNumber3);
        etSeries4 = (TextView) findViewById(R.id.series4);
        etIssueNumber4 = (TextView) findViewById(R.id.issueNumber4);
        etSeries5 = (TextView) findViewById(R.id.series5);
        etIssueNumber5 = (TextView) findViewById(R.id.issueNumber5);
        tvRA = (TextView) findViewById(R.id.textViewRA);
        tlRA = (TableLayout) findViewById(R.id.recentAdd);
        display = getWindowManager().getDefaultDisplay();
        c = this;


        AdGenerator ag = new AdGenerator(MainActivity.this);
        ag.createBannerAd(this.findViewById(android.R.id.content),R.id.banner);

        //load data
        loadStats();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
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

        HelperFunctions.activityMenu(this, MainActivity.this, id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goHome(View V) {
        /*Intent i = new Intent(MainActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            ImportComics ic = new ImportComics(filePath, MainActivity.this);
            ic.execute();
            loadStats();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i = new Intent(MainActivity.this, AddComic.class);

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add_comic:
                startActivity(i);
                return true;
            case R.id.action_barcode_comic:
                i.putExtra("extra_data", true);
                i.putExtra("barcode", true);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
