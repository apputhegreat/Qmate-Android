package com.quotemate.qmate.util;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by anji kinnara on 2/16/17.
 */

public class Analytics {
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void init(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static void sendStartedAppEvent() {
        mFirebaseAnalytics.logEvent("started_app", null);
    }

    public static void sendGSigninEvent() {
        mFirebaseAnalytics.logEvent("google_signin_screen", null);
    }

    public static void sendSpinEvent() {
        mFirebaseAnalytics.logEvent("spin", null);
    }

    public static void sendFacebookLoginEvent() {
        mFirebaseAnalytics.logEvent("facebook_login", null);
    }

    public static void sendTermsConditionEvent() {
        mFirebaseAnalytics.logEvent("terms_condition_screen", null);
    }

    public static void sendPrivacyPolicyEvent() {
        mFirebaseAnalytics.logEvent("privacy_policy_screen", null);
    }

    public static void sendProfileEvent() {
        mFirebaseAnalytics.logEvent("history_screen", null);
    }

    public static void sendAboutEvent() {
        mFirebaseAnalytics.logEvent("about_screen", null);
    }

    public static void sendFAQEvent() {
        mFirebaseAnalytics.logEvent("faqs_screen", null);
    }

    public static void sendHelpEvent() {
        mFirebaseAnalytics.logEvent("help_screen", null);
    }

    public static void sendInviteEvent() {
        mFirebaseAnalytics.logEvent("Invite_send", null);
    }

    public static void sendLogoutEvent() {
        mFirebaseAnalytics.logEvent("logout", null);
    }

    public static void sendSearchEvent() {
        mFirebaseAnalytics.logEvent("search_click", null);
    }

    public static void sendAuthorSelectEvent(String author) {
        Bundle params = new Bundle();
        params.putString("author", author);
        mFirebaseAnalytics.logEvent("author_select", params);
    }

    public static void sendTagSelectEvent(String tag) {
        Bundle params = new Bundle();
        params.putString("tag", tag);
        mFirebaseAnalytics.logEvent("tag_select", params);
    }

    public static void sendLikeEvent() {
        mFirebaseAnalytics.logEvent("like_click", null);
    }

    public static void sendBookmarkEvent() {
        mFirebaseAnalytics.logEvent("bookmark_click", null);
    }

    public static void sendBookmarksEvent() {
        mFirebaseAnalytics.logEvent("bookmarks", null);
    }

    public static void sendBookmarksDeatailEvent() {
        mFirebaseAnalytics.logEvent("bookmarks_detail", null);
    }

    public static void sendShareEvent() {
        mFirebaseAnalytics.logEvent("share_click", null);
    }

    public static void  cardsOnCloseApp(int total) {
        Bundle params = new Bundle();
        params.putInt("quotes_count", total);
        mFirebaseAnalytics.logEvent("quotes_read_before_close", null);
    }

}