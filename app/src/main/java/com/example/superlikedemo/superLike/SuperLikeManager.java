package com.example.superlikedemo.superLike;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.superlikedemo.R;
import com.example.superlikedemo.superLike.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

public class SuperLikeManager {
    private static final long duration = 1000;
    private static final int MESSAGE_CODE_REFRESH_ANIMATION = 1001;
    private static final int LONG_CLICK = 1002;
    private int[] mipmaps = new int[]{
            R.mipmap.emoji_1,
            R.mipmap.emoji_2,
            R.mipmap.emoji_3,
            R.mipmap.emoji_4,
            R.mipmap.emoji_5,
            R.mipmap.emoji_6,
            R.mipmap.emoji_7,
            R.mipmap.emoji_8,
            R.mipmap.emoji_9,
            R.mipmap.emoji_10
    };
    private Activity activity;

    private BitmapProvider.Provider provider;
    private long lastClickTimeMillis;
    private int textTopMargin = 100;
    private int likeCount;
    private ImageView newImageView;
    private ViewGroup viewGroup;
    private View currentView;

    Handler animationHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MESSAGE_CODE_REFRESH_ANIMATION) {
                newImageView.setVisibility(View.GONE);
            }
            return false;
        }
    });

    private Handler longClickHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LONG_CLICK:
                    if (currentView != null) {
                        showSuperLike(currentView);
                    }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Runnable longClickTask = new Runnable() {
        @Override
        public void run() {
            if (longClickHanlder!=null){
                longClickHanlder.sendEmptyMessage(LONG_CLICK);
                longClickHanlder.postDelayed(this, 100);
            }
        }
    };


    public SuperLikeManager(Activity activity) {
        this.activity = activity;
        newImageView = new ImageView(activity);
        viewGroup = activity.findViewById(android.R.id.content);
        setProvider(BitmapProviderFactory.getHDProvider(activity));
    }

    /**
     * 设置长按的逻辑
     */
    public void longClickShowSuperLike(View v) {
        currentView = v;
        if (longClickHanlder != null) {
            longClickHanlder.postDelayed(longClickTask, 100);
        }
    }

    /**
     * 取消长按的效果
     */
    public void cancleSuperLikeLongClick() {
        if (longClickHanlder != null) {
            longClickHanlder.removeCallbacks(longClickTask);
        }
        currentView = null;
    }

    public void showSuperLike(View v) {
        if (likeCount==1) {
            currentView = null;
            currentView = v;
            initTextView(v);
        }
        if (newImageView != null && newImageView.getVisibility() != View.VISIBLE) {
            newImageView.setVisibility(View.VISIBLE);
        }

        calculateCombo();
        showEmoji(v);
        showText();
        if (animationHandler != null) {
            animationHandler.removeMessages(MESSAGE_CODE_REFRESH_ANIMATION);
            animationHandler.sendEmptyMessageDelayed(MESSAGE_CODE_REFRESH_ANIMATION, duration);
        }
    }

    private void calculateCombo() {
        if (System.currentTimeMillis() - lastClickTimeMillis < duration) {
            likeCount++;
        } else {
            likeCount = 1;
        }
        lastClickTimeMillis = System.currentTimeMillis();
    }

    private void showEmoji(View v) {
        ParticleSystem ps = new ParticleSystem(activity, 80, mipmaps, 800); // 最多显示表情数 显示时间
        ps.setSpeedModuleAndAngleRange(0.4f, 0.5f, 160, 300);
        ps.setAcceleration(0.0001f, 225); // 表情加速度
        ps.setFadeOut(200, new AccelerateInterpolator());
        int showEmojiNum;
        if (likeCount==1){
            showEmojiNum = 5;
        } else {
            showEmojiNum = (int)(3+Math.random()*(7-3+1));
        }
        ps.oneShot(v, showEmojiNum, new DecelerateInterpolator()); // 每次点击发射数
    }

    private void setProvider(BitmapProvider.Provider provider) {
        this.provider = provider;
    }

    private BitmapProvider.Provider getProvider() {
        if (provider == null) {
            provider = new BitmapProvider.Builder(activity)
                    .build();
        }
        return provider;
    }

    private void initTextView(View v) {
        int[] itemPosition = new int[2];
        v.getLocationInWindow(itemPosition);

        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;

        // 应用区
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);

        // 绘制区
        Rect outRect2 = new Rect();
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect2);

        int rightMargin = widthPixels - itemPosition[0] - v.getWidth();
        int titleBarHeight = outRect.height() - outRect2.height();
        int topMargin = itemPosition[1] - titleBarHeight - v.getHeight() - textTopMargin;

        newImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.topMargin = topMargin;
        layoutParams.rightMargin = rightMargin;
        viewGroup.removeView(newImageView);
        viewGroup.addView(newImageView, layoutParams);
    }

    private void showText() {
        Bitmap bitmap = mergeBitmap(likeCount);
        newImageView.setImageBitmap(bitmap);
    }


    /**
     *  根据连击数添加bitmap
     */
    private Bitmap mergeBitmap(int num) {
        List<Bitmap> bitmapList = new ArrayList<>();
        int count = num;
        while (count > 0) {
            int number = count % 10;
            Bitmap bitmap = getProvider().getNumberBitmap(number);
            count = count / 10;
            bitmapList.add(0, bitmap);
        }

        int level = num / 10;
        if (level > 2) {
            level = 2;
        }
        Bitmap levelBitmap = getProvider().getLevelBitmap(level);
        bitmapList.add(levelBitmap);
        return mergeThumbnailBitmap(bitmapList);
    }

    /**
     * 根据连击数合并bitmap
     */
    private Bitmap mergeThumbnailBitmap(List<Bitmap> bitmapList) {
        int totalWidth = 0;
        for (int i = 0; i < bitmapList.size(); i++) {
            totalWidth += bitmapList.get(i).getWidth();
        }
        Bitmap bitmap = Bitmap.createBitmap(totalWidth, bitmapList.get(bitmapList.size() - 1).getHeight(), bitmapList.get(0).getConfig());

        Canvas canvas = new Canvas(bitmap);

        totalWidth = 0;
        for (int i = 0; i < bitmapList.size(); i++) {
            canvas.drawBitmap(bitmapList.get(i), totalWidth, 0, null);
            totalWidth += bitmapList.get(i).getWidth();
        }
        return bitmap;
    }
}
