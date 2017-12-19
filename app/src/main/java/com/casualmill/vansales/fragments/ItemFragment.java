package com.casualmill.vansales.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.casualmill.vansales.R;
import com.casualmill.vansales.data.AppDatabase;
import com.casualmill.vansales.data.models.Item;

import java.util.List;

public class ItemFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_item, container, false);

        Load(v);
        return v;
    }

    private void Load(View v) {

        recyclerView = v.findViewById(R.id.item_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Item> items = AppDatabase.Instance.itemDao().getAll();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ItemAdapter adapter = new ItemAdapter(items);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }


    // Adapter for Item RecyclerView
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.Holder> {

        List<Item> items;

        public ItemAdapter(List<Item> items) {
            this.items = items;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_item, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Item item = items.get(position);
            holder.itemName.setText(item.itemName);
            holder.itemCode.setText(item.itemCode);
            holder.barCode.setText(item.barcode);
            // holder.price.setText("$" + item.price);
            holder.qty.setText(item.stock_balance + "");
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            public TextView itemName, itemCode, barCode, qty, price;

            public Holder(View itemView) {
                super(itemView);
                this.itemCode = itemView.findViewById(R.id.listitem_invoice_no);
                this.itemName = itemView.findViewById(R.id.listitem_invoice_customer);
                this.barCode = itemView.findViewById(R.id.listitem_invoice_date);
                this.price = itemView.findViewById(R.id.listitem_invoice_gtotal);
                this.qty = itemView.findViewById(R.id.listitem_item_qty);

            }
        }
    }
}
