package com.livescribe.framework.oauth.service;

import java.util.Date;
import java.util.List;

import com.livescribe.afp.AFPException;
import com.livescribe.aws.login.dto.AuthorizationDto;
import com.livescribe.aws.login.util.AuthorizationType;
import com.livescribe.framework.exception.DuplicateEmailAddressException;
import com.livescribe.framework.exception.ServerException;
import com.livescribe.framework.exception.UserNotFoundException;
import com.livescribe.framework.login.exception.AuthorizationException;
import com.livescribe.framework.login.exception.AuthorizationExpiredException;
import com.livescribe.framework.login.exception.AuthorizationNotFoundException;
import com.livescribe.framework.login.exception.RegistrationNotFoundException;
import com.livescribe.framework.login.exception.UserNotLoggedInException;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.PremiumCode;
import com.livescribe.framework.orm.consumer.User;

/**
 * Interface for accessing <code>Authorization</code> related data.
 * 
 * @author Mohammad M. Naqvi
 *
 */
public interface AuthorizationService {
	
	public Authorization getAuthorization(String email, AuthorizationType authorizationType) throws UserNotFoundException, DuplicateEmailAddressException;
	
	/**
	 * <p>Returns a <code>List</code> of authorizations (i.e.  OAuth access tokens)
	 * for the Livescribe user identified by the given UID.</p>
	 * 
	 * <p>If no authorizations are found, this method will return an empty 
	 * <code>List</code></p>.
	 * 
	 * @param uid The unique ID of the Livescribe user from the <code>consumer.user</code> table.
	 * @param authorizationType  The enumeration representing the provider.
	 * 
	 * @return a list of authorization records.
	 */
	public List<AuthorizationDto> findAuthorizationsByUid(String uid, AuthorizationType authorizationType);
	
	/**
	 * <p>Returns the <b>primary</b> authorization for the user identified by the given email and the given provider type.</p>
	 * 
	 * @param email email of the user
	 * @param authorizationType provider type
	 * @param reAuthorizing whether or not this is the first time or reauthorizing..
	 * @return the primary authorization for this user for given provider
	 * @throws UserNotFoundException if no user was found in the database by given email.
	 * @throws DuplicateEmailAddressException if multiple users were found by the given email id.
	 */
	public Authorization findPrimaryAuthByEmail(String email, AuthorizationType authorizationType) throws UserNotFoundException, DuplicateEmailAddressException;

	/**
	 * <p>Returns the <b>primary</b> authorization for the user uid and the given provider type.</p>
	 * 
	 * @param uid uid of the user
	 * @param authType provider type
	 * @return the primary authorization for this user for given provider
	 * @throws UserNotFoundException
	 * @throws AuthorizationException
	 */
	public Authorization findPrimaryAuthByUId(String uid, AuthorizationType authType) throws UserNotFoundException, AuthorizationException;

	/**
	 * <p>Returns the authorization for given userid, given providerUserId and the given provider type. Note that this authorization may or may not be the primary authorization for this user.</p>
	 * 
	 * @param uid uid of the user
	 * @param providerUserId providerUserId 
	 * @param authType provider type
	 * @return the matching authorization
	 * @throws UserNotFoundException if no user found by given uid
	 * @throws ServerException 
	 * @throws AuthorizationException if no authorization found by given parameters
	 */
	public Authorization findAuthByUIDAndProviderUserId(String uid, Long providerUserId, AuthorizationType authType) throws UserNotFoundException, ServerException, AuthorizationException;
	
	/**
	 * <p>Returns the <b>primary</b> authorization by Pen's displayId and the provider type.</p>
	 * 
	 * @param penDisplayId pen's display id
	 * @param authorizationType AuthorizationType
	 * 
	 * @return the primary authorization
	 * @throws AuthorizationException if no authorization found by given parameters
	 * @throws UserNotFoundException  if no user found to be the owner of the given pen
	 * @throws AFPException if no pen found by the given displayId
	 */
	public Authorization findByPenDisplayId(String penDisplayId, AuthorizationType authorizationType) throws RegistrationNotFoundException, AuthorizationException, UserNotFoundException, AFPException;
	
	/**
	 * <p>Returns the <b>primary</b> authorization by Pen's serail number and the provider type.</p>
	 * 
	 * @param serialNumber serial number of the pen
	 * @param authorizationType provider type
	 * 
	 * @return the primary authorization
	 * @throws RegistrationNotFoundException if the given pen not found to be registered
	 * @throws AuthorizationException if no authorization found by given parameters
	 * @throws UserNotFoundException if no user found to be the owner of the given pen
	 */
	public Authorization findByPenSerialNumber(String serialNumber, AuthorizationType authorizationType) throws RegistrationNotFoundException, AuthorizationException, UserNotFoundException;

