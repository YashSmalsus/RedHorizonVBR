package com.smalsus.redhorizonvbr.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smalsus.redhorizonvbr.R;

import java.util.ArrayList;
import java.util.List;

public class InsallAppList extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;

    private RelativeLayout mRelativeLayout;
    private ListView mListView;
    Drawable icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insall_app_list);


        mContext = getApplicationContext();

        // Get the activity
        mActivity = InsallAppList.this;


        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
        mListView = (ListView) findViewById(R.id.lv);

        // Initialize a new Intent which action is main
        Intent intent = new Intent(Intent.ACTION_MAIN,null);

        // Set the newly created intent category to launcher
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Set the intent flags
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK|
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        );

        // Generate a list of ResolveInfo object based on intent filter
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent,0);

        // Initialize a new ArrayList for holding non system package names
        List<String> packageNames = new ArrayList<>();

        // Loop through the ResolveInfo list
        for(ResolveInfo resolveInfo : resolveInfoList){
            // Get the ActivityInfo from current ResolveInfo
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            // If this is not a system app package
            if(!isSystemPackage(resolveInfo)){
                // Add the non system package to the list
                PackageManager pm = this.getPackageManager();
                packageNames.add(activityInfo.applicationInfo.loadLabel(pm).toString());
               icon=activityInfo.applicationInfo.loadIcon(pm);
                String name =activityInfo.applicationInfo.loadLabel(pm).toString();

            }
        }

        // Initialize an ArrayAdapter using non system package names
        ArrayAdapter adapter = new ArrayAdapter<String>(
                mContext,
                android.R.layout.simple_list_item_1,
                packageNames
        );

        // DataBind the ListView with adapter
        mListView.setAdapter(adapter);

        // Set an item click listener for the ListView widget
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the ListView selected item
                String selectedItem = (String) adapterView.getItemAtPosition(i);

                // Display a Toast notification for clicked item
                Toast.makeText(mContext,"CLICKED : " + selectedItem,Toast.LENGTH_SHORT).show();

                // Get the intent to launch the specified application
                Intent intent = getPackageManager().getLaunchIntentForPackage(selectedItem);
                if(intent != null){
                    startActivity(intent);
                }else {
                    Toast.makeText(mContext,selectedItem + " Launch Error.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Custom method to determine an app is system app
    private boolean isSystemPackage(ResolveInfo resolveInfo){
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
}
