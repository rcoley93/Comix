package me.ryancoley.comix;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.InMobiNative;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ryan on 10/5/2016.
 */

public class AdGenerator {
    private Context c;
    private final Boolean blnAdsEnabled = BuildConfig.HAS_ADS;

    public AdGenerator(Context cApp) {
        this.c = cApp;
    }

    /*public void createNativeAd(MyCustomListAdapter.ViewHolder v) {
        MyNativeAdListener nal = new MyNativeAdListener();
        InMobiNative nativeAd = new InMobiNative(1474559768072L, nal);
        nativeAd.load();
        adLoader al = new adLoader(nal,v);
        al.execute();
    }*/

    public void createInterstitialAd() {
        // ‘this’ is used to specify context, replace it with the appropriate context as needed.
        InMobiInterstitial interstitial = new InMobiInterstitial(c, 1474854263257L, new InMobiInterstitial.InterstitialAdListener() {
            @Override
            public void onAdRewardActionCompleted(InMobiInterstitial ad, Map rewards) {
            }

            @Override
            public void onAdDisplayed(InMobiInterstitial ad) {
            }

            @Override
            public void onAdDismissed(InMobiInterstitial ad) {
            }

            @Override
            public void onAdInteraction(InMobiInterstitial ad, Map params) {
            }

            @Override
            public void onAdLoadSucceeded(final InMobiInterstitial ad) {
            }

            @Override
            public void onAdLoadFailed(InMobiInterstitial ad, InMobiAdRequestStatus requestStatus) {
            }

            @Override
            public void onUserLeftApplication(InMobiInterstitial ad) {
            }
        });
        if(blnAdsEnabled) {
            interstitial.load();
            while (!interstitial.isReady() && HelperFunctions.isOnline(c)) ;
            if (HelperFunctions.isOnline(c)) interstitial.show();
        }
    }

    public void createBannerAd(View v, int bannerID) {
        InMobiBanner banner = (InMobiBanner) v.findViewById(bannerID);
        if(blnAdsEnabled) banner.load();
        else{
            banner.getLayoutParams().height = 0;
            banner.getLayoutParams().width = 0;
        }
    }

    public  void  createBannerAd(MyCustomListAdapter.ViewHolder v){
        if(blnAdsEnabled) v.banner.load();
        else{
            v.banner.getLayoutParams().height = 0;
            v.banner.getLayoutParams().width = 0;
        }
    }

   /* private class adLoader extends AsyncTask<String, Integer, String> {

        MyNativeAdListener nal;
        MyCustomListAdapter.ViewHolder adView;
        public adLoader(MyNativeAdListener nalAdListener, MyCustomListAdapter.ViewHolder v){
            this.nal = nalAdListener;
            this.adView = v;
        }

        @Override
        protected String doInBackground(String... params) {
            while(!nal.isReady() && HelperFunctions.isOnline(c));
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            adView.banner.load();
            /*if(HelperFunctions.isOnline(c) && nal.isValid()) {
                final Map<String, String> adData = nal.getAdData();

                adView.tvTitle.setText(adData.get("title"));
                adView.tvDescription.setText(adData.get("description"));

                if(adData.get("icon") != null){
                    adView.ivIcon.setVisibility(View.VISIBLE);
                    try {
                        final URL url = new URL(adData.get("icon"));
                        Thread thread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    adView.ivIcon.setImageBitmap(bmp);
                                } catch (Exception e) {}
                            }
                        });
                        thread.start();

                    } catch (MalformedURLException ex) {
                    } catch (IOException ex) {}



                }else adView.ivIcon.setVisibility(View.INVISIBLE);


                if(adData.get("rating")!=null) {
                    adView.rbRating.setVisibility(View.VISIBLE);
                    adView.rbRating.setMax(5);
                    adView.rbRating.setProgress(Integer.parseInt(adData.get("rating")));
                }else adView.rbRating.setVisibility(View.INVISIBLE);


                adView.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adData.get("URL")));
                        c.startActivity(browserIntent);
                    }
                });
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... text) {
        }
    }

    private class MyNativeAdListener implements InMobiNative.NativeAdListener {
        private Map<String, String> adData = new HashMap<>();
        private boolean isReady = false, isValid = false;

        public boolean isReady(){
            return this.isReady;
        }

        public boolean isValid() {
            return this.isValid;
        }

        public Map<String,String> getAdData(){
            return adData;
        }

        @Override
        public void onAdLoadSucceeded(InMobiNative inMobiNative) {
            JSONObject jData;

            try {
                jData = new JSONObject((String) inMobiNative.getAdContent());
                isValid=true;

                JSONObject iconData = jData.getJSONObject("icon");

                this.adData.put("title",jData.getString("title"));
                this.adData.put("URL",jData.getString("landingURL"));
                this.adData.put("rating",String.valueOf(jData.getInt("rating")));
                this.adData.put("cta",jData.getString("cta"));

                this.adData.put("icon",iconData.getString("url"));
                this.adData.put("width",String.valueOf(iconData.getInt("width")));
                this.adData.put("height",String.valueOf(iconData.getInt("height")));


            } catch (JSONException e) {
                jData = new JSONObject();
                e.printStackTrace();
            }


            this.isReady = true;
        }

        @Override
        public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {

        }

        @Override
        public void onAdDismissed(InMobiNative inMobiNative) {

        }

        @Override
        public void onAdDisplayed(InMobiNative inMobiNative) {

        }

        @Override
        public void onUserLeftApplication(InMobiNative inMobiNative) {

        }
    }*/
}
