package com.example.billit_all;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    ArrayList<AnnouncementModel> announcementModelArrayList;

    public AnnouncementAdapter(ArrayList<AnnouncementModel> announcementModelArrayList) {
        this.announcementModelArrayList = announcementModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnnouncementModel announcementModel = announcementModelArrayList.get(position);

//        holder.announcementTitle.setText(announcementModel.announcementTitle);
//        holder.announcementSubject.setText(announcementModel.announcementSubject);
        holder.announcementTitle.setText(announcementModel.getAnnouncementTitle());
        holder.announcementSubject.setText(announcementModel.getAnnouncementSubject());
    }

    @Override
    public int getItemCount() {
        return announcementModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView announcementTitle, announcementSubject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            announcementTitle = itemView.findViewById(R.id.announcementTitle);
            announcementSubject = itemView.findViewById(R.id.announcementSubject);
        }
    }
}