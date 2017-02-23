package actiknow.com.resultier.activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Hashtable;
import java.util.Map;
import actiknow.com.resultier.R;
import actiknow.com.resultier.receiver.SmsListener;
import actiknow.com.resultier.receiver.SmsReceiver;
import actiknow.com.resultier.util.AppConfigTags;
import actiknow.com.resultier.util.AppConfigURL;
import actiknow.com.resultier.util.NetworkConnection;
import actiknow.com.resultier.util.UserDetailsPref;
import actiknow.com.resultier.util.Utils;
import static actiknow.com.resultier.util.Utils.isValidMobile;
/**
 * Created by actiknow on 1/18/17.
 */

public class LoginActivity extends AppCompatActivity {
    EditText etMobile;
    EditText etOTP;
    Button btSendSMSCode;
    TextView tvResendOtp;
    TextView tvDetectOTP;
    Button btVerify;
    int otp = 0;
    String firebase_id;
    int valid = 0;
    int user_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        etMobile = (EditText) findViewById(R.id.etMobile);
        etOTP = (EditText) findViewById(R.id.etOTP);
        tvResendOtp = (TextView) findViewById(R.id.tvResendOtp);
        tvDetectOTP = (TextView) findViewById(R.id.tvDetectOTP);
        btSendSMSCode = (Button) findViewById(R.id.btSendSMSCode);
        btVerify = (Button) findViewById(R.id.btVerify);
    }


    private void initData() {

    }

    private void displayFirebaseRegId() {
        UserDetailsPref userDetailsPref = UserDetailsPref.getInstance();
        firebase_id = userDetailsPref.getStringPref(LoginActivity.this, UserDetailsPref.USER_FIREBASE_ID);
        Log.e("sud", "Firebase reg id: " + firebase_id);
    }


    private void initListener() {
        btSendSMSCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayFirebaseRegId();
                if (etMobile.getText().toString().trim().equalsIgnoreCase(""))
                    etMobile.setError("Enter a Mobile number");
                else
                    valid = isValidMobile(etMobile.getText().toString().trim());

                switch (valid) {
                    case 1:
                        etMobile.setError("Enter a complete Mobile number");
                        break;
                    case 2:
                        etMobile.setError("Enter a valid Mobile number");
                        break;
                    case 3:
                        getOTP(etMobile.getText().toString(), String.valueOf(firebase_id));
                        break;
                    case 4:
                        etMobile.setError("Enter a Mobile number");
                        break;

                }
                OtpCountDownTimer();
                SmsReceiver.bindListener (new SmsListener() {
                    @Override
                    public void messageReceived (String messageText) {
                        Log.e ("Text", messageText);
                       // String otptext = messageText.replaceAll ("[^0-9]", "");
                        String otptext = messageText.substring(12,18);
                        Log.e("OTPMESSAGE",otptext);
                        etOTP.setText (otptext);
                        if(otptext.equals(etOTP.getText().toString().trim())){
                            Intent intent = new Intent(LoginActivity.this,LandingActivity.class);
                            intent.putExtra(AppConfigTags.STATUS,1);
                            startActivity(intent);
                            overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                            setPreferences(etMobile.getText().toString().trim(), String.valueOf(user_id),etOTP.getText().toString().trim());
                        }else{
                            Toast.makeText(LoginActivity.this,"Incorrect otp",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                setPreferences(etMobile.getText().toString().trim(), String.valueOf(user_id),etOTP.getText().toString().trim());

            }


        });


        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(String.valueOf(otp).equals(etOTP.getText().toString().trim())){
                    Intent intent = new Intent(LoginActivity.this,LandingActivity.class);
                    intent.putExtra(AppConfigTags.STATUS,1);
                    startActivity(intent);
                    setPreferences(etMobile.getText().toString().trim(), String.valueOf(user_id),etOTP.getText().toString().trim());
                    overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                }else{
                    Toast.makeText(LoginActivity.this,"Incorrect otp",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void OtpCountDownTimer() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResendOtp.setText(String.format("seconds left 00:%02d",+millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvResendOtp.setTextColor(getResources().getColor(R.color.text_color_red));
                tvResendOtp.setText("Resend OTP");

                tvResendOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(LoginActivity.this,""+String.valueOf(otp),Toast.LENGTH_LONG).show();
                    }
                });
            }

        }.start();
    }


    private void getOTP(final String mobile, final String firebase_id) {
        if (NetworkConnection.isNetworkAvailable(this)) {
            //  Utils.showProgressDialog (progressDialog, null, false);
            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.URL_GETOTP, true);
            StringRequest strRequest2 = new StringRequest(Request.Method.POST, AppConfigURL.URL_GETOTP,
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
                                        otp = jsonObj.getInt(AppConfigTags.OTP);
                                        btSendSMSCode.setVisibility(View.GONE);
                                        etOTP.setVisibility(View.VISIBLE);
                                        tvResendOtp.setVisibility(View.VISIBLE);
                                        tvDetectOTP.setVisibility(View.VISIBLE);
                                        btVerify.setVisibility(View.VISIBLE);
                                        user_id = jsonObj.getInt("id");
                                      //  etOTP.setText("" + otp);
                                        Log.e("User_id", "" + user_id);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "You are not registered on resultier", Toast.LENGTH_LONG).show();
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
                    params.put(AppConfigTags.USER_MOBILE, mobile);
                    params.put("firebase_id", firebase_id);
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest(strRequest2, 30);
        }
        else
        {
            Toast.makeText(this,"Seems like there is no internet connection",Toast.LENGTH_LONG).show();
        }

}

    private void setPreferences(String mobile,String user_id,String user_otp) {
        UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
        userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.MOBILE,mobile);
        userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.USER_ID,user_id);
        userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.USER_OTP,user_otp);
    }


}
