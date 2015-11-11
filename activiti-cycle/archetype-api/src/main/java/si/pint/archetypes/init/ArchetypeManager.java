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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import lombok.extern.log4j.Log4j;
import openEHR.v1.template.TEMPLATE;

import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CMultipleAttribute;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.CPrimitiveObject;
import org.openehr.am.archetype.constraintmodel.CSingleAttribute;
import org.openehr.am.archetype.constraintmodel.ConstraintRef;
import org.openehr.am.archetype.constraintmodel.primitive.CBoolean;
import org.openehr.am.archetype.constraintmodel.primitive.CDate;
import org.openehr.am.archetype.constraintmodel.primitive.CDateTime;
import org.openehr.am.archetype.constraintmodel.primitive.CDuration;
import org.openehr.am.archetype.constraintmodel.primitive.CInteger;
import org.openehr.am.archetype.constraintmodel.primitive.CReal;
import org.openehr.am.archetype.constraintmodel.primitive.CString;
import org.openehr.am.archetype.constraintmodel.primitive.CTime;
import org.openehr.am.archetype.ontology.ArchetypeTerm;
import org.openehr.am.archetype.ontology.OntologyDefinitions;
import org.openehr.am.openehrprofile.datatypes.basic.CDvState;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvOrdinal;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity;
import org.openehr.am.openehrprofile.datatypes.quantity.Ordinal;
import org.openehr.am.openehrprofile.datatypes.text.CCodePhrase;
import org.openehr.am.parser.ParseException;
import org.openehr.am.serialize.ADLSerializer;
import org.openehr.am.template.Flattener;
import org.openehr.am.template.FlatteningException;
import org.openehr.am.template.OETParser;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.basic.DataValue;
import org.openehr.rm.datatypes.basic.DvBoolean;
import org.openehr.rm.datatypes.quantity.DvCount;
import org.openehr.rm.datatypes.quantity.DvOrdinal;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.quantity.datetime.DvTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.identification.TerminologyID;
import org.openehr.rm.util.GenerationStrategy;
import org.openehr.rm.util.SkeletonGenerator;
import org.openehr.validation.DataValidator;
import org.openehr.validation.DataValidatorImpl;
import org.openehr.validation.ValidationError;

import se.acode.openehr.parser.ADLParser;
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.archetypes.exceptions.EmptyParametersException;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import archetypes.FileLocator;

/**
 * @author Blaz
 *
 *	Methods for 'archetype roundtrip':
 *
 *  - addArchetype (1...*)
 *  - setTemplate (1...1)
 *  - parseADL
 *  - parseOET
 *  - flattenArchetypeObject
 *  - generateSkeleton
 *  - validateData
 *  - bindDataToXML
 *  - writeDataToXML
 *  - readDataFromXML
 *  
 */
@Log4j
public class ArchetypeManager {
	

	//Map of archetype (adl) files 
	private Map<String, Archetype> archeMap;
	
	//Object representing oet file
	private TEMPLATE template;
	
	//Object representing all 'adl' files + 'oet' file
	private Archetype archetypeObj;
	
	//RM object representing RM of 'archetypeObj'
	private Object rmObject;
	
	//ADL parser
	private ADLParser adlParser;
	
	//OET parser - parsing template files
	private OETParser oetParser;
	
	//Flattener
	private Flattener flattener;
	
	//Factory method to get instance of the RM skeleton generator
	SkeletonGenerator generator;
	
	//ADL serializer
	ADLSerializer adlSerializer = new ADLSerializer();
	
	//RM validator
	private DataValidator validator;
	
	//encoding
	private static final String ENCODING = "UTF-8";
	
	//map of input values (path, RM type)
	private static Map<String, Object> archetypeObjInputsmap;
	
