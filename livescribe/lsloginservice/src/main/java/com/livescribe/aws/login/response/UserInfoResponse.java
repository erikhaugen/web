package com.livescribe.aws.login.response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dom4j.Document;
import org.dom4j.Node;
import org.hibernate.Hibernate;

import com.livescribe.aws.login.client.ObjectFactory;
import com.livescribe.framework.dto.UserDTO;
import com.livescribe.framework.orm.consumer.Authorization;
import com.livescribe.framework.orm.consumer.User;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class UserInfoResponse extends ServiceResponse {

	@XStreamAlias("uid")
	private String uid;

	@XStreamAlias("firstName")
	private String firstName;

	@XStreamAlias("middleName")
	private String middleName;

	@XStreamAlias("lastName")
	private String lastName;

	@XStreamAlias("email")
	private String email;

	@XStreamAlias("gradYear")
	private Integer gradYear;

	@XStreamAlias("birthYear")
	private Integer birthYear;

	@XStreamAlias("major")
	private String major;

	@XStreamAlias("phone")
	private String phone;

	@XStreamAlias("organization")
	private String organization;

	@XStreamAlias("occupation")
	private String occupation;

	@XStreamAlias("sex")
	private Character sex;

	@XStreamAlias("university")
	private String university;

	@XStreamAlias("locale")
	private String locale;

	@XStreamAlias("address")
	private UserAddressResponse address;

	@XStreamAlias("authorizations")
	private List<AuthorizationResponse> authorizations;

	@XStreamAlias("confirmed")
	private boolean confirmed;
	
	@XStreamAlias("confirmationDate")
	private Date confirmationDate;
	
	@XStreamAlias("enabled")
	private boolean enabled;

	@XStreamAlias("sendEmail")
	private Boolean sendEmail;
	
	public UserInfoResponse() {
	}


	/**
	 * <p></p>
	 * 
	 * @param ResponseCode code
	 */
	public UserInfoResponse(ResponseCode code) {
		
		if (code != null) {
			this.setResponseCode(code);
		}
	}

	/**
	 * <p></p>
	 * 
	 * @param User user
	 */
	public UserInfoResponse(User user) {
		this(null, user);
	}

	/**
	 * <p>Constructs a <code>UserInfoResponse</code> with the given <code>User</code> object.</p>
	 * 
	 * This constructor DOES NOT use any authorization information from the <code>User</code> object.
	 * 
	 * @param ResponseCode code
	 * @param User user
	 */
	public UserInfoResponse(ResponseCode code, User user) {
		
		super();
		Hibernate.initialize(user);
		if (code != null) {
			this.setResponseCode(code);
		}
		if (user != null) {
			this.uid = user.getUid();
			this.firstName = "";
			this.middleName = "";	//TODO: hardcode for now since db does not have this column
			this.lastName = "";
			this.email = user.getPrimaryEmail();
			this.gradYear = 0;
			this.birthYear = 0;
			this.major = "";
			this.phone = "";
			this.organization = "";
			this.occupation = "";
			this.sex = '\0';
			this.university = "";
			this.locale = "en-US"; //TODO: hardcode for now since db does not have this column
			
			// MN - DE6908 Filtering the evernote authorization info from the response..
//			if (user.getAuthorizations().size() > 0)
//				this.authorizations = new ArrayList<AuthorizationResponse>();
//			for (Authorization anAuth : user.getAuthorizations()) {
//				this.authorizations.add(new AuthorizationResponse(null, anAuth));
//			}
			
			// KF - 3/12/13.  US9655 - Add some test data for GetUserInfo
			// Include test data for email addresses starting with "testuser" from livescribe or marketlive
			if (this.email.startsWith("testuser") 
			 && (this.email.endsWith("livescribe.com") || this.email.endsWith("marketlive.com"))) {
				this.firstName = "firstname";
				this.middleName = "m";
				this.lastName = "lastname";
				this.gradYear = 2005;
				this.birthYear = 1980;
				this.major = "Computer Science";
				this.phone = "5108889999";
				this.organization = "Livescribe";
				this.occupation = "Architect";
				this.sex = 'F';
				this.university = "UC Berkeley";

				this.address = new UserAddressResponse();
				this.address.setAddress1("11 Main Street");
				this.address.setAddress2("Suite 12000");
				this.address.setCity("Oakland");
				this.address.setCounty("Alameda");
				this.address.setZip("94111");
				this.address.setProvince("Quebec");
				this.address.setState("CA");
				this.address.setCountry("US");
			}
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @param ResponseCode code
	 * @param User user
	 */
	public UserInfoResponse(ResponseCode code, UserDTO user) {
		
		super();
		Hibernate.initialize(user);
		if (code != null) {
			this.setResponseCode(code);
		}
		if (user != null) {
			this.uid = user.getUid();
			this.firstName = user.getFirstName();
			this.middleName = user.getMiddleName();
			this.lastName = user.getLastName();
			this.email = user.getPrimaryEmail();
			this.gradYear = user.getGradYear();
			this.birthYear = user.getBirthYear();
			this.major = user.getMajor();
			this.phone = user.getPhone();
			this.enabled = user.getEnabled();
			this.sendEmail = user.getSendEmail();
			this.locale = "en-US"; //TODO: hardcode for now since db does not have this column
		}
	}

	/**
	 * <p></p>
	 *
	 * @param document
	 * 
	 * @throws ParseException 
	 */
	public UserInfoResponse(Document document) throws ParseException {
		
		Node responseCodeNode = document.selectSingleNode(ObjectFactory.NODE_RESPONSE_CODE);
		if (responseCodeNode != null) {	// SUCCESS
			if ("SUCCESS".equals(responseCodeNode.getText())) {
				setResponseCode(ResponseCode.SUCCESS);
				setUid(document.selectSingleNode(ObjectFactory.NODE_UID).getText());
				setEmail(document.selectSingleNode(ObjectFactory.NODE_EMAIL_ADDRESS).getText());
				
				//TODO: EEvL 2012-10-11 Need to add Address support (should probably be an AddressResponse Object)
				
				Node authorizationsNode = document.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS);
				if (null != authorizationsNode) {
					List<Node> authorizationsNodeResponseNodes = (List<Node>)authorizationsNode.selectNodes(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_RESPONSE);
					List<AuthorizationResponse> responseAuthorizations = new ArrayList<AuthorizationResponse>();
					for (Node anAuthorizationsNodeResponseNode : authorizationsNodeResponseNodes) {
						AuthorizationResponse subResponse = new AuthorizationResponse();
						//					subResponse.setUser(anAuthorizationsNodeResponseNode.selectSingleNode(NODE_USER).getText());
						subResponse.setEnUsername(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_USERNAME).getText());
						subResponse.setOauthAccessToken(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_OAUTHACCESSTOKEN).getText());
						subResponse.setProvider(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_PROVIDER).getText());
						subResponse.setEnShardId(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_SHARDID).getText());
						subResponse.setExpiration(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_EXPIRATION).getText()));
						subResponse.setCreated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_CREATED).getText()));
						subResponse.setLastModified(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_LASTMODIFIED).getText()));
						subResponse.setLastModifiedBy(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_LASTMODIFIEDBY).getText());
						subResponse.setEnUserId(Long.parseLong(anAuthorizationsNodeResponseNode.selectSingleNode(ObjectFactory.NODE_USERINFO_AUTHORIZATIONS_USERID).getText()));
						responseAuthorizations.add(subResponse);
					}
					setAuthorizations(responseAuthorizations);
				}
			}	
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @param code
	 * @param document
	 * 
	 * @throws ParseException 
	 */
	public UserInfoResponse(ResponseCode code, Document document) throws ParseException {
		
		this(document);
		this.setResponseCode(code);
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public Integer getGradYear() {
		return gradYear;
	}

	public void setGradYear(Integer gradYear) {
		this.gradYear = gradYear;
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public Character getSex() {
		return sex;
	}

	public void setSex(Character sex) {
		this.sex = sex;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public UserAddressResponse getAddress() {
		return address;
	}

	public void setAddress(UserAddressResponse address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public List<AuthorizationResponse> getAuthorizations() {
		return authorizations;
	}

	public void setAuthorizations(List<AuthorizationResponse> authorizations) {
		this.authorizations = authorizations;
	}

	public Boolean getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(Boolean sendEmail) {
		this.sendEmail = sendEmail;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserInfoResponse <uid=").append(uid);
		builder.append(", firstName=").append(firstName);
		builder.append(", middleName=").append(middleName);
		builder.append(", lastName=").append(lastName);
		builder.append(", email=").append(email);
		builder.append(", gradYear=").append(gradYear);
		builder.append(", birthYear=").append(birthYear);
		builder.append(", major=").append(major);
		builder.append(", phone=").append(phone);
		builder.append(", organization=").append(organization);
		builder.append(", occupation=").append(occupation);
		builder.append(", sendEmail=").append(sendEmail);
		builder.append(", sex=").append(sex);
		builder.append(", university=").append(university);
		builder.append(", locale=").append(locale);
		builder.append(", address=").append(address);
		builder.append(", authorizations=").append(authorizations).append(">");
		return builder.toString();
	}


	/**
	 * @return the confirmed
	 */
	public boolean isConfirmed() {
		return confirmed;
	}


	/**
	 * @return the confirmationDate
	 */
	public Date getConfirmationDate() {
		return confirmationDate;
	}


	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}


	/**
	 * @param confirmed the confirmed to set
	 */
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}


	/**
	 * @param confirmationDate the confirmationDate to set
	 */
	public void setConfirmationDate(Date confirmationDate) {
		this.confirmationDate = confirmationDate;
	}


	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
