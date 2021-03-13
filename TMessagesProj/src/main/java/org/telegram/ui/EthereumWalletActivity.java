package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.ethereum.geth.Geth;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.telegram.ethergramUtils.GethNodeHolder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.webrtc.ContextUtils.getApplicationContext;

public class EthereumWalletActivity extends BaseFragment {

    protected View actionBarBackground;

    private CircleImageView ethlogoimage;
    private TextView titleTextView;
    private TextView messageTextView;
    private LinearLayout ethwalletheader;

    File dir;

    public EthereumWalletActivity(File dir) {

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
        actionBar.setTitle(LocaleController.getString("PeopleNearby", R.string.PeopleNearby));
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

        View ethwalletheaderView = LayoutInflater.from(context).inflate(R.layout.ethwalletheader, null);

        ethwalletheader = (LinearLayout) ethwalletheaderView.findViewById(R.id.ethwalletheader);

        ethlogoimage = (CircleImageView) ethwalletheaderView.findViewById(R.id.ethlogo);
        ethlogoimage.setImageResource(R.drawable.ethtoken);

        ethlogoimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GethNodeHolder.getInstance().getNode() == null) {

                    messageTextView.setText("Syncing your Ethereum node...");

                    new CreateNode().execute();

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

        if(GethNodeHolder.getInstance().getNode() != null){

            scaleDownAnimation();
            messageTextView.setText("Node running on http://localhost:" + GethNodeHolder.getInstance().getNode().getNodeInfo().getListenerPort());

        }else{

            messageTextView.setText("Click on the Ethereum logo to get started.");

        }

        frameLayout.addView(ethwalletheader);

        return fragmentView;

    }

    private void scaleDownAnimation(){

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ethlogoimage, "scaleX", 0.5f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ethlogoimage, "scaleY", 0.5f);
        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);

        ObjectAnimator moveUpY = ObjectAnimator.ofFloat(ethlogoimage, "translationY", -155);
        moveUpY.setDuration(1000);

        ObjectAnimator moveuptext = ObjectAnimator.ofFloat(titleTextView, "translationY", -305);
        moveuptext.setDuration(1000);
        ObjectAnimator moveuptext2 = ObjectAnimator.ofFloat(messageTextView, "translationY", -305);
        moveuptext2.setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();
        AnimatorSet moveUp = new AnimatorSet();

        scaleDown.play(scaleDownX).with(scaleDownY).with(moveUpY).with(moveuptext).with(moveuptext2);

        scaleDown.start();

    }

    private class CreateNode extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            try {

                NodeConfig nc = new NodeConfig();
                Node node = Geth.newNode(dir + "/.ethNode", nc);
                node.start();

                GethNodeHolder gethNode = GethNodeHolder.getInstance();
                gethNode.setNode(node);

                //FIXME add animation https://stackoverflow.com/questions/29911046/android-zoom-animation-using-animatorset

                getParentActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        scaleDownAnimation();

                    }
                });

                messageTextView.setText("Node running on http://localhost:" + gethNode.getNode().getNodeInfo().getListenerPort());

            } catch (Exception e) {

                messageTextView.setText("Cannot create an Ethereum node. \n" + e.getMessage());

            }

            return null;
        }
    }

}


