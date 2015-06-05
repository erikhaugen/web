package com.livescribe.admin.lookup;

import org.apache.log4j.Logger;

import com.livescribe.admin.exception.ParameterParsingException;

public class LookupCriteriaBuilder {

	private static Logger logger = Logger.getLogger(LookupCriteriaBuilder.class.getName());
	
	public static final String EMAIL_PATTERN = "\\b[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,4}\\b";
	private static final String PEN_DISPLAY_ID_PATTERN = "AYE-\\w{3}-\\w{3}-\\w{2}";
	private static final String PEN_SERIAL_NUMBER_PATTERN = "\\d{13}";
	
	/**
	 * 
	 * @param lookupField
	 * @param comparator
	 * @param value
	 * @return
	 */
	public static LookupCriteria buildLookupCriteria(String lookupField, String comparator, String value) throws ParameterParsingException {
	
		QueryField queryField = findQueryField(lookupField);
		Comparator operator = findComparator(comparator);
		
		return validateAndBuildLookupCriteria(queryField, operator, value);
	}
	
	private static LookupCriteria validateAndBuildLookupCriteria(QueryField queryField, Comparator operator, String value) throws ParameterParsingException {

		logger.debug("validateAndBuildLookupCriteria(" + queryField + 
													", " + operator +
													", " + value);
		// Validate the value for null
		if (value == null || value.trim().length() == 0) {
			throw new ParameterParsingException("Value cannot be empty.");
		}
		if (null == queryField) {
			throw new ParameterParsingException("QueryField cannot be empty.");
		}
		if (null == operator) {
			throw new ParameterParsingException("Comparator cannot be empty.");
		}
		if (queryField.equals(QueryField.PRIMARY_EMAIL) && operator.equals(Comparator.IS_EQUAL_TO)) {
			// validate email format
			if (!value.matches(EMAIL_PATTERN)) {
				throw new ParameterParsingException("Invalid E-Mail format!");
			}
		}
		if (queryField.equals(QueryField.PEN_SERIAL_NUMBER) && operator.equals(Comparator.IS_EQUAL_TO)) {
			// validate email format
			if (!value.matches(PEN_SERIAL_NUMBER_PATTERN)) {
				throw new ParameterParsingException("Invalid Pen Serial Number format!");
			}
		}
		if (queryField.equals(QueryField.PEN_DISPLAY_ID) && operator.equals(Comparator.IS_EQUAL_TO)) {
			// validate email format
			if (!value.matches(PEN_DISPLAY_ID_PATTERN)) {
				throw new ParameterParsingException("Invalid Pen Display Id format!");
			}
		}
		
		//Validations passed, construct the object and return..
		return new LookupCriteria(queryField, operator, value.trim());
	}

	/**
	 * 
	 * @param lookupField
	 * @return
	 */
	public static QueryField findQueryField(String lookupField) {
		QueryField queryFields[] = QueryField.values(); 
		
		for (QueryField qf : queryFields) {
			if (qf.getField().equals(lookupField)) {
				return qf;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param comparator
	 * @return
	 */
	public static Comparator findComparator(String comparator) {
		Comparator comparators[] = Comparator.values();
		
		for (Comparator c : comparators) {
			if (c.getName().equals(comparator)) {
				return c;
			}
		}
		
		return null;
	}
}
