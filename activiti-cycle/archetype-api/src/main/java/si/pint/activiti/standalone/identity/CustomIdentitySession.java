/***
 * Copyright (c) 2013.
 * University of Primorska, Andrej Marušič Institute. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * Project eOskrba (http://eOskrba.si) was financed by Slovenian Research
 * Agency and Ministry of Health of Republic of Slovenia.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package si.pint.activiti.standalone.identity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.cfg.IdentitySession;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.GroupEntity;
import org.activiti.engine.impl.identity.IdentityInfoEntity;
import org.activiti.engine.impl.identity.UserEntity;
import org.activiti.engine.impl.interceptor.Session;

/**
 * Example of a custom implementation of an identitySession, just logs the
 * method calls and returns a user with the given id in the createNewUser
 * method.
 * 
 * @author Nils Preusker (nils.preusker@camunda.com)
 */
public class CustomIdentitySession implements IdentitySession, Session {

	private static Logger log = Logger.getLogger(CustomIdentitySession.class.getName());

	// IdentitySession

	@Override
	public void createMembership(String userId, String groupId) {
		//System.out.println("1");
	}

	@Override
	public Group createNewGroup(String groupId) {
		//System.out.println("2");
		return null;
	}

	@Override
	public GroupQuery createNewGroupQuery() {
		//System.out.println("3");
		return new GroupQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
	}

	@Override
	public User createNewUser(String userId) {
		//System.out.println("4");
		return new UserEntity(userId);
	}

	@Override
	public UserQuery createNewUserQuery() {
		//System.out.println("5");
		return new UserQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
	}

	@Override
	public void deleteGroup(String groupId) {
		//System.out.println("6");
	}

	@Override
	public void deleteMembership(String userId, String groupId) {
		//System.out.println("7");
	}

	@Override
	public void deleteUser(String userId) {
		//System.out.println("8");
	}

	@Override
	public Group findGroupById(String groupId) {
		//System.out.println("9");
		return null;
	}

	@Override
	public List<Group> findGroupByQueryCriteria(Object query, Page page) {
		//System.out.println("10");
		GroupQueryImpl groupQuery = (GroupQueryImpl) query;
										     
		//TODO: vrni vse grupe, ki imajo ivana in so tipa security-role
		InitialDirContext lContext = null;
		List<Group> list = new ArrayList<Group>();

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "";
			if (groupQuery.getType() != null)
				lSearchString = "(&(&(businessCategory=" + groupQuery.getType() + ")(|(member=uid=" + groupQuery.getUserId() + "," + LdapConstants.LDAP_USERS_DN_BASE + ")(member=uidAtt=" + groupQuery.getUserId() + "," + LdapConstants.LDAP_USERS_DN_BASE + ")))(objectclass=groupOfNames))";
			else
				lSearchString = "(&(|(member=uid=" + groupQuery.getUserId() + "," + LdapConstants.LDAP_USERS_DN_BASE + ")(member=uidAtt=" + groupQuery.getUserId() + "," + LdapConstants.LDAP_USERS_DN_BASE + "))(objectclass=groupOfNames))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);

			while (lResults.hasMore()) { 
				List<Group> retList = getGroups(lResults.next());
				list.addAll(retList);
			}
			

		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}
		}		
		
		if (list != null) {
			Iterator<Group> it = list.iterator();
			while (it.hasNext()) {
				Group group = it.next();
				//System.out.println("group- id: " + group.getId() + ", name: " + group.getName() + ", type: " + group.getType());
			}
		}		
		return list;
	}

	@Override
	public long findGroupCountByQueryCriteria(Object query) {
		//System.out.println("11");
		GroupQueryImpl groupQuery = (GroupQueryImpl) query;
		
		InitialDirContext lContext = null;
		List<Group> list = new ArrayList<Group>();

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(|(member=uid=" + groupQuery.getUserId() + "," + LdapConstants.LDAP_USERS_DN_BASE + ")(member=uidAtt=" + groupQuery.getUserId() + "," + LdapConstants.LDAP_USERS_DN_BASE + "))(objectclass=groupOfNames))";
//			String lSearchString = "(&(member=uid=" + "ivan" + "," + LdapConstants.LDAP_USERS_DN_BASE + ")(objectclass=groupOfNames))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);

			while (lResults.hasMore()) { 
				List<Group> retList = getGroups(lResults.next());
				list.addAll(retList);
			}
			

		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}
		}			
		return list != null ? list.size() : 0;		

	}

	@Override
	public List<Group> findGroupsByUser(String userId) {
		//System.out.println("12");
		InitialDirContext lContext = null;
		List<Group> list = new ArrayList<Group>();

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(|(member=uid=" + userId + "," + LdapConstants.LDAP_USERS_DN_BASE + ")(member=uidAtt=" + userId + "," + LdapConstants.LDAP_USERS_DN_BASE + "))(objectclass=groupOfNames))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);

			while (lResults.hasMore()) { 
				List<Group> retList = getGroups(lResults.next());
				list.addAll(retList);
			}
			

		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}
		}			
		return list;
	}
	

	// this method always returns a user with the password "xxx"
	@Override
	public User findUserById(String userId) {
		//System.out.println("13");

		InitialDirContext lContext = null;
		User user = null;

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(|(&(uid=" + userId + ")(objectclass=inetOrgPerson))(&(uidAtt=" + userId + ")(objectclass=eOskrbaPerson)))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);

			if (lResults.hasMore())
				user = getUser(lResults.next());
			
			//lContext.bind(name, obj)
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}
		}
