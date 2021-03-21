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

import org.ethereum.geth.Account;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.telegram.ethergramUtils.NodeHolder;
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
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

import de.hdodenhof.circleimageview.CircleImageView;
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

    private ListView transactionsListView;
    private TextView balanceTextView;

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

        if(NodeHolder.getInstance().getAccount() != null){ //user already logged

            messageTextView.setText(NodeHolder.getInstance().getAccount().getAddress().getHex());

            fillWalletViewer();

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

                    }

                });

                new ConnectNode(true).execute();

            }

        }

    }

    //Syncing a node needs to be done in background to not block the UI.
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

                        mainLayout.addView(walletViewer);

                    }

                });

                return false;

            }

        }

        @Override
        public void onPostExecute(Object result){

            boolean isSucceeded = (boolean) result;

            if(isSucceeded){ //If starting node is succeeded

                fillWalletViewer();

            }

        }

    }

    public void fillWalletViewer(){

        try {

            NodeHolder.getInstance().getNode().ethGetBalance(NodeHolder.getInstance().getAccount().getAddress().getHex(), DefaultBlockParameterName.LATEST)
                    .observable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(balance -> {
                        final BigInteger bigInt = balance.getBalance();
                        final BigDecimal etherBalance = Convert.fromWei(bigInt.toString(), Convert.Unit.ETHER);

                        getParentActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                balanceTextView.setText(etherBalance + " ETH"); //returns 0?? //FIXME

                                mainLayout.addView(walletViewer);

                            }

                        });

                    });

        }catch(Exception e){

            getParentActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    messageTextView.setText("Cannot fetch balance\n" + e.getMessage());

                }

            });

        }

    }

}


