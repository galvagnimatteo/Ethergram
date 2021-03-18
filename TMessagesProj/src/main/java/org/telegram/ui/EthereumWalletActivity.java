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
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ethereum.geth.Account;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.telegram.ethergramUtils.GethNodeHolder;
import org.telegram.ethergramUtils.RippleBackground;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.io.File;
import java.nio.file.Files;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.telegram.ethergramUtils.RippleBackground.dpToPx;

public class EthereumWalletActivity extends BaseFragment {

    protected View actionBarBackground;

    private CircleImageView ethlogoimage;
    private TextView titleTextView;
    private TextView messageTextView;
    private LinearLayout ethwalletheader;
    private LinearLayout registerlayout;
    private EditTextBoldCursor password;
    private TextView button;
    private RippleBackground pulseanimation;

    private Context context;

    File dir;

    public EthereumWalletActivity(File dir) {

        super();
        this.dir = dir;

    }


    @Override
    public View createView(Context context) {

        this.context = context;

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
            }
        };

        actionBarBackground.setAlpha(0.0f);
        frameLayout.addView(actionBarBackground, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        frameLayout.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        //---------------------------------------HEADER---------------------------------------------

        View ethwalletheaderView = LayoutInflater.from(context).inflate(R.layout.ethwalletheader, null);

        ethwalletheader = (LinearLayout) ethwalletheaderView.findViewById(R.id.ethwalletheader);

        ethlogoimage = (CircleImageView) ethwalletheaderView.findViewById(R.id.ethlogo);
        ethlogoimage.setImageResource(R.drawable.ethtoken);

        pulseanimation = (RippleBackground) ethwalletheaderView.findViewById(R.id.content);

        ethlogoimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GethNodeHolder.getInstance().getNode() == null) {

                    pulseanimation.stopRippleAnimation();

                    if (new File(dir + "/keystore").exists()) {

                        messageTextView.setText("Syncing your Ethereum node...");

                        new SyncNode(false).execute();


                    }else{

                        messageTextView.setText("Creating your node...");

                        new SyncNode(true).execute();

                    }

                }

            }
        });

        titleTextView = (TextView) ethwalletheaderView.findViewById(R.id.titleTextView);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setText("Your Ethereum wallet");

        messageTextView = (TextView) ethwalletheaderView.findViewById(R.id.messageTextView);
        messageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        messageTextView.setGravity(Gravity.CENTER);

        if (GethNodeHolder.getInstance().getNode() != null) { //Session started, already logged in

            pulseanimation.stopRippleAnimation();

            scaleDownAnimation(context);

            messageTextView.setText(GethNodeHolder.getInstance().getAccount().getAddress().getHex());

        } else { //Session not started, node not running, division between no account created and account created

            pulseanimation.startRippleAnimation();

            if (new File(dir + "/keystore").exists()) {

                messageTextView.setText("Click on the Ethereum logo to sync your node.");

            }else{

                messageTextView.setText("Click on the Ethereum logo to create an account.");

            }

        }

        frameLayout.addView(ethwalletheader); //Header composed of icon and 2 textview

        //------------------------------------HEADER END--------------------------------------------

        //--------------------------------------LOGIN-----------------------------------------------

        View ethwalletcredentialsView = LayoutInflater.from(context).inflate(R.layout.ethwalletcredentials, null);

        registerlayout = (LinearLayout) ethwalletcredentialsView.findViewById(R.id.ethwalletcredentials); //header layout

        password = (EditTextBoldCursor) ethwalletcredentialsView.findViewById(R.id.password);
        password.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        password.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        password.setCursorSize(AndroidUtilities.dp(20));
        password.setCursorWidth(1.5f);
        password.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        password.setHint("Password");
        password.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        password.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        password.setMaxLines(1);
        password.setPadding(0, 0, 0, 0);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password.setTypeface(Typeface.DEFAULT);
        password.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);

        button = (TextView) ethwalletcredentialsView.findViewById(R.id.button);
        button.setText("Confirm");
        button.setTextColor(0xffffffff);
        button.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        button.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4), 0xff50a8eb, 0xff439bde));

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new CreateAccount(password.getText().toString()).execute();

            }

        });

        //-----------------------------------LOGIN END----------------------------------------------

        return fragmentView;

    }

    //Animation that scales down ETH logo.
    private void scaleDownAnimation(Context context){

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ethlogoimage, "scaleX", 0.5f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ethlogoimage, "scaleY", 0.5f);
        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);

        ObjectAnimator moveUpY = ObjectAnimator.ofFloat(ethlogoimage, "translationY", -dpToPx(55, context));
        moveUpY.setDuration(1000);

        ObjectAnimator moveuptitle = ObjectAnimator.ofFloat(titleTextView, "translationY", -dpToPx(110, context));
        moveuptitle.setDuration(1000);
        ObjectAnimator moveupmessage = ObjectAnimator.ofFloat(messageTextView, "translationY", -dpToPx(110, context));
        moveupmessage.setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();

        scaleDown.play(scaleDownX).with(scaleDownY).with(moveUpY).with(moveuptitle).with(moveupmessage);

        scaleDown.start();

    }

    private class CreateAccount extends AsyncTask{

        private String password;

        public CreateAccount(String password){

            this.password = password;

        }

        @Override
        protected Object doInBackground(Object[] objects){

            try {

                GethNodeHolder gethNodeHolder = GethNodeHolder.getInstance();
                Node gethNode = gethNodeHolder.getNode();

                if (gethNode != null) {

                    KeyStore ks = new KeyStore(dir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);
                    Account newAccount = ks.newAccount(this.password);

                    gethNodeHolder.setAccount(newAccount);

                    Account account = gethNodeHolder.getAccount();
                    //accDisplayTextView.setText("Here is your Account Address: " + account.getAddress().getHex());

                    messageTextView.setText(account.getAddress().getHex());

                }

            } catch (Exception e) {

                messageTextView.setText("Cannot create an Ethereum wallet. \n" + e.getMessage());

            }

            return null;

        }

    }

    //Creating a node needs to be done in background to not block the UI.
    private class SyncNode extends AsyncTask{

        private boolean displayRegisterLayout;

        public SyncNode(boolean displayRegisterLayout){

            this.displayRegisterLayout = displayRegisterLayout;

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {

                NodeConfig nc = new NodeConfig();
                Node node = Geth.newNode(dir + "/.ethNode", nc);
                node.start();

                GethNodeHolder gethNode = GethNodeHolder.getInstance();
                gethNode.setNode(node);

                getParentActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        scaleDownAnimation(context);

                    }

                });

                messageTextView.setText("Node running on http://localhost:" + gethNode.getNode().getNodeInfo().getListenerPort()); //TODO change text here

            } catch (Exception e) {

                messageTextView.setText("Cannot create an Ethereum node. \n" + e.getMessage());

            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o){

            if(displayRegisterLayout){

                getParentActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        FrameLayout frameLayout = (FrameLayout) fragmentView;

                        frameLayout.addView(registerlayout);

                    }

                });

            }else{

                //display transactions

            }

        }

    }

}


