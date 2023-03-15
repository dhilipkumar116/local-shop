package com.example.localshop.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.localshop.Prevalent.productType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.example.localshop.modelClass.Products;

import java.util.ArrayList;
import java.util.HashMap;

public class adminAddOrderAdapter extends RecyclerView.Adapter<adminAddOrderAdapter.myViewHolder> {

    Context context;
    ArrayList<Products> productsArrayList;
    private int getnoofitem = 1, noofpdt_avail,sellprice;
    private boolean isElegantbtnClicked = false;
    private ProgressDialog progressDialog;


    public adminAddOrderAdapter(Context context, ArrayList<Products> productsArrayList) {
        this.context = context;
        this.productsArrayList = productsArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_order_layout, parent, false);
        myViewHolder myviewHolder = new myViewHolder(view);
        return myviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

//        DatabaseReference orderList = FirebaseDatabase.getInstance().getReference()
//                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
//        orderList.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//             if(snapshot.child("orderProduct").child(productsArrayList.get(position).getPid()).exists()) {
//                 holder.checkBox.setChecked(true);
//                 progressDialog.dismiss();
//             }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        holder.name.setText("Name : "+productsArrayList.get(position).getName()+" : "
                +productsArrayList.get(position).getQuantity()+productsArrayList.get(position).getKgmglml());
        holder.price.setText("Price : "+productType.notation+" "+productsArrayList.get(position).getSelling());

        holder.disableCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "no of product = 0", Toast.LENGTH_SHORT).show();
            }
        });
        holder.unavailableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"product not available",Toast.LENGTH_SHORT).show();
            }
        });
        if(Integer.valueOf(productsArrayList.get(position).getNoofitem()) ==0){
            holder.unavailableLayout.setVisibility(View.VISIBLE);
        }
        holder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                if(holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                }
                getnoofitem = Integer.parseInt(holder.elegantNumberButton.getNumber());
                sellprice = Integer.parseInt(productsArrayList.get(position).getSelling()) * getnoofitem;
                isElegantbtnClicked = true;
                if (Integer.parseInt(productsArrayList.get(position).getNoofitem()) - getnoofitem - 1 < 0) {
                    holder.disableCountLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.disableCountLayout.setVisibility(View.INVISIBLE);
                }
                if (Integer.parseInt(productsArrayList.get(position).getNoofitem()) > 0) {
                    noofpdt_avail = Integer.parseInt(productsArrayList.get(position).getNoofitem()) - getnoofitem;
                    holder.price.setText("Price : "+productType.notation+" "+ sellprice);
                }
            }
        });
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //to date to db
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("adding...");
                    progressDialog.show();


                    final HashMap<String, Object> cartproductdetials = new HashMap<>();
                    cartproductdetials.put("pid", productsArrayList.get(position).getPid());
                    cartproductdetials.put("pname", productsArrayList.get(position).getName());
                    cartproductdetials.put("selling_price", holder.price.getText().toString());
                    cartproductdetials.put("no_of_product", holder.elegantNumberButton.getNumber());
                    cartproductdetials.put("image", productsArrayList.get(position).getImage());
                    if (isElegantbtnClicked) {
                        cartproductdetials.put("sellP", Integer.parseInt(productsArrayList.get(position).getSelling())
                                *Integer.valueOf(holder.elegantNumberButton.getNumber()));
                    } else {
                        cartproductdetials.put("sellP", Integer.parseInt(productsArrayList.get(position).getSelling()));
                    }
                    orderRef.child("orderProduct").child(productsArrayList.get(position).getPid())
                            .updateChildren(cartproductdetials)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                        }
                                }
                            });

                }else {

                    orderRef.child("orderProduct").child(productsArrayList.get(position).getPid())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }
    public void updateproductlist(ArrayList<Products> productFiltered) {
        productsArrayList = new ArrayList<>();
        productsArrayList.addAll(productFiltered);
        notifyDataSetChanged();
    }
    public class myViewHolder extends RecyclerView.ViewHolder{

        private TextView name,price;
        private CheckBox checkBox;
        private ElegantNumberButton elegantNumberButton;
        private LinearLayout disableCountLayout,unavailableLayout;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.P_name);
            price = itemView.findViewById(R.id.P_price);
            checkBox = itemView.findViewById(R.id.addOrder);
            elegantNumberButton = itemView.findViewById(R.id.countBtn);
            disableCountLayout = itemView.findViewById(R.id.disable_count_btn_layout);
            unavailableLayout = itemView.findViewById(R.id.UnavailLayout);
        }
    }
}
