package actiknow.com.resultier.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Hashtable;
import java.util.Map;
import actiknow.com.resultier.R;
import actiknow.com.resultier.app.Config;
import actiknow.com.resultier.util.AppConfigTags;
import actiknow.com.resultier.util.AppConfigURL;
import actiknow.com.resultier.util.NetworkConnection;
import actiknow.com.resultier.util.NotificationUtils;
import actiknow.com.resultier.util.UserDetailsPref;
import actiknow.com.resultier.util.Utils;

import static android.R.id.input;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    UserDetailsPref userDetailsPref;
    ImageView imHappy, imNeutral, imSad;
    Button btSubmit;
    int state = 0;
    int push_noti_question_id = 0;
    int  ques_id = 0;
    String user_id;
    String user_otp;
    private RadioGroup radioGroup;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLogin ();
        initView();
        getBroadCast();
        initData();

    }



    private void initData() {
        imHappy.setOnClickListener(this);
        imNeutral.setOnClickListener(this);
        imSad.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
    }


    private void getBroadCast() {
        Intent intent = getIntent();
        push_noti_question_id = intent.getIntExtra("id",0);
        if(push_noti_question_id != 0) {
            getAnswerFromServer(String.valueOf(push_noti_question_id));
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications

                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    ques_id = intent.getIntExtra("id", 0);
                    Log.e("ques_id",""+ques_id);
                  //  getAnswerFromServer(String.valueOf(question_id));


                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();

    }

    private void initView() {
        txtRegId   = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);
        imHappy    = (ImageView)findViewById(R.id.imHappy);
        imNeutral  = (ImageView)findViewById(R.id.imNeutral);
        imSad      = (ImageView) findViewById(R.id.imSad);
        btSubmit   = (Button) findViewById(R.id.btSubmit);
    }



    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
        String firebase_id = userDetailsPref.getStringPref (MainActivity.this, UserDetailsPref.USER_FIREBASE_ID);
        user_id = userDetailsPref.getStringPref(MainActivity.this,UserDetailsPref.USER_ID);
        user_otp = userDetailsPref.getStringPref(MainActivity.this,UserDetailsPref.USER_OTP);
        Log.e("MainActivity User_id",user_id);
        Log.e ("sud", "Firebase reg id: " + firebase_id);

        if (!TextUtils.isEmpty(firebase_id))
            txtRegId.setText("");
        else
            txtRegId.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void isLogin () {
        userDetailsPref = UserDetailsPref.getInstance ();
        Log.e("Mobile",""+userDetailsPref.getStringPref(this, UserDetailsPref.MOBILE));
        Log.e("OTP",""+userDetailsPref.getStringPref(this, UserDetailsPref.USER_OTP));
        if (userDetailsPref.getStringPref(this, UserDetailsPref.MOBILE).equalsIgnoreCase("") || userDetailsPref.getStringPref(this, UserDetailsPref.USER_OTP).equalsIgnoreCase("0")) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void getAnswerFromServer(final String question_id) {
        if (NetworkConnection.isNetworkAvailable(this)) {
            //  Utils.showProgressDialog (progressDialog, null, false);
            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.URL_GETOPTIONS, true);
            StringRequest strRequest2 = new StringRequest(Request.Method.POST, AppConfigURL.URL_GETOPTIONS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    //   progressDialog.dismiss ();
                                    JSONObject jsonObj = new JSONObject(response);
                                    String question = jsonObj.getString(AppConfigTags.QUESTION);
                                    Log.e("Question", question);
                                        txtMessage.setText(question);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //  progressDialog.dismiss ();
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //   progressDialog.dismiss ();
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
//
                            }
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.QUESTION_ID, question_id);
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest(strRequest2, 30);
        }else{
            Toast.makeText(this,"Seems like there is no internet connection",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.imHappy:
                state = 1;
                imHappy.setImageResource(R.drawable.ic_ic_happy2);
                imNeutral.setImageResource(R.drawable.ic_neutral);
                imSad.setImageResource(R.drawable.ic_sad);
                break;

            case R.id.imNeutral:
                state = 2;
                imHappy.setImageResource(R.drawable.ic_happy);
                imNeutral.setImageResource(R.drawable.ic_ic_neutral2);
                imSad.setImageResource(R.drawable.ic_sad);
                break;

            case R.id.imSad:
                state = 3;
                Alertdialog(state);
                imHappy.setImageResource(R.drawable.ic_happy);
                imNeutral.setImageResource(R.drawable.ic_neutral);
                imSad.setImageResource(R.drawable.ic_ic_sad);
                break;

            case R.id.btSubmit:
                if (state == 1 || state == 2 || state == 3) {
                    if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {

                        if(!String.valueOf(ques_id).equalsIgnoreCase("0")  || !String.valueOf(push_noti_question_id).equalsIgnoreCase("0")) {

                            if (String.valueOf(ques_id).equalsIgnoreCase("0")) {
                                Log.e("Question_id", String.valueOf(push_noti_question_id));
                                PutRemarkToServer(String.valueOf(state), String.valueOf(push_noti_question_id), user_id, "");
                            } else {
                                PutRemarkToServer(String.valueOf(state), String.valueOf(ques_id), user_id, "");
                            }

                        }else
                        {
                            Toast.makeText(this,"Currently there is No Question",Toast.LENGTH_LONG).show();
                        }

                    }else
                    {
                        Toast.makeText(this,"Seems like there is no internet connection",Toast.LENGTH_LONG).show();
                    }

                 //   }
                }
                else
                {
                    Toast.makeText(this, "Please select a response first", Toast.LENGTH_LONG).show();
                    break;
                }
        }

    }

    private void Alertdialog(final int state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_remark,null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        final EditText etRemark = (EditText) dialogView.findViewById(R.id.etRemark);
        TextView tvRemarkCancel = (TextView) dialogView.findViewById(R.id.tvRemarkCancel);
        TextView tvSubmit  = (TextView) dialogView.findViewById(R.id.tvSubmit);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        tvRemarkCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();
            }
        });

        // Set negative/no button click listener
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                String user = etRemark.getText().toString();
                if (String.valueOf(ques_id).equalsIgnoreCase("0")) {
                    Log.e("Question_id", String.valueOf(push_noti_question_id));
                    PutRemarkToServer(String.valueOf(state), String.valueOf(push_noti_question_id), user_id, user);
                } else {
                    PutRemarkToServer(String.valueOf(state), String.valueOf(ques_id), user_id, user);
                }
                dialog.cancel();
            }
        });
        // Display the custom alert dialog on interface
        dialog.show();
    }

    private void PutRemarkToServer(final String state, final String ques_id, final String user_id, final String remark) {
        if (NetworkConnection.isNetworkAvailable(this)) {
            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.URL_PUTREMARK, true);
            StringRequest strRequest2 = new StringRequest(Request.Method.POST, AppConfigURL.URL_PUTREMARK,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    //   progressDialog.dismiss ();
                                    JSONObject jsonObj = new JSONObject(response);
                                    int status = jsonObj.getInt("status");
                                    if (status != 0) {
                                        Log.e(AppConfigTags.STATUS, "" + status);
                                        Toast.makeText(MainActivity.this, "Thanks for giving your feedback", Toast.LENGTH_LONG).show();
                                        finish();
                                    }else{
                                        Toast.makeText(MainActivity.this,"Incorrect Question Id",Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //  progressDialog.dismiss ();
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //   progressDialog.dismiss ();
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
//
                            }
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.RESPONSE, state);
                    params.put(AppConfigTags.QUESTION_ID, ques_id);
                    params.put(AppConfigTags.USER_ID, user_id);
                    params.put(AppConfigTags.REMARK, remark);
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest(strRequest2, 30);
        }
        else{
            Toast.makeText(this,"Seems like there is no internet connection",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
