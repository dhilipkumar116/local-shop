package com.example.localshop.shopActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.localshop.Prevalent.productType;
import com.example.localshop.R;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

public class ShopRegisterActivity extends AppCompatActivity {

    private EditText Shopownername, Shopname, Shopphonenumber, Shoppassword, Shopemail, Streetname, Postcode, District;
    private Button Regsitershopbutton;
    private ProgressDialog Loadingbar;
    private Animation anim;
    private Spinner spinner;
    private String category;
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register);

        Paper.init(this);
        LinearLayout linearLayout = findViewById(R.id.SregisterL1);
        Shopownername = (EditText) findViewById(R.id.RegisterS_ownername);
        Shopname = (EditText) findViewById(R.id.RegisterS_shopname);
        Shopphonenumber = (EditText) findViewById(R.id.RegisterS_phonenumber);
        Shoppassword = (EditText) findViewById(R.id.RegisterS_password);
        Shopemail = (EditText) findViewById(R.id.RegisterS_email);
        Streetname = (EditText) findViewById(R.id.RegisterS_street);
        Postcode = (EditText) findViewById(R.id.RegisterS_postcode);
        District = (EditText) findViewById(R.id.RegisterS_district);
        Regsitershopbutton = (Button) findViewById(R.id.RegisterS_btn);
        spinner = (Spinner) findViewById(R.id.shop_cat_spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("grocery stores");
        arrayList.add("resturants and foods");
        arrayList.add("cakes and deserts");
        arrayList.add("meats and fishs");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        Loadingbar = new ProgressDialog(this);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        anim = AnimationUtils.loadAnimation(this, R.anim.bottom_top);
        linearLayout.setAnimation(anim);


        Regsitershopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Creatshopaccount();
            }
        });
    }


    private void Creatshopaccount() {

        final String Phonenumber = Shopphonenumber.getText().toString().trim();
        final String email = Shopemail.getText().toString().trim();
        final String Password = Shoppassword.getText().toString().trim();
        final String shopname = Shopname.getText().toString().trim();
        final String name = Shopownername.getText().toString().trim();
        final String streetname = Streetname.getText().toString().trim();
        final String postcode = Postcode.getText().toString().trim();
        final String district = District.getText().toString().trim();

        if (Phonenumber.isEmpty()) {
            Toast.makeText(this, "enter phone number", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(this, "enter email", Toast.LENGTH_SHORT).show();
        } else if (Password.isEmpty()) {
            Toast.makeText(this, "enter password", Toast.LENGTH_SHORT).show();
        } else if (shopname.isEmpty()) {
            Toast.makeText(this, "enter shop name", Toast.LENGTH_SHORT).show();
        } else if (name.isEmpty()) {
            Toast.makeText(this, "enter shop owner name", Toast.LENGTH_SHORT).show();
        } else if (streetname.isEmpty()) {
            Toast.makeText(this, "enter street name", Toast.LENGTH_SHORT).show();
        } else if (postcode.isEmpty()) {
            Toast.makeText(this, "enter postal code", Toast.LENGTH_SHORT).show();
        } else if (district.isEmpty()) {
            Toast.makeText(this, "enter city", Toast.LENGTH_SHORT).show();
        } else if (!Phonenumber.trim().matches(productType.phonePattern)) {
            Toast.makeText(this, "enter vaild phone", Toast.LENGTH_SHORT).show();
        } else if (!email.trim().matches(productType.emailPattern)) {
            Toast.makeText(this, "enter vaild email", Toast.LENGTH_SHORT).show();
        }else if (postcode.length()!=productType.postalcodeLength) {
            Toast.makeText(this, "enter vaild postal code", Toast.LENGTH_SHORT).show();
        } else if (Password.length()<productType.passwordLength) {
            Toast.makeText(this, "mininum character should be 8", Toast.LENGTH_SHORT).show();
        }else {

            Loadingbar.setMessage("Please wait Registering your shop...");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();

            //_________uploading data______________//

            final DatabaseReference Rootref;
            Rootref = FirebaseDatabase.getInstance().getReference().child("Admins");
            Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.child(shopname).exists()) {
                        HashMap<String, Object> shopdataMap = new HashMap<>();
                        shopdataMap.put("shop_email", email);
                        shopdataMap.put("shop_name", shopname);
                        shopdataMap.put("owner_name", name);
                        shopdataMap.put("shop_phone", Phonenumber);
                        shopdataMap.put("shop_password", Password);
                        shopdataMap.put("street", streetname);
                        shopdataMap.put("postcode", postcode);
                        shopdataMap.put("district", district);
                        shopdataMap.put("category", category);
                        shopdataMap.put("type", "own shop");
                        shopdataMap.put("approval", productType.approvalStatus);
                        Rootref.child(shopname).updateChildren(shopdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ShopRegisterActivity.this, "registered", Toast.LENGTH_SHORT).show();
                                    Loadingbar.dismiss();
                                    Paper.book().destroy();
                                    startActivity(new Intent(ShopRegisterActivity.this, ShopLoginActivity.class));

                                } else {
                                    Loadingbar.dismiss();
                                    Toast.makeText(ShopRegisterActivity.this, "Network error,please try again later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Loadingbar.dismiss();
                        Toast.makeText(ShopRegisterActivity.this, "Shop already exist, try another", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }
}
