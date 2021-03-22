package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ethereum.geth.Account;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.ethergramUtils.ERC20Transaction;
import org.telegram.ethergramUtils.NodeHolder;
import org.telegram.ethergramUtils.Transaction;
import org.telegram.ethergramUtils.TransactionsAdapter;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EthereumWalletActivity extends BaseFragment {

    private Context context;
    protected View actionBarBackground;

    private LinearLayout mainLayout;

    private LinearLayout headerLayout;

    private CircleImageView ethlogoimage;
    private TextView titleTextView;
    private TextView messageTextView;

    private LinearLayout passwordLayout;

    private EditTextBoldCursor password;
    private TextView button;

    private LinearLayout walletViewer;

    private TextView balanceTextView;
    private ListView transactionsListView;

    private MultiTaskHandler multiTaskHandler;

    private ArrayList<Transaction> transactions;


    File dir;

    public EthereumWalletActivity(File dir) {

        super();
        this.dir = dir;

    }


    @Override
    public View createView(Context context) {

        this.context = context;

        mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams mainparams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mainLayout.setLayoutParams(mainparams);

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackground(null);
        actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_listSelector), false);
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet());
        actionBar.setTitle(LocaleController.getString("ETHwallet", R.string.ETHwallet));
        actionBar.getTitleTextView().setAlpha(0.0f);
        if (!AndroidUtilities.isTablet()) {
            actionBar.showActionModeTop();
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) actionBarBackground.getLayoutParams();
                layoutParams.height = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(3);

                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
            }
        };

        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        fragmentView.setTag(Theme.key_windowBackgroundGray);
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        actionBarBackground = new View(context) {

            private Paint paint = new Paint();

            @Override
            protected void onDraw(Canvas canvas) {
                paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                int h = getMeasuredHeight() - AndroidUtilities.dp(3);
                canvas.drawRect(0, 0, getMeasuredWidth(), h, paint);
                parentLayout.drawHeaderShadow(canvas, h);

                mainparams.setMargins(0, getMeasuredHeight(), 0, 0);
                mainLayout.setLayoutParams(mainparams);
            }
        };

        actionBarBackground.setAlpha(0.0f);
        frameLayout.addView(actionBarBackground, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        frameLayout.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        frameLayout.addView(mainLayout);

        //---------------------------------------HEADER---------------------------------------------

        View ethwalletheaderView = LayoutInflater.from(context).inflate(R.layout.ethwalletheader, null);

        headerLayout = (LinearLayout) ethwalletheaderView.findViewById(R.id.ethwalletheader);

        ethlogoimage = (CircleImageView) ethwalletheaderView.findViewById(R.id.ethlogo);
        ethlogoimage.setImageResource(R.drawable.ethtoken);

        titleTextView = (TextView) ethwalletheaderView.findViewById(R.id.titleTextView);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setText("Your Ethereum wallet");

        messageTextView = (TextView) ethwalletheaderView.findViewById(R.id.messageTextView);
        messageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        messageTextView.setGravity(Gravity.CENTER);

        mainLayout.addView(headerLayout);

        //------------------------------------HEADER END--------------------------------------------

        //-------------------------------------PASSWORD---------------------------------------------

        View ethwalletcredentialsView = LayoutInflater.from(context).inflate(R.layout.ethwalletcredentials, null);

        passwordLayout = (LinearLayout) ethwalletcredentialsView.findViewById(R.id.ethwalletcredentials); //header layout

        password = (EditTextBoldCursor) ethwalletcredentialsView.findViewById(R.id.password);
        password.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        password.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        password.setCursorSize(AndroidUtilities.dp(20));
        password.setCursorWidth(1.5f);
        password.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        password.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        password.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        password.setMaxLines(1);
        password.setPadding(10, 0, 10, 20);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password.setTypeface(Typeface.DEFAULT);
        password.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);

        button = (TextView) ethwalletcredentialsView.findViewById(R.id.button);
        button.setTextColor(0xffffffff);
        button.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        button.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4), 0xff50a8eb, 0xff439bde));

        //----------------------------------PASSWORD END--------------------------------------------

        //------------------------------------VIEWER------------------------------------------------

        View ethwalletviewerView = LayoutInflater.from(context).inflate(R.layout.ethwalletviewer, null);

        walletViewer = (LinearLayout) ethwalletviewerView.findViewById(R.id.ethwalletviewer);

        balanceTextView = (TextView) ethwalletviewerView.findViewById(R.id.balance);

        transactionsListView = (ListView) ethwalletviewerView.findViewById(R.id.transactionsListView);

        //----------------------------------END VIEWER----------------------------------------------

        //-------------------------------------LOGIC------------------------------------------------

        transactions =  new ArrayList<Transaction>();

        if(NodeHolder.getInstance().getAccount() != null){ //user already logged

            messageTextView.setText(NodeHolder.getInstance().getAccount().getAddress().getHex());

            fillWalletViewer(true);

        }else{

            if (new File(dir + "/keystore").exists()) { //login

                messageTextView.setText("Unlock your wallet");

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new PasswordTask(true).execute();

                    }
                });

            } else { //account creation

                messageTextView.setText("Create your wallet");

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new PasswordTask(false).execute();

                    }
                });

            }

            mainLayout.addView(passwordLayout);

        }

        return fragmentView;

    }

    //-------------------------------------Async Tasks----------------------------------------------

    //Keystore operations needs to be done in async mode to not stop the UI.
    private class PasswordTask extends AsyncTask{

        boolean login;

        public PasswordTask(boolean login){

            this.login = login;

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if(login){

                try {

                    KeyStore ks = new KeyStore(dir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);

                    ks.unlock(ks.getAccounts().get(0), password.getText().toString());

                    NodeHolder.getInstance().setAccount(ks.getAccounts().get(0));

                    return true;

                }catch(Exception e){

                    getParentActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            messageTextView.setText("Error while unlocking your Ethereum wallet.\n" + e.getMessage());

                        }

                    });

                    return false;

                }

            }else{ //registration

                try {

                    KeyStore keyStore = Geth.newKeyStore(dir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);

                    Account account = keyStore.newAccount(password.getText().toString());

                    NodeHolder.getInstance().setAccount(account);

                    return true;

                }catch(Exception e){

                    getParentActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            messageTextView.setText("Error while creating an Ethereum wallet.\n" + e.getMessage());

                        }

                    });

                    return false;

                }

            }

        }

        //On post execute it is displayed the wallet viewer.
        @Override
        public void onPostExecute(Object result){

            boolean isSucceeded = (boolean) result;

            if(isSucceeded){ //If account is created or unlocked the node can be started.

                getParentActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        ((ViewManager) passwordLayout.getParent()).removeView(passwordLayout);

                        messageTextView.setText(NodeHolder.getInstance().getAccount().getAddress().getHex());

                        fillWalletViewer(true);

                    }

                });

            }

        }

    }

    //Syncing a node needs to be done in background to not block the UI. Node synced only when sending a transaction.
    private class ConnectNode extends AsyncTask{

        private boolean rinkeby;

        public ConnectNode(boolean rinkeby){

            this.rinkeby = rinkeby;

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            Web3j web3j;

            try {

                if (rinkeby) {

                    web3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/" + BuildVars.INFURA_API));

                } else {

                    web3j = Web3jFactory.build(new HttpService("https://mainnet.infura.io/v3/" + BuildVars.INFURA_API));

                }

                NodeHolder.getInstance().setNode(web3j);

                return true;

            }catch (Exception e){

                getParentActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        messageTextView.setText("Cannot connect to node\n" + e.getMessage());

                    }

                });

                return false;

            }

        }

    }

    public void fillWalletViewer(boolean rinkeby){

        try {

            String domainAPI;

            if(rinkeby){

                domainAPI = "https://api-rinkeby.etherscan.io";

            }else{

                domainAPI = "https://api.etherscan.io";

            }

            int totalNumOfTasks = 3;
            multiTaskHandler = new MultiTaskHandler(totalNumOfTasks) {
                @Override
                protected void onAllTasksCompleted() {

                    new UpdateWalletViewer().execute();

                }
            };

            new UpdateBalance(domainAPI).execute();
            new UpdateTransactions(domainAPI).execute();
            new UpdateERC20Transactions(domainAPI).execute();

        }catch(Exception e){

            getParentActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    messageTextView.setText("Cannot fetch balance\n" + e.getMessage());

                }

            });

        }

    }

    private class UpdateWalletViewer extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {

            Collections.sort(transactions); //order by timestamp

            return null;

        }

        @Override
        protected void onPostExecute(Object objects) {

            TransactionsAdapter adapter = new TransactionsAdapter(context, transactions);
            transactionsListView.setAdapter(adapter);

            if(walletViewer.getParent() == null){

                mainLayout.addView(walletViewer);

            }

            Toast.makeText(context, "Wallet updated", Toast.LENGTH_LONG).show();

        }

    }

    private class UpdateERC20Transactions extends AsyncTask{

        String call = "/api?module=account&action=tokentx&address=" + NodeHolder.getInstance().getAccount().getAddress().getHex() + "&startblock=0&endblock=999999999&sort=asc&apikey=" + BuildVars.ETHERSCAN_API;

        public UpdateERC20Transactions(String domainAPI){

            this.call = domainAPI + this.call;

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            return callEtherscanAPI(call); //returns json string

        }

        @Override
        public void onPostExecute(Object o){

            String result = (String) o;

            try {

                JSONObject jObject = new JSONObject(result);

                JSONArray jArray = jObject.getJSONArray("result");

                for (int i=0; i < jArray.length(); i++) {

                    try {

                        JSONObject oneObject = jArray.getJSONObject(i);

                        String timestamp = oneObject.getString("timeStamp");
                        String from = oneObject.getString("from");
                        String to = oneObject.getString("to");
                        String value = oneObject.getString("value");
                        String tokenSymbol = oneObject.getString("tokenSymbol");
                        String decimals = oneObject.getString("tokenDecimal");

                        ERC20Transaction transaction = new ERC20Transaction();
                        transaction.setFrom(from);
                        transaction.setTo(to);
                        transaction.setValue(value);
                        transaction.setTokenSymbol(tokenSymbol);
                        transaction.setTimestamp(timestamp);
                        transaction.setTokenDecimal(decimals);

                        transactions.add(transaction);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                multiTaskHandler.taskComplete();

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

    }

    private class UpdateTransactions extends AsyncTask{

        String call = "/api?module=account&action=txlist&address=" + NodeHolder.getInstance().getAccount().getAddress().getHex() + "&startblock=0&endblock=99999999&sort=asc&apikey=" + BuildVars.ETHERSCAN_API;

        public UpdateTransactions(String domainAPI){

            this.call = domainAPI + this.call;

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            return callEtherscanAPI(call); //returns json string

        }

        @Override
        public void onPostExecute(Object o){

            String result = (String) o;

            try {

                JSONObject jObject = new JSONObject(result);

                JSONArray jArray = jObject.getJSONArray("result");

                for (int i=0; i < jArray.length(); i++) {

                    try {

                        JSONObject oneObject = jArray.getJSONObject(i);

                        String timestamp = oneObject.getString("timeStamp");
                        String from = oneObject.getString("from");
                        String to = oneObject.getString("to");
                        String value = oneObject.getString("value");

                        Transaction transaction = new Transaction();
                        transaction.setFrom(from);
                        transaction.setTo(to);
                        transaction.setValue(value);
                        transaction.setTimestamp(timestamp);

                        transactions.add(transaction);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                multiTaskHandler.taskComplete();

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

    }

    private class UpdateBalance extends AsyncTask{

        String call = "/api?module=account&action=balance&address=" + NodeHolder.getInstance().getAccount().getAddress().getHex() + "&tag=latest&apikey=" + BuildVars.ETHERSCAN_API;

        public UpdateBalance(String domainAPI){

            this.call = domainAPI + this.call;

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            return callEtherscanAPI(call); //returns json string

        }

        @Override
        public void onPostExecute(Object o){

            String result = (String) o;

            try {

                JSONObject jObject = new JSONObject(result);

                String balanceInWei = jObject.getString("result");

                balanceTextView.setText(Convert.fromWei(balanceInWei, Convert.Unit.ETHER) + " ETH");

                multiTaskHandler.taskComplete();

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }

    }

    private String callEtherscanAPI(String call){

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            URL url = new URL(call);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {

                buffer.append(line+"\n");

            }

            return buffer.toString();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (connection != null) {

                connection.disconnect();

            }

            try {

                if (reader != null) {

                    reader.close();
                }

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

        return null;

    }

    public abstract class MultiTaskHandler {

        private int mTasksLeft;
        private boolean mIsCanceled = false;

        public MultiTaskHandler(int numOfTasks) {
            mTasksLeft = numOfTasks;
        }

        protected abstract void onAllTasksCompleted();

        public void taskComplete()  {
            mTasksLeft--;
            if (mTasksLeft==0 && !mIsCanceled) {
                onAllTasksCompleted();
            }
        }

        public void reset(int numOfTasks) {
            mTasksLeft = numOfTasks;
            mIsCanceled=false;
        }

        public void cancel() {
            mIsCanceled = true;

        }
    }


}


