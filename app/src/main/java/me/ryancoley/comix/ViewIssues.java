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

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

public class ViewIssues extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String strSeries, strPublisher;
    private MyCustomListAdapter aa;

    @Override
    protected void onResume() {
        super.onResume();
        getListOfComics();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_issues);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SugarContext.init(ViewIssues.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = getIntent();
        strSeries = i.getStringExtra("series");

        //setup the list view
        aa = new MyCustomListAdapter(this);
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();

        //get the list of comics
        getListOfComics();
    }

    private void getListOfComics() {
        aa.reset();

        //get the results
        List<ComicBook> ComicIssues = HelperFunctions.sortComics(ComicBook.find(ComicBook.class, "series = ?", strSeries));

        if (ComicIssues.size() == 0) this.finish();

        //parse the results
        int i = 0;
        int adCount = (ComicIssues.size() > 1) ? (ComicIssues.size() > 2) ? (ComicIssues.size() > 10) ? (ComicIssues.size() > 20)? 7 : 5 : 3 : 2 : 1;
        for (ComicBook comic : ComicIssues) {
            aa.addComicItem(new ComicListElement(comic.getId(), comic.getComicListElement()));
            if((++i%adCount)==0) aa.addAdItem();
        }

        //update the list view
        aa.notifyDataSetChanged();
    }

    public void addComic() {
        Intent i = new Intent(ViewIssues.this, AddComic.class);
        i.putExtra("extra_data", true);
        i.putExtra("series", strSeries);
        i.putExtra("publisher", strPublisher);
        startActivity(i);

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
        getMenuInflater().inflate(R.menu.view_issues, menu);
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
                addComic();
                return true;
            case R.id.add_range:
                new DialogCreator(ViewIssues.this).createRangeDialog(strSeries, strPublisher);
                return true;
            case R.id.action_remove_series:
                new DialogCreator(ViewIssues.this).createDeleteSeriesDialog(strSeries, this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        HelperFunctions.activityMenu(this, ViewIssues.this, id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goHome(View V) {
        Intent i = new Intent(ViewIssues.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
