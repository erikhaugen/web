/**
 * Created:  Mar 28, 2014 4:16:50 PM
 */
package com.livescribe.scratch.velocity;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface EvernoteProcessor {

	public StringWriter merge(Context context) throws VelocityException, IOException;
}
