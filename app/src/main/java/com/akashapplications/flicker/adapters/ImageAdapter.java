package com.akashapplications.flicker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akashapplications.flicker.InfoPopup;
import com.akashapplications.flicker.R;
import com.akashapplications.flicker.models.ImageModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ImageModel> imageModels;

    public ImageAdapter(Context context, ArrayList<ImageModel> imageModels) {
        this.context = context;
        this.imageModels = imageModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        Glide.with(context)
                .load(imageModels.get(i).getMedia())
                .apply(new RequestOptions().placeholder(R.drawable.picture).error(R.drawable.picture))
                .into(holder.image);

        holder.title.setText(imageModels.get(i).getTitle());
        holder.uploaded.setText(imageModels.get(i).getUploadedDate());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoPopup popup = new InfoPopup(view.getContext(),imageModels.get(i).getTitle(),imageModels.get(i).getDescription(),imageModels.get(i).getTags());
                popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popup.showOnAnchor(view,RelativePopupWindow.VerticalPosition.CENTER, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT,false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title,uploaded;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            uploaded = itemView.findViewById(R.id.uploadedTime);

        }
    }
}
