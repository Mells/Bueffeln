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

    /**
     * Constructor.  Calls superconstructor and initializes local views.
     *
     * @param context Context to pass along to superconstructor.
     */
    public LoadingBarView(Context context)
    {
        super(context);
        init();
    }

    /**
     * Constructor.  Calls superconstructor and initializes local views.
     *
     * @param context Context to pass along to superconstructor.
     * @param attrs   Attribute set to pass along to superconstructor.
     */
    public LoadingBarView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    /**
     * Initializes all local views, and sets the visibility to 'invisible'.
     */
    private void init()
    {
        inflate(getContext(), R.layout.exercise_view_loading_bar_layout, this);
        loadingText = (TextView) findViewById(R.id.loading_text);
        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        handler = new Handler(Looper.getMainLooper());
        hide();
    }

    /**
     * Makes this view visible.
     */
    public void show()
    {
        setVisibility(View.VISIBLE);
    }

    /**
     * Makes this view invisible.
     */
    public void hide()
    {
        setVisibility(View.INVISIBLE);
    }

    /**
     * Sets the text in the local text view to the given text.  This does not activate the
     * loading bar.
     *
     * @param text The text to set.
     */
    public void setText(String text)
    {
        loadingText.setText(text);
    }

    /**
     * Activates and shows the loading bar, using the given text as the text to display.
     * Loading bar will remain active until deactivated with {@link LoadingBarView#deactivate()}.
     *
     * @param text The text to set.  If null, does not change the existing text.
     */
    public void activate(String text)
    {
        isActive = true;
        destroyTimer();
        if (text != null)
            setText(text);
        show();
    }

    /**
     * Deactivates and hides the loading bar.
     */
    public void deactivate()
    {
        isActive = false;
        destroyTimer();
        hide();
    }

    /**
     * Calls {@link LoadingBarView#activate(String)} after a delay with the given text,
     * unless it is deactivated in the meantime.  If {@link LoadingBarView#deactivate()} is called
     * before the full delay duration, then the loading bar will not appear.
     *
     * @param text The text to set.  If null, does not change the existing text.
     */
    public void activateWithDelay(String text)
    {
        activateWithDelay(text, DEFAULT_ACTIVATION_DELAY);
    }

    /**
     * Calls {@link LoadingBarView#activate(String)} after the given delay with the given text,
     * unless it is deactivated in the meantime.  If {@link LoadingBarView#deactivate()} is called
     * before the full delay duration, then the loading bar will not appear.
     *
     * @param text  The text to set.  If null, does not change the existing text.
     * @param delay The amount to delay activation of the loading bar, in milliseconds.
     */
    public void activateWithDelay(final String text, long delay)
    {
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
                            if (isActive) // Redundant active check, to account for threading issues
                                activate(text);
                            destroyTimer();
                        }
                    });
                }
            }
        }, delay);
    }

    /**
     * Destroys the timer object associated with activation delays.  This prevents any timer-gated
     * functions from occurring.
     */
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
