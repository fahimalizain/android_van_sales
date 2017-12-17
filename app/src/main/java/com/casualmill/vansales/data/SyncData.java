package com.casualmill.vansales.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.casualmill.vansales.activities.LoadingActivity;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Inet4Address;
import java.net.Socket;

/**
 * Created by faztp on 16-Dec-17.
 */

public class SyncData extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> ctx;
    private Intent loadingIntent;

    public SyncData(Context ctx) {
        this.ctx = new WeakReference<>(ctx);
        loadingIntent = new Intent(ctx, LoadingActivity.class);
        loadingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loadingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        loadingIntent.putExtra("keep", true);
        ctx.get().startActivity(loadingIntent);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        loadingIntent.putExtra("keep", false);
        ctx.get().startActivity(loadingIntent);

        Toast.makeText(ctx.get(), "Sync Successful", Toast.LENGTH_SHORT).show();
        ctx = null;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Socket socket = new Socket(Inet4Address.getByName("10.0.2.2"), 18565);

            // Data In/Out Stream handles in Network byte Order (big endian)
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // identify
            JSONObject obj = new JSONObject();
            obj.put("van_id", "VAN2145");
            sendData(out, obj);

            // receive latest info
            obj = receiveData(in);

            Log.e("JSON", (String) obj.getJSONArray("items").getJSONObject(0).get("item_code"));
            socket.close();
        } catch (Exception e) {
            Log.e("SYNC", e.getMessage() != null ? e.getMessage() : "Unknown Error : " + e.toString());
        }
        return null;
    }

    static JSONObject receiveData(DataInputStream in) throws Exception {
        int expectedSize = in.readInt();
        int bufferSize = 0;

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
