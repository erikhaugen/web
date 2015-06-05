/*
 * Created:  Dec 7, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.dto;

/**
 * <p>Encapsulates the registration state of a device.</p>
 * 
 * <ul>
 * <li><code>existingUser</code></li> - <code>true</code> if the user already
 * existed.
 * <li><code>retry</code> - <code>true</code> if the registration has been started,
 * but not completed; <code>false</code> if the registration was either never
 * started, or is complete.</li> 
 * <li><code>penRegistrationStarted</code></li> - <code>true</code> if a record 
 * was found in the <code>registered_user</code> table.
 * <li><code>penRegistrationComplete</code></li> - <code>true</code> if the 
 * pen has completed its registration.
 * <li><code>userAccountConfirmed</code></li> - <code>true</code> if the user&apos;s
 * account has been confirmed.
 * </ul>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class RegistrationState {

	private boolean existingUser = false;
	private boolean retry = false;
	private boolean penRegistrationStarted = false;
	private boolean penRegistrationComplete = false;
	private boolean userAccountConfirmed = false;
	
	/**
	 * <p></p>
	 * 
	 */
	public RegistrationState() {
	}

	/**
	 * <p></p>
	 * 
	 * @return the existingUser
	 */
	public boolean isExistingUser() {
		return existingUser;
	}

	/**
	 * <p></p>
	 * 
	 * @return the retry
	 */
	public boolean isRetry() {
		return retry;
	}

	/**
	 * <p></p>
	 * 
	 * @return the penRegistrationComplete
	 */
	public boolean isPenRegistrationComplete() {
		return penRegistrationComplete;
	}

	/**
	 * <p></p>
	 * 
	 * @return the userAccountConfirmed
	 */
	public boolean isUserAccountConfirmed() {
		return userAccountConfirmed;
	}

	/**
	 * <p></p>
	 * 
	 * @param existingUser the existingUser to set
	 */
	public void setExistingUser(boolean existingUser) {
		this.existingUser = existingUser;
	}

	/**
	 * <p></p>
	 * 
	 * @param retry the retry to set
	 */
	public void setRetry(boolean retry) {
		this.retry = retry;
	}

	/**
	 * <p></p>
	 * 
	 * @param penRegistrationComplete the penRegistrationComplete to set
	 */
	public void setPenRegistrationComplete(boolean penRegistrationComplete) {
		this.penRegistrationComplete = penRegistrationComplete;
	}

	/**
	 * <p></p>
	 * 
	 * @param userAccountConfirmed the userAccountConfirmed to set
	 */
	public void setUserAccountConfirmed(boolean userAccountConfirmed) {
		this.userAccountConfirmed = userAccountConfirmed;
	}

	/**
	 * <p></p>
	 * 
	 * @return the penRegistrationStarted
	 */
	public boolean isPenRegistrationStarted() {
		return penRegistrationStarted;
	}

	/**
	 * <p></p>
	 * 
	 * @param penRegistrationStarted the penRegistrationStarted to set
	 */
	public void setPenRegistrationStarted(boolean penRegistrationStarted) {
		this.penRegistrationStarted = penRegistrationStarted;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("\n--------------------------------------------------\n");
		builder.append("   R e g i s t r a t i o n S t a t e\n");
		builder.append("--------------------------------------------------\n");
		builder.append("     existingUser:  " + existingUser + "\n");
		builder.append("            retry:  " + retry + "\n");
		builder.append("    acctConfirmed:  " + userAccountConfirmed + "\n");
		builder.append("       regStarted:  " + penRegistrationStarted + "\n");
		builder.append("      regComplete:  " + penRegistrationComplete + "\n");
		builder.append("--------------------------------------------------\n");
		
		return builder.toString();
	}
}
