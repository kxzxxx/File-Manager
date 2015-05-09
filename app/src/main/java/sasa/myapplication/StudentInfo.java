package sasa.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by vV on 2015/4/29.
 */
public class StudentInfo extends Fragment
{
    // Fragment对应的标签，当Fragment依附于Activity时得到
    private String tag;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        tag = getTag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        TextView textView = new TextView(getActivity());
     //   TextView textView =
        textView.setText(tag);
        return textView;
    }
}
