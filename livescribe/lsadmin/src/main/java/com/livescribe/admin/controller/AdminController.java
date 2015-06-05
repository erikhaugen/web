/**
 * 
 */
package com.livescribe.admin.controller;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.livescribe.admin.config.AppProperties;
import com.livescribe.admin.controller.dto.PageDTO;
import com.livescribe.admin.controller.dto.SessionDTO;
import com.livescribe.admin.exception.MultipleRecordsFoundException;
import com.livescribe.admin.exception.RegisteredDeviceNotFoundException;
import com.livescribe.admin.exception.UserNotFoundException;
import com.livescribe.admin.service.AdminService;
import com.livescribe.admin.service.UserService;
import com.livescribe.aws.login.client.LoginClient;
import com.livescribe.framework.exception.ClientException;
import com.livescribe.framework.exception.InvalidParameterException;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.orm.lsevernotedb.Document;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.client.RegistrationClient;
import com.livescribe.web.registration.exception.RegistrationNotFoundException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @author <a href="mailto:MNaqvi@livescribe.com">Mohammad M. Naqvi</a>
 * @version 1.0
 */
@Controller
public class AdminController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public static final String VIEW_USER_LOOKUP	= "infoLookup";
	public static final String VIEW_DOCUMENT_DETAILS = "documentDetails";
	public static final String VIEW_SYNC_DOCUMENTS = "syncHistory";
	
	private static final String USER_LOOKUP_PATH = "/infoLookup.htm";
	private static final String USER_SYNCDOCS_PATH = "/services/lsadmin/sync/docs/user/";
	private static final String SYNCDOCS_PATH = "/sync/docs/";
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * <p></p>
	 *
	 */
	public AdminController() {
		
	}

	/**
	 * <p>Clears <b>unarchived</b> documents from a user&apos;s account.</p>
	 * 
	 * @param uid The unique ID of the user&apos;s record in the <code>consumer.user</code> table.
	 * @param displayId The 14-character display ID of the device.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/clearSyncDocs/user/{uid}/pen/{displayId}", method = {RequestMethod.POST, RequestMethod.GET})
	@PreAuthorize("hasAnyRole('superUser', 'deleteInformationUser')")
    public ModelAndView clearAllSyncData(@PathVariable("uid") String uid, @PathVariable("displayId") String displayId) {
		
		String method = "clearAllSyncData(" + uid + ", " + displayId + "):  ";
		
		logger.debug(method + "START - - - - - - - - - - - - - - - - - - - -");
		String redirectPath = SYNCDOCS_PATH + "user/" + uid + "/pen/" + displayId;

		ModelAndView mv = new ModelAndView();
		
		try {
			adminService.clearSyncDocsFromUserAndPen(uid, displayId);
		} catch (ClientException ce) {
			String msg = "ClientException thrown while attempting to delete sync data for pen '" + displayId + "' and uid '" + uid + "'.";
			logger.error(method + msg, ce);
			mv.addObject("errorMessage", ce.getMessage() + " at " + new Date());
			mv.setViewName("redirect:" + redirectPath);
			return mv;
		} catch (URISyntaxException use) {
			String msg = "URISyntaxException thrown while attempting to delete sync data for pen with display ID '" + displayId + "'.";
			logger.error(method + msg, use);
			mv.addObject("errorMessage", msg + " at " + new Date());
			mv.setViewName("redirect:" + redirectPath);
			return mv;
		}
		mv.addObject("successMessage", "Document(s) deleted successfully!");
		logger.debug("Redirecting to:  " + redirectPath);
		mv.setViewName("redirect:" + redirectPath);

		return mv;
	}
	
	/**
	 * <p></p>
	 * 
	 * @param displayId
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sync/docs/pen/{displayId}", method = RequestMethod.GET)
	public ModelAndView findSyncDocumentsByPen(@PathVariable("displayId") String displayId) {		
	
		List<Document> list = this.adminService.findSyncDocumentsByPen(displayId);

		ModelAndView mv = new ModelAndView();
		mv.setViewName(VIEW_SYNC_DOCUMENTS);
		mv.addObject("documents", list);
		mv.addObject("displayId", displayId);
		
		return mv;
	}
	
	@RequestMapping(value = "/sync/docs/detail/{documentId}", method = RequestMethod.GET)
    public ModelAndView displayDocumentDetailByDocId(@PathVariable("documentId") String documentId) {
        
        String method = "displayDocumentDetailByDocId(" + documentId + "):  ";
        logger.debug(method + "called");
        
        ModelAndView mv = new ModelAndView();
        Document document = adminService.findSyncDocumentById(documentId);
        if (document != null) {
            mv.addObject("documentId", documentId);
            mv.addObject("document", document);
            List<PageDTO> pageList = adminService.findPagesOfSyncDocumentByPen(documentId);
            List<SessionDTO> sessionList = adminService.findSessionsOfSyncDocumentByPen(documentId);
            mv.addObject("pages", pageList);
            mv.addObject("sessions", sessionList);
        }
        
        mv.setViewName(VIEW_DOCUMENT_DETAILS);
        
        return mv;
    }

	/**
	 * <p>Finds all documents a user has sync&apos;d to Evernote.</p>
	 * 
	 * @param uid The unique ID of the user.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sync/docs/user/{uid}", method = RequestMethod.GET)
	public ModelAndView findSyncDocumentsByUser(@PathVariable("uid") String uid) {
		
		String method = "findSyncDocumentsByUser(" + uid + "):  ";
        logger.debug(method + "-------------    S T A R T E D    ---------------");
        
		User user = null;
        user = this.userService.findByUID(uid);

        Map<String, List<Document>> syncedDocsToPrimaryENAuth = adminService.findSyncDocsMapByUserForPrimaryENAuth(user);
        Map<String, List<Document>> syncedDocsToNonPrimaryENAuth = adminService.findSyncDocsMapByUserForNonPrimaryENAuth(user);
		
        ModelAndView mv = new ModelAndView();
        mv.setViewName(VIEW_SYNC_DOCUMENTS);
        mv.addObject("docsSyncedToPrimaryEn", syncedDocsToPrimaryENAuth);
        mv.addObject("docsSyncedToNonPrimaryEn", syncedDocsToNonPrimaryENAuth);
        mv.addObject("user", user);

        logger.debug(method + "-------------    F I N I S H E D    ---------------");
		return mv;
	}
	
	/**
     * <p>Finds all documents a user has sync&apos;d by a registered pen to Evernote.</p>
     * 
     * @param uid The unique ID of the user.
     * @param displayId display Id of the pen.
     * 
     * @return
     */
    @RequestMapping(value = "/sync/docs/user/{uid}/pen/{displayId}", method = RequestMethod.GET)
    public ModelAndView findSyncDocumentsByUserAndPen(@PathVariable("uid") String uid, @PathVariable("displayId") String displayId) {
        
        String method = "findSyncDocumentsByUserAndPen(" + uid + ", " + displayId + "):  ";
        logger.debug(method + "-------------    S T A R T E D    ---------------");
        
        User user = this.userService.findByUID(uid);
        if (null == user) {
        	// TO-DO Handle this scenario
        }
 
        //List<Document> list = this.adminService.findSyncDocumentsByUserAndPen(uid, displayId);
        
        Map<String, List<Document>> syncedDocsToPrimaryENAuth = adminService.findSyncDocsMapByUserAndPenForPrimaryENAuth(user, displayId);
        Map<String, List<Document>> syncedDocsToNonPrimaryENAuth = adminService.findSyncDocsMapByUserAndPenForNonPrimaryENAuth(user, displayId);
        
        ModelAndView mv = new ModelAndView();
        mv.setViewName(VIEW_SYNC_DOCUMENTS);
        mv.addObject("docsSyncedToPrimaryEn", syncedDocsToPrimaryENAuth);
        mv.addObject("docsSyncedToNonPrimaryEn", syncedDocsToNonPrimaryENAuth);
        mv.addObject("user", user);
        mv.addObject("displayId", displayId);
      
        //mv.addObject("documents", list);

        logger.debug(method + "-------------    F I N I S H E D    ---------------");
        return mv;
    }
	
	/**
	 * <p>Unregister a pen from its user.</p>
	 * 
	 * @param penSerial The numeric serial number of the pen to unregister.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/unregister/{penSerial}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('superUser', 'deleteInformationUser')")
	public ModelAndView unregisterPen(@PathVariable("penSerial") String penSerial,
			@RequestParam(required=false) String redirectPath) {
		
		String method = "unregisterPen(" + penSerial + "):  ";
		
		ModelAndView mv = new ModelAndView();
		
		User user = null;
		try {
			this.adminService.unregisterPen(penSerial);
			user = this.userService.findByPenSerialNumber(penSerial);
		} catch (MultipleRecordsFoundException mrfe) {
			logger.error(method, mrfe);
			mv.addObject("error", "Found multiple pen registration records for serial number '" + penSerial + "'.");
		} catch (RegisteredDeviceNotFoundException rdnfe) {
			logger.error(method, rdnfe);
			mv.addObject("error", "No pen registration found for serial number '" + penSerial + "'.");
		}
		
		logger.debug("Redirecting to:  " + redirectPath);
		mv.setViewName("redirect:" + redirectPath);
		mv.addObject("user", user);

		return mv;
	}
	
	/**
     * <p>Unregister a vector pen from its user.</p>
     * 
     * @param penSerial The numeric serial number of the pen to unregister.
     * 
     * @return
     */
    @RequestMapping(value = "/unregister/vector/{penSerial}/{userEmail}/{appId}.html", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('superUser', 'deleteInformationUser')")
    public ModelAndView unregisterVectorPen(
            @PathVariable("penSerial") String penSerial,
            @PathVariable("userEmail") String userEmail,
            @PathVariable("appId")     String appId,
            @RequestParam(required=false) String redirectPath) {
        
        String method = "unregisterVectorPen(" + penSerial + "):  ";
        
        ModelAndView mv = new ModelAndView();
        
        try {
            RegistrationClient client = new RegistrationClient();
            ServiceResponse res = client.unregister(appId, penSerial, userEmail);
            if (!res.getResponseCode().equals(ResponseCode.SUCCESS))
                mv.addObject("error", "Fail to unregister pen (serial number=" + penSerial + ").");
        } catch (InvalidParameterException e) {
            logger.error(method, e);
            mv.addObject("error", "Invalid parameter while trying to unregister pen (serial number=" + penSerial + ").");
        } catch (RegistrationNotFoundException e) {
            logger.error(method, e);
            mv.addObject("error", "No pen registration found for serial number '" + penSerial + "'.");
        } catch (com.livescribe.framework.exception.MultipleRecordsFoundException e) {
            logger.error(method, e);
            mv.addObject("error", "Found multiple pen registration records for serial number '" + penSerial + "'.");
        } catch (Exception e) {
            logger.error(method, e);
            mv.addObject("error", "Unknown error while trying to unregister pen (serial number=" + penSerial + ").");
        }
        
        logger.debug("Redirecting to:  " + redirectPath);
        mv.setViewName("redirect:" + redirectPath);

        return mv;
    }
	
	/**
	 * <p>Delete a user from the system.</p>
	 * 
	 * @param email
	 * @param redirectPath
	 * @return
	 */
	@RequestMapping(value = "/deleteuser", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('superUser')")
	public ModelAndView deleteUser(@RequestParam("email") String email,
			@RequestParam(required=false) String redirectPath) {
		
		String method = "deleteUser(" + email + "):  ";
		logger.info("BEFORE - " + method);
		
		ModelAndView mv = new ModelAndView();
		String errorMessage = "You have successfully deleted user '" + email + "'. PLEASE GO TO LSSupport TO DELETE THIS USER FROM WOAPP SYSTEM.";

		if ((email == null) || (email.isEmpty())) {
			errorMessage = "No email address was provided.  Unable to delete user.";
			redirectPath = redirectPath + "&successMessage=" + errorMessage;
			logger.debug("Redirecting to:  " + redirectPath);
			mv.setView(new RedirectView(redirectPath));
			logger.info("AFTER - " + method);
			
			return mv;
		}
		
		try {
			logger.debug("Deleting user " + email);
			userService.deleteConsumerUser(email);
		} catch(UserNotFoundException e) {
			logger.error(method, e);
			errorMessage =  "The user '" + email + "' could not be found.";
			
		} catch (ClientException ce) {
			String msg = "Error while attempting to delete the user '" + email + "'.";
			logger.error(method + msg, ce);
			mv.addObject("errorMessage", ce.getMessage() + " Error occured at " + new Date());
			mv.setView(new RedirectView(redirectPath));
			return mv;
		}
		
		redirectPath = redirectPath + "&successMessage=" + errorMessage;
		logger.debug("Redirecting to:  " + redirectPath);
		mv.setView(new RedirectView(redirectPath));
		
		logger.info("AFTER - " + method);
		
		return mv;
	}
	
	/**
     * <p>Delete a user from the system.</p>
     * 
     * @param email
     * @param redirectPath
     * @return
     */
    @RequestMapping(value = "/changePasswd", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('superUser', 'deleteInformationUser')")
    public ModelAndView changePasswd(@RequestParam("email") String email,
            @RequestParam("newPasswd") String newPasswd,
            @RequestParam(required=false) String redirectPath) {
        
        String method = "changePasswd(" + email + ", " + newPasswd + "): ";
        
        ModelAndView mv = new ModelAndView();
        String errorMessage = "";
        try {
            logger.debug("Change password for user " + email);
            LoginClient loginClient = new LoginClient();
            loginClient.changePasswordWithoutUserTokenAndOldPassword(email, newPasswd);
            errorMessage = "You have successfully change password for user '" + email + "'.";
        } catch (Exception e) {
            logger.error(method, e);
            errorMessage = "There is exception occured when trying to change password: " + e.getMessage();
        }
        
        redirectPath = redirectPath + "&successMessage=" + errorMessage;
        logger.debug("Redirecting to:  " + redirectPath);
        mv.setView(new RedirectView(redirectPath));

        return mv;
    }
	
	@RequestMapping(value = "/auth/remove/{uid}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('superUser', 'deleteInformationUser')")
	public ModelAndView removeAuthorization(@PathVariable("uid") String uid,
			@RequestParam(required=false) String redirectPath) {
		
		String method = "removeAuthorization(" + uid + "):  ";
		logger.debug(method);
		
		ModelAndView mv = new ModelAndView();
		
		try {
			adminService.removeAuthorization(uid);
			
		} catch (UserNotFoundException e) {
			logger.error(method, e);
			mv.addObject("error", e.getMessage());
		}
		
		logger.debug("Redirecting to:  " + redirectPath);
		mv.setView(new RedirectView(redirectPath));
		
		return mv;
	}
	
	@RequestMapping(value = "/delete/doc/{documentId}/pen/{displayId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('superUser', 'deleteInformationUser')")
    public ModelAndView deleteDocument(@PathVariable("documentId") String documentId, @PathVariable("displayId") String displayId) {

		String method = "deleteDocument (" + documentId + ", " + displayId + "):  ";
		logger.debug(method + " **************************************** S T A R T ****************************************");
		ModelAndView mv = new ModelAndView();
		
		String uid = userService.getUIDByDocumentId(Long.parseLong(documentId));

		String redirectPath = "";
		if (null == uid) {
			// Unable to determine the user, redirect to the user-lookup page
			logger.debug("Unable to determine the user, redirecting to the user lookup page!");
			redirectPath = USER_LOOKUP_PATH;
		} else {
			redirectPath = USER_SYNCDOCS_PATH + uid;
		}
		
		try {
			adminService.deleteDocument(documentId, displayId);
		} catch (Exception e) {
			String msg = " Exception thrown while attempting to delete a document with id " + documentId + " of the pen '" + displayId + "'.";
			logger.error(method + msg, e);
			mv.addObject("errorMessage", msg + " at " + new Date());
			mv.setView(new RedirectView(redirectPath));
			return mv;
		} 	

		logger.info(method + " Requested document has been deleted, now redirecting to:  " + redirectPath);

		mv.addObject("successMessage", "Requested document was deleted successfully.");
		mv.setView(new RedirectView(redirectPath));
		logger.debug(method + " **************************************** E N D ****************************************");

		return mv;
    }
}