	//path to local disk where XMLs will be stored
	public static String XML_DIRECTORY = null;
	
	
	/**
	 * Constructor. 
	 */
	public ArchetypeManager() {
		archeMap = new HashMap<String, Archetype>();
		archetypeObj = null;
		template = null;
		oetParser = new OETParser();
		flattener = new Flattener();
		adlParser = null;
		rmObject = null;
		archetypeObjInputsmap = null;
		if (XML_DIRECTORY == null || XML_DIRECTORY.equals("")) {
			XML_DIRECTORY = "/opt/activiti-5.4/storage/"; //TODO: ni za localhost ali testno izvajanje!!!
			File f = new File(XML_DIRECTORY);
			if (!f.exists()) //for testing
				XML_DIRECTORY = "C:/Projects/Activiti/Snapshots/Proces-Opomnik-Vnos-PEF_New/archetype-api/src/main/resources/data/"; 
		}
	}
	
	
	
	/**
	 * Loads template file. 
	 * 
	 * @param filePath   relative file of template file (e.g. 'src/main/java/si/pint/archetypes/files/openEHR-EHR-ITEM_TREE.oet')
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public TEMPLATE setTemplate(String fileName) throws FileNotFoundException, UnsupportedEncodingException, Exception {
		InputStream is = getArchetypeAbsPath(fileName);
        template = oetParser.parseTemplate(is).getTemplate();
		return template;
	}
	
	
	
	/**
	 * 
	 * Loads ADL (archetype) file and adds it to the archetype archetypeObjInputsmap.
	 * 
	 * @param filePath   relative file of template file (e.g. 'src/main/java/si/pint/archetypes/files/openEHR-EHR-ITEM_TREE.medication.v1.adl')
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws Exception
	 */
	public Archetype addArchetype(String fileName) throws IOException, ParseException, Exception {
		adlParser = new ADLParser(getArchetypeAbsPath(fileName), ENCODING);
		Archetype archetype_ = adlParser.parse();
		archeMap.put(archetype_.getArchetypeId().getValue(), archetype_);
		return archetype_;
	}
	
	
	/**
	 * 
	 * 	A template flattener which can take OET object model generated by 
	 *  OET-template parser and join constraints from the parent archetype and
	 *  all included children archetypes, and output the combined constraints
	 *  in pure AOM instance.
	 * 
	 * @return
	 * @throws FlatteningException
	 * @throws EmptyParametersException
	 */
	public Archetype flattenArchetypeObject() throws FlatteningException, EmptyParametersException {
		if (this.archeMap.isEmpty() || this.template == null)
			throw new EmptyParametersException("Template and/or Archetype files are not loaded!");
		
		archetypeObj = flattener.toFlattenedArchetype(this.template, this.archeMap);
		return null;
	}
	
	
	
