package com.casualmill.vansales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.casualmill.vansales.R;
import com.casualmill.vansales.data.AppDatabase;
import com.casualmill.vansales.data.DataHelper;
import com.casualmill.vansales.data.models.Item;
import com.casualmill.vansales.support.FragmentLifecycle;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemSearchFragment extends Fragment implements FragmentLifecycle {

    ItemAdapter adapter;

    public ItemSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_item_search, container, false);

        EditText searchField = parent.findViewById(R.id.itemsearch_searchTerm);
        RecyclerView rv = parent.findViewById(R.id.itemsearch_resultRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ItemAdapter(new ArrayList<Item>());
        rv.setAdapter(adapter);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateList(charSequence.toString());
                    }
                }).start();
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        return parent;
    }

    private synchronized void updateList(String search) {
        final List<Item> items = AppDatabase.Instance.itemDao().searchItem(search);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                adapter.items.clear();
                adapter.items.addAll(items);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void OnItemSelected(final Item item) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // fill UOMDetails
                DataHelper.fillItem(item);

                Intent data = new Intent();
                data.putExtra("Item", item);
                getActivity().setResult(CommonStatusCodes.SUCCESS, data);
                getActivity().finish();
            }
        }).start();
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

    }


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
            holder.item = item;
            holder.itemName.setText(item.itemName);
            holder.itemCode.setText(item.itemCode);
            holder.barCode.setText(item.barcode);
            holder.qty.setText(String.valueOf(item.stock_balance));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView itemName, itemCode, barCode, qty, price;
            Item item;

            Holder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                this.itemCode = itemView.findViewById(R.id.listitem_item_itemCode);
                this.itemName = itemView.findViewById(R.id.listitem_item_itemName);
                this.barCode = itemView.findViewById(R.id.listitem_item_barCode);
                this.price = itemView.findViewById(R.id.listitem_item_price);
                this.qty = itemView.findViewById(R.id.listitem_item_qty);
            }

            @Override
            public void onClick(View view) {
                OnItemSelected(item);
            }
        }
    }
}
