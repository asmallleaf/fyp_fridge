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

//it is a adapter of class Item. It is used to load the item into recycle view
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
    // it need the context of this activity
    private Context context;
    private List<Item> items;

    // it is used to create a ViewHolder class to load the layout of item
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView text_name;
        TextView text_num;
        ImageView imageView;

        public ViewHolder(View view){
            super(view);
            // find components from the layout
            cardView = (CardView)view;
            text_name = (TextView) view.findViewById(R.id.txt_name);
            text_num = (TextView) view.findViewById(R.id.txt_num);
            imageView = (ImageView) view.findViewById(R.id.rect);
        }
    }

    // accept the list of class item
    public ItemAdapter(List<Item> pitems){
        this.items = pitems;
    }

    // it will load the item layout as designed at first
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_card,viewGroup,false);
        return new ViewHolder(view);
    }

    // the component in view holder can be set in this function
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // get the item index
        Item item = items.get(i);
        // set item name into name Text view
        viewHolder.text_name.setText(item.getItem_name());
        // set one of the three item information and fill into Text view
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
        // it will generate a sidebar, a picture, on the left of item layout
        // there are totally two colors and will be  changed one by one
        // Glide is used to load the picture in a quicker way
        if(i%2!=0){
            Glide.with(context).load(R.mipmap.list_icon_background1).into(viewHolder.imageView);
        }
        else
            Glide.with(context).load(R.mipmap.list_icon_background).into(viewHolder.imageView);
    }

    // it is used to get the whole size of items
    @Override
    public int getItemCount() {
        return items.size();
    }
}
