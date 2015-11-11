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
package si.eoskrba.xmlbeans;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.NotImplementedException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.conf.SystemConstant;


public class MedsXmlReader {
	
	private static Document document = null;
	
	public static final String LEVEL_5_EL = "level5";
	public static final String SUBSTANCE_EL = "substance";
	public static final String NAME_EL = "name";
	public static final String MANUFACTURER_EL = "manufacturer";
	
	private static Node nodeFromId = null; //store Node in this variable - if found in function 'find'
	private static List<MedsFNode> medsList = null; //stores all final meds nodes

	public static List<MedsFNode> getMedicineList(String userId, String patientId, EMPLOYEE_TYPE_ENUM employeeType) {
		return getMedicineList(userId, patientId, employeeType, null);
	}
	
	public static List<MedsFNode> getMedicineList(String userId, String patientId, EMPLOYEE_TYPE_ENUM employeeType, String levelId) {
		if (medsList == null)
			loadXML(userId, patientId, employeeType, levelId);
		return medsList;
	}
	
	public static void reloadMedicineList() {
		medsList = null;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private static boolean loadXML(String userId, String patientId, EMPLOYEE_TYPE_ENUM employeeType, String levelId) {
		
		try {
			
			//read file from eXist
			String medsXmlName = "";
			String medsXsdName = "";
			switch (employeeType) {
				case ASTMA :
					throw new NotImplementedException();
				case DIABETES :
					medsXmlName = SystemConstant.EXIST_MEDS_XML_NAME_DI.toString();
					medsXsdName = SystemConstant.EXIST_MEDS_XSD_NAME_DI.toString();
					break;
				case SHIZOFRENIJA :
					throw new NotImplementedException();
				default :
					break;
			}
			String xml = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readWholeXmlByPath(userId, patientId, SystemConstant.EXIST_MEDS_PATH.toString(), medsXmlName);
			
			//validate
			validateXML(userId, patientId, xml, medsXsdName);
			
			//parse
			document = parseXML(xml);
			
			//fill elements to list
			getLevel5Nodes(levelId);
						
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	private static Document parseXML(String xml) {
		
		Document doc = null;
		 
		// Create a factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			// Use document builder factory
			DocumentBuilder builder = factory.newDocumentBuilder();			
			
			//Parse the document
			Reader reader=new CharArrayReader(xml.toCharArray());
			doc = builder.parse(new org.xml.sax.InputSource(reader));
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		
		return doc;
	}
	
	private static boolean validateXML(String userId, String patientId, String xml, String medsXsdName) {
		
		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        


        Schema schema;
		try {
	        // 2. Compile the schema. 
	        // Here the schema is loaded from a java.io.File, but you could use 
	        // a java.net.URL or a javax.xml.transform.Source instead.
	        //URL schemaLocation = MedsXmlReader.class.getResource("meds.xsd");
	        String xsd = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readWholeXmlByPath(userId, patientId,SystemConstant.EXIST_MEDS_PATH.toString(), medsXsdName);
	        Source xsdSrc = new StreamSource(new java.io.StringReader(xsd));
	        
			schema = factory.newSchema(xsdSrc);
			
	        // 3. Get a validator from the schema.
	        Validator validator = schema.newValidator();
	        
	        // 4. Parse the document you want to check.
	        Source source = new StreamSource(new java.io.StringReader(xml)); 
	        
	        // 5. Check the document
			validator.validate(source);
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	
	/**
	 * This method visits all the nodes in a DOM tree and returns node with passes level id.
	 * 
	 * @param node
	 * @param levelId
	 */
	private static void find(Node node, String levelId) {
	    // Process node

	    // If there are any children, visit each one
	    NodeList list = node.getChildNodes();
	    if (node.getNodeValue() != null && node.getNodeValue().equals(levelId)) {
	    	nodeFromId = node;
	    	return;
	    }
	    
	    for (int i=0; i<list.getLength(); i++) {
	        // Get child node
	        Node childNode = list.item(i);
	        
	        // Visit child node
	        find(childNode, levelId);
	    }
	    return;
	}	
	
	/**
	 * Find all subnodes of given 'node' and append them to list.
	 * 
	 * @param node
	 */
	private static void visitAndAddToList(Node node) {
	    // Process node

	    // If there are any children, visit each one
	    NodeList list = node.getChildNodes();
	    if (node.getNodeName().equals(LEVEL_5_EL)) {
	    	medsList = addNodeToList(node, medsList);
	    }
	    
	    for (int i=0; i<list.getLength(); i++) {
	        // Get child node
	        Node childNode = list.item(i);
	        
	        // Visit child node
	        visitAndAddToList(childNode);
	    }
	    return;
	}	
	
	
	
	/**
	 * Create new object for medicine and map data from XML DOM object.
	 * 
	 * @param node
	 * @param list
	 * @return
	 */
	private static List<MedsFNode> addNodeToList(Node node, List<MedsFNode> list) {
		NodeList children = node.getChildNodes();
		MedsFNode medsNode = null;
		
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals(SUBSTANCE_EL)) {
				if (medsNode == null)
					medsNode = new MedsFNode();				
				medsNode.setSubstance(child.getChildNodes() != null && child.getChildNodes().getLength() > 0 ? child.getChildNodes().item(0).getNodeValue() : null);
			} 
			else if (child.getNodeName().equals(NAME_EL)) {
				if (medsNode == null)
					medsNode = new MedsFNode();
				medsNode.setName(child.getChildNodes() != null && child.getChildNodes().getLength() > 0 ? child.getChildNodes().item(0).getNodeValue() : null);
			}
			else if (child.getNodeName().equals(MANUFACTURER_EL)) {
				if (medsNode == null)
					medsNode = new MedsFNode();				
				medsNode.setManufacturer(child.getChildNodes() != null && child.getChildNodes().getLength() > 0 ? child.getChildNodes().item(0).getNodeValue() : null);
			}
		}
		if (list == null)
			list = new ArrayList();
		if (medsNode != null)
			list.add(medsNode);
		
		return list;
	}
	
	

	/**
	 * Returns all elements with same parent (levelId)
	 * 
	 * @param   inputIdLevel id of element from 1 to 4
	 * @return  return list of level5 elements which corresponds to given id
	 * @throws LevelNotSupportedException
	 */
	private static List<MedsFNode> getLevel5Nodes(String levelId) {
		
		nodeFromId = null;
		if (levelId != null)
			find(document.getFirstChild(), levelId);
		else
			nodeFromId = document.getFirstChild().getFirstChild(); //return all level 5 nodes
		
		if (nodeFromId == null)
			return null;
				
		medsList = null;
		visitAndAddToList(nodeFromId.getParentNode().getParentNode());
		
		return medsList;
	}

	/**
	 * Looks for text in array 'dropdownPaths' and inserts dropdown menu (html options element) 
	 * 
	 * @param form           whole html element
	 * @param dropdownPaths  array of strings ("<select name ...>")  where dropdown menu needs to be inserted
	 * @return
	 */
	public static String insertDropdown(String userId, String patientId, EMPLOYEE_TYPE_ENUM employeeType, String form, ArrayList dropdownPaths) {
		
		getMedicineList(userId, patientId, employeeType);
		StringBuffer newForm = new StringBuffer(form);
		for (Object segmentObj : dropdownPaths) {
			String segment = (String) segmentObj;
			
			StringBuffer sbPartStart = new StringBuffer(newForm.substring(0, newForm.indexOf(segment) + segment.length()));
			StringBuffer sbPartEnd = new StringBuffer(newForm.substring(newForm.indexOf(segment) + segment.length()));
			newForm = new StringBuffer("");//TODO: ali je tole prav? s tem dejansko uničiš prvotno formo. Rezultat bo torej samo select stavek in option-i!!!
			newForm.append(sbPartStart);
			
			for (MedsFNode medicine : medsList) {
				
				newForm.append("<option value=\"");
				newForm.append(medicine.getName());
				newForm.append("\">");
				newForm.append(medicine.getName());
				newForm.append("</option>");
			}
			
			newForm.append(sbPartEnd);
		}
		return newForm.toString();
	}
}
