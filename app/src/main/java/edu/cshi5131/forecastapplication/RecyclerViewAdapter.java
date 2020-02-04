package edu.cshi5131.forecastapplication;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> mImages;

    public RecyclerViewAdapter(List<String> items) {
        mImages = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.photo_item);
            imageView = view.findViewById(R.id.photo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = mImages.get(position);
        String httpUrl = url.replace("https","http");
        Log.i("RecyclerViewAdapter",url);

        Glide.with(holder.imageView.getContext()).load(url)
                .error(Glide.with(holder.imageView.getContext()).load(httpUrl))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }
}
