package me.ryancoley.comix;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.inmobi.ads.InMobiBanner;


import java.util.ArrayList;
import java.util.Map;


public class MyCustomListAdapter extends BaseAdapter {

    private static final int TYPE_AD_ITEM = 0;
    private static final int TYPE_COMIC_ITEM = 1;
    private static final int TYPE_SEARCH_ITEM = 2;
    private static final int TYPE_SERIES_ITEM = 3;
    private static final int TYPE_MAX_COUNT = TYPE_SERIES_ITEM + 1;

    private ArrayList mData = new ArrayList();
    private ArrayList mType = new ArrayList();

    private LayoutInflater mInflater;
    private Context cApp;


    public MyCustomListAdapter(Context c) {
        this.cApp = c;
        this.mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAdItem() {
        this.mData.add(null);
        this.mType.add(TYPE_AD_ITEM);
        //notifyDataSetChanged();
    }

    public void addComicItem(final ComicListElement item) {
        this.mData.add(item);
        this.mType.add(TYPE_COMIC_ITEM);
        //notifyDataSetChanged();
    }

    public void addSearchItem(final SearchListElement item) {
        this.mData.add(item);
        this.mType.add(TYPE_SEARCH_ITEM);
        //notifyDataSetChanged();
    }

    public void addSeriesItem(final SeriesListElement item) {
        this.mData.add(item);
        this.mType.add(TYPE_SERIES_ITEM);
        //notifyDataSetChanged();
    }

    public void reset(){
        mData.clear();
        mType.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return (int) this.mType.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return this.mData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return super.hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        //System.out.println("getView " + position + " " + convertView + " type = " + type);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case TYPE_AD_ITEM:
                    convertView = mInflater.inflate(R.layout.ad_list_element, null);

                    holder.banner = (InMobiBanner) convertView.findViewById(R.id.banner);

                    break;
                case TYPE_COMIC_ITEM:
                    convertView = mInflater.inflate(R.layout.comic_list_element, null);

                    holder.tvTitle = (TextView) convertView.findViewById(R.id.itemComicTitle);
                    holder.tvIssueNumber = (TextView) convertView.findViewById(R.id.itemIssueNumber);

                    break;
                case TYPE_SEARCH_ITEM:
                    convertView = mInflater.inflate(R.layout.search_list_element, null);

                    holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
                    holder.tvIssueNumber = (TextView) convertView.findViewById(R.id.issueNumber);
                    holder.tvPublisher = (TextView) convertView.findViewById(R.id.publisher);
                    holder.tvSeries = (TextView) convertView.findViewById(R.id.series);

                    break;
                case TYPE_SERIES_ITEM:
                    convertView = mInflater.inflate(R.layout.series_list_element, null);

                    holder.tvSeries = (TextView) convertView.findViewById(R.id.itemSeriesTitle);
                    holder.tvIssueNumber = (TextView) convertView.findViewById(R.id.itemNumberOfIssues);
                    holder.tvPublisher = (TextView) convertView.findViewById(R.id.itemPublisher);



                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch(type){
            case TYPE_AD_ITEM:
                AdGenerator ag = new AdGenerator(cApp);
                ag.createBannerAd(holder);
                break;
            case TYPE_COMIC_ITEM:{
                final ComicListElement item = (ComicListElement) getItem(position);

                holder.tvTitle.setText(item.title);
                holder.tvIssueNumber.setText(item.issueNumber);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(cApp, ViewComic.class);
                        intent.putExtra("extra_data", true);
                        intent.putExtra("id", item.id);
                        cApp.startActivity(intent);
                    }

                });

                }
                break;
            case TYPE_SEARCH_ITEM:{
                final SearchListElement item = (SearchListElement) getItem(position);
                final Map<String,String> details = item.getDetails();

                holder.tvTitle.setText(details.get("title"));
                holder.tvIssueNumber.setText(details.get("issue_number"));
                holder.tvPublisher.setText(details.get("publisher"));
                holder.tvSeries.setText(details.get("series"));

                if (item.isSeries()) {
                    holder.tvPublisher.getLayoutParams().height = 0;
                    holder.tvTitle.setText(details.get("publisher"));
                    holder.tvSeries.setText(details.get("title"));
                    //holder.tvSeries.setTextAppearance(cApp, android.R.style.TextAppearance_Large);
                }else{
                   // holder.tvSeries.setTextAppearance(cApp,android.R.style.TextAppearance_Small);
                    holder.tvPublisher.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
                }


                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        if (!item.isSeries()) {
                            intent = new Intent(cApp, ViewComic.class);
                            intent.putExtra("extra_data", true);
                            intent.putExtra("id", Long.parseLong(details.get("id")));
                        } else {
                            intent = new Intent(cApp, ViewIssues.class);
                            //TODO Change to ID
                            intent.putExtra("series", details.get("id"));
                        }
                        cApp.startActivity(intent);
                    }

                });
                }
                break;
            case TYPE_SERIES_ITEM:
                final SeriesListElement item = (SeriesListElement) getItem(position);

                String strLabel = (item.getIssueCount() < 2) ? " issue" : " issues";
                holder.tvSeries.setText(item.getSeries());
                holder.tvIssueNumber.setText(String.valueOf(item.getIssueCount()) + strLabel);
                holder.tvPublisher.setText(item.getPublisher());



                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(cApp, ViewIssues.class);
                        intent.putExtra("series", item.getSeriesID());
                        cApp.startActivity(intent);

                    }

                });

                break;
        }

        return convertView;

    }

    public static class ViewHolder {
        TextView tvTitle, tvSeries, tvPublisher, tvIssueNumber;
        public InMobiBanner banner;
    }
}
