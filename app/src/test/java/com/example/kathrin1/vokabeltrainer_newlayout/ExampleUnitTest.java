package com.example.kathrin1.vokabeltrainer_newlayout;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest
{
/*
    public static final int CHAR_COUNT = 20;

    @Test
    public void mathTester() throws Exception
    {

        VocObject word = new VocObject(-1, "word", "translation", "status", "book", "chapter",
                                       "pos", "[sentences]", 0, "lemma", "word")
                                 .setParameters(0f, 0f, 0.3f, 0f);

        Date referenceTime = new Date();

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(referenceTime);

        List<SessionObject> sessions = new ArrayList<>();

        List<InterxObject> interactions = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            InterxObject interx = InterxObject.build(-1, word, 2500 - (i * 200), cal.getTime(), i < 10 ? InterxObject.RESULT_SUCCESS : InterxObject.RESULT_FAILURE, 20, InterxObject.EXERCISE_TEST);
            interactions.add(interx);

            // TESTING BREAKPOINT
            float effectiveRT = ModelMath.effectiveRT(interx);

            //float newAlpha =  ModelMath.newAlpha(interx, interactions);
            if (i > 0)
            {
                float newAlpha = ModelMath.newAlpha(interx, interactions, sessions);

                word.setAlpha(newAlpha);
            }

            cal.add(Calendar.MILLISECOND, 15000 + (i * 10000));
        }

        cal.setTime(referenceTime);
        cal.add(Calendar.MILLISECOND, -ModelMath.LOOKAHEAD_TIME);

        for (int i = 0; i < 60; i++)
        {
            float activation = ModelMath.activation(cal.getTime(), word, interactions, sessions, word.getAlpha());

            float predictedRT = ModelMath.predictedRT(20, activation);

            float maxRT = ModelMath.maxRT(CHAR_COUNT);

            float recallProb = ModelMath.recallProbability(activation);

            float reactionTimeCharDiscount = ModelMath.reactionTimeCharDiscount(CHAR_COUNT);


            cal.add(Calendar.MILLISECOND, 5000);
        }


    }

    @Test
    public void sessionTester() throws Exception
    {
        List<SessionObject> sessions = new ArrayList<>();
        sessions.add(SessionObject.build(-1, new Date(2017, 3, 19, 15, 0), new Date(2017, 3, 19, 16, 0)));
        sessions.add(SessionObject.build(-1, new Date(2017, 3, 19, 17, 0), new Date(2017, 3, 19, 18, 0)));
        sessions.add(SessionObject.build(-1, new Date(2017, 3, 19, 19, 0), new Date(2017, 3, 19, 20, 0)));

        double diff1516 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 0),
                                                            new Date(2017, 3, 19, 16, 0),
                                                            sessions);


        double diff1517 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 0),
                                                            new Date(2017, 3, 19, 17, 0),
                                                            sessions);


        double diff1518 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 0),
                                                            new Date(2017, 3, 19, 18, 0),
                                                            sessions);


        double diff1519 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 0),
                                                            new Date(2017, 3, 19, 19, 0),
                                                            sessions);


        double diff1520 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 0),
                                                            new Date(2017, 3, 19, 20, 0),
                                                            sessions);


        double diff1521 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 0),
                                                            new Date(2017, 3, 19, 21, 0),
                                                            sessions);


        double diff1522 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 0),
                                                            new Date(2017, 3, 19, 22, 0),
                                                            sessions);


        double diff15301630 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 30),
                                                                new Date(2017, 3, 19, 16, 30),
                                                                sessions);


        double diff15301730 = ModelMath.effectiveTimeDifference(new Date(2017, 3, 19, 15, 30),
                                                                new Date(2017, 3, 19, 17, 30),
                                                                sessions);

        float blah = 0;

    }
*/
}