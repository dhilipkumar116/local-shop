package com.example.localshop.shopActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localshop.Prevalent.productType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.localshop.Adapters.adminAddOrderAdapter;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.example.localshop.modelClass.Carts;
import com.example.localshop.modelClass.Products;

import java.util.ArrayList;

public class AddOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<Products> productsArrayList;
    private ArrayList<Carts> selectedList;
    private DatabaseReference databaseReference;
    private adminAddOrderAdapter adminAddOrderAdapter;
    private Button nextbtn;
    private boolean isProductListEmpty = false;
    private int onetypetotalprice=0;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);


        recyclerView = findViewById(R.id.add_orderr1);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nextbtn = findViewById(R.id.nextBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopPrevalent.current_shop.getShop_name()).child("products");


        DatabaseReference deleteOrderRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
        deleteOrderRef.child("orderProduct").removeValue().isSuccessful();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productsArrayList = new ArrayList<>();
                for (DataSnapshot product : snapshot.getChildren()) {
                    Products products = product.getValue(Products.class);
                    productsArrayList.add(products);
                }
                adminAddOrderAdapter = new adminAddOrderAdapter(AddOrderActivity.this,
                        productsArrayList);
                recyclerView.setItemViewCacheSize(productsArrayList.size());
                recyclerView.setAdapter(adminAddOrderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference AddOrderRef = FirebaseDatabase.getInstance().getReference().child("Admins")
                        .child(shopPrevalent.current_shop.getShop_name());
                AddOrderRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("orderProduct").exists()) {
                            isProductListEmpty = true;
                        } else {
                            isProductListEmpty = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (isProductListEmpty) {
                    Toast.makeText(AddOrderActivity.this, "list is empty", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else
                {
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);

                    if (getTotalPrice() > 0) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddOrderActivity.this);
                        builder.setMessage("total price : "+ productType.notation+" "+ onetypetotalprice);
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(AddOrderActivity.this, SubmitAdminOrderActivity.class);
                                intent.putExtra("totalPrice", String.valueOf(onetypetotalprice));
                                startActivity(intent);
                            }
                        });
                        builder.show();
                        onetypetotalprice = 0;

                    }


                }

            }


        });

    }

    private int getTotalPrice() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
        orderRef.child("orderProduct").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onetypetotalprice = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Carts list = dataSnapshot.getValue(Carts.class);
                    onetypetotalprice = onetypetotalprice + list.getSellP();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progressDialog.dismiss();
        return onetypetotalprice;
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

                for (Products filter : productsArrayList) {
                    if (filter.getName().toLowerCase().contains(userinput)) {
                        productFiltered.add(filter);
                    }
                }
                adminAddOrderAdapter.updateproductlist(productFiltered);
                return true;
            }
        });
        return true;

    }
}
