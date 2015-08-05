package com.livescribe.web.registration.service.support;

import com.livescribe.framework.lsconfiguration.AppProperties;
import com.livescribe.framework.lsmail.QueueManager;
import com.livescribe.web.registration.controller.RegistrationData;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kiman on 07.02.14.
 */
@Component
public final class EMailNotifications {

    private Logger logger = Logger.getLogger(EMailNotifications.class);

    private QueueManager mailQueue = null;

    @Autowired
    private AppProperties appProperties;

    public EMailNotifications() { }


    /**
     * <p>Sets up a queue for use in sending email messages.</p>
     *
     * <p>Uses the <code>queue.server</code> property in the
     * <code>app.properties</code> file to define where the queue will
     * be set up.</p>
     */
    private void setupMessageQueue() {
        logger.debug("Setting up messge Queue..");

        List<String> queueServerList = new ArrayList<String>();

        String queueServer = appProperties.getProperty("queue.server", null);
        if (logger.isDebugEnabled()){
            logger.debug("KestrelServerHost -> " + queueServer);
        }

        queueServerList.add(queueServer);
        mailQueue = new QueueManager();
        mailQueue.setQueueManager("general", queueServerList);
    }


    public void pushEmail(String queueName, RegistrationData data, String penTypeName) {

        final String method = "pushEmail(" + queueName + ", " + penTypeName + "):	";

        if (logger.isDebugEnabled()) {
            logger.debug(method + " called.");
        }

        if (null == queueName || queueName.isEmpty()) {
            logger.error(method + "ERROR - queueName is null or empty, can't push the email message!!");
            return;
        }

        String fName = (data.getFirstName() == null) ? "" : data.getFirstName();
        String lName = (data.getLastName() == null) ? "" : data.getLastName();
        String name = fName + " " + lName;

        String locale = null;
        if (data.getLocale() != null )
            locale = data.getLocale().replace("_", "-");

        JSONObject jsonData = new JSONObject();
        if (data.getEmail() != null) {
            jsonData.put("email", data.getEmail());
        } else {
            logger.error(method + "ERROR - email is empty, can't push the email message!!");
            return;
        }
        if (data.getOptIn() != null) {
            jsonData.put("optIn", data.getOptIn().toString());
        } else {
            logger.error(method + "ERROR - optIn is empty, can't push the email message!!");
            return;
        }
        if (name.trim() != null && !name.trim().isEmpty()) {
            jsonData.put("name", name.trim());
        } else {
            logger.error(method + "ERROR - name is empty, can't push the email message!!");
            return;
        }
        if (penTypeName != null) {
            jsonData.put("penType", penTypeName.trim());
        } else {
            logger.error(method + "ERROR - penType is empty, can't push the email message!!");
            return;
        }
        if (locale != null) {
            jsonData.put("country", locale.trim());
        } else {
            logger.error(method + "ERROR - country is empty, can't push the email message!!");
            return;
        }

        JSONObject cmd = new JSONObject();
        cmd.put("PenRegistration", jsonData);

        pushEmail(queueName, cmd.toString() );

    }

    private void pushEmail(String queueName, String jsonString) {

        final String method = "pushEmail(" + queueName + ", " + jsonString + "):	";

        if (logger.isDebugEnabled()) {
            logger.debug(method + " called.");
        }

        if (null == this.mailQueue) {

            if (logger.isDebugEnabled())
                logger.debug(method + "MailQueue is Not setup yet..");

            setupMessageQueue();
        }

        if (mailQueue.isValidQueue() && jsonString != null) {
            mailQueue.pushToQueue(queueName, jsonString);
            if (logger.isDebugEnabled())
                logger.debug(method + "Pushing Email to " + jsonString);
        } else {
            logger.error(method + "ERROR - Invalid queue or No Email ID, can't push the email message!!");
      }
    }

}