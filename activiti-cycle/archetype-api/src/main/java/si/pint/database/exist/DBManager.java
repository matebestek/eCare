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
package si.pint.database.exist;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lombok.extern.log4j.Log4j;

import org.exist.xmldb.CollectionManagementServiceImpl;
import org.exist.xmldb.XQueryService;
import org.exist.xmldb.XmldbURI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.exceptions.SecurityPolicyViolationException;
import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.model.User;

/* *
 *VARNOSTNA POLITIKA:
 *Parametri:
 *-id uporabnika aplikacije->pripada neki domeni in neki vlogi
 *-id pacienta za katerega dobivamo podatke-->vidi samo svoje podatke
 *-domena (hujšaje, astma,...)
 *-arhetip id
 *-query znotraj arhetipa
 */
@Log4j
public class DBManager {
	
	private static DBManager _instance = null;
	
	private DBManager(){
		super();
		
	}
	
	private DBManager(SystemType sys){
		super();
		system = sys;
	}
	
			
	public static DBManager getInstance(SystemType sys){
		if(_instance == null){
			_instance = new DBManager(sys);			
			log.info("nova instanca DBManager");
		}
		return _instance;
	}
	
	
	public static String ENGINE_USER_ID = "engine_user_id";
	

	// path to database on server
	private String URI = "xmldb:exist://"
			+ SystemConstant.EXIST_HOST_NAME.toString() + ":8080/exist/xmlrpc";

	// user
	private String ADMIN = SystemConstant.EXIST_USERNAME.toString();

	// password
	private String PASSWORD = SystemConstant.EXIST_PASSWORD.toString();
	
	private static enum TraceEventType {
		READ,
		WRITE,
		WRONG_ACCESS;				
	};
	
	public static enum SystemType{
		NOTDEFINED(0),
		EOSKRBAPROCESSENGINE(1),
		EOSKRBAWEBAPP(2),
		EOSKRBAMOBILEAPP(3),
		EOSKRBATEST(4);
		
		private final int id;
		SystemType(int id){
			this.id = id;
		}
		
		
		public int id(){
			return id;
		}
	};
	
	private SystemType system = null;
	
	public void setSystemType(SystemType sys){
		this.system = sys;
	}
	
	
	
	
	// delete collection
	// id = patient id
 	public void deleteCollection(String id, String domain)
			throws Exception {
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);

