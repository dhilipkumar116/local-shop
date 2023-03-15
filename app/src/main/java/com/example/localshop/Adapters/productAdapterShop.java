package com.example.localshop.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localshop.Prevalent.productType;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.example.localshop.modelClass.Products;
import com.example.localshop.shopActivity.AddProductActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class productAdapterShop extends RecyclerView.Adapter<productAdapterShop.myviewHolder> {


    private Context context;
    private ArrayList<Products> productList;




    public productAdapterShop(Context context, ArrayList<Products> product_List) {
        this.context = context;
        this.productList = product_List;
    }


    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_product_layout, parent, false);
        myviewHolder myviewHolder = new myviewHolder(view);
        return myviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final myviewHolder holder, final int position) {
        holder.admintxtproductname.setText(productList.get(position).getName() + " : "
                + productList.get(position).getQuantity() + productList.get(position).getKgmglml());
        holder.admintxtproductprice.setText("MRP : "+productType.notation+" "+ productList.get(position).getPrice());
        holder.admintxtproductprice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.admintxtnofoproducts.setText("available : " + productList.get(position).getNoofitem());
        holder.admintxtsellingprice.setText("price: "+productType.notation+" "+ productList.get(position).getSelling());
        holder.admindiscount.setText("save : "+productType.notation+" "+ productList.get(position).getDiscount());
        Picasso.get().load(productList.get(position).getProducttype()).into(holder.producttypeimage);
        Picasso.get().load(productList.get(position).getImage()).into(holder.adminimageView);
        holder.editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddProductActivity.class);
                intent.putExtra("category", productList.get(position).getCategory());
                intent.putExtra("pid", productList.get(position).getPid());
                context.startActivity(intent);
            }
        });
        holder.removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
                        .child("Admins").child(shopPrevalent.current_shop.getShop_name()).child("products");
                final StorageReference productImageRef = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(productList.get(position).getImage());
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove this product ?");
                builder.setCancelable(true);
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        productImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "deleted ", Toast.LENGTH_SHORT).show();
                                productRef.child(productList.get(position).getPid())
                                        .removeValue();
                            }
                        });
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateproductlist(ArrayList<Products> productFiltered) {
           productList = new ArrayList<>();
           productList.addAll(productFiltered);
           notifyDataSetChanged();
    }


    public class myviewHolder extends RecyclerView.ViewHolder {

        public TextView admintxtproductname, admintxtproductprice, admintxtnofoproducts,
                admintxtsellingprice, admindiscount, admin_sample;
        public ImageView adminimageView, producttypeimage;
        public Button editbtn, removebtn;


        public myviewHolder(@NonNull View itemView) {
            super(itemView);

            adminimageView = (ImageView) itemView.findViewById(R.id.admin_Product_Image);
            admintxtproductname = (TextView) itemView.findViewById(R.id.admin_product_Name);
            admintxtproductprice = (TextView) itemView.findViewById(R.id.admin_product_Price);
            admintxtnofoproducts = (TextView) itemView.findViewById(R.id.admin_product_Quantity);
            admintxtsellingprice = (TextView) itemView.findViewById(R.id.admin_selling_price);
            admindiscount = (TextView) itemView.findViewById(R.id.admin_discount);
            editbtn = (Button) itemView.findViewById(R.id.admin_editproductbtn);
            removebtn = (Button) itemView.findViewById(R.id.admin_removeproductbtn);
            producttypeimage = (ImageView) itemView.findViewById(R.id.admin_product_Type);
        }
    }




}
