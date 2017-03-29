package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ACTRModel;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.LearnModel;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ModelMath;
import com.example.kathrin1.vokabeltrainer_newlayout.network.NetworkError;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxBuilder;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.example.kathrin1.vokabeltrainer_newlayout.views.LoadingBarView;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controls an exercise that tests users on their vocabulary, and uses training exercises to
 * introduce new words or to review words that require more work.
 */
public class TrainAndTest extends AppCompatActivity
{

    public static final String LOG_TAG = "[TrainAndTest]";
    public static final int TRAIN_L1_DELAY = 1000;


    private LearnModel model;
    private DatabaseManager dbManager;
    private SessionObject currentSession;
    private Timer trainDelayTimer;
    private ObjectAnimator trainTextAnimator;

    private VocObject currentWord;
    private InterxBuilder currInterx;
    private String currExerciseType;

    private final Set<VocObject> wordsToIgnore = new HashSet<>();

    private Handler handler;

    //------------------------LAYOUT ELEMENTS------------------------
    private TextView txt_voc;
    private TextView txt_bsp;
    private TextView txt_feedback;
    private EditText edit_solution;
    private RelativeLayout feedbackLayout, inputLayout;
    private Button btn_next, btn_solution, btn_auswahl, btn_hint;

    private LoadingBarView loadingBar;

    private SlidingLayer mSlidingLayer;
    //-----------------------------------------------------------

