package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
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
    public static float RT_SCALAR = 1000f;
    public static float RECALL_PROB_NOISE_REDUCTION = 0.255f;
    public static float TRAIN_FACTOR = 0.8f;
    public static float TEST_FACTOR = 1f;
    public static int LOOKAHEAD_TIME = 15000; // In milliseconds
    public static float PSYCH_TIME_SCALAR = 0.025f;
    public static float RT_CHAR_DISCOUNT_SCALAR = 19.5f; // In milliseconds  (13.29 or 19.5)
    public static float RT_CHAR_DISCOUNT_INTERCEPT = -15.9f; // In milliseconds  (354 or -157.9)
    public static float RT_MIN = 300; // In milliseconds
    public static float BETA_S = 0;
    public static float ALPHA_CONVERGENCE_ITERATIONS = 6;


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
        return (RT_SCALAR * (float) Math.exp(-1.5 * THRESHOLD)) + reactionTimeCharDiscount(charCount);
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
        return (RT_SCALAR * (float) Math.exp(-activation)) + reactionTimeCharDiscount(charCount);
    }

    /**
     * The modified reaction time based on the result of the presentation.
     *
     * @param interx Object containing all information about the interaction
     * @return The modified effective reaction time for the presentation.
     */
    public static float effectiveRT(InterxObject interx)
    {
        return effectiveRT(interx.getCharCount(), interx.getLatency(), interx.getResult());
    }

    /**
     * The modified reaction time based on the result of the presentation.
     *
     * @param charCount The number of characters in the context
     * @param latency   The measured reaction time
     * @param result    String describing the result, {@link InterxObject#RESULT_SUCCESS} or
     *                  {@link InterxObject#RESULT_FAILURE}.
     * @return The modified effective reaction time for the presentation.
     */
    public static float effectiveRT(int charCount, float latency, String result)
    {
        switch (result)
        {
            case InterxObject.RESULT_FAILURE:
                return maxRT(charCount);
            case InterxObject.RESULT_SUCCESS:
            default:
                return Math.max(latency, reactionTimeCharDiscount(charCount) + 1);
        }
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
    public static float observedActivation(int charCount, float reactionTime)
    {
        // m_obs(t_j) = -ln( (RT - f) / F )
        return (float) -Math.log((reactionTime - reactionTimeCharDiscount(charCount)) / RT_SCALAR);
    }

    /**
     * The modified time based on the default lookahead time.
     *
     * @param time The time to calculate from.
     * @return The time shifted ahead by the lookahead time.
     */
    public static Date lookaheadTime(Date time)
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(time);
        cal.add(Calendar.MILLISECOND, LOOKAHEAD_TIME);
        return cal.getTime();
    }


    /**
     * The activation of a word at the current time (m_i(t)) based on the word's
     * parameters, and all previous interactions with the word.  The lookahead time parameter
     * is added to the current time for the purposes of the calculation.
     *
     * @param word         The word to calculate activation for.
     * @param interactions List of all interactions with the word, in temporal order.
     * @param alpha        The alpha value of the word to use for calculations.
     *                     This must be passed as an argument rather than simply reading the word
     *                     object in order to allow calculating with different alphas.
     * @return The activation of the item at the given time
     */
    public static float activation(VocObject word, List<InterxObject> interactions,
                                   List<SessionObject> sessions, float alpha)
    {
        return activation(new Date(), word, interactions, sessions, alpha);
    }


    /**
     * The activation of a word at the given time (m_i(t)) based on the word's
     * parameters, and all previous interactions with the word.  The lookahead time parameter
     * is added to the given time for the purposes of the calculation.
     *
     * @param time         The time to calculate activation in reference to.
     * @param word         The word to calculate activation for.
     * @param interactions List of all interactions with the word, in temporal order.
     * @param sessions     List of all study sessions, in order to scale the time between them.
     * @param alpha        The alpha value of the word to use for calculations.
     *                     This must be passed as an argument rather than simply reading the word
     *                     object in order to allow calculating with different alphas.
     * @return The activation of the item at the given time
     */
    public static float activation(Date time, VocObject word, List<InterxObject> interactions,
                                   List<SessionObject> sessions, float alpha)
    {

        // Calculate the activation at this time
        float activation = activationRecursion(lookaheadTime(time), word, interactions, sessions,
                                               true, alpha);

        // Clear all cached activation values
        clearCachedActivation(interactions);

        // Return the calculated activation
        return activation;
    }

    /**
     * Clears the cached activation for all interactions in the given list.
     *
     * @param interactions The list of interactions for which to clear the activation cache
     */
    public static void clearCachedActivation(List<InterxObject> interactions)
    {
        for (InterxObject interx : interactions)
            interx.clearActivation();
    }

    /**
     * Calculates the activation for the given word at the given time, based on the given
     * interactions.  Must recursively calculate activation for all previous encounters.
     * <p>
     * The 'doLast' parameter is for when calculating the sum up to interaction n-1.  When false,
     * the last interaction will be entirely ignored, while still using the
     * given time.  This is used as part of the {@link ModelMath#latestAlpha(InterxObject, List, List)}
     * calculations.  This being false also signals that the extra activation parameters
     * (betas and sigma) should not be counted.
     * </p><p>
     * This method caches activation calculations inside the given InterxObjects, and does <b>NOT</b>
     * clear the caches afterwards.  This must be done manually with
     * {@link ModelMath#clearCachedActivation(List)}.
     * </p>
     *
     * @param time         The time to calculate activation in reference to.
     * @param word         The word to calculate activation for.
     * @param interactions List of all interactions with the word, in temporal order.
     * @param sessions     List of all study sessions, in order to scale the time between them.
     * @param doLast       Whether or not the last interaction should be counted
     * @param alpha        The alpha value of the word to use for calculations.
     *                     This must be passed as an argument rather than simply reading the word
     *                     object in order to allow calculating with different alphas.
     * @return The activation of the item at the given time
     */
    private static float activationRecursion(Date time, VocObject word, List<InterxObject> interactions,
                                             List<SessionObject> sessions, boolean doLast, float alpha)
    {
        // Base case for recursion: if there are no previous encounters, activation is -infinity.
        // Used !before() here instead of after(), so that it acts like >=
        if (interactions.size() == 0 || !interactions.get(0).getTimestamp().before(time))
            return Float.NEGATIVE_INFINITY;

        // Initialize the running sum
        float runningSum = 0;

        int limit = interactions.size() - (doLast ? 0 : 1);

        // Iterate through all interactions before the current time
        for (int i = 0; i < limit && interactions.get(i).getTimestamp().before(time); i++)
        {
            InterxObject interx = interactions.get(i);

            // Get the values for b_j and (t - t_j)
            float exerciseScalar = getExerciseScalar(interx.getExerciseType());
            double timeDifference = effectiveTimeDifference(interx.getTimestamp(), time, sessions);

            // If this interaction does not have a cached activation value, calculate and store it
            if (!interx.hasActivation())
                interx.storeActivationValue(activationRecursion(interx.getTimestamp(),
                                                                word, interactions, sessions,
                                                                true, alpha));

            // b_j( t - t_j )^(-d_i,j)
            float decay = decay(interx.retrieveActivation(), alpha);
            runningSum += exerciseScalar * (float) Math.pow(timeDifference, -decay);
        }

        // m_i(t) = sigma_i + beta_s + beta_s,i + beta_i + ln( sum(...) )
        return (doLast ? activationModifiers(word) : 0) + (float) Math.log(runningSum);
    }

    /**
     * The sum of all activation modifiers (sigma_i, beta_s, beta_s,i, and beta_i) for the given
     * word.
     *
     * @param word The word to use for activation modifiers
     * @return The sum of all activation modifiers for the given word
     */
    public static float activationModifiers(VocObject word)
    {
        return word.getSigma() + BETA_S + word.getBeta_si() + word.getBeta_i();
    }

    /**
     * The alpha value that best fits the last interaction based on it's observed reaction time.
     *
     * @param latest       The latest interaction.  This item should be the final item in the given
     *                     list.  // TODO:  Probably doesn't need to be it's own argument
     * @param interactions The list of all interactions for the word
     * @param sessions     List of all study sessions, in order to scale the time between them.
     * @return The alpha value that fits the latest interaction
     */
    public static float latestAlpha(InterxObject latest, List<InterxObject> interactions,
                                    List<SessionObject> sessions)
    {
        // Use a lookahead time for reference
        Date time = lookaheadTime(latest.getTimestamp());
        VocObject word = latest.getWord();

        // Calculate the activation as if the latest interaction didn't occur
        float activationWithoutLatest = activationRecursion(time, word, interactions, sessions,
                                                            false, word.getAlpha());


        // Calculate the observed level of activation based on the measured reaction time
        float observedActivation = observedActivation(latest.getCharCount(), effectiveRT(latest))
                                   - activationModifiers(word);
        // TODO:  Should activation modifiers be included here?

        // Calculate the decay value for the latest interaction
        double timeDifference = effectiveTimeDifference(latest.getTimestamp(), time, sessions);
        float lastDecay = (float) -logN(Math.exp(observedActivation) - activationWithoutLatest,
                                        timeDifference);

        // TODO:  Clear activation cache in interactions list here?  Probably not

        // Calculate the activation for the word in the instant before the latest encounter
        float activationBeforeLatest = activationRecursion(latest.getTimestamp(),
                                                           word,
                                                           interactions, sessions,
                                                           true, word.getAlpha());

        // Clear the stored activation values
        clearCachedActivation(interactions);

        // Calculate the alpha that fits the latest interaction
        return lastDecay - (float) (DECAY_SCALAR * Math.exp(activationBeforeLatest));
    }

    /**
     * The total reaction time error for the word across all of the given reactions using
     * the given alpha.  This is equivalent to sum of the differences between the predicted
     * and observed reaction times for all encounters.
     *
     * @param word         The word to calculate the error for
     * @param interactions List of all interactions for the word
     * @param sessions     List of all study sessions, in order to scale the time between them.
     * @param alpha        The alpha value to use (instead of whatever value is stored by the word)
     * @return The total reaction time error.
     */
    public static float reactionTimeError(VocObject word, List<InterxObject> interactions,
                                          List<SessionObject> sessions, float alpha)
    {
        // Initialize running sum
        float totalError = 0;
        // Iterate through all interactions
        for (InterxObject interx : interactions)
        {
            // Calculate the level of activation at the instant before the encounter
            float activation = activationRecursion(interx.getTimestamp(), word, interactions,
                                                   sessions, true, alpha);

            // Clear cached activation values
            clearCachedActivation(interactions);

            // If this was the first encounter, ignore it
            if (activation == Float.NEGATIVE_INFINITY)
                continue;

            // Calculate the predicted reaction time based on the calculated activation
            float predicted = predictedRT(interx.getCharCount(), activation);

            // Find the difference between the measured reaction time and the predicted reaction
            // time, and add it to the running total.
            totalError += Math.abs(effectiveRT(interx) - predicted);

        }

        // Return the total error
        return totalError;
    }

    /**
     * The alpha value that best fits the data, adjusting the existing value to account for the
     * latest interaction.
     *
     * @param latest       The latest interaction.  This item should be the final item in the given
     *                     list.  // TODO:  Probably doesn't need to be it's own argument
     * @param interactions The list of all interactions for the word
     * @param sessions     List of all study sessions, in order to scale the time between them.
     * @return The new alpha value that best fits the word's interactions
     */
    public static float newAlpha(InterxObject latest, List<InterxObject> interactions,
                                 List<SessionObject> sessions)
    {
        VocObject word = latest.getWord();

        // Get the two alpha values to search between.
        float oldAlpha = word.getAlpha();
        float latestAlpha = latestAlpha(latest, interactions, sessions);

        // Use these to define the lower and upper bounds for the alpha
        float lower = Math.min(oldAlpha, latestAlpha);
        float upper = Math.max(oldAlpha, latestAlpha);
        float mid = (lower + upper) / 2;

        // Conduct several iterations of a binary search to converge on the optimal alpha value
        // between the calculated bounds.

        // Iterate a finite number of times, defined by a constant
        for (int i = 0; i < ALPHA_CONVERGENCE_ITERATIONS; i++)
        {
            // Calculate the total error for each alpha value
            float lowerError = reactionTimeError(word, interactions, sessions, lower);
            float upperError = reactionTimeError(word, interactions, sessions, upper);

            // Adjust the search space based on which error was lower
            if (lowerError < upperError)
                upper = mid;
            else
                lower = mid;

            mid = (lower + upper) / 2;
        }

        // Return the optimized alpha value
        return mid;

    }

    /**
     * Calculates the effective time distance between the two given dates, scaling the time that
     * passed between the given sessions.  Returns a value in <b>SECONDS</b>.
     *
     * @param older    The older of the two times to compare.
     * @param newer    The newer of the two times to compare.
     * @param sessions The sessions to scale time between.  If null, simply returns the unscaled
     *                 time difference.
     * @return The effective time difference between the two given times based on the given
     * sessions, in seconds.
     */
    public static double effectiveTimeDifference(Date older, Date newer, List<SessionObject> sessions)
    {
        if (newer.before(older))
            throw new IllegalArgumentException("'older' argument must be a time before 'newer'.");

        // Find the raw millisecond difference between the gives dates
        double difference = newer.getTime() - older.getTime();

        // If there are no sessions, simply return here
        if (sessions == null)
            return difference / 1000.0;

        // Calculate the inverse psychological time scalar
        float deltaInverse = 1 - PSYCH_TIME_SCALAR;


        /*
         * The strategy here is the following:
         *
         * Iterate through all sessions, determining how much of the given timespan fell between
         * the session and it's predecessor.  This amount is scaled by the INVERSE psychological
         * time scalar to determine a value to SUBTRACT from the otherwise-unscaled difference.
         * This results in the time remaining as part of the difference total to equal the time
         * scaled by the non-inverted psych time scalar.
         */

        // Iterate through all sessions
        for (int i = 0; i < sessions.size(); i++)
        {
            // Note the current and previous sessions (if one exists)
            SessionObject current = sessions.get(i);
            SessionObject previous = i > 0 ? sessions.get(i - 1) : null;

            // If the previous interaction is marked as unfinished, throw an error
            if (previous != null && !previous.isFinished())
                throw new IllegalArgumentException("Non-final session is unfinished.");

            // If the sessions are mis-ordered or overlap, throw an error
            if (previous != null && !current.getStart().after(previous.getEnd()))
                throw new IllegalArgumentException("Study sessions are not properly ordered.");

            // If the older given timestamp is before the start of the current session
            if (older.before(current.getStart()))
            {
                // If the older given timestamp is before the end of the previous session
                if (previous != null && older.before(previous.getEnd()))
                {
                    // Older -> Previous end *-> Newer -> Current start
                    if (current.getStart().after(newer))
                        difference -= deltaInverse * (newer.getTime() -
                                                      previous.getEnd().getTime());

                    // Older -> Previous end *-> Current start -> Newer
                    else
                        difference -= deltaInverse * (current.getStart().getTime() -
                                                      previous.getEnd().getTime());
                }
                else
                {
                    // (Previous end) -> Older *-> Newer -> Current start
                    if (current.getStart().after(newer))
                        difference -= deltaInverse * (newer.getTime() -
                                                      older.getTime());
                    // (Previous end) -> Older *-> Current start -> Newer
                    else
                        difference -= deltaInverse * (current.getStart().getTime() -
                                                      older.getTime());
                }
            }

            // If this is the last session, and the newer time is after its end, scale it
            if (i + 1 == sessions.size() && current.isFinished() && current.getEnd().before(newer))
                difference -= deltaInverse * (newer.getTime() - current.getEnd().getTime());

            // If both given timestamps lie within or before the current session, stop here
            if (!newer.after(current.getStart()) ||
                (current.isFinished() && !newer.after(current.getEnd())))
                break;

        }

        // Return the scaled time difference, converted to seconds
        return difference / 1000.0;


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

    /**
     * Perform base-n logarithm of the given value.
     *
     * @param value The value to log.
     * @param n     The base of the log.
     * @return The base-n log of the given value.
     */
    public static double logN(double value, double n)
    {
        return Math.log(value) / Math.log(n);
    }

}
