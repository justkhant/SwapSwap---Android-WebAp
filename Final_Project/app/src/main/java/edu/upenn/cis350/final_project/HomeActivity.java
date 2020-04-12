package edu.upenn.cis350.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ArrayList<Item> itemList = new ArrayList<>();
    public static final int INFO_ACTIVITY_ID = 5;
    public static final int SEARCH_ACTIVITY_ID = 6;
    public static final int POSTS_ACTIVITY_ID = 7;
    public static final int PROFILE_ACTIVITY_ID = 8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: Started.");
        ListView mListView = (ListView) findViewById(R.id.listView);

        //Create the Item objects

        Item book1 = new Item("Hamlet","Book","Borrow",
                "drawable://" + R.drawable.image_1);
        Item whiteboard = new Item("Whiteboard","Materials","Give",
                "drawable://" + R.drawable.image_1);
        Item book2 = new Item("To Kill A Mockingbird","Book","Give",
                "drawable://" + R.drawable.image_1);
        Item pencils = new Item("Pencils","Materials","Give",
                "drawable://" + R.drawable.image_1);
        Item desk = new Item("Desk","Furniture","Give",
                "drawable://" + R.drawable.image_1);
        Item book3 = new Item("Beloved","book","Borrow",
                "drawable://" + R.drawable.image_1);
        Item mouse = new Item("Mouse","Materials","Request",
                "drawable://" + R.drawable.image_1);




        //Add the Person objects to an ArrayList
        // ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(book1);
        itemList.add(whiteboard);
        itemList.add(book2);
        itemList.add(pencils);
        itemList.add(desk);
        itemList.add(book3);
        itemList.add(mouse);
        itemList.add(pencils);
        itemList.add(book2);
        itemList.add(mouse);
        itemList.add(whiteboard);
        itemList.add(book3);
        itemList.add(book1);
        itemList.add(pencils);
        itemList.add(book1);
        itemList.add(whiteboard);
        itemList.add(book2);
        itemList.add(pencils);
        itemList.add(desk);
        itemList.add(book3);
        itemList.add(mouse);
        itemList.add(pencils);
        itemList.add(book2);
        itemList.add(mouse);
        itemList.add(whiteboard);
        itemList.add(book3);
        itemList.add(book1);
        itemList.add(pencils);



        ItemListAdapter adapter = new ItemListAdapter(this, R.layout.activity_item_adapter_view_layout, itemList);
        mListView.setAdapter(adapter);

    }

    public void onInfoClick(View view) {
        Intent i = new Intent(this, InstructionsActivity.class);
        startActivityForResult(i, INFO_ACTIVITY_ID);
    }

    public void onHomeClick(View view) {
        //idk if need this?
        //Intent i = new Intent(this, ____Activity.class);
        //startActivityForResult(i, HOME_ACTIVITY_ID);
    }

    public void onSearchClick(View view) {
       //not implemented yet
        Intent i = new Intent(this, SearchActivity.class);
        startActivityForResult(i, SEARCH_ACTIVITY_ID);
    }

    public void onPostsClick(View view) {
        //not implemented yet
        //Intent i = new Intent(this, __Activity.class);
        //startActivityForResult(i, POSTS_ACTIVITY_ID);
    }

    public void onProfileClick(View view) {
        Intent i = new Intent(this, UserProfileActivity.class);
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }

}



