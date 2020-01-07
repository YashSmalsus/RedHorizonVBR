package com.smalsus.redhorizonvbr.view.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smalsus.redhorizonvbr.ImageUtils;
import com.smalsus.redhorizonvbr.R;
import com.smalsus.redhorizonvbr.adapters.FriendListAdapter;
import com.smalsus.redhorizonvbr.databinding.ActivityGroupDetailsScreenBinding;
import com.smalsus.redhorizonvbr.model.EventUser;
import com.smalsus.redhorizonvbr.model.GroupModel;
import com.smalsus.redhorizonvbr.utils.CalenderUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailsScreen extends BaseActivity implements ImageUtils.ImageAttachmentListener{
    private ActivityGroupDetailsScreenBinding groupDetailsScreenBinding;
    private Menu menu;
    private FriendListAdapter friendListAdapter;
    private GroupModel groupModelDetails;
    private TextView screnTitle;
    public static final String GROUPDETAILSDATA="group_details_data";


    public int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupDetailsScreenBinding= DataBindingUtil.setContentView(this,R.layout.activity_group_details_screen);
        setSupportActionBar(groupDetailsScreenBinding.toolbar);
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        screnTitle=  findViewById(titleId);
        Intent intent=getIntent();
        groupModelDetails = (GroupModel) intent.getSerializableExtra(GROUPDETAILSDATA);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(groupModelDetails.getName());
            getSupportActionBar().setSubtitle(String.format("Created By %s%s , %s", groupModelDetails.getEventUser().getfName(), groupModelDetails.getEventUser().getfName(), CalenderUtils.convertTimeDateFormat(groupModelDetails.getCreatedAt())));
        }
        friendListAdapter=new FriendListAdapter(this,groupModelDetails.getEventmember());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        groupDetailsScreenBinding.groupDetailsContent.participantsCounts.setText(String.format(getResources().getString(R.string.paticipants_count), groupModelDetails.getEventmember().size()));
        groupDetailsScreenBinding.groupDetailsContent.participantsList.setLayoutManager(mLayoutManager);
        groupDetailsScreenBinding.groupDetailsContent.participantsList.setAdapter(friendListAdapter);

        groupDetailsScreenBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.editGroup);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.editGroup);
                }
            }
        });


        groupDetailsScreenBinding.fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetailsScreen.this);
                        String[] uploadOptions = {"Camera", "Gallery"};
                        builder.setItems(uploadOptions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        break;
                                    case 1:
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(Intent.createChooser(intent, "Search Image"), PICK_IMAGE_REQUEST);
                                        break;
                                }
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();*/
                    }
                }
        );
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        getMenuInflater().inflate(R.menu.menu_group_details_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

        }

        return super.onOptionsItemSelected(item);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }
}
