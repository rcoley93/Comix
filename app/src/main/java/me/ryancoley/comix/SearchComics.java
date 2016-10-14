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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchComics extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText etSearchQuery;
    Spinner spCategory;
    private MyCustomListAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_comics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        etSearchQuery = (EditText) findViewById(R.id.searchQuery);
        spCategory = (Spinner) findViewById(R.id.category);

        //setup the list view
        aa = new MyCustomListAdapter(SearchComics.this);
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();
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

        HelperFunctions.activityMenu(this, SearchComics.this, id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goHome(View V) {
        Intent i = new Intent(SearchComics.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


    public void search(View v) {
        aa.reset();

        String strCategory = spCategory.getSelectedItem().toString();
        String strSearchType = "", strWhere, strWhereClause;

        if (strCategory.equals("Title")) strSearchType = "issue_title";
        else if (strCategory.equals("Issue Number")) strSearchType = "issue_number";
        else if (strCategory.equals("Series")) strSearchType = "name";


        strWhere = "'%" + etSearchQuery.getText().toString() + "%'";
        strWhereClause = strSearchType + " LIKE " + strWhere;

        int i = 0;
        if (!strCategory.equals("Series")) {
            List<ComicBook> comics = ComicBook.find(ComicBook.class, strWhereClause, null);

            for (ComicBook c : comics) {
                aa.addSearchItem(new SearchListElement(c.getId(), c.getSearchListElement(), false));
                if ((++i % 5) == 0) aa.addAdItem();
            }

        } else {
            List<ComicBookSeries> series = ComicBookSeries.find(ComicBookSeries.class, strWhereClause, null);

            for (ComicBookSeries s : series){
                aa.addSearchItem(new SearchListElement(s.getId(), s.getSearchListElement(), true));
                if ((++i % 5) == 0) aa.addAdItem();
            }
        }

        aa.notifyDataSetChanged();

    }

}