package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class PostingsListActivity extends AppCompatActivity {

    private String titles[] = {"Hello", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7",
            "Item 8"};
    private String descriptions[] = {"Item description 1", "Item description 2", "Item description 3",
            "Item description 4", "Item description 5", "Item description 6", "Item description 7",
            "Item description 8"};
    //images in drawable folder
    private int image_icons[] = {R.drawable.art_icon, R.drawable.blackboard_icon, R.drawable.book_icon,
            R.drawable.books_icon, R.drawable.computer_icon, R.drawable.deskchair_icon,
            R.drawable.art_icon, R.drawable.blackboard_icon};

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postings_list);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(titles, descriptions, image_icons);
        recyclerView.setAdapter(mAdapter);

    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private String[] titles;
        private String[] descriptions;
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
        public MyAdapter(String[] titles, String[] descriptions, int[] images) {
            this.titles = titles;
            this.descriptions = descriptions;
            this.images = images;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.postingslist_row, parent, false);

            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            TextView item_title = holder.postRow.findViewById(R.id.item_title);
            item_title.setText(titles[position]);

            TextView item_description = holder.postRow.findViewById(R.id.item_description);
            item_description.setText(descriptions[position]);

            ImageView image = holder.postRow.findViewById(R.id.row_image);
            image.setImageAlpha(images[position]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
       /* listView = findViewById(R.id.posts_list);

        MyAdapter adapter = new MyAdapter(this, titles, descriptions, image_icons);
        listView.setAdapter(adapter);

        // item click on list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(PostingsListActivity.this,
                            "Brief item description here.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
/*
    class MyAdapter extends ArrayAdapter {

        Context context;
        String itemTitles[];
        String itemDescriptions[];
        int itemImages[];

        public MyAdapter (Context c, String title[], String description[], int imgs[]) {
            super(c, R.layout.postingslist_row);
            this.context = c;
            this.itemTitles = title;
            this.itemDescriptions = description;
            this.itemImages = imgs;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View concertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.postingslist_row, parent,false);
            ImageView images = row.findViewById(R.id.row_image);
            TextView myTitle = row.findViewById(R.id.item_title);
            TextView myDescription = row.findViewById(R.id.item_description);

            images.setImageResource(itemImages[position]);
            myTitle.setText(itemTitles[position]);
            myDescription.setText(itemDescriptions[position]);

            return row;
        }

    }
}
*/