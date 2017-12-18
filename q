[1mdiff --git a/app/src/main/java/com/casualmill/vansales/activities/TransactionActivity.java b/app/src/main/java/com/casualmill/vansales/activities/TransactionActivity.java[m
[1mindex 7287c3e..7d1d9e2 100644[m
[1m--- a/app/src/main/java/com/casualmill/vansales/activities/TransactionActivity.java[m
[1m+++ b/app/src/main/java/com/casualmill/vansales/activities/TransactionActivity.java[m
[36m@@ -3,9 +3,9 @@[m [mpackage com.casualmill.vansales.activities;[m
 import android.content.Intent;[m
 import android.graphics.PorterDuff;[m
 import android.graphics.drawable.Drawable;[m
[31m-import android.support.v4.content.ContextCompat;[m
[31m-import android.support.v7.app.AppCompatActivity;[m
 import android.os.Bundle;[m
[32m+[m[32mimport android.os.Handler;[m
[32m+[m[32mimport android.support.v7.app.AppCompatActivity;[m
 import android.support.v7.widget.LinearLayoutManager;[m
 import android.support.v7.widget.RecyclerView;[m
 import android.support.v7.widget.Toolbar;[m
[36m@@ -14,14 +14,18 @@[m [mimport android.text.TextWatcher;[m
 import android.view.LayoutInflater;[m
 import android.view.View;[m
 import android.view.ViewGroup;[m
[32m+[m[32mimport android.widget.AdapterView;[m
[32m+[m[32mimport android.widget.ArrayAdapter;[m
 import android.widget.Button;[m
[32m+[m[32mimport android.widget.EditText;[m
[32m+[m[32mimport android.widget.Spinner;[m
 import android.widget.TextView;[m
[31m-import android.widget.Toast;[m
 [m
 import com.casualmill.vansales.R;[m
[32m+[m[32mimport com.casualmill.vansales.animation.ResizeAnimation;[m
 import com.casualmill.vansales.data.models.InvoiceItem;[m
 import com.casualmill.vansales.data.models.Item;[m
[31m-import com.casualmill.vansales.fragments.ItemFragment;[m
[32m+[m[32mimport com.casualmill.vansales.data.models.UOM;[m
 import com.google.android.gms.common.api.CommonStatusCodes;[m
 [m
 import java.util.ArrayList;[m
[36m@@ -32,6 +36,9 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
 [m
     public static int ITEM_ADD_ACTIVITY_REQUEST_CODE = 59;[m
     private ItemsAdapter itemsAdapter;[m
[32m+[m[32m    private ItemsAdapter.Holder expandedHolder = null;[m
[32m+[m[32m    private EditText discountEditText;[m
[32m+[m[32m    private TextView totalTextView, grandTotalTextView;[m
 [m
     @Override[m
     protected void onCreate(Bundle savedInstanceState) {[m
[36m@@ -54,6 +61,19 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
         Toolbar b = findViewById(R.id.transaction_toolbar);[m
         setSupportActionBar(b);[m
 [m
[32m+[m[32m        discountEditText = findViewById(R.id.invoice_discount);[m
[32m+[m[32m        totalTextView = findViewById(R.id.invoice_total);[m
[32m+[m[32m        grandTotalTextView  = findViewById(R.id.invoice_gtotal);[m
[32m+[m
[32m+[m[32m        discountEditText.addTextChangedListener(new TextWatcher() {[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {[m
[32m+[m[32m                CalculateTotal();[m
[32m+[m[32m            }[m
[32m+[m[32m            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }[m
[32m+[m[32m            @Override public void afterTextChanged(Editable editable) { }[m
[32m+[m[32m        });[m
[32m+[m
         final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);[m
         upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryBGText), PorterDuff.Mode.SRC_ATOP);[m
         getSupportActionBar().setHomeAsUpIndicator(upArrow);[m
[36m@@ -102,6 +122,7 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
                 item.itemCode = t.itemCode;[m
                 item.itemName = t.itemName;[m
                 item.qty = 1;[m
[32m+[m[32m                item.UnitDetails = t.UnitDetails;[m
                 // item.price = t.price;[m
 [m
                 itemsAdapter.items.add(item);[m
[36m@@ -109,10 +130,34 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
             } else {[m
                 itemsAdapter.notifyItemChanged(itemsAdapter.items.indexOf(item));[m
             }[m
[32m+[m[32m            CalculateTotal();[m
         }[m
         super.onActivityResult(requestCode, resultCode, data);[m
     }[m
 [m
[32m+[m[32m    private synchronized void CalculateTotal() {[m
[32m+[m[32m        final float discount = Math.round(Float.valueOf(discountEditText.getText().toString().isEmpty() ? "0" : discountEditText.getText().toString()) * 100) / 100;[m
[32m+[m[32m        new Thread(new Runnable() {[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public void run() {[m
[32m+[m[32m                float tot = 0;[m
[32m+[m[32m                for (InvoiceItem i :itemsAdapter.items) {[m
[32m+[m[32m                    tot += Math.round(i.qty * i.price * 100) / 100;[m
[32m+[m[32m                }[m
[32m+[m[32m                final float total = tot;[m
[32m+[m[32m                final float grandTotal = total - discount;[m
[32m+[m[32m                new Handler(getMainLooper()).post(new Runnable() {[m
[32m+[m[32m                    @Override[m
[32m+[m[32m                    public void run() {[m
[32m+[m[32m                        discountEditText.setText(String.format(Locale.ENGLISH, "%.2f", discount));[m
[32m+[m[32m                        totalTextView.setText(String.format(Locale.ENGLISH, "%.2f", total));[m
[32m+[m[32m                        grandTotalTextView.setText(String.format(Locale.ENGLISH, "%.2f", grandTotal));[m
[32m+[m[32m                    }[m
[32m+[m[32m                });[m
[32m+[m[32m            }[m
[32m+[m[32m        }).start();[m
[32m+[m[32m    }[m
[32m+[m
     public class ItemsAdapter extends RecyclerView.Adapter<TransactionActivity.ItemsAdapter.Holder> {[m
 [m
         List<InvoiceItem> items;[m
[36m@@ -135,8 +180,13 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
             holder.itemName.setText(item.itemName);[m
             holder.itemCode.setText(item.itemCode);[m
             holder.barCode.setText(item.barcode);[m
[31m-            holder.price.setText(String.format(Locale.ENGLISH ,"$%f", item.price));[m
[32m+[m[32m            holder.price.setText(String.format(Locale.ENGLISH ,"$%.2f", item.price));[m
             holder.qty.setText(String.valueOf(item.qty));[m
[32m+[m
[32m+[m[32m            // unit init[m
[32m+[m[32m            ArrayAdapter spinnerAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, item.UnitDetails.toArray());[m
[32m+[m[32m            holder.unitSpinner.setAdapter(spinnerAdapter);[m
[32m+[m[32m            holder.unitSpinner.setSelection(0);[m
         }[m
 [m
         @Override[m
[36m@@ -147,7 +197,10 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
         public class Holder extends RecyclerView.ViewHolder {[m
 [m
             public TextView itemName, itemCode, barCode, qty, price;[m
[32m+[m[32m            public Spinner unitSpinner;[m
[32m+[m[32m            public View currentView;[m
             public InvoiceItem item;[m
[32m+[m[32m            private Boolean expanded = false;[m
 [m
             public Holder(View itemView) {[m
                 super(itemView);[m
[36m@@ -156,6 +209,15 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
                 this.barCode = itemView.findViewById(R.id.invoiceitem_item_barCode);[m
                 this.price = itemView.findViewById(R.id.invoiceitem_item_price);[m
                 this.qty = itemView.findViewById(R.id.invoiceitem_item_qty);[m
[32m+[m[32m                this.unitSpinner = itemView.findViewById(R.id.invoiceitem_unit_spinner);[m
[32m+[m
[32m+[m[32m                currentView = itemView;[m
[32m+[m[32m                currentView.setOnClickListener(new View.OnClickListener() {[m
[32m+[m[32m                    @Override[m
[32m+[m[32m                    public void onClick(View view) {[m
[32m+[m[32m                        ToggleExpand();[m
[32m+[m[32m                    }[m
[32m+[m[32m                });[m
 [m
                 qty.addTextChangedListener(new TextWatcher() {[m
 [m
[36m@@ -167,6 +229,7 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
                     @Override[m
                     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {[m
                         item.qty = Float.parseFloat(charSequence.toString());[m
[32m+[m[32m                        CalculateTotal();[m
                     }[m
 [m
                     @Override[m
[36m@@ -175,6 +238,22 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
                     }[m
                 });[m
 [m
[32m+[m[32m                unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {[m
[32m+[m[32m                    @Override[m
[32m+[m[32m                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {[m
[32m+[m[32m                        item.price = ((UOM) unitSpinner.getSelectedItem()).price;[m
[32m+[m[32m                        price.setText(String.format(Locale.ENGLISH, "$%.2f", item.price));[m
[32m+[m[32m                        CalculateTotal();[m
[32m+[m[32m                    }[m
[32m+[m
[32m+[m[32m                    @Override[m
[32m+[m[32m                    public void onNothingSelected(AdapterView<?> adapterView) {[m
[32m+[m[32m                        item.price = 0;[m
[32m+[m[32m                        price.setText("$0.00");[m
[32m+[m[32m                        CalculateTotal();[m
[32m+[m[32m                    }[m
[32m+[m[32m                });[m
[32m+[m
                 View.OnClickListener qty_button_listener = new View.OnClickListener() {[m
                     @Override[m
                     public void onClick(View view) {[m
[36m@@ -184,12 +263,28 @@[m [mpublic class TransactionActivity extends AppCompatActivity {[m
                             item.qty--;[m
 [m
                         qty.setText(String.valueOf(item.qty));[m
[32m+[m[32m                        CalculateTotal();[m
                     }[m
                 };[m
                 itemView.findViewById(R.id.invoiceitem_add_qty).setOnClickListener(qty_button_listener);[m
                 itemView.findViewById(R.id.invoiceitem_sub_qty).setOnClickListener(qty_button_listener);[m
 [m
             }[m
[32m+[m
[32m+[m[32m            void ToggleExpand() {[m
[32m+[m[32m                ResizeAnimation anim = new ResizeAnimation(currentView, ((int) (getResources().getDimensionPixelSize(R.dimen.itemlist_height) * (expanded ? 0.5 : 2f))), true);[m
[32m+[m[32m                anim.setDuration(500);[m
[32m+[m[32m                currentView.startAnimation(anim);[m
[32m+[m[32m                expanded = !expanded;[m
[32m+[m
[32m+[m[32m                if (expanded) {[m
[32m+[m[32m                    if (expandedHolder != null)[m
[32m+[m[32m                        expandedHolder.ToggleExpand();[m
[32m+[m[32m                    expandedHolder = this;[m
[32m+[m[32m                } else if (expandedHolder == this) // retracted by clicking self[m
[32m+[m[32m                    expandedHolder = null;[m
[32m+[m[32m            }[m
         }[m
     }[m
 }[m
[41m+[m
