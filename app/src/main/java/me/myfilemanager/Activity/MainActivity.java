package me.myfilemanager.Activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import me.myfilemanager.Adapter.AdapterDetailedList;
import me.myfilemanager.Callback.NavigationDrawerCallbacks;
import me.myfilemanager.Custom.CustomDrawer;
import me.myfilemanager.Fragment.NavigationDrawerFragment;
import me.myfilemanager.R;
import me.myfilemanager.Utils.MoveFile;
import me.myfilemanager.Utils.UpdateList;


//TODO next animation
//TODO save current folder
// TODO: 2015/12/6 file handler

public class MainActivity extends AppCompatActivity /*implements NavigationDrawerCallbacks */{
    public static String currentFolder;
    public static AdapterDetailedList adapter;
    public ActionMode mActionMode;
    public boolean actionMode = false;
    public
    Toolbar ab;
    NavigationDrawerFragment mNavigationDrawerFragment;
    MainActivityHandler uiHandler = new MainActivityHandler(this);
    public String sourceLocation;
    public
    RecyclerView recyclerView;
    public Handler chHandler;
    String TAG = MainActivity.class.getSimpleName();
    int mOldStatusBarColor;
    View mRevealView;
    View mRevealBackgroundView;
    LinkedList<String> pathSet = new LinkedList<>();
    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        CharSequence mTitle;
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
            mTitle=ab.getTitle();
            ab.setTitle(null);
       //    animateAppAndStatusBar(R.color.myPrimaryColor, R.color.myactionModePrimaryColor,1);
        //    getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
            pathSet.clear();
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            //noinspection SimplifiableIfStatement
             /*   if (id == R.id.action_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    MainActivity.this.startActivity(intent);
                    return true
                }*/
            //TODO
            switch (item.getItemId()) {
                case R.id.cutfile:
                    Toast.makeText(getApplicationContext(), "cut file",
                            Toast.LENGTH_SHORT).show();
                    for (int i :
                            adapter.getCheckedItemPositions()) {
                        pathSet.add(adapter.fileDetails.get(i).getName());
                        Log.d(TAG, "pathset " + pathSet.peekLast());
                    }
                    sourceLocation = currentFolder;
                    break;
                case R.id
                        .copyfile:
                    Toast.makeText(getApplicationContext(), "copy file",
                            Toast.LENGTH_SHORT).show();
                    for (int i :
                            adapter.getCheckedItemPositions()) {
                        pathSet.add(adapter.fileDetails.get(i).getName());
                        Log.d(TAG, "pathset " + pathSet.peekLast());
                    }
                    sourceLocation = currentFolder;
                    break;

            }

