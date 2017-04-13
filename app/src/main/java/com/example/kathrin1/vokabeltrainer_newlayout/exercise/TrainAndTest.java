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
import android.text.Spanned;
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
import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ACTRModel;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.LearnModel;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ModelMath;
import com.example.kathrin1.vokabeltrainer_newlayout.network.NetworkError;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxBuilder;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SimInterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.example.kathrin1.vokabeltrainer_newlayout.views.LoadingBarView;
import com.opencsv.CSVWriter;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject.buildUnfinished;

/**
 * Controls an exercise that tests users on their vocabulary, and uses training exercises to
 * introduce new words or to review words that require more work.
 */
public class TrainAndTest extends AppCompatActivity
{

    public static final String LOG_TAG = "[TrainAndTest]";
    public static final int TRAIN_L1_DELAY = 1000;
    public static final int EXERCISE_CORRECT_WAIT_DELAY = 2000;
    public static final int EXERCISE_INCORRECT_WAIT_DELAY = 4000;


    private LearnModel model;
    private DatabaseManager dbManager;
    private SessionObject currentSession;

    private Timer trainDelayTimer, exerciseWaitTimer;
    private ObjectAnimator trainTextAnimator;

    private VocObject currentWord;
    private InterxBuilder currInterx;
    private String currExerciseType;
    private SentObject currSentence;

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

        //*
        dbManager.clearInteractionsTable();
        dbManager.clearSessionsTable();
        dbManager.wipeUserWordData();
        //*/
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
        simEnabled = false;

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

        currentSession = buildUnfinished(-1, new Date());

        // Build the model
        model = ACTRModel.build(this, dbManager);

