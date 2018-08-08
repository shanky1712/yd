package com.bbcafe.community;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

public class MyApplication extends Application{
    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;
    private String BASEURL = "http://community.courtalam.com/";
    private String NEWSURL = BASEURL+"utilities/view_all_resources";
    private String JOBURL = BASEURL+"utilities/view_all_resources?filters=job";
    private String LOGINURL = BASEURL+"utilities/assign_work";
    private String SHOPURL = BASEURL+"utilities/view_all_contracts";
    public String getBASEURL() {
        return BASEURL;
    }
    public String getLOGINURL() {
        return LOGINURL;
    }
    public String getNEWSURL() {
        return NEWSURL;
    }
    public String getSHOPURL() {
        return SHOPURL;
    }
    public String getJOBURL() {
        return JOBURL;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MobileAds.initialize(this, "ca-app-pub-3953183793477529~4518969756");
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
}
