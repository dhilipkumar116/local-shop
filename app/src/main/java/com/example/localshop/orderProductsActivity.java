package com.example.localshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.modelClass.Carts;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class orderProductsActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DatabaseReference orderproductref;
    private String userId, orderId, from, shopname;
    private Button confirmOrderBtn, deliverycall;
    private DatabaseReference cartenableref, changeorderstate_admin,
            changeorderstate_user, notificaionref;
    private CardView delivery_cardView;
    private LinearLayout delivery_layout;
    private TextView deliName, deliPhone, deliVnum, deliVtype;
    private ProgressDialog progressDialog;
    private String deliveryGuyPhno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        userId = getIntent().getStringExtra("userId");
        orderId = getIntent().getStringExtra("orderId");
        from = getIntent().getStringExtra("from");
        shopname = getIntent().getStringExtra("username");


        recyclerView = findViewById(R.id.order_productList_RecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        confirmOrderBtn = findViewById(R.id.confirm_order_btn);
        delivery_cardView = findViewById(R.id.delivery_cardView);
        delivery_layout = findViewById(R.id.delivery_layout);

        deliName = findViewById(R.id.delivery_name);
        deliPhone = findViewById(R.id.delivery_phone);
        deliverycall = findViewById(R.id.delivery_call);
        deliVnum = findViewById(R.id.delivery_vehicleNum);
        deliVtype = findViewById(R.id.delivery_vehicleType);


        deliverycall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makecall = new Intent(Intent.ACTION_DIAL);
                makecall.setData(Uri.parse("tel:" + deliveryGuyPhno));
                startActivity(makecall);
            }
        });


        orderproductref = FirebaseDatabase.getInstance().getReference().child("orders_ID").child(orderId);
        notificaionref = FirebaseDatabase.getInstance().getReference().child("notification");
        cartenableref = FirebaseDatabase.getInstance().getReference().child("cart_list")
                .child("checkcartenable").child(userId).child(shopname);
        changeorderstate_admin = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopname)
                .child("Orders").child(orderId);
        changeorderstate_user = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userId)
                .child("orders").child(orderId);


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (confirmOrderBtn.getText().toString().equals("confirm")) {
                    final String status = "confirmed";
                    final String message = " your order is confirmed by the shop";
                    AlertDialog.Builder builder = new AlertDialog.Builder(orderProductsActivity.this);
                    builder.setMessage("Are you sure to confirm this order ?");
                    builder.setCancelable(true);

                    builder.setPositiveButton("accept order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.show();
                            uploadSoldItems();
                            changeorderstatus(status, message);
                            confirmOrderBtn.setText("is this bulk order");
                        }
                    });
                    builder.setNegativeButton("cancel order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelorder();
                        }

                    });
                    builder.show();
                }else if(confirmOrderBtn.getText().toString().equals("is this bulk order")){
                    bulkOrderOrNot();

                }else if (confirmOrderBtn.getText().toString().equals("ready for delivery")) {
                    readyForDelivery();
                }

            }
        });

    }


    private void uploadSoldItems() {
        DatabaseReference orderproRef = FirebaseDatabase.getInstance().getReference()
                .child("orders_ID").child(orderId);
        orderproRef.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot list : dataSnapshot.getChildren()) {
                    Carts carts = list.getValue(Carts.class);
                    final int soldnow = Integer.parseInt(carts.getNo_of_product());
                    final String pid = carts.getPid();
                    final DatabaseReference proRef = FirebaseDatabase.getInstance().getReference()
                            .child("Admins").child(shopname).child("products");
                    proRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(pid).exists()) {
                                int alreadySold = Integer.parseInt(dataSnapshot.child(pid).child("sold").getValue().toString());
                                int curravailable = Integer.parseInt(dataSnapshot.child(pid).child("noofitem").getValue().toString());
                                int totalsold = alreadySold + soldnow;
                                int newavailable ;
                                if(curravailable > 0 && curravailable > curravailable-soldnow){
                                     newavailable = curravailable - soldnow;
                                }else {
                                    newavailable = 0;
                                }
                                proRef.child(pid).child("sold").setValue(totalsold).isSuccessful();
                                proRef.child(pid).child("noofitem").setValue(String.valueOf(newavailable)).isSuccessful();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void bulkOrderOrNot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(orderProductsActivity.this);
        builder.setMessage("Is this is Bulk Order?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                orderproductref.child("type").setValue("bulk").isSuccessful();
                changeorderstate_admin.child("type").setValue("bulk").isSuccessful();
                changeorderstate_user.child("type").setValue("bulk").isSuccessful();
                confirmOrderBtn.setText("ready for delivery");
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                orderproductref.child("type").setValue("normal").isSuccessful();
                changeorderstate_admin.child("type").setValue("normal").isSuccessful();
                changeorderstate_user.child("type").setValue("normal").isSuccessful();
                confirmOrderBtn.setText("ready for delivery");
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void readyForDelivery() {
        final String status = "ready for delivery";
        final String message = "your order is packed by the shop!! , waiting for delivery.  ";
        AlertDialog.Builder builder = new AlertDialog.Builder(orderProductsActivity.this);
        builder.setMessage("do you finshed packing these products and ready to deliver this order ?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                changeorderstatus(status, message);
                confirmOrderBtn.setVisibility(View.GONE);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void cancelorder() {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(orderProductsActivity.this);
        builder1.setTitle("CANCEL ORDER:");
        builder1.setMessage("Are you sure to cancel this order ?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String status = "cancelled";
                final String message = "your order has been cancelled by the shop , to get reason please contact shop ";
                changeorderstatus(status, message);
            }
        });
        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder1.show();

    }

    private void changeorderstatus(final String status, final String message) {

        cartenableref.child("status").setValue(status).isSuccessful();
        changeorderstate_user.child("status").setValue(status).isSuccessful();
        orderproductref.child("status").setValue(status).isSuccessful();
        orderproductref.child("shoplat").setValue(shopPrevalent.current_shop.getLatitude()).isSuccessful();
        orderproductref.child("shoplang").setValue(shopPrevalent.current_shop.getLongtitude()).isSuccessful();
        changeorderstate_admin.child("status").setValue(status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            HashMap<String, String> notification = new HashMap<>();
                            notification.put("from", shopPrevalent.current_shop.getShop_name()
                                    +" "+shopPrevalent.current_shop.getCategory());
                            notification.put("info", message);
                            notificaionref.child(userId).push().setValue(notification)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(orderProductsActivity.this,
                                                        "you " + status + " this order", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(orderProductsActivity.this,
                                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(orderProductsActivity.this,
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        checkIsOrderCanceledOrNot();
        FirebaseRecyclerOptions<Carts> options =
                new FirebaseRecyclerOptions.Builder<Carts>()
                        .setQuery(orderproductref.child("products"), Carts.class)
                        .build();

        FirebaseRecyclerAdapter<Carts, myViewHolder> adapter =
                new FirebaseRecyclerAdapter<Carts, myViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myViewHolder holder, int i, @NonNull Carts carts) {
                        holder.title.setText(carts.getPname());
                        holder.price.setText(carts.getSelling_price());
                        Picasso.get().load(carts.getImage()).into(holder.image);
                        holder.noofpdt.setText("no of items : " + carts.getNo_of_product());
                    }

                    @NonNull
                    @Override
                    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.wishlist_layout, parent, false);
                        myViewHolder holder = new myViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private void checkIsOrderCanceledOrNot() {


        //___________-- to check wheather order cancelled
        changeorderstate_admin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("status").getValue().equals("cancelled")) {
                    confirmOrderBtn.setVisibility(View.GONE);
                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(orderProductsActivity.this);
                    builder2.setTitle("ORDER CANCELLED:");
                    builder2.setMessage("this order has been already cancelled !!");
                    builder2.setCancelable(true);
                    builder2.show();
                }
                if (dataSnapshot.child("status").getValue().equals("confirmed")) {
                    confirmOrderBtn.setText("is this bulk order");
                }
                if(dataSnapshot.child("type").exists()&&dataSnapshot.child("status").getValue().equals("confirmed")){
                    confirmOrderBtn.setText("ready for delivery");
                }
                if (dataSnapshot.child("status").getValue().equals("ready for delivery")) {
                    confirmOrderBtn.setVisibility(View.GONE);
                }
                if (dataSnapshot.child("status").getValue().equals("picked up")) {
                    confirmOrderBtn.setVisibility(View.GONE);
                    delivery_layout.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child("status").getValue().equals("on the way")) {
                    confirmOrderBtn.setVisibility(View.GONE);
                    delivery_layout.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child("status").getValue().equals("delivered")) {
                    confirmOrderBtn.setVisibility(View.GONE);
                    delivery_layout.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child("payment").getValue().toString().equals("COD")) {
                    if (dataSnapshot.child("deliveryID").exists()) {
                        getdeliveryGuy(dataSnapshot.child("deliveryID").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getdeliveryGuy(String deliveryID) {
        final DatabaseReference deliveryRef = FirebaseDatabase.getInstance().getReference()
                .child("delivery_account").child(deliveryID);
        deliveryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliName.setText("name : " + dataSnapshot.child("username").getValue().toString());
                deliPhone.setText("ph no : " + dataSnapshot.child("phno").getValue().toString());
                deliVnum.setText("vehicle num : " + dataSnapshot.child("vehicleNum").getValue().toString());
                deliVtype.setText("vehicle type : " + dataSnapshot.child("vehicleType").getValue().toString());
                deliveryGuyPhno = dataSnapshot.child("phno").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public class myViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title, noofpdt, price;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Cart_product_NameQuantity);
            price = itemView.findViewById(R.id.Cart_product_Price);
            noofpdt = itemView.findViewById(R.id.Cart_product_Noofproduct);
            image = itemView.findViewById(R.id.wishlist_image_lay);
        }
    }

}
