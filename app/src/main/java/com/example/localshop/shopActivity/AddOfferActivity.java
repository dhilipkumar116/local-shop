package com.example.localshop.shopActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localshop.DialogueBox.ComboDialogBox;
import com.example.localshop.Prevalent.productType;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.example.localshop.modelClass.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddOfferActivity extends AppCompatActivity {

    private ImageView pickImage, TodayComboImage, editImageIcon;
    private TextView selling_price, Today_combo_isempty_txt;
    private Button upload_image, add_combo_product;
    private EditText combo_price, combo_discount, combo_name , comb0_avail;
    private Uri imageUri;
    private String result;
    private Boolean imagePicked = false ,editImagePicked = false;
    private ProgressDialog progressBar;
    private String myUrl;
    private String currentDate, currentTime, productRandomKey;
    private Boolean isImageAlreadyPresent = false;
    private String oldImagePid , Cprice , Cdiscount , Cname;
    private int price, discount;
    private int image;


    private DatabaseReference Today_offer_Dateref;
    private StorageReference imageref;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String currentdate, currenttime, Productrandomkey, name, quantity, mrp, dis;
    private int sellingprice;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        Today_offer_Dateref = FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopPrevalent.current_shop.getShop_name());
        imageref = FirebaseStorage.getInstance().getReference().child("product image");
        progressBar = new ProgressDialog(this);

        combo_name = (EditText) findViewById(R.id.Today_Comboname);
        combo_price = (EditText) findViewById(R.id.Today_Comboprice);
        combo_discount = (EditText) findViewById(R.id.Today_Combodiscount);
        selling_price = (TextView) findViewById(R.id.Today_Combosellingprice);
        comb0_avail = (EditText) findViewById(R.id.Today_ComboAvailable);
        upload_image = (Button) findViewById(R.id.Today_Combo_AddImage);
        add_combo_product = (Button) findViewById(R.id.Today_Combo_Addproduct);
        pickImage = (ImageView) findViewById(R.id.pick_image_icon);
        TodayComboImage = (ImageView) findViewById(R.id.Today_Comboimage);
        editImageIcon = (ImageView) findViewById(R.id.edit_image_icon);
        Today_combo_isempty_txt = (TextView) findViewById(R.id.Today_combo_isempty_txt);

        recyclerView = findViewById(R.id.Today_offer_product_R1);
        recyclerView.addItemDecoration(new
                DividerItemDecoration(AddOfferActivity.this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image = 1;
                CropImage.activity(imageUri).setAspectRatio(3, 1)
                        .start(AddOfferActivity.this);
            }
        });


        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });


        editImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image = 2;
                CropImage.activity(imageUri).setAspectRatio(3, 1)
                        .start(AddOfferActivity.this);
            }

        });

        add_combo_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAllDatePresent();
            }
        });


        combo_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().equalsIgnoreCase("")) {
                    if (combo_price.getText().toString().trim().length() > 5) {
                        combo_price.setError("cannot exceed 5 digit");
                    } else if (combo_discount.getText().toString().trim().length() > 5) {
                        combo_discount.setError("cannot exceed 5 digit");
                    } else {
                        setSellingPrice();
                    }
                } else {
                    result = String.valueOf(price - 0);
                    selling_price.setText("selling price : "+ productType.notation+" "+ result);
                }
            }
        });

    }

    private void validateAllDatePresent(){
        if(!isImageAlreadyPresent){
            Toast.makeText(AddOfferActivity.this, "add image", Toast.LENGTH_SHORT).show();
        } else if (editImagePicked) {
            Toast.makeText(this, "save changes", Toast.LENGTH_SHORT).show();
        } else if (imagePicked) {
            Toast.makeText(this, "update product", Toast.LENGTH_SHORT).show();
//        } else if (!combo_name.getText().toString().trim().equals(Cname) ||
//                !combo_price.getText().toString().trim().equals(Cprice) ||
//                !combo_discount.getText().toString().trim().equals(Cdiscount)) {
//            Toast.makeText(this, "save changes", Toast.LENGTH_SHORT).show();
        }else if (combo_name.getText().toString().trim().equals("")) {
            Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();
        } else if (combo_price.getText().toString().trim().equals("")) {
            Toast.makeText(this, "enter price", Toast.LENGTH_SHORT).show();
        } else if (combo_discount.getText().toString().trim().equals("")) {
            Toast.makeText(this, "enter discount", Toast.LENGTH_SHORT).show();
        } else if (combo_price.getText().toString().trim().length() > 5) {
            combo_price.setError("cannot exceed 5 digit");
        } else if (combo_discount.getText().toString().trim().length() > 5) {
            combo_discount.setError("cannot exceed 5 digit");
        }else if (comb0_avail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "enter available", Toast.LENGTH_SHORT).show();
        } else {
            ComboDialogBox comboDialogBox = new ComboDialogBox();
            comboDialogBox.show(getSupportFragmentManager(), "combo dialog box");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showComboImage();

    }

    private void showComboImage() {


        Today_offer_Dateref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Todayoffer").exists()) {
                    if(dataSnapshot.child("Todayoffer").child("product").exists()){
                        getProductList();
                        Today_combo_isempty_txt.setTextColor(getResources().getColor(R.color.green));
                        Today_combo_isempty_txt.setText("no of products in combo : "+
                                dataSnapshot.child("Todayoffer").child("product").getChildrenCount());
                    }
                    combo_name.setText(dataSnapshot.child("Todayoffer").child("cname").getValue().toString());
                    combo_price.setText(dataSnapshot.child("Todayoffer").child("cprice").getValue().toString());
                    combo_discount.setText(dataSnapshot.child("Todayoffer").child("cdiscount").getValue().toString());
                    selling_price.setText(
                            "selling price : RM " + dataSnapshot.child("Todayoffer").child("csellp").getValue().toString());
                    if(!editImagePicked){
                        Picasso.get().load(dataSnapshot.child("Todayoffer").child("image").getValue().toString()).into(TodayComboImage);
                    }
                    comb0_avail.setText(dataSnapshot.child("Todayoffer").child("cavailable").getValue().toString());
                    oldImagePid = dataSnapshot.child("Todayoffer").child("pid").getValue().toString();
                    pickImage.setVisibility(View.GONE);
                    isImageAlreadyPresent = true;
                    editImageIcon.setVisibility(View.VISIBLE);
                    upload_image.setText("save changes");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getProductList() {

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(Today_offer_Dateref.child("Todayoffer")
                        .child("product"),Products.class).build();

        FirebaseRecyclerAdapter<Products , offerProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, offerProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull offerProductViewHolder holder, int i, @NonNull final Products products) {

                        holder.title.setText(products.getName()+" : "+products.getQuantity()+products.getKgmglml());
                        Picasso.get().load(products.getProducttype()).into(holder.productType);
                        holder.deletePoduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Today_offer_Dateref.child("Todayoffer").child("product").child(products.getPid()).removeValue();
                                notifyDataSetChanged();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public offerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.show_offer_layout, parent,false);
                        return new offerProductViewHolder(view);
                    }
                };


        recyclerView.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView,false);
        adapter.startListening();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null) {
            switch (image)
            {
                case 1:{
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    imageUri = result.getUri();
                    pickImage.setVisibility(View.GONE);
                    imagePicked = true;
                    TodayComboImage.setImageURI(imageUri);
                    break;
                }
                case 2:{
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    imageUri = result.getUri();
                    editImagePicked = true;
                    pickImage.setVisibility(View.GONE);
                    TodayComboImage.setImageURI(imageUri);
                    break;
                }
            }
        }
    }

    private void setSellingPrice() {
        price = Integer.parseInt(combo_price.getText().toString().trim());
        discount = Integer.parseInt(combo_discount.getText().toString().trim());
        Integer sub = price - (discount + 0);
        if (sub < 0) {
            combo_discount.setError("discount cannot greater than price");
        } else {
            result = String.valueOf(sub);
            selling_price.setText("selling price : " +productType.notation+" "+ result);
        }
    }

    private void validateData() {
        validateText();
    }


    private void validateText() {
        if (combo_name.getText().toString().trim().equals("")) {
            Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();
        } else if (combo_price.getText().toString().trim().equals("")) {
            Toast.makeText(this, "enter price", Toast.LENGTH_SHORT).show();
        } else if (combo_discount.getText().toString().trim().equals("")) {
            Toast.makeText(this, "enter discount", Toast.LENGTH_SHORT).show();
        } else if (combo_price.getText().toString().trim().length() > 5) {
            combo_price.setError("cannot exceed 5 digit");
        } else if (combo_discount.getText().toString().trim().length() > 5) {
            combo_discount.setError("cannot exceed 5 digit");
        }else if (comb0_avail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "enter available", Toast.LENGTH_SHORT).show();
        } else {
            setSellingPrice();
            if (isImageAlreadyPresent && !editImagePicked) {
                uploadOnlyText();
            } else if (isImageAlreadyPresent && editImagePicked) {
                uploadImage();
                productRandomKey = oldImagePid;
            } else if (imagePicked) {
                uploadImage();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
                currentDate = currentdate.format(calendar.getTime());
                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
                currentTime = currenttime.format(calendar.getTime());
                productRandomKey = currentDate + currentTime;
            } else {
                Toast.makeText(AddOfferActivity.this, "add image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadOnlyText() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cname", combo_name.getText().toString().trim());
        map.put("cprice", combo_price.getText().toString().trim());
        map.put("cdiscount", combo_discount.getText().toString().trim());
        map.put("cavailable", comb0_avail.getText().toString().trim());
        map.put("csellp", result);
        Today_offer_Dateref.child("Todayoffer").updateChildren(map);
        Toast.makeText(AddOfferActivity.this, "saved", Toast.LENGTH_SHORT).show();
    }

    private void uploadImage() {
        progressBar.setMessage("Uploading your product... ");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();

        StorageReference refImage = FirebaseStorage.getInstance().getReference()
                .child("product image").child(shopPrevalent.current_shop.getShop_name());
        final StorageReference filePath = refImage
                .child("offerProduct" + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    Uri downloadUrl = (Uri) task.getResult();
                    myUrl = downloadUrl.toString();
                    progressBar.dismiss();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("cname", combo_name.getText().toString().trim());
                    map.put("cprice", combo_price.getText().toString().trim());
                    map.put("cdiscount", combo_discount.getText().toString().trim());
                    map.put("cavailable", comb0_avail.getText().toString().trim());
                    map.put("csellp", result);
                    map.put("pid", productRandomKey);
                    map.put("image", myUrl);
                    Today_offer_Dateref.child("Todayoffer").updateChildren(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(AddOfferActivity.this, "added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddOfferActivity.this, AddOfferActivity.class));
                                    finish();
                                }
                            });


                } else {
                    progressBar.dismiss();
                    Toast.makeText(AddOfferActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

   private class offerProductViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private ImageView productType , deletePoduct;

       public offerProductViewHolder(@NonNull View itemView) {
           super(itemView);
           title = itemView.findViewById(R.id.offerProductTitile);
           productType = itemView.findViewById(R.id.offerProductType);
           deletePoduct = itemView.findViewById(R.id.offerProductDelete);
       }
   }

}
