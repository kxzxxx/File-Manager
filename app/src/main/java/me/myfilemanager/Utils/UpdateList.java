package me.myfilemanager.Utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import me.myfilemanager.Activity.MainActivity;
import me.myfilemanager.Adapter.AdapterDetailedList;
import me.myfilemanager.R;

/**
 * Created by vV on 2015/9/30.
 */
public class UpdateList extends AsyncTask<String, Void, LinkedList<AdapterDetailedList
        .FileDetail>> {

    String exceptionMessage;
    MainActivity activity;


    public UpdateList(MainActivity activity) {
        super();
        this.activity = activity;


    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkedList<AdapterDetailedList.FileDetail> doInBackground(final String... params) {
        try {

            final String path = params[0];  //params[0]为传给UpdateList的第一个参数 此处为文件夹路径

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
            MainActivity.currentFolder = tempFolder.getAbsolutePath();

            if (!tempFolder.canRead()) {
                this.cancel(true);

                // pop up a dialog



                /*if (RootFW.connect()) {
                    com.spazedog.lib.rootfw4.utils.File folder = RootFW.getFile(activity
                    .currentFolder);
                    com.spazedog.lib.rootfw4.utils.File.FileStat[] stats = folder.getDetailedList();

                    if (stats != null) {
                        for (com.spazedog.lib.rootfw4.utils.File.FileStat stat : stats) {
                            *//**
                 * @return
                 *     The file type ('d'=>Directory, 'f'=>File, 'b'=>Block Device,
                 *     'c'=>Character Device, 'l'=>Symbolic Link)
                 *//*
                            if (stat.type().equals("d")) {
                                folderDetails.add(new AdapterDetailedList.FileDetail(stat.name(),
                                        activity.getString(R.string.folder),
                                        ""));
                            } else if (!FilenameUtils.isExtension(stat.name().toLowerCase(),
                            unopenableExtensions)
                                    && stat.size() <= 20_000 * FileUtils.ONE_KB) {// Java 7
                                    新增数值中使用下划线分割
                                final long fileSize = stat.size();
                                //SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy
                                hh:mm a");
                                //String date = format.format("");
                                fileDetails.add(new AdapterDetailedList.FileDetail(stat.name(),
                                        FileUtils.byteCountToDisplaySize(fileSize), ""));
                            }
                        }
                    }
                }*/
            } else {
                File[] files = tempFolder.listFiles();// load file list

                Arrays.sort(files, getFileNameComparator());


                for (final File f : files) {
                    if (f.isDirectory()) {
                        folderDetails.add(new AdapterDetailedList.FileDetail(f.getName(),
                                activity.getString(R.string.folder),
                                ""));
                    } else if (f.isFile()
                            && !FilenameUtils.isExtension(f.getName().toLowerCase(),
                            unopenableExtensions)
                            && FileUtils.sizeOf(f) <= 20_000 * FileUtils.ONE_KB) {
                        final long fileSize = f.length();
                        SimpleDateFormat format = new SimpleDateFormat();
                        String date = format.format(f.lastModified());
                        fileDetails.add(new AdapterDetailedList.FileDetail(f.getName(),
                                FileUtils.byteCountToDisplaySize(fileSize), date));
                    }
                }

            }

            folderDetails.addAll(fileDetails);

  MainActivity.adapter.fileDetails.clear();
            MainActivity.adapter.fileDetails.addAll(folderDetails);

            if (!MainActivity.currentFolder.equals("/")) {
                MainActivity.adapter.fileDetails.addFirst(new AdapterDetailedList.FileDetail
                        ("..", activity
                        .getString(R
                                .string
                                .parent_dir)
                        , ""));
            } else {
                MainActivity.adapter.fileDetails.addFirst(new AdapterDetailedList
                        .FileDetail(activity.getString(R.string.home), activity
                        .getString(R.string.folder), ""));
            }
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
        super.onPostExecute(names);
        if (names != null) {
            MainActivity.adapter.notifyDataSetChanged();
        }
        if (exceptionMessage != null) {
            Toast.makeText(activity, exceptionMessage, Toast.LENGTH_SHORT).show();
        }
        activity.invalidateOptionsMenu();
    }

    public final Comparator<File> getFileNameComparator() {
        return new AlphanumComparator();
    }
}