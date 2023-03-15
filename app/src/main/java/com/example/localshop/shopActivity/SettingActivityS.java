package com.example.localshop.shopActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.localshop.PlacePickerActivity;
import com.example.localshop.Prevalent.productType;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingActivityS extends AppCompatActivity {

    private EditText shopName, shopPhone, shopPass, shopStreet, shopPostal, shopDistrict, shopEmail;
    private TextView changeShopPic;
    private ImageView shopPic, hidepass, editcat;
    private Button updateBtn;
    private Uri imageUri;
    private Boolean hide = true;
    private StorageReference picRef;
    private String myUrl = "";
    private Boolean isNewImagePicked = false, isImageAlreadyPresent = false;
    private Spinner spinner1;
    private Boolean isNewItemSelected = false;
    private String category;

    private EditText distribution_code;
    private Button distribution_btn;
    private LinearLayout distributedLayout;

    private String getCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_s);

        picRef = FirebaseStorage.getInstance().getReference().child("shop pictures");

        shopName = (EditText) findViewById(R.id.admin_setting_shop_name);
        shopPass = findViewById(R.id.admin_setting_shop_password);
        shopPhone = findViewById(R.id.admin_setting_shop_phone);
        shopDistrict = findViewById(R.id.admin_setting_shop_district);
        shopStreet = findViewById(R.id.admin_setting_shop_street);
        shopPostal = findViewById(R.id.admin_setting_postal_code);
        shopEmail = findViewById(R.id.admin_setting_shop_email);
        changeShopPic = findViewById(R.id.admin_setting_profile_image_txt);
        shopPic = findViewById(R.id.admin_setting_profile_image);
        updateBtn = findViewById(R.id.admin_setting_update_btn);
        hidepass = findViewById(R.id.hide_pass_icon);
        editcat = findViewById(R.id.edit_cate_icon);

        distributedLayout = findViewById(R.id.distribution_layout);
        distribution_code = findViewById(R.id.distribution_code);
        distribution_btn = findViewById(R.id.distribution_button);

        spinner1 = findViewById(R.id.shop_cat_spinner_edit);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("grocery stores");
        arrayList.add("foods and items");
        arrayList.add("cakes and deserts");
        arrayList.add("meats and fishs");
        arrayList.add("other shops");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(arrayAdapter);

        userInfoDisplay();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        shopPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(imageUri).setAspectRatio(1, 1)
                        .start(SettingActivityS.this);
            }
        });

        hidepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hide == true) {
                    shopPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hide = false;
                } else {
                    shopPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hide = true;
                }

            }
        });

        editcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner1.setVisibility(View.VISIBLE);
                spinner1.performClick();
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isNewItemSelected = true;
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        distribution_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDistributedCode();
            }
        });

    }

    private void validateDistributedCode() {
        if (distribution_code.getText().toString().isEmpty()) {
            Toast.makeText(SettingActivityS.this, "enter code", Toast.LENGTH_SHORT).show();
        } else if (distribution_code.getText().length() != 6) {
            Toast.makeText(SettingActivityS.this, "enter 6 digit code", Toast.LENGTH_SHORT).show();
        } else {
            if (getCode.equals(distribution_code.getText().toString().trim())) {
                Intent intent = new Intent(SettingActivityS.this, HomeActivityS.class);
                intent.putExtra("code", getCode);
                startActivity(intent);
            } else {
                Toast.makeText(SettingActivityS.this, "incorrect code", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void userInfoDisplay() {
        DatabaseReference settingRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
        settingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("image").exists()) {
                    isImageAlreadyPresent = true;
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(shopPic);
                }
                shopName.setText(dataSnapshot.child("shop_name").getValue().toString());
                shopPhone.setText(dataSnapshot.child("shop_phone").getValue().toString());
                shopPass.setText(dataSnapshot.child("shop_password").getValue().toString());
                shopDistrict.setText(dataSnapshot.child("district").getValue().toString());
                shopPostal.setText(dataSnapshot.child("postcode").getValue().toString());
                shopStreet.setText(dataSnapshot.child("street").getValue().toString());
                shopEmail.setText(dataSnapshot.child("shop_email").getValue().toString());
                if (dataSnapshot.child("category").getValue().toString().equals("supermarket")) {
                    spinner1.setSelection(0);
                }
                if (dataSnapshot.child("category").getValue().toString().equals("foods and hotels")) {
                    spinner1.setSelection(1);
                }
                if (dataSnapshot.child("category").getValue().toString().equals("shop recipes and items")) {
                    spinner1.setSelection(2);
                }
                if (dataSnapshot.child("category").getValue().toString().equals("cakes and deserts")) {
                    spinner1.setSelection(3);
                }

                if (dataSnapshot.child("type").getValue().toString().equals("distributed shop")) {
                    distributedLayout.setVisibility(View.VISIBLE);
                    getCode = dataSnapshot.child("distributed_code").getValue().toString();
                } else {
                    distributedLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            isNewImagePicked = true;
            shopPic.setImageURI(imageUri);

        }
    }

    private void validateData() {

        if (shopName.getText().toString().trim().equals("")) {
            Toast.makeText(SettingActivityS.this, "enter shop name", Toast.LENGTH_SHORT).show();
        } else if (shopPhone.getText().toString().trim().equals("")) {
            Toast.makeText(SettingActivityS.this, "enter shop phone", Toast.LENGTH_SHORT).show();
        } else if (shopPass.getText().toString().trim().equals("")) {
            Toast.makeText(SettingActivityS.this, "enter shop password", Toast.LENGTH_SHORT).show();
        } else if (shopStreet.getText().toString().trim().equals("")) {
            Toast.makeText(SettingActivityS.this, "enter shop street", Toast.LENGTH_SHORT).show();
        } else if (shopPostal.getText().toString().trim().equals("")) {
            Toast.makeText(SettingActivityS.this, "enter shop postal code", Toast.LENGTH_SHORT).show();
        } else if (shopDistrict.getText().toString().trim().equals("")) {
            Toast.makeText(SettingActivityS.this, "enter shop district", Toast.LENGTH_SHORT).show();
        } else if (shopEmail.getText().toString().trim().equals("")) {
            Toast.makeText(SettingActivityS.this, "enter shop email", Toast.LENGTH_SHORT).show();
        } else if (!shopPhone.getText().toString().trim().trim().matches(productType.phonePattern)) {
            Toast.makeText(SettingActivityS.this, "incorrect email", Toast.LENGTH_SHORT).show();
        } else if (!shopEmail.getText().toString().trim().matches(productType.emailPattern)) {
            Toast.makeText(SettingActivityS.this, "incorrect email", Toast.LENGTH_SHORT).show();
        } else if (shopPostal.getText().toString().trim().length()!=productType.postalcodeLength) {
            Toast.makeText(SettingActivityS.this, "incorrect postal code", Toast.LENGTH_SHORT).show();
        }else if (shopPass.getText().toString().trim().length()<productType.passwordLength) {
            Toast.makeText(SettingActivityS.this, "minimum character should be 8", Toast.LENGTH_SHORT).show();
        }else {
            if (isNewImagePicked) {
                uploadImage();
            } else {
                uploadInfoToDb();
            }

        }

    }

    private void uploadImage() {

        if (!imageUri.equals("")) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("updating your profile...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final StorageReference fileRef = picRef.child(shopPrevalent.current_shop.getShop_name() + ".jpg");
            UploadTask uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        Uri downloadUrl = (Uri) task.getResult();
                        myUrl = downloadUrl.toString();
                        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference()
                                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
                        HashMap<String, Object> shopMap = new HashMap<>();
                        shopMap.put("shop_name", shopPrevalent.current_shop.getShop_name());
                        shopMap.put("shop_phone", shopPhone.getText().toString().trim());
                        shopMap.put("shop_password", shopPass.getText().toString().trim());
                        shopMap.put("district", shopDistrict.getText().toString().trim());
                        shopMap.put("postcode", shopPostal.getText().toString().trim());
                        shopMap.put("street", shopStreet.getText().toString().trim());
                        shopMap.put("shop_email", shopEmail.getText().toString().trim());
//                        if (isNewItemSelected) {
//                            shopMap.put("category", category);
//                        }
                        shopMap.put("image", myUrl);

                        shopRef.updateChildren(shopMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SettingActivityS.this, "updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SettingActivityS.this, SettingActivityS.class));
                                    finish();

                                }

                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivityS.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadInfoToDb() {

        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
        HashMap<String, Object> shopMap = new HashMap<>();
        shopMap.put("shop_name", shopPrevalent.current_shop.getShop_name());
        shopMap.put("shop_phone", shopPhone.getText().toString().trim());
        shopMap.put("shop_password", shopPass.getText().toString().trim());
        shopMap.put("district", shopDistrict.getText().toString().trim());
        shopMap.put("postcode", shopPostal.getText().toString().trim());
        shopMap.put("street", shopStreet.getText().toString().trim());
        shopMap.put("shop_email", shopEmail.getText().toString().trim());
//        if (isNewItemSelected) {
//            shopMap.put("category", category);
//        }
        shopRef.updateChildren(shopMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingActivityS.this, "updated", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_shop_location, menu);
        MenuItem item = menu.findItem(R.id.shop_pick_location);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(SettingActivityS.this, PlacePickerActivity.class);
                intent.putExtra("shopname", shopPrevalent.current_shop.getShop_name());
                startActivity(intent);
                return true;
            }
        });
        return true;
    }
}
