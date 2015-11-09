/**
 * 
 */
package com.livescribe.framework.audit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

/**
 * <p>Adds log entries before and after the invocation of methods.</p>
 * 
 * The following beans must be added to the Spring <code>context.xml</code> file
 * for this class to be used:
 * 
 * Service bean to be &quot;audited&quot;:
 * <code><bean id="loginServiceBean" class="com.livescribe.framework.login.service.LoginServiceImpl" /></code>
 * 
 * Logging bean:
 * <code><bean id="auditMethodAdvice" class="com.livescribe.framework.audit.AuditMethodInterceptor" /></code>
 * 
 * AOP-proxied bean to be used at runtime:
 * <code>
 * 	<bean id="loginService" class="org.springframework.aop.framework.ProxyFactoryBean">
 * 		<property name="target" ref="loginServiceBean" />
 * 		<property name="interceptorNames">
 * 			<list>
 * 				<value>auditMethodAdvice</value>
 * 			</list>
 * 		</property>
 * 	</bean>
 * </code>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AuditMethodInterceptor implements MethodInterceptor {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * <p>Default class constructor.</p>
	 *
	 */
	public AuditMethodInterceptor() {
		logger.debug("Instantiated.");
	}

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		String method = invocation.getMethod().getName();
//		Object[] params = invocation.getArguments();
		
		String target = invocation.getMethod().getDeclaringClass().getName();
		
		long start = System.currentTimeMillis();
		
		logger.info("BEFORE - " + method + "()");
		logger.debug("Class:  " + target);
		
		//	Invoke the advised method.
		Object result = null;
		try {
			result = invocation.proceed();
		}
		catch (Exception e) {
			long exp = System.currentTimeMillis();
			long elapsedTime = exp - start;
			logger.info("EXCEPTION - " + method + "():  After " + elapsedTime + " milliseconds.", e);
			throw e;
		}
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		
		logger.info("AFTER - " + method + "():  Completed in " + duration + " milliseconds.");

		return result;
	}
}