	/**
	 * Create RM object tree based on given archetype with a given strategy. 
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object generateSkeleton(GenerationStrategy generationStrategy) throws EmptyParametersException, Exception {
		if (this.archetypeObj == null || this.archeMap == null)
			throw new EmptyParametersException("Archetype/template not loaded!");
		generator = SkeletonGenerator.getInstance();
		Object obj = generator.create(this.archetypeObj, null, this.archeMap, generationStrategy);
		return obj;
	}
	
	
	
	/**
	 * Create RM object tree based on given archetype with a given strategy. 
	 * 
	 * @param userInputsMap
	 * @return Object
	 * @throws Exception
	 */
	public Object populateSkeleton(Map userInputsMap) throws EmptyParametersException, IllegalArgumentException, Exception {
		if (this.archetypeObj == null || this.archeMap == null)
			throw new EmptyParametersException("Archetypef");
		
		generator = SkeletonGenerator.getInstance();
		GenerationStrategy strategyGlbl = GenerationStrategy.MAXIMUM;
		
		this.rmObject = generator.create(this.archetypeObj, null, this.archeMap, strategyGlbl);
		if (userInputsMap != null) {

			//in case of MAXIMUM generation delate all nodes that are not on path
			Data2RmUtils.deleteAllNodesNotInPath(this.rmObject, this.archetypeObj);
			
			Iterator it = userInputsMap.keySet().iterator();
			while (it.hasNext()) {
				ArcheDataPath pathData = (ArcheDataPath) it.next();
				//locate user input node: if it doesn't exit -> crete ALL nodes that are on its path
				int depth = 0;
				String pathShortExt = Data2RmUtils.findSubPath(pathData.getPath(), depth);
				while(pathShortExt != null) {
								
					Object nodeOnPath = Data2RmUtils.findNodeToGenerateSiblings(pathShortExt, this.rmObject); //nova metoda iskanja: ce je elementov vec, poisce zadnji element (tipa Cluster) iz seznama 
								
					//if node not in tree: create new RM object
					if (nodeOnPath == null) { 
						generateNewNode(pathShortExt, pathData, depth, userInputsMap);						
					}
					else {  
						generateNewSiblingNode(pathShortExt, pathData, depth, userInputsMap, nodeOnPath);
					}

					depth++;
					pathShortExt = Data2RmUtils.findSubPath(pathData.getPath(), depth);
				}				
				
			}
		}
		
		return this.rmObject;
	}

	
	/**
	 * Generate new node and attach new value.
	 * 
	 * @param pathShortExt
	 * @param pathData
	 * @param depth
	 * @param userInputsMap
	 * @throws Exception
	 */
	private void generateNewNode(String pathShortExt, ArcheDataPath pathData, int depth, Map userInputsMap) throws Exception {
		
		Object nodeOnPath = null;
		
		//find new (leaf) 'constraint' CObject from 'flattened' archetype
		CObject cObject = Data2RmUtils.findConstraintObjectFromPath(pathShortExt, archetypeObj);
		
		if (cObject == null) {
			throw new IllegalArgumentException("Path: depth="+depth+" \nshort=" + pathShortExt+" \ndata="+pathData.getPath() +" does not exist!! Incorrect user input!");
		}
			
		GenerationStrategy strategy = GenerationStrategy.MAXIMUM;
		
		//always generate from last archetype on template path!
		Archetype archetype4Gen = this.archetypeObj;
		String archetypeId = Data2RmUtils.findLastArchetypeIdFromPath(pathShortExt); 
		if (archetypeId != null)
			archetype4Gen = this.archeMap.get(archetypeId);												

		//generate new RM object
		nodeOnPath = generator.createObject(cObject, archetype4Gen, this.archeMap, strategy);							
		
		//delete default 'leaf' values 
		String pathToEndNode = Data2RmUtils.findSubpathFromNode(pathData.getPath(), depth);
		Data2RmUtils.deleteGeneratedObject(pathToEndNode, nodeOnPath);

		//exception for Cluster -> delete redundant subnodes
		Data2RmUtils.deleteRedundantClusterElements(nodeOnPath, pathToEndNode, 0);
		
		//if it is a leaf node: set value
		if (nodeOnPath instanceof DataValue) {
			nodeOnPath = addDataValueToSkeleton((DataValue) nodeOnPath, userInputsMap.get(pathData), cObject, archetype4Gen);
			
			if (nodeOnPath instanceof DvCodedText) { //special case (2 level structure)
				depth++;
			} 		
		}
								
		//put it on the right place
		Data2RmUtils.appendDataToTreeStart(pathShortExt, this.rmObject, nodeOnPath, 0);		
	}
	
	
	
