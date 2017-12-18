package com.casualmill.vansales.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.casualmill.vansales.R;
import com.casualmill.vansales.animation.ResizeAnimation;
import com.casualmill.vansales.data.models.InvoiceItem;
import com.casualmill.vansales.data.models.Item;
import com.casualmill.vansales.data.models.UOM;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    public static int ITEM_ADD_ACTIVITY_REQUEST_CODE = 59;
    private ItemsAdapter itemsAdapter;
    private ItemsAdapter.Holder expandedHolder = null;
    private EditText discountEditText;
    private TextView totalTextView, grandTotalTextView;

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

        discountEditText = findViewById(R.id.invoice_discount);
        totalTextView = findViewById(R.id.invoice_total);
        grandTotalTextView  = findViewById(R.id.invoice_gtotal);

        discountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CalculateTotal();
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void afterTextChanged(Editable editable) { }
        });

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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                itemsAdapter.items.remove(viewHolder.getAdapterPosition());
                itemsAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(r);
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
                item.UnitDetails = t.UnitDetails;
                // item.price = t.price;

                itemsAdapter.items.add(item);
                itemsAdapter.notifyItemInserted(itemsAdapter.items.size() - 1);
            } else {
                itemsAdapter.notifyItemChanged(itemsAdapter.items.indexOf(item));
            }
            CalculateTotal();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private synchronized void CalculateTotal() {
        final float discount = Math.round(Float.valueOf(discountEditText.getText().toString().isEmpty() ? "0" : discountEditText.getText().toString()) * 100) / 100;
        new Thread(new Runnable() {
            @Override
            public void run() {
                float tot = 0;
                for (InvoiceItem i :itemsAdapter.items) {
                    tot += Math.round(i.qty * i.price * 100) / 100;
                }
                final float total = tot;
                final float grandTotal = total - discount;
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        discountEditText.setText(String.format(Locale.ENGLISH, "%.2f", discount));
                        totalTextView.setText(String.format(Locale.ENGLISH, "%.2f", total));
                        grandTotalTextView.setText(String.format(Locale.ENGLISH, "%.2f", grandTotal));
                    }
                });
            }
        }).start();
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
            holder.price.setText(String.format(Locale.ENGLISH ,"$%.2f", item.price));
            holder.qty.setText(String.valueOf(item.qty));

            // unit init
            ArrayAdapter spinnerAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, item.UnitDetails.toArray());
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.unitSpinner.setAdapter(spinnerAdapter);
            holder.unitSpinner.setSelection(0);
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            public TextView itemName, itemCode, barCode, qty, price;
            public Spinner unitSpinner;
            public View currentView;
            public InvoiceItem item;
            private Boolean expanded = false;

            public Holder(View itemView) {
                super(itemView);
                this.itemCode = itemView.findViewById(R.id.invoiceitem_item_itemCode);
                this.itemName = itemView.findViewById(R.id.invoiceitem_item_itemName);
                this.barCode = itemView.findViewById(R.id.invoiceitem_item_barCode);
                this.price = itemView.findViewById(R.id.invoiceitem_item_price);
                this.qty = itemView.findViewById(R.id.invoiceitem_item_qty);
                this.unitSpinner = itemView.findViewById(R.id.invoiceitem_unit_spinner);

                currentView = itemView;
                currentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ToggleExpand();
                    }
                });

                qty.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        item.qty = Float.parseFloat(charSequence.toString());
                        CalculateTotal();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        item.price = ((UOM) unitSpinner.getSelectedItem()).price;
                        price.setText(String.format(Locale.ENGLISH, "$%.2f", item.price));
                        CalculateTotal();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        item.price = 0;
                        price.setText("$0.00");
                        CalculateTotal();
                    }
                });

                View.OnClickListener qty_button_listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.invoiceitem_add_qty)
                            item.qty++;
                        else {
                            item.qty--;
                            item.qty = Math.max(0, item.qty);
                        }
                        qty.setText(String.valueOf(item.qty));
                        CalculateTotal();
                    }
                };
                itemView.findViewById(R.id.invoiceitem_add_qty).setOnClickListener(qty_button_listener);
                itemView.findViewById(R.id.invoiceitem_sub_qty).setOnClickListener(qty_button_listener);

            }

            void ToggleExpand() {
                ResizeAnimation anim = new ResizeAnimation(currentView, ((int) (getResources().getDimensionPixelSize(R.dimen.itemlist_height) * (expanded ? 1 : 2f))), true);
                anim.setDuration(500);
                currentView.startAnimation(anim);
                expanded = !expanded;

                if (expanded) {
                    if (expandedHolder != null)
                        expandedHolder.ToggleExpand();
                    expandedHolder = this;
                } else if (expandedHolder == this) // retracted by clicking self
                    expandedHolder = null;
            }
        }
    }
}

