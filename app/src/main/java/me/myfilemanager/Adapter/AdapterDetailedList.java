package me.myfilemanager.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.util.LinkedList;

import me.myfilemanager.R;

/**
 * Created by xz on 2015/9/22.
 */
public class AdapterDetailedList extends
        ArrayAdapter<AdapterDetailedList.FileDetail> {

    //layout inflater
    final LayoutInflater inflater;
    final LinkedList<FileDetail> orig;
    CustomFilter customFilter;

    //list of file details
    LinkedList<FileDetail> fileDetails;

    public AdapterDetailedList(final Context context,
                               final LinkedList<FileDetail> fileDetails,
                               final boolean isRoot) {
        super(context, R.layout.item_file_list, fileDetails);
        this.fileDetails = fileDetails;
        this.orig = fileDetails;
        this.inflater = LayoutInflater.from(context);
        if (!isRoot) {
            this.fileDetails.addFirst(new FileDetail("..", context.getString(R.string.folder), ""));
        } else {
            this.fileDetails.addFirst(new FileDetail(context.getString(R.string.home), context.getString(R.string.folder), ""));
        }
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.item_file_list, null);
            final ViewHolder hold = new ViewHolder();
            hold.nameLabel = (TextView) convertView.findViewById(android.R.id.text1);
            hold.detailLabel = (TextView) convertView.findViewById(android.R.id.text2);
            hold.icon = (ImageView) convertView.findViewById(android.R.id.icon);
            convertView.setTag(hold);
            final FileDetail fileDetail = fileDetails.get(position);
            final String fileName = fileDetail.getName();
            setIcon(hold, fileDetail);
            hold.nameLabel.setText(fileName);
            hold.detailLabel.setText(fileDetail.getSize() + "\t\t" + fileDetail.getDateModified());

        } else {
            final ViewHolder hold = (ViewHolder) convertView.getTag();
            final FileDetail fileDetail = fileDetails.get(position);
            final String fileName = fileDetail.getName();
            setIcon(hold, fileDetail);
            hold.nameLabel.setText(fileName);
            hold.detailLabel.setText(fileDetail.getSize() + "\t\t" + fileDetail.getDateModified());

        }
        return convertView;
    }

    public int getCount() {
        return fileDetails.size();
    }

    void setIcon(final ViewHolder viewHolder, final FileDetail fileDetail) {

        final String fileName = fileDetail.getName();
        final String ext = FilenameUtils.getExtension(fileName);
        if (fileDetail.isFolder()) {
            viewHolder.icon.setImageResource(R.color.file_folder);
        }
        else {
            viewHolder.icon.setImageResource(R.color.file_text);
        }

    }

    public Filter getFilter(){
        if(customFilter==null){
            customFilter= new CustomFilter();
        }
        return customFilter;
    }

    public static class ViewHolder {

        // Name of the file
        public TextView nameLabel;

        // Size of the file
        public TextView detailLabel;

        // Icon of the file
        public ImageView icon;
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

    private class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = orig;
                results.count = orig.size();
            } else {
                LinkedList<FileDetail> nHolderList = new LinkedList<>();
                for (FileDetail h : orig) {
                    if (h.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        nHolderList.add(h);
                }
                results.values = nHolderList;
                results.count = nHolderList.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {


            fileDetails = (LinkedList<FileDetail>) results.values;
            notifyDataSetChanged();
        }
    }
}
