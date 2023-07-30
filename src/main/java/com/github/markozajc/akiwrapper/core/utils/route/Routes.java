package com.github.markozajc.akiwrapper.core.utils.route;

import static com.github.markozajc.akiwrapper.core.utils.route.Route.Endpoint.*;

@SuppressWarnings("javadoc") // internal util
public final class Routes {

	/*
	 * Certain constant parameters are marked with "?" - this means that the parameter is
	 * set by the website (though not necessarily required), but I'm not sure of its
	 * function or significance. Feel free to open an issue if you know what any of them
	 * do so that I can note it down and possibly use them in the wrapper.
	 */

	private static final String PARAMETER_QUESTION_FILTER = "question_filter";

	private static final String VALUE_QUESTION_FILTER_PROFANITY_ENABLED = "";
	private static final String VALUE_QUESTION_FILTER_PROFANITY_DISABLED = "cat=1";

	public static final String PARAMETER_STEP = "step";
	public static final String PARAMETER_ANSWER = "answer";
	public static final String PARAMETER_SIZE = "size";
	public static final String PARAMETER_MAX_PIC_WIDTH = "max_pic_width";
	public static final String PARAMETER_MAX_PIC_HEIGHT = "max_pic_height";
	public static final String PARAMETER_ELEMENT = "element";

	/**
	 * Creates a new game session that all further state is associated with.<br>
	 * <i>This route takes no parameters.</i>
	 */
	public static final Route NEW_SESSION = new RouteBuilder("/new_session").endpoint(WEBSITE)
		.requiresUrlApiWs()
		.requiresFrontaddr()
		.requiresUidExtSession()
		.constantParameter("player", "website-desktop") // ?
		.constantParameter("partner", "1") // ?
		.constantParameter("constraint", "ETAT<>'AV'") // ?
		.profanityEnabledParameter("childMod", "")
		.profanityEnabledParameter("soft_constraint", "")
		.profanityEnabledParameter(PARAMETER_QUESTION_FILTER, VALUE_QUESTION_FILTER_PROFANITY_ENABLED)
		.profanityDisabledParameter("childMod", "true")
		.profanityDisabledParameter("soft_constraint", "ETAT='EN'")
		.profanityDisabledParameter(PARAMETER_QUESTION_FILTER, VALUE_QUESTION_FILTER_PROFANITY_DISABLED)
		.build();

	/**
	 * Answers the current question and fetches the next one. <br>
	 * Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}: must always point to the current zero-index
	 * step</li>
	 * <li>{@link Routes#PARAMETER_ANSWER}: the answer to the current question</li>
	 * </ul>
	 * <b>This route requires a session</b>
	 */
	public static final Route ANSWER = new RouteBuilder("/answer_api").endpoint(WEBSITE)
		.requiresUrlApiWs()
		.requiresFrontaddr()
		.requiresSession()
		.profanityEnabledParameter(PARAMETER_QUESTION_FILTER, VALUE_QUESTION_FILTER_PROFANITY_ENABLED)
		.profanityDisabledParameter(PARAMETER_QUESTION_FILTER, VALUE_QUESTION_FILTER_PROFANITY_DISABLED)
		.mandatoryParameter(PARAMETER_STEP)
		.mandatoryParameter(PARAMETER_ANSWER)
		.build();

	/**
	 * Cancels (undoes) an answer and fetches the previous question. Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}: must always point to the current zero-index
	 * step</li>
	 * </ul>
	 * <b>This route requires a session</b>
	 */
	public static final Route CANCEL_ANSWER = new RouteBuilder("/cancel_answer").endpoint(GAME_SERVER)
		.requiresSession()
		.constantParameter(PARAMETER_ANSWER, "-1")
		.profanityEnabledParameter(PARAMETER_QUESTION_FILTER, VALUE_QUESTION_FILTER_PROFANITY_ENABLED)
		.profanityDisabledParameter(PARAMETER_QUESTION_FILTER, VALUE_QUESTION_FILTER_PROFANITY_DISABLED)
		.mandatoryParameter(PARAMETER_STEP)
		.build();

	/**
	 * Lists available guesses. Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}: must always point to the current zero-index
	 * step</li>
	 * <li><i>(optional)</i> {@link Routes#PARAMETER_SIZE}: the number of guesses to
	 * fetch (or all if not set)</li>
	 * <li><i>(defaults to 246)</i> {@link Routes#PARAMETER_MAX_PIC_WIDTH}: presumably
	 * the max width of the returned image, but I haven't checked if that's actually
	 * true</li>
	 * <li><i>(defaults to 294)</i> {@link Routes#PARAMETER_MAX_PIC_HEIGHT}: ditto for
	 * height</li>
	 * </ul>
	 * <b>This route requires a session</b>
	 */
	public static final Route LIST = new RouteBuilder("/list").endpoint(GAME_SERVER)
		.requiresSession()
		.constantParameter("pref_photos", "VO-OK") // ?
		.constantParameter("duel_allowed", "1") // ?
		.constantParameter("mode_question", "0") // ?
		.defaultParameter(PARAMETER_MAX_PIC_HEIGHT, "294")
		.defaultParameter(PARAMETER_MAX_PIC_WIDTH, "246")
		.mandatoryParameter(PARAMETER_STEP)
		.optionalParameter(PARAMETER_SIZE)
		.build();

	/**
	 * <b>IMPORTANT: This route is EXCLUDED from tests!</b> Because automated tests don't
	 * tend to behave like players, calling this during testing might introduce faulty
	 * data into Akinator's algorithm, so please avoid doing that.<br>
	 * Exclude a guess. Apparently this won't prevent it from showing up in {@link #LIST}
	 * for whatever reason, but it might improve the further questions. Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}: must always point to the current zero-index
	 * step</li>
	 * </ul>
	 * <b>This route requires a session</b>
	 *
	 * @apiNote Considering that it doesn't take a question ID parameter and that it's
	 *          called right after a guess is rejected on the website, I can only assume
	 *          that this excludes the top guess.
	 */
	public static final Route EXCLUSION = new RouteBuilder("/exclusion").endpoint(GAME_SERVER)
		.requiresSession()
		.constantParameter("forward_answer", "1") // ?
		.mandatoryParameter(PARAMETER_STEP)
		.build();

	/**
	 * <b>IMPORTANT: This route is EXCLUDED from tests!</b> Because automated tests don't
	 * tend to behave like players, calling this during testing might introduce faulty
	 * data into Akinator's algorithm, so please avoid doing that.<br>
	 * Confirm a guess. While this doesn't affect the current session, because it's
	 * called at the very end, it likely affects Akinator's algorithm and associates the
	 * taken answer route with the confirmed guess, thus improving the game for everyone.
	 * Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}: must always point to the current zero-index
	 * step</li>
	 * <li>{@link Routes#PARAMETER_ELEMENT}: the guess ID to confirm</li>
	 * </ul>
	 * <b>This route requires a session</b>
	 */
	public static final Route CHOICE = new RouteBuilder("/choice").endpoint(GAME_SERVER)
		.requiresSession()
		.constantParameter("duel_allowed", "1") // ?
		.mandatoryParameter(PARAMETER_STEP)
		.mandatoryParameter(PARAMETER_ELEMENT)
		.build();

	private Routes() {}

}
