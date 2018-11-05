package de.blocklink.pgiri.pgd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import de.blocklink.pgiri.pgd.PieDetailActivity;
import de.blocklink.pgiri.pgd.R;

public  class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final List<PieItem> pies;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PieItem item = (PieItem) view.getTag();

            Context context = view.getContext();
            Intent intent = new Intent(context, PieDetailActivity.class);
            intent.putExtra(PieDetailActivity.ARG_ITEM_ID, item.location);
            context.startActivity(intent);
        }
    };

    public SimpleItemRecyclerViewAdapter(List<PieItem> items) {
        pies = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(pies.get(position).location);

        holder.itemView.setTag(pies.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }



    @Override
    public int getItemCount() {
        if (pies != null){
            return pies.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = (TextView) view.findViewById(R.id.pieItem);
            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }
}
