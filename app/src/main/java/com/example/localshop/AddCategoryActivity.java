package com.example.localshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.localshop.Adapters.categoryAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class AddCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    ArrayList<String> categorylist = new ArrayList<>(Arrays.asList(
            "Rice & Grains",
            "Delicious foods",
            "Cakes & Deserts",
            "Packed foods",
            "spices",
            "meats & fishs",
            "Fruits",
            "Vegetables",
            "Sauces & jams",
            "Beverages",
            "Snacks",
            "Canned & Frozen foods"
    ));


    ArrayList catIconlist = new ArrayList(Arrays.asList(
            R.drawable.rice,
            R.drawable.foods,
            R.drawable.cake,
            R.drawable.packed,
            R.drawable.spices,
            R.drawable.meat,
            R.drawable.fruits,
            R.drawable.vegitables,
            R.drawable.sauce,
            R.drawable.beverages,
            R.drawable.snack,
            R.drawable.canned
    ));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        recyclerView = findViewById(R.id.add_cate_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        categoryAdapter categoryAdapter = new categoryAdapter(AddCategoryActivity.this
                ,categorylist,catIconlist);
        recyclerView.setAdapter(categoryAdapter);

    }
}
