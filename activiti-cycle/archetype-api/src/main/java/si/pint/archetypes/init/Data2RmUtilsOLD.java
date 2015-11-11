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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.content.ContentItem;
import org.openehr.rm.composition.content.entry.Activity;
import org.openehr.rm.composition.content.entry.Evaluation;
import org.openehr.rm.composition.content.entry.Instruction;
import org.openehr.rm.composition.content.entry.Observation;
import org.openehr.rm.composition.content.navigation.Section;
import org.openehr.rm.datastructure.history.History;
import org.openehr.rm.datastructure.history.PointEvent;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datastructure.itemstructure.ItemSingle;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.datastructure.itemstructure.ItemTree;
import org.openehr.rm.datastructure.itemstructure.representation.Cluster;
import org.openehr.rm.datastructure.itemstructure.representation.Item;
import org.openehr.rm.datatypes.basic.DataValue;
import org.openehr.rm.datatypes.basic.DvBoolean;
import org.openehr.rm.datatypes.quantity.DvAmount;
import org.openehr.rm.datatypes.quantity.DvOrdinal;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.text.DvCodedText;

import si.pint.archetypes.model.ArcheDataPath;

/**
 * Helper methods for converting data (in XML form) back to archetype object model. 
 * 
 * @author Blaz
 *
 */
public class Data2RmUtilsOLD {

	//return value for method 'findConstraintObjectFromPath'
	private static CObject cObject;
	
	//return value for method 'findNodeInSkeletonTree'
	private static Object nodeInTree;
	
	//if method found target node (for delete method)
	private static Boolean foundDelete;
	
