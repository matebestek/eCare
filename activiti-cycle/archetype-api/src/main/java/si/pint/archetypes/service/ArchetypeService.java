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
package si.pint.archetypes.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.CSingleAttribute;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvOrdinal;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantityItem;
import org.openehr.am.openehrprofile.datatypes.quantity.Ordinal;
import org.openehr.am.openehrprofile.datatypes.text.CCodePhrase;
import org.openehr.am.template.FlatteningException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.validation.ValidationError;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.exceptions.EmptyParametersException;
import si.pint.archetypes.init.ArchetypeManager;
import si.pint.archetypes.init.Data2RmUtils;
import si.pint.database.exist.DBManager.SystemType;

/**
 * 
 * @author Blaz
 * 
 */
public abstract class ArchetypeService implements IArchetypeService {

	ArchetypeManager archetypeManager = null;
	private List<ValidationError> errorList;
	Object rmObject = null;
	protected EMPLOYEE_TYPE_ENUM employeeType = null;

	public abstract void setEmployeeType();
	
	public ArchetypeService() {
		archetypeManager = new ArchetypeManager();
		errorList = null;		
	}

	
	public void findValueForPath(String pathShortExt) {
		Archetype a = archetypeManager.getArchetypeObj();
		CObject co = Data2RmUtils.findConstraintObjectFromPath(pathShortExt, a);

	}

	// flatten template and archetyes
	private Object flattenArchetypeObject() throws FlatteningException,
			EmptyParametersException {
		return archetypeManager.flattenArchetypeObject();
	}

	//
	private Object generateRMFromInputs(Map userInputsMap)
			throws IllegalArgumentException, EmptyParametersException,
			Exception {
		Object rmObject = archetypeManager.populateSkeleton(userInputsMap);
		return rmObject;
	}

	// validation
	private List<ValidationError> validateRM(Locatable rmObject)
			throws Exception {
		return archetypeManager.validateData(rmObject);
	}

