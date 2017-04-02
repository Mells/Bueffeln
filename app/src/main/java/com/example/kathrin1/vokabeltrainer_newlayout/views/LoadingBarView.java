package com.example.kathrin1.vokabeltrainer_newlayout.views;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kathrin1.vokabeltrainer_newlayout.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A bar for displaying loading in progress
 */

public class LoadingBarView extends RelativeLayout
{

    public static final int DEFAULT_ACTIVATION_DELAY = 500;

    private TextView loadingText;
    private ProgressBar loadingBar;

    private boolean isActive = false;
    private Timer timer;

    private Handler handler;

    // TODO:  DOCUMENT

    public LoadingBarView(Context context)
    {
        super(context);
        init();
    }

    public LoadingBarView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.view_loading_bar_layout, this);
        loadingText = (TextView) findViewById(R.id.loading_text);
        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        handler = new Handler(Looper.getMainLooper());
        hide();
    }

    public void show()
    {
        setVisibility(View.VISIBLE);
    }

    public void hide()
    {
        setVisibility(View.INVISIBLE);
    }

    public void setText(String text)
    {
        loadingText.setText(text);
    }

    public void activate(String text)
    {
        isActive = true;
        destroyTimer();
        if (text != null)
            setText(text);
        show();
    }

    public void deactivate()
    {
        isActive = false;
        destroyTimer();
        hide();
    }

    public void activateWithDelay(String text)
    {
        activateWithDelay(text, DEFAULT_ACTIVATION_DELAY);
    }

    public void activateWithDelay(final String text, long delay)
    {
        if (timer != null)
            destroyTimer();

        isActive = true;

        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (isActive)
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (isActive)
                                activate(text);
                            destroyTimer();
                        }
                    });
                }
            }
        }, delay);
    }

    private void destroyTimer()
    {
        if (timer != null)
        {
            timer.cancel();
            timer.purge();
        }
        timer = null;
    }


}
