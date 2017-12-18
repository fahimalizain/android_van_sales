package com.casualmill.vansales.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.casualmill.vansales.activities.LoadingActivity;
import com.casualmill.vansales.data.models.Item;
import com.casualmill.vansales.data.models.UOM;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by faztp on 16-Dec-17.
 */

public class SyncData extends AsyncTask<String, Void, Boolean> {

    private WeakReference<Context> ctx;

    public SyncData(Context ctx) {
        this.ctx = new WeakReference<>(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        LoadingActivity.Start(ctx.get());
    }

    @Override
    protected void onPostExecute(Boolean aBool) {
        super.onPostExecute(aBool);
        LoadingActivity.Stop(ctx.get());

        Toast.makeText(ctx.get(), aBool ? "Sync Successful" : "Sync Failed. Please try again.", Toast.LENGTH_SHORT).show();
        ctx = null;
    }

    @Override
    protected Boolean doInBackground(String... data) {
        try {
            /*
                data
                0 - address (10.0.2.2:18565)
                1 - vehicle_id
             */

            String[] details = data[0].split(":");
            Socket socket = new Socket(); // timeout 5seconds
            socket.connect(new InetSocketAddress(Inet4Address.getByName(details[0]), Integer.valueOf(details[1])), 5000);
            socket.setSoTimeout(5000);

            // Data In/Out Stream handles in Network byte Order (big endian)
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // identify
            JSONObject obj = new JSONObject();
            obj.put("vehicle_id", data[1]);
            sendData(out, obj);

            // receive latest info
            obj = receiveData(in);

            Log.e("JSON", (String) obj.getJSONArray("items").getJSONObject(0).get("item_code"));
            socket.close();

            AppDatabase.Instance.itemDao().DeleteAll();
            // Save to AppDatabase
            for (int i = 0; i < obj.getJSONArray("items").length(); i++) {
                JSONObject item_obj = obj.getJSONArray("items").getJSONObject(i);

                Item t = new Item();
                t.itemCode = item_obj.getString("item_code");
                t.itemName = item_obj.getString("item_name");
                t.barcode = item_obj.getString("barcode");
                t.default_uom = item_obj.getString("default_uom");
                t.stock_balance = (float) obj.getJSONObject("stock_details").getDouble(t.itemCode);

                AppDatabase.Instance.itemDao().Insert(t);
            }

            for (int i = 0; i <obj.getJSONArray("unit_and_price").length(); i++) {
                JSONObject uom_obj = obj.getJSONArray("unit_and_price").getJSONObject(i);
                UOM u = new UOM();
                u.unit_code = uom_obj.getString("unit_code");
                u.unit_name = uom_obj.getString("unit_name");
                u.item_code = uom_obj.getString("item_code");
                u.conversion_factor = (float) uom_obj.getDouble("conversion_factor");
                u.price = (float) uom_obj.getDouble("price");

                AppDatabase.Instance.uomDao().Insert(u);
            }

            return true;
        } catch (Exception e) {
            Log.e("SYNC", e.getMessage() != null ? e.getMessage() : "Unknown Error : " + e.toString());
            Log.e("SYNC", "Target Address : " + data[0]);
        }
        return false;
    }

    static JSONObject receiveData(DataInputStream in) throws Exception {
        int expectedSize = in.readInt();
        int bufferSize = 0;

        Thread.sleep(500);
        byte[] data = new byte[expectedSize];
        in.readFully(data, 0, expectedSize);
        /*
        while (bufferSize != -1 && receivedSize != expectedSize) {
            receivedSize += bufferSize;
            bufferSize = in.read(data, 0, expectedSize);
        }
        */

        return new JSONObject(getString(data));
    }

    static void sendData(DataOutputStream out, JSONObject obj) throws IOException {
        byte[] data = getBytes(obj.toString());
        out.writeInt(data.length);
        out.flush();
        out.write(data);
        out.flush();
    }

    static byte[] getBytes(String data) throws UnsupportedEncodingException {
        return data.getBytes("UTF-16BE");
    }

    static String getString(byte[] data) throws UnsupportedEncodingException {
        return new String(data, "UTF-16BE");
    }

    static void CheckSize(int expected, int received) throws Exception {
        if (expected != received)
            throw new Exception("Expected and Received num bytes does'nt match");
    }
}
