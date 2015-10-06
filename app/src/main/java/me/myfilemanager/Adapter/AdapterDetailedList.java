package me.myfilemanager.Adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.LinkedList;

import me.myfilemanager.Activity.MainActivity;
import me.myfilemanager.R;
import me.myfilemanager.Utils.UpdateList;

/**
 * Created by xz on 2015/9/22.
 */
public class AdapterDetailedList extends RecyclerView.Adapter<AdapterDetailedList.ViewHolder> {
    //layout inflater
    //  final LayoutInflater inflater;
    final LinkedList<FileDetail> orig;

    Context context;
    //list of file details
    LinkedList<FileDetail> fileDetails;
    MainActivity mainActivity;

    public AdapterDetailedList(final MainActivity mainActivity,
                               final LinkedList<FileDetail> fileDetails,
                               final boolean isRoot) {
        this.fileDetails = fileDetails;
        this.mainActivity=mainActivity;
        this.orig = fileDetails;
        this.context = mainActivity;
        //    this.inflater = LayoutInflater.from(context);
        if (!isRoot) {
            this.fileDetails.addFirst(new FileDetail("..", context.getString(R.string.folder), ""));
        } else {
            this.fileDetails.addFirst(new FileDetail(context.getString(R.string.home), context.getString(R.string.folder), ""));
        }
    }


    public void onBindViewHolder(AdapterDetailedList.ViewHolder viewHolder, final int i) {

        setIcon(viewHolder, fileDetails.get(i));
        viewHolder.nameLabel.setText(fileDetails.get(i).getName());
        //  viewHolder.sizeLabel.setText(fileDetail.getSize() + "\t\t" + fileDetail.getDateModified());

    }


    public int getItemCount() {
        return fileDetails != null ? fileDetails.size() : 0;
    }


    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_file_list, viewGroup, false);
        return new ViewHolder(v, mainActivity);
    }

    void setIcon(final ViewHolder viewHolder, final FileDetail fileDetail) {

        final String fileName = fileDetail.getName();
        final String ext = FilenameUtils.getExtension(fileName);
        if (fileDetail.isFolder()) {
            viewHolder.icon.setImageResource(R.color.file_folder);
        } else {
            viewHolder.icon.setImageResource(R.color.file_text);
        }

    }


    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        // Name of the file
        public TextView nameLabel;

        // Size of the file
        public TextView sizeLabel;

        public TextView dataLabel;

        // Icon of the file
        public ImageView icon;
        MainActivity mainActivity;

//TODO 解决问题
        public ViewHolder(View itemView,final MainActivity mainActivity) {
            super(itemView);
            nameLabel = (TextView) itemView.findViewById(R.id.text1);
            sizeLabel = (TextView) itemView.findViewById(R.id.text2);
            dataLabel = (TextView) itemView.findViewById(R.id.text3);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            this.mainActivity=mainActivity;
            itemView.setOnClickListener(this );
        }
        
        
        
        public void onClick(View v) {

            String name = nameLabel.getText().toString();

            Log.d("s", "onClick" + getAdapterPosition() + name + mainActivity.currentFolder);

            if (name.equals("..")) {

                if (mainActivity.currentFolder.equals("/")) {
                    new UpdateList(mainActivity).execute(Environment.getExternalStorageDirectory().getAbsolutePath());
                } else {
                    File tempFile = new File(mainActivity.currentFolder);
                    if (tempFile.isFile()) {
                        tempFile = tempFile.getParentFile()
                                .getParentFile();
                    } else {
                        tempFile = tempFile.getParentFile();
                    }
                    new UpdateList(mainActivity).execute(tempFile.getAbsolutePath());
                }
                return;
            } else if
                    (name.equals(mainActivity.getString(R.string.home))) {
                new UpdateList(mainActivity).execute(Environment.getExternalStorageDirectory().getAbsolutePath());
                return;
            }

            final File selectedFile = new File(mainActivity.currentFolder, name);

            if (selectedFile.isDirectory()) {
                new UpdateList(mainActivity).execute(selectedFile.getAbsolutePath());
                Log.d("f", "onClick" + getAdapterPosition() + mainActivity.currentFolder);
            }
        }

    }

    public static class FileDetail {
        private final String name;
        private final String size;
        private final String dateModified;
        private final boolean isFolder;

        public FileDetail(String name, String size,
                          String dateModified) {
            this.name = name;
            this.size = size;
            this.dateModified = dateModified;
            isFolder = TextUtils.isEmpty(dateModified);
        }

        public String getDateModified() {
            return dateModified;
        }

        public String getSize() {
            return size;
        }

        public String getName() {
            return name;
        }

        public boolean isFolder() {
            return isFolder;
        }
    }
}
