package com.livescribe.framework.lsmail;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * <bold>imported from the LSFoundation project</bold>
 * 
 * The QueueManager class provide various convenient API to push data to the messaging queue and retrieve items that are in a queue.
 * The QueueManager work with a Queuing system that use a memcached protocol. The current queuing system use is Kestrel, see
 * http://robey.lag.net/2008/11/27/scarling-to-kestrel.html for more details
 * The basic concept of the class is to create a QueueManager with a name - so you can potentially differentiate different manage. In 
 * addition the name is used internally to create the memcached client (see SockIOPool documentation for details). 
 * When the QueueManager is created you can simply use it to push something in a queue (a queue is simply identified by a name), and you
 * can retrieve any object inserted in a particular queue through the getFromQueue's API. The getFromQueue API provide one method that take
 * a timeout, and it is strongly suggested to use such API if you know that the queue that you are requesting may not be constantly filled up.
 * In addition if you don't want to specify any particular timeout, it is strongly suggested to use the API
 * <code>
 * public Object getFromQueue(String queueName);
 * </code>
 * This method will use a default short timeout (default is 100 ms), thus the call may incur a small pause but will make your system 
 * run a lot smoother. By using a timeout, you will avoid creating an application that will be constantly polling the server 
 * (thus using a lot of CPU). 
 * 
 * When accessing the content of the queue, the QueueManager create each queue in Kestrel using the specified name but prefixed with "-" sign
 * in front... By doing that it may later open the API to get some other queue without a "-" that could be use more from a system point of view
 * or any other convenient thing. Feel free to remove that prefix is you don't think it's useful.
 * 
 * @author Mohammad M. Naqvi 
 *
 */
@Component
public class QueueManager {

	private final Logger logger = Logger.getLogger(QueueManager.class);
	
	protected String managerName;
	protected MemCachedClient memcacheClient;

	/**
	 * Constructor to initialize a Queue using the Memcached protocol. You need to provide the name of the queue manager
	 * and the list of servers that are used for the corresponding queue. Like memcached the queue system can be distributed across
	 * different system (using Kestrel right now as the back end system).
	 * A queue manager connect to a particular cluster of machine, and can obviously contains different queue. By providing 
	 * a name to the manager, you can differentiate different logical queue that can be spread in different cluster.
	 * 
	 * @param mName the queue manager name to use
	 * @param servers the list of server where Kestrel is running. Format should : 'hostname:port'
	 */
	public QueueManager(String mName, List<String> servers) {
		super();
		managerName = mName;
		if (servers != null && servers.size() > 0) {
			SockIOPool pool = SockIOPool.getInstance(managerName);
			String[] serverList = servers.toArray(new String[servers.size()]);
			pool.setServers(serverList);
			pool.initialize();
			// Create the client for that queue
			memcacheClient = new MemCachedClient(managerName);
			logger.debug("Created a new QueueManager ["+managerName+"] with servers: " + servers);
		} else {
			memcacheClient = null;
			logger.warn("QueueManager created but no server configured. QueueManager ["+managerName+"] will NOT work!");
		}
	}

	/**
	 * Push an object to the specified queue with no particular expiration date.
	 * @param queueName the name of the queue where the object should be added
	 * @param anObject the object to push in the queue
	 */
	public void pushToQueue(String queueName, Object anObject) {
		pushToQueue(queueName, anObject, null);
	}

	/**
	 * Push an object to the specified queue with an expiration date setup to expire in timeoutInMillis from now
	 * @param queueName the name of the queue where the object should be added
	 * @param anObject the object to push in the queue
	 * @param timeoutInMillis time out in milli seconds from now for the object to expire
	 */
	public void pushToQueue(String queueName, Object anObject, long timeoutInMillis) {
		pushToQueue(queueName, anObject, new java.util.Date(System.currentTimeMillis() + timeoutInMillis));
	}

	/**
	 * Push an object to the Queue.
	 * @param queueName the name of the queue where the object should be added
	 * @param anObject the object to push in the queue
	 * @param expirationDate when the object can be expired in the queue
	 */
	public void pushToQueue(String queueName, Object anObject, Date expirationDate) {
		// If no memcached client don't bother trying to put something in the queue
		if (!isValidQueue()) {
			logger.warn("The queue ["+queueName+"] is NOT valid. ");
			return;
		}
		memcacheClient.set(queueNameFrom(queueName), anObject, expirationDate, queueNameFrom(queueName).hashCode());
		logger.debug("Pushed: [" + anObject + "] in queue ["+queueName+"]");
	}

	/**
	 * Return true if the current Queue has an active queue (information can be pushed an retrieved).
	 * @return
	 */
	public boolean isValidQueue() {
		return memcacheClient != null;
	}
	
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public MemCachedClient getMemcacheClient() {
		return memcacheClient;
	}
	public void setMemcacheClient(MemCachedClient memcacheClient) {
		this.memcacheClient = memcacheClient;
	}

	@Override
	public String toString() {
		return "<QueueManager> Queue name ["+managerName+"] - client: " + memcacheClient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((managerName == null) ? 0 : managerName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueueManager other = (QueueManager) obj;
		if (managerName == null) {
			if (other.managerName != null)
				return false;
		} else if (!managerName.equals(other.managerName))
			return false;
		return true;
	}
	
	private String queueNameFrom(String aName) {
		return "-" + aName;
	}
}