	/**
	 * Generate new sibling node and attach new value.
	 * 
	 * @param pathShortExt
	 * @param pathData
	 * @param depth
	 * @param userInputsMap
	 * @param nodeOnPath
	 * @throws Exception
	 */
	private void generateNewSiblingNode(String pathShortExt, ArcheDataPath pathData, int depth, Map userInputsMap, Object nodeOnPath) throws Exception {
		if (nodeOnPath instanceof DataValue && !(nodeOnPath instanceof CodePhrase)) { //leaf node already set -> user trying to add another (same path) leaf node !!! Check if it is possible to add same node. 
			String firstSiblingPath = Data2RmUtils.findLastAttOnPath(pathData.getPath());
			if (firstSiblingPath != null && !firstSiblingPath.equals("")) {
				
				//create constraint object
				CObject cFirstSibling = Data2RmUtils.findConstraintObjectFromPath(firstSiblingPath, archetypeObj);
				
				//check if this element allows new values -> if not go higher in tree
				int depthUp = depth - 1;
				while (depthUp >=0 && !cFirstSibling.getOccurrences().isUpperUnbounded() /*&& !cFirstSibling.getRmTypeName().equals("CLUSTER")*/) { //if not -> find first upper node that allows multiple nodes (CLUSTER is definitely OK)
					firstSiblingPath = Data2RmUtils.findSubPath(pathData.getPath(), depthUp);
					cFirstSibling = Data2RmUtils.findConstraintObjectFromPath(firstSiblingPath, archetypeObj);
					depthUp--;
				}

				//if leaf node 'below' archetype_id (nested archetype)** && leaf node!
				String archetypeId = Data2RmUtils.findLastArchetypeIdFromPath(firstSiblingPath); //TODO: test pri vecih arhetipih
				Archetype archetype4Gen = this.archeMap.get(archetypeId);
				if (archetype4Gen == null){
					archetype4Gen = this.archetypeObj;
				}
				
				//generate RM object from constraint
				Object firstSibling = generator.createObject(cFirstSibling, archetype4Gen, this.archeMap, GenerationStrategy.MAXIMUM);
												
				//set value: go to first node with DataValue ('value' element)
				String newDataValuePath = Data2RmUtils.findFirstValueNode(pathData.getPath(), firstSiblingPath);
				
				//delete all 'leaf' nodes //TODO: delete also redundant cluster elements
				Data2RmUtils.deleteAllFinalNodes(firstSibling, archetypeObj, newDataValuePath);
				
				//exception for Cluster -> delete redundant subnodes
				Data2RmUtils.deleteRedundantClusterElements(firstSibling, newDataValuePath, 0);
												
				//locate new 'DataValue' node
				Object newDataValue = Data2RmUtils.findNodeToGenerate(newDataValuePath, firstSibling);
				
				//exception for DvOrdinal (multilevel structure)
				if (newDataValue instanceof DataValue) {
					if (newDataValue instanceof DvOrdinal) {
						CComplexObject ccomplex = (CComplexObject) cFirstSibling;
						List attributes = ccomplex.getAttributes();
						if (attributes != null && attributes.size() > 0) {
							CSingleAttribute singleAtt = (CSingleAttribute) attributes.get(0);
							List children =  singleAtt.getChildren();
							if (children != null && children.size() > 0) {
								cFirstSibling = (CObject) children.get(0);
							}
						}
						
					}
						
					newDataValue = addDataValueToSkeleton((DataValue) newDataValue, userInputsMap.get(pathData), cFirstSibling, archetype4Gen);
				}
				
				//try to append to existing tree
				Data2RmUtils.appendDataToTreeStart(firstSiblingPath, this.rmObject, firstSibling, 0);

			}
		}		
	}


	/**
	 * OpeneEHR referece model validator
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public List<ValidationError> validateData(Locatable data) throws Exception {
		if (this.archetypeObj == null)
			throw new EmptyParametersException("Template/archetype files are not loaded!");
		if (data == null)
			throw new EmptyParametersException("Data object was not passed properly!");
		
		validator = new DataValidatorImpl();
		List<ValidationError> errors = validator.validate(data, this.archetypeObj);		
		return errors;
	}
	
	
	/**
	 * Converts RM object (with data) to XML and writes it to eXist
	 * 
	 * @param path
	 * @return
	 * @throws Exception 
	 * @throws ParserConfigurationException 
	 */
	public String saveRmObjectToXML(SystemType sys, String userId, String patientId,String fileName, String domain) throws ParserConfigurationException, Exception {
		if (this.rmObject == null)
			throw new EmptyParametersException("Archetype/Template not loaded or RM object not flattened!");
		
		Object rmObjectBind = Rm2XmlUtils.bindRmToXML(this.rmObject);
		
		String rmXml = Rm2XmlUtils.applyXmlTransformation(rmObjectBind);
		
		DBManager.getInstance(sys).addXML(userId, patientId,rmXml, fileName, domain);
		
		//temp resitev: write to local disk
//		Rm2XmlUtils.writeToXML(rmObjectBind, XML_DIRECTORY + fileName, rmXml);
		
		//test: generate RM object from XML
//		Object rawObj = Rm2XmlUtils.bindXmlToRM(XML_DIRECTORY + fileName, SECTION.type);
		
		return rmXml;

	}
	
	
	/**
	 * Fills global map 'archetypeObjInputsmap' with pairs (path, RM type) 
	 * 
	 * @return
	 * @throws EmptyParametersException
	 */
	public Map getArchetypeObjInputsMap() throws EmptyParametersException { //funkcija bo vracala pare (path, tip)
		if (this.archetypeObj == null) 
			throw new EmptyParametersException("Archetype object not loaded!!");
		
		archetypeObjInputsmap = new HashMap<String, Object>();
		fillMapWithNodes(this.archetypeObj.getDefinition());
		
		return archetypeObjInputsmap;
	}
	
