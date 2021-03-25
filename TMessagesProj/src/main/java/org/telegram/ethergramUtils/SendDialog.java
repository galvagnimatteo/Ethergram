package org.telegram.ethergramUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.telegram.messenger.R;

import java.util.ArrayList;

public class SendDialog extends Dialog {

    private Context context;

    private LinearLayout spinnerLayout;
    private Spinner tokenSelection;

    private ArrayList<Balance> balances;

    public SendDialog(@NonNull Context context, ArrayList<Balance> balances) {
        super(context);
        this.context = context;
        this.balances = balances;
    }

    @Override
    protected void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);

        setContentView(R.layout.sendtransaction_layout);
        setCanceledOnTouchOutside(false);
        setCancelable(true);

        View spinnerView = LayoutInflater.from(context).inflate(R.layout.custom_spinner, null);

        spinnerLayout = (LinearLayout) spinnerView.findViewById(R.id.spinnerlayout);
        tokenSelection = (Spinner) spinnerView.findViewById(R.id.customspinner);

        Toast.makeText(context, balances.size() + " balances", Toast.LENGTH_SHORT).show(); //TODO show balances in spinner

    }

}
