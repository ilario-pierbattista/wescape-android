package com.dii.ids.application.main.navigation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dii.ids.application.R;

public class StaticListAdapter extends BaseAdapter {

    private String[] texts;
    private int[] images;
    private Context context;

    public StaticListAdapter(Context context, String[] texts, int[] images) {
        this.texts = texts;
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return texts.length;
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
        holder.actionText.setText(texts[position]);
        holder.iconView.setImageResource(images[position]);

        return view;
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView actionText;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            actionText = (TextView) view.findViewById(R.id.list_item_text);
        }
    }
}
