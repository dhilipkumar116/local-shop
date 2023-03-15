package com.example.localshop.shopActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localshop.Prevalent.productType;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddProductActivity<ImageUri> extends AppCompatActivity {

    private String CategoryName, Pdescription, Pname, Pprice, Pno, Pdiscount,
            pquantity, Savecurrentdate, Savecurrenttime;
    private Button Upload_button;
    private EditText Product_name, Product_price, No_of_products, Product_description,
            Product_discount, product_quantity;
    private ImageView Product_image, Product_image_icon;
    private static final int Gallerpick = 1;
    private Uri ImageUri;
    private String Productrandomkey, Downloadimageurl, result;
    private StorageReference Productimageref;
    private DatabaseReference productsref;
    private ProgressDialog Loadingbar;
    private TextView selling_price;
    private RadioGroup quantitygrp, typegrp;
    private RadioButton radiobtn, typebtn;
    private static final String TAG = "adding product photo";
    private String pidForEdit = "", oldProductImg = "";
    private Boolean imagePicked = false;
    private int price, discount;
    private int sold;
    private String code ="";
    private RadioButton kg,g,l,inch,veg,non,natural,meter,piece;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        CategoryName = getIntent().getExtras().get("category").toString();
        pidForEdit = getIntent().getStringExtra("pid");

        Productimageref = FirebaseStorage.getInstance().getReference()
                .child("product image").child(shopPrevalent.current_shop.getShop_name());
        productsref = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopPrevalent.current_shop.getShop_name()).child("products");

        Product_name = (EditText) findViewById(R.id.Productname);
        Product_price = (EditText) findViewById(R.id.Productprice);
        Product_discount = (EditText) findViewById(R.id.Productdiscount);
        No_of_products = (EditText) findViewById(R.id.Noofproducts);
        Product_description = (EditText) findViewById(R.id.Productdescription);
        Upload_button = (Button) findViewById(R.id.Upload_button);
        Product_image = (ImageView) findViewById(R.id.Productimage);
        Product_image_icon = (ImageView) findViewById(R.id.Productimage_icon);
        selling_price = (TextView) findViewById(R.id.Productsellingprice);
        product_quantity = (EditText) findViewById(R.id.Productquantity);
        Loadingbar = new ProgressDialog(this);

        quantitygrp = (RadioGroup) findViewById(R.id.quantity_btn);
        kg = (RadioButton)findViewById(R.id.kg_btn);
        g = (RadioButton)findViewById(R.id.g_btn);
        l = (RadioButton)findViewById(R.id.l_btn);
        inch = (RadioButton)findViewById(R.id.inch_btn);
        meter = (RadioButton)findViewById(R.id.meter_btn);
        piece = (RadioButton)findViewById(R.id.piece_btn);

        typegrp = (RadioGroup) findViewById(R.id.type_btn);
        veg = (RadioButton) findViewById(R.id.veg_btn);
        non = (RadioButton)findViewById(R.id.nonveg_btn);
        natural = (RadioButton)findViewById(R.id.natural_btn);

        Product_image_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        Upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qty = quantitygrp.getCheckedRadioButtonId();
                radiobtn = ((RadioButton) findViewById(qty));

                int type = typegrp.getCheckedRadioButtonId();
                typebtn = ((RadioButton) findViewById(type));

                ValidateProductData();
            }
        });

        if (!pidForEdit.isEmpty()) {
            Product_image_icon.setVisibility(View.GONE);
            Upload_button.setText("update changes");
            displayThisProduct();
        }

        Product_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().equalsIgnoreCase("")) {
                    if (Product_price.getText().length() > 5) {
                        Product_price.setError("cannot exceed 5 digit");
                    } else if (Product_discount.getText().length() > 5) {
                        Product_discount.setError("cannot exceed 5 digit");
                    } else {
                        setSellingPrice();
                    }
                } else {
                    result = String.valueOf(price- 0);
                    selling_price.setText("selling price : "+productType.notation+" "+ result);
                }

            }
        });

        DatabaseReference codeRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
        codeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.child("distributed_code").exists()){
                    code = snapshot.child("distributed_code").getValue().toString();
             }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setSellingPrice() {
        price = Integer.parseInt(Product_price.getText().toString().trim());
        discount = Integer.parseInt(Product_discount.getText().toString().trim());
        Integer sub = price - discount;
        if (sub < 0) {
            Product_discount.setError("discount cannot exceed price");
        } else {
            result = String.valueOf(sub);
            selling_price.setText("selling price : "+productType.notation+" " + result);
        }
    }

    private void displayThisProduct() {
        productsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(pidForEdit).exists()) {
                    Product_name.setText(dataSnapshot.child(pidForEdit).child("name").getValue().toString());
                    product_quantity.setText(dataSnapshot.child(pidForEdit).child("quantity").getValue().toString());
                    Product_price.setText(dataSnapshot.child(pidForEdit).child("price").getValue().toString());
                    Product_discount.setText(dataSnapshot.child(pidForEdit).child("discount").getValue().toString());
                    selling_price.setText("selling price : " + dataSnapshot.child(pidForEdit).child("selling").getValue().toString() + "RM");
                    No_of_products.setText(dataSnapshot.child(pidForEdit).child("noofitem").getValue().toString());
                    Product_description.setText(dataSnapshot.child(pidForEdit).child("description").getValue().toString());
                    Picasso.get().load(dataSnapshot.child(pidForEdit).child("image").getValue().toString()).into(Product_image);
                    oldProductImg = dataSnapshot.child(pidForEdit).child("image").getValue().toString();
                    sold = Integer.parseInt(dataSnapshot.child(pidForEdit).child("sold").getValue().toString());
                    imagePicked = true;
                    String pQ = dataSnapshot.child(pidForEdit).child("kgmglml").getValue().toString();
                    String pT = dataSnapshot.child(pidForEdit).child("producttype").getValue().toString();
                    if(pQ.equals("kg")){kg.setChecked(true);}
                    else if(pQ.equals("g")){g.setChecked(true);}
                    else if(pQ.equals("l")){l.setChecked(true);}
                    else if(pQ.equals("meter")){meter.setChecked(true);}
                    else if(pQ.equals("piece")){piece.setChecked(true);}
                    else{inch.setChecked(true);}

                    if(pT.equals(productType.veg)){veg.setChecked(true);}
                    else if(pT.equals(productType.nonveg)){non.setChecked(true);}
                    else{natural.setChecked(true);}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ValidateProductData() {
        Pdescription = Product_description.getText().toString().trim();
        Pname = Product_name.getText().toString().trim();
        Pprice = Product_price.getText().toString().trim();
        Pno = No_of_products.getText().toString().trim();
        Pdiscount = Product_discount.getText().toString().trim();
        pquantity = product_quantity.getText().toString().trim();

        if (!imagePicked) {
            Toast.makeText(AddProductActivity.this, "add image", Toast.LENGTH_SHORT).show();
        } else if (Pname.isEmpty()) {
            Toast.makeText(AddProductActivity.this, "enter product name", Toast.LENGTH_SHORT).show();
        } else if (pquantity.isEmpty()) {
            Toast.makeText(AddProductActivity.this, "enter quantity", Toast.LENGTH_SHORT).show();
        } else if (Pprice.isEmpty()) {
            Toast.makeText(AddProductActivity.this, "enter price", Toast.LENGTH_SHORT).show();
        } else if (Pno.isEmpty()) {
            Toast.makeText(AddProductActivity.this, "enter no of product", Toast.LENGTH_SHORT).show();
        } else if (Pdescription.isEmpty()) {
            Toast.makeText(AddProductActivity.this, "enter description", Toast.LENGTH_SHORT).show();
        } else if (Product_price.getText().length() > 5) {
            Product_price.setError("cannot exceed 5 digit");
        } else if (Product_discount.getText().length() > 5) {
            Product_discount.setError("cannot exceed 5 digit");
        } else {
            setSellingPrice();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
            Savecurrentdate = currentdate.format(calendar.getTime());
            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
            Savecurrenttime = currenttime.format(calendar.getTime());

            if (pidForEdit.isEmpty()) {
                Productrandomkey = Savecurrentdate + Savecurrenttime;
                StoreProductInformation(result);
            } else {
                Productrandomkey = pidForEdit;
                SaveproductInfoDatabase();
            }
        }
    }

    private void StoreProductInformation(String result) {
        Loadingbar.setMessage("Uploading your product... ");
        Loadingbar.setCanceledOnTouchOutside(false);
        Loadingbar.show();


        final StorageReference filepath = Productimageref
                .child(ImageUri.getLastPathSegment() + Productrandomkey + ".jpg");

        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Loadingbar.dismiss();
                String message = e.toString();
                Toast.makeText(AddProductActivity.this, "ERROR:" + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        Log.w(TAG, task.getException());

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        Downloadimageurl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Downloadimageurl = task.getResult().toString();
                            SaveproductInfoDatabase();

                        }
                    }
                });
            }
        });
    }

    private void SaveproductInfoDatabase() {

        String selection = (String) radiobtn.getText().toString();
        String type = (String) typebtn.getText().toString();

        HashMap<String, Object> productMap = new HashMap<>();

        if (!pidForEdit.isEmpty()) {
            productMap.put("pid", Productrandomkey);
            productMap.put("sold", sold);
        } else {
            productMap.put("pid", Productrandomkey);
            productMap.put("image", Downloadimageurl);
            productMap.put("sold", 0);
        }
        productMap.put("category", CategoryName);
        productMap.put("date", Savecurrentdate);
        productMap.put("time", Savecurrenttime);
        productMap.put("description", Product_description.getText().toString().trim());
        productMap.put("name", Pname);
        productMap.put("price", Pprice);
        productMap.put("discount", Pdiscount);
        productMap.put("selling", result);
        productMap.put("shopname", shopPrevalent.current_shop.getShop_name());
        if (typebtn.getText().equals("veg")) {
            productMap.put("producttype", productType.veg);
        }
        if (typebtn.getText().equals("nonveg")) {
            productMap.put("producttype", productType.nonveg);
        }
        if (typebtn.getText().equals("natural")) {
            productMap.put("producttype", productType.natural);
        }

        productMap.put("noofitem", Pno);
        productMap.put("quantity", pquantity);
        productMap.put("kgmglml", selection);

        productsref.child(Productrandomkey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Loadingbar.dismiss();
                            Toast.makeText(AddProductActivity.this, "saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddProductActivity.this, HomeActivityS.class);
                            intent.putExtra("userORadmin" , "admin");
                            intent.putExtra("shopName" , shopPrevalent.current_shop.getShop_name());
                            intent.putExtra("code",code);
                            startActivity(intent);
                            finish();

                            if (!pidForEdit.isEmpty()) {
                                Toast.makeText(AddProductActivity.this, "product added", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(AddProductActivity.this, HomeActivityS.class);
                                intent1.putExtra("userORadmin" , "admin");
                                intent1.putExtra("shopName" , shopPrevalent.current_shop.getShop_name());
                                intent1.putExtra("code",code);
                                startActivity(intent1);
                                finish();
                            }

                        } else {
                            Loadingbar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddProductActivity.this, "ERROR:" + message, Toast.LENGTH_SHORT).show();
                        }


                    }
                });


    }

    private void openGallery() {
        CropImage.activity(ImageUri)
                .setAspectRatio(6, 5)
                .start(AddProductActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            ImageUri = result.getUri();
            Product_image.setImageURI(ImageUri);
            imagePicked = true;
            Product_image_icon.setVisibility(View.GONE);
        }
    }


}