	/**
	 * Getter method for 'archetypeObj' Archetype object.
	 * 
	 * @return
	 */
	public Archetype getArchetypeObj() {
		return this.archetypeObj;
	}
	
	
	/**
	 * Recursive method - goes down to leaf (basic) elements.
	 * 
	 * @param archetypeObjNode
	 */
	private static void fillMapWithNodes(Object archetypeObjNode) {
		if (archetypeObjNode == null)
			return;
		
		//System.out.println("inst:  " + archetypeObjNode.getClass().getName());
		
		//constraintmodel -->>>>
		if (archetypeObjNode instanceof CComplexObject) {
			CComplexObject complexObj = (CComplexObject) archetypeObjNode;
			archetypeObjInputsmap.put(complexObj.path(), complexObj.getRmTypeName()  /*+ ": CComplexObject"*/);
			iterateChildrenFillMap(complexObj.getAttributes(), complexObj.path());
		}
		else if (archetypeObjNode instanceof CPrimitiveObject) {
			CPrimitiveObject primitiveObj = (CPrimitiveObject) archetypeObjNode;
			archetypeObjInputsmap.put(primitiveObj.path(), primitiveObj.getRmTypeName() /*+ ": CPrimitiveObject"*/);
		}		
		else if (archetypeObjNode instanceof ConstraintRef) {
			ConstraintRef constraintRef = (ConstraintRef) archetypeObjNode;
			archetypeObjInputsmap.put(constraintRef.path(), constraintRef.getRmTypeName()/* + ": ConstraintRef"*/);
		}
		else if (archetypeObjNode instanceof CMultipleAttribute) { //constraint on any kind of attribute node.
			CMultipleAttribute multiAttribute = (CMultipleAttribute) archetypeObjNode;
//			archetypeObjInputsmap.put("a: " + multiAttribute.path(), multiAttribute.getRmAttributeName());
			iterateChildrenFillMap(multiAttribute.getChildren(), multiAttribute.path());
		}
		else if (archetypeObjNode instanceof CSingleAttribute) { //
			CSingleAttribute singleAtt = (CSingleAttribute) archetypeObjNode;
//			archetypeObjInputsmap.put("a: " + singleAtt.path(), singleAtt.getRmAttributeName());
			iterateChildrenFillMap(singleAtt.getChildren(), singleAtt.path());
		}
		else if (archetypeObjNode instanceof CBoolean) { //
			CBoolean bool = (CBoolean) archetypeObjNode;
		}
		else if (archetypeObjNode instanceof CDate) { //
			
		}
		else if (archetypeObjNode instanceof CDateTime) { //
			
		}
		else if (archetypeObjNode instanceof CDuration) { //
			
		}
		else if (archetypeObjNode instanceof CInteger) { //
			
		}	
		else if (archetypeObjNode instanceof CReal) { //
			
		}	
		else if (archetypeObjNode instanceof CString) { //
			
		}	
		else if (archetypeObjNode instanceof CTime) { //
			
		}			
		//datatypes --->>>>>
		else if (archetypeObjNode instanceof CCodePhrase) { //Express constraints on instances of DV_CODED_TEXT.
			CCodePhrase codePhrase = (CCodePhrase) archetypeObjNode;
			archetypeObjInputsmap.put(codePhrase.path(), codePhrase.getRmTypeName() /*+ ": CCodePhrase"*/);
			iterateChildrenFillMap(codePhrase.getCodeList(), codePhrase.path());
		}
		else if (archetypeObjNode instanceof CDvQuantity) { //class represents constrain instances of DV_QUANTITY.
			CDvQuantity dvQuantity = (CDvQuantity) archetypeObjNode;
			archetypeObjInputsmap.put(dvQuantity.path(), dvQuantity.getRmTypeName() /*+ ": CDvQuantity"*/);
		}
		else if (archetypeObjNode instanceof CDvState) { //Constraint type for DV_STATE instances
			CDvState dvState = (CDvState) archetypeObjNode;
			archetypeObjInputsmap.put(dvState.path(), dvState.getRmTypeName() /*+ ": CDvState"*/);
		}		
		
		else if (archetypeObjNode instanceof CDvOrdinal) { //Class specifying constraints on instances of DV_ORDINAL
			CDvOrdinal dvOrdinal = (CDvOrdinal) archetypeObjNode;
			archetypeObjInputsmap.put(dvOrdinal.path(), dvOrdinal.getRmTypeName()/* + ": CDvOrdinal"*/);
		}
		else if (archetypeObjNode instanceof String) {
			String text = (String) archetypeObjNode;
			//archetypeObjInputsmap.put(text, text.getClass().getName());
		}

	}
	
	
	/***
	 * Helper method for recursion over List objects.
	 * 
	 * @param attributeList
	 */
	private static void iterateChildrenFillMap(List<?> attributeList, String path) {
		if (attributeList == null || attributeList.size() == 0)
			return;
				
		Iterator<?> it = attributeList.iterator();
		while (it.hasNext()) {
			Object child = it.next();
			if (child instanceof String)
				archetypeObjInputsmap.put(path /*+ "/" + (String) child*/, "java.util.String");
			fillMapWithNodes(child);
		}
	}
	
