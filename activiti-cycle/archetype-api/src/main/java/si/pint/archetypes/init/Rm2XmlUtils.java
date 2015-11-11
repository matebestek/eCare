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
package si.pint.archetypes.init;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.binding.XMLBinding;
import org.openehr.binding.XMLBindingException;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.content.ContentItem;
import org.openehr.rm.composition.content.entry.Activity;
import org.openehr.rm.composition.content.entry.Evaluation;
import org.openehr.rm.composition.content.entry.Instruction;
import org.openehr.rm.composition.content.entry.Observation;
import org.openehr.rm.composition.content.navigation.Section;
import org.openehr.rm.datastructure.history.History;
import org.openehr.rm.datastructure.history.PointEvent;
import org.openehr.rm.datastructure.itemstructure.ItemSingle;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.datastructure.itemstructure.ItemTree;
import org.openehr.rm.datastructure.itemstructure.representation.Cluster;
import org.openehr.rm.datastructure.itemstructure.representation.Item;
import org.openehr.rm.datatypes.basic.DataValue;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.schemas.v1.ACTION;
import org.openehr.schemas.v1.ACTIVITY;
import org.openehr.schemas.v1.COMPOSITION;
import org.openehr.schemas.v1.CompositionDocument;
import org.openehr.schemas.v1.EVALUATION;
import org.openehr.schemas.v1.INSTRUCTION;
import org.openehr.schemas.v1.ITEMTREE;
import org.openehr.schemas.v1.ItemsDocument;
import org.openehr.schemas.v1.OBSERVATION;
import org.openehr.schemas.v1.SECTION;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import si.pint.archetypes.exceptions.EmptyParametersException;


/**
 * Utility methods for working with templates and conversion from RM to XML and back.
 * 
 * @author Blaz
 *
 */
public class Rm2XmlUtils {
	
	//encoding
	private static final String ENCODING = "UTF-8";	

	//RM object found in recursion method 'findRmByNode' 
	private static Object rmRootObject = null;
	
	//List of all template paths
	private static List<String> templatePaths;
	
