package com.iamtrk.androidExplorer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iamtrk.R;

/**
 * Created by m01231 on 15/08/16.
 */
public class Fragment_RVAdapter extends RecyclerView.Adapter<Fragment_RVAdapter.ContentViewHolder>{

    Content.Item build;
    //TODO: void notifyItemChanged (int position,Object payload) method. To update the position.

    public Fragment_RVAdapter(Content.Item Item){
        this.build = Item;
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        holder.title.setText(build.getDetailedItems().get(position).getName());
        holder.content.setText(build.getDetailedItems().get(position).getDetail());
    }

    @Override
    public Fragment_RVAdapter.ContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_details_layout, viewGroup, false);
        return new ContentViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return build.getDetailedItems().size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView content;

        ContentViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.new_item_cv);
            title = (TextView)itemView.findViewById(R.id.build_item_details_card_title);
            content = (TextView)itemView.findViewById(R.id.build_item_details_card_content);
        }
    }

}
