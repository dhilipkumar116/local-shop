package com.example.localshop.shopActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localshop.Adapters.productAdapterShop;
import com.example.localshop.AddCategoryActivity;
import com.example.localshop.HelpActivity;
import com.example.localshop.PlacePickerActivity;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.example.localshop.TermsandCondActivity;
import com.example.localshop.modelClass.Products;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;

public class HomeActivityS extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<Products> products_List;
    private productAdapterShop productAdapterShop;
    private CircularImageView shopPic;
    private TextView shopName;
    private LinearLayout add_product_to_shop,distribution_shop_side_layout;
    private String shopType,code;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_s);

        Toolbar toolbar = findViewById(R.id.shop_nav_tool_bar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("loading...");
        progressDialog.show();

        drawerLayout = findViewById(R.id.shop_drawer_layout);
        add_product_to_shop = findViewById(R.id.add_product_to_shop);
        distribution_shop_side_layout = findViewById(R.id.distribution_shop_side_layout);

        code = getIntent().getStringExtra("code");

        NavigationView navigationView = findViewById(R.id.shop_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menunav = navigationView.getMenu();
        final MenuItem add_menu = menunav.findItem(R.id.nav_admin_addproducts);
        final MenuItem addoffer_menu = menunav.findItem(R.id.nav_admin_bestoffer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_open_drawer, R.string.navigation_close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        FloatingActionButton floatingActionButton = findViewById(R.id.fab_new_order);
        View Header = navigationView.getHeaderView(0);
        shopPic = Header.findViewById(R.id.shop_header_profilepic);
        shopName = Header.findViewById(R.id.shop_header_name);

        Paper.init(this);
        recyclerView = findViewById(R.id.shop_product_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new
                DividerItemDecoration(HomeActivityS.this,
                DividerItemDecoration.VERTICAL));


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivityS.this , NewOrderActivity.class));
            }
        });

        add_product_to_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivityS.this , AddCategoryActivity.class));
            }
        });
        distribution_shop_side_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dummy click listener for distributed shop layout , to disable edit and remove button
            }
        });

        DatabaseReference shopTypeRef = FirebaseDatabase.getInstance().getReference().child("Admins");
        shopTypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopType = dataSnapshot.child(shopPrevalent.current_shop.getShop_name())
                        .child("type").getValue().toString();
                if(shopType.equals("distributed shop") && code.equals("")){
                    add_menu.setEnabled(false);
                    addoffer_menu.setEnabled(false);
                    distribution_shop_side_layout.setEnabled(false);
                }else if(shopType.equals("own shop")){
                    add_menu.setEnabled(true);
                    addoffer_menu.setEnabled(true);
                    distribution_shop_side_layout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(code.length()==6){
            add_menu.setEnabled(true);
            addoffer_menu.setEnabled(true);
            distribution_shop_side_layout.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        showDailogueBoxes();

//        DatabaseReference AdminOrderRef = FirebaseDatabase.getInstance().getReference()
//        .child("Admins").child(shopPrevalent.current_shop.getShop_name());
//        AdminOrderRef.child("orderProduct").removeValue();

        shopName.setText(shopPrevalent.current_shop.getShop_name());
        Picasso.get().load(shopPrevalent.current_shop.getImage()).into(shopPic);
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name()).child("products");

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products_List = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Products p = dataSnapshot1.getValue(Products.class);
                    products_List.add(p);
                }
                productAdapterShop =
                        new productAdapterShop(HomeActivityS.this, products_List);
                recyclerView.setAdapter(productAdapterShop);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showDailogueBoxes() {
        DatabaseReference alertDailogRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
        alertDailogRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isFinishing()) {
                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivityS.this);

//                    if (!dataSnapshot.child("products").exists()) {
//                        builder1.setTitle("shop is empty!");
//                        builder1.setMessage("add your products to your shop");
//                        builder1.setCancelable(false);
//                        builder1.setPositiveButton("now", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(HomeActivityS.this, AddCategoryActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//
//                        builder1.setNegativeButton("after", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        }).show();
//
//                    }

                    if (!dataSnapshot.child("image").exists()) {
                        builder1.setTitle("set shop pic!");
                        builder1.setMessage("add shop image to publish your shop");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(HomeActivityS.this, SettingActivityS.class);
                                startActivity(intent);
                            }
                        }).show();
                    } else if (!dataSnapshot.child("latitude").exists()) {

                        builder1.setCancelable(false);
                        builder1.setTitle("location setting!");
                        builder1.setMessage("pick shop location in map, to show your shop location to user");
                        builder1.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(HomeActivityS.this, PlacePickerActivity.class);
                                intent.putExtra("shopname", shopPrevalent.current_shop.getShop_name());
                                startActivity(intent);

                            }
                        }).show();
                    }
                }

                if(!dataSnapshot.child("products").exists()){
                    add_product_to_shop.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String userinput = s.toLowerCase();

                ArrayList<Products> productFiltered = new ArrayList<>();

                for (Products filter : products_List) {
                    if (filter.getName().toLowerCase().contains(userinput)) {
                        productFiltered.add(filter);
                    }
                }
                productAdapterShop.updateproductlist(productFiltered);
                return true;
            }
        });
        return true;

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_admin_addproducts: {
                Intent intent = new Intent(HomeActivityS.this, AddCategoryActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_admin_bestoffer: {
                startActivity(new Intent(HomeActivityS.this, AddOfferActivity.class));
                break;
            }
            case R.id.nav_admin_add_order: {
                startActivity(new Intent(HomeActivityS.this,AddOrderActivity.class));
                break;
            }
            case R.id.nav_admin_order: {
                startActivity(new Intent(HomeActivityS.this,NewOrderActivity.class));
                break;
            }
            case R.id.nav_admin_setting: {
                Intent intent = new Intent(HomeActivityS.this, SettingActivityS.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_admin_help: {
                Intent intent = new Intent(HomeActivityS.this , HelpActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_admin_termsandcondition: {
                Intent intent = new Intent(HomeActivityS.this , TermsandCondActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_admin_logout: {
                Paper.book().destroy();
                Intent intent = new Intent(HomeActivityS.this, ShopLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
