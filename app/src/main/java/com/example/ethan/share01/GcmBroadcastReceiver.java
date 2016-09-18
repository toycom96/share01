package com.example.ethan.share01;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.List;

/**
 * Created by on 2016-04-04.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int badgeCount = 0;
        int chatCnt = 0, etcCnt = 0;
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
        System.out.println("************************************************* Receiver 실행");

        Bundle extras = intent.getExtras();

        RbPreference mPref = new RbPreference(context);

        if (mPref.getValue("badge_chatcnt", "").equals("")) {
            chatCnt = 0;
        } else {
            chatCnt = Integer.parseInt(mPref.getValue("badge_chatcnt", "").toString());
        }
        if (mPref.getValue("badge_etccnt","").equals("")) {
            etcCnt = 0;
        } else {
            etcCnt = Integer.parseInt(mPref.getValue("badge_etccnt", "").toString());
        }

        if (extras.getString("Type").equals("1")) {
            chatCnt = chatCnt + 1;
            mPref.put("badge_chatcnt",  String.valueOf(chatCnt));
        } else {
            etcCnt = etcCnt + 1;
            mPref.put("badge_etccnt",  String.valueOf(etcCnt));
        }
        badgeCount = chatCnt + etcCnt;

/*        if (extras.getString("Badge").equals("")) {
            badgeCount = -1;
        } else {
            badgeCount = Integer.parseInt(extras.getString("Badge"));
        }*/
        if (Integer.parseInt(extras.getString("Badge")) > 0) {
            badgeCount = Integer.parseInt(extras.getString("Badge"));
        }

        if (badgeCount < 0) {

        } else {
            updateIconBadge(context, badgeCount);
        }

    }

    public static String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public static void updateIconBadge(Context context, int notiCnt) {

        if (notiCnt < 0) {
            int chatCnt = 0, etcCnt = 0;
            RbPreference mPref2 = new RbPreference(context);

            if (mPref2.getValue("badge_chatcnt", "").equals("")) {
                chatCnt = 0;
            } else {
                chatCnt = Integer.parseInt(mPref2.getValue("badge_chatcnt", "").toString());
            }
            if (mPref2.getValue("badge_etccnt", "").equals("")) {
                etcCnt = 0;
            } else {
                etcCnt = Integer.parseInt(mPref2.getValue("badge_etccnt", "").toString());
            }
            notiCnt = chatCnt + etcCnt;
        }


        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", notiCnt);
        badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
        badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
        //badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
        //badgeIntent.putExtra("badge_count_class_name", "com.example.ethan.share01.GcmBroadcastReceiver");
        context.sendBroadcast(badgeIntent);
    }

}

