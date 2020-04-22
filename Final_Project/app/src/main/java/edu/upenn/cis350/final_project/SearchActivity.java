package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    RadioGroup radioRadius;
    RadioGroup radioMaterials;
    RadioGroup radioTrade;
    TextView desiredPrice;
    TextView desiredAvail;
    ListView searchResults;
    ArrayAdapter<String> adapter;
    static final int HOME_ACTIVITY_ID = 29;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        radioRadius = findViewById(R.id.radio_radius);
        radioMaterials = findViewById(R.id.radio_materials);
        radioTrade = findViewById(R.id.radio_trade);
        desiredPrice = findViewById(R.id.price_input);
        desiredAvail = findViewById(R.id.avail_input);
        searchResults = findViewById(R.id.search_item);

        // This is where the entire database of items would be populated
        ArrayList<String> items = new ArrayList<>();
        items.addAll(Arrays.asList(getResources().getStringArray(R.array.example)));
        adapter = new ArrayAdapter<>(
                SearchActivity.this,
                android.R.layout.simple_list_item_1,
                items
        );
        searchResults.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Set up the search bar
        MenuItem search = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                radioRadius.setVisibility(View.VISIBLE);
                radioMaterials.setVisibility(View.VISIBLE);
                radioTrade.setVisibility(View.VISIBLE);
                desiredPrice.setVisibility(View.VISIBLE);
                desiredAvail.setVisibility(View.VISIBLE);
                searchResults.setVisibility(View.INVISIBLE);
                adapter.getFilter().filter(newText);
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    //TODO: Fill this in as needed
    void performSearch(String query) {
        radioRadius.setVisibility(View.INVISIBLE);
        radioMaterials.setVisibility(View.INVISIBLE);
        radioTrade.setVisibility(View.INVISIBLE);
        desiredPrice.setVisibility(View.INVISIBLE);
        desiredAvail.setVisibility(View.INVISIBLE);
        searchResults.setVisibility(View.VISIBLE);
    }

    public void onHomeClick(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivityForResult(i, HOME_ACTIVITY_ID);
    }

}
