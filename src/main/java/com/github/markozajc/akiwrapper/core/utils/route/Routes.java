package com.github.markozajc.akiwrapper.core.utils.route;

import static com.github.markozajc.akiwrapper.core.utils.route.Route.Endpoint.*;
import static java.lang.System.currentTimeMillis;

@SuppressWarnings("javadoc") // internal util
public final class Routes {

	private static final String PARAMETER_TIMESTAMP = "_";
	private static final String PARAMETER_QUESTION_FILTER = "question_filter";

	private static final String VALUE_QUESTION_FILTER_PROFANITY_ENABLED = "";
	private static final String VALUE_QUESTION_FILTER_PROFANITY_DISABLED = "cat=1";

	public static final String PARAMETER_STEP = "step";
	public static final String PARAMETER_ANSWER = "answer";
	public static final String PARAMETER_SIZE = "size";

	/**
	 * Creates a new game session that all further state is associated with.<br>
	 * <i>This route takes no parameters.</i>
	 */
	public static final Route NEW_SESSION = new RouteBuilder("/new_session").endpoint(WEBSITE)
		.requiresUrlApiWs()
		.requiresFrontaddr()
		.requiresUidExtSession()
		.constantParameter("player", "website-desktop")
		.constantParameter("partner", "1")
		.constantParameter("constraint", "ETAT<>'AV'")
		.profanityEnabledParameter("childMod", "")
		.profanityEnabledParameter("soft_constraint", "")
		.profanityEnabledParameter(PARAMETER_QUESTION_FILTER, VALUE_QUESTION_FILTER_PROFANITY_ENABLED)
		.profanityDisabledParameter("childMod", "true")
		.profanityDisabledParameter("soft_constraint", "ETAT='EN'")
		.profanityDisabledParameter(PARAMETER_QUESTION_FILTER, VALUE_QUESTION_FILTER_PROFANITY_DISABLED)
		.automaticParameter(PARAMETER_TIMESTAMP, () -> Long.toString(currentTimeMillis()))
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
		.automaticParameter(PARAMETER_TIMESTAMP, () -> Long.toString(currentTimeMillis()))
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
		.automaticParameter(PARAMETER_TIMESTAMP, () -> Long.toString(currentTimeMillis()))
		.mandatoryParameter(PARAMETER_STEP)
		.build();

	/**
	 * Lists available guesses. Parameters:
	 * <ul>
	 * <li>{@link Routes#PARAMETER_STEP}: must always point to the current zero-index
	 * step</li>
	 * <li><i>(optional)</i> {@link Routes#PARAMETER_SIZE}: the number of guesses to
	 * fetch (or all if not set)</li>
	 * </ul>
	 * <b>This route requires a session</b>
	 */
	public static final Route LIST = new RouteBuilder("/list").endpoint(GAME_SERVER)
		.requiresSession()
		.constantParameter("max_pic_width", "246")
		.constantParameter("max_pic_height", "294")
		.constantParameter("pref_photos", "VO-OK")
		.constantParameter("duel_allowed", "1")
		.constantParameter("mode_question", "0")
		.mandatoryParameter(PARAMETER_STEP)
		.optionalParameter(PARAMETER_SIZE)
		.automaticParameter(PARAMETER_TIMESTAMP, () -> Long.toString(currentTimeMillis()))
		.build();

	private Routes() {}

}
