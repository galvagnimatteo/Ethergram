package org.telegram.ethergramUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.googlecode.mp4parser.authoring.Edit;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;
import org.w3c.dom.Text;
import org.web3j.abi.Utils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendRawTransaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;

import rx.Subscription;

public class SendDialog extends Dialog {

    private Context context;

    private Spinner tokenSelection;
    private ArrayList<Balance> balances;
    private Network selectedNetwork;
    private Button sendButton;
    private EditText address;
    private EditText amount;
    private TextView errorDisplay;

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
        errorDisplay = (TextView) this.findViewById(R.id.errordisplayer);

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

            createNode();

            if(!WalletUtils.isValidAddress(address.getText().toString())){

                errorDisplay.setText("Address not valid");

                return null;

            }else {

                if (((Balance) tokenSelection.getSelectedItem()).getTokenSymbol() == "ETH") {

                    try {

                        BigInteger value = Convert.toWei(amount.getText().toString(), Convert.Unit.ETHER).toBigInteger();
                        EthGetTransactionCount ethGetTransactionCount = NodeHolder.getInstance().getNode().ethGetTransactionCount(
                                NodeHolder.getInstance().getCredentials().getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();

                        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, BigInteger.valueOf(4_100_000_000L), BigInteger.valueOf(9_000_000), address.getText().toString(), value);

                        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NodeHolder.getInstance().getCredentials());

                        String hexValue = Numeric.toHexString(signedMessage);

                        EthSendTransaction sendTransaction = NodeHolder.getInstance().getNode().ethSendRawTransaction(hexValue).sendAsync().get();

                        if (sendTransaction.hasError()) {

                            errorDisplay.setText("Transaction has errors");

                            return null;

                        } else {

                            errorDisplay.setText("Sending transaction...");

                            return sendTransaction;

                        }

                    } catch (Exception e) {

                        errorDisplay.setText("Cant send transaction");

                        return null;

                    }
                }

            }

            return null;

        }

        @Override
        public void onPostExecute(Object result){
            /*
            if(((EthGetTransactionReceipt)result).getTransactionReceipt() != null){

                Toast.makeText(context, "Transaction sent: " + ((EthGetTransactionReceipt) result).getTransactionReceipt().getTransactionHash(), Toast.LENGTH_LONG).show();

            }*/

            EthGetTransactionReceipt transactionReceipt;

            if((EthSendTransaction)result != null){

                try {

                    while (true) {

                        transactionReceipt = NodeHolder.getInstance().getNode().ethGetTransactionReceipt(((EthSendTransaction) result).getTransactionHash()).sendAsync().get();

                        if (transactionReceipt.getResult() != null) {

                            errorDisplay.setText("Mined on block " + transactionReceipt.getTransactionReceipt().getBlockNumberRaw());

                            break;
                        }

                        Thread.sleep(15000);
                    }

                }catch (Exception e){

                    e.printStackTrace();

                    errorDisplay.setText("Cant get transaction receipt.");

                }

            }

        }

    }

    private void createNode(){

        Web3j web3j;

        try {

            if (selectedNetwork.getName().equals("Rinkeby")) {

                web3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/" + BuildVars.INFURA_API));

            } else {

                web3j = Web3jFactory.build(new HttpService("https://mainnet.infura.io/v3/" + BuildVars.INFURA_API));

            }

            NodeHolder.getInstance().setNode(web3j);

        }catch (Exception e){

            errorDisplay.setText("Cant sync node");

        }

    }

}