		Collection col = null;
		XMLResource res = null;
		Collection col1 = null;
		try {
			// get the collection

			col = DatabaseManager.getCollection(URI + domain, ADMIN, PASSWORD);
			// CollectionManagementService mgt = (CollectionManagementService)
			// parent.getService("CollectionManagementService", "1.0");
			col = col.getChildCollection(domain);

			String[] childs = col.listChildCollections();
			// String[] resources = col.listResources();
			for (int i = 0; i < childs.length; i++) {
				if (childs[i].contains(id)) {// poiščem userja
					col1 = col.getChildCollection(childs[i]);
					CollectionManagementServiceImpl mgt = (CollectionManagementServiceImpl) col
							.getService("CollectionManagementService", "1.0");
					mgt.removeCollection(XmldbURI.create(id));
					// String[] resources = col1.listResources();
					// for(int j = 0; j < resources.length; j++){
					// col1.removeResource(col1.getResource(resources[j]));
					// }

				}

			}
			// col = col.getChildCollection(id);

			// System.out.print("Ime kolekcije: " + col.getName() + "\n");
			// CollectionManagementServiceImpl mgt =
			// (CollectionManagementServiceImpl)
			// col.getService("CollectionManagementService", "1.0");
			// String temp[] = col.listChildCollections();
			// String temp1[] = null;
			// for (int i = 0; i < temp.length; i++) {
			// col1 = col.getChildCollection(temp[i]);
			// temp1 = col1.listResources();
			// for (int j = 0; j < temp1.length; j++) {
			// col1.removeResource(col1.getResource(temp1[j]));
			// }
			// }

		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}

	}

	private void checkSecurityPolicy(String userId, String patientId) throws SecurityPolicyViolationException{
		//employeeType mora bit enak pri obeh
		boolean b = false;
		if(userId.equalsIgnoreCase(ENGINE_USER_ID)){//tole lahko uporablja samo process engine, ki sam poganja procese
			return;
		}
		b = UserRegistryFactory.isSameDomain(userId, patientId);
		if(!b){
			log.warn("SECURITY POLICY VIOLATION userId="+userId+" patientId=" +patientId);
			saveTrace(TraceEventType.WRONG_ACCESS.name(), system.name(), "chechSecurityPolicy", "WRONG DOMAIN ACCESS", userId, patientId);
			//throw new SecurityPolicyViolationException(userId,patientId);
		}
	}
	
	/**
	 * @param userId id uporabnika aplikacije, zaradi enforce-anja varnostne politike
	 * */
	public String read(String userId, String patientId, String archetype_id,
			Date dateD, String query) throws Exception {
		checkSecurityPolicy(userId,patientId);
		

		// get patient from LDAP
		User ldapUsr = UserRegistryFactory.getUserFromLDAP(patientId);
		if (ldapUsr == null)
			return null;

		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		String date = dateD.getTime() + ".xml";
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patientId, ADMIN,
					PASSWORD);
			col2 = col1.getChildCollection(ldapUsr.getEmployeeType()
					.toDomainName());
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patientId);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col.getName());
			col1 = col.getChildCollection(archetype_id);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col1.getName());
			// String temp3[] = col1.listResources();
			// for(int i=0; i<temp3.length;i++)
			// System.out.println(temp3[i]);
			res = (XMLResource) col1.getResource(date);
			// System.out.println(date);
			if (res == null) {
				System.out.println("Date doesn't exist! ");
				return null;
			}
			// System.out.println("datum je " + res.getId());
			// col.setProperty(OutputKeys.INDENT, "no");
			// String exprS = "//archetype_details/archetype_id/value";
			XQueryService xqps = (XQueryService) col1.getService(
					"XPathQueryService", "1.0");
			xqps.setNamespace("ns", "http://schemas.openehr.org/v1");
			xqps.setNamespace("xsi",
					"http://www.w3.org/2001/XMLSchema-instance");

			// for(int i=0; i<queryt.length;i++)
			// System.out.println("Ta vrsta:" + queryt[i]);
			String queryt[] = query.split("/");
			query = "";
			int j = 0;
			if (queryt[1].equals(queryt[0]))
				j = 1;
			for (int i = j; i < queryt.length; i++)
				if (i != queryt.length - 1 && i != j)
					query = query + "/ns:" + queryt[i];
				else if (i != j)
					query = query + "/" + queryt[i];

			if (query.substring(0, 0) != query.substring(1, 1)) {
				query = "/" + query;
			}
			// System.out.println(query);
			CompiledExpression compiled = xqps.compile(query);
			// xqps.clearNamespaces();
			Resource res1;
			String temp = null;
			try {
				res1 = xqps.execute(res, compiled).getResource(0);
				temp = (String) (res1).getContent();
			} catch (Exception e) {
				System.out.println("Napacen query 'read'! " + query);

			}
			saveTrace(TraceEventType.READ.name(), system.name(), "read", temp, userId, patientId);
			return temp;

			// if(res == null) {
			// System.out.println("document not found!");
			// } else {
			// System.out.println(res.getContent());
			// }
		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}

	public ResourceSet readLastObject(String userId, String patient_id, String archetype_id,
			String objectQuery) throws Exception {
		checkSecurityPolicy(userId,patient_id);
		// get patient from LDAP
		User ldapUsr = UserRegistryFactory.getUserFromLDAP(patient_id);
		if (ldapUsr == null)
			return null;

		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patient_id, ADMIN,
					PASSWORD);
			col2 = col1.getChildCollection(ldapUsr.getEmployeeType()
					.toDomainName());
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patient_id);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col.getName());
			col1 = col.getChildCollection(archetype_id);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col1.getName());
			String temp3[] = col1.listResources();
			// for(int i=0; i<temp3.length;i++)
			// System.out.println(temp3[i]);
			res = (XMLResource) col1.getResource(temp3[findMax(temp3)]);
			// System.out.println(date);
			if (res == null) {
				System.out.println("Date doesn't exist! ");
				return null;
			}
			// System.out.println("datum je " + res.getId());
			// col.setProperty(OutputKeys.INDENT, "no");
			// String exprS = "//archetype_details/archetype_id/value";
			XQueryService xqps = (XQueryService) col1.getService(
					"XPathQueryService", "1.0");
			xqps.setNamespace("ns", "http://schemas.openehr.org/v1");
			xqps.setNamespace("xsi",
					"http://www.w3.org/2001/XMLSchema-instance");

			// for(int i=0; i<queryt.length;i++)
			// System.out.println("Ta vrsta:" + queryt[i]);
			String queryt[] = objectQuery.split("/");
			objectQuery = "";
			int j = 0;
			if (queryt[1].equals(queryt[0]))
				j = 1;
			for (int i = j; i < queryt.length; i++)
				if (i != queryt.length - 1 && i != j)
					objectQuery = objectQuery + "/ns:" + queryt[i];
				else if (i != j)
					objectQuery = objectQuery + "/" + queryt[i];

			if (objectQuery.substring(0, 0) != objectQuery.substring(1, 1)) {
				objectQuery = "/" + objectQuery;
			}
			// System.out.println(query);
			CompiledExpression compiled = xqps.compile(objectQuery);
			// xqps.clearNamespaces();
			ResourceSet res1;
			
			try {
				res1 = xqps.execute(res, compiled);
			} catch (Exception e) {
				throw e;

			}

			return res1;

			// if(res == null) {
			// System.out.println("document not found!");
			// } else {
			// System.out.println(res.getContent());
			// }
		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}
	
	public String readLast(String userId,String patient_id, String archetype_id,
			String query) throws Exception {
		checkSecurityPolicy(userId,patient_id);
		// get patient from LDAP
		User ldapUsr = UserRegistryFactory.getUserFromLDAP(patient_id);
		if (ldapUsr == null)
			return null;

		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patient_id, ADMIN,
					PASSWORD);
			col2 = col1.getChildCollection(ldapUsr.getEmployeeType()
					.toDomainName());
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patient_id);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col.getName());
			col1 = col.getChildCollection(archetype_id);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col1.getName());
			String temp3[] = col1.listResources();
			// for(int i=0; i<temp3.length;i++)
			// System.out.println(temp3[i]);
			
			if (temp3 == null || temp3.length == 0) {
				System.out.println("Date doesn't exist! ");
				return null;
			}
			
			res = (XMLResource) col1.getResource(temp3[findMax(temp3)]);
			// System.out.println(date);
			if (res == null) {
				System.out.println("Date doesn't exist! ");
				return null;
			}
			// System.out.println("datum je " + res.getId());
			// col.setProperty(OutputKeys.INDENT, "no");
			// String exprS = "//archetype_details/archetype_id/value";
			XQueryService xqps = (XQueryService) col1.getService(
					"XPathQueryService", "1.0");
			xqps.setNamespace("ns", "http://schemas.openehr.org/v1");
			xqps.setNamespace("xsi",
					"http://www.w3.org/2001/XMLSchema-instance");

			// for(int i=0; i<queryt.length;i++)
			// System.out.println("Ta vrsta:" + queryt[i]);
			String queryt[] = query.split("/");
			query = "";
			int j = 0;
			if (queryt[1].equals(queryt[0]))
				j = 1;
			for (int i = j; i < queryt.length; i++)
				if (i != queryt.length - 1 && i != j)
					query = query + "/ns:" + queryt[i];
				else if (i != j)
					query = query + "/" + queryt[i];

			if (query.substring(0, 0) != query.substring(1, 1)) {
				query = "/" + query;
			}
			// System.out.println(query);
			CompiledExpression compiled = xqps.compile(query);
			// xqps.clearNamespaces();
			Resource res1;
			String temp = null;
			try {
				res1 = xqps.execute(res, compiled).getResource(0);
				temp = (String) (res1).getContent();
			} catch (Exception e) {
				System.out.println("Napacen query 'read Last'! " + query);

			}
			saveTrace(TraceEventType.READ.name(), system.name(), "readLast", temp, userId, patient_id);
			return temp;

			// if(res == null) {
			// System.out.println("document not found!");
			// } else {
			// System.out.println(res.getContent());
			// }
		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}

	/**
	 * Vrne timestamp (ime) zadnjega vpisanega XML-ja.
	 * 
	 * @param patient_id
	 * @param archetype_id
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public String readLastTimeStamp(String userId, String patient_id,
			String archetype_id) throws Exception {
		checkSecurityPolicy(userId,patient_id);
		// get patient from LDAP
		User ldapUsr = UserRegistryFactory.getUserFromLDAP(patient_id);
		if (ldapUsr == null)
			return null;

		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patient_id, ADMIN,
					PASSWORD);
			col2 = col1.getChildCollection(ldapUsr.getEmployeeType()
					.toDomainName());
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patient_id);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col.getName());
			col1 = col.getChildCollection(archetype_id);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col1.getName());
			String temp3[] = col1.listResources();
			// for(int i=0; i<temp3.length;i++)
			// System.out.println(temp3[i]);
			res = (XMLResource) col1.getResource(temp3[findMax(temp3)]);
			// System.out.println(date);
			if (res == null) {
				System.out.println("Date doesn't exist! ");
				return null;
			}
			return res.getDocumentId().substring(0,res.getDocumentId().indexOf("."));
						

		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Vrne timestamp (ime) prvega vpisanega XML-ja.
	 * 
	 * @param patient_id
	 * @param archetype_id
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public String readFirstTimeStamp(String userId, String patient_id,
			String archetype_id) throws Exception {
		checkSecurityPolicy(userId,patient_id);
		// get patient from LDAP
		User ldapUsr = UserRegistryFactory.getUserFromLDAP(patient_id);
		if (ldapUsr == null)
			return null;

		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patient_id, ADMIN,
					PASSWORD);
			col2 = col1.getChildCollection(ldapUsr.getEmployeeType()
					.toDomainName());
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patient_id);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col.getName());
			col1 = col.getChildCollection(archetype_id);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col1.getName());
			String temp3[] = col1.listResources();
			// for(int i=0; i<temp3.length;i++)
			// System.out.println(temp3[i]);
			res = (XMLResource) col1.getResource(temp3[findMin(temp3)]);
			// System.out.println(date);
			if (res == null) {
				System.out.println("Date doesn't exist! ");
				return null;
			}
			return res.getDocumentId().substring(0,res.getDocumentId().indexOf("."));

		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}

	/**
	 * Pogleda samo zadnji vpisani dokument
	 * */
	public String readByName(String userId, String patient_id, String resourceName,
			String archetype_id, String query) throws Exception {
		checkSecurityPolicy(userId,patient_id);
		// get patient from LDAP
		User ldapUsr = UserRegistryFactory.getUserFromLDAP(patient_id);
		if (ldapUsr == null)
			return null;

		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patient_id, ADMIN,
					PASSWORD);
			col2 = col1.getChildCollection(ldapUsr.getEmployeeType()
					.toDomainName());
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patient_id);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			col1 = col.getChildCollection(archetype_id);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			// String temp3[] =col1.listResources();
			// res = (XMLResource)col1.getResource(temp3[temp3.length-1]);
			res = (XMLResource) col1.getResource(resourceName);

			XQueryService xqps = (XQueryService) col1.getService(
					"XPathQueryService", "1.0");
			xqps.setNamespace("ns", "http://schemas.openehr.org/v1");
			xqps.setNamespace("xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			String queryt[] = query.split("/");
			query = "";
			int j = 0;
			if (queryt[1].equals(queryt[0]))
				j = 1;
			for (int i = j; i < queryt.length; i++)
				if (i != queryt.length - 1 && i != j)
					query = query + "/ns:" + queryt[i];
				else if (i != j)
					query = query + "/" + queryt[i];
			CompiledExpression compiled = xqps.compile(query);
			Resource res1;
			String temp = null;
			try {
				res1 = xqps.execute(res, compiled).getResource(0);
				temp = (String) (res1).getContent();
			} catch (Exception e) {
				System.out.println("Napacen query 'readByName'! " + query);

			}
			saveTrace(TraceEventType.READ.name(), system.name(), "readByName", temp, userId, patient_id);
			return temp;
		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}

	/**
	 * Metoda prebere zadnje meritve za vse paciente, ki to meritev imajo.
	 * @param userId id uporabnika aplikacije
	 * @param patientId glede na to, da gre za več pacientov, bomo dali kar userId tudi tu saj je pomembna samo domena
	 * @deprecated glej novo metodo, ki sprejema xquery ozr. xpath ampak ga zna dejansko ponucat
	 * */
	@Deprecated
	public String[][] readLastAll(String userId, String patientId,String archetype_id, String query,
			EMPLOYEE_TYPE_ENUM employeeType) throws Exception {
		checkSecurityPolicy(userId, patientId);
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		// String date=dateD.getTime()+".xml";
		Collection col = null;
		Collection col1 = null;
		Collection colMain = null;
		XMLResource res = null;
		Date date;
		Calendar cal;
		boolean exists = true;
		try {
			// get the collection
			colMain = DatabaseManager.getCollection(URI + PASSWORD, ADMIN,
					PASSWORD);
			colMain = colMain.getChildCollection(employeeType.toDomainName());
			if (colMain == null) {
				System.out.println("No data yet! ");
				return null;
			}
			String[] patients = colMain.listChildCollections();
			String[] resources;
			String[][] result = new String[patients.length][3];
			int tempPatients = 0;
			for (int i = 0; i < patients.length; i++) {
				result[i][0] = patients[i];
				// System.out.println(patients[i]);
			}

			String queryt[] = query.split("/");
			// for(int i=0; i<queryt.length;i++)
			// System.out.println("Ta vrsta:" + queryt[i]);
			query = "";
			int j = 0;
			if (queryt[1].equals(queryt[0])) {
				j = 1;
				query = query + "/";
			}
			for (int i = j; i < queryt.length; i++)
				if (i != queryt.length - 1 && i != j)
					query = query + "/ns:" + queryt[i];
				else if (i != j)
					query = query + "/" + queryt[i];

			for (int k = 0; k < patients.length; k++) {
				exists = true;
				col = colMain.getChildCollection(patients[k]);

				// System.out.println(col.getName());
				col1 = col.getChildCollection(archetype_id);
				if (col1 == null) {
					// System.out.println("Archetype with this id doesn't exist! ");
					exists = false;
				}
				if (exists) {
					// System.out.println(col1.getName());
					// String temp3[] =col1.listResources();
					// for(int i=0; i<temp3.length;i++)
					// System.out.println(temp3[i]);
					resources = col1.listResources();
					if (resources.length == 0) // user data found in DB, but
												// different archetype_id
						continue;
					res = (XMLResource) col1
							.getResource(resources[findMax(resources)]);
					// System.out.println(date);
					if (res == null) {
						System.out.println("Date doesn't exist! ");
						return null;
					}
					// System.out.println("datum je " + res.getId());
					XQueryService xqps = (XQueryService) col1.getService(
							"XPathQueryService", "1.0");
					xqps.setNamespace("ns", "http://schemas.openehr.org/v1");
					xqps.setNamespace("xsi",
							"http://www.w3.org/2001/XMLSchema-instance");

					// System.out.println(query);
					CompiledExpression compiled = xqps.compile(query);
					// xqps.clearNamespaces();
					Resource res1;
					String temp = null;
					try {
						res1 = xqps.execute(res, compiled).getResource(0);						
						if (res1 == null)
							continue;
							
						result[k][1] = (String) (res1).getContent();
						// date= new
						// Date(Long.valueOf(res.getId().split(".")[0]));
						// date.setTime(Long.valueOf(res.getId()));
						cal = Calendar.getInstance();
						// System.out.println(res.getId());
						String temp3 = res.getId().split("\\.")[0];
						// System.out.println(temp3);
						try {
							cal.setTimeInMillis(Long.valueOf(temp3));
							result[k][2] = cal.get(Calendar.YEAR) + "-"
									+ (cal.get(Calendar.MONTH) + 1) + "-"
									+ cal.get(Calendar.DAY_OF_MONTH) + " "
									+ cal.get(Calendar.HOUR_OF_DAY) + ":"
									+ cal.get(Calendar.MINUTE) + ":"
									+ cal.get(Calendar.SECOND);
						} catch (NumberFormatException e) {
							result[k][2] = null;
						}
						
						tempPatients++;
					} catch (Exception e) {
						System.out.println("Napacen query 'readLastAll'! "
								+ query);

					}
				}

			}
			String[][] result1 = new String[tempPatients][3];
			int l = 0;
			StringBuffer sb = new StringBuffer();
			// System.out.println(tempPatients);
			for (int i = 0; i < result.length; i++) {
				if (result[i][1] != null) {
					result1[l][0] = result[i][0];
					result1[l][1] = result[i][1];
					result1[l][2] = result[i][2];
					sb.append("*"+result1[l][0]+"-"+result1[l][1]+"-"+result1[l][2]+"*\n");
					l++;
				}
			}
			saveTrace(TraceEventType.READ.name(), system.name(), "readLastAll", sb.toString(), userId, patientId);
			return result1;

			// if(res == null) {
			// System.out.println("document not found!");
			// } else {
			// System.out.println(res.getContent());
			// }
		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}

	}

	/**
	 * Metoda prebere zadnje meritve za vse paciente, ki to meritev imajo.
	 * @author Mate Beštek 12.3.2012
	 * @param userId id uporabnika aplikacije
	 * @param patientId
	 * @param employeeType domena (diabetes | astma | shizofrenija | hujsanje)
	 * @param query xquery poizvedba
	 * @return rezultat poizvedbe je ena vrednost npr. int
	 * */
	public Object getSingleValue(String userId, String patientId, EMPLOYEE_TYPE_ENUM employeeType, String query) throws Exception {
		checkSecurityPolicy(userId, patientId);
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		Resource res = null;
		Collection col = null;
		try {
			// get the collection
			col = DatabaseManager.getCollection(URI + PASSWORD, ADMIN,
					PASSWORD);

			XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
			xqs.setProperty("indent", "yes");
			CompiledExpression compiled = xqs.compile(query);
			ResourceSet result = xqs.execute(compiled);
			ResourceIterator i = result.getIterator();
			res = i.nextResource();
			saveTrace(TraceEventType.READ.name(), system.name(), "getSingleValue", "QUERY:" + query + " RESPONSE:" + (res != null && res.getContent() != null ? res.getContent().toString() : "null"), userId, patientId);
			return res != null ? res.getContent() : null;
		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}

	}
	
	

	/**
	 * Returns list of values
	 * 
	 * @param userId
	 * @param patientId
	 * @param employeeType
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List getMultiValue(String userId, String patientId, EMPLOYEE_TYPE_ENUM employeeType, String query) throws Exception {
		checkSecurityPolicy(userId, patientId);
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		Resource res = null;
		List resList = new ArrayList();
		
		Collection col = null;
		try {
			// get the collection
			col = DatabaseManager.getCollection(URI + PASSWORD, ADMIN,
					PASSWORD);

			XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
			xqs.setProperty("indent", "yes");
			CompiledExpression compiled = xqs.compile(query);
			ResourceSet result = xqs.execute(compiled);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Object el = i.nextResource().getContent();
				resList.add(el);				
			}

			saveTrace(TraceEventType.READ.name(), system.name(), "getSingleValue", "QUERY:" + query + " RESPONSE:" + (res != null && res.getContent() != null ? res.getContent().toString() : "null"), userId, patientId);
			return resList;
		} finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}

	}	

	
	/**
	 * @param type
	 *            je lahko A, S ali D. Rabim zato da ne more nek zdravnik iz
	 *            druge klinične študije gledat rezultatov pacientov te
	 *            študije.¸ TODO: implementiraj logiko tako, da se upošteva
	 *            parameter type
	 * */
	public String[][] readTimespan(String userId, String patient_id, String type,
			String archetype_id, Date timestamp_start, Date timestamp_finish,
			String query) throws Exception {
		checkSecurityPolicy(userId,patient_id);
		// get patient from LDAP
		User ldapUsr = UserRegistryFactory.getUserFromLDAP(patient_id);
		if (ldapUsr == null)
			return null;

		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		// String timestamp_start_xml=timestamp_start.getTime()+".xml";
		// String timestamp_finish_xml=timestamp_finish.getTime()+".xml";
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		String temp[][] = null;
		String result[][] = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patient_id, ADMIN,
					PASSWORD);
			col2 = col1.getChildCollection(ldapUsr.getEmployeeType()
					.toDomainName());
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}

			col = col2.getChildCollection(patient_id);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col.getName());
			col1 = col.getChildCollection(archetype_id);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col1.getName());
			String temp3[] = col1.listResources();
			double temp2[] = new double[temp3.length];
			String str[] = null;
			for (int i = 0; i < temp2.length; i++) {
				// System.out.println("tralala: " + temp3[i]);
				str = temp3[i].split("\\.");
				temp2[i] = Double.parseDouble(str[0]);
			}

			XQueryService xqps = (XQueryService) col1.getService(
					"XPathQueryService", "1.0");
			xqps.setNamespace("ns", "http://schemas.openehr.org/v1");
			xqps.setNamespace("xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			String queryt[] = query.split("/");
			for (int i = 0; i < queryt.length; i++)
				// System.out.println("Ta vrsta:" + queryt[i]);
				query = "";
			int j = 0;
			if (queryt[1].equals(queryt[0])) {
				j = 1;
				query = query + "/";
			}
			for (int i = j; i < queryt.length; i++)
				if (i != queryt.length - 1 && i != j)
					query = query + "/ns:" + queryt[i];
				else if (i != j)
					query = query + "/" + queryt[i];
			// System.out.println(query);
			CompiledExpression compiled;
			try {
				compiled = xqps.compile(query);
			} catch (Exception e) {
				System.out.println("Napacen query 'readTimespan'!" + query);
				return null;
			}
			// xqps.clearNamespaces();
			// int size=0;
			int l = 0;
			temp = new String[temp3.length][2];
			for (int i = 0; i < temp3.length; i++) {
				if ((temp2[i] >= timestamp_start.getTime())
						&& (temp2[i] <= timestamp_finish.getTime())) {
					// System.out.println("risorsi: " + temp3[i]);
					try {
						ResourceSet rs = xqps.execute(((XMLResource) col1.getResource(temp3[i])), compiled);
						if (rs != null && rs.getResource(0) != null && rs.getResource(0).getContent() != null) 
							temp[l][1] = (String) rs.getResource(0).getContent();
						
						temp[l][0] = Double.toString(temp2[i]);
						l++;
					} catch (Exception e) {
						System.out.println("Nobene vrednosti!" + temp3[i]);
						e.printStackTrace();
						return null;
					}

					// System.out.println(" " + temp[i]);
				}
			}

			if (l == 0)
				return null;
			result = new String[l][2];
			// int k=0;
			// int i=0;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < l; i++) {
				result[i][0] = temp[i][0];
				result[i][1] = temp[i][1];
				sb.append("*"+result[i][0]+"-"+result[i][1]+"*\n");

			}
			saveTrace(TraceEventType.READ.name(), system.name(), "readTimespan", sb.toString(), userId, patient_id);
			return result;
		}

		finally {
			// dont forget to clean up!

			if (res != null) {
				// try { (res).; } catch(XMLDBException xe)
				// {xe.printStackTrace();}
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}

	public void addXML(String userId, String patient_id, String rmXml, String fileName, String domain)
			throws XMLDBException, SecurityPolicyViolationException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		checkSecurityPolicy(userId,patient_id);
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		String temp[] = fileName.split("_");
		String bid = "";
		for (int i = 0; i < temp.length - 1; i++) {
			bid = bid + temp[i];
			if (i != temp.length - 2)
				bid = bid + "_";
		}
		String time = temp[temp.length - 1];
		Collection col = null;
		Collection col1 = null;
		Collection colFinal = null;
		XMLResource res = null;
		// XMLResource tempXML = (XMLResource)rmXml;

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(rmXml));

		Document doc = builder.parse(is);

		// System.out.print(rmXml);

		XPath xpath = XPathFactory.newInstance().newXPath();
		// XPathExpression expr =
		// xpath.compile("//items[@archetype_node_id='openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1']/name/value");
		// String exprS = "//*[local-name() = 'name']/value";
		String exprLevelContent = "//@archetype_node_id";
		String exprLevelItems = "//ns1:archetype_id[1]/ns1:value/text()";
		// String exprS =
		// "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1']/name/value";
		// String nsContext = getNamespaceURI("http://schemas.openehr.org/v1");
		NamespaceContext nsContext = new NamespaceContext() {
			@Override
			public String getNamespaceURI(String prefix) {
				String uri;
				if (prefix.equals("ns1"))
					uri = "http://schemas.openehr.org/v1";
				else if (prefix.equals("ns2"))
					uri = "http://www.w3.org/2001/XMLSchema-instance";
				else
					uri = null;
				return uri;

			}

			@Override
			public Iterator getPrefixes(String val) {
				return null;
			}

			// Dummy implemenation - not used!
			@Override
			public String getPrefix(String uri) {
				return null;
			}
		};
		xpath.setNamespaceContext(nsContext);
		// xpath.setNamespaceContext("http://schemas.openehr.org/v1");

		String archName = (String) xpath.evaluate(exprLevelContent, doc,
				XPathConstants.STRING);
		String template = archName;

		NodeList nodes = (NodeList) xpath.evaluate(exprLevelItems, doc,
				XPathConstants.NODESET);
		if (template == null || template.trim().length() == 0) {
			for (int i = 0; i < nodes.getLength(); i++) {
				template = (nodes.item(0).getNodeValue()); // Mislim da je
															// problem sedaj
															// resen
															// pogleda se torej
															// vedno prvi
															// archetype_node_id
															// kot ime nase
															// kolekcije

				template = (nodes.item(0).getNodeValue());
			}
		}
		// System.out.println("je:" + template);
		try {
			col1 = getOrCreateCollection(domain);
			// create new XMLResource; an id will be assigned to the new
			// resource
			XmldbURI templateURI = XmldbURI.create(bid);
			if (col1.getChildCollection(domain) == null) {
				CollectionManagementServiceImpl mgt = (CollectionManagementServiceImpl) col1
						.getService("CollectionManagementService", "1.0");
				mgt.createCollection(XmldbURI.create(domain));

			}
			col = col1.getChildCollection(domain);
			if (col.getChildCollection(bid) == null) {
				CollectionManagementServiceImpl mgt = (CollectionManagementServiceImpl) col
						.getService("CollectionManagementService", "1.0");
				mgt.createCollection(templateURI);

			}
			col1 = col.getChildCollection(bid);
			templateURI = XmldbURI.create(template);
			if (col1.getChildCollection(template) == null) {
				CollectionManagementServiceImpl mgt = (CollectionManagementServiceImpl) col1
						.getService("CollectionManagementService", "1.0");
				mgt.createCollection(templateURI);

			}
			col = col1.getChildCollection(template);
			res = (XMLResource) col.createResource(time, "XMLResource");
			// File f = new File(fajl);
			/*
			 * if(!f.canRead()) { System.out.println("cannot read file " +
			 * fajl); return; }
			 */
			res.setContent(rmXml);
			// res.setContent(f);
			// System.out.print("parent " + col.getName() + "\n");
			// System.out.print("child " + col1.getName() + "\n");
			// System.out.print(col.getChildCollection(template).getName() +
			// "\n");
			// System.out.print(col1.getParentCollection().getName() + " " +
			// col1.getResourceCount() + "\n");
			System.out.println("storing document " + res.getId() + "...");
			col.storeResource(res);
			saveTrace(TraceEventType.WRITE.name(), system.name(), "addXML", rmXml, userId, patient_id);
			// System.out.println("ok." + "\n" + template);
		} finally {
			// dont forget to cleanup
			// if(res != null) {
			// try { ((EXistResource)res)); } catch(XMLDBException xe)
			// {xe.printStackTrace();}
			// }

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
			if (col1 != null) {
				try {
					col1.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}

	private Collection getOrCreateCollection(String collectionUri)
			throws XMLDBException {
		return getOrCreateCollection(collectionUri, 0);
	}

	private Collection getOrCreateCollection(String collectionUri,
			int pathSegmentOffset) throws XMLDBException {

		Collection col = DatabaseManager.getCollection(URI + collectionUri,
				ADMIN, PASSWORD);
		if (col == null) {
			if (collectionUri.startsWith("/")) {
				collectionUri = collectionUri.substring(1);
			}

			String pathSegments[] = collectionUri.split("/");
			if (pathSegments.length > 0) {

				StringBuilder path = new StringBuilder();
				for (int i = 0; i <= pathSegmentOffset; i++) {
					path.append("/" + pathSegments[i]);
				}

				Collection start = DatabaseManager.getCollection(URI + path,
						ADMIN, PASSWORD);
				if (start == null) {
					// collection does not exist, so create
					String parentPath = path
							.substring(0, path.lastIndexOf("/"));
					Collection parent = DatabaseManager.getCollection(URI
							+ parentPath, ADMIN, PASSWORD);
					CollectionManagementService mgt = (CollectionManagementService) parent
							.getService("CollectionManagementService", "1.0");
					col = mgt.createCollection(pathSegments[pathSegmentOffset]);
					col.close();
					parent.close();
				} else {
					start.close();
				}
			}
			return getOrCreateCollection(collectionUri, ++pathSegmentOffset);
		} else {
			return col;
		}
	}

	public static int findMax(String[] table) {
		long max = 0;
		int index = 0;
		long temp;
		for (int i = 0; i < table.length; i++) {
			if(table[i] == null) continue;
			try {
				temp = Long.valueOf(table[i].split("\\.")[0]);
			} catch (NumberFormatException e) {
				temp = 0;
			}
			// System.out.println(temp);
			if (temp > max) {
				max = temp;
				index = i;
			}
		}
		// System.out.println(max);
		return index;
	}
	
	
	public static int findMin(String[] table) {
		long min = Long.MAX_VALUE;
		int index = 0;
		long temp;
		for (int i = 0; i < table.length; i++) {
			try {
				temp = Long.valueOf(table[i].split("\\.")[0]);
			} catch (NumberFormatException e) {
				temp = 0;
			}
			// System.out.println(temp);
			if (temp < min) {
				min = temp;
				index = i;
			}
		}
		// System.out.println(max);
		return index;
	}
	
	/**
	 *  Returns index of second biggest filename (timestamp). 
	 *
	 * 
	 * @param table
	 * @return
	 */
	public static int findSecondMax(String[] table) {
		long secondMax = 0;
		long max = 0;
		int indexMax = 0;
		int indexSecondMax = 0;
		long temp;
		for (int i = 0; i < table.length; i++) {
			try {
				temp = Long.valueOf(table[i].split("\\.")[0]);
			} catch (NumberFormatException e) {
				temp = 0;
			}
			
			// System.out.println(temp);
			if (temp > secondMax && temp < max) {
				//second max element
				indexSecondMax = i;				
				secondMax = temp;
			}
			else if (temp > max) {
				//second max element
				indexSecondMax = indexMax;
				
				//max element
				secondMax = max;
				max = temp;
				indexMax = i;				
			}
		}
		// System.out.println(max);
		return indexSecondMax;
	}

	/**
	 * Prebere XML datoteko iz eXist-a.
	 * 
	 * @param path
	 *            first level on eXist path
	 * @param fileName
	 *            file name to read
	 * @return
	 * @throws Exception
	 */
	public String readWholeXmlByPath(String userId,String patientId,String path, String fileName)
			throws Exception {
		checkSecurityPolicy(userId,patientId);
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		String xml = null;
		try {
			// get the collection
			Collection col = DatabaseManager.getCollection(URI + PASSWORD,
					ADMIN, PASSWORD);
			col = col.getChildCollection(path);
			if (col == null) {
				System.out.println("No data!");
				return null;
			}

			// String tempResList[] = col.listResources();
			// for(int i=0; i<temp3.length;i++)
			// System.out.println(temp3[i]);

			XMLResource res = (XMLResource) col.getResource(fileName);

			StringWriter writer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(new DOMSource(res.getContentAsDOM()),
					new StreamResult(writer));
			xml = writer.toString();
			saveTrace(TraceEventType.READ.name(), system.name(), "readWholeXmlByPath", xml, userId, patientId);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}		
		return xml;
	}
	
	
	/**
	 * Returns last writen XML on path: domain/patientId/templateId/lastXML.xml 
	 * 
	 * @param domain      e.g. eAstma, eDiabetes or eShizofrenija
	 * @param patientId   e.g. pacient.as
	 * @param templateId  e.g. openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1 
	 * @return
	 * @throws Exception
	 */
	public String readLastWholeXml(String userId, String domain, String patientId, String templateId)
			throws Exception {
		checkSecurityPolicy(userId,patientId);
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		String xml = null;
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patientId, ADMIN, PASSWORD);
			col2 = col1.getChildCollection(domain);
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patientId);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			col1 = col.getChildCollection(templateId);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			String temp3[] = col1.listResources();
			res = (XMLResource) col1.getResource(temp3[findMax(temp3)]);
			if (res == null) {
				System.out.println("Date doesn't exist! ");
				return null;
			}


			//StringWriter writer = new StringWriter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(new DOMSource(res.getContentAsDOM()),
					new StreamResult(baos));
			//xml = writer.toString();
			//writer.flush();			
			xml = new String(baos.toByteArray(),"UTF-8");
			saveTrace(TraceEventType.READ.name(), system.name(), "readLastWholeXml", xml, userId, patientId);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}

		return xml;
	}
	
	/**
	 * Returns last writen XML on path: domain/patientId/templateId/<fileName>
	 * 
	 * @param domain      e.g. eAstma, eDiabetes or eShizofrenija
	 * @param patientId   e.g. pacient.as
	 * @param fileName	  e.g. <uid>_<timestamp>.xml
	 * @param templateId  e.g. openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1 
	 * @return
	 * @throws Exception
	 */
	public String readWholeXml(String userId, String domain, String patientId, String fileName, String templateId)
			throws Exception {
		checkSecurityPolicy(userId,patientId);
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		String xml = null;
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patientId, ADMIN, PASSWORD);
			col2 = col1.getChildCollection(domain);
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patientId);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			col1 = col.getChildCollection(templateId);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			String temp3[] = col1.listResources();
			if(temp3 != null){
				for(int i = 0; i < temp3.length; i++){
					if(fileName.contains(temp3[i].trim())){
						res = (XMLResource) col1.getResource(temp3[i]);
						break;
					}
				}
			}
			
