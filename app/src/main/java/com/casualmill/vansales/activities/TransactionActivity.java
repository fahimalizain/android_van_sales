package com.casualmill.vansales.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.casualmill.vansales.R;
import com.casualmill.vansales.animation.ResizeAnimation;
import com.casualmill.vansales.data.AppDatabase;
import com.casualmill.vansales.data.Converters;
import com.casualmill.vansales.data.DataHelper;
import com.casualmill.vansales.data.models.Invoice;
import com.casualmill.vansales.data.models.InvoiceItem;
import com.casualmill.vansales.data.models.Item;
import com.casualmill.vansales.data.models.UOM;
import com.casualmill.vansales.fragments.InvoiceFragment;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private static final String TAG = "TransactionActivity";
    public static int ITEM_ADD_ACTIVITY_REQUEST_CODE = 59;
    private ItemsAdapter itemsAdapter;
    private ItemsAdapter.Holder expandedHolder = null;
    private EditText discountEditText, dateEditText, customerEditText;
    private TextView totalTextView, grandTotalTextView;
    private Button addItemButton, saveButton, cancelButton, editButton;

    public enum UIMode {READ, EDIT}
    float grandTotal;
    Invoice currentInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Load();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void Load() {
        // Type
        // 0 - Invoice
        // 1 - Transfer
        Toolbar b = findViewById(R.id.transaction_toolbar);
        setSupportActionBar(b);

        dateEditText = findViewById(R.id.invoice_dateField);
        customerEditText = findViewById(R.id.invoice_customer);
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
        addItemButton = findViewById(R.id.invoice_add_item);
        saveButton = findViewById(R.id.invoice_saveButton);
        cancelButton = findViewById(R.id.invoice_cancelButton);
        editButton = findViewById(R.id.invoice_editButton);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(TransactionActivity.this, ItemSearchActivity.class);
                startActivityForResult(t,ITEM_ADD_ACTIVITY_REQUEST_CODE );
            }
        });

        // Date Field
        final Calendar calendar = Calendar.getInstance();
        dateEditText.setText(Converters.getDateFormat().format(calendar.getTime()));
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TransactionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);

                        SimpleDateFormat sdf = Converters.getDateFormat();
                        dateEditText.setText(sdf.format(calendar.getTime()));
                        dateEditText.setTag(calendar.getTime());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

        // Load Invoice
        if (getIntent().hasExtra(InvoiceFragment.InvoiceExtraKey))
        {
            currentInvoice = ((Invoice) getIntent().getSerializableExtra(InvoiceFragment.InvoiceExtraKey));
            LoadInvoice(currentInvoice);
            SetUiMode(UIMode.READ);
            b.setTitle(currentInvoice.invoice_no);
        } else
            SetUiMode(UIMode.EDIT);
    }

    private void SetUiMode(UIMode mode) {
        Boolean enabled = mode != UIMode.READ;
        dateEditText.setEnabled(enabled);
        customerEditText.setEnabled(enabled);
        discountEditText.setEnabled(enabled);
        saveButton.setVisibility(mode == UIMode.EDIT ? View.VISIBLE : View.INVISIBLE);
        editButton.setVisibility(mode == UIMode.READ ? View.VISIBLE : View.INVISIBLE);
        cancelButton.setVisibility(saveButton.getVisibility());
        addItemButton.setVisibility(saveButton.getVisibility());
    }

    private void LoadInvoice(final Invoice inv) {
        LoadingActivity.IncrementLoading();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500); // prevent Load on CPU
                    final List<InvoiceItem> items = AppDatabase.Instance.invoiceItemDao().get_all_for_invoice_no(inv.invoice_no);
                    for (InvoiceItem t : items)
                        DataHelper.fillItem(t);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            itemsAdapter.items = items;
                            itemsAdapter.notifyDataSetChanged();
                            dateEditText.setText(Converters.getDateFormat().format(inv.date));
                            customerEditText.setText(inv.customer_name);
                            discountEditText.setText(Converters.toString(inv.discount, 2));
                            CalculateTotal();

                            LoadingActivity.DecrementLoading();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LoadingActivity.DecrementLoading();
                }
            }
        }).start();
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

    private synchronized Thread CalculateTotal() {
        final float discount = Converters.roundFloat(Converters.fromString(discountEditText.getText().toString()), 2);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                float tot = 0;
                for (InvoiceItem i :itemsAdapter.items) {
                    tot += Math.round(i.qty * i.price * 100) / 100;
                }
                final float total = tot;
                grandTotal = total - discount;
                // edit the discount edittext only after setting the global grandTotal
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        totalTextView.setText(String.format(Locale.ENGLISH, "%.2f", total));
                        grandTotalTextView.setText(String.format(Locale.ENGLISH, "%.2f", grandTotal));
                    }
                });
                Log.d(TAG, "Calculated Total");
            }
        });
        t.start();
        return t;
    }

    public void Save(View v) {
        LoadingActivity.IncrementLoading();
        final Invoice t = currentInvoice == null ? new Invoice() : currentInvoice;
        final Boolean editing = currentInvoice != null;

        EditText dateField = findViewById(R.id.invoice_dateField);
        EditText customerField = findViewById(R.id.invoice_customer);

        final int last_invoice_no = PreferenceManager.getDefaultSharedPreferences(this).getInt("last_invoice_no", 0);
        String invoice_format = PreferenceManager.getDefaultSharedPreferences(this).getString("invoice_no_template", "SINV-VEHICLEA-%5d");
        t.invoice_no = editing ? t.invoice_no :  String.format(invoice_format, last_invoice_no + 1).replace(' ', '0');
        Log.d(TAG, "Generated Invoice No to Save : " + t.invoice_no);

        SimpleDateFormat sdf = Converters.getDateFormat();
        try {

            t.date = sdf.parse(dateField.getText().toString());
            t.customer_name = customerField.getText().toString();
            t.discount = Converters.roundFloat(Converters.fromString(discountEditText.getText().toString()), 2);
        } catch (Exception e) {
            if (e instanceof ParseException)
                new AlertDialog.Builder(this).setMessage("Please set the Date").setPositiveButton("OK", null).show();

            LoadingActivity.DecrementLoading();
            return;
        }
        new Thread(new Runnable() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void run() {
                try {
                    Thread calcThread = CalculateTotal();
                    calcThread.join(); // calculate and update grand_total
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t.grand_total = grandTotal;

                for(InvoiceItem i : itemsAdapter.items)
                    i.invoiceNo = t.invoice_no;

                // editing - update
                AppDatabase.Instance.invoiceDao().Insert(t, itemsAdapter.items.toArray(new InvoiceItem[itemsAdapter.items.size()]), editing);
                if (!editing) // increment only if new invoice
                    PreferenceManager.getDefaultSharedPreferences(TransactionActivity.this).edit().putInt("last_invoice_no", last_invoice_no + 1).commit();
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        LoadingActivity.DecrementLoading();
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Invoice Saved", Snackbar.LENGTH_LONG).show();

                        Intent result = new Intent();
                        result.putExtra(InvoiceFragment.InvoiceExtraKey, t);
                        setResult(InvoiceFragment.TRANSACTION_SUCCESS_RESULT_CODE, result);
                        finish();
                    }
                });
                Log.d(TAG, "Invoice Successfully Saved");
            }
        }).start();
    }

    public void Edit(View v) {
        SetUiMode(UIMode.EDIT);
    }

    public void Cancel(View view) {
        finish();
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

