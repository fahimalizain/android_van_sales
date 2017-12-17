package com.casualmill.vansales.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.casualmill.vansales.R;
import com.casualmill.vansales.data.models.InvoiceItem;
import com.casualmill.vansales.data.models.Item;
import com.casualmill.vansales.fragments.ItemFragment;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    public static int ITEM_ADD_ACTIVITY_REQUEST_CODE = 59;
    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Load(getIntent().getIntExtra("type", 0));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void Load(int type) {
        // Type
        // 0 - Invoice
        // 1 - Transfer
        Toolbar b = findViewById(R.id.transaction_toolbar);
        setSupportActionBar(b);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryBGText), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Add Item
        Button add_item_button = findViewById(R.id.invoice_add_item);
        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(TransactionActivity.this, ItemSearchActivity.class);
                startActivityForResult(t,ITEM_ADD_ACTIVITY_REQUEST_CODE );
            }
        });

        // Recycler View
        itemsAdapter = new ItemsAdapter(new ArrayList<InvoiceItem>());
        RecyclerView r = findViewById(R.id.invoice_itemrecyclerView);
        r.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        r.setAdapter(itemsAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != CommonStatusCodes.SUCCESS || data == null)
            return;

        if (requestCode == ITEM_ADD_ACTIVITY_REQUEST_CODE) {
            Item t = (Item) data.getSerializableExtra("Item");

            InvoiceItem item = null;
            for (InvoiceItem invitem : itemsAdapter.items) {
                if (t.itemCode.equals(invitem.itemCode))
                {
                    item = invitem;
                    item.qty++;
                    break;
                }
            }

            if (item == null) {
                item = new InvoiceItem();
                item.itemCode = t.itemCode;
                item.itemName = t.itemName;
                item.qty = 1;
                // item.price = t.price;

                itemsAdapter.items.add(item);
                itemsAdapter.notifyItemInserted(itemsAdapter.items.size() - 1);
            } else {
                itemsAdapter.notifyItemChanged(itemsAdapter.items.indexOf(item));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class ItemsAdapter extends RecyclerView.Adapter<TransactionActivity.ItemsAdapter.Holder> {

        List<InvoiceItem> items;

        public ItemsAdapter(List<InvoiceItem> items) {
            this.items = items;
        }

        @Override
        public TransactionActivity.ItemsAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_transaction_item, parent, false);
            return new TransactionActivity.ItemsAdapter.Holder(v);
        }

        @Override
        public void onBindViewHolder(TransactionActivity.ItemsAdapter.Holder holder, int position) {
            InvoiceItem item = items.get(position);

            holder.item = item;
            holder.itemName.setText(item.itemName);
            holder.itemCode.setText(item.itemCode);
            holder.barCode.setText(item.barcode);
            holder.price.setText(String.format(Locale.ENGLISH ,"$%f", item.price));
            holder.qty.setText(String.valueOf(item.qty));
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            public TextView itemName, itemCode, barCode, qty, price;
            public InvoiceItem item;

            public Holder(View itemView) {
                super(itemView);
                this.itemCode = itemView.findViewById(R.id.invoiceitem_item_itemCode);
                this.itemName = itemView.findViewById(R.id.invoiceitem_item_itemName);
                this.barCode = itemView.findViewById(R.id.invoiceitem_item_barCode);
                this.price = itemView.findViewById(R.id.invoiceitem_item_price);
                this.qty = itemView.findViewById(R.id.invoiceitem_item_qty);

                qty.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        item.qty = Float.parseFloat(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                View.OnClickListener qty_button_listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.invoiceitem_add_qty)
                            item.qty++;
                        else
                            item.qty--;

                        qty.setText(String.valueOf(item.qty));
                    }
                };
                itemView.findViewById(R.id.invoiceitem_add_qty).setOnClickListener(qty_button_listener);
                itemView.findViewById(R.id.invoiceitem_sub_qty).setOnClickListener(qty_button_listener);

            }
        }
    }
}
