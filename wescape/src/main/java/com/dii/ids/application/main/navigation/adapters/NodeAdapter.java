package com.dii.ids.application.main.navigation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeAdapter extends BaseAdapter implements Filterable {
    private List<Node> nodes;
    private List<Node> nodesFiltered;
    private Context context;

    public NodeAdapter(Context context, List<Node> nodes) {
        this.nodes = nodes;
        this.nodesFiltered = nodes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return nodesFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return nodesFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.navigation_list_item, parent, false);
        }
        ViewHolder holder = new ViewHolder(convertView);

        Node node = (Node) getItem(position);
        holder.textView.setText(node.getName());
        holder.iconView.setImageResource(R.drawable.ic_place);
        return convertView;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    // no constraint given, just return all the data. (no searchDoublePath)
                    results.count = nodes.size();
                    results.values = nodes;
                } else { //do the searchDoublePath
                    List<Node> resultsData = new ArrayList<>();
                    String searchStr = constraint.toString().toUpperCase();
                    for (Node node : nodes)
                        if (node.getName().toUpperCase().contains(searchStr)) resultsData.add(node);
                    results.count = resultsData.size();
                    results.values = resultsData;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                nodesFiltered = (ArrayList<Node>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView textView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            textView = (TextView) view.findViewById(R.id.list_item_text);
        }
    }
}
