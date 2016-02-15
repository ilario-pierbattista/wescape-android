package com.dii.ids.application.main.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dii.ids.application.R;

public class CustomAdapter extends BaseAdapter {

    private String[] textstrings;
    private int[] images;
    private Context context;

    public CustomAdapter(Context context, String[] textStrings, int[] images) {
        this.textstrings = textStrings;
        this.images = images;
        this.context = context;
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView actionText;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            actionText = (TextView) view.findViewById(R.id.list_item_text);
        }
    }

    @Override
    public int getCount() {
        return textstrings.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.navigation_list_item, parent, false);

        ViewHolder holder = new ViewHolder(view);
        holder.actionText.setText(textstrings[position]);
        holder.iconView.setImageResource(images[position]);

        return view;
    }
}
