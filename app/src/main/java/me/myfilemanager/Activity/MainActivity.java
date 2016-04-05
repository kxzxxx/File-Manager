package me.myfilemanager.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import me.myfilemanager.Adapter.AdapterDetailedList;
import me.myfilemanager.Callback.NavigationDrawerCallbacks;
import me.myfilemanager.Custom.CustomDrawer;
import me.myfilemanager.Fragment.NavigationDrawerFragment;
import me.myfilemanager.R;
import me.myfilemanager.Utils.UpdateList;


//TODO next animation
//TODO save current folder
//TODO checkbox animation
// TODO: 2015/12/6 file handler  

public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    public static String currentFolder;
    String TAG = MainActivity.class.getSimpleName();
    public ActionMode mActionMode;
    public boolean actionMode = false;
    public static AdapterDetailedList adapter;
    int mOldStatusBarColor;
    public
    Toolbar ab;
    public static String sourceLocation;

    LinkedList<String> pathSet = new LinkedList<>();
    public
    RecyclerView recyclerView;

    NavigationDrawerFragment mNavigationDrawerFragment;

    static class MainActivityHandler extends Handler {
        final WeakReference<MainActivity> mTarget;

        MainActivityHandler(MainActivity target) {
            mTarget = new WeakReference<>(target);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case (1):
                    Toast.makeText(mTarget.get().getApplicationContext(), "move file " +
                                    "successful",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:

                    Toast.makeText(mTarget.get().getApplicationContext(), Integer.toString(msg
                            .arg1),
                            Toast.LENGTH_SHORT).show();
            }
        }
    }

    MainActivityHandler uiHandler = new MainActivityHandler(this);

    protected void onCreate(Bundle savedInstanceState) {
        currentFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOldStatusBarColor = getWindow().getStatusBarColor();


        ab = (Toolbar) findViewById(R.id.toolbar_actionbar);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        //setup toolbar
        setSupportActionBar(ab);
        ab.setOnMenuItemClickListener(onMenuItemClick);
        Log.d(TAG, "load====");

        //setup drawer

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(
                        R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (CustomDrawer) findViewById(R.id
                .drawer
        ), ab);


        //setup recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.LTGRAY)
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        // recyclerView.setAdapter(mAdapter);

        //   new UpdateList(this).execute(currentFolder);

        //  recyclerView.setTextFilterEnabled(true);

        //获取主储存路径

        //    String homePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //  File file = new File(homePath);


        this.onNavigationDrawerItemSelected(0);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
            }

            if (id == R.id.pastefile) {
                //TODO:add a dialog
                Log.d(TAG, "paste file" + Integer.toString(pathSet.size()));

                if (pathSet.size() != 0) {
                    new Thread(new Runnable() {

                        public void run() {
                            for (String path : pathSet) {
                                File source = new File(sourceLocation + "/" + path);
                                File dest = new File(currentFolder + "/" + path);
                                //check if file is exist
                                if (!dest.exists()) {
                                    if (source.renameTo(dest))

                                    {
                                        Log.d(TAG, "move successful");
                                    } else {
                                        Log.d(TAG, "Move file failed.");
                                    }

                                }
                            }
                            uiHandler.sendEmptyMessage(1);
                        }

                    }).start();
                } else {
                    //open a dialog
                }
                Toast.makeText(getApplicationContext(), "move file successful",
                        Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();

                return true;
            }

            return true;
        }
    };

    public void onBackPressed() {
        //exit action mode
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
      /* else if(this.mActionMode != null){
            this.actionMode = false;
            this.mActionMode.finish();
            this.mActionMode = null;
        }*/

        else if (currentFolder.isEmpty() || currentFolder.equals("/")) {
            Log.d(TAG, "finish");
            finish();
        } else {
            File file = new File(currentFolder);
            String parentFolder = file.getParent();

            new UpdateList(this).execute(parentFolder);
        }


    }

    public void onStop() {
        super.onStop();
        NavigationDrawerFragment.readSharedSetting(this, "currentFolder", currentFolder);
    }

    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items

            getMenuInflater().inflate(R.menu.filehandler, menu);

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            getWindow().setStatusBarColor(getResources().getColor(R.color
                    .myactionModePrimaryDarkColor));
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            //noinspection SimplifiableIfStatement
             /*   if (id == R.id.action_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    MainActivity.this.startActivity(intent);
                    return true
                }*/
            switch (id) {
                case R.id.cutfile:
                    Toast.makeText(getApplicationContext(), "cut file",
                            Toast.LENGTH_SHORT).show();
                    adapter.getCheckedItemPositions();
                    for (int i :
                            adapter.getCheckedItemPositions()) {
                        pathSet.add(adapter.fileDetails.get(i).getName());
                        Log.d(TAG, "pathset" + pathSet.peekLast());
                    }
                    sourceLocation = currentFolder;
                    // TODO: 2016/3/21 file manager  put file path to a set
                    break;
                case R.id
                        .copyfile:
                    Toast.makeText(getApplicationContext(), "copy file",
                            Toast.LENGTH_SHORT).show();
                    // TODO: 2016/3/21 file manager  put file path to a set
                    for (int i :
                            adapter.getCheckedItemPositions()) {
                        pathSet.add(adapter.fileDetails.get(i).getName());
                        Log.d(TAG, "pathset" + pathSet.peekLast());
                    }
                    sourceLocation = currentFolder;
                    break;

            }

            MainActivity.this.mActionMode.finish();
            return true;

        }

        // Called when the user exits the action mode
        public void onDestroyActionMode(ActionMode mode) {
            if (adapter.getCheckedItemPositions().size() != 0) {


                adapter.mSelectedItemsIds.clear();

                adapter.toggleAllCheckbox(false);


            }
            getWindow().setStatusBarColor(mOldStatusBarColor);
            actionMode = false;
            mActionMode = null;
        }
    };

    public void onNavigationDrawerItemSelected(int itemPosition) {


        new UpdateList(this).execute(mNavigationDrawerFragment.adapter.getList().get
                (itemPosition).getText());


    }


}
