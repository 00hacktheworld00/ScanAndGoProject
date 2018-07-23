package com.sadashivsinha.scanandgo.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sadashivsinha.scanandgo.Adapters.CartAdapter;
import com.sadashivsinha.scanandgo.Listeners.FragmentCartTotalListener;
import com.sadashivsinha.scanandgo.R;
import com.sadashivsinha.scanandgo.Repositories.Room.CartModel;
import com.sadashivsinha.scanandgo.ViewModel.CartViewModel;

import java.util.HashMap;
import java.util.List;

public class CartActivity extends AppCompatActivity implements FragmentCartTotalListener {

    private CartAdapter mAdapter;
    RecyclerView recyclerViewCart;
    String userId;

    Boolean initialLoad = true;

    FirebaseAuth auth;

    TextView textCartTotal;
    TextView btnCheckout;

    CartViewModel mCartViewModel;
    RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setTitle("Cart");
        auth = FirebaseAuth.getInstance();

        findViewsByIds();
        setupRecyclerView();
        setupViewModel();
        prepareCartData();
    }

    public void findViewsByIds() {
        recyclerViewCart = findViewById(R.id.recycler_view);
        textCartTotal = findViewById(R.id.text_cart_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        loadingLayout = findViewById(R.id.layout_loading);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceedToPayments();
            }
        });

    }

    public void setupRecyclerView() {
        mAdapter = new CartAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewCart.setLayoutManager(mLayoutManager);
        recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCart.setAdapter(mAdapter);
    }

    public void setupViewModel() {
        mCartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);

        mAdapter.setCartItems(mCartViewModel.getAllCartItems().getValue());

        mCartViewModel.getAllCartItems().observe(this, new Observer<List<CartModel>>() {
            @Override
            public void onChanged(@Nullable final List<CartModel> cartItems) {
                mAdapter.setCartItems(cartItems);
            }
        });
    }

    private void prepareCartData() {
        userId = auth.getUid();
        loadingScreen(true);

        FirebaseDatabase.getInstance().getReference()
                .child("cartItems")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (initialLoad) {
                            mCartViewModel.removeAllItems();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                String CURRENT_KEY = dataSnapshot1.getKey();

                                CartModel cartItem = dataSnapshot.child(CURRENT_KEY).getValue(CartModel.class);

                                CartModel cartModel = new CartModel();
                                cartModel.setId(cartItem.getId());
                                cartModel.setItemName(String.valueOf(cartItem.getItemName()));
                                cartModel.setItemQuantity(cartItem.getItemQuantity());
                                mCartViewModel.insert(cartModel);


                                textCartTotal.setText(String.valueOf(Integer.parseInt(textCartTotal.getText().toString()) + (100 * cartItem.getItemQuantity())));

                            }
                            initialLoad = false;
                        }
                        loadingScreen(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {/*Do Nothing*/}
                });
    }

    public void updateMainCartAmount(Boolean add, int quantity) {
        if (add) {
            textCartTotal.setText(String.valueOf(Integer.parseInt(textCartTotal.getText().toString()) + (100 * quantity)));
        } else {
            textCartTotal.setText(String.valueOf(Integer.parseInt(textCartTotal.getText().toString()) - (100 * quantity)));
        }
    }

    @Override
    public void onValuesChanged(long id, String itemName, int quantity, Boolean addingQuantity) {

        if (addingQuantity) {
            updateMainCartAmount(true, 1);
        } else {
            updateMainCartAmount(false, 1);
        }

        try {
            HashMap<String, Object> result = new HashMap<>();
            CartModel cartItem = new CartModel();
            cartItem.setId(id);
            cartItem.setItemQuantity(quantity);
            cartItem.setItemName(itemName);
            Log.d("Update Item : ", String.valueOf(cartItem.getId()));
            result.put("id", id);
            result.put("itemName", itemName);
            result.put("itemQuantity", quantity);

            mCartViewModel.updateItem(cartItem);

            FirebaseDatabase.getInstance().getReference()
                    .child("cartItems")
                    .child(userId)
                    .child(String.valueOf(cartItem.getId())).setValue(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onValuesDeleted(long id, String itemName, int quantity) {
        try {
            HashMap<String, Object> result = new HashMap<>();
            CartModel cartItem = new CartModel();
            cartItem.setId(id);
            cartItem.setItemQuantity(quantity);
            cartItem.setItemName(itemName);
            Log.d("Delete Item : ", String.valueOf(cartItem.getId()));
            result.put("id", id);
            result.put("itemName", itemName);
            result.put("itemQuantity", quantity);

            mCartViewModel.removeItem(cartItem);

            FirebaseDatabase.getInstance().getReference()
                    .child("cartItems")
                    .child(userId)
                    .child(String.valueOf(cartItem.getId())).removeValue();

            textCartTotal.setText(String.valueOf(Integer.parseInt(textCartTotal.getText().toString()) - (100)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void proceedToPayments() {
        DatabaseReference mDatabase;
        long id = System.currentTimeMillis();

        mDatabase = FirebaseDatabase.getInstance().getReference("purchases");

        mDatabase.child(userId).child(String.valueOf(id)).setValue(mCartViewModel.getAllCartItems());

        mDatabase = FirebaseDatabase.getInstance().getReference("cartItems");
        mDatabase.removeValue();

        startActivity(new Intent(CartActivity.this, PaymentActivity.class));
        finish();
    }

    public void loadingScreen(Boolean isLoading) {
        if (isLoading) {
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            loadingLayout.setVisibility(View.GONE);
        }
    }

}