//			res = (XMLResource) col1.getResource(temp3[findMax(temp3)]);
//			if (res == null) {
//				System.out.println("Date doesn't exist! ");
//				return null;
//			}


			//StringWriter writer = new StringWriter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(new DOMSource(res.getContentAsDOM()),
					new StreamResult(baos));
			//xml = writer.toString();
			//writer.flush();			
			xml = new String(baos.toByteArray(),"UTF-8");
			saveTrace(TraceEventType.READ.name(), system.name(), "readWholeXml", xml, userId, patientId);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}

		return xml;
	}
	
	/**
	 * Returns an array of strings which represent the whole contents of all
	 * XML-s on path: domain/patientId/templateId/
	 * 
	 * @param domain      e.g. eAstma, eDiabetes or eShizofrenija
	 * @param patientId   e.g. pacient.as
	 * @param templateId  e.g. openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1
	 * @param maxNumOfXmls the number of last xml-s to read. If 0, reads all xml-s, if 1 reads just last xml, if 2 reads last two xml-s, etc.
	 */
	public String[] readAllXmls(String userId, String domain, String patientId, String templateId, int maxNumOfXmls) throws Exception {
		
		String[] result = null;
		
		checkSecurityPolicy(userId,patientId);
		
		// initialize database driver
		final String driver = "org.exist.xmldb.DatabaseImpl";
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		String xml = null;
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patientId, ADMIN, PASSWORD);
			col2 = col1.getChildCollection(domain);
			if (col2 == null) {
				System.out.println("[archetype-api] readAllXmls: No data yet.");
				return null;
			}
			col = col2.getChildCollection(patientId);
			if (col == null) {
				System.out.println("[archetype-api] readAllXmls: Patient with this id doesn't exist.");
				return null;
			}
			col1 = col.getChildCollection(templateId);
			if (col1 == null) {
				System.out.println("[archetype-api] readAllXmls: Archetype with this id doesn't exist.");
				return null;
			}
			String temp3[] = col1.listResources();
			
			int numOfXmlsToRead = temp3.length;
			if(maxNumOfXmls > 0 && maxNumOfXmls < numOfXmlsToRead) numOfXmlsToRead = maxNumOfXmls;
			
			result = new String[numOfXmlsToRead];
			
			for(int iter_idx = 0; iter_idx < numOfXmlsToRead; iter_idx++) {
				
				// find max
				int xml_idx = findMax(temp3);
				
				// Get xml name
				String xml_id = temp3[xml_idx];
				
				// Flag xml as already read
				temp3[xml_idx] = null;
				
				res = (XMLResource) col1.getResource(xml_id);
				if (res == null) {
					System.out.println("[archetype-api] readAllXmls: Napaka pri branju arhetipa " + xml_id);
					result[iter_idx] = null;
					continue;
				}
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.transform(new DOMSource(res.getContentAsDOM()), new StreamResult(baos));
		
				xml = new String(baos.toByteArray(),"UTF-8");
				result[iter_idx] = xml + "<!-- xml=\"" + xml_id + "\" -->";
				saveTrace(TraceEventType.READ.name(), system.name(), "readAllXmls", xml, userId, patientId);
				
			}

		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}
		
		return result;
		
	}
	
	/**
	 * Returns an array of strings which represent the whole contents of all
	 * XML-s on path: domain/patientId/templateId/
	 * 
	 * @param domain      e.g. eAstma, eDiabetes or eShizofrenija
	 * @param patientId   e.g. pacient.as
	 * @param templateId  e.g. openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1
	 */
	public String[] readAllXmls(String userId, String domain, String patientId, String templateId) throws Exception {
		return readAllXmls(userId, domain, patientId, templateId, 0);
	}
	
	/**
	 * Gets all usernames
	 * 
	 * @param domain      e.g. eAstma, eDiabetes or eShizofrenija
	 */
	public String[] getAllUsernames(String userId, String domain) throws Exception {

		String[] result = null;
		
		// initialize database driver
		final String driver = "org.exist.xmldb.DatabaseImpl";
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		String xml = null;
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		
		try {
			col = DatabaseManager.getCollection(URI + PASSWORD, ADMIN, PASSWORD).getChildCollection(domain);
			result = col.listChildCollections();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return result;
		
	}
	
	/**
	 * 
	 * 
	 * @param patient_id    e.g. pacient.as
	 * @param employeeType  e.g. EMPLOYEE_TYPE_ENUM.DIABETES
	 * @param archetype_id  e.g. openEHR-EHR-SECTION.opomnik_eo_di.v1
	 * @param query         e.g. //content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_di.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()
	 * @return              [70.0, 71.0]
	 * @throws Exception
	 * 
	 */
	public String[] readLastTwo(String userId,String patient_id, EMPLOYEE_TYPE_ENUM employeeType, String archetype_id, String query) throws Exception {
		checkSecurityPolicy(userId,patient_id);
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		Collection col = null;
		Collection col1 = null;
		Collection col2 = null;
		XMLResource res = null;
		XMLResource resSecondMax = null;
		
		try {
			// get the collection
			col1 = DatabaseManager.getCollection(URI + patient_id, ADMIN,
					PASSWORD);
			col2 = col1.getChildCollection(employeeType.toDomainName());
			if (col2 == null) {
				System.out.println("No data yet! ");
				return null;
			}
			col = col2.getChildCollection(patient_id);
			if (col == null) {
				System.out.println("Patient with this id doesn't exist! ");
				return null;
			}
			// System.out.println(col.getName());
			col1 = col.getChildCollection(archetype_id);
			if (col1 == null) {
				System.out.println("Archetype with this id doesn't exist! ");
				return null;
			}
			String temp3[] = col1.listResources();
			res = (XMLResource) col1.getResource(temp3[findMax(temp3)]);
			
			//not enough elements to return
			if (temp3 == null || temp3.length < 2)
				return null;
			
			resSecondMax = (XMLResource) col1.getResource(temp3[findSecondMax(temp3)]);

			if (res == null || resSecondMax == null) {
				System.out.println("Date doesn't exist! ");
				return null;
			}
			XQueryService xqps = (XQueryService) col1.getService("XPathQueryService", "1.0");
			xqps.setNamespace("ns", "http://schemas.openehr.org/v1");
			xqps.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

			String queryt[] = query.split("/");
			query = "";
			int j = 0;
			if (queryt[1].equals(queryt[0]))
				j = 1;
			for (int i = j; i < queryt.length; i++)
				if (i != queryt.length - 1 && i != j)
					query = query + "/ns:" + queryt[i];
				else if (i != j)
					query = query + "/" + queryt[i];

			if (query.substring(0, 0) != query.substring(1, 1)) {
				query = "/" + query;
			}
			// System.out.println(query);
			CompiledExpression compiled = xqps.compile(query);
			// xqps.clearNamespaces();
			Resource res1;
			Resource res2;
			String[] temp = new String[2];
			try {
				res1 = xqps.execute(res, compiled).getResource(0);
				res2 = xqps.execute(resSecondMax, compiled).getResource(0);
				temp[0] = (String) (res1).getContent();
				temp[1] = (String) (res2).getContent();
			} catch (Exception e) {
				System.out.println("Napacen query 'read Last'! " + query);

			}

			return temp;
		} finally {
			// dont forget to clean up!

			if (res != null) {
			}

			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @param appUserId sestra ali zdravnik lahko vnašata obiske
	 * @param appUserRole vloga CAREMANAGER ali DOCTOR
	 * @param patientId id pacienta
	 * @param visitFileName ime xml datoteke, ki je v eXist shranjena za tale obisk - tam se torej ne vidi da gre za nov obisk
	 * */
	public void addNewVisit(String appUserId, String appUserRole,String patientId, String visitFileName, String label, String domain){
		Connection con = null;
		Statement statement = null;
		PreparedStatement insertStatement = null;
		BufferedInputStream bis = null;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(
					"jdbc:postgresql://"+SystemConstant.EXIST_HOST_NAME.toString()+":5432/ehr", "ehr", "ehr");
			con.setAutoCommit(true);

			insertStatement = con.prepareStatement("insert into \"visit\" (\"app_user_id\", \"app_user_role\", \"patient_id\", \"visit_file_name\",\"timestamp\",\"label\",\"domain\") values (?,?,?,?,?,?,?)");
			insertStatement.setString(1, appUserId);
			insertStatement.setString(2, appUserRole);						
			insertStatement.setString(3, patientId);
			insertStatement.setString(4, visitFileName);
			insertStatement.setString(5, new SimpleDateFormat("dd.MM.yyyy:HH.mm.ss").format(new Date()));
			insertStatement.setString(6, label);
			insertStatement.setString(7, domain);
			insertStatement.executeUpdate();
		} catch (Exception e){
			log.error("Napaka:"+e.getMessage(),e);
			e.printStackTrace();
		}
		finally {
			try{	
				if(bis != null){
					
					bis.close();
				}
				if (statement != null){
					statement.close();
				}					
				if (insertStatement != null){
					insertStatement.close();
				}					
				if (con != null){
					con.close();
				}										
					
			} catch(Exception e){
				log.error("Napaka2:"+e.getMessage(),e);
				e.printStackTrace();
			}
		}
	}
	
	private void saveTrace(final String eventType,final String system,final String methodName, final String data, final String userId, final String patientId){
		Connection con = null;
		Statement statement = null;
		PreparedStatement insertStatement = null;
		BufferedInputStream bis = null;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(
					"jdbc:postgresql://"+SystemConstant.EXIST_HOST_NAME.toString()+":5432/monitoring", "monitor", "monitor");
			con.setAutoCommit(true);

			insertStatement = con.prepareStatement("insert into \"EVENTS\" (\"type\", \"system\", \"timestamp\", method, data, \"applicationUser\", \"accessedUser\") values (?,?,?,?,?,?,? )");
			insertStatement.setString(1, eventType);
			insertStatement.setString(2, system);			
			insertStatement.setString(3, new SimpleDateFormat("dd.MM.yyyy:HH.mm.ss").format(new Date()));
			insertStatement.setString(4, methodName);
			
			byte[] bites = null;
			if (data != null)
				bites = data.getBytes("UTF-8");
			else
				bites = "".getBytes("UTF-8");
				 
			bis = new BufferedInputStream(new ByteArrayInputStream(bites));
			insertStatement.setBinaryStream(5, bis, bites.length);
			insertStatement.setString(6, userId);
			insertStatement.setString(7, patientId);
			insertStatement.executeUpdate();
		} catch (Exception e){
			log.error("Napaka:"+e.getMessage(),e);
			e.printStackTrace();
		}
		finally {
			try{	
				if(bis != null){
					
					bis.close();
				}
				if (statement != null){
					statement.close();
				}					
				if (insertStatement != null){
					insertStatement.close();
				}					
				if (con != null){
					con.close();
				}										
					
			} catch(Exception e){
				log.error("Napaka2:"+e.getMessage(),e);
				e.printStackTrace();
			}
		}

	}

}
