package org.telegram.ethergramUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.telegram.messenger.R;
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

            vi = inflater.inflate(R.layout.ethtransaction, null);

        }

        TextView amount = (TextView) vi.findViewById(R.id.title);
        TextView address = (TextView) vi.findViewById(R.id.subtitle);

        Transaction trans = transactions.get(position);

        try {

            if(trans instanceof ERC20Transaction){

                ERC20Transaction transaction = (ERC20Transaction) trans;

                int decimals = Integer.parseInt(transaction.getTokenDecimal());
                long value = Long.parseLong(transaction.getValue());

                amount.setText((value/Math.pow(10, decimals)) + " " + transaction.getTokenSymbol());

            }else{

                amount.setText(Convert.fromWei(trans.getValue(), Convert.Unit.ETHER) + " ETH");

            }

            if(trans.getTo().toLowerCase().equals(NodeHolder.getInstance().getCredentials().getAddress().toLowerCase())){

                address.setText("From: " + trans.getFrom().toLowerCase());

            }else{

                address.setText("To: " + trans.getTo().toLowerCase());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return vi;
    }
}