        // Show the loading bar
        loadingBar.activateWithDelay(getString(R.string.TrainTest_InitializeModel));

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
                loadingBar.activateWithDelay(getString(R.string.TrainTest_InitialDownload));

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
        model.calculateNextWordASync(null, new LearnModel.WordSelectionListener()
        {
            @Override
            public void onSelection(VocObject vocObject)
            {
                if (vocObject != null)
                {
                    currExerciseType = vocObject.isNew()
                                       ? InterxObject.EXERCISE_TRAIN
                                       : InterxObject.EXERCISE_TEST;
                }
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
            currSentence = dbManager.getExampleSentence(word);

            Date currTime = new Date();
            float preActivation = model.recalcSingleActivation(
                    ModelMath.advanceMilliseconds(currTime, -ModelMath.LOOKAHEAD_TIME),
                    word, word.getAlpha());
            currInterx = new InterxBuilder().setCharCount(currSentence.getSentence().length())
                                            .setExerciseType(currExerciseType)
                                            .setTimestamp(currTime)
                                            .setWord(currentWord)
                                            .setSession(currentSession)
                                            .setPreAlpha(word.getAlpha())
                                            .setPreActivation(preActivation);

            // Display the word depending on the current exercise type
            switch (currExerciseType)
            {
                // If this should be a training exercise, emphasize the word in the example sentence
                // and display the L1 translation after a delay.
                case InterxObject.EXERCISE_TRAIN:
                    toggleOptionalUIElements(View.INVISIBLE);

                    txt_bsp.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.replaceWordInSentence(currSentence, word, "<b><big>%s</big></b>")));
                    scheduleTrainDelayTimer(word.getTranslation());

                    // Set interaction values on training exercise immediately
                    currInterx.setLatency((int) ModelMath.predictedRT(currSentence.getSentence().length(),
                                                                      word.getActivation()))
                              .setResult(InterxObject.RESULT_SUCCESS);
                    model.addNewInteraction(currInterx.buildWithoutInserting());
                    model.recalculateActivationASync(null, null);

                    break;

                // If this should be a testing exercise, blank out the word in the example sentence
                // and display the L1 translation immediately.
                case InterxObject.EXERCISE_TEST:
                    toggleOptionalUIElements(View.VISIBLE);

                    txt_bsp.setText(ExerciseUtils.deleteWordFromSentence(currSentence, word));
                    scheduleTrainDelayTimer(word.getTranslation());
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
        hideFeedback(); // Always hide feedback until displayed

        //btn_hint.setVisibility(visibility);
        btn_solution.setVisibility(visibility);
        inputLayout.setVisibility(visibility);
        btn_next.setVisibility(visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);

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
        txt_feedback.setText("");

        // The display field
        txt_voc = (TextView) findViewById(R.id.txt_voc);
        txt_bsp = (TextView) findViewById(R.id.txt_bsp);


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


        edit_solution.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (currInterx != null)
                {
                    currInterx.markLatency(new Date());
                    Log.d(LOG_TAG, "Marked interaction latency.");
                }
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
    public void onSolutionClicked(View view)
    {
        if (currentWord == null)
            return;

       showFeedback(ExerciseUtils.fromHtml(
                getString(R.string.TrainTest_Answer,
                          currentWord.getTranslation(),
                          currentWord.getVoc())));
    }

    /**
     * Actions to perform when the 'Next' button is clicked.
     */
    public void onNextClicked(View view)
    {
        getNextWord();
    }

    /**
     * Actions to perform when the 'Hint' button is clicked.
     */
    public void onHintClicked(View view)
    {
        if (simEnabled)
            simEnabled = false;
        else
        {
            simEnabled = true;
            runSimulation();
        }
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

        destroyExerciseWaitTimer();

        String input = edit_solution.getText().toString();
        switch (ExerciseUtils.isAnswerCorrect(currSentence, currentWord, input, false))
        {
            case CORRECT:
                showFeedback(R.string.TrainText_Correct);
                if (currInterx != null)
                    currInterx.setResult(InterxObject.RESULT_SUCCESS);

                scheduleExerciseWaitTimer(EXERCISE_CORRECT_WAIT_DELAY);
                break;

            case CLOSE:
               showFeedback(ExerciseUtils.fromHtml(
                        getString(R.string.TrainTest_Close, currentWord.getTranslation(), input, currentWord.getVoc())));
                if (currInterx != null)
                    currInterx.setResult(InterxObject.RESULT_SUCCESS);

                scheduleExerciseWaitTimer(EXERCISE_INCORRECT_WAIT_DELAY);
                break;

            case INCORRECT:
                showFeedback(ExerciseUtils.fromHtml(
                        getString(R.string.TrainText_Incorrect, currentWord.getTranslation(), currentWord.getVoc())));
                if (currInterx != null)
                    currInterx.setResult(InterxObject.RESULT_FAILURE);

                scheduleExerciseWaitTimer(EXERCISE_INCORRECT_WAIT_DELAY);
                break;
        }

        if (currInterx != null)
        {
            model.addNewInteraction(currInterx.buildWithoutInserting());
            currInterx = null;
        }

        edit_solution.getText().clear();

    }


    private void showFeedback(int text)
    {
        showFeedback(getString(text));
    }

    private void showFeedback(String text)
    {
        feedbackLayout.setVisibility(View.VISIBLE);
        txt_feedback.setText(text);
    }

    private void showFeedback(Spanned text)
    {
        feedbackLayout.setVisibility(View.VISIBLE);
        txt_feedback.setText(text);
    }

    private void hideFeedback()
    {
        feedbackLayout.setVisibility(View.INVISIBLE);
    }

    // Standard deviation of simulated reaction time's variance from the predicted reaction time
    public static final float RT_STDDEV = 300;  // in milliseconds
    public static final int INPUT_TIME = 2500;
    public boolean simEnabled = false;
    private List<SimInterxObject> simList = new ArrayList<>();
    private List<SessionObject> simSessions = new ArrayList<>();
    private Map<VocObject, Float> targetAlphas = new HashMap<>();

    /**
     * Runs a simulation, pretending to operate as a user and generating data.
     * DEBUGGING ONLY.
     */
    private void runSimulation()
    {
        Calendar calendar = GregorianCalendar.getInstance();
        Calendar startCalendar = GregorianCalendar.getInstance();
        Calendar endCalendar = GregorianCalendar.getInstance();
        endCalendar.add(Calendar.MINUTE, 30);
        Random r = new Random();

        simSessions.add(SessionObject.build(-1, startCalendar.getTime(), endCalendar.getTime()));
        startCalendar.add(Calendar.HOUR, 24);
        endCalendar.add(Calendar.HOUR, 24);
        simSessions.add(SessionObject.build(-1, startCalendar.getTime(), endCalendar.getTime()));

        ((ACTRModel) model).forceAddSessions(simSessions);

        for (VocObject voc : dbManager.getWordsByBookChapter(book, chapter, unit))
            targetAlphas.put(voc, ((float)r.nextGaussian() * 0.08f) + ModelMath.ALPHA_DEFAULT);

        simLoop(calendar, r);
    }

    private void simLoop(final Calendar cal, final Random r)
    {
        if (!simEnabled)
        {
            simulationOver();
            return;
        }

        model.calculateNextWordASync(cal.getTime(), new LearnModel.WordSelectionListener()
        {
            @Override
            public void onSelection(final VocObject vocObject)
            {
                // This assumes sessions are in chronological order and are all finished
                SessionObject currSimSession = simSessions.get(0);
                for (SessionObject sesh : simSessions)
                {
                    if (sesh.getStart().after(cal.getTime()))
                    {
                        if (currSimSession.getEnd().before(cal.getTime()))
                        {
                            cal.setTime(sesh.getStart());
                            cal.add(Calendar.SECOND, 1);
                            currSimSession = sesh;
                            Log.d(LOG_TAG, "SIM:  ======= SKIPPED TO NEXT SESSION. =======");
                        }
                        break;
                    }
                    currSimSession = sesh;
                }

                if (vocObject == null || !simEnabled || currSimSession.getEnd().before(cal.getTime()))
                {
                    simulationOver();
                    return;
                }


                float actualActivation = model.recalcSingleActivation(
                        ModelMath.advanceMilliseconds(cal.getTime(), -ModelMath.LOOKAHEAD_TIME),
                        vocObject,
                        targetAlphas.get(vocObject));
                float measuredActivation = model.recalcSingleActivation(
                        ModelMath.advanceMilliseconds(cal.getTime(), -ModelMath.LOOKAHEAD_TIME),
                        vocObject,
                        vocObject.getAlpha());
                final float preAlpha = vocObject.getAlpha();

                //Log.d(LOG_TAG, "Simulating word:  " + vocObject.toString());

                currentWord = vocObject;
                currExerciseType = vocObject.isNew()
                                   ? InterxObject.EXERCISE_TRAIN
                                   : InterxObject.EXERCISE_TEST;
                int sentenceLength = dbManager.getExampleSentence(vocObject).getSentence().length();

                float predictedRT = ModelMath.predictedRT(sentenceLength,
                                                          actualActivation);
                float latency = predictedRT + (float) (r.nextGaussian()
                                                       * Math.min(RT_STDDEV,
                                                                  ModelMath.reactionTimeCharDiscount(sentenceLength)));
                latency = Math.max(ModelMath.reactionTimeCharDiscount(sentenceLength), latency);


                float correctProb = ModelMath.recallProbability(actualActivation);
                float guess = r.nextFloat();


                String result = currExerciseType.equals(InterxObject.EXERCISE_TRAIN)
                                || guess <= correctProb
                                ? InterxObject.RESULT_SUCCESS
                                : InterxObject.RESULT_FAILURE;


                Log.d(LOG_TAG, String.format("SIM [%s](%s): Pre-alpha= %f, Target alpha= %f, " +
                                             "R%%= %f, Model m= %f, Real m= %f, Result= %s",
                                             currExerciseType, currentWord.getVoc(), preAlpha,
                                             targetAlphas.get(vocObject), correctProb,
                                             measuredActivation, actualActivation,
                                             result));

                final InterxObject interx =
                        new InterxBuilder()
                                .setCharCount(sentenceLength)
                                .setSession(currSimSession)
                                .setWord(vocObject)
                                .setTimestamp(cal.getTime())
                                .setExerciseType(currExerciseType)
                                .setLatency((int) latency)
                                .setResult(result)
                                .setPreAlpha(preAlpha)
                                .setPreActivation(measuredActivation)
                                .buildWithoutInserting();


                int advanceTime = (int) latency + INPUT_TIME;
                if (currExerciseType.equals(InterxObject.EXERCISE_TEST))
                {
                    advanceTime += (result.equals(InterxObject.RESULT_SUCCESS)
                                    ? EXERCISE_CORRECT_WAIT_DELAY
                                    : EXERCISE_INCORRECT_WAIT_DELAY);
                }

                cal.add(Calendar.MILLISECOND, advanceTime);

                model.addNewInteractionASync(
                        interx,
                        new LearnModel.CalcListener()
                        {
                            @Override
                            public void onCompletion()
                            {
                                simList.add(SimInterxObject.build(interx, targetAlphas.get(vocObject)));
                                simLoop(cal, r);
                            }
                        });
            }
        });
    }

    private void simulationOver()
    {
        Log.d(LOG_TAG, "Simulation complete.");
        String interxFilename = "sim.csv";
        String sessionFilename = "sesh.csv";
        File dir = new File(getFilesDir(), "data");
        if (!dir.exists())
            dir.mkdir();

        try
        {
            File interxFile = new File(dir, interxFilename);
            CSVWriter interxWriter = new CSVWriter(new FileWriter(interxFile));

            for (SimInterxObject sim : simList)
            {
                sim.writeToCSV(interxWriter);
            }
            interxWriter.close();


            File seshFile = new File(dir, sessionFilename);
            CSVWriter seshWriter = new CSVWriter(new FileWriter(seshFile));

            for (SessionObject sesh : simSessions)
            {
                String[] fields = new String[]{DBHandler.ISO_DATE.format(sesh.getStart()),
                                               DBHandler.ISO_DATE.format(sesh.getEnd())};
                seshWriter.writeNext(fields);
            }
            seshWriter.close();

        } catch (IOException e)
        {
            Log.e(LOG_TAG, "Failed to save CSV files of simulation data.", e);
        }

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
                ExerciseUtils.updateBook(TrainAndTest.this, tab);
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
                                               .getBoolean("unit_B", false);
        Boolean pref_unit_C = PreferenceManager.getDefaultSharedPreferences(this)
                                               .getBoolean("unit_C", false);

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

    private void destroyExerciseWaitTimer()
    {
        if (exerciseWaitTimer != null)
        {
            exerciseWaitTimer.cancel();
            exerciseWaitTimer.purge();
        }
        exerciseWaitTimer = null;
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

    private void scheduleExerciseWaitTimer(int delay)
    {
        exerciseWaitTimer = new Timer();
        exerciseWaitTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getNextWord();
                    }
                });
            }
        }, delay);
    }
}
