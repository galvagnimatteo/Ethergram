package org.telegram.ethergramUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.R;

import java.util.ArrayList;

public class BalanceSpinnerAdapter extends BaseAdapter {
    Context context;
    private static LayoutInflater inflater = null;
    ArrayList<Balance> balancesList;

    public BalanceSpinnerAdapter(Context applicationContext, ArrayList<Balance> balancesList) {
        this.context = applicationContext;
        this.balancesList = balancesList;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return (int) balancesList.size();
    }

    @Override
    public Object getItem(int i) {
        try {
            return balancesList.get(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if (vi == null) {

            vi = inflater.inflate(R.layout.balance_spinner_item, null);

        }

        TextView tokenSymbol = (TextView) vi.findViewById(R.id.tokensymbol);
        TextView valuebalance = (TextView) vi.findViewById(R.id.valuebalance);

        tokenSymbol.setText(balancesList.get(position).getTokenSymbol());
        valuebalance.setText(balancesList.get(position).getBalance() + "");

        return vi;
    }
}