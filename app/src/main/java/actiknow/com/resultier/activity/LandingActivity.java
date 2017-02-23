package actiknow.com.resultier.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import actiknow.com.resultier.R;
import actiknow.com.resultier.util.AppConfigTags;
import actiknow.com.resultier.util.UserDetailsPref;

/**
 * Created by actiknow on 2/14/17.
 */

public class LandingActivity extends AppCompatActivity{
    int status = 0;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Intent intent = getIntent();
        status = intent.getIntExtra(AppConfigTags.STATUS,0);
        TextView text = (TextView) findViewById(R.id.text);
        Log.e(AppConfigTags.STATUS,""+status);
        if(status == 1){
            text.setText("You are successfully Registered");
        }else{
            text.setText("You will get notification for every question");
        }
    }
}
