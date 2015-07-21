package sasa.myapplication.Activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import sasa.myapplication.Custom.CustomDrawer;
import sasa.myapplication.Fragment.CardViewFragment;
import sasa.myapplication.Fragment.NavigationDrawerFragment;
import sasa.myapplication.NavigationDrawerCallbacks;
import sasa.myapplication.R;
import sasa.myapplication.StudentInfo;


public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    private String TAG = MainActivity.class.getSimpleName();

    private Toolbar ab;

    private NavigationDrawerFragment mNavigationDrawerFragment;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //setup toolbar
        ab = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(ab);
        ab.setOnMenuItemClickListener(onMenuItemClick);


        //setup drawer

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (CustomDrawer) findViewById(R.id.drawer), ab);


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


    public void onNavigationDrawerItemSelected(int itemPosition) {

        String[] listNames = getResources().getStringArray(R.array.student);

        Log.d(TAG, String.format("Select item : %d", itemPosition + 1));
        StudentInfo student = new StudentInfo();
        CardViewFragment Card = new CardViewFragment();
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
    }

    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

}
