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

public class NetworkSpinnerAdapter extends BaseAdapter {
    Context context;
    private static LayoutInflater inflater = null;
    ArrayList<Network> networksList;

    public NetworkSpinnerAdapter(Context applicationContext, ArrayList<Network> networksList) {
        this.context = applicationContext;
        this.networksList = networksList;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return (int) networksList.size();
    }

    @Override
    public Object getItem(int i) {
        try {
            return networksList.get(i);
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

            vi = inflater.inflate(R.layout.network_spinner_item, null);

        }

        ImageView networkImage = (ImageView) vi.findViewById(R.id.networkImage);
        TextView networkName = (TextView) vi.findViewById(R.id.networkName);

        networkName.setText(networksList.get(position).getName());
        networkImage.setImageResource(networksList.get(position).getImageId());

        return vi;
    }
}