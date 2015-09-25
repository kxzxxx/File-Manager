package me.myfilemanager.Activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import com.spazedog.lib.rootfw4.RootFW;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import me.myfilemanager.Custom.CustomDrawer;
import me.myfilemanager.Fragment.NavigationDrawerFragment;
import me.myfilemanager.NavigationDrawerCallbacks;
import me.myfilemanager.R;
import me.myfilemanager.Adapter.AdapterDetailedList;
import me.myfilemanager.Utils.AlphanumComparator;


//TODO show file list


public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks, AdapterView.OnItemClickListener {
    private String TAG = MainActivity.class.getSimpleName();

    public static String currentFolder;

    private Toolbar ab;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ListView listView;

    private Filter filter;


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

        //获取主储存路径

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
    private class UpdateList extends AsyncTask<String, Void, LinkedList<AdapterDetailedList.FileDetail>> {
//异步加载文件夹目录
        String exceptionMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected LinkedList<AdapterDetailedList.FileDetail> doInBackground(final String... params) {
            try {

                final String path = params[0];  //params[0]为传给UpdateList的第一个参数
                if (TextUtils.isEmpty(path)) {
                    return null;
                }

                File tempFolder = new File(path);
                if (tempFolder.isFile()) {
                    tempFolder = tempFolder.getParentFile();
                }

                String[] unopenableExtensions = {"apk", "mp3", "mp4", "png", "jpg", "jpeg"};

                final LinkedList<AdapterDetailedList.FileDetail> fileDetails = new LinkedList<>();
                final LinkedList<AdapterDetailedList.FileDetail> folderDetails = new LinkedList<>();
                currentFolder = tempFolder.getAbsolutePath();

                if (!tempFolder.canRead()) {
                    if (RootFW.connect()) {
                        com.spazedog.lib.rootfw4.utils.File folder = RootFW.getFile(currentFolder);
                        com.spazedog.lib.rootfw4.utils.File.FileStat[] stats = folder.getDetailedList();

                        if (stats != null) {
                            for (com.spazedog.lib.rootfw4.utils.File.FileStat stat : stats) {
                                if (stat.type().equals("d")) {
                                    folderDetails.add(new AdapterDetailedList.FileDetail(stat.name(),
                                            getString(R.string.folder),
                                            ""));
                                } else if (!FilenameUtils.isExtension(stat.name().toLowerCase(), unopenableExtensions)
                                        && stat.size() <= 20_000 * FileUtils.ONE_KB) {
                                    final long fileSize = stat.size();
                                    //SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy  hh:mm a");
                                    //String date = format.format("");
                                    fileDetails.add(new AdapterDetailedList.FileDetail(stat.name(),
                                            FileUtils.byteCountToDisplaySize(fileSize), ""));
                                }
                            }
                        }
                    }
                } else {
                    File[] files = tempFolder.listFiles();

                    Arrays.sort(files, getFileNameComparator());

                    if (files != null) {
                        for (final File f : files) {
                            if (f.isDirectory()) {
                                folderDetails.add(new AdapterDetailedList.FileDetail(f.getName(),
                                        getString(R.string.folder),
                                        ""));
                            } else if (f.isFile()
                                    && !FilenameUtils.isExtension(f.getName().toLowerCase(), unopenableExtensions)
                                    && FileUtils.sizeOf(f) <= 20_000 * FileUtils.ONE_KB) {
                                final long fileSize = f.length();
                                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy  hh:mm a");
                                String date = format.format(f.lastModified());
                                fileDetails.add(new AdapterDetailedList.FileDetail(f.getName(),
                                        FileUtils.byteCountToDisplaySize(fileSize), date));
                            }
                        }
                    }
                }

                folderDetails.addAll(fileDetails);
                return folderDetails;
            } catch (Exception e) {
                exceptionMessage = e.getMessage();
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(final LinkedList<AdapterDetailedList.FileDetail> names) {
            if (names != null) {
                boolean isRoot = currentFolder.equals("/");
                AdapterDetailedList mAdapter = new AdapterDetailedList(getBaseContext(), names, isRoot);
                listView.setAdapter(mAdapter);
                filter = mAdapter.getFilter();
            }
            if (exceptionMessage != null) {
                Toast.makeText(MainActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
            }
            invalidateOptionsMenu();
            super.onPostExecute(names);
        }

        public final Comparator<File> getFileNameComparator() {
            return new AlphanumComparator() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public String getTheString(Object obj) {
                    return ((File) obj).getName()
                            .toLowerCase();
                }
            };
        }
    }

}
