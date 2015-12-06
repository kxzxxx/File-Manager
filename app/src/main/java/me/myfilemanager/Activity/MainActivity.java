package me.myfilemanager.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.myfilemanager.Adapter.AdapterDetailedList;
import me.myfilemanager.Custom.CustomDrawer;
import me.myfilemanager.Fragment.NavigationDrawerFragment;
import me.myfilemanager.Callback.NavigationDrawerCallbacks;
import me.myfilemanager.R;
import me.myfilemanager.Utils.UpdateList;


//TODO next animation
//TODO save current folder

public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    public static String currentFolder;
    String TAG = MainActivity.class.getSimpleName();
    public ActionMode mActionMode;
    public boolean actionMode=false;
public static AdapterDetailedList adapter;
    public
   Toolbar ab;

    public
    RecyclerView recyclerView;

    NavigationDrawerFragment mNavigationDrawerFragment;



    protected void onCreate(Bundle savedInstanceState) {
        currentFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ab=(Toolbar) findViewById(R.id.toolbar_actionbar);
        recyclerView=( RecyclerView)findViewById(R.id.list);
        //setup toolbar
        setSupportActionBar(ab);
        ab.setOnMenuItemClickListener(onMenuItemClick);
        Log.d(TAG,"load");

        //setup drawer

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (CustomDrawer) findViewById(R.id.drawer), ab);


        //setup recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(intent);
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

  public void onStop(){
      super.onStop();
      NavigationDrawerFragment.readSharedSetting(this,"currentFolder",currentFolder);
  }

    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items

            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    MainActivity.this.startActivity(intent);
                    return true;
                }

                return true;

        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
          if( adapter.getCheckedItemPositions().size()!=0){
              adapter.mSelectedItemsIds.clear();

              adapter.notifyDataSetChanged();

          }

            actionMode = false;
            mActionMode = null;
        }
    };

    public void onNavigationDrawerItemSelected(int itemPosition) {



        new UpdateList(this).execute(mNavigationDrawerFragment.adapter.getList().get(itemPosition).getText());


    }


}
