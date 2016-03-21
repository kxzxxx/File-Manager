package me.myfilemanager.Adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
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
  static  MainActivity mainActivity;
   public static SparseBooleanArray mSelectedItemsIds;
    public boolean stoppedAnimation = false;
    Animation localAnimation;
    int anim;
    int offset = 100;
    // Allows to remember the last item shown on screen
    int lastPosition = -1;

    public AdapterDetailedList(final MainActivity mainActivity,
                               final LinkedList<FileDetail> fileDetails,
                               final boolean isRoot) {
        this.fileDetails = fileDetails;
        this.mainActivity = mainActivity;
        this.orig = fileDetails;
        this.context = mainActivity;
        mSelectedItemsIds = new SparseBooleanArray(); //save checkbox status

        if (!isRoot) {
            this.fileDetails.addFirst(new FileDetail("..", context.getString(R.string.parent_dir), ""));
        } else {
            this.fileDetails.addFirst(new FileDetail(context.getString(R.string.home), context.getString(R.string.folder), ""));
        }


        anim = /*main.IS_LIST?R.anim.fade_in_top:*/R.anim.fade;
    }

    @Override
    public void onViewDetachedFromWindow(AdapterDetailedList.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public boolean onFailedToRecycleView(AdapterDetailedList.ViewHolder holder) {
        holder.itemView.clearAnimation();
        return super.onFailedToRecycleView(holder);
    }

    void animate(AdapterDetailedList.ViewHolder holder, int position) {
        if (position > lastPosition) {
            holder.itemView.clearAnimation();
            localAnimation = AnimationUtils.loadAnimation(context, anim);
            localAnimation.setStartOffset(this.offset);
            holder.itemView.startAnimation(localAnimation);
            lastPosition = position;

            // this.offset+=30;
        }

    }

    public void onBindViewHolder(final AdapterDetailedList.ViewHolder viewHolder, final int i) {
        setIcon(viewHolder, fileDetails.get(i));
        viewHolder.checkBox.setVisibility(View.VISIBLE);
        Log.d("Get file name", fileDetails.get(i).getName());
        //setup checkbox
        if (i==0)
            viewHolder.checkBox.setVisibility(View.INVISIBLE);
        viewHolder.checkBox.setChecked(false);

        if (mSelectedItemsIds.get(i, false)) viewHolder.checkBox.setChecked(true);


        //setup row
        viewHolder.nameLabel.setText(fileDetails.get(i).getName());
        viewHolder.sizeLabel.setText(fileDetails.get(i).getSize());
        viewHolder.dataLabel.setText(fileDetails.get(i).getDateModified());

        //setup checkboxlistener
        if (!fileDetails.get(i).getName().equals("..")) {
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    toggleChecked(i); //go actionmode
                }
            });
            if(i!=0){
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {

                    if (!mSelectedItemsIds.get(i, false)) viewHolder.checkBox.setChecked(true);
                    else viewHolder.checkBox.setChecked(false);
                    toggleChecked(i);
                    return true;
                }


            });}

        }
        //  if (!this.stoppedAnimation)   animate(viewHolder,i);


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                //load file list
                public void onClick(View v) {
                   if(!mainActivity.actionMode) {
                        String name = fileDetails.get(i).getName();
                        if (i==0) {

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

                        }
                        else if
                                (name.equals(mainActivity.getString(R.string.home))) {
                            new UpdateList(mainActivity).execute(Environment.getExternalStorageDirectory().getAbsolutePath());
                            return;
                        }

                        final File selectedFile = new File(mainActivity.currentFolder, name);

                        if (selectedFile.isDirectory()) {
                            new UpdateList(mainActivity).execute(selectedFile.getAbsolutePath());
                        }
                    }else   if(i!=0){

                       if (!mSelectedItemsIds.get(i, false)) viewHolder.checkBox.setChecked(true);
                       else viewHolder.checkBox.setChecked(false);
                       toggleChecked(i);
                   }
                }

            });


    }


    public int getItemCount() {
        return fileDetails != null ? fileDetails.size() : 0;
    }


    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_file_list, viewGroup, false);
        return new ViewHolder(v);
    }

    void setIcon(final ViewHolder viewHolder, final FileDetail fileDetail) {

      //  final String fileName = fileDetail.getName();
    //    final String ext = FilenameUtils.getExtension(fileName);
        if (fileDetail.isFolder()) {
            viewHolder.icon.setImageResource(R.drawable.folder);
        } else {
            viewHolder.icon.setImageResource(R.drawable.file);
        }

    }

    public void toggleChecked(final int postion) {

        if (mSelectedItemsIds.get(postion, false)) mSelectedItemsIds.put(postion, false);
        else mSelectedItemsIds.put(postion, true);

if(postion!=0){
        //start actionmode
        if ((!mainActivity.actionMode || mainActivity.mActionMode == null)) {
            mainActivity.actionMode = true;
            mainActivity.mActionMode = mainActivity.ab.startActionMode(mainActivity.mActionModeCallback);
        }}
        // mainActivity.mActionMode.invalidate();

        if (getCheckedItemPositions().size() == 0) {
            mainActivity.actionMode = false;
            mainActivity.mActionMode.finish();
            mainActivity.mActionMode = null;
        }
    }

    //some problem
    public SparseBooleanArray getCheckedItemPositions() {
        SparseBooleanArray checkedItemPositions = new SparseBooleanArray(0);

        for (int i = 0; i < mSelectedItemsIds.size(); i++) {

            if (mSelectedItemsIds.valueAt(i)) {
                checkedItemPositions.put(mSelectedItemsIds.keyAt(i), mSelectedItemsIds.valueAt(i));
            }
        }

        return checkedItemPositions;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {


        // Name of the file
        public TextView nameLabel;

        // Size of the file
        public TextView sizeLabel;

        public TextView dataLabel;

        // Icon of the file
        public ImageView icon;

        public CheckBox checkBox;


        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            nameLabel = (TextView) itemView.findViewById(R.id.text1);
            sizeLabel = (TextView) itemView.findViewById(R.id.text2);
            dataLabel = (TextView) itemView.findViewById(R.id.text3);
            icon = (ImageView) itemView.findViewById(R.id.icon);
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
