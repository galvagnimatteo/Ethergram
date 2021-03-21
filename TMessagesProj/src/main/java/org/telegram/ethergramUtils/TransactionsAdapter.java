package org.telegram.ethergramUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ethereum.geth.Node;
import org.ethereum.geth.Transactions;
import org.telegram.messenger.R;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Convert;

import java.util.ArrayList;

public class TransactionsAdapter extends BaseAdapter {

    Context context;
    ArrayList<Transaction> transactions;
    private static LayoutInflater inflater = null;

    public TransactionsAdapter(Context context, ArrayList<Transaction> transactions) {

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
            return transactions.get(position);
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

            vi = inflater.inflate(R.layout.testtransaction, null);

        }

        TextView amount = (TextView) vi.findViewById(R.id.title);
        TextView to_address = (TextView) vi.findViewById(R.id.subtitle);

        try {

            amount.setText(Convert.fromWei(transactions.get(position).getValueRaw(), Convert.Unit.ETHER) + " ETH");

            if(NodeHolder.getInstance().getAccount().getAddress().getHex().toLowerCase() == transactions.get(position).getTo().toLowerCase()){ //if account address == getTo, it is received

                to_address.setText("From " + transactions.get(position).getFrom());

            }else{

                to_address.setText("To " + transactions.get(position).getTo());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return vi;
    }
}