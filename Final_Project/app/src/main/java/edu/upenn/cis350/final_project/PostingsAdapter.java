package edu.upenn.cis350.final_project;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class PostingsAdapter extends RecyclerView.Adapter<PostingsAdapter.MyViewHolder> {
    private List<String> titles;
    private List<String> descriptions;
    private int[] images;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout postRow;

        public MyViewHolder(LinearLayout v) {
            super(v);
            postRow = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostingsAdapter(List<String> titles, List<String> descriptions, int[] images) {
        this.titles = titles;
        this.descriptions = descriptions;
        this.images = images;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostingsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postingslist_row, parent, false);

        PostingsAdapter.MyViewHolder vh = new PostingsAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PostingsAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView item_title = holder.postRow.findViewById(R.id.item_title);
        item_title.setText(titles.get(position));

        TextView item_description = holder.postRow.findViewById(R.id.item_description);
        item_description.setText(descriptions.get(position));

        ImageView image = holder.postRow.findViewById(R.id.row_image);
        image.setImageAlpha(images[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return titles.size();
    }
}

