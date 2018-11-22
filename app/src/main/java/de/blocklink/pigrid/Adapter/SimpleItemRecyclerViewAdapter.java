package de.blocklink.pigrid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import de.blocklink.pigrid.FullscreenActivity;
import de.blocklink.pigrid.R;

public  class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private List<PiItem> pis;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PiItem item = (PiItem) view.getTag();

            Context context = view.getContext();
            Intent intent = new Intent(context, FullscreenActivity.class);
            intent.putExtra(FullscreenActivity.URL, item.location);
            context.startActivity(intent);
        }
    };

    public SimpleItemRecyclerViewAdapter(List<PiItem> items) {
        pis = items;
    }
    public void setData(List<PiItem> items)
    {
        pis = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(pis.get(position).location);

        holder.itemView.setTag(pis.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }



    @Override
    public int getItemCount() {
        if (pis != null){
            return pis.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.pieItem);
            mContentView = view.findViewById(R.id.content);
        }
    }
}
