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
        /*第一个参数是groupId，如果不需要可以设置为Menu.NONE。将若干个menu item都设置在同一个Group中，可以使用setGroupVisible()，setGroupEnabled()，setGroupCheckable()这样的方法，而不需要对每个item都进行setVisible(), setEnable(), setCheckable()这样的处理，这样对我们进行统一的管理比较方便
       * 第二个参数就是item的ID，我们可以通过menu.findItem(id)来获取具体的item
       * 第三个参数是item的顺序，一般可采用Menu.NONE，具体看本文最后MenuInflater的部分
       * 第四个参数是显示的内容，可以是String，或者是引用Strings.xml的ID*/
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //TODO:接收所有intent的activity

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify activity_self_edit parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) { //如果选择了setting 就开始新的setting

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class DropDownListenser implements ActionBar.OnNavigationListener
    {
        // 得到和SpinnerAdapter里一致的字符数组
        String[] listNames = getResources().getStringArray(R.array.student);

        // 当选择下拉菜单项的时候，将Activity中的内容置换为对应的Fragment
        public boolean onNavigationItemSelected(int itemPosition, long itemId)
        {
            Log.d(TAG, String.format("Select item : %d",itemPosition+1));

            // 生成自定的Fragment
            StudentInfo student = new StudentInfo();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setTransition(4099); //设置转换动画

            CardViewFragment Card = new  CardViewFragment();

            switch (itemPosition) {

                case 0:

                    transaction.replace(R.id.container, Card);break;

                default :// 将Activity中的内容替换成对应选择的Fragment

                    transaction.replace(R.id.container, student, listNames[itemPosition]);//第三个参数 设置Tag

            }



            transaction.commit();
            return true;
        }
    }
}
