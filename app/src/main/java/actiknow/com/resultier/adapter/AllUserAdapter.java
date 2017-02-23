package actiknow.com.resultier.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import actiknow.com.resultier.R;
import actiknow.com.resultier.activity.MainActivity;
import actiknow.com.resultier.model.User;
import actiknow.com.resultier.util.AppConfigTags;
import actiknow.com.resultier.util.Constants;
import actiknow.com.resultier.util.Utils;

/**
 * Created by actiknow on 2/3/17.
 */

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;

    private List<User> userlist = new ArrayList<User>();
    private Typeface typeface;

    public AllUserAdapter (Activity activity, List<User> userlist) {
        this.activity = activity;
        this.userlist = userlist;
        Log.e("USERLIST",""+userlist);
//        typeface = Typeface.createFromAsset (activity.getAssets (), "Kozuka-Gothic.ttf");
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_dbselect, parent, false);
        return new ViewHolder (sView);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final User user = userlist.get (position);

        holder.name.setText (user.getUser_name ().toUpperCase ());
        holder.email.setText (user.getUser_email ().toUpperCase ());
        holder.mobile.setText (user.getUser_mobile ().toUpperCase ());
    }

    @Override
    public int getItemCount () {
        return userlist.size ();
    }

    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }



    public interface OnItemClickListener {
        public void onItemClick (View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView email;
        TextView mobile;

        public ViewHolder (View view) {
            super (view);
            name = (TextView) view.findViewById (R.id.tvName);
            email = (TextView) view.findViewById (R.id.tvEmail);
            mobile = (TextView) view.findViewById (R.id.tvPhone);


            view.setOnClickListener (this);
        }

        @Override
        public void onClick (View v) {

            }
        }
    }


