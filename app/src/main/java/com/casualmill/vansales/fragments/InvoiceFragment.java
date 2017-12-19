package com.casualmill.vansales.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.casualmill.vansales.R;
import com.casualmill.vansales.activities.TransactionActivity;
import com.casualmill.vansales.data.AppDatabase;
import com.casualmill.vansales.data.Converters;
import com.casualmill.vansales.data.models.Invoice;
import com.casualmill.vansales.data.models.Item;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.List;

public class InvoiceFragment extends Fragment {

    private static final int NEW_INVOICE_REQUEST_CODE = 10;
    private static final int EDIT_INVOICE_REQUEST_CODE = 20;
    public static final int TRANSACTION_SUCCESS_RESULT_CODE = 25;
    public static final String InvoiceExtraKey = "INVOICE";
    private InvoiceAdapter invoiceAdapter;

    public InvoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invoice, container, false);

        v.findViewById(R.id.invoice_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                startActivityForResult(intent, NEW_INVOICE_REQUEST_CODE);
            }
        });

        // RecyclerView Setup
        final RecyclerView rv = v.findViewById(R.id.invoice_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Invoice> items = AppDatabase.Instance.invoiceDao().getAll();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        invoiceAdapter = new InvoiceAdapter(items);
                        rv.setAdapter(invoiceAdapter);
                    }
                });
            }
        }).start();
        return v;
    }

    private void OpenInvoice(Invoice in) {
        Intent intent = new Intent(getActivity(), TransactionActivity.class);
        intent.putExtra(InvoiceExtraKey, in);
        startActivityForResult(intent, EDIT_INVOICE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != TRANSACTION_SUCCESS_RESULT_CODE || !data.hasExtra(InvoiceExtraKey))
            return;

        final Invoice t = ((Invoice) data.getSerializableExtra(InvoiceExtraKey));
        switch (requestCode) {
            case NEW_INVOICE_REQUEST_CODE:
                invoiceAdapter.items.add(t);
                invoiceAdapter.notifyItemInserted(invoiceAdapter.items.size() - 1);
                break;
            case EDIT_INVOICE_REQUEST_CODE:
                for (int i = 0; i < invoiceAdapter.items.size(); i++) { // object reference is lost (serialization copies ?)
                    if (invoiceAdapter.items.get(i).invoice_no.contentEquals(t.invoice_no)) {
                        invoiceAdapter.items.remove(i);
                        invoiceAdapter.items.add(i, t);
                        invoiceAdapter.notifyItemChanged(i);
                        break;
                    }
                }
                break;
        }
    }

    class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.Holder> {

        List<Invoice> items;

        InvoiceAdapter(List<Invoice> items) {
            this.items = items;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_invoice, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Invoice invoice = items.get(position);
            holder.invoice_no.setText(invoice.invoice_no);
            holder.customer_name.setText(invoice.customer_name);
            holder.date.setText(Converters.getDateFormat().format(invoice.date));
            holder.gTotal.setText(Converters.toCurrency(invoice.grand_total));
            holder.invoice = invoice;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView invoice_no, customer_name, date, gTotal;
            Invoice invoice;

            Holder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                invoice_no = itemView.findViewById(R.id.listitem_invoice_no);
                customer_name = itemView.findViewById(R.id.listitem_invoice_customer);
                date = itemView.findViewById(R.id.listitem_invoice_date);
                gTotal = itemView.findViewById(R.id.listitem_invoice_gtotal);
            }

            @Override
            public void onClick(View view) {
                OpenInvoice(invoice);
            }
        }
    }
}
