package com.sadashivsinha.scanandgo.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sadashivsinha.scanandgo.Listeners.FragmentCartTotalListener;
import com.sadashivsinha.scanandgo.R;
import com.sadashivsinha.scanandgo.Repositories.Room.CartModel;

import java.util.List;

/**
 * Created by sadashivsinha on 21/07/18.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<CartModel> cartModelList;
    private Context context;

    FragmentCartTotalListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemPrice, itemQuantity;
        public ImageView btnMinus, btnPlus;
        public TextView textId;

        public MyViewHolder(View view) {
            super(view);

            context = view.getContext();

            textId = view.findViewById(R.id.text_id);

            itemName = view.findViewById(R.id.item_name);
            itemPrice = view.findViewById(R.id.item_price);
            itemQuantity = view.findViewById(R.id.item_quantity);

            btnMinus = view.findViewById(R.id.btn_minus);
            btnPlus = view.findViewById(R.id.btn_plus);

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentQuantity = Integer.parseInt(itemQuantity.getText().toString());
                    currentQuantity--;

                    if (currentQuantity > 0) {

                        itemQuantity.setText(String.valueOf(currentQuantity));

                        listener.onValuesChanged(Long.valueOf(textId.getText().toString())
                                , itemName.getText().toString()
                                , Integer.parseInt(itemQuantity.getText().toString()), false);

                    }
                    else if(currentQuantity==0){
                        final int currentQuantityValue = currentQuantity;
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
                        alertbox.setMessage("Do you want to remove this item from cart?");
                        alertbox.setTitle("Confirm Remove");

                        alertbox.setNeutralButton("OK",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        itemQuantity.setText(String.valueOf(currentQuantityValue));

                                        listener.onValuesDeleted(Long.valueOf(textId.getText().toString())
                                                , itemName.getText().toString()
                                                , Integer.parseInt(itemQuantity.getText().toString()));
                                    }
                                });
                        alertbox.show();
                    }
                    else
                        Toast.makeText(context, "Quantity cannot be less than 0.", Toast.LENGTH_SHORT).show();
                }
            });

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentQuantity = Integer.parseInt(itemQuantity.getText().toString());
                    currentQuantity++;
                    itemQuantity.setText(String.valueOf(currentQuantity));

                    listener.onValuesChanged(Long.valueOf(textId.getText().toString())
                            , itemName.getText().toString()
                            , Integer.parseInt(itemQuantity.getText().toString()), true);


                }
            });

        }
    }


    public CartAdapter(FragmentCartTotalListener listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_cart, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        CartModel model = cartModelList.get(position);
        holder.textId.setText(String.valueOf(model.getId()));
        holder.itemName.setText(model.getItemName());
        holder.itemQuantity.setText(String.valueOf(model.getItemQuantity()));

    }

    public void setCartItems(List<CartModel> cartModelList) {
        this.cartModelList = cartModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (cartModelList != null)
            return cartModelList.size();
        else return 0;
    }
}