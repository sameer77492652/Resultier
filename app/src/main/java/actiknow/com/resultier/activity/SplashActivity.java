package actiknow.com.resultier.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import actiknow.com.resultier.R;
import actiknow.com.resultier.util.UserDetailsPref;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by actiknow on 1/23/17.
 */

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    public static int PERMISSION_REQUEST_CODE = 11;
    UserDetailsPref userDetailsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkPermissions ();
        initData();
    }


    private void initData() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                isLogin();
                //finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void isLogin () {
        userDetailsPref = UserDetailsPref.getInstance ();
        Log.e("Mobile",""+userDetailsPref.getStringPref(this, UserDetailsPref.MOBILE));
        Log.e("getOtp",""+userDetailsPref.getStringPref(this, UserDetailsPref.USER_OTP));
        if (userDetailsPref.getStringPref(this, UserDetailsPref.MOBILE).equalsIgnoreCase("") || userDetailsPref.getStringPref(this, UserDetailsPref.USER_OTP).equalsIgnoreCase("")) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        }else{
            Intent myIntent = new Intent(this, LandingActivity.class);
            startActivity(myIntent);
            overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }


    public void checkPermissions () {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission (android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission (android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission (WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.INTERNET, android.Manifest.permission.RECEIVE_BOOT_COMPLETED, WRITE_EXTERNAL_STORAGE},
                            this.PERMISSION_REQUEST_CODE);
                }

            }
            else{
                //finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale (permission);
                    if (! showRationale) {
                      /*  AlertDialog.Builder builder = new AlertDialog.Builder (SplashActivity.this);
                        builder.setMessage ("Permission are required please enable them on the App Setting page")
                                .setCancelable (false)
                                .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                    public void onClick (DialogInterface dialog, int id) {
                                        dialog.dismiss ();
                                       // Intent intent = new Intent (Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                             //   Uri.fromParts ("package", getPackageName (), null));
                                       // startActivity (intent);
                                    }
                                });
                        AlertDialog alert = builder.create ();
                        alert.show ();*/
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (android.Manifest.permission.CAMERA.equals (permission)) {
//                        Utils.showToast (this, "Camera Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    } else if (android.Manifest.permission.ACCESS_FINE_LOCATION.equals (permission)) {
//                        Utils.showToast (this, "Location Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    } else if (WRITE_EXTERNAL_STORAGE.equals (permission)) {
//                        Utils.showToast (this, "Write Permission is required");
//                        showRationale (permission, R.string.permission_denied_contacts);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    }
                }
            }


            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }

    }


}
