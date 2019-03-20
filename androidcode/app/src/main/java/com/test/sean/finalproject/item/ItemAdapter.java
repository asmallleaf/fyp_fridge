package com.test.sean.finalproject.item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.sean.finalproject.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
    private Context context;
    private List<Item> items;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView text_name;
        TextView text_num;
        ImageView imageView;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            text_name = (TextView) view.findViewById(R.id.txt_name);
            text_num = (TextView) view.findViewById(R.id.txt_num);
            imageView = (ImageView) view.findViewById(R.id.rect);
        }
    }

    public ItemAdapter(List<Item> pitems){
        this.items = pitems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_card,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Item item = items.get(i);
        viewHolder.text_name.setText(item.getItem_name());
        switch (item.getItem_flag()){
            case num:
                viewHolder.text_num.setText(item.getItem_num());
                break;
            case tab:
                viewHolder.text_num.setText(item.getItem_tab());
                break;
            case time:
                viewHolder.text_num.setText(item.getItem_time());
                break;
        }
        if(i%2!=0){
            Glide.with(context).load(R.mipmap.list_icon_background1).into(viewHolder.imageView);
        }
        else
            Glide.with(context).load(R.mipmap.list_icon_background).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
