package com.tappitz.app.validators;



public class GPSValidator {

	public GPSValidator() {
	}

	/**
	 * Validate hex with regular expression
	 * 
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public boolean validate(final String hex) {
		return !(hex.contains("'") || hex.contains("--") || hex.contains("\"") || hex.contains("<") || hex.contains(">"));

	}
}
