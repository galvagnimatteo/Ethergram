package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.telegram.ethergramUtils.GethNodeHolder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.webrtc.ContextUtils.getApplicationContext;

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

    private LinearLayout transactionsLayout;

    private ListView transactions;

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

        //-------------------------------------LOGIC------------------------------------------------

        if(GethNodeHolder.getInstance().getAccount() != null){ //user already logged

            messageTextView.setText(GethNodeHolder.getInstance().getAccount().getAddress().getHex());

        }else{

            if (new File(dir + "/keystore").exists()) { //login

                messageTextView.setText("Unlock your wallet");

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            KeyStore ks = new KeyStore(dir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);

                            ks.unlock(ks.getAccounts().get(0), password.getText().toString());

                            GethNodeHolder.getInstance().setAccount(ks.getAccounts().get(0));

                            Account account = GethNodeHolder.getInstance().getAccount();

                            messageTextView.setText(account.getAddress().getHex());

                            ((ViewManager) passwordLayout.getParent()).removeView(passwordLayout);

                        }catch(Exception e){

                            messageTextView.setText("Error while unlocking your Ethereum wallet.\n" + e.getStackTrace().toString());

                        }

                    }
                });

            } else { //account creation

                messageTextView.setText("Create your wallet");

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            KeyStore ks = new KeyStore(dir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);
                            Account newAccount = ks.newAccount(password.getText().toString());

                            GethNodeHolder.getInstance().setAccount(newAccount);

                            messageTextView.setText(newAccount.getAddress().getHex());

                            ((ViewManager) passwordLayout.getParent()).removeView(passwordLayout);

                        }catch(Exception e){

                            messageTextView.setText("Error while creating an Ethereum wallet.\n" + e.getStackTrace().toString());

                        }

                    }
                });

            }

            mainLayout.addView(passwordLayout);

        }

        return fragmentView;

    }

    //Syncing a node needs to be done in background to not block the UI.
    private class SyncNode extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {

            try {

                NodeConfig nc = new NodeConfig();
                Node node = Geth.newNode(dir + "/.ethNode", nc);
                node.start();

                GethNodeHolder gethNode = GethNodeHolder.getInstance();
                gethNode.setNode(node);

            } catch (Exception e) {

                //display error

            }

            return null;

        }

    }

}


