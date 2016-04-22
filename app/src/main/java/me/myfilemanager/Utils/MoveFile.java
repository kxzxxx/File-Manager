package me.myfilemanager.Utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;

import me.myfilemanager.Activity.MainActivity;
import me.myfilemanager.R;

public class MoveFile extends AsyncTask<String[], Void, LinkedList<String>> {
    MainActivity mainA;
    String sourceLocation;
    String currentFolder;
    LinkedList<String> overPath=new LinkedList<>();

    public MoveFile(MainActivity mainActivity) {
        this.mainA = mainActivity;
        this.sourceLocation = mainA.sourceLocation;


    }

    protected LinkedList<String> doInBackground(String[]... param) {

        LinkedList<String> pathSet =new LinkedList<>( Arrays.asList(param[0]));

        if (pathSet.size() != 0) {


            for (String path : pathSet) {
                File source = new File(sourceLocation + "/" + path);
                File dest = new File(MainActivity.currentFolder + "/" + path);
                //check if file is exist
                if (!dest.exists()) {
                    if (source.renameTo(dest))

                    {
                        Log.d("Move File AsyncTask", "Successful move to "+MainActivity
                                .currentFolder+"/"+path );
                    } else {
                        Log.d("Move File AsyncTask", "Move file failed.");
                    }

                } else {
                    //if file exists , then add to a new ;

                    overPath.add(dest.getName());
                    //open a dialog


                }
            }


            //open a dialog
        }
        return overPath;
    }

    protected void onPostExecute(LinkedList<String> overPath) {
        super.onPostExecute(overPath);
        if (overPath.size() != 0) showDialog();
        Toast.makeText(mainA.getApplicationContext(), "move file successful",
                Toast.LENGTH_SHORT).show();

        new UpdateList(mainA).execute(currentFolder);
    }

    public void showDialog() {
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder
                (mainA).onAny(new MaterialDialog
                .SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction
                    which) {
                if (which == DialogAction.POSITIVE) {


                }
            }
        })
                .title(R.string.cut_file_dialog_title)
                .content(R.string.cut_file_dialog_content)
                .positiveText(R.string.cut_file_dialog_agree)
                .negativeText(R.string.cut_file_dialog_disagree);

        MaterialDialog dialog = dialogBuilder.build();
        dialog.show();
    }
}