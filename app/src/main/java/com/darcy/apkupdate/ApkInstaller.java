package com.darcy.apkupdate;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

import static com.darcy.apkupdate.constant.APKPATH;

public class ApkInstaller {
    public static void install(Activity ctx) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contentUri = FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".fileProvider"
                    , new File(APKPATH));
        } else {
            contentUri = Uri.fromFile(new File(APKPATH));
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //授予权限
        List<ResolveInfo> resInfoList = ctx.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            ctx.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        ctx.startActivity(intent);
    }
}
