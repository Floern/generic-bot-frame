/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api;

class Requests {


	private static final String API_KEY = "Z85xnMThsPJ*TpmcWgCfkg((";

	private static final String API_BASE_URL = "https://api.stackexchange.com/2.2/";


	private static final String URL_ANSWERS = API_BASE_URL +
			"answers?site=stackoverflow" +
			"&key=" + API_KEY +
			"&order=desc" +
			"&sort=activity" +
			"&pagesize=50&page=%d" +
			"&filter=!t.lNkcL6gE2D47B*VEmRNVYJrI4)NLe";


	private static final String URL_QUESTIONS = API_BASE_URL +
			"questions?site=stackoverflow" +
			"&key=" + API_KEY +
			"&order=desc" +
			"&sort=activity" +
			"&pagesize=50&page=%d" +
			"&filter=!17vW0QPGOaUc(OC*hFxd.DIKCeu*POKI2oGGEqzd4x64Rj";


	private static final String URL_SUGGESTED_EDITS = API_BASE_URL +
			"suggested-edits?site=stackoverflow" +
			"&key=" + API_KEY +
			"&order=desc" +
			"&sort=creation" +
			"&pagesize=50&page=%d" +
			"&filter=!t.ly0tCSL3j7Co.p88nu7VQpu)pytna";


	private static final String URL_COMMENTS = API_BASE_URL +
			"comments?site=stackoverflow" +
			"&key=" + API_KEY +
			"&order=desc" +
			"&sort=creation" +
			"&pagesize=100&page=%d" +
			"&filter=!SYCmunmA6S2HWAddSs";


	protected static String answers(int page) {
		return String.format(URL_ANSWERS, page);
	}


	protected static String questions(int page) {
		return String.format(URL_QUESTIONS, page);
	}


	protected static String suggestedEdits(int page) {
		return String.format(URL_SUGGESTED_EDITS, page);
	}


	protected static String comments(int page) {
		return String.format(URL_COMMENTS, page);
	}

}
