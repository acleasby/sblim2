/**
 * (C) Copyright IBM Corp. 2013
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author : Dave Blaschke, blaschke@us.ibm.com
 * 
 * 
 * Change History
 * Flag     Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 3596303  2013-01-04  blaschke-oss windows http response WWW-Authenticate: Negotiate fails
 */
package org.sblim.cimclient.unittest.http;

import org.sblim.cimclient.internal.http.Challenge;
import org.sblim.cimclient.unittest.TestCase;

/**
 * Class HttpChallengeTest is responsible for testing the Challenge class.
 */
public class HttpChallengeTest extends TestCase {

	/**
	 * testValidChallenges
	 */
	public void testValidChallenges() {
		Challenge[] result;

		try {
			result = Challenge.parseChallenge("Basic realm=\"cimom01\"");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			checkSchemeAndRealm(result[0], "Basic", "cimom01");

			result = Challenge.parseChallenge("Basic realm=cimom02");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			checkSchemeAndRealm(result[0], "Basic", "cimom02");

			result = Challenge.parseChallenge("Basic realm = \"cimom03\"");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			checkSchemeAndRealm(result[0], "Basic", "cimom03");

			result = Challenge.parseChallenge("Basic realm = cimom04");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			checkSchemeAndRealm(result[0], "Basic", "cimom04");

			result = Challenge.parseChallenge("  Basic realm=\"cimom05\"  ");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			checkSchemeAndRealm(result[0], "Basic", "cimom05");

			result = Challenge.parseChallenge("  Basic realm=cimom06  ");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			checkSchemeAndRealm(result[0], "Basic", "cimom06");

			result = Challenge.parseChallenge("Basic realm=\"cimom07\",  Basic realm=cimom08");
			verify("Unexpected length " + result.length + " is not 2", result.length == 2);
			checkSchemeAndRealm(result[0], "Basic", "cimom07");
			checkSchemeAndRealm(result[1], "Basic", "cimom08");

			result = Challenge
					.parseChallenge("Digest realm=\"cimom09\", domain=\"http://example.com\"");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			checkSchemeAndRealm(result[0], "Digest", "cimom09");

			result = Challenge
					.parseChallenge("Basic realm=\"cimom10\", Digest realm=\"cimom11\", domain=\"http://example.com\", Basic realm=\"cimom12\"");
			verify("Unexpected length " + result.length + " is not 3", result.length == 3);
			checkSchemeAndRealm(result[0], "Basic", "cimom10");
			checkSchemeAndRealm(result[1], "Digest", "cimom11");
			checkSchemeAndRealm(result[2], "Basic", "cimom12");

			result = Challenge.parseChallenge("Basic realm=\"cimom13\", Negotiate");
			verify("Unexpected length " + result.length + " is not 2", result.length == 2);
			checkSchemeAndRealm(result[0], "Basic", "cimom13");

			result = Challenge.parseChallenge("Negotiate, Basic realm=cimom14");
			verify("Unexpected length " + result.length + " is not 2", result.length == 2);
			checkSchemeAndRealm(result[1], "Basic", "cimom14");

			result = Challenge.parseChallenge("Basic realm=\"my favorite  cimom\"");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			checkSchemeAndRealm(result[0], "Basic", "my favorite  cimom");

			// Special case for SVC ICAT CIMOM, this should be an error but...
			result = Challenge.parseChallenge("Digest 1.2.3.4:5988");
			verify("Unexpected length " + result.length + " is not 1", result.length == 1);
			verify("Unexpected scheme " + result[0].getScheme() + " is not Digest", result[0]
					.getScheme().equalsIgnoreCase("Digest"));
			verify("Unexpected realm " + result[0].getRealm() + " is not null", result[0]
					.getRealm() == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception " + e.getMessage());
		}
	}

	private void checkSchemeAndRealm(Challenge challenge, String scheme, String realm) {
		verify("Unexpected scheme " + challenge.getScheme() + " is not " + scheme, challenge
				.getScheme().equalsIgnoreCase(scheme));
		verify("Unexpected realm " + challenge.getRealm() + " is not " + realm, challenge
				.getRealm().equalsIgnoreCase(realm));
	}

	private String[] invalidChallenges = { null, "", "     ", "\t \r\r\n", "realm=\"cimom14\"",
			"realm=cimom15", "Basic realm=\"cimom", "Basic realm=\"", "Basic realm=",
			"Basic realm= ", "Basic realm = ", "Basic realm=cimom extra", "=cimom", "= cimom",
			" = cimom", "a b c d e f g" };

	/**
	 * testInvalidChallenges
	 */
	public void testInvalidChallenges() {
		for (int i = 0; i < this.invalidChallenges.length; i++) {
			try {
				Challenge.parseChallenge(this.invalidChallenges[i]);
				fail("Expected exception for " + this.invalidChallenges[i]);
			} catch (Exception e) {
				verify("", e.getMessage() != null);
			}
		}
	}
}
