package com.casualmill.vansales.camera;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by faztp on 12-Dec-17.
 */

public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private BarcodeTracker.BarcodeUpdateListener parent;
    public BarcodeTrackerFactory(BarcodeTracker.BarcodeUpdateListener mContext) {
        this.parent = mContext;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new BarcodeTracker(parent);
    }
}
