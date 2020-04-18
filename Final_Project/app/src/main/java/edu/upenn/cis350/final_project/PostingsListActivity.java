package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class PostingsListActivity extends AppCompatActivity {

    ListView listView;
    String titles[] = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7",
            "Item 8"};
    String descriptions[] = {"Item description 1", "Item description 2", "Item description 3",
            "Item description 4", "Item description 5", "Item description 6", "Item description 7",
            "Item description 8"};
    // images in drawable folder
    int images[] = {R.drawable.art_icon, R.drawable.blackboard_icon, R.drawable.book_icon,
            R.drawable.books_icon, R.drawable.computer_icon, R.drawable.deskchair_icon,
            R.drawable.art_icon, R.drawable.blackboard_icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postings_list);

        listView = findViewById(R.id.listView);

        MyAdapter adapter = new MyAdapter(this, titles, descriptions, images);
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
        });
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String itemTitles[];
        String itemDescriptions[];
        int itemImages[];

        MyAdapter (Context c, String title[], String description[], int imgs[]) {
            super(c, R.layout.postingslist_row, R.id.item_title, title);
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