    private String book;
    private String chapter;
    private String unit;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.train_and_test);

        dbManager = DatabaseManager.build(this);

        buildTabLayout();
        initializeUI();

        // on initiating the activity
        setBookValues();

        // Get the handler for the UI thread
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * {@inheritDoc}
     * <p>
     * The model is created in onStart(), which starts a new session
     * </p>
     */
    @Override
    protected void onStart()
    {
        super.onStart();

        initializeModel();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The model is destroyed in onStop(), ending the current session and storing all data.
     * </p>
     */
    @Override
    protected void onStop()
    {
        super.onStop();

        // Finish the current session
        currentSession.finish(new Date());

        // Attempt to push data to remote server and forget
        model.pushToRemote(null);

        // Save the model data to the local database before destroying the model
        model.saveToDatabaseASync(false, new LearnModel.CalcListener()
        {
            @Override
            public void onCompletion()
            {
                model.destroy();
                model = null;
                dbManager.destroy();
                dbManager = null;
            }
        });
    }

    /**
     * Performs all actions necessary to initialize the model and start a new study session.
     * Once completed, a word is chosen and displayed.
     */
    private void initializeModel()
    {

        currentSession = SessionObject.buildUnfinished(-1, new Date());

        // Build the model
        model = ACTRModel.build(this, dbManager);

        // Show the loading bar
        loadingBar.activateWithDelay(getString(R.string.TrainText_InitializeModel));

        // First, initialize the model.  Then set the restrictions for word selection, and
        // check that the Parse ID is known for all words in the dictionary, and retrieve any
        // that are missing.  If unsuccessful, calculate the next word to present and do not
        // do the following steps.
        // Once completed retrieving missing Parse IDs, pull all updated data from the remote
        // database, if able.  If successful, save the database.
        // Whether successful or not, calculate the next work to present, and present it.
        model.initializeASync(new LearnModel.CalcListener()
        {
            @Override
            public void onCompletion()
            {
                // Set restrictions for word selection
                model.setRestrictions(book, chapter, unit);

                // Add the new session to the model
                model.addNewSession(currentSession);

                // Change the text on the loading bar
                loadingBar.activateWithDelay(getString(R.string.TrainText_InitialDownload));

                // Find any missing Parse IDs, and query for the server for any found
                model.getUnknownParseIDs(new LearnModel.ParseResponseListener()
                {
                    @Override
                    public void onResponse(NetworkError error)
                    {
                        // If an error occurred while attempting to get Parse IDs, just
                        // skip pulling from remote and display the next word
                        if (error != null)
                        {
                            getNextWord();
                            return;
                        }

                        // Change the text on the loading bar
                        loadingBar.activate(getString(R.string.TrainTest_LoadingModel));

                        // Get any updates from the server
                        model.pullFromRemote(new LearnModel.ParseResponseListener()
                        {
                            @Override
                            public void onResponse(NetworkError error)
                            {
                                // If no error occurred, asynchronously save the database and forget
                                if (error == null)
                                    model.saveToDatabaseASync(false, null);

                                // Get and display the next word
                                getNextWord();
                            }
                        });

                    }
                });
            }
        });

    }

    /**
     * Asynchronously calculates the next word to present, and then displays it
     */
    private void getNextWord()
    {
        loadingBar.activateWithDelay(getString(R.string.TrainTest_CalculatingWord));
        model.calculateNextWordASync(new LearnModel.WordSelectionListener()
        {
            @Override
            public void onSelection(VocObject vocObject)
            {
                currExerciseType = vocObject.getActivation() == null ||
                                   vocObject.getActivation() == Float.NEGATIVE_INFINITY
                                   ? InterxObject.EXERCISE_TRAIN
                                   : InterxObject.EXERCISE_TEST;
                displayWord(vocObject);
                loadingBar.deactivate();
            }
        }, wordsToIgnore);
    }

    /**
     * Performs all actions necessary to present the given word in the current presentation mode.
     *
     * @param word The word to present
     */
    private void displayWord(VocObject word)
    {
        // Destroy any existing timers
        destroyTrainDelayTimer();

        // Clear feedback text
        txt_feedback.setText("");

        // If a word was found successfully
        if (word != null)
        {
            // Mark the given word as the current word
            currentWord = word;
            Log.v(LOG_TAG, "Word to display = " + word.toString());

            // Find a suitable example sentence for the word
            SentObject sentence = dbManager.getExampleSentence(word);

            currInterx = new InterxBuilder().setCharCount(sentence.getSentence().length())
                                            .setExerciseType(currExerciseType)
                                            .setTimestamp(new Date())
                                            .setWord(currentWord)
                                            .setSession(currentSession);

            // Display the word depending on the current exercise type
            switch (currExerciseType)
            {
                // If this should be a training exercise, emphasize the word in the example sentence
                // and display the L1 translation after a delay.
                case InterxObject.EXERCISE_TRAIN:
                    toggleOptionalUIElements(View.INVISIBLE);

                    txt_bsp.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.replaceWordInSentence(sentence, word, "<b><big>%s</big></b>")));
                    scheduleTrainDelayTimer(word.getTranslation());

                    // Set interaction values on training exercise immediately
                    currInterx.setLatency((int) ModelMath.predictedRT(sentence.getSentence().length(),
                                                                      word.getActivation()))
                              .setResult(InterxObject.RESULT_SUCCESS);
                    model.addNewInteraction(currInterx.buildWithoutInserting());
                    model.recalculateActivationASync(null);

                    break;

                // If this should be a testing exercise, blank out the word in the example sentence
                // and display the L1 translation immediately.
                case InterxObject.EXERCISE_TEST:
                    toggleOptionalUIElements(View.VISIBLE);

                    txt_bsp.setText(ExerciseUtils.deleteWordFromSentence(sentence, word));
                    txt_voc.setText(word.getTranslation());
                    break;
            }
        }
        // If for some reason there is no word to present, display an error
        else
        {
            Log.v(LOG_TAG, "Missing word.");

            txt_bsp.setText(R.string.TrainTest_MissingWord);
            txt_voc.setText("");
        }
    }

    private void toggleOptionalUIElements(int visibility)
    {
        btn_hint.setVisibility(visibility);
        btn_solution.setVisibility(visibility);
        feedbackLayout.setVisibility(visibility);
        inputLayout.setVisibility(visibility);

        if (visibility == View.VISIBLE)
            edit_solution.requestFocus();
    }

    /**
     * Performs all initialization of the UI
     */
    private void initializeUI()
    {

        // ----------------SLIDER---------------------

        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSlidingLayer.getLayoutParams();
        rlp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mSlidingLayer.setLayoutParams(rlp);


        //The action buttons at the bottom
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_solution = (Button) findViewById(R.id.btn_solution);
        btn_auswahl = (Button) findViewById(R.id.btn_auswahl);
        btn_hint = (Button) findViewById(R.id.btn_hint);

        //the input field
        edit_solution = (EditText) findViewById(R.id.edit_solution);
        inputLayout = (RelativeLayout) findViewById(R.id.lay_eingabe);

        //feedback field also used for information like hint and solution
        txt_feedback = (TextView) findViewById(R.id.txt_feedback);
        feedbackLayout = (RelativeLayout) findViewById(R.id.lay_feedback);


        txt_voc = (TextView) findViewById(R.id.txt_voc);
        txt_bsp = (TextView) findViewById(R.id.txt_bsp);


        txt_feedback.setText("");

        btn_next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onNextClicked();
            }
        });

        //If Kontext
        btn_hint.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onHintClicked();
            }
        });

        btn_solution.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSolutionClicked();
            }
        });


        edit_solution.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    onAnswerEntered();
                }
                return false;
            }
        });

        edit_solution.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO:  Accommodate for situations in which the field is erased after entering
                if (s.length() == 0 && after > 0 && currInterx != null)
                    currInterx.markLatency(new Date());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        btn_auswahl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mSlidingLayer.openLayer(true);
            }

        });

        mSlidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener()
        {
            @Override
            public void onOpen()
            {
            }

            @Override
            public void onShowPreview()
            {
            }

            @Override
            public void onClose()
            {
            }

            @Override
            public void onOpened()
            {
            }

            @Override
            public void onPreviewShowed()
            {
            }

            @Override
            public void onClosed()
            {
                onSlidingLayerClosed();
            }
        });

        loadingBar = (LoadingBarView) findViewById(R.id.lay_loading);
    }

    /**
     * Actions to perform when the 'Solution' button is clicked.
     */
    private void onSolutionClicked()
    {
        if (currentWord == null)
            return;

        txt_feedback.setText(ExerciseUtils.fromHtml(
                getString(R.string.TrainTest_Answer,
                          currentWord.getTranslation(),
                          currentWord.getVoc())));
    }

    /**
     * Actions to perform when the 'Next' button is clicked.
     */
    private void onNextClicked()
    {
        wordsToIgnore.add(currentWord);
        getNextWord();
    }

    /**
     * Actions to perform when the 'Hint' button is clicked.
     */
    private void onHintClicked()
    {

    }

    /**
     * Actions to perform when the sliding layer (chapter selector) is closed.
     */
    private void onSlidingLayerClosed()
    {
        setBookValues();

        model.setRestrictions(book, chapter, unit);

        getNextWord();
    }

    /**
     * Actions to perform when an answer has been entered.
     */
    private void onAnswerEntered()
    {
        String input = edit_solution.getText().toString();
        if (ExerciseUtils.isAnswerCorrect(currentWord, input, false))
        {
            txt_feedback.setText(R.string.TrainText_Correct);
            if (currInterx != null)
                currInterx.setResult(InterxObject.RESULT_SUCCESS);
        }
        else
        {
            txt_feedback.setText(ExerciseUtils.fromHtml(
                    getString(R.string.TrainText_Incorrect, input)));
            if (currInterx != null)
                currInterx.setResult(InterxObject.RESULT_FAILURE);
        }

        if (currInterx != null)
        {
            model.addNewInteraction(currInterx.buildWithoutInserting());
            currInterx = null;
        }

        edit_solution.getText().clear();
    }

    private void buildTabLayout()
    {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Book 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Book 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Book 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                                             (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(setCurrentBook());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });
    }

    //------------------------BOOK, CHAPTER AND UNIT ------------------------
    private void setBookValues()
    {
        String pref_book = PreferenceManager.getDefaultSharedPreferences(this)
                                            .getString("book", "0");

        if (!pref_book.equals("0"))
        {
            book = pref_book;
        }
        else
        {
            book = "I";
        }

        String pref_chapter = PreferenceManager.getDefaultSharedPreferences(this)
                                               .getString("chapter", "Welcome");


        chapter = pref_chapter;

        Boolean pref_unit_A = PreferenceManager.getDefaultSharedPreferences(this)
                                               .getBoolean("unit_A", true);
        Boolean pref_unit_B = PreferenceManager.getDefaultSharedPreferences(this)
                                               .getBoolean("unit_A", false);
        Boolean pref_unit_C = PreferenceManager.getDefaultSharedPreferences(this)
                                               .getBoolean("unit_A", false);

        if (pref_unit_A)
        {
            if (pref_unit_B)
            {
                if (pref_unit_C)
                {
                    unit = "A B C";
                }
                else
                {
                    unit = "A B";
                }
            }
            else
            {
                if (pref_unit_C)
                {
                    unit = "A C";
                }
                else
                {
                    unit = "A";
                }
            }
        }
        else
        {
            if (pref_unit_B)
            {
                if (pref_unit_C)
                {
                    unit = "B C";
                }
                else
                {
                    unit = "B";
                }
            }
            else
            {
                if (pref_unit_C)
                {
                    unit = "C";
                }
                else
                {
                    unit = "A";
                }
            }
        }
    }

    private int setCurrentBook()
    {
        int tab = 0;
        String pref_book = PreferenceManager.getDefaultSharedPreferences(this)
                                            .getString("book", "0");
        switch (pref_book)
        {
            case "0":
                tab = 0;
                break;
            case "I":
                tab = 0;
                break;
            case "II":
                tab = 1;
                break;
            case "III":
                tab = 2;
                break;
        }
        return tab;
    }

    // gets called in Book1, Book2, Book3
    public SlidingLayer getSlidingLayer()
    {
        return mSlidingLayer;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_help:
                Intent intent_help = new Intent(TrainAndTest.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(TrainAndTest.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    /**
     * Cleans up the timer responsible for delaying the appearance of L1 text in the training
     * exercises, as well as its animator, preventing either from functioning anymore
     */
    private void destroyTrainDelayTimer()
    {
        if (trainDelayTimer != null)
        {
            trainDelayTimer.cancel();
            trainDelayTimer.purge();
        }
        trainDelayTimer = null;

        if (trainTextAnimator != null)
        {
            trainTextAnimator.end();
            trainTextAnimator.cancel();
        }
        trainTextAnimator = null;
    }

    /**
     * Schedules the given word to appear after a delay, with a simple fading-in animation
     *
     * @param word The word to display
     */
    private void scheduleTrainDelayTimer(final String word)
    {
        // Clear existing text
        txt_voc.setText("");

        // Wipe any existing timers and animators
        destroyTrainDelayTimer();

        // Create the timer
        trainDelayTimer = new Timer();
        trainDelayTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                // Post on UI thread
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // Create and activate the animator
                        trainTextAnimator = ObjectAnimator.ofFloat(txt_voc, "alpha", 0f, 1f);
                        trainTextAnimator.setDuration(500);
                        trainTextAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        trainTextAnimator.start();

                        // Set the text to display
                        txt_voc.setText(word);
                    }
                });
            }
        }, TRAIN_L1_DELAY);
    }
}
