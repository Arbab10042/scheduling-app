package com.example.mockup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_main_recyclerview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Node node = nodeList.get(position);

        holder.title.setText(node.getTitle());
        holder.time.setText(node.getTimeFormat());
        holder.status.setText(node.getStatusFormat());
        holder.episode_count.setText(node.getEpisodesCountFormat());
        if(node.getBookmarked() == 1){
            holder.bookmark_icon.setVisibility(View.VISIBLE);
        }else{
            holder.bookmark_icon.setVisibility(View.GONE);
        }

        holder.edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.context,EditActivity.class);
                intent.putExtra("node", node);
                ((Activity) holder.context).startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nodeList.size();
    }

    private ArrayList<Node> nodeList;

    public RecyclerAdapter(ArrayList<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public void updateNodeList(ArrayList<Node> nodeList){
        this.nodeList = nodeList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, time, episode_count, status;
        private ImageView edit_icon, bookmark_icon;
        private final Context context;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.home_fragment_recyclerview_title);
            time = (TextView) itemView.findViewById(R.id.home_fragment_recyclerview_time);
            episode_count = (TextView) itemView.findViewById(R.id.home_fragment_recyclerview_episode_count);
            status = (TextView) itemView.findViewById(R.id.home_fragment_recyclerview_status);
            edit_icon = (ImageView) itemView.findViewById(R.id.home_fragment_recyclerview_icon_edit);
            bookmark_icon = (ImageView) itemView.findViewById(R.id.home_fragment_recyclerview_icon_bookmark);
        }
    }
}