	/**
	 * Returns ontology map
	 * 
	 * @return
	 */
	public Map getOntologyMap(Archetype archetype_) {
		if (archetype_ == null)
			return null;
		
		Map ontoMap = new HashMap();
		List<OntologyDefinitions> list = archetype_.getOntology().getTermDefinitionsList();
		if (list == null)
			return null;
		
		Iterator<OntologyDefinitions> it = list.iterator();
		while (it.hasNext()) {
			OntologyDefinitions ontologyDef = it.next();
			if (ontologyDef.getDefinitions() == null || (!ontologyDef.getLanguage().equals("en") && !ontologyDef.getLanguage().equals("sl")))
				continue;
			Iterator<ArchetypeTerm> itTerms = ontologyDef.getDefinitions().iterator();
			while (itTerms.hasNext()) {
				ArchetypeTerm archetypeTerm = itTerms.next();
				ontoMap.put(archetypeTerm.getCode(), archetypeTerm.getText());
			}
			
		}
		return ontoMap;
		
	}
	
	/**
	 * Add value to RM object DataValue.
	 * 
	 * @param dataValue
	 * @param value
	 * @return
	 */
	private Object addDataValueToSkeleton(DataValue dataValue, Object value, CObject cObject, Archetype archetype_) {
		
		if (dataValue instanceof DvQuantity) {
			
			//int newValue = value instanceof Integer ? ((Integer) value).intValue() : Integer.parseInt((String) value);
			double newValue = 0;
			if (value instanceof Integer) {
				newValue = ((Integer) value).intValue();
			}
			else {
				try {
					newValue = Integer.parseInt((String) value);
				} catch (NumberFormatException nfe) {
					newValue = Double.parseDouble((String) value);
				}
			}
			((DvQuantity) dataValue).setMagnitude(newValue);
		}
		else if (dataValue instanceof DvCodedText) {
			DvCodedText codedText = (DvCodedText) dataValue;
			codedText.setDefiningCode(new CodePhrase(new TerminologyID("local"), (String) value));
			codedText.setValue((String) (getOntologyMap(archetype_)).get(value));	
		}
		else if (dataValue instanceof DvOrdinal) {
			DvOrdinal ordinal = (DvOrdinal) dataValue;
			CDvOrdinal cOrdinal = (CDvOrdinal) cObject; 
			Iterator<Ordinal> it = cOrdinal.getList().iterator();
			Ordinal o = null;
			while (it.hasNext()) {
				Ordinal oInt = it.next();
				if (oInt.getSymbol().getCodeString().equals(value)) {
					o = oInt;
					break;
				}		
			}
			if (o == null)
				return null;
			ordinal.setValue(o.getValue());
			ordinal.getSymbol().setDefiningCode(new CodePhrase(new TerminologyID("local"), (String) value));
			ordinal.getSymbol().setValue((String) (getOntologyMap(archetype_)).get(value));
		}
		else if (dataValue instanceof DvCount) {
			DvCount dc = (DvCount) dataValue;
			int newValue = value instanceof Integer ? ((Integer) value).intValue() : Integer.parseInt((String) value);
			
			dc.setMagnitude(newValue);
		}
		else if (dataValue instanceof DvText) {
			DvText dt = (DvText) dataValue;
			dt.setValue((String) value);
		} else if (dataValue instanceof DvDuration) {
			
			if(value instanceof String && ((String)value).contains("P")){
				dataValue = new DvDuration((String)value);
			} else{
				int newValue = value instanceof Integer ? ((Integer) value).intValue() : Integer.parseInt((String) value);
				dataValue = new DvDuration("P" + newValue + "Y");	
			}
			
			//dataValue = new DvDuration(""+newValue);
		}
		else if (dataValue instanceof DvDate) {
			String dateStr = (String) value;
			if (dateStr.matches("[0-9][0-9].[0-9][0-9].[1-2][0-9][0-9][0-9]"))
				dataValue = new DvDate(Rm2XmlUtils.parseStringDateToRmFormat(dateStr));
			else
				dataValue = new DvDate(dateStr);
				
		}
		else if (dataValue instanceof DvTime) {  //dataValue - format HH:mm 
			String time = (String) value;
			if (time.contains(".")) {
				dataValue = new DvTime("00:" + time.replace(".", ":") );
			} else if (time.lastIndexOf(":") > 4) { 
				dataValue = new DvTime(time);
			}
			else {
				dataValue = new DvTime(time + ":00");
			}
		}	
		else if (dataValue instanceof DvDateTime) {  //dataValue - format HH:mm 
			String time = (String) value;
			try {
				dataValue = new DvDateTime(time + "T00");
			} catch (IllegalArgumentException iae) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					dataValue = new DvDateTime(sdf.format(UserRegistryFactory.parseTimestamp(time).getTime()) + "T00");
				} catch (Exception e) {}		
			}
		}		
		else if (dataValue instanceof DvBoolean) {
			dataValue = new DvBoolean(Boolean.parseBoolean((String) value));
		}
		else {
			return null;
		}
		//TODO: dodaj ostale tipe		
		return dataValue;
	}


	/**
	 * Returns RM 'skeleton' object of template/archetype file(s).
	 * 
	 * @return
	 */
	public Object getRmObject() {
		return rmObject;
	}
	
	/**
	 * Returns archetype object base on its id.
	 * 
	 * @param archetypeId
	 * @return
	 */
	public Archetype getArchetypeFromId(String archetypeId) {
		if (this.archeMap == null)
			return null;
		
		return this.archeMap.get(archetypeId);
	}
	
	/**
	 * Returns InputStream of a given file.
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream getArchetypeAbsPath(String fileName) {
		InputStream is = FileLocator.class.getResourceAsStream(fileName);
		return is;
	}
	
}
