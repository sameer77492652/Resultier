package actiknow.com.resultier.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by actiknow on 1/23/17.
 */

public class CleanReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent) {
     //   CustomToast.showToastMessage(context, "Uninstalling Application");
       // Toast.makeText(context,"Uninstalling Application",Toast.LENGTH_LONG).show();
       // Log.e("CleanReceiver Called",""+context);
      //  Log.d("Receiver", "Intent: " + intent.getAction());
        if (intent != null) {
            if (intent.getAction().equals(intent.ACTION_PACKAGE_REMOVED))   {
                try {
                    String packageName = intent.getData().toString();
                    //Logcat shows the packageName is "com.XXX.YYY"
                    Log.v("debug",packageName);

                    PackageManager packageManager = context.getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                    //Got NameNotFoundException
                    Log.v("debug",packageInfo.versionName);

                }catch(PackageManager.NameNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }
}