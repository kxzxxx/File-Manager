package me.myfilemanager.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;

import me.myfilemanager.Custom.CustomDrawer;
import me.myfilemanager.Fragment.NavigationDrawerFragment;
import me.myfilemanager.NavigationDrawerCallbacks;
import me.myfilemanager.R;
import me.myfilemanager.Utils.UpdateList;


//TODO show file list


public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks, AdapterView.OnItemClickListener {
    private String TAG = MainActivity.class.getSimpleName();

    public String currentFolder;

    private Toolbar ab;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ListView listView;


    protected void onCreate(Bundle savedInstanceState) {
        currentFolder = Environment.getExternalStorageDirectory().getAbsolutePath();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //setup toolbar
        ab = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(ab);
        ab.setOnMenuItemClickListener(onMenuItemClick);


        //setup drawer

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (CustomDrawer) findViewById(R.id.drawer), ab);

        //setup listview

        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
        listView.setTextFilterEnabled(true);

        //

        String homePath =Environment.getExternalStorageDirectory().getAbsolutePath();
        File file=new File(homePath);

        new UpdateList().execute(file.getAbsolutePath());


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


    public void onNavigationDrawerItemSelected(int itemPosition) {/*

        String[] listNames = getResources().getStringArray(R.array.student);

        Log.d(TAG, String.format("Select item : %d", itemPosition + 1));
        StudentInfo student = new StudentInfo();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);


        switch (itemPosition) {

          case 0:

                transaction.replace(R.id.container, Card);
                break;


           default:

                transaction.replace(R.id.container, student, listNames[itemPosition]);//3rd param set TAG

        }

        transaction.commit();
  */
    }
    public void onItemClick(AdapterView<?> parent,
                            View view, int position, long id) {



    }

    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

}
