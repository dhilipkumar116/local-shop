package com.example.localshop.shopActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.localshop.Prevalent.productType;
import com.example.localshop.Prevalent.shopPrevalent;
import com.example.localshop.R;
import com.example.localshop.modelClass.Orders;
import com.example.localshop.orderProductsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewOrderActivity extends AppCompatActivity {

    private LinearLayout unavailable_new_order;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DatabaseReference userorderref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Admins").child(shopPrevalent.current_shop.getShop_name());
        userorderref=FirebaseDatabase.getInstance().getReference().child("Admins")
                .child(shopPrevalent.current_shop.getShop_name()).child("Orders");
        unavailable_new_order = findViewById(R.id.unavailable_new_order);
        recyclerView = findViewById(R.id.new_order_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        chechOrderAvailable(orderRef);
    }

    private void chechOrderAvailable(DatabaseReference orderRef) {

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Orders").exists()){
                    unavailable_new_order.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(userorderref,Orders.class)
                .build();

        FirebaseRecyclerAdapter<Orders,AdminorderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, AdminorderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminorderViewHolder holder,
                                                    int i, @NonNull final Orders orders) {
                        holder.userOrdername.setText("name : "+orders.getOrdername());
                        holder.userphone.setText("phone : "+orders.getPhone_number());
                        holder.usertotalprice.setText("total price : "+ productType.notation+orders.getTotprice());
                        holder.useraddress.setText("delivery address : "+orders.getAddress());
                        holder.userdate.setText("date : "+orders.getDate());
                        holder.usertime.setText("time : "+orders.getTime());
                        holder.userorderid.setText("orderID : "+orders.getID());
                        holder.userordertype.setText("order type : "+orders.getPayment());
                        if(orders.getStatus().equals("orderplaced")){
                            holder.orderstatus.setText("status : not confirmed");
                            holder.orderstatus.setTextColor(getResources().getColor(R.color.red));
                        }
                        if(orders.getStatus().equals("confirmed")){
                            holder.orderstatus.setText("status : confirmed");
                            holder.orderstatus.setTextColor(getResources().getColor(R.color.green));
                        }
                        if(orders.getStatus().equals("ready for delivery")){
                            holder.orderstatus.setText("status : waiting for delivery");
                            holder.orderstatus.setTextColor(getResources().getColor(R.color.green));
                        }
                        if(orders.getStatus().equals("picked up")){
                            holder.orderstatus.setText("status : delivery boy assigned");
                            holder.orderstatus.setTextColor(getResources().getColor(R.color.green));
                        }
                        if(orders.getStatus().equals("delivered")){
                            holder.deletetxt.setVisibility(View.VISIBLE);
                            holder.orderstatus.setText("status : delivered");
                            holder.orderstatus.setTextColor(getResources().getColor(R.color.green));
                        }
                        if(orders.getStatus().equals("on the way")){
                            holder.orderstatus.setText("status : on the way");
                            holder.orderstatus.setTextColor(getResources().getColor(R.color.green));
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(NewOrderActivity.this, orderProductsActivity.class);
                                intent.putExtra("userId",orders.getUserId());
                                intent.putExtra("username",orders.getShopname());
                                intent.putExtra("orderId" , orders.getID());
                                intent.putExtra("from" , "admin");
                                startActivity(intent);
                            }
                        });

                        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                                .child("Admins");

                        holder.deletetxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                orderRef.child(orders.getShopname()).child("Orders").child(orders.getID()).removeValue();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminorderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.neworder_layout,parent,false);
                        AdminorderViewHolder holder=new AdminorderViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class AdminorderViewHolder extends RecyclerView.ViewHolder
    {

        public TextView userOrdername,userphone,usertotalprice,useraddress
                ,userdate,usertime,userordertype,userorderid,orderstatus,deletetxt;

        public AdminorderViewHolder(@NonNull View itemView) {
            super(itemView);


            userorderid = itemView.findViewById(R.id.A_orderid);
            userOrdername=itemView.findViewById(R.id.A_ordername);
            userphone=itemView.findViewById(R.id.A_orderphone);
            usertotalprice=itemView.findViewById(R.id.A_ordertotalp);
            useraddress=itemView.findViewById(R.id.A_orderaddress);
            userdate=itemView.findViewById(R.id.A_orderdate);
            usertime=itemView.findViewById(R.id.A_ordertime);
            userordertype=itemView.findViewById(R.id.A_odertype);
            orderstatus=itemView.findViewById(R.id.A_oderstatus);
            deletetxt = itemView.findViewById(R.id.A_delete_ordertxt);

        }

    }
}
