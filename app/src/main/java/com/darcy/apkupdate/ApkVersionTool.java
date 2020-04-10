package com.darcy.apkupdate;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import hugo.weaving.DebugLog;

import java.text.MessageFormat;
import java.util.List;

import static com.darcy.apkupdate.constant.APKPATH;

@DebugLog
public class ApkVersionTool {
    private static final String TAG = ApkVersionTool.class.getSimpleName();
    public static void setPathOfApkToInstall(String pathOfApkToInstall) {
        ApkVersionTool.pathOfApkToInstall = pathOfApkToInstall;
    }

    private static String pathOfApkToInstall = APKPATH;

    private static boolean isApkInstalled(Context ctx) {
        PackageInfo newApk = ctx.getPackageManager().getPackageArchiveInfo(pathOfApkToInstall, 0);
        String pkgName = newApk.packageName;
        List<ApplicationInfo> infoList = ctx.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo applicationInfo : infoList) {
            if (applicationInfo.packageName.equals(pkgName))
                return true;
        }
        return false;
    }

    private static boolean isLatestVCode(long installed, long newApk) {
        return newApk > installed ? true : false;
    }

    private static boolean isLatestVName(String installed, String newApk) {
        Log.i(TAG, MessageFormat.format("installed:{0},newApk:{1}", installed, newApk));

        int res = 0;

        String[] oldNumbers = installed.split("\\.");
        String[] newNumbers = newApk.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;//新版新，
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;//新版旧
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res == -1 ? true : false;
    }


    private static String getVersionName(PackageInfo pkgInfo) {
        if (pkgInfo != null) {
            return pkgInfo.versionName;
        }
        return "";
    }

    private static long getVersionCode(PackageInfo pkgInfo) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            return pkgInfo.getLongVersionCode();
        } else {
            return pkgInfo.versionCode;
        }
    }

    /**
     * @return true only if OutOfDate else false
     */
    public static boolean isLatestApkVersion(Context ctx) {
        PackageInfo newApk = ctx.getPackageManager().getPackageArchiveInfo(pathOfApkToInstall, 0);
        String pkgName = newApk.packageName;
        PackageInfo installedApk = null;
        try {
            installedApk = ctx.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String installedVName = getVersionName(installedApk);
        String newApkVName = getVersionName(newApk);
        long installedApkVCode = getVersionCode(installedApk);
        long newApkVCode = getVersionCode(newApk);

        boolean upDate = isLatestVCode(installedApkVCode, newApkVCode);
        boolean update2 = isLatestVName(installedVName, newApkVName);

        if (upDate || update2) {
            return true;
        }
        return false;
    }


    public static boolean isApkNeedToInstall(Context ctx) {
        if (!isApkInstalled(ctx) || isLatestApkVersion(ctx)) {
            return true;
        }
        return false;
    }

}
