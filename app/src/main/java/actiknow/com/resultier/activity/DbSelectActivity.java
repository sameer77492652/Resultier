package actiknow.com.resultier.activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import actiknow.com.resultier.R;
import actiknow.com.resultier.adapter.AllUserAdapter;
import actiknow.com.resultier.helper.DatabaseHandler;
import actiknow.com.resultier.model.User;
import actiknow.com.resultier.util.AppConfigTags;
import actiknow.com.resultier.util.AppConfigURL;
import actiknow.com.resultier.util.NetworkConnection;
import actiknow.com.resultier.util.Utils;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by actiknow on 2/3/17.
 */

public class DbSelectActivity extends AppCompatActivity {
    int  json_array_len = 0;
    RecyclerView rvGetUser;
    AllUserAdapter adapter;
    private List<User> userlist = new ArrayList<>();
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbselect);
        initView ();
        initData ();
        getDetailsfromServer ();
        db.closeDB ();
    }

    private void initData() {
        db = new DatabaseHandler (getApplicationContext ());
        adapter = new AllUserAdapter (this, userlist);
        //    Constants.questionsList.clear ();

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (adapter);
        alphaAdapter.setDuration (700);
        rvGetUser.setAdapter (alphaAdapter);
        rvGetUser.setHasFixedSize (true);
        rvGetUser.setLayoutManager (new LinearLayoutManager (this));
        rvGetUser.setItemAnimator (new DefaultItemAnimator ());
    }

    private void initView() {
        rvGetUser = (RecyclerView) findViewById(R.id.rvGetUser);
    }

    private void getDetailsfromServer() {
        if (NetworkConnection.isNetworkAvailable(this)) {
            //  Utils.showProgressDialog (progressDialog, null, false);
            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.URL_GETUSERLIST, true);
            StringRequest strRequest2 = new StringRequest(Request.Method.POST, AppConfigURL.URL_GETUSERLIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            int is_data_received = 0;
                            int json_array_len = 0;
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                is_data_received = 1;
                                try {
                                    //   progressDialog.dismiss ();
                                    JSONObject jsonObj = new JSONObject(response);
                                    JSONArray jsonArray = jsonObj.getJSONArray ("details");
                                    db.deleteAllUsers ();
                                    json_array_len = jsonArray.length ();
                                    for(int i = 0; i < json_array_len; i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                                        User user = new User ();
                                        user.setUser_id(jsonObject.getInt("id"));
                                        user.setUser_name(jsonObject.getString("name"));
                                        user.setUser_email(jsonObject.getString("email"));
                                        user.setUser_mobile(jsonObject.getString("mobile"));
                                        userlist.add(user);
                                        db.createUser (user);
                                        Log.e("Data",""+userlist);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //  progressDialog.dismiss ();
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            adapter.notifyDataSetChanged ();
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
                            getUserListFromLocalDatabase ();
                        }
                    });
            Utils.sendRequest(strRequest2, 30);
        }else{
            Toast.makeText(this,"Seems like there is no internet connection",Toast.LENGTH_LONG).show();
            getUserListFromLocalDatabase ();
        }
    }
    private void getUserListFromLocalDatabase () {
        Utils.showLog (Log.DEBUG, AppConfigTags.TAG, "Getting all the atm from local database", true);
        userlist.clear ();
        List<User> allUser = db.getAllUsers ();
        for (User user : allUser)
            userlist.add (user);
        Log.e("USERLIST",""+userlist);
        adapter.notifyDataSetChanged ();
    }
}
