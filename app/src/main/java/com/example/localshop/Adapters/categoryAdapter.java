package com.example.localshop.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localshop.R;
import com.example.localshop.shopActivity.AddProductActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.myviewHolder> {

    private Context context;
    private ArrayList categorylist ,catIconlist;

    public categoryAdapter(Context context,ArrayList categorylist,
                           ArrayList catIconlist ) {
        this.context = context;
        this.categorylist = categorylist;
        this.catIconlist = catIconlist;
    }


    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_category_layout,parent,false);


        myviewHolder myviewHolder = new myviewHolder(view);
        return myviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myviewHolder holder, final int position) {

        holder.circularImageView.setImageResource(catIconlist.get(position).hashCode());
        holder.listname.setText(categorylist.get(position).toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent indent = new Intent(context, AddProductActivity.class);
                    indent.putExtra("category", categorylist.get(position).toString());
                    indent.putExtra("pid","");
                    context.startActivity(indent);
                    ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorylist.size();
    }


    public class myviewHolder extends RecyclerView.ViewHolder {

        private TextView listname;
        private CircularImageView circularImageView;

        public myviewHolder(@NonNull View itemView) {
            super(itemView);

            listname = itemView.findViewById(R.id.add_cate_txt);
            circularImageView = itemView.findViewById(R.id.add_cate_icon);
        }
    }
}