//		//System.out.println("userX: " + (user != null ? user.getId() : "") + ", " + (user != null ? user.getPassword() : ""));
		return user;
	}
	
	// this method always returns a user with the password "xxx"
	public User findUserById(String userId, String password) {
		//System.out.println("14");
		InitialDirContext lContext = null;
		User user = null;

		try {
			lContext = getLDAPContext(userId, password);
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(|(&(uid=" + userId + ")(objectclass=inetOrgPerson))(&(uidAtt=" + userId + ")(objectclass=eOskrbaPerson)))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);

			if (lResults.hasMore()) {
				user = getUser(lResults.next());
				user.setPassword(password);
			}
			
			//lContext.bind(name, obj)
		} catch (Throwable e) {
//			throw new RuntimeException(e);
			user = null;
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}
		}
//		//System.out.println("userX: " + (user != null ? user.getId() : "") + ", " + (user != null ? user.getPassword() : ""));
		return user;
	}	

	@Override
	public List<User> findUserByQueryCriteria(Object query, Page page) {
		//System.out.println("15");
		InitialDirContext lContext = null;
		UserQueryImpl userQuery = (UserQueryImpl) query;
		List<User> list = new ArrayList<User>();

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(|(&(uid=" + userQuery.getId() + ")(objectclass=inetOrgPerson))(&(uidAtt=" + userQuery.getId() + ")(objectclass=eOskrbaPerson)))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);

			while (lResults.hasMore()) {
				list.add(getUser(lResults.next()));
			}
			
			//lContext.bind(name, obj)
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}
		}
		//System.out.println("isce se user: " + userQuery.getId() + ", nasli smo " + list.size() + " elementov.");
		return list;

	}

	@Override
	public long findUserCountByQueryCriteria(Object query) {
		//System.out.println("16");
		return 0;
	}

	@Override
	public List<User> findUsersByGroupId(String groupId) {
		//System.out.println("17");
		return null;
	}

	@Override
	public void insertGroup(Group group) {
		//System.out.println("18");
	}

	@Override
	public void insertUser(User user) {
		//System.out.println("19");
	}

	@Override
	public boolean isValidUser(String userId) {
		//System.out.println("20");
		return false;
	}

	@Override
	public void updateGroup(Group updatedGroup) {
		//System.out.println("21");
	}

	@Override
	public void updateUser(User updatedUser) {
		//System.out.println("22");
	}

	// Session

	@Override
	public void close() {
		//System.out.println("23");
	}

	@Override
	public void flush() {
		//System.out.println("24");
	}

	// Utility Methods

	/**
	 * Utility method to log the method calls
	 */
	public static void trace(StackTraceElement e[]) {
		boolean doNext = false;
		for (StackTraceElement s : e) {
			if (doNext) {
				log.info(s.getClassName() + ": " + s.getMethodName());
				return;
			}
			doNext = s.getMethodName().equals("getStackTrace");
		}
	}

	@Override
	public void deleteUserInfoByUserIdAndKey(String userId, String key) {
		//System.out.println("deleteUserInfoByUserIdAndKey");
	}

	@Override
	public List<String> findUserInfoKeysByUserIdAndType(String userId,
			String userInfoType) {
		//System.out.println("findUserInfoKeysByUserIdAndType");
		return null;
	}

	@Override
	public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId,
			String key) {
		//System.out.println("findUserInfoByUserIdAndKey");
		return null;
	}

	@Override
	public IdentityInfoEntity findUserAccountByUserIdAndKey(String userId,
			String userPassword, String key) {
		//System.out.println("findUserAccountByUserIdAndKey");
		return null;
	}

	@Override
	public void setUserInfo(String userId, String userPassword, String type,
			String key, String value, String accountPassword,
			Map<String, String> accountDetails) {
		//System.out.println("setUserInfo");
	}

	/**
	 * Fetch the LDAP Initial Context
	 * 
	 * @return The InitialDirContext
	 * 
	 * @throws NamingException
	 * @throws IOException
	 */
	private InitialDirContext getLDAPContext() throws NamingException, IOException {
		InitialDirContext lLdapCtx = null;

		// Set up LDAP configuration settings
		Hashtable<String, String> lContextValues = new Hashtable<String, String>();

		lContextValues.put("java.naming.ldap.version", "3");
		lContextValues.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
		lContextValues.put("java.naming.security.authentication", "Simple");
		lContextValues.put("java.naming.referral", "follow");
		lContextValues.put("java.naming.provider.url", LdapConstants.LDAP_PROVIDER_URL);
		lContextValues.put("java.naming.security.principal", LdapConstants.LDAP_CONN_USERNAME);
		lContextValues.put("java.naming.security.credentials", LdapConstants.LDAP_CONN_PASSWORD);

		// Make LDAP connection
		lLdapCtx = new InitialDirContext(lContextValues);

		return lLdapCtx;
	}
	
	
	/*
	 * @return The InitialDirContext
	 * 
	 * @throws NamingException
	 * @throws IOException
	 */
	public static InitialDirContext getLDAPContext(String username, String password) throws NamingException, IOException {
		InitialDirContext lLdapCtx = null;

		// Set up LDAP configuration settings
		Hashtable<String, String> lContextValues = new Hashtable<String, String>();

		lContextValues.put("java.naming.ldap.version", "3");
		lContextValues.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
		lContextValues.put("java.naming.security.authentication", "Simple");
		lContextValues.put("java.naming.referral", "follow");
		lContextValues.put("java.naming.provider.url", LdapConstants.LDAP_PROVIDER_URL);
		lContextValues.put("java.naming.security.principal", "uid=" + username + "," + LdapConstants.LDAP_USERS_DN_BASE);
		lContextValues.put("java.naming.security.credentials", password);

		try {
			lLdapCtx = new InitialDirContext(lContextValues);
		} catch (AuthenticationException ae) {
			//try again with different RDN
			lContextValues.put("java.naming.security.principal", "uidAtt=" + username + "," + LdapConstants.LDAP_USERS_DN_BASE);
			lLdapCtx = new InitialDirContext(lContextValues);
		}

		return lLdapCtx;
	}	
	
	
	 private String getAttributeValue(SearchResult iResult, String iAttributeName) throws NamingException {
		 NamingEnumeration<?> lAllAttrValues = null;
		 Attribute lAttr = iResult.getAttributes().get(iAttributeName);
		 
		 if ( lAttr != null )
		 lAllAttrValues = lAttr.getAll();
		 
		 if ( lAllAttrValues != null && lAllAttrValues.hasMore() )
			 return (String) lAllAttrValues.next();
		 else
			 return null;
	}
	 
	/**
	 * Set User object based on SearchResult.
	 * 
	 * @param iResult
	 * @return
	 * @throws NamingException
	 * @throws IOException
	 */
	private User getUser(SearchResult iResult) throws NamingException, IOException {
		String iUserId = getAttributeValue(iResult, "uid");	
		String lEmail = getAttributeValue(iResult, "mail");
		String lFirstname = getAttributeValue(iResult, "givenName");
		String lLastname = getAttributeValue(iResult, "sn");
		String password = null;
		
		if (iUserId == null) {
			iUserId = getAttributeValue(iResult, "uidAtt");	
			lEmail = getAttributeValue(iResult, "eMailAtt");
			String firstNameLastName = getAttributeValue(iResult, "cnPatient"); 
			lFirstname = firstNameLastName.substring(0, firstNameLastName.indexOf(' '));
			lLastname = firstNameLastName.substring(firstNameLastName.indexOf(' ') + 1, firstNameLastName.length()); 
		}
//		Object pass = getAttributeValue(iResult, "userPassword");
		
		 
		final User lUser = new UserEntity(iUserId);
		lUser.setFirstName(lFirstname);
		lUser.setLastName(lLastname);
		lUser.setEmail(lEmail);
		
		//lUser.setBusinessEmail(lEmail + "@DUMMY.gc.ca");
//		lUser.setPassword("ivan");
		return lUser;
	}
	
	
	/**
	 * Return list of GroupEntity objects.
	 * 
	 * @param iResult
	 * @return
	 * @throws NamingException
	 * @throws IOException
	 */
	private List<Group> getGroups(SearchResult iResult) throws NamingException, IOException {
		 NamingEnumeration<?> lAllAttrValues = null;
		 
		 final List<Group> lGroups = new ArrayList<Group>();
		 final Attribute lAttr = iResult.getAttributes().get("member"); //we don't need group members at this point - just list of groups
		 final Attribute lAttrType = iResult.getAttributes().get("businessCategory");
		 final Attribute lAttrId = iResult.getAttributes().get("cn");
		 final Attribute lAttrDescription = iResult.getAttributes().get("description");
		 final Attribute lAttrRevision = iResult.getAttributes().get("o");
		 
		 
//		 if ( lAttr != null )
//			 lAllAttrValues = lAttr.getAll();
//		 
//		 while ( lAllAttrValues.hasMore() )
//		 {
//			 String lGroupDN = (String) lAllAttrValues.next();
			 GroupEntity groupE = new GroupEntity();
			 groupE.setId(lAttrId.get(0).toString());
			 groupE.setType(lAttrType.get(0).toString());
			 groupE.setName(lAttrDescription.get(0).toString());
			 
			 String revision = lAttrRevision.get(0).toString();
			 if (revision == null || revision.equals(""))
				 revision = "2";
			 
			 groupE.setRevision(Integer.parseInt(revision));
			 lGroups.add(groupE);
//			 lGroups.add(new GroupImpl(getExtractedIdFromDN(lGroupDN)));
//		 }
		 
		 return lGroups;
	 }	
	 
	 /**
	  * Extract 'uid' value from dn.
	  * 
	  * @param iGroupDN
	  * @return
	  */
	 private String getExtractedIdFromDN(String iGroupDN) {
		 StringTokenizer lTok = new StringTokenizer(iGroupDN, ",");
		 
		 String lGroupCN = lTok.nextToken();
		 
		 return lGroupCN.substring(4);
	 }

}
