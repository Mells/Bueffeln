package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public abstract class ModelMath
{
    public static float THRESHOLD = -0.8f;
    public static float ALPHA_DEFAULT = 0.3f;
    public static float DECAY_SCALAR = 0.25f;
    public static float RT_SCALAR = 1f;
    public static float RECALL_PROB_NOISE_REDUCTION = 0.255f;
    public static float TRAIN_FACTOR = 0.8f;
    public static float TEST_FACTOR = 1f;
    public static int LOOKAHEAD_TIME = 15000; // In milliseconds
    public static float PSYCH_TIME_SCALAR = 0.025f;
    public static float RT_CHAR_DISCOUNT_SCALAR = 19.5f; // In milliseconds
    public static float RT_CHAR_DISCOUNT_INTERCEPT = -157.9f; // In milliseconds
    public static float RT_MIN = 300; // In milliseconds
    public static float BETA_S = 0;


    /**
     * The predicted reaction time intercept (f) based on the number of characters in the context.
     *
     * @param charCount The number of characters in the context
     * @return The reaction time intercept, f, in milliseconds
     */
    public static float reactionTimeCharDiscount(int charCount)
    {
        // f = max(RT_di + (RT_ds * C_count), RT_min)
        return Math.max(RT_CHAR_DISCOUNT_INTERCEPT + (RT_CHAR_DISCOUNT_SCALAR * charCount),
                        RT_MIN);
    }

    /**
     * The maximum reaction time (RT_max) based on the number of characters in the context and
     * the forgetting threshold.
     *
     * @param charCount The number of characters in the context
     * @return The maximum reaction time, RT_max, in milliseconds
     */
    public static float maxRT(int charCount)
    {
        // RT_max = Fe^(-1.5tau) + f
        return RT_SCALAR * (float) Math.exp(-1.5 * THRESHOLD) + reactionTimeCharDiscount(charCount);
    }

    /**
     * The predicted reaction time (RT_i,j) based on the number of characters in the context and
     * the activation level of the item (m_i,j).
     *
     * @param charCount  The number of characters in the context
     * @param activation The activation of the item, m_i,j.
     * @return The predicted reaction time, RT_i,j, in milliseconds
     */
    public static float predictedRT(int charCount, float activation)
    {
        // RT_max = Fe^(-m_i,j) + f
        return RT_SCALAR * (float) Math.exp(-activation) + reactionTimeCharDiscount(charCount);
    }

    /**
     * The decay factor (d_i,j) based on the activation of the item at a particular time (m_i(t_j))
     * and the item's activation intercept (alpha_i)
     *
     * @param activation Activation of the item, m_i(t_j)
     * @param alpha      The item's activation intercept, alpha_i
     * @return The decay factor, d_i,j.
     */
    public static float decay(float activation, float alpha)
    {
        if (activation == Float.NEGATIVE_INFINITY)
            return alpha;

        // d_i,j = ce^(m_i(t_j)) + alpha_i
        return (DECAY_SCALAR * (float) Math.exp(activation)) + alpha;
    }

    /**
     * The probability of the item being recalled (p_r(m_i)) based on the activation of the
     * item (m_i).
     *
     * @param activation Activation of the item, m_i.
     * @return The probability that the item will be recalled, p_r(m_i).
     */
    public static float recallProbability(float activation)
    {
        // p_r(m_i) = 1 / ( 1 + e^((tau - m_i) / s) )
        return 1 / (1 + (float) Math.exp((THRESHOLD - activation) / RECALL_PROB_NOISE_REDUCTION));
    }

    /**
     * The observed activation of an item (m_obs(t_j)) based on the observed reaction time of the
     * user (RT_obs) and the number of characters in the context.
     *
     * @param charCount    The number of characters in the context
     * @param reactionTime The observed reaction time of the user, RT_obs, in milliseconds.
     * @return The observed activation of an item, m_obs(t_j).
     */
    public static float observedActivation(int charCount, int reactionTime)
    {
        // m_obs(t_j) = -ln( (RT - f) / F )
        return -(float) Math.log((reactionTime - reactionTimeCharDiscount(charCount) / RT_SCALAR));
    }


    /**
     * The activation of a word at the current time (m_i(t)) based on the word's
     * parameters, and all previous interactions with the word.  The lookahead time parameter
     * is added to the current time for the purposes of the calculation.
     *
     * @param word         The word to calculate activation for.
     * @param interactions List of all interactions with the word, in temporal order.
     * @return The activation of the item at the given time
     */
    public static float activation(VocObject word, List<InterxObject> interactions)
    {
        return activation(new Date(), word, interactions);
    }


    /**
     * The activation of a word at the given time (m_i(t)) based on the word's
     * parameters, and all previous interactions with the word.  The lookahead time parameter
     * is added to the given time for the purposes of the calculation.
     *
     * @param time         The time to calculate activation in reference to.
     * @param word         The word to calculate activation for.
     * @param interactions List of all interactions with the word, in temporal order.
     * @return The activation of the item at the given time
     */
    public static float activation(Date time, VocObject word, List<InterxObject> interactions)
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(time);
        cal.add(Calendar.MILLISECOND, LOOKAHEAD_TIME);

        // Calculate the activation at this time
        float activation = activationRecursion(cal.getTime(), word, interactions);

        // Clear all cached activation values
        for (InterxObject interx : interactions)
            interx.clearActivation();

        // Return the calculated activation
        return activation;
    }

    /**
     * Calculates the activation for the given word at the given time, based on the given
     * interactions.  Must recursively calculate activation for all previous encounters.
     *
     * @param time         The time to calculate activation in reference to.
     * @param word         The word to calculate activation for.
     * @param interactions List of all interactions with the word, in temporal order.
     * @return The activation of the item at the given time
     */
    private static float activationRecursion(Date time, VocObject word, List<InterxObject> interactions)
    {
        // TODO:  Factor in psych time

        long currTime = time.getTime();

        // Base case for recursion: if there are no previous encounters, activation is -infinity.
        // Used !before() here instead of after(), so that it acts like >=
        if (interactions.size() == 0 || !interactions.get(0).getTimestamp().before(time))
            return Float.NEGATIVE_INFINITY;

        // Initialize the running sum
        float runningSum = 0;

        // Iterate through all interactions before the current time
        for (int i = 0; i < interactions.size() && interactions.get(i).getTimestamp().before(time); i++)
        {
            InterxObject interx = interactions.get(i);

            // Get the values for b_j and (t - t_j)
            float exerciseScalar = getExerciseScalar(interx.getExerciseType());
            long timeDifference = currTime - interx.getTimestamp().getTime();

            // If this interaction does not have a cached activation value, calculate and store it
            if (!interx.hasActivation())
                interx.storeActivationValue(activationRecursion(interx.getTimestamp(),
                                                                word,
                                                                interactions));

            // b_j( t - t_j )^(-d_i,j)
            runningSum += exerciseScalar *
                          (float) Math.pow(timeDifference,
                                           -decay(interx.retrieveActivation(),
                                                  word.getAlpha()));
        }

        // m_i(t) = sigma_i + beta_s + beta_s,i + beta_i + ln( sum(...) )
        return (float)Math.log(word.getSigma() + BETA_S + word.getBeta_si() +
                               word.getBeta_i() + runningSum);
    }

    /**
     * Gets the value of the exercise scalar based on the given exercise type string.
     *
     * @param exerciseType Descriptor of the exercise type
     * @return The exercise scalar for the given exercise.
     */
    public static float getExerciseScalar(String exerciseType)
    {

        switch (exerciseType)
        {
            case InterxObject.EXERCISE_TRAIN:
                return TRAIN_FACTOR;
            case InterxObject.EXERCISE_TEST:
                return TEST_FACTOR;
            default:
                return 1f;
        }
    }

}
