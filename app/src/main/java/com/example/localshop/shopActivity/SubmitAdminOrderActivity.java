package com.example.localshop.shopActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localshop.Prevalent.productType;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SubmitAdminOrderActivity extends AppCompatActivity {

    private EditText searchLocation;
    private EditText confirm_name, confirm_phone, confirm_address;
    private TextView total_payment_cod,payment_chosen,total_payment_cos;
    private Button place_order;
    private RadioGroup paymentGrp;
    private RadioButton paymentRadiobtn;
    private ProgressDialog progressDialog;
    private String savecurrentdate, savecurrenttime, orderID;
    private DatabaseReference frommode, tonode,shopRef;
    private Double deliveryPricein20km, deliveryPriceoout20km;
    private String deliveryPrice, subtot;
    private String cod_total, cod_total_sub, distance;
    private double lat=0.0,lang=0.0,shopLat = 0.0, shopLong = 0.0;
    private String shopAdd,shopPh,bankNum="not available",bankName="not available";
    private  String totalPrice;
    private RadioButton Cod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_admin_order);
        Places.initialize(this,"AIzaSyDfgBMnDZFxKHsndOKyRAaCjwyDgcnM1JA");
        progressDialog = new ProgressDialog(this);

        totalPrice = getIntent().getStringExtra("totalPrice");
        searchLocation = findViewById(R.id.AsearchPlace);
        confirm_name = findViewById(R.id.Aconfirm_name);
        confirm_phone = findViewById(R.id.Aconfirm_phonenumber);
        confirm_address = findViewById(R.id.Aconfirm_address);
        place_order = findViewById(R.id.Adminplace_order);
        total_payment_cod = findViewById(R.id.Atotal_payment_cod);
        total_payment_cos = findViewById(R.id.Atotal_payment_cos);
        payment_chosen = findViewById(R.id.Apayment_indicator3);
        Cod = findViewById(R.id.Aradio_cod);
        shopRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
        searchLocation.setFocusable(false);
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(SubmitAdminOrderActivity.this);
                startActivityForResult(intent,100);
            }
        });
        paymentGrp = findViewById(R.id.Aconfirm_ordertyperadiogrp);

            paymentGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (lat != 0.0 && lang != 0.0) {
                        if (Double.parseDouble(distance) < 20) {
                            deliveryPrice = String.format("%.2f", Math.ceil(Double.parseDouble(distance) * deliveryPricein20km));
                            cod_total = String.format("%.2f", Double.parseDouble(deliveryPrice)
                                    + Double.parseDouble(totalPrice));
                        } else {
                            deliveryPrice = String.format("%.2f", Math.ceil(Double.parseDouble(distance) * deliveryPriceoout20km));
                            cod_total = String.format("%.2f", Double.parseDouble(deliveryPrice)
                                    + Double.parseDouble(totalPrice));
                        }
                        switch (checkedId) {
                            case R.id.Aradio_cos: {
                                total_payment_cod.setVisibility(View.GONE);
                                total_payment_cos.setVisibility(View.VISIBLE);
                                total_payment_cos.setText("product price : "+ productType.notation+ totalPrice + "\n" +
                                        "delivery fee : "+productType.notation+ 0.00 + "\n"
                                        + "total price : "+productType.notation+ totalPrice);
                                payment_chosen.setText("payment selected");
                                payment_chosen.setTextColor(getResources().getColor(R.color.green));
                                break;
                            }
                            case R.id.Aradio_cod: {
                                total_payment_cos.setVisibility(View.GONE);
                                total_payment_cod.setVisibility(View.VISIBLE);
                                total_payment_cod.setText("product price : "+productType.notation+ totalPrice + "\n" +
                                        "delivery fee : "+productType.notation+ deliveryPrice + "\n"
                                        + "total price : "+productType.notation+ cod_total);
                                payment_chosen.setText("payment selected");
                                payment_chosen.setTextColor(getResources().getColor(R.color.green));
                                break;
                            }
                        }
                    }else {
                        Toast.makeText(SubmitAdminOrderActivity.this,"choose location",Toast.LENGTH_SHORT).show();
                        Cod.setChecked(false);
                    }

                }
            });

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lang == 0.0 && lat == 0.0) {
                    Toast.makeText(SubmitAdminOrderActivity.this, "pick location", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(confirm_phone.getText().toString())
                        || TextUtils.isEmpty(confirm_name.getText().toString())
                        || TextUtils.isEmpty(confirm_address.getText().toString())) {
                    Toast.makeText(SubmitAdminOrderActivity.this, "enter all detials", Toast.LENGTH_SHORT).show();
                } else if (paymentGrp.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(SubmitAdminOrderActivity.this, "choose payment", Toast.LENGTH_SHORT).show();
                } else {
                    int type = paymentGrp.getCheckedRadioButtonId();
                    paymentRadiobtn = ((RadioButton) findViewById(type));
                    progressDialog.setMessage("placing order...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    placeOrderToShop();
                }
            }
        });

    }


    private void placeOrderToShop() {

        Calendar calfordate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdate = currentdate.format(calfordate.getTime());
        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm:ss a");
        savecurrenttime = currenttime.format(calfordate.getTime());
        orderID = savecurrentdate + savecurrenttime;

        tonode = FirebaseDatabase.getInstance().getReference()
                .child("orders_ID")
                .child(orderID);


//        shopRef.child("orderProduct").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                tonode.child("products").setValue(dataSnapshot.getValue())
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Confirmorder();
//                            }
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//

        shopRef.child("orderProduct").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tonode.child("products").setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Confirmorder();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

       @Override
    protected void onStart() {
        super.onStart();

        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("bank_name").exists()){
                    bankName = snapshot.child("bank_name").getValue().toString();
                }
                if(snapshot.child("bank_number").exists()){
                    bankNum = snapshot.child("bank_number").getValue().toString();
                }
                shopPh = snapshot.child("shop_phone").getValue().toString();
                shopAdd = snapshot.child("street").getValue().toString()+" "+
                        snapshot.child("district").getValue().toString()+" "+
                        snapshot.child("postcode").getValue().toString();
                shopLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                shopLong = Double.parseDouble(snapshot.child("longtitude").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference utilites = FirebaseDatabase.getInstance().getReference()
                .child("UTILITIES");
        utilites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                deliveryPricein20km = Double.parseDouble(dataSnapshot.child("delivery20km").getValue().toString());
                deliveryPriceoout20km = Double.parseDouble(dataSnapshot.child("deliveryabove20km").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void Confirmorder() {

        final DatabaseReference orderref = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopPrevalent.current_shop.getShop_name()).child("Orders");
        HashMap<String, Object> ordermap = new HashMap<>();
        ordermap.put("ordername", confirm_name.getText().toString());
        ordermap.put("userId", "--");
        ordermap.put("phone_number", confirm_phone.getText().toString());
        ordermap.put("address", confirm_address.getText().toString());
        ordermap.put("time", savecurrenttime);
        ordermap.put("date", savecurrentdate);
        ordermap.put("ID", orderID);
        ordermap.put("shopname", shopPrevalent.current_shop.getShop_name());
        ordermap.put("shop_ph",shopPh);
        ordermap.put("shop_address",shopAdd);
        ordermap.put("bankNum",bankNum);
        ordermap.put("bankName",bankName);
        if (paymentRadiobtn.getText().toString().equals("cash on shop")) {
            ordermap.put("payment", "COS");
            ordermap.put("Totprice", totalPrice);
            ordermap.put("delivery_fee", "0");
            ordermap.put("km", distance);
        }
        if (paymentRadiobtn.getText().toString().equals("cash on delivery")) {
            ordermap.put("payment", "COD");
            ordermap.put("Totprice", totalPrice);
            ordermap.put("delivery_fee", deliveryPrice);
            ordermap.put("km", distance);
        }
        ordermap.put("latitude", lat);
        ordermap.put("longtitude", lang);
        ordermap.put("shoplat",shopLat);
        ordermap.put("shoplang",shopLong);
        ordermap.put("status", "ready for delivery");
        ordermap.put("type", "specialOrder");
        tonode.updateChildren(ordermap).isSuccessful();
        orderref.child(orderID).updateChildren(ordermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();


                    Toast.makeText(SubmitAdminOrderActivity.this,
                            "order placed successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SubmitAdminOrderActivity.this,
                            HomeActivityS.class);
                    intent.putExtra("code", "");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            String location = place.getAddress();
            searchLocation.setText(location);
            lat = place.getLatLng().latitude;
            lang = place.getLatLng().longitude;
            confirm_address.setText(location);
            LatLng fromLatLang = new LatLng(lat,lang);
            LatLng toLatLang = new LatLng(shopLat,shopLong);
            distance = String.format("%.2f", SphericalUtil.computeDistanceBetween(fromLatLang,toLatLang)/1000);
            paymentGrp.clearCheck();
            total_payment_cod.setVisibility(View.GONE);

        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(SubmitAdminOrderActivity.this,status.getStatusMessage(),Toast.LENGTH_SHORT).show();
            Log.e("searchLocation",status.getStatusMessage());
        }
    }
}
