package me.ryancoley.comix;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.inmobi.ads.InMobiBanner;

public class ViewComic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView etSeries, etPublisher, etIssueNumber, etIssueTitle, etPricePaid,
            etWriter, etPenciller, etInker, etColorist, etLetterer, etEditor,
            etCoverArtist, etLocationAcquired, etCoverPrice, etCoverDate,
            etAquiredDate, etGrade, etStorageMethod, etReadUnread, etComicLocation, etBarcode;

    long lngID;

    @Override
    protected void onResume() {
        super.onResume();
        viewComic();
    }

    public void viewComic() {

        ComicBook comic = ComicBook.findById(ComicBook.class, lngID);
        //TODO change to MAP

        String strCoverPriceRaw = comic.getAllDetails()[6];
        String strPricePaidRaw = comic.getAllDetails()[9];
        String strIssueNumberRaw = comic.getAllDetails()[1];

        //set all the text views to the appropriate text
        String strPricePaid = (strPricePaidRaw.length() > 5) ? strPricePaidRaw.substring(4) : "";
        String strCoverPrice = (strCoverPriceRaw.length() > 5) ? strCoverPriceRaw.substring(4) : "";
        String strIssueNumber = strIssueNumberRaw;

        if (strPricePaid == "0.0") strPricePaid = "Free";
        else if (strPricePaid.length() == 3) strPricePaid += "0";

        if (strCoverPrice == "0.0") strCoverPrice = "Free";
        else if (strCoverPrice.length() == 3) strCoverPrice += "0";

        etSeries.setText(comic.getAllDetails()[0]);
        if (!strIssueNumber.equals("-1")) etIssueNumber.setText(strIssueNumber);
        else etIssueNumber.setText(" ");
        etIssueTitle.setText(comic.getAllDetails()[2]);
        etPublisher.setText(comic.getAllDetails()[3]);
        etCoverDate.setText((comic.getAllDetails()[4].equals("0")) ? HelperFunctions.getMonth(comic.getAllDetails()[4]) : HelperFunctions.getMonth(comic.getAllDetails()[4]) + " " + comic.getAllDetails()[5]);
        etCoverPrice.setText(strCoverPrice);
        etGrade.setText(HelperFunctions.getGrade(Integer.parseInt(comic.getAllDetails()[7])));
        etStorageMethod.setText(HelperFunctions.getStorage(Integer.parseInt(comic.getAllDetails()[8])));
        etPricePaid.setText(strPricePaid);
        etWriter.setText(comic.getAllDetails()[10]);
        etPenciller.setText(comic.getAllDetails()[11]);
        etInker.setText(comic.getAllDetails()[12]);
        etColorist.setText(comic.getAllDetails()[13]);
        etLetterer.setText(comic.getAllDetails()[14]);
        etEditor.setText(comic.getAllDetails()[15]);
        etCoverArtist.setText(comic.getAllDetails()[16]);
        etReadUnread.setText(HelperFunctions.getReadUnread(Integer.parseInt(comic.getAllDetails()[17])));
        if (comic.getAllDetails()[19].equals("-1"))
            etAquiredDate.setText(HelperFunctions.getMonth(comic.getAllDetails()[18]));
        else
            etAquiredDate.setText(HelperFunctions.getMonth(comic.getAllDetails()[18]) + " " + comic.getAllDetails()[19]);
        etLocationAcquired.setText(comic.getAllDetails()[20]);
        etComicLocation.setText(comic.getAllDetails()[21]);
        etBarcode.setText(comic.getAllDetails()[22]);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = getIntent();
        lngID = i.getLongExtra("id", -1);

        etSeries = (TextView) findViewById(R.id.etseries);
        etPublisher = (TextView) findViewById(R.id.etpublisher);
        etIssueNumber = (TextView) findViewById(R.id.etissueNumber);
        etIssueTitle = (TextView) findViewById(R.id.etissueTitle);
        etCoverDate = (TextView) findViewById(R.id.etcoverDate);
        etCoverPrice = (TextView) findViewById(R.id.etcoverPrice);
        etGrade = (TextView) findViewById(R.id.etcondition);
        etStorageMethod = (TextView) findViewById(R.id.etstorageMethod);
        etPricePaid = (TextView) findViewById(R.id.etpricePaid);
        etWriter = (TextView) findViewById(R.id.etwriter);
        etPenciller = (TextView) findViewById(R.id.etpenciller);
        etInker = (TextView) findViewById(R.id.etinker);
        etColorist = (TextView) findViewById(R.id.etcolorist);
        etLetterer = (TextView) findViewById(R.id.etletterer);
        etEditor = (TextView) findViewById(R.id.eteditor);
        etCoverArtist = (TextView) findViewById(R.id.etcoverArtist);
        etLocationAcquired = (TextView) findViewById(R.id.etlocationAcquired);
        etReadUnread = (TextView) findViewById(R.id.etreadUnread);
        etAquiredDate = (TextView) findViewById(R.id.etacquiredDate);
        etComicLocation = (TextView) findViewById(R.id.etComicLocation);
        etBarcode = (TextView) findViewById(R.id.etBarcode);

        AdGenerator ag = new AdGenerator(ViewComic.this);
        ag.createBannerAd(this.findViewById(android.R.id.content),R.id.banner);

        viewComic();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_comic, menu);
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
            case R.id.action_edit_comic:
                editComic();
                return true;
            case R.id.action_remove_comic:
                DialogCreator dt = new DialogCreator(ViewComic.this);
                dt.createDeleteComicDialog(lngID, this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void editComic() {
        Intent i = new Intent(ViewComic.this, AddComic.class);
        i.putExtra("extra_data", true);
        i.putExtra("id", lngID);
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        HelperFunctions.activityMenu(this, ViewComic.this, id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goHome(View V) {
        Intent i = new Intent(ViewComic.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}
