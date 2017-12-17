package com.casualmill.vansales.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Xml;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import com.casualmill.vansales.R;
import com.casualmill.vansales.activities.LoadingActivity;
import com.casualmill.vansales.activities.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;

/**
 * Created by faztp on 16-Dec-17.
 */

public class SyncData extends AsyncTask<Void, Void, Void> {

    private Context ctx;
    private Intent loadingIntent;

    public SyncData(Context ctx) {
        this.ctx = ctx;
        loadingIntent = new Intent(ctx, LoadingActivity.class);
        loadingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loadingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        loadingIntent.putExtra("keep", true);
        ctx.startActivity(loadingIntent);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        loadingIntent.putExtra("keep", false);
        ctx.startActivity(loadingIntent);
        ctx = null;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Socket socket = new Socket(Inet4Address.getByName("10.0.2.2"), 18565);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());



            JSONObject obj = new JSONObject();
            obj.put("name", "Fahim Ali Zain");
            byte[] data = obj.toString().getBytes("UTF-16BE");

            out.writeInt(data.length);
            out.flush();
            out.write(data);
            out.flush();
            socket.close();
        } catch (Exception e) {}
        return null;
    }
}
