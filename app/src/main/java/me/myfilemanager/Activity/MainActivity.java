package me.myfilemanager.Activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.myfilemanager.Custom.CustomDrawer;
import me.myfilemanager.Fragment.NavigationDrawerFragment;
import me.myfilemanager.NavigationDrawerCallbacks;
import me.myfilemanager.R;
import me.myfilemanager.Utils.UpdateList;


//TODO show file list


public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    public static String currentFolder;
    String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.toolbar_actionbar)
    Toolbar ab;

    public
    @InjectView(R.id.list)
    RecyclerView recyclerView;

    NavigationDrawerFragment mNavigationDrawerFragment;


    protected void onCreate(Bundle savedInstanceState) {
        currentFolder = Environment.getExternalStorageDirectory().getAbsolutePath();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        //setup toolbar
        setSupportActionBar(ab);
        ab.setOnMenuItemClickListener(onMenuItemClick);


        //setup drawer

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (CustomDrawer) findViewById(R.id.drawer), ab);

        //setup recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //  recyclerView.setTextFilterEnabled(true);

        //获取主储存路径

        String homePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(homePath);
        Log.d(TAG, "获取主储存路径" + homePath);

    //    new UpdateList(this).execute(file.getAbsolutePath());


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

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
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();

        else if (currentFolder.isEmpty() || currentFolder.equals("/")) {
            Log.d(TAG, "finish");
            finish();
        } else {
            File file = new File(currentFolder);
            String parentFolder = file.getParent();
            Log.d(TAG, parentFolder);
            new UpdateList(this).execute(parentFolder);
        }


    }


    public void onNavigationDrawerItemSelected(int itemPosition) {
        String select = mNavigationDrawerFragment.adapter.getList().get(itemPosition).getText();

        Log.d(TAG, String.format("Select item : %d  "+select, itemPosition + 1));


        new UpdateList(this).execute(select);


    }


}
