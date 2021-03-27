package org.telegram.ethergramUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.googlecode.mp4parser.authoring.Edit;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;

public class SendDialog extends Dialog {

    private Context context;

    private Spinner tokenSelection;
    private ArrayList<Balance> balances;
    private Network selectedNetwork;
    private Button sendButton;
    private EditText address;
    private EditText amount;

    public SendDialog(@NonNull Context context, ArrayList<Balance> balances, Network selectedNetwork) {
        super(context);
        this.context = context;
        this.balances = balances;
        this.selectedNetwork = selectedNetwork;
    }

    @Override
    protected void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);

        setContentView(R.layout.sendtransaction_layout);
        setCanceledOnTouchOutside(false);
        setCancelable(true);

        sendButton = (Button) this.findViewById(R.id.send);

        tokenSelection = (Spinner) this.findViewById(R.id.balanceselection);

        BalanceSpinnerAdapter balanceSpinnerAdapter = new BalanceSpinnerAdapter(context, balances);
        tokenSelection.setAdapter(balanceSpinnerAdapter);
        tokenSelection.setSelection(0);

        address = (EditText) this.findViewById(R.id.sendaddress);
        amount = (EditText) this.findViewById(R.id.sendvalue);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SendTransaction().execute();

            }
        });


    }

    //Syncing a node needs to be done in background to not block the UI. Node synced only when sending a transaction.
    private class SendTransaction extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            //--------------------------------NODE CONNECTION---------------------------------------

            Web3j web3j;

            try {

                if (selectedNetwork.getName().equals("Rinkeby")) {

                    web3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/" + BuildVars.INFURA_API));

                } else {

                    web3j = Web3jFactory.build(new HttpService("https://mainnet.infura.io/v3/" + BuildVars.INFURA_API));

                }

                NodeHolder.getInstance().setNode(web3j);

            }catch (Exception e){

                getOwnerActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        //TODO display error

                    }

                });

            }

            //-------------------------------END NODE CONNECTION------------------------------------



            if(((Balance)tokenSelection.getSelectedItem()).getTokenSymbol() == "ETH"){

                try {

                    BigInteger value = Convert.toWei(amount.getText().toString(), Convert.Unit.ETHER).toBigInteger();
                    EthGetTransactionCount ethGetTransactionCount = NodeHolder.getInstance().getNode().ethGetTransactionCount(
                            NodeHolder.getInstance().getCredentials().getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();

                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, BigInteger.valueOf(4_100_000_000L), BigInteger.valueOf(9_000_000),address.getText().toString(), value);

                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NodeHolder.getInstance().getCredentials());

                    String hexValue = Numeric.toHexString(signedMessage);

                    NodeHolder.getInstance().getNode().ethSendRawTransaction(hexValue).send();

                }catch (Exception e){



                }
            }



            return null;

        }

    }

}


