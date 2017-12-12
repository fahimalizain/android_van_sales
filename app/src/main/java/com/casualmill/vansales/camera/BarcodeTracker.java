package com.casualmill.vansales.camera;

import android.content.Context;
import android.support.annotation.UiThread;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by faztp on 12-Dec-17.
 */

public class BarcodeTracker extends Tracker<Barcode> {


    private BarcodeUpdateListener mBarcodeUpdateListener;

    /**
     * Consume the item instance detected from an Activity or Fragment level by implementing the
     * BarcodeUpdateListener interface method onBarcodeDetected.
     */
    public interface BarcodeUpdateListener {
        @UiThread
        void onBarcodeDetected(Barcode barcode);
    }

    public BarcodeTracker(Context context) {
        if (context instanceof BarcodeUpdateListener) {
            this.mBarcodeUpdateListener = (BarcodeUpdateListener) context;
        } else {
            throw new RuntimeException("Hosting activity must implement BarcodeUpdateListener");
        }
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, Barcode item) {
        Log.e("TrACKER", "DETECTED");
        mBarcodeUpdateListener.onBarcodeDetected(item);
    }
}