            supportInvalidateOptionsMenu(); //reflash menu
            MainActivity.this.mActionMode.finish();
            return true;

        }

        // Called when the user exits the action mode
        public void onDestroyActionMode(ActionMode mode) {
            if (adapter.getCheckedItemPositions().size() != 0) {


                adapter.mSelectedItemsIds.clear();

                adapter.toggleAllCheckbox(false);


            }
     //      animateAppAndStatusBar(R.color.myactionModePrimaryColor, R.color.myPrimaryColor,0);
           // getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
            ab.setTitle(mTitle);
            actionMode = false;
            mActionMode = null;
        }
    };

    Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        public boolean onMenuItemClick(MenuItem item) {



            switch (item.getItemId()) {
                case R.id.action_settings: {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    MainActivity.this.startActivity(intent);
                    break;
                }

                case R.id.pastefile: {          //TODO:add a dialog , notify

                    final String[] pathS =
                            pathSet.toArray(new String[pathSet.size()]);
                    Log.d(TAG, "paste " + Integer.toString(pathSet.size()) + "files");

                    new MoveFile(MainActivity.this).execute(pathS);

                    pathSet.clear();
                    adapter.notifyDataSetChanged();

                    break;
                }

                default:
                    return true;
            }

            return true;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        currentFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOldStatusBarColor = getWindow().getStatusBarColor();


        ab = (Toolbar) findViewById(R.id.appbar);
        recyclerView = (RecyclerView) findViewById(R.id.list);
      //  mRevealView = findViewById(R.id.reveal);
      //  mRevealBackgroundView = findViewById(R.id.revealBackground);
        //setup toolbar
        setSupportActionBar(ab);
        ab.setOnMenuItemClickListener(onMenuItemClick);

        //setup drawer

      /*  mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(
                        R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (CustomDrawer) findViewById(R.id
                .drawer
        ), ab);*/


        adapter = new AdapterDetailedList(this, new LinkedList<AdapterDetailedList.FileDetail>());
        //setup recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
     /*   recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.LTGRAY)
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());*/
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        // recyclerView.setAdapter(mAdapter); //   new UpdateList(this).execute(currentFolder);
        //  recyclerView.setTextFilterEnabled(true); //获取主储存路径 //    String homePath =
        // Environment.getExternalStorageDirectory().getAbsolutePath(); //  File file = new File
        // (homePath);

//        this.onNavigationDrawerItemSelected(0);
        new UpdateList(this).execute("/sdcard/");
        Log.d(TAG, "====load====");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onPrepareOptionsMenu(final Menu menu) {
if(pathSet.size()==0)
        {
            menu.removeItem(R.id.pastefile);
        }

        return true;
    }
    public void onBackPressed() {
        //exit action mode
     //   if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();

      /* else if(this.mActionMode != null){
            this.actionMode = false;
            this.mActionMode.finish();
            this.mActionMode = null;
        }*/

        if (currentFolder.isEmpty() || currentFolder.equals("/")) {
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

    private void animateAppAndStatusBar(final int fromColor, final int toColor, final int ifSpread) {
        mRevealBackgroundView.setVisibility(View.VISIBLE);
        Animator animator;
      if(ifSpread==1) {
          animator = ViewAnimationUtils.createCircularReveal(
                  mRevealView,
                  ab.getWidth() / 2,
                  ab.getHeight() / 2, 0,
                  ab.getWidth() / 2); animator.addListener(new AnimatorListenerAdapter() {

            public void onAnimationStart(Animator animation) {
                mRevealView.setBackgroundColor(getResources().getColor(toColor));
                mRevealView.bringToFront();

                mRevealBackgroundView.setBackgroundColor(getResources().getColor(fromColor));
            }
              public void onAnimationEnd(Animator animation ){
                mRevealView.bringToFront();
                  mRevealBackgroundView.setVisibility(View.INVISIBLE);
              }
        });

      }else {
          animator = ViewAnimationUtils.createCircularReveal(
                  mRevealBackgroundView,
                  ab.getWidth() / 2,
                  ab.getHeight() / 2,
                  ab.getWidth() / 2,0);
       animator.addListener(new AnimatorListenerAdapter() {

            public void onAnimationStart(Animator animation) {
                mRevealBackgroundView.setBackgroundColor(getResources().getColor(fromColor));
                mRevealBackgroundView.bringToFront();
                mRevealView.setBackgroundColor(getResources().getColor(toColor));
            }
           public void onAnimationEnd(Animator animation){

               mRevealView.bringToFront();

               mRevealBackgroundView.setVisibility(View.INVISIBLE);

           }
        });

      }



        animator.setStartDelay(200);
        animator.setDuration(125);
        animator.start();
    }

  /*  public void onNavigationDrawerItemSelected(int itemPosition) {


        new UpdateList(this).execute(NavigationDrawerFragment.adapter.getList().get
                (itemPosition).getText());


    }*/

    static class MainActivityHandler extends Handler {
        final WeakReference<MainActivity> mTarget;

        MainActivityHandler(MainActivity target) {
            mTarget = new WeakReference<>(target);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           /* switch (msg.what) {
                case 111: {



                    break;
                }

                case (1):
                    Toast.makeText(mTarget.get().getApplicationContext(), "move file " +
                                    "successful",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:

                    Toast.makeText(mTarget.get().getApplicationContext(), Integer.toString(msg
                                    .arg1),
                            Toast.LENGTH_SHORT).show();
            }*/
        }
    }


}
