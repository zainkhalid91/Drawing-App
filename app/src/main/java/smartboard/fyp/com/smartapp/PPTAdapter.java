package smartboard.fyp.com.smartapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PPTAdapter extends ArrayAdapter<File> {


    Context context;
    PPTAdapter.ViewHolder viewHolder;
    ArrayList<File> al_ppt;

    public PPTAdapter(Context context, ArrayList<File> al_ppt) {
        super(context, R.layout.adapter_ppt, al_ppt);
        this.context = context;
        this.al_ppt = al_ppt;

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (al_ppt.size() > 0) {
            return al_ppt.size();
        } else {
            return 1;
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {


        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_ppt, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_name_ppt = view.findViewById(R.id.tv_name_ppt);

            view.setTag(viewHolder);
        } else {
            viewHolder = (PPTAdapter.ViewHolder) view.getTag();

        }

        viewHolder.tv_name_ppt.setText(al_ppt.get(position).getName());
        return view;

    }

    public class ViewHolder {

        TextView tv_name_ppt;


    }

}