	/**
	 * Loads template/archetype files and flattens them.
	 * 
	 * @param userInputMap
	 * @param userInputsMap
	 * @return DvBoolean, DvQuantity, DvCodedText, ...
	 */
	@Override
	public boolean initInput() {
		try {
			// load templates and archetypes
			if (!loadAdlAndTemplateFiles()) 
				return false;
			
			//ASTMA, DIABETES ali SHIZOFRENIJA
			setEmployeeType();

			// flatten them into Archetype object representation
			flattenArchetypeObject();

			// print all nodes
//			 Rm2XmlUtils.printArchetypeNodes(this.archetypeManager.getArchetypeObj().getDefinition(),
//			 this.archetypeManager.getArchetypeObj());

		} catch (FlatteningException e) {
			e.printStackTrace();
			return false;
		} catch (EmptyParametersException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public Locatable getRMObject() {
		return (Locatable) rmObject;
	}

	/**
	 * @param pathShortExt npr. /items/data/...
	 * @param valueCode npr. at0045 pri podatkovnem tipu DV_ORDINAL. Podaš torej id vrednosti iz šifranta
	 * */
	public Object getValue(String pathShortExt, String valueCode) {
		CObject cObject = Data2RmUtils.findConstraintObjectFromPath(
				pathShortExt, this.archetypeManager.getArchetypeObj());
		if (cObject instanceof CDvOrdinal) {
			CDvOrdinal cOrdinal = (CDvOrdinal) cObject;
			Iterator<Ordinal> it = cOrdinal.getList().iterator();
			Ordinal o = null;
			while (it.hasNext()) {
				Ordinal oInt = it.next();
				if (oInt.getSymbol().getCodeString().equals(valueCode)) {
					o = oInt;
					break;
				}
			}			
			return o == null?null:o.getValue();
		}

		return null;
	}
	
	/**
	 * @param pathShortExt npr. /items/data/...
	 * @param valueCode npr. at0045 pri podatkovnem tipu DV_ORDINAL. Vrneš torej id vrednosti iz šifranta
	 * */
	public Object geDefaultCode(String pathShortExt) {
		CObject cObject = Data2RmUtils.findConstraintObjectFromPath(
				pathShortExt, this.archetypeManager.getArchetypeObj());
		if (cObject instanceof CDvOrdinal) {
			CDvOrdinal cOrdinal = (CDvOrdinal) cObject;
			Ordinal orf = cOrdinal.getList().iterator().next();//vzamem prvo vrednost
			return orf.getSymbol().getCodeString();			
		} 
		else if (cObject instanceof CDvQuantity){
			CDvQuantity cq = (CDvQuantity)cObject;
			CDvQuantityItem item = cq.getList().iterator().next();
			
		} else if (cObject instanceof CComplexObject){
			CComplexObject co = (CComplexObject)cObject;
			CSingleAttribute a = (CSingleAttribute)co.getAttribute("defining_code");
			CCodePhrase c = (CCodePhrase)a.getChildren().get(0);
			String code = c.getCodeList().get(c.getCodeList().size()-1);//vzamem zadnjo vrednost iz šifranta
			return code;
		}
		return null;
	}
	
	
	/**
	 * Looks for right archetype object and returns ontology value based on key.
	 * 
	 * @param archetypeId
	 * @param key
	 * @return
	 */
	public Object getOntologyValue(String archetypeId, String key) {

		if (this.archetypeManager == null)
			return null;
		
		Archetype archetype = this.archetypeManager.getArchetypeFromId(archetypeId);
		if (archetype == null)
			return null;
		
		Map ontologyMap = this.archetypeManager.getOntologyMap(archetype);
		if (ontologyMap == null)
			return null;
		
		return ontologyMap.get(key);
	}
	

	/**
	 * Populate RM object model with user data and validate.
	 * 
	 * @param userInputsMap
	 * @return
	 */
	public boolean setAndValidataData(Map userInputsMap) {
		try {
			// generate RM
			this.rmObject = generateRMFromInputs(userInputsMap);
			if (!(this.rmObject instanceof Locatable)) {
				EmptyParametersException epe = new EmptyParametersException(
						"Error in RM skeleton generation");
				System.out.println("[ArchetypeService] Error in RM skeleton generation");
				epe.printStackTrace();
				return false;
			}

			// validate data
			this.errorList = validateRM((Locatable) rmObject);

			if (this.errorList != null && this.errorList.size() > 0) {
				for(int i = 0; i < this.errorList.size(); i++) {
					System.out.println("[ArchetypeService] [Validation] Error in RM validation:");
					System.out.println("\t" + this.errorList.get(i).getErrorType().toString() );
					System.out.println("\t" + this.errorList.get(i).getArchetypePath() );
					System.out.println("\t" + this.errorList.get(i).getRuntimePath() );
					System.out.println("\t" + this.errorList.get(i).toString());
				}
				return false;
			}

		} catch (FileNotFoundException e) {
			System.out.println("[ArchetypeService] FileNotFoundException");
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			System.out.println("[ArchetypeService] UnsupportedEncodingException");
			e.printStackTrace();
			return false;
		} catch (FlatteningException e) {
			System.out.println("[ArchetypeService] FlatteningException");
			e.printStackTrace();
			return false;
		} catch (EmptyParametersException e) {
			System.out.println("[ArchetypeService] EmptyParametersException");
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException iae) {
			System.out.println("[ArchetypeService] IllegalArgumentException");
			iae.printStackTrace();
			return false;
		} catch (Exception e) {
			System.out.println("[ArchetypeService] Exception");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	
	/**
	 * Save user input data to persistent layer.
	 * 
	 * @return
	 * @throws Exception
	 * @throws ParserConfigurationException
	 */
	public boolean saveInput(SystemType sys, String userId, String patientId) {
		if (this.archetypeManager.getArchetypeObj() == null
				|| this.archetypeManager.getRmObject() == null) {
			EmptyParametersException epe = new EmptyParametersException(
					"Archetype/Template files not loaded!!");
			epe.printStackTrace();
			return false;
		}

		try {
		
			String fileName = patientId + "_"
					+ Calendar.getInstance().getTimeInMillis() + ".xml";
			String xml = this.archetypeManager.saveRmObjectToXML(sys, userId,patientId,fileName, this.employeeType.toDomainName());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		return true;
		
	}
	
	/**
	 * Save user input data to persistent layer.
	 * 
	 * @return filename
	 * @throws Exception
	 * @throws ParserConfigurationException
	 */
	public String saveInputReturnFilename(SystemType sys, String userId, String patientId) {
		if (this.archetypeManager.getArchetypeObj() == null
				|| this.archetypeManager.getRmObject() == null) {
			EmptyParametersException epe = new EmptyParametersException(
					"Archetype/Template files not loaded!!");
			epe.printStackTrace();
			return null;
		}

		try {
		
			String fileName = patientId + "_"
					+ Calendar.getInstance().getTimeInMillis() + ".xml";
			String xml = this.archetypeManager.saveRmObjectToXML(sys, userId,patientId,fileName, this.employeeType.toDomainName());
			return fileName;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		
	}
	

	/**
	 * Returns inputs map.
	 * 
	 * @return
	 * @throws EmptyParametersException
	 */
	public Map getInputsMap() {
		try {
			return this.archetypeManager.getArchetypeObjInputsMap();
		} catch (EmptyParametersException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ValidationError> getErrorList() {
		return errorList;
	}
	
	public EMPLOYEE_TYPE_ENUM getEmployeeType() {
		return this.employeeType;
	}

}
