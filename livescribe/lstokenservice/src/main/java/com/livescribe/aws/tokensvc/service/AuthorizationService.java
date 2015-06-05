/*
 * Created:  Nov 28, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.service;

import com.amazonaws.services.securitytoken.model.Credentials;
import com.livescribe.framework.orm.manufacturing.Pen;

/**
 * <p>Encapsulates logic to generate IAM policies and generate temporary
 * credentials from them.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Deprecated
public interface AuthorizationService {

	/**
	 * <p>Creates temporary AWS credentials for the given pen to use when
	 * accessing its SQS queue.</p>
	 * 
	 * @param pen The pen requesting access to its SQS queue.
	 * 
	 * @return temporary AWS credentials.
	 */
	public Credentials authorizePenAccessToQueue(Pen pen);
	
	public Credentials authorizeAccessToAWSResource();
}
