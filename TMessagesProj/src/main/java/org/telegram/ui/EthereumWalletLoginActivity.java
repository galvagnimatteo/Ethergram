package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import de.hdodenhof.circleimageview.CircleImageView;

import static org.telegram.ethergramUtils.RippleBackground.dpToPx;

public class EthereumWalletLoginActivity extends BaseFragment {

    protected View actionBarBackground;

    private LinearLayout ethwalletheader;
    private LinearLayout loginLayout;

    private CircleImageView ethlogoimage;
    private TextView titleTextView;
    private TextView messageTextView;
    private EditTextBoldCursor password;

    File dir;

    public EthereumWalletLoginActivity(File dir) {

        super();
        this.dir = dir;

    }


    @Override
    public View createView(Context context) {

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
                LayoutParams layoutParams = (LayoutParams) actionBarBackground.getLayoutParams();
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

        ethwalletheader = (LinearLayout) ethwalletheaderView.findViewById(R.id.ethwalletheader); //header layout

        ethlogoimage = (CircleImageView) ethwalletheaderView.findViewById(R.id.ethlogo);
        ethlogoimage.setImageResource(R.drawable.ethtoken);
        RelativeLayout.LayoutParams logoparams = (RelativeLayout.LayoutParams) ethlogoimage.getLayoutParams();
        logoparams.height = dpToPx(110, context);
        logoparams.width = dpToPx(110, context);
        ethlogoimage.setLayoutParams(logoparams);

        RippleBackground background = (RippleBackground) ethwalletheaderView.findViewById(R.id.content);
        LinearLayout.LayoutParams bgparams = (LinearLayout.LayoutParams) background.getLayoutParams();
        bgparams.height = dpToPx(140, context);
        background.setLayoutParams(bgparams);

        titleTextView = (TextView) ethwalletheaderView.findViewById(R.id.titleTextView);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setText("Your Ethereum wallet");

        messageTextView = (TextView) ethwalletheaderView.findViewById(R.id.messageTextView);
        messageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        messageTextView.setGravity(Gravity.CENTER);
        messageTextView.setText("Unlock your wallet");

        frameLayout.addView(ethwalletheader);

        //------------------------------------HEADER END--------------------------------------------

        //--------------------------------------LOGIN-----------------------------------------------

        View ethwalletcredentialsView = LayoutInflater.from(context).inflate(R.layout.ethwalletcredentials, null);

        loginLayout = (LinearLayout) ethwalletcredentialsView.findViewById(R.id.ethwalletcredentials); //header layout

        password = (EditTextBoldCursor) ethwalletcredentialsView.findViewById(R.id.password);
        password.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        password.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        password.setCursorSize(AndroidUtilities.dp(20));
        password.setCursorWidth(1.5f);
        password.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        password.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        password.setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
        password.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        password.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        password.setMaxLines(1);
        password.setPadding(0, 0, 0, 0);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password.setTypeface(Typeface.DEFAULT);
        password.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);

        frameLayout.addView(loginLayout);

        //-----------------------------------LOGIN END----------------------------------------------

        return fragmentView;

    }

}


