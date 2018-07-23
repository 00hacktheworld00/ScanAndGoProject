package com.sadashivsinha.scanandgo.Fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import com.sadashivsinha.scanandgo.Listeners.FragmentCartValueListener;
import com.sadashivsinha.scanandgo.R;
import com.sadashivsinha.scanandgo.Repositories.Room.CartModel;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by sadashivsinha on 21/07/18.
 */

public class ScanFragment extends Fragment implements ZXingScannerView.ResultHandler {

    Context context;
    ZXingScannerView qrScanner;
    FirebaseAuth auth;
    String userId;

    FragmentCartValueListener mCallback;
    final static int DELAY_IN_SECONDS = 1000;
    private long lastTimestamp = 0;

    Vibrator vibrator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (FragmentCartValueListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        context = view.getContext();

        qrScanner = view.findViewById(R.id.qr_scanner);

        vibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);

        setupFirebase();

        return view;
    }

    private void setupFirebase() {

        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();
    }

    @Override
    public void onResume() {
        super.onResume();
        qrScanner.setResultHandler(this); // Register ourselves as a handler for scan results.
        qrScanner.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        qrScanner.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        qrScanner.resumeCameraPreview(ScanFragment.this);

        if (rawResult.getText() != null) {
            MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.sound_effect_beep);
            mp.start();
            vibrator.vibrate(200);

            if (System.currentTimeMillis() - lastTimestamp > DELAY_IN_SECONDS) {
                Log.d("Scanner result:", rawResult.toString());

                addItemToFirebaseDB(rawResult.toString(), 1);

                mCallback.onItemScanned();

//        context.startActivity(new Intent(context, CartActivity.class));
            }

            lastTimestamp = System.currentTimeMillis();
        }
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    private void addItemToFirebaseDB(String itemName, int itemQuantity) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("cartItems");
        long id = System.currentTimeMillis();

        CartModel cartItem = new CartModel();
        cartItem.setItemName(EncodeString((itemName)));
        cartItem.setItemQuantity(itemQuantity);
        cartItem.setId(id);
        mDatabase.child(userId).child(String.valueOf(id)).setValue(cartItem);

    }
}