	//if method found target node (for append method)
	public static Boolean foundAppend;	
	
	
	/**
	 * Search user input map and returns data object 
	 * 
	 * @param attId
	 * @param userInputsMap
	 * @return               DvBoolean, DvQuantity, DvCodedText, ...
	 */
	private static Object findInUserInputs(String attId, Map userInputsMap) {
		if (attId == null || attId.equals("") || userInputsMap == null)
			return null;
		Iterator it = userInputsMap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (key.equals(attId))
				return userInputsMap.get(key); 
		}
		return null;
	}

	
	/**
	 * Browse object tree and return right object depending on the path.
	 * 
	 * @param path
	 * @param archetypeObj
	 * @return
	 */
	public static CObject findConstraintObjectFromPath(String path, Archetype archetypeObj) {
		cObject = null;
		findNode(archetypeObj.getDefinition(), path, 0);
		return cObject;
	}
	
	private static void findNode(CObject object, String path, int level) {
		if (object == null || path == null)
			return;
		
//		System.out.println(object.path() + ", " + level);
//		String buff = "";
//		for (int i = 0; i < level; i++)
//			buff = buff + "   ";
//		System.out.println(buff + object.path());
//		System.out.println("");
		
		if (object.path().equals(path)) {
			cObject = object;
			return;
		}
				
		if (object instanceof CComplexObject) {
			CComplexObject compObject = (CComplexObject) object;
			level++;
			
			Iterator<CAttribute> itAtt = compObject.getAttributes().iterator();
			CAttribute cAttributeOut = null;
			while (itAtt.hasNext()) {
				CAttribute cAttribute = itAtt.next();
//				System.out.println(cAttribute.path());
				if (cAttribute.path().equals(path)) {
					//find first child
					List children = cAttribute.getChildren();
					if (children != null && children.size() > 0)
						cObject = (CObject) children.get(0);
//					nodeFound = cAttribute; 
					
					break;				
				}						
				
				Iterator<CObject> itChildren = cAttribute.getChildren().iterator();
				while (itChildren.hasNext()) {
					CObject cObject = itChildren.next();
					findNode(cObject, path, (level + 1));
				}
			}			
		}
//		else if (object instanceof CPrimitiveObject) {
//			CPrimitiveObject primitiveObj = (CPrimitiveObject) object;
//			System.out.println(buff + "primitivni: " + primitiveObj.getRmTypeName() + ", " + primitiveObj.getItem().getType());
//		}		
//		else if (object instanceof CCodePhrase) {
//			CCodePhrase codePhrase = (CCodePhrase) object;
//			System.out.println(buff + "koda: " + codePhrase.getRmTypeName());
//			Iterator it = codePhrase.getCodeList().iterator();
//			while (it.hasNext()) {
//				String object2 = (String) it.next();
//				System.out.println(buff + object2);				
//			}
//		}
//		else if (object instanceof CDvQuantity) {
//			CDvQuantity dvQuantity = (CDvQuantity) object;
//			System.out.println(buff + "kolicina: " + dvQuantity.getRmTypeName() + ", default: " + (dvQuantity.getDefaultValue() != null ? dvQuantity.getDefaultValue().getMagnitude().doubleValue() : ""));
//		}
//		else  {
//			System.out.println(buff + "Nepoznan tip: " + object.getClass().getName());
//		}
		
		return;
	}

	
	
	/**
	 * Search through generated tree and look for element at given path.
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @return
	 */
	public static Object deleteGeneratedObject(String path, Object minimumSkeleton) {
		foundDelete = null;
		findGeneratedObjectInTreeAndDelete(path, minimumSkeleton, 0, null);
		return minimumSkeleton;
	}
	
	/**
	 * 
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @return
	 */
	private static void findGeneratedObjectInTreeAndDelete(String path, Object minimumSkeleton, int level, Object parent) {
			
			if (minimumSkeleton == null || path == null || path.equals(""))
				return;
			
			String pathShortExt = findAttNameFromPath(path, level);
			String pathShort = extractAttFromNode(pathShortExt);
			
			Boolean isSecondLast = isSecondLastNodeOnPath(path, level);
			String nextChildExt = findAttNameFromPath(path, level + 1);
			String nextChild = extractAttFromNode(nextChildExt);

			
//			if (minimumSkeleton instanceof Locatable)
//				System.out.println("skeleton: " + ((Locatable) minimumSkeleton).atNode() + ((Locatable) minimumSkeleton).getArchetypeNodeId() + ", pathShort: " + pathShort);

	    	if (minimumSkeleton instanceof Section) {
	    		Section sec = (Section) minimumSkeleton;
	    		
	    		if (pathShort.equals(sec.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
//	    				generatedObject = sec;
	    				return;
	    			}	    			
	    		}
	    		Iterator it = sec.getItems().iterator();
	    		level = level + 1;
	    		while (it.hasNext()) {
	    			ContentItem ci = (ContentItem) it.next();
					findGeneratedObjectInTreeAndDelete(path, ci, level, minimumSkeleton);
				}	    		
	    	}
	    	else if (minimumSkeleton instanceof Instruction) {
	    		Instruction in = (Instruction) minimumSkeleton;
	    		
	    		if (pathShort.equals(in.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
//	    				generatedObject = in;
	    				return;
	    			}	    			
	    		}	 
	    		Iterator it = in.getActivities().iterator();
	    		level = level + 1;
	    		while (it.hasNext()) {
					Activity object = (Activity) it.next();
					findGeneratedObjectInTreeAndDelete(path, object, level, minimumSkeleton);				
				}	    		
	    	}
	    	else if (minimumSkeleton instanceof Observation) {
	    		Observation obs = (Observation) minimumSkeleton;
	    		
	    		if (pathShort.equals(obs.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
//	    				generatedObject = obs;
	    				return;
	    			}	    			
	    		}
	    		History history = obs.getData();
	    		if (history != null && history.getEvents() != null) {
	    			Iterator it = history.getEvents().iterator();
	    			level = level + 1;
	    			while (it.hasNext()) {
						PointEvent<ItemStructure> event = (PointEvent) it.next();
						findGeneratedObjectInTreeAndDelete(path, event, level, minimumSkeleton);
					}
	    		}	    		
	    	}
	    	else if (minimumSkeleton instanceof Composition) {
	    		Composition comp = (Composition) minimumSkeleton;
	    		
	    		if (pathShort.equals(comp.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
//	    				generatedObject = in;
	    				return;
	    			}	    			
	    		}	 
	    		level = level + 1;
				findGeneratedObjectInTreeAndDelete(path, comp.getContext().getOtherContext(), level, minimumSkeleton);
				if (foundDelete != null && foundDelete.booleanValue()) 
					return;
				
				level = level - 1; //actually we stay on same lavel!
				if (comp.getContent() != null) {
					Iterator<ContentItem> it = comp.getContent().iterator();
					while (it.hasNext()) {
						ContentItem contentItem = it.next();
						findGeneratedObjectInTreeAndDelete(path, contentItem, level, minimumSkeleton);
					}
				}
	    	}	
	    	else if (minimumSkeleton instanceof Evaluation) {
	    		Evaluation eval = (Evaluation) minimumSkeleton;
	    		
	    		if (pathShort.equals(eval.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
//	    				generatedObject = in;
	    				return;
	    			}	    			
	    		}	 
	    		
	    		level = level + 1;	    		
	    		if (eval.getData() != null)
	    			findGeneratedObjectInTreeAndDelete(path, eval.getData(), level, minimumSkeleton);				    		
	    	}	    	
	    	else if (minimumSkeleton instanceof Activity) {
	    		Activity ac = (Activity) minimumSkeleton;
	    		
	    		if (pathShort.equals(ac.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
//	    				generatedObject = ac;
	    				return;
	    			}
		    		ItemTree itemTree = (ItemTree) ac.getDescription();
		    		Iterator<Item> it = itemTree.getItems().iterator();
		    		level = level + 1;
		    		while (it.hasNext()) {
						Item item = it.next();
						findGeneratedObjectInTreeAndDelete(path, item, level, minimumSkeleton);				
					}	    			
	    		}
	    		
	    	}
	    	else if (minimumSkeleton instanceof ItemList) {
	    		ItemList iList = (ItemList) minimumSkeleton;
	    		
	    		if (pathShort.equals(iList.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
	    				
	    				return;
	    			}
		    		if (iList.getItems() != null) {
		    			Iterator it = iList.getItems().iterator();
		    			level = level + 1;
		    			while (it.hasNext()) {
		    				org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) it.next();
		    				findGeneratedObjectInTreeAndDelete(path, el, level, minimumSkeleton);
						}
		    		}	    			
	    		}	    		
	    	}
	    	else if (minimumSkeleton instanceof ItemTree) {
	    		ItemTree tree = (ItemTree) minimumSkeleton;
	    		
	    		if (pathShort.equals(tree.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
	    				
	    				return;
	    			}
		    		if (tree.getItems() != null) {
		    			Iterator it = tree.getItems().iterator();
		    			level = level + 1;
		    			while (it.hasNext()) {
		    				Item item = (Item) it.next();
		    				findGeneratedObjectInTreeAndDelete(path, item, level, minimumSkeleton);
						}
		    		}	    			
	    		}	    		
	    	}	    	
	    	else if (minimumSkeleton instanceof Item) {
	 		
	    		if (minimumSkeleton instanceof Cluster) {
	    			Cluster c = (Cluster) minimumSkeleton;
	    			
		    		if (pathShort.equals(c.getArchetypeNodeId())) {
		    			if (isSecondLast != null && isSecondLast.booleanValue()) {
		    				
//		    				generatedObject = c;
		    				return;
		    			}
		        		Iterator<Item> it = c.getItems().iterator();
		        		level = level + 1;
		        		while (it.hasNext()) {
							Item item = it.next();
							findGeneratedObjectInTreeAndDelete(path, item, level, minimumSkeleton);
						}		    			
		    		}	    			
	        		

	    		}
	    		else if (minimumSkeleton instanceof org.openehr.rm.datastructure.itemstructure.representation.Element) {
	    			org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) minimumSkeleton;
   			
		    		if (pathShort.equals(el.getArchetypeNodeId())) {
		    			if (isSecondLast != null && isSecondLast.booleanValue() || el.getValue() instanceof DvCodedText) { //izjema!! DvCodedText ima se en dodaten podelement!
		    				//brisanje!!!!
		    				el.set("/" + nextChild, null);
		    				foundDelete = Boolean.TRUE;
		    				return;
		    			}	
//		        		if (el.getValue() instanceof DvBoolean) {
//		    			
//		    		    }
//		    			else if (el.getValue() instanceof DvQuantity) {
//		    				DvQuantity quant = (DvQuantity) el.getValue();
//		    			}
//		    			else if (el.getValue() instanceof DvOrdinal) {
//		    				DvOrdinal ord = (DvOrdinal) el.getValue();
//
//		    			}     		
//		    			else if (el.getValue() instanceof DvAmount) {
//		    				DvAmount amount = (DvAmount) el.getValue();
//
//		    			}  
//		    			else if (el.getValue() instanceof DvCodedText) {
//		    				DvCodedText codedText = (DvCodedText) el.getValue();
//		    			}    		
//		    			else {
//		    				System.out.println("other: ?? " + el.getValue().getClass().getName());
//		    			}    			

//		    			generatedObject = el;
		    			
		    		}
	    		}    	
	    	}	    	
	    	else if (minimumSkeleton instanceof PointEvent<?>) {
	    		PointEvent<?> event = (PointEvent) minimumSkeleton;
	    		
	    		if (pathShort.equals(event.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
	    				//TODO: tukaj bi se moralo brisati
	    				//erase element ------->
	    				//parent.removeChild("/" + pathShortExt);
	    				
//	    				generatedObject = event;
	    				return;
	    			}
	    			level = level + 1;
		    		findGeneratedObjectInTreeAndDelete(path, event.getData(), level, minimumSkeleton);
		    		if (foundDelete != null && foundDelete.booleanValue()) 
		    			return;
		    		findGeneratedObjectInTreeAndDelete(path, event.getState(), level, minimumSkeleton);	    			
	    		}
	    	}
	    	else if (minimumSkeleton instanceof ItemSingle) {
	    		ItemSingle item = (ItemSingle) minimumSkeleton;
	    		
	    		if (pathShort.equals(item.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
	    				//TODO: tukaj bi se moralo brisati
//	    				generatedObject = item;
	    				return;
	    			}
	    			level = level + 1;
	    			findGeneratedObjectInTreeAndDelete(path, item.getItem(), level, minimumSkeleton);
	    		}
	    	}
	    	return;		
			
	}

	
	/**
	 * Searches right section path depending on level.
	 * e.g. path: /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value
	 *      level: 2
	 *      result: data[at0001]
	 * 
	 * @param path
	 * @param level
	 * @return
	 */
	public static String findAttNameFromPath(String path, int level) {
		if (path == null || path.equals(""))
			return null;
		
		String[] nodes = path.split("/");
		if (nodes == null || (nodes.length - 1) <= level)
			return null;
		
		return nodes[level + 1];
	}
	
	/**
	 * Searches right section path depending on level.
	 * e.g. path: /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value
	 *      level: 2
	 *      result: data[at0001]
	 * 
	 * @param path
	 * @param level
	 * @return
	 */
	public static String findSubPath(String path, int depth) {
		if (path == null || path.equals(""))
			return null;
		
		String[] nodes = path.split("/");
		if (nodes == null || (nodes.length - 1) <= depth)
			return null;
		
		String subPath = "";
		for (int i = 0; i < depth + 1; i++)
			subPath = subPath + "/" + nodes[i + 1];
		
		return subPath;
	}	
	
	
	/**
	 * Extract node from path.
	 * 
	 * @param attribute
	 * @return
	 */
	public static String extractAttFromNode(String attribute) {
		if (attribute != null && attribute.contains("[")) {
			attribute = attribute.substring(attribute.indexOf("[") + 1, attribute.indexOf("]"));
		}
		return attribute;
		
	}
	
	/**
	 * Is given attribute (node) last on given path.
	 * 
	 * @param path
	 * @param att
	 * @return
	 */
	private static Boolean isLastAttOnPath(String path, String att) {
		if (path == null || att == null || path.equals("") || att.equals(""))
			return null;
		
		int nextIndex = path.indexOf("[", path.indexOf(att) + 1);
		if (nextIndex > 0)
			return Boolean.FALSE;
		else
			return Boolean.TRUE;
		
	}
	
	
	/**
	 * Is given attribute (node) last on given path.
	 * 
	 * @param path
	 * @param att
	 * @return
	 */
	public static Boolean isLastNodeOnPath(String path, int level) {
		if (path == null || path.equals("") || level < 0)
			return null;
		
		String[] pathArray = path.split("/");
		if (pathArray != null && pathArray.length - 1 == (level + 1))
			return Boolean.TRUE;
		
		return Boolean.FALSE;
	}
	
	
	
	/**
	 * Is given attribute (node) second last on given path.
	 * 
	 * @param path
	 * @param att
	 * @return
	 */
	private static Boolean isSecondLastAttOnPath(String path, String att) {
		if (path == null || att == null || path.equals("") || att.equals(""))
			return null;
		
		int firstAttIndex = path.indexOf("[", path.indexOf(att) + 1);
		//at least one more
		if (firstAttIndex > 0) {
			int secondAttIndex = path.indexOf("[", firstAttIndex + 1);
			if (secondAttIndex == -1)
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
		
	}
	
	
	/**
	 * Is given node second last on given path.
	 * 
	 * @param path
	 * @param att
	 * @return
	 */
	public static Boolean isSecondLastNodeOnPath(String path, int level) {
		if (path == null || path.equals("") || level < 0)
			return null;
		
		String[] slices = path.split("/");
		if (slices == null || ((slices.length - 1) < (level + 1)))
			return null;
				
		if ((slices.length - 2) == (level + 1))
			return Boolean.TRUE;		
		
		return Boolean.FALSE;
	}
	
	
	/**
	 * e.g. input: /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value, 1
	 *      out: /events[at0028]/data[at0001]/item[at0004]/value
	 * 
	 * @param path
	 * @param pathShortExt
	 * @return
	 */
	public static String findSubpathFromNode(String path, int depth) {
		if (path == null || path.equals("") || depth < 0 )
			return null;
		String[] slices = path.split("/");
		
		if (slices == null || slices.length - 1 < depth + 1)
			return null;
		
		String retPath = "";
		for (int i = depth + 1; i < slices.length; i++) {
			retPath = retPath + "/" + slices[i]; 
		}
		return retPath;
	}
	
	/**
	 * e.g. input:  /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value
	 * 		output: /data[at0002]/events[at0028]/data[at0001]/item[at0004]
	 * 
	 * @param path
	 * @return
	 */
	public static String findLastAttOnPath(String path) {
		if (path == null || path.equals(""))
			return null;
		
		String[] slices = path.split("/");
		if (slices == null)
			return null;
		
		String retPath = "";
		for (int i = 1; i < slices.length; i++) {
			if (slices[i].contains("["))
				retPath = retPath + "/" + slices[i];

		}
		
		return retPath;
						
	}
	
	
	/**
	 * Look for last attribute on path and then move down to first 'value'.
	 * 
	 * e.g. input:  /data[at0002]/events[at0028]/data[at0029]/item[at0030]/value/defining_code
	 * 				/data[at0002]/events[at0028]/data[at0029]/item[at0030]
	 *      output: /item[at0030]/value
	 * @param path
	 * @return
	 */
	public static String findFirstValueNode(String wholePath, String lastAttPath) {
		if (wholePath == null || wholePath.equals("") || lastAttPath == null || lastAttPath.equals(""))
			return null;
		
		String[] wholePathSlice = wholePath.split("/");
		String[] lastAttPathSlice = lastAttPath.split("/");
		
		String subPath = "/" + lastAttPathSlice[lastAttPathSlice.length - 1];
		for (int i = lastAttPathSlice.length; i < wholePathSlice.length; i++) {
			subPath = subPath + "/" + wholePathSlice [i];
			if (wholePathSlice[i].equals("value")) {
				return subPath;
			}
		}
		return null;
	}
	
	
	/**
	 * Is 
	 * 
	 * e.g. input:  /data[at0002]/events[at0028]/data[at0029]/item[at0030]/value/defining_code
	 * 				data[at0029]
	 *      output: true
	 * @param path
	 * @return
	 */
	public static boolean isAttOnPath(String wholePath, String attName) {
		if (StringUtils.isEmpty(wholePath) || StringUtils.isEmpty(attName))
			return false;
		
		String[] wholePathSlice = wholePath.split("/");
		
		for (int i = 1; i < wholePathSlice.length - 1; i++) {
			int startIdx = wholePathSlice[i].indexOf('[');
			if (startIdx == -1)
				return false;
			int endIdx = wholePathSlice[i].indexOf(']', startIdx);
			if (endIdx == -1)
				return false;
			if (wholePathSlice[i].substring(startIdx + 1, endIdx).equals(attName))
				return true;
		}
		return false;
	}
	
	
	/**
	 * input:  /data[at0001]/events[at0002]/data[at0003]/items[at0127]/items[openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1]/items[at0032]/items[at0004]/value
	 * output: openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1
	 * 
	 * @param path
	 * @return
	 */
	public static String findLastArchetypeIdFromPath(String path) {
		if (StringUtils.isEmpty(path))
			return null;
		
		String[] array = path.split("/");
		for (int i = array.length -1; i > 0; i--) {
			int index = array[i].indexOf("[");
			if (index == -1)
				continue;
			
			String att = array[i].substring(index + 1, array[i].indexOf("]", index));
			if (!att.matches("at\\d\\d\\d\\d")) {
				return att;
			}
				
		}
			
		return null;
	}

	
	
	/**
	 * Find node with given 'path' in 'minimumSkeleton' and set 'dvData' to it.
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @param dvData
	 * @param level
	 */
	public static void appendDataToTree(String path, Object minimumSkeleton, Object dvData, int level) throws IllegalArgumentException {
		if (minimumSkeleton == null || path == null || path.equals(""))
			return;
		
		String pathShortExt = findAttNameFromPath(path, level);
		if (pathShortExt == null)
			return;
		String pathShort = extractAttFromNode(pathShortExt);

		//Boolean isLast = isLastNodeOnPath(path, level);
		Boolean isSecondLast = isSecondLastNodeOnPath(path, level);
		
//		if (minimumSkeleton instanceof Locatable)
//			System.out.println("skeleton: " + ((Locatable) minimumSkeleton).atNode() + ((Locatable) minimumSkeleton).getArchetypeNodeId() + ", pathShort: " + pathShort);

    	if (minimumSkeleton instanceof Section) {
    		Section sec = (Section) minimumSkeleton;
    		
    		if (pathShort.equals(sec.getArchetypeNodeId())) { 
    			if (isSecondLast != null && isSecondLast.booleanValue()) { //we reached parent node (time to append child)
    				sec.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				foundAppend = Boolean.TRUE;
    				return;
    			}	    			
    		}
    		Iterator it = sec.getItems().iterator();
    		level = level + 1;
    		while (it.hasNext()) {
				ContentItem object = (ContentItem) it.next();
				appendDataToTree(path, object, dvData, level);
			}	    		
    	}
    	else if (minimumSkeleton instanceof Instruction) {
    		Instruction in = (Instruction) minimumSkeleton;
    		
    		if (pathShort.equals(in.getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {
    				in.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				foundAppend = Boolean.TRUE;
    				return;
    			}	    			
    		}	 
    		Iterator it = in.getActivities().iterator();
    		level = level + 1;
    		while (it.hasNext()) {
				Activity object = (Activity) it.next();
				appendDataToTree(path, object, dvData, level);				
			}	    		
    	}
    	else if (minimumSkeleton instanceof Observation) {
    		Observation obs = (Observation) minimumSkeleton;
    		
    		if (pathShort.equals(obs.getData().getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {
    				//obs.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				System.out.println("TODO!!!!");
    				return;
    			}	    			
    		}
    		History history = obs.getData();
    		if (history != null && history.getEvents() != null) {
    			Iterator it = history.getEvents().iterator();
    			level = level + 1;
    			while (it.hasNext()) {
					PointEvent<ItemStructure> event = (PointEvent) it.next();
					appendDataToTree(path, event, dvData, level);
				}
    		}	    		
    	}
    	else if (minimumSkeleton instanceof Composition) {
    		Composition comp = (Composition) minimumSkeleton;
    		
    		if (pathShort.equals(comp.getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {
    				//obs.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				System.out.println("TODO!!!!");
    				return;
    			}	    			
    		}
    		level = level + 1;
			appendDataToTree(path, comp.getContext().getOtherContext(), dvData, level);
    		if (foundAppend != null && foundAppend.booleanValue()) 
    			return;			
			level = level - 1;
			if (comp.getContent() != null) {
				Iterator<ContentItem> it = comp.getContent().iterator();
				while (it.hasNext()) {
					ContentItem contentItem = it.next();
					appendDataToTree(path, contentItem, dvData, level);
				}
			}			
  			    		
    	}  
    	else if (minimumSkeleton instanceof Evaluation) {
    		Evaluation eval = (Evaluation) minimumSkeleton;
    		
    		if (pathShort.equals(eval.getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {
    				//obs.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				System.out.println("TODO!!!!");
    				return;
    			}	    			
    		}
    		level = level + 1;
			appendDataToTree(path, eval.getData(), dvData, level);
  			    		
    	}      	
    	else if (minimumSkeleton instanceof Activity) {
    		Activity ac = (Activity) minimumSkeleton;
    		
    		if (pathShort.equals(ac.getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {
    				ac.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				foundAppend = Boolean.TRUE;
    				return;
    			}
	    		ItemTree itemTree = (ItemTree) ac.getDescription();
	    		Iterator<Item> it = itemTree.getItems().iterator();
	    		level = level + 1;
	    		while (it.hasNext()) {
					Item item = it.next();
					appendDataToTree(path, item, dvData, level);				
				}	    			
    		}
    		
    	}
    	else if (minimumSkeleton instanceof ItemList) {
    		ItemList iList = (ItemList) minimumSkeleton;
    		
    		if (pathShort.equals(iList.getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {
    				//iList.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				iList.getItems().add((org.openehr.rm.datastructure.itemstructure.representation.Element) dvData);
    				foundAppend = Boolean.TRUE;
    				return;
    			}
	    		if (iList.getItems() != null) {
	    			Iterator it = iList.getItems().iterator();
	    			level = level + 1;
	    			while (it.hasNext()) {
	    				org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) it.next();
	    				appendDataToTree(path, el, dvData, level);
					}
	    		}	    			
    		}	    		
    	}
    	else if (minimumSkeleton instanceof ItemTree) {
    		ItemTree tree = (ItemTree) minimumSkeleton;
    		
    		if (pathShort.equals(tree.getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {
    				//tree.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				tree.getItems().add((Item) dvData);
    				foundAppend = Boolean.TRUE;
    				return;
    			}
	    		if (tree.getItems() != null) {
	    			Iterator it = tree.getItems().iterator();
	    			level = level + 1;
	    			while (it.hasNext()) {
	    				Item item = (Item) it.next();
	    				appendDataToTree(path, item, dvData, level);
					}
	    		}	    			
    		}	    		
    	}    	
    	else if (minimumSkeleton instanceof Item) {
 		
    		if (minimumSkeleton instanceof Cluster) {
    			Cluster c = (Cluster) minimumSkeleton;
    			
	    		if (pathShort.equals(c.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
	    				//c.set("/" + findAttNameFromPath(path, level + 1), dvData);
	    				c.getItems().add((Item) dvData);
	    				return;
	    			}
	        		Iterator<Item> it = c.getItems().iterator();
	        		level = level + 1;
	        		while (it.hasNext()) {
						Item item = it.next();
						appendDataToTree(path, item, dvData, level);
					}		    			
	    		}	    			
        		

    		}
    		else if (minimumSkeleton instanceof org.openehr.rm.datastructure.itemstructure.representation.Element) {
    			org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) minimumSkeleton;
			
	    		if (pathShort.equals(el.getArchetypeNodeId())) {
	    			if (isSecondLast != null && isSecondLast.booleanValue()) {
	    				
	    				el.set("/" + findAttNameFromPath(path, level + 1), dvData);
	    				foundAppend = Boolean.TRUE;
	    				
//	    				if (dvData instanceof DataValue)
//	    					el.setValue((DataValue) dvData);
//		        		if (el.getValue() instanceof DvBoolean) {
//		        			
//		        		}
//		        		else if (el.getValue() instanceof DvQuantity) {
//		        			DvQuantity quant = (DvQuantity) el.getValue();
//		        		}
//		        		else if (el.getValue() instanceof DvOrdinal) {
//		        			DvOrdinal ord = (DvOrdinal) el.getValue();
//
//		        		}     		
//		        		else if (el.getValue() instanceof DvAmount) {
//		        			DvAmount amount = (DvAmount) el.getValue();
//
//		        		}  
//		        		else if (el.getValue() instanceof DvCodedText) {
//		        			DvCodedText codedText = (DvCodedText) el.getValue();
//		        		}    		
//		        		else {
//		        			System.out.println("other: ?? " + el.getValue().getClass().getName());
//		        		}    			
				
//	    				generatedObject = el;
	    				return;
	    			}	
	    		}
	    		
    		}    	
    	}
    	else if (minimumSkeleton instanceof PointEvent<?>) {
    		PointEvent<?> event = (PointEvent) minimumSkeleton;
    		
    		if (pathShort.equals(event.getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {
    				event.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				foundAppend = Boolean.TRUE;
    				return;
    			}
    			level = level + 1;
	    		appendDataToTree(path, event.getData(), dvData, level);
	    		if (foundAppend != null && foundAppend.booleanValue()) 
	    			return;			    		
	    		appendDataToTree(path, event.getState(), dvData, level);	    			
    		}
    	}
    	else if (minimumSkeleton instanceof ItemSingle) {
    		ItemSingle item = (ItemSingle) minimumSkeleton;
    		
    		if (pathShort.equals(item.getArchetypeNodeId())) {
    			if (isSecondLast != null && isSecondLast.booleanValue()) {

    				item.set("/" + findAttNameFromPath(path, level + 1), dvData);
    				if (!(item.itemAtPath("/" + findAttNameFromPath(path, level + 1)).equals(dvData)))
    					throw new IllegalArgumentException("Trying to add input data to unsupported node/type!");
    				foundAppend = Boolean.TRUE;
    				return;
    			}
    			level = level + 1;
    			appendDataToTree(path, item.getItem(), dvData, level);
    		}
    	}    	
    	return;			
		
		
	}

	
	/**
	 * Finds node with given path. If not found returns null.
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @param archetypeObj
	 */
	public static Object findNodeToGenerate(String pathShortExt, Object minimumSkeleton) {
		nodeInTree = null;
		findNodeInSkeletonTree(pathShortExt, minimumSkeleton, 0);
		return nodeInTree;
	}
	
	
	private static void findNodeInSkeletonTree(String path, Object minimumSkeleton, int level) {
		if (minimumSkeleton == null || path == null || path.equals(""))
			return;
		
		String pathShortExt = findAttNameFromPath(path, level);
		if (pathShortExt == null)
			return;
		
		String pathShort = extractAttFromNode(pathShortExt);
		
		Boolean isLastNode = isLastNodeOnPath(path, level);
		
//		if (minimumSkeleton instanceof Locatable)
//			System.out.println("skeleton: " + ((Locatable) minimumSkeleton).atNode() + ((Locatable) minimumSkeleton).getArchetypeNodeId() + ", pathShort: " + pathShort);

    	if (minimumSkeleton instanceof Section) {
    		Section sec = (Section) minimumSkeleton;
    		
    		if (pathShort.equals(sec.getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = sec;
    				return;
    			}	    			
    		}
    		Iterator it = sec.getItems().iterator();
    		level = level + 1;
    		while (it.hasNext()) {
    			ContentItem object = (ContentItem) it.next();
				findNodeInSkeletonTree(path, object, level);
			}	    		
    	}
    	else if (minimumSkeleton instanceof Instruction) {
    		Instruction in = (Instruction) minimumSkeleton;
    		
    		if (pathShort.equals(in.getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = in;
    				return;
    			}	    			
    		}	 
    		Iterator it = in.getActivities().iterator();
    		level = level + 1;
    		while (it.hasNext()) {
				Activity object = (Activity) it.next();
				findNodeInSkeletonTree(path, object, level);				
			}	    		
    	}
    	else if (minimumSkeleton instanceof Observation) {
    		Observation obs = (Observation) minimumSkeleton;
    		
    		if (pathShort.equals(obs.getData().getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = obs;
    				return;
    			}	    			
    		}
    		History history = obs.getData();
    		if (history != null && history.getEvents() != null) {
    			Iterator it = history.getEvents().iterator();
    			level = level + 1;
    			while (it.hasNext()) {
					PointEvent<ItemStructure> event = (PointEvent) it.next();
					findNodeInSkeletonTree(path, event, level);
				}
    		}	    		
    	}
    	else if (minimumSkeleton instanceof Composition) {
    		Composition comp = (Composition) minimumSkeleton;
    		
    		if (isLastNode != null && isLastNode.booleanValue()) {
    			if (pathShort.equals(comp.getArchetypeNodeId())) {
    				nodeInTree = comp;
    				return;
    			}
    			else if (pathShort.equals("context")) { //'context' node hasn't got archetypeNodeID info
    				nodeInTree = comp.getContext();
    				return;    				
    			}
    		}	 
    		level = level + 1;
			findNodeInSkeletonTree(path, comp.getContext().getOtherContext(), level);
			if (nodeInTree != null) return; //if found return
			
			level = level - 1;
			if (comp.getContent() != null) {
				Iterator<ContentItem> it = comp.getContent().iterator();
				while (it.hasNext()) {
					ContentItem contentItem = it.next();
					findNodeInSkeletonTree(path, contentItem, level);
				}
			}			
	    		
    	}    	
    	else if (minimumSkeleton instanceof Evaluation) {
    		Evaluation eval = (Evaluation) minimumSkeleton;
    		
    		if (pathShort.equals(eval.getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = eval;
    				return;
    			}	    			
    		}	 
    		level = level + 1;
			findNodeInSkeletonTree(path, eval.getData(), level);				
	    		
    	}    	
    	else if (minimumSkeleton instanceof Activity) {
    		Activity ac = (Activity) minimumSkeleton;
    		
    		if (pathShort.equals(ac.getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = ac;
    				return;
    			}
	    		ItemTree itemTree = (ItemTree) ac.getDescription();
	    		Iterator<Item> it = itemTree.getItems().iterator();
	    		level = level + 1;
	    		while (it.hasNext()) {
					Item item = it.next();
					findNodeInSkeletonTree(path, item, level);				
				}	    			
    		}
    		
    	}
    	else if (minimumSkeleton instanceof ItemList) {
    		ItemList iList = (ItemList) minimumSkeleton;
    		
    		if (pathShort.equals(iList.getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = iList;
    				return;
    			}
	    		if (iList.getItems() != null) {
	    			Iterator it = iList.getItems().iterator();
	    			level = level + 1;
	    			while (it.hasNext()) {
	    				org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) it.next();
	    				findNodeInSkeletonTree(path, el, level);
					}
	    		}	    			
    		}	    		
    	}
    	else if (minimumSkeleton instanceof ItemTree) {
    		ItemTree tree = (ItemTree) minimumSkeleton;
    		
    		if (pathShort.equals(tree.getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = tree;
    				return;
    			}
	    		if (tree.getItems() != null) {
	    			Iterator it = tree.getItems().iterator();
	    			level = level + 1;
	    			while (it.hasNext()) {
	    				Item item = (Item) it.next();
	    				findNodeInSkeletonTree(path, item, level);
					}
	    		}	    			
    		}	    		
    	}    	
    	else if (minimumSkeleton instanceof Item) {
 		
    		if (minimumSkeleton instanceof Cluster) {
    			Cluster c = (Cluster) minimumSkeleton;
    			
	    		if (pathShort.equals(c.getArchetypeNodeId())) {
	    			if (isLastNode != null && isLastNode.booleanValue()) {
	    				nodeInTree = c;
	    				return;
	    			}
	        		Iterator<Item> it = c.getItems().iterator();
	        		level = level + 1;
	        		while (it.hasNext()) {
						Item item = it.next();
						findNodeInSkeletonTree(path, item, level);
					}		    			
	    		}	    			
        		

    		}
    		else if (minimumSkeleton instanceof org.openehr.rm.datastructure.itemstructure.representation.Element) {
    			org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) minimumSkeleton;
			
	    		if (pathShort.equals(el.getArchetypeNodeId())) {
	    			if (isLastNode != null && isLastNode.booleanValue()) {
	    				nodeInTree = el;   			
	    				return;
	    			}
//	    			findNodeInSkeletonTree(path, el.getValue(), ++level);
	    	    	if (el.getValue() instanceof DataValue) {
	    	    		if (el.getValue() instanceof DvBoolean) {
	    					nodeInTree = el.getValue();   			
	    					return;    			
	    	    		}
	    	    		else if (el.getValue() instanceof DvQuantity) {
	    	    			DvQuantity quant = (DvQuantity) el.getValue();
	    					nodeInTree = el.getValue();   			
	    					return;
	    				}
	    	    		else if (el.getValue() instanceof DvOrdinal) {
	    	    			DvOrdinal ord = (DvOrdinal) el.getValue();
	    					nodeInTree = el.getValue();   			
	    					return;
	    	    		}     		
	    	    		else if (el.getValue() instanceof DvAmount) {
	    	    			DvAmount amount = (DvAmount) el.getValue();
	    					nodeInTree = el.getValue();   			
	    					return;
	    	    		}  
	    	    		else if (el.getValue() instanceof DvCodedText) {
	    	    			DvCodedText codedText = (DvCodedText) el.getValue();

	    	    			Boolean isLastNodeChild = isLastNodeOnPath(path, level + 1); //is child last node?
	    	    			Boolean isSecondLastNodeChild = isSecondLastNodeOnPath(path, level + 1);//is child a second last node?
	    	    			if (isLastNodeChild != null && isLastNodeChild.booleanValue()) {	    	    				
	    	    				nodeInTree = codedText;   			
	    	    				return;
	    	    			}
	    	    			else if (isSecondLastNodeChild != null && isSecondLastNodeChild.booleanValue()) {
	    	    				nodeInTree = codedText.getDefiningCode();   			
	    	    				return;	    	    				
	    	    			}
	    	    		}    		
	    	    		else {
	    	    			System.out.println("other: ?? " + el.getValue().getClass().getName());
	    	    		}     		
	    	    	}	    			
	    			
	    		}
	    		
	    		
    		}    	
    	}
    	else if (minimumSkeleton instanceof PointEvent<?>) {
    		PointEvent<?> event = (PointEvent) minimumSkeleton;
    	
    		if (pathShort.equals(event.getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = event;
    				return;
    			}
    			level = level + 1;
    			findNodeInSkeletonTree(path, event.getData(), level);
    			findNodeInSkeletonTree(path, event.getState(), level);	    			
    		}
    	}
    	else if (minimumSkeleton instanceof ItemSingle) {
    		ItemSingle item = (ItemSingle) minimumSkeleton;
    	
    		if (pathShort.equals(item.getArchetypeNodeId())) {
    			if (isLastNode != null && isLastNode.booleanValue()) {
    				nodeInTree = item;
    				return;
    			}
    			level = level + 1;
    			findNodeInSkeletonTree(path, item.getItem(), level);
    		}
    	}        	
    	return;				
	}
	
	
	
	/**
	 * Find List in which 'lastAtt' is present (in case of nested Clusters)
	 * 
	 * @param listWithAtt
	 * @param lastAtt
	 * @return
	 */
	public static void deleteRedundantClusterElements(Object clusterObj, String path, int depth) {
		if (clusterObj == null || !(clusterObj instanceof Cluster) ||StringUtils.isEmpty(path))
			return;
		
		List<Item> itemList = ((Cluster) clusterObj).getItems();
		if (itemList == null)
			return;
		
		List<Item> newList = new ArrayList<Item>();
		Iterator<Item> itC = itemList.iterator();
		while (itC.hasNext()) {
			Item item = itC.next();
			if (isAttOnPath(path, item.getArchetypeNodeId())) {
				newList.add(item);
				if (item instanceof Cluster)
					deleteRedundantClusterElements(item, path, depth);
			}
		}
		itemList.clear();
		itemList.addAll(newList);
		return;
	}


	public static Object deleteAllNodesNotInPath(Map userInputsMap, Object rmObject, Archetype archetype) {
		List<String> list = Rm2XmlUtils.getAllArchetypeNodesList(archetype.getDefinition(), archetype);
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String path = it.next();
			Iterator itUserkeys = userInputsMap.keySet().iterator();
			boolean deletePath = true;
			while (itUserkeys.hasNext()) {
				ArcheDataPath adp = (ArcheDataPath) itUserkeys.next();
				if (adp.getPath().indexOf(path) >= 0) {
					deletePath = false;
					break;
				}
			}
			if (deletePath && path.contains("/value")) {
//				System.out.println("brisem: " + path);
				
//				deleteGeneratedObject(path, rmObject);
			}
		}
		return rmObject;
	}

}