	/**
	 * <p></p>
	 * 
	 * @param loginToken
	 * @return
	 * @throws UserNotLoggedInException
	 * @throws AuthorizationException
	 */
	public Authorization findPrimaryAuthByLoginToken(String loginToken) throws UserNotLoggedInException, AuthorizationException;
	
	/**
	 * <p>Find all existing authorizations by given loginToken</p>
	 * 
	 * @param loginToken
	 * @return
	 * @throws UserNotLoggedInException
	 * @throws AuthorizationException
	 * @throws DuplicateEmailAddressException 
	 * @throws UserNotFoundException 
	 */
	public List<AuthorizationDto> findAuthorizationListByLoginToken(String loginToken) throws UserNotLoggedInException, AuthorizationException, UserNotFoundException, DuplicateEmailAddressException;

	/**
	 * <p>Find all existing authorizations by given provider user id and provider type.</p>
	 * 
	 * @param providerUserId enUserId
	 * @param authorizationType authProviderType
	 * @return List of matching authorization records
	 */
	public List<AuthorizationDto> findAuthorizationListByProviderUserId(String providerUserId, AuthorizationType authorizationType);
	
	/**
	 * <p></p>
	 * 
	 * @param email
	 * @param accessToken
	 * @param enUserName
	 * @param enUserId
	 * @param enShardId
	 * @param authorizationType
	 * @param expirationDate
	 * @return {@link}com.livescribe.framework.orm.consumer.Authorization
	 * @throws UserNotFoundException
	 * @throws AuthorizationException
	 * @throws DuplicateEmailAddressException 
	 */
	public Authorization saveAuthorization(String email, String accessToken, String enUserName, int enUserId, String enShardId, AuthorizationType authorizationType, Date expirationDate) throws UserNotFoundException, AuthorizationException, DuplicateEmailAddressException;
	
	/**
	 * <p>Saves (and/or updates) Evernote Authorization in the database. 
	 * If an existing authorization for the same user by same evernote account is found, it gets updated with the new access token and the new expiration date. 
	 * If multiple authorizations found in the database, means a different Evernote account is used, then a new authorization is added and all old ones are updated with their isPrimary column reset to zero.</p>
	 * 
	 * @param lsUser the Livescribe user
	 * @param evernoteUser the Evernote user
	 * @param accessToken OAuth token
	 * @param expirationDate expiration date of the token
	 * @return newly created or the updated Authorization record
	 * @throws UserNotFoundException
	 * @throws DuplicateEmailAddressException if multiple users were found by the given email id.
	 */
	public Authorization saveEvernoteAuthorization(User lsUser, com.evernote.edam.type.User evernoteUser, String accessToken, Date expirationDate) throws UserNotFoundException, DuplicateEmailAddressException;

	
	/**
	 * <p>Makes the EN account by given enUserName primary/syncing for the user identified by given login token</p>
	 * 
	 * @param loginToken token of the user 
	 * @param enUserName to be made primary/syncing
	 * @author Mohammad M. Naqvi
	 * @throws UserNotLoggedInException 
	 * @throws AuthorizationException 
	 * @throws AuthorizationNotFoundException 
	 * @throws AuthorizationExpiredException 
	 */
	public void switchPrimaryEvernoteAccount(String loginToken, String enUserName) throws UserNotLoggedInException, AuthorizationException, AuthorizationNotFoundException, AuthorizationExpiredException;
	
	/**
	 * <p></p>
	 * 
	 * @param user
	 * @param code
	 * @return
	 */
	public PremiumCode saveEvernotePremiumCode(User user, String code);
	
	/**
	 * <p></p>
	 * 
	 * @param authorization
	 * @return
	 */
	public boolean isAuthorizationExpired(Authorization authorization);

	/**
	 * <p>Determines if the logged in user has an OAuth access token saved in
	 * the database.</p>
	 * 
	 * @param loginToken The login token of the user.
	 * 
	 * @return <code>true</code> if the user has an access token; 
	 * <code>false</code> if not.
	 * @throws UserNotLoggedInException 
	 */
	public boolean isAuthorized(String loginToken) throws UserNotLoggedInException;
	
	/**
	 * <p>Finds <b>all</b> <code>User</code> objects associated with the given userId and the provider.</p>
	 * 
	 * @param provider provider e.g. EN
	 * @param userId id of the user
	 * @return List of User objects matching with given criteria
	 * @throws AuthorizationException if no authorizations were found for the given id and provider
	 * @throws UserNotFoundException if no users were found for the given id and provider
	 */
	public List<User> findUsersByProviderUserId(String userId, AuthorizationType provider) throws AuthorizationException, UserNotFoundException;
}