	/**
	 * Creates new Xml object with parameter values.
	 * 
	 * @param rootElementName      
	 * @param rootXsiType
	 * @param rootArchetypeNodeId
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document createNewXmlDocument(String rootElementName, String rootXsiType, String rootArchetypeNodeId) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		Element root = document.createElement(rootElementName);
		root.setAttribute("xmlns", "http://schemas.openehr.org/v1");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		if (rootXsiType != null && !rootXsiType.equals(""))
			root.setAttribute("xsi:type", rootXsiType);
		if (rootArchetypeNodeId != null && !rootArchetypeNodeId.equals(""))
			root.setAttribute("archetype_node_id", rootArchetypeNodeId);
		
		document.appendChild(root);
		
		//init object before populating tree
		rmRootObject = null;
		return document;
	}
	
	
	/**
	 * Populate XML object model. 
	 * 
	 * @param nodeList        children nodes
	 * @param currentNode     parent node
	 * @param rmRootObjPass   RM (root) object - e.g. Section, Composition, ItemTree, ...
	 */
	public static void populateXml(NodeList nodeList, Node currentNode, Object rmRootObjPass) {
		if (nodeList == null || currentNode == null) 
			return;
	
    	for (int j = 0; nodeList != null && j < nodeList.getLength(); j++) {
    		Node child = nodeList.item(j);
    		
    		Object refItem = null; //child's corresponding RM (data) object 
    		String nodeAtt = null; //child's attribute 'xsi:type' value
    		
    		//TODO: preveri ce tale if sploh rabim
    		if (child.getNodeName().equals("#text")) {
    			Text textValue = currentNode.getOwnerDocument().createTextNode(child.getNodeValue());
    			currentNode.appendChild(textValue);
    			
    			if (refItem == null) {}
//    			else if (refItem instanceof DvText) {
//    				nodeAtt = "DvTEXT";
//    			}
//    			else if (refItem instanceof DvQuantity) {
//    				nodeAtt = "DvQUANTITY";
//    			}
    		}   		
    		else {
    			org.w3c.dom.Element el1 = currentNode.getOwnerDocument().createElement(child.getNodeName());
    			org.w3c.dom.Node childEl = currentNode.appendChild(el1);

    			//find element by attribute
    			NamedNodeMap map = child.getAttributes();
    			String acrhetypeNodeIdText = null;
    			for (int k=0; map != null && k < map.getLength(); k++) {
    				Node nAtt= map.item(k);
    				el1.setAttribute(nAtt.getNodeName(), nAtt.getNodeValue());
    				if (nAtt.getNodeName().equals("archetype_node_id")) {
    					acrhetypeNodeIdText = nAtt.getNodeValue();

    					//search right element in refList
    					findRmByNode(child.getNodeName(), acrhetypeNodeIdText, rmRootObjPass);
    					refItem = rmRootObject;
    				}
    			}
    			
    			//find type for 'leaf' elements (DV_QUNATITY, DV_TEXT, ...)
    			if (map == null || map.getLength() == 0) {
    				if (currentNode.getNodeName().equals("items") && child.getNodeName().equals("value")) {
    					boolean quantityFound = false;
    					boolean codedTextFound = false;
    					boolean dateTimeFound = false;
    					boolean booleanFound = false;
    					NodeList localChildren = child.getChildNodes();
    					for (int idx = 0 ; localChildren != null && idx < localChildren.getLength(); idx++) {
    						org.w3c.dom.Node nIdx = localChildren.item(idx);
    						if (nIdx.getNodeName().equals("magnitude") || nIdx.getNodeName().equals("units") || nIdx.getNodeName().equals("precision") || nIdx.getNodeName().equals("accuracy")) {
    							quantityFound = true;
    							nodeAtt = "DV_QUANTITY";
    							break;
    						}
    						if (nIdx.getNodeName().equals("defining_code")) {
    							codedTextFound = true;
    							nodeAtt =  "DV_CODED_TEXT";
    							break;
    						}
    						//TODO: stestiraj datum!!!
    						if (nIdx.getNodeValue() != null && nIdx.getNodeValue().lastIndexOf(':') > nIdx.getNodeValue().indexOf(':') && nIdx.getNodeValue().lastIndexOf(':') > nIdx.getNodeName().lastIndexOf('T')) {
    							dateTimeFound = true;
    							nodeAtt =  "DV_DATE_TIME";
    							break;
    						}
    						if (nIdx.getNodeValue() != null && (nIdx.getNodeValue().equals("true") || nIdx.getNodeValue().equals("false")) && nIdx.getNodeName().equals("value")) {
    							booleanFound = true;
    							nodeAtt =  "DV_BOOLEAN";
    							break;
    						}    						
    					}
    					if (!quantityFound && !codedTextFound && !dateTimeFound && !booleanFound)
    						nodeAtt = "DV_TEXT";
    				}
    				//primer za 'name' element, ki ima pod sabo 'dv_coded_tekst'
    				if (currentNode.getNodeName().equals("items") && child.getNodeName().equals("name")) {
    					NodeList localChildren = child.getChildNodes();
    					for (int idx = 0 ; localChildren != null && idx < localChildren.getLength(); idx++) {
    						org.w3c.dom.Node nIdx = localChildren.item(idx);
    						if (nIdx.getNodeName().equals("defining_code")) {
    							nodeAtt = "DV_CODED_TEXT";
    							break;
    						}
    					}
    				}
    				
    				//case for 'subject'
    				if (currentNode.getNodeName().equals("items") && child.getNodeName().equals("subject")) {
						nodeAtt = "PARTY_SELF"; 					
    				}
    				if (currentNode.getNodeName().equals("external_ref") && child.getNodeName().equals("id")) {
    					nodeAtt = "HIER_OBJECT_ID";
    				}
    			}
    			
    			if (refItem == null) {}
    			else if (refItem instanceof Instruction) {
    				nodeAtt = "INSTRUCTION";
    				if (child.getNodeName().equals("activities")) {
    					nodeAtt = "ITEM_TREE";
    				}
    			}
    			else if (refItem instanceof Composition) {
    				nodeAtt = "COMPOSITION"; //TODO: test
    			}   
    			else if (refItem instanceof Evaluation) {
    				nodeAtt = "EVALUATION"; //TODO: test
    			}    			
    			else if (refItem instanceof Activity) {
    				nodeAtt = "ACTIVITY";
    			}
    			else if (refItem instanceof Cluster) {
    				nodeAtt = "CLUSTER";
    			}
    			else if (refItem instanceof Element) {
    				nodeAtt = "ELEMENT";
    				org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) refItem;
    				DataValue dv = el.getValue();
    				if (dv instanceof DvText) {
    					DvText dvT = (DvText) dv;
    				}
    				else if (dv instanceof DvQuantity) {
    					DvQuantity dvQ = (DvQuantity) dv;
    				}
    			}    			
    			
    			if (nodeAtt != null && !nodeAtt.equals(""))
    				el1.setAttribute("xsi:type", nodeAtt);
    			
    			//set default
    			rmRootObject = null;
    			populateXml(child.getChildNodes(), childEl, rmRootObjPass);
    		}
    	}
    	return;		
	}
	
	
	/**
	 * Function searches for 
	 * 
	 * @param elName
	 * @param archetypeNodeId
	 * @param rmObj
	 * @return
	 */
    public static Object findRmByNode(String elName, String archetypeNodeId, Object rmObj) {
    	
       	if (rmObj == null || archetypeNodeId == null || elName == null)
    		return null;
    	
       	
    	if (rmObj instanceof Section) {
    		Section sec = (Section) rmObj;
    		if (elName.equals("section") && archetypeNodeId.equals(sec.getArchetypeNodeId())) {
    			rmRootObject = sec;
    			return sec;
    		}
    		Iterator it = sec.getItems().iterator();
    		while (it.hasNext()) {
				Instruction object = (Instruction) it.next();
				findRmByNode(elName, archetypeNodeId, object);
			}
    		
    	}
    	else if (rmObj instanceof Instruction) {
    		Instruction in = (Instruction) rmObj;
    		if ((elName.equals("activities") || elName.equals("description")) 
    			 && archetypeNodeId.equals(in.getArchetypeNodeId())) {
    			rmRootObject = in;
    			return in;    		
    		}

    		Iterator it = in.getActivities().iterator();
    		while (it.hasNext()) {
				Activity object = (Activity) it.next();
				findRmByNode(elName, archetypeNodeId, object);				
			}
    	}
    	else if (rmObj instanceof Composition) { //TODO: preveri ce ok
    		Composition comp = (Composition) rmObj;
    		if ((elName.equals("data") || elName.equals("composition")) 
    			 && archetypeNodeId.equals(comp.getArchetypeNodeId())) {
    			rmRootObject = comp;
    			return comp;    		
    		}

			findRmByNode(elName, archetypeNodeId, comp.getContext().getOtherContext());
			if (comp.getContent() != null) {
				Iterator<ContentItem> it = comp.getContent().iterator();
				while (it.hasNext()) {
					ContentItem contentItem = it.next();
					findRmByNode(elName, archetypeNodeId, contentItem);
				}
			}
    	}   
    	else if (rmObj instanceof Evaluation) { //TODO: preveri ce ok!!
    		Evaluation ev = (Evaluation) rmObj;
    		if (archetypeNodeId.equals(ev.getArchetypeNodeId())) {
    			rmRootObject = ev;
    			return ev;    		
    		}

			findRmByNode(elName, archetypeNodeId, ev.getData());				
    	}    	
    	else if (rmObj instanceof Observation) {
    		Observation obs = (Observation) rmObj;
    		History history = obs.getData();
    		if (history != null && history.getEvents() != null) {
    			Iterator it = history.getEvents().iterator();
    			while (it.hasNext()) {
					PointEvent<ItemStructure> event = (PointEvent) it.next();
					findRmByNode(elName, archetypeNodeId, event);
				}
    		}    		    		
    	}
    	else if (rmObj instanceof Activity) {
    		Activity ac = (Activity) rmObj;
    		if ("activities".equals(elName) && archetypeNodeId.equals(ac.getArchetypeNodeId())) {
    			rmRootObject = ac;
    			return ac;
    		}

    		ItemTree itemTree = (ItemTree) ac.getDescription();
    		Iterator<Item> it = itemTree.getItems().iterator();
    		while (it.hasNext()) {
				Item item = it.next();
				findRmByNode(elName, archetypeNodeId, item);				
			}
    	}
    	else if (rmObj instanceof Item) {
 		
    		if (rmObj instanceof Cluster) {
    			Cluster c = (Cluster) rmObj; 
        		if ("items".equals(elName) && archetypeNodeId.equals(c.getArchetypeNodeId())) {
        			rmRootObject = c;
        			return c;    			
        		}
        		
        		Iterator<Item> it = c.getItems().iterator();
        		while (it.hasNext()) {
					Item item = it.next();
					findRmByNode(elName, archetypeNodeId, item);
				}
    		}
    		else if (rmObj instanceof org.openehr.rm.datastructure.itemstructure.representation.Element) {
    			org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) rmObj;
        		if ("items".equals(elName) && archetypeNodeId.equals(el.getArchetypeNodeId())) {
        			rmRootObject = el;
        			return el;    			
        		}
    		}		
    	}
    	else if (rmObj instanceof PointEvent<?>) {
    		PointEvent<?> event = (PointEvent) rmObj;
    		findRmByNode(elName, archetypeNodeId, event);
    	}
    	else if (rmObj instanceof ItemSingle) {
    		ItemSingle item = (ItemSingle) rmObj;
    		findRmByNode(elName, archetypeNodeId, item.getItem());
    	}     	
    	return null;
    }	
    
    
    
    /**
     * Serialize w3c dom document.
     * 
     * @param docToSerialize
     * @return
     * @throws Exception
     */
    public static String serializeXmlObject(org.w3c.dom.Document docToSerialize) throws Exception {

        // Get a factory (DOMImplementationLS) for creating a Load and Save object.
        org.w3c.dom.ls.DOMImplementationLS impl = 
            (org.w3c.dom.ls.DOMImplementationLS) 
            org.w3c.dom.bootstrap.DOMImplementationRegistry.newInstance().getDOMImplementation("LS");

        // Use the factory to create an object (LSSerializer) used to 
        // write out or save the document.
        org.w3c.dom.ls.LSSerializer writer = impl.createLSSerializer();
        org.w3c.dom.DOMConfiguration config = writer.getDomConfig();
        config.setParameter("xml-declaration", false);
        config.setParameter("format-pretty-print", Boolean.TRUE);
        
        // Use the LSSerializer to write out or serialize the document to a String.
        String serializedXML = writer.writeToString(docToSerialize);
        return serializedXML;
    }    
    
    
	/**
	 * Method reads file from file and generates RM object.
	 * 
	 * @param filePathAndName              e.g. "src/main/java/si/pint/archetypes/files/buff.txt"
	 * @param schemaType
	 * @return
	 * @throws XMLBindingException
	 * @throws XmlException
	 * @throws IOException
	 * @throws EmptyParametersException
	 * @throws Exception
	 */
	public static Object bindXmlToRM(String filePathAndName, SchemaType schemaType) throws XMLBindingException, XmlException, IOException, EmptyParametersException, Exception {
		//read from file
        FileInputStream fis = new FileInputStream(new File(filePathAndName)); 	        
        InputStreamReader isr = new InputStreamReader(fis, ENCODING);
        
        //set encoding
        XmlOptions options = new XmlOptions();
        options.setCharacterEncoding("UTF-8");
        
        //RM object
        Object rmObject = null;
        
        if (schemaType.equals(SECTION.type)) { //TODO: test!!
        	rmObject = SECTION.Factory.parse(isr, options);
        }
        else if (schemaType.equals(COMPOSITION.type)) {
        	rmObject = CompositionDocument.Factory.parse(isr, options).getComposition();
        }
        else if (schemaType.equals(OBSERVATION.type)) {
        	rmObject = ItemsDocument.Factory.parse(isr, options).getItems();
        }        
        else if (schemaType.equals(INSTRUCTION.type)) {
        	rmObject = INSTRUCTION.Factory.parse(isr, options);
        }
        else if (schemaType.equals(ITEMTREE.type)) { //TODO: test!!
        	rmObject = ItemsDocument.Factory.parse(isr, options);
        }
        else if (schemaType.equals(EVALUATION.type)) { //TODO: test!!
        	rmObject = EVALUATION.Factory.parse(isr, options);
        }        
        else {
        	throw new EmptyParametersException("Schema type not recognized in binding to RM!");
        }        
				
        XMLBinding xmlBinding = new XMLBinding();
		Object rmObj = xmlBinding.bindToRM(rmObject);
		return rmObj;
	}    
	
	/**
	 * Writes String object to file. 
	 * 
	 * @param xmlObject
	 * @param filePathAndName    e.g. "src/main/java/si/pint/archetypes/files/buff.txt"
	 * @param xmlString
	 * @throws IOException
	 */
	public static void writeToXML(Object xmlObject, String filePathAndName, String xmlString) throws IOException {
		File f = new File(filePathAndName);
		if (!f.exists())
			f.createNewFile();
		
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), ENCODING));		
		out.write(xmlString);
		out.close();		
		return;
	}	
	
	
	/**
	 * Creates new XML object from old one. Changes root element and adds some element attributes.
	 * 
	 * @param xmlObject
	 * @return
	 * @throws ParserConfigurationException
	 * @throws EmptyParametersException
	 * @throws Exception
	 */
	public static String applyXmlTransformation(Object xmlObject) throws ParserConfigurationException, EmptyParametersException, Exception {
		//first level children
		NodeList childrenLevel1 = null;
		String rootArchetypeId = null;
		String xsiTypeText = null;
		String rootElementName = null;
		
		if (xmlObject instanceof SECTION) {
			SECTION section_ = (SECTION) xmlObject;
			childrenLevel1 = section_.getDomNode().getChildNodes();
			rootArchetypeId = section_.getArchetypeNodeId();
			xsiTypeText = "SECTION";
			rootElementName = "content";
		}
		else if (xmlObject instanceof COMPOSITION) {
			COMPOSITION composition_ = (COMPOSITION) xmlObject;
			childrenLevel1 = composition_.getDomNode().getChildNodes();
			rootArchetypeId = composition_.getArchetypeNodeId();
			xsiTypeText = "COMPOSITION";
			rootElementName = "composition"; //or "data"
		}
		else if (xmlObject instanceof EVALUATION) { //TODO: test!!
			EVALUATION evaluation_ = (EVALUATION) xmlObject;
			childrenLevel1 = evaluation_.getDomNode().getChildNodes();
			rootArchetypeId = evaluation_.getArchetypeNodeId();
			xsiTypeText = "EVALUATION";
			rootElementName = "items";
		}		
		else if (xmlObject instanceof OBSERVATION) {
			OBSERVATION observation_ = (OBSERVATION) xmlObject;
			childrenLevel1 = observation_.getDomNode().getChildNodes();
			rootArchetypeId = observation_.getArchetypeNodeId();
			xsiTypeText = "OBSERVATION";
			rootElementName = "items"; 
		}		
		else if (xmlObject instanceof List<?>) { //List<CONTENTITEM> 
			List list_ = (List) xmlObject;
			//TODO: kaj narediti v tem primeru???
		}
		else if (xmlObject instanceof INSTRUCTION) {
			INSTRUCTION instruction_ = (INSTRUCTION) xmlObject;
			childrenLevel1 = instruction_.getDomNode().getChildNodes();
			rootArchetypeId = instruction_.getArchetypeNodeId();
			xsiTypeText = "INSTRUCTION";
			rootElementName = "items";			
		}
		else if (xmlObject instanceof ACTIVITY) { //TODO: tega primera verjetno ne bo 
			ACTIVITY activitiy_ = (ACTIVITY) xmlObject;
			childrenLevel1 = activitiy_.getDomNode().getChildNodes();
			rootArchetypeId = activitiy_.getArchetypeNodeId();
			xsiTypeText = "ACTIVITY";
			rootElementName = "??";
		}
		else if (xmlObject instanceof ItemTree) { //TODO: tuki je verjetno z veliko, test!!!
			ITEMTREE itemTree_ = (ITEMTREE) xmlObject;
			childrenLevel1 = itemTree_.getDomNode().getChildNodes();
			rootArchetypeId = itemTree_.getArchetypeNodeId();
			xsiTypeText = "ITEM_TREE";
			rootElementName = "description";			
		}
		else if (xmlObject instanceof ACTION) {
			ACTION action_ = (ACTION) xmlObject;
			childrenLevel1 = action_.getDomNode().getChildNodes();
			rootArchetypeId = action_.getArchetypeNodeId();
			xsiTypeText = "ACTION";
			rootElementName = "items";			
		}
		if (childrenLevel1 == null ||
			rootArchetypeId	== null ||
			xsiTypeText == null ||
			rootElementName == null)
			throw new EmptyParametersException("Xml transformation error! Unknown XML binding class!");
		
		//create empty XML object
		Document document = Rm2XmlUtils.createNewXmlDocument(rootElementName /*content,items,..*/, xsiTypeText /*rootXsiType*/, rootArchetypeId /*rootArchetypeNodeId*/);
		
		//populate XML with elemenets and values
		Rm2XmlUtils.populateXml(childrenLevel1 /*nodeList*/,  document.getFirstChild() /*currentNode*/, xmlObject /*rmRootObjPass*/);
		
		//transform XML object to String
		String stringXML = Rm2XmlUtils.serializeXmlObject(document);
				
		return stringXML;
	}
	
	/**
	 * Binds data from reference model instance to XML binding classes.
	 * 
	 * @param objToBind
	 * @return
	 * @throws XMLBindingException
	 * @throws EmptyParametersException
	 */
	public static Object bindRmToXML(Object objToBind) throws XMLBindingException, EmptyParametersException {
		if (objToBind == null)
			throw new EmptyParametersException("Object to bind cannot be null!");
		
		XMLBinding xmlBinding = new XMLBinding();
		Object retObject = xmlBinding.bindToXML(objToBind);
		return retObject;
	}	
	
	
	/**
	 * Replaces 'atXXXX' atributes with real names.
	 * 
	 * @param archetypeObj
	 * @param path
	 * @return
	 */
	public static String changeAttIdsForNames(Archetype archetypeObj, String path) {
		int idx = path.indexOf("[at");
		if (idx < 0)
			return path;
			
		while (idx >= 0) {
			String at = path.substring(idx + 1, idx + 7);
			if (at.matches("at\\d\\d\\d\\d")) {
				if (archetypeObj.getOntology().termDefinition("sl", at) != null) {
					String name = archetypeObj.getOntology().termDefinition("sl", at).getText();
					path = path.replace(at, name);					
				}
			}
			idx = path.indexOf("[at", idx + 2);
		}				
		return path;
	}
	
	
	/**
	 * Prints whole tree (CObjects & CAttributes) + RM data types
	 * 
	 * @param object
	 * @param archetype
	 * @return
	 */
	public static Object printArchetypeNodes(CObject object, Archetype archetype) {
		if (object == null || archetype == null)
			return null;
		
		//System.out.println(Rm2XmlUtils.changeAttIdsForNames(archetype, object.path()) + "      : " + object.getRmTypeName());
		System.out.println(object.path() + "      : " + object.getRmTypeName());
						
		if (!(object instanceof CComplexObject)) {
			return null;
		}
		
		CComplexObject compObject = (CComplexObject) object;
		
		Iterator<CAttribute> itAtt = compObject.getAttributes().iterator();
		CAttribute cAttributeOut = null;
		while (itAtt.hasNext()) {
			CAttribute cAttribute = itAtt.next();
			System.out.println(cAttribute.path() + "         :  " + cAttribute.getRmAttributeName());
			
			Iterator<CObject> itChildren = cAttribute.getChildren().iterator();
			while (itChildren.hasNext()) {
				CObject cObject = itChildren.next();
				printArchetypeNodes(cObject, archetype);
			}
		}
		
		return null;
	}
	

	/**
	 * Returns  all template paths.
	 * 
	 * @param object
	 * @param archetype
	 * @return
	 */
	public static List<String> getAllArchetypeNodesList(CObject object, Archetype archetype) {
		templatePaths = new ArrayList<String>();
		getAllArchetypeNodesListRec(object, archetype);
		return templatePaths;
	}
	
	
	/**
	 * Saves whole tree (CObjects)
	 * 
	 * @param object
	 * @param archetype
	 * @return
	 */
	private static void getAllArchetypeNodesListRec(CObject object, Archetype archetype) {
		if (object == null || archetype == null)
			return;
		
		templatePaths.add(object.path());
						
		if (!(object instanceof CComplexObject)) {
			return;
		}
		
		CComplexObject compObject = (CComplexObject) object;
		
		Iterator<CAttribute> itAtt = compObject.getAttributes().iterator();
		CAttribute cAttributeOut = null;
		while (itAtt.hasNext()) {
			CAttribute cAttribute = itAtt.next();
						
			Iterator<CObject> itChildren = cAttribute.getChildren().iterator();
			while (itChildren.hasNext()) {
				CObject cObject = itChildren.next();
				getAllArchetypeNodesListRec(cObject, archetype);
			}
		}
		
		return;
	}	
	
	
	/**
	 * Convert String from dd.MM.yyyy to yyyy-MM-dd.
	 * 
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	public static String parseStringDateToRmFormat(String date) {
		SimpleDateFormat sdfOld = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd");
		Date d;
		try {
			d = sdfOld.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return sdfNew.format(d);
	}
	
    
    /**
     * Getter.
     * 
     * @return
     */
    public static Object getRmRootObject() {
    	return rmRootObject;
    }
	
}
