package me.myfilemanager.Utils;

import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.widget.Toast;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import me.myfilemanager.Adapter.AdapterDetailedList;
import me.myfilemanager.Activity.MainActivity;

/**
 * Created by xz on 2015/9/22.
 */
public class UpdateList extends AsyncTask<String, Void, LinkedList<AdapterDetailedList.FileDetail>> {

    String exceptionMessage;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mSearchView != null) {
            mSearchView.setIconified(true);
            MenuItemCompat.collapseActionView(mSearchViewMenuItem);
            mSearchView.setQuery("", false);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkedList<AdapterDetailedList.FileDetail> doInBackground(final String... params) {
        try {

            final String path = params[0];
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
                                    && stat.size() <= Build.MAX_FILE_SIZE * FileUtils.ONE_KB) {
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
                                && FileUtils.sizeOf(f) <= Build.MAX_FILE_SIZE * FileUtils.ONE_KB) {
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
            Toast.makeText(SelectFileActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
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