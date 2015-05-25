package sasa.myapplication;


import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;


import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;



public class MainActivity extends AppCompatActivity {
    private String TAG= MainActivity.class.getSimpleName();

    private Toolbar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       SpinnerAdapter adapter = ArrayAdapter.createFromResource(this,R.array.student,android.R.layout.simple_list_item_1);


        ab=(Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(ab);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(adapter, new DropDownListenser());


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify activity_self_edit parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class DropDownListenser implements ActionBar.OnNavigationListener
    {

        String[] listNames = getResources().getStringArray(R.array.student);


        public boolean onNavigationItemSelected(int itemPosition, long itemId)
        {
            Log.d(TAG, String.format("Select item : %d",itemPosition+1));


            StudentInfo student = new StudentInfo();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setTransition(transaction.TRANSIT_FRAGMENT_OPEN); //

            CardViewFragment Card = new  CardViewFragment();
            BlankFragment Socket = new  BlankFragment();

            switch (itemPosition) {

                case 0:

                    transaction.replace(R.id.container, Card);break;
                case 2:

                    transaction.replace(R.id.container, Socket);break;

                default :

                    transaction.replace(R.id.container, student, listNames[itemPosition]);//第三个参数设置TAG

            }



            transaction.commit();
            return true;
        }
    }
}
