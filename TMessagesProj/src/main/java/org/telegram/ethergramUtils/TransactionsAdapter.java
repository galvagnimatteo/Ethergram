package org.telegram.ethergramUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ethereum.geth.Transaction;
import org.ethereum.geth.Transactions;
import org.telegram.messenger.R;

public class TransactionsAdapter extends BaseAdapter {

    Context context;
    Transactions transactions;
    private static LayoutInflater inflater = null;

    public TransactionsAdapter(Context context, Transactions transactions) {

        this.context = context;
        this.transactions = transactions;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return (int) transactions.size();
    }

    @Override
    public Object getItem(int position) {
        try {
            return transactions.get((long)position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if (vi == null) {

            vi = inflater.inflate(R.layout.ethtransaction, null);

        }

        TextView amount = (TextView) vi.findViewById(R.id.amount);
        TextView to_address = (TextView) vi.findViewById(R.id.to_address);

        try {
            amount.setText(transactions.get(position).getValue().string());
            to_address.setText(transactions.get(position).getTo().getHex());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vi;
    }
}