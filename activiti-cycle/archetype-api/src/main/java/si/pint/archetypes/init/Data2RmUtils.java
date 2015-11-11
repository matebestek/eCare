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

import java.lang.reflect.Method;
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
import org.openehr.rm.composition.content.entry.Action;
import org.openehr.rm.composition.content.entry.Activity;
import org.openehr.rm.composition.content.entry.AdminEntry;
import org.openehr.rm.composition.content.entry.Evaluation;
import org.openehr.rm.composition.content.entry.Instruction;
import org.openehr.rm.composition.content.entry.Observation;
import org.openehr.rm.composition.content.navigation.Section;
import org.openehr.rm.datastructure.history.Event;
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
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;

/**
 * Helper methods for converting data (in XML form) back to archetype object
 * model.
 * 
 * @author Blaz
 * 
 */
//@CommonsLog
public class Data2RmUtils {

	// return value for method 'findConstraintObjectFromPath'
	private static CObject cObject;

	// return value for method 'findNodeInSkeletonTree'
	private static Object nodeInTree;

	// if method found target node (for delete method)
	private static Boolean foundDelete;

	// if method found target node (for append method)
	public static Boolean foundAppend;

	/**
	 * Search user input map and returns data object
	 * 
	 * @param attId
	 * @param userInputsMap
	 * @return DvBoolean, DvQuantity, DvCodedText, ...
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
	public static CObject findConstraintObjectFromPath(String path,
			Archetype archetypeObj) {
		cObject = null;
		findNode(archetypeObj.getDefinition(), path, 0);
		return cObject;
	}

	private static void findNode(CObject object, String path, int level) {
		if (object == null || path == null)
			return;

		// System.out.println(object.path() + ", " + level);
		// String buff = "";
		// for (int i = 0; i < level; i++)
		// buff = buff + "   ";
		// System.out.println(buff + object.path());
		// System.out.println("");

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
				// System.out.println(cAttribute.path());
				if (cAttribute.path().equals(path)) {
					// find first child
					List children = cAttribute.getChildren();
					if (children != null && children.size() > 0)
						cObject = (CObject) children.get(0);
					// nodeFound = cAttribute;

					break;
				}

				Iterator<CObject> itChildren = cAttribute.getChildren()
						.iterator();
				while (itChildren.hasNext()) {
					CObject cObject = itChildren.next();
					findNode(cObject, path, (level + 1));
				}
			}
		}
		// else if (object instanceof CPrimitiveObject) {
		// CPrimitiveObject primitiveObj = (CPrimitiveObject) object;
		// System.out.println(buff + "primitivni: " +
		// primitiveObj.getRmTypeName() + ", " +
		// primitiveObj.getItem().getType());
		// }
		// else if (object instanceof CCodePhrase) {
		// CCodePhrase codePhrase = (CCodePhrase) object;
		// System.out.println(buff + "koda: " + codePhrase.getRmTypeName());
		// Iterator it = codePhrase.getCodeList().iterator();
		// while (it.hasNext()) {
		// String object2 = (String) it.next();
		// System.out.println(buff + object2);
		// }
		// }
		// else if (object instanceof CDvQuantity) {
		// CDvQuantity dvQuantity = (CDvQuantity) object;
		// System.out.println(buff + "kolicina: " + dvQuantity.getRmTypeName() +
		// ", default: " + (dvQuantity.getDefaultValue() != null ?
		// dvQuantity.getDefaultValue().getMagnitude().doubleValue() : ""));
		// }
		// else {
		// System.out.println(buff + "Nepoznan tip: " +
		// object.getClass().getName());
		// }

		return;
	}

	/**
	 * Search through generated tree and look for element at given path.
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @return
	 */
	public static Object deleteGeneratedObject(String path,	Object minimumSkeleton) {
		foundDelete = null;
		if (minimumSkeleton instanceof Section) { //if root element is Section - then path hasn't got root element
			for (ContentItem ci : ((Section) minimumSkeleton).getItems()) {
				if (ci.getArchetypeNodeId().equals(extractAttFromNode(findAttNameFromPath(path, 0)))) {
					findGeneratedObjectInTreeAndDelete(path, ci, 0, null);
					if (foundDelete != null)
						break;
				}
			}
		}
		else if (minimumSkeleton instanceof Action) { //if root element is Action: same as with Section
			findGeneratedObjectInTreeAndDelete(path, ((Action) minimumSkeleton).getDescription(), 0, null);
		}
		else if (minimumSkeleton instanceof Evaluation) {
			findGeneratedObjectInTreeAndDelete(path, ((Evaluation) minimumSkeleton).getData(), 0, null);
		}		
		else {
			findGeneratedObjectInTreeAndDelete(path, minimumSkeleton, 0, null);
		}
		return minimumSkeleton;
	}

	/**
	 * 
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @return
	 */
	private static void findGeneratedObjectInTreeAndDelete(String path,
			Object minimumSkeleton, int level, Object parent) {

		if (minimumSkeleton == null || path == null || path.equals(""))
			return;

		String pathShortExt = findAttNameFromPath(path, level);
		String pathShort = extractAttFromNode(pathShortExt);

		Boolean isSecondLast = isSecondLastNodeOnPath(path, level);
		String nextChildExt = findAttNameFromPath(path, level + 1);
		String nextChild = extractAttFromNode(nextChildExt);
		
		// if (minimumSkeleton instanceof Locatable)
		// System.out.println("skeleton: " + ((Locatable)
		// minimumSkeleton).atNode() + ((Locatable)
		// minimumSkeleton).getArchetypeNodeId() + ", pathShort: " + pathShort);

		if (minimumSkeleton instanceof Section) {
			Section sec = (Section) minimumSkeleton;

			if (pathShort.equals(sec.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// generatedObject = sec;
					return;
				}
			}
			Iterator it = sec.getItems().iterator();
			level = level + 1;
			while (it.hasNext()) {
				ContentItem ci = (ContentItem) it.next();
				findGeneratedObjectInTreeAndDelete(path, ci, level,
						minimumSkeleton);
			}
		} else if (minimumSkeleton instanceof Instruction) {
			Instruction in = (Instruction) minimumSkeleton;

			if (pathShort.equals(in.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// generatedObject = in;
					return;
				}
			}
			Iterator it = in.getActivities().iterator();
			level = level + 1;
			while (it.hasNext()) {
				Activity object = (Activity) it.next();
				findGeneratedObjectInTreeAndDelete(path, object, level,
						minimumSkeleton);
			}
		} else if (minimumSkeleton instanceof Observation) {
			Observation obs = (Observation) minimumSkeleton;

			if (pathShort.equals(obs.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// generatedObject = obs;
					return;
				}
			}
			level = level + 1;
			findGeneratedObjectInTreeAndDelete(path, obs.getData(), level,	minimumSkeleton);
			
			History history = obs.getData();
			if (history != null && history.getEvents() != null) {
				Iterator it = history.getEvents().iterator();
				
				while (it.hasNext()) {
					PointEvent<ItemStructure> event = (PointEvent) it.next();
					findGeneratedObjectInTreeAndDelete(path, event, level,
							minimumSkeleton);
				}
			}
		} else if (minimumSkeleton instanceof Composition) {
			Composition comp = (Composition) minimumSkeleton;

			if (pathShort.equals(comp.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// generatedObject = in;
					return;
				}
			}
			level = level + 1;
			findGeneratedObjectInTreeAndDelete(path, comp.getContext()
					.getOtherContext(), level, minimumSkeleton);
			if (foundDelete != null && foundDelete.booleanValue())
				return;

			level = level - 1; // actually we stay on same lavel!
			if (comp.getContent() != null) {
				Iterator<ContentItem> it = comp.getContent().iterator();
				while (it.hasNext()) {
					ContentItem contentItem = it.next();
					findGeneratedObjectInTreeAndDelete(path, contentItem,
							level, minimumSkeleton);
				}
			}
		} else if (minimumSkeleton instanceof Evaluation) {
			Evaluation eval = (Evaluation) minimumSkeleton;

			if (pathShort.equals(eval.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// generatedObject = in;
					return;
				}
			}

			level = level + 1;
			if (eval.getData() != null)
				findGeneratedObjectInTreeAndDelete(path, eval.getData(), level,
						minimumSkeleton);
		} else if (minimumSkeleton instanceof Activity) {
			Activity ac = (Activity) minimumSkeleton;

			if (pathShort.equals(ac.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// generatedObject = ac;
					return;
				}
				ItemTree itemTree = (ItemTree) ac.getDescription();
				level = level + 1;
				findGeneratedObjectInTreeAndDelete(path, itemTree, level, minimumSkeleton);

			}

		} else if (minimumSkeleton instanceof ItemList) {
			ItemList iList = (ItemList) minimumSkeleton;

			if (pathShort.equals(iList.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {

					return;
				}
				if (iList.getItems() != null) {
					Iterator it = iList.getItems().iterator();
					level = level + 1;
					while (it.hasNext()) {
						org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) it
								.next();
						findGeneratedObjectInTreeAndDelete(path, el, level,
								minimumSkeleton);
					}
				}
			}
		} else if (minimumSkeleton instanceof ItemTree) {
			ItemTree tree = (ItemTree) minimumSkeleton;

			if (pathShort.equals(tree.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					String lastAtt = findLastAttNameOnPath(path);
					lastAtt = extractAttFromNode(lastAtt);
					for (Item item: tree.getItems()) {
						if (item.getArchetypeNodeId().equals(lastAtt)) {
							tree.getItems().remove(item);
							foundDelete = Boolean.TRUE;
							break;
						}
					}
					return;
				}
				if (tree.getItems() != null) {
					Iterator it = tree.getItems().iterator();
					level = level + 1;
					while (it.hasNext()) {
						Item item = (Item) it.next();
						findGeneratedObjectInTreeAndDelete(path, item, level,
								minimumSkeleton);
					}
				}
			}
		} else if (minimumSkeleton instanceof Item) {

			if (minimumSkeleton instanceof Cluster) {
				Cluster c = (Cluster) minimumSkeleton;

				if (pathShort.equals(c.getArchetypeNodeId())) {
					if (isSecondLast != null && isSecondLast.booleanValue()) { 
						for (Item item : c.getItems()) {
							if (item.getArchetypeNodeId().equals(nextChild)) {
								c.getItems().remove(item);
								break;
							}
						}
						foundDelete = Boolean.TRUE;
						return;
					}
					Iterator<Item> it = c.getItems().iterator();
					level = level + 1;
					while (it.hasNext()) {
						Item item = it.next();
						findGeneratedObjectInTreeAndDelete(path, item, level,
								minimumSkeleton);
					}
				}

			} else if (minimumSkeleton instanceof org.openehr.rm.datastructure.itemstructure.representation.Element) {
				org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) minimumSkeleton;

				if (pathShort.equals(el.getArchetypeNodeId())) {
					if (isSecondLast != null && isSecondLast.booleanValue()
							|| el.getValue() instanceof DvCodedText || el.getValue() instanceof DvDuration) { // izjema!!
																		// DvCodedText
																		// ima
																		// se en
																		// dodaten
																		// podelement!
						// brisanje!!!!
						el.set("/" + nextChild, null);
						foundDelete = Boolean.TRUE;
						return;
					}
					// if (el.getValue() instanceof DvBoolean) {
					//
					// }
					// else if (el.getValue() instanceof DvQuantity) {
					// DvQuantity quant = (DvQuantity) el.getValue();
					// }
					// else if (el.getValue() instanceof DvOrdinal) {
					// DvOrdinal ord = (DvOrdinal) el.getValue();
					//
					// }
					// else if (el.getValue() instanceof DvAmount) {
					// DvAmount amount = (DvAmount) el.getValue();
					//
					// }
					// else if (el.getValue() instanceof DvCodedText) {
					// DvCodedText codedText = (DvCodedText) el.getValue();
					// }
					// else {
					// System.out.println("other: ?? " +
					// el.getValue().getClass().getName());
					// }

					// generatedObject = el;

				}
			}
		} else if (minimumSkeleton instanceof DataValue) {
			DataValue dv = (DataValue) minimumSkeleton;
			if (dv instanceof DvText) {
				((DvText) dv).setValue(null);
			}
			//TODO: dodaj se ostale primitivne tipe
			foundDelete = Boolean.TRUE;
			return;
		} else if (minimumSkeleton instanceof PointEvent<?>) {
			PointEvent<?> event = (PointEvent) minimumSkeleton;

			if (pathShort.equals(event.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// TODO: tukaj bi se moralo brisati
					//System.out.println("TODO BRISANJE ELEMENTENA");
					// erase element ------->
					// parent.removeChild("/" + pathShortExt);

					// generatedObject = event;
					return;
				}
				level = level + 1;
				findGeneratedObjectInTreeAndDelete(path, event.getData(),
						level, minimumSkeleton);
				if (foundDelete != null && foundDelete.booleanValue())
					return;
				findGeneratedObjectInTreeAndDelete(path, event.getState(),
						level, minimumSkeleton);
			}
		} else if (minimumSkeleton instanceof ItemSingle) {
			ItemSingle item = (ItemSingle) minimumSkeleton;

			if (pathShort.equals(item.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// TODO: tukaj bi se moralo brisati
					// generatedObject = item;
					return;
				}
				level = level + 1;
				findGeneratedObjectInTreeAndDelete(path, item.getItem(), level,	minimumSkeleton);
			}
		} else if (minimumSkeleton instanceof AdminEntry) {
			AdminEntry adminEntry = (AdminEntry) minimumSkeleton;
			
			if (pathShort.equals(adminEntry.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
//					nodeInTree = adminEntry;
					return;
				}
				level = level + 1;
				findGeneratedObjectInTreeAndDelete(path, adminEntry.getData(), level, minimumSkeleton);
			}
		} else if (minimumSkeleton instanceof History<?>) {
			History<?> history = (History<?>) minimumSkeleton;
			
			if (pathShort.equals(history.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					return;
				}
				level = level + 1;
				Iterator it = history.getEvents().iterator();
				while (it.hasNext()) {
					Event event = (Event) it.next();
					findGeneratedObjectInTreeAndDelete(path, event, level, minimumSkeleton);
				}
			}
		} else if (minimumSkeleton instanceof Action) {
			Action action = (Action) minimumSkeleton;
			
			if (pathShort.equals(action.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					return;
				}
				level = level + 1;
				findGeneratedObjectInTreeAndDelete(path, action.getDescription(), level, minimumSkeleton);
			}
		}
		
		return;

	}
	
	

	/**
	 * Searches right section path depending on level. e.g. path:
	 * /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value level: 2
	 * result: at0001
	 * 
	 * @param path
	 * @param level
	 * @return
	 */
	public static String findAttNameFromPath(String path, int level) {
		if (path == null || path.equals(""))
			return null;

		String[] nodes = splitPath(path);
		if (nodes == null || (nodes.length - 1) <= level)
			return null;

		return nodes[level+1];
	}

	/**
	 * Searches right section path depending on level. e.g. path:
	 * /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value level: 2
	 * result: /data[at0002]/events[at0028]/data[at0001]
	 * 
	 * @author Mate Beštek 27.7.2011
	 * @param path
	 * @param level
	 * @return
	 */
	public static String findSubPath(String path, int depth) {
		if (path == null || path.equals(""))
			return null;
		
		String[] nodes = splitPath(path);		
		if (nodes.length == 0 || (nodes.length - 1) <= depth){
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < depth+1; i++){
			sb.append("/"+nodes[i+1]);
		}

		return sb.toString();
	}
	
	/**
	 * Metoda, ki vrne seznam node-ov na podani poti. Je drugačna od .split()
	 * saj upošteva tudi dejstvo, da imamo lahko znotraj [] tudi / in vse
	 * ostale metode so delale split samo po / kar je napačno delovanje.
	 * @author Mate
	 * */
	private static String[] splitPath(String path){
		String[] nodes = path.split("]/");
		List<String> all = new ArrayList<String>();
		all.add("");
		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = (nodes[i].contains("[") && !nodes[i].endsWith("]"))?nodes[i]+"]":nodes[i];
				nodes[i] = (nodes[i].startsWith("/"))?nodes[i].substring(1):nodes[i];
				if(!nodes[i].contains("["))
				{	
					String[] temp = nodes[i].split("/");
					if(temp != null){
						for(int j = 0; j < temp.length; j++){
							all.add(temp[j]);
						}
					}
				} else {
					all.add(nodes[i]);
				}
			}
		}
		String[] n = new String[all.size()];
		return all.toArray(n);
	}
	
	

	/**
	 * Extract node from path.
	 * 
	 * @param attribute
	 * @return
	 */
	public static String extractAttFromNode(String attribute) {
		if (attribute != null && attribute.contains("[")) {
			//System.out.println(attribute);
			if (attribute.indexOf("]") > 0) {
				attribute = attribute.substring(attribute.indexOf("[") + 1,
						attribute.indexOf("]"));
			}
			attribute =  getOnlyAttNameWithoutExtras(attribute);
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
	public static Boolean isLastAttOnPath(String path, String att) {
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

		String[] pathArray = splitPath(path);
		if (pathArray != null && pathArray.length - 1 == (level + 1)){
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * Is given attribute (node) second last on given path.
	 * 
	 * @param path
	 * @param att
	 * @return
	 */
	public static Boolean isSecondLastAttOnPath(String path, String att) {
		if (path == null || att == null || path.equals("") || att.equals(""))
			return null;

		int firstAttIndex = path.indexOf("[", path.indexOf(att) + 1);
		// at least one more
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

		String[] slices = splitPath(path);
		if (slices == null || ((slices.length - 1) < (level +1) ))
			return null;

		if ((slices.length - 2) == (level + 1) ){
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * e.g. input: /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value,
	 * 1 out: /events[at0028]/data[at0001]/item[at0004]/value
	 * 
	 * @param path
	 * @param pathShortExt
	 * @return
	 */
	public static String findSubpathFromNode(String path, int depth) {
		if (path == null || path.equals("") || depth < 0)
			return null;

		String[] slices = splitPath(path);
		
		if (slices == null || slices.length - 1 < depth +1)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = depth + 1; i < slices.length; i++) {
			sb.append("/" + slices[i]);
		}
		return sb.toString();
	}

	/**
	 * e.g. input: /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value
	 * output: /data[at0002]/events[at0028]/data[at0001]/item[at0004]
	 * 
	 * @param path
	 * @return
	 */
	public static String findLastAttOnPath(String path) {
		if (path == null || path.equals(""))
			return null;

		String[] slices = splitPath(path);
		if (slices == null)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < slices.length; i++) {
			if (slices[i].contains("[")){
				sb.append("/" + slices[i]);
			}

		}

		return sb.toString();

	}
	
	
	
	/**
	 * e.g. input: /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value
	 * output: item[at0004]
	 * 
	 * @param path
	 * @return
	 */
	public static String findLastAttNameOnPath(String path) {
		
		
		if (path == null || path.equals(""))
			return null;
		
		String[] slices = splitPath(path);
		if (slices == null)
			return null;

		return slices[slices.length - 1];

	}	
	
	
	/**
	 * e.g. input: /data[at0002]/events[at0028]/data[at0001]/item[at0004]/value
	 * output: item[at0004]
	 * 
	 * @param path
	 * @return
	 */
	public static String findSecondLastAttNameOnPath(String path) {
		
		
		if (path == null || path.equals(""))
			return null;
		
		String[] slices = splitPath(path);
		if (slices == null || slices.length < 2)
			return null;

		return slices[slices.length - 2];

	}
	
	

	/**
	 * Look for last attribute on path and then move down to first 'value'.
	 * 
	 * e.g. input: /data[at0002]/events[at0028]/data[at0029]/item[at0030]/value/
	 * defining_code /data[at0002]/events[at0028]/data[at0029]/item[at0030]
	 * output: /item[at0030]/value
	 * 
	 * @param path
	 * @return
	 */
	public static String findFirstValueNode(String wholePath, String lastAttPath) {
		if (wholePath == null || wholePath.equals("") || lastAttPath == null
				|| lastAttPath.equals(""))
			return null;

		String[] wholePathSlice = splitPath(wholePath);
		String[] lastAttPathSlice = splitPath(lastAttPath);

		StringBuffer sb = new StringBuffer();
		sb.append("/" + lastAttPathSlice[lastAttPathSlice.length - 1]);
		for (int i = lastAttPathSlice.length; i < wholePathSlice.length; i++) {
			sb.append("/" + wholePathSlice[i]);
			if (wholePathSlice[i].equals("value")) {
				return sb.toString();
			}
		}
		return null;
	}

	/**
	 * Is
	 * 
	 * e.g. input: /data[at0002]/events[at0028]/data[at0029]/item[at0030]/value/
	 * defining_code at0029 output: true
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isAttOnPath(String wholePath, String attName) {
		if (StringUtils.isEmpty(wholePath) || StringUtils.isEmpty(attName))
			return false;

		String[] wholePathSlice = splitPath(wholePath);

		for (int i = 1; i < wholePathSlice.length-1; i++) {
			int startIdx = wholePathSlice[i].indexOf('[');
			if (startIdx == -1){//ko naletim na vrednost, končam ker očitno ne najdem atributa
				return false;
			}
			int endIdx = wholePathSlice[i].indexOf(']', startIdx);
			if (endIdx == -1){//je to sploh možno?
				return false;
			}
			if (wholePathSlice[i].substring(startIdx + 1, endIdx).equals(
					attName)){
				return true;
			}
		}
		return false;
	}

	/**
	 * input:
	 * /data[at0001]/events[at0002]/data[at0003]/items[at0127]/items[openEHR
	 * -EHR-
	 * CLUSTER.asthma_control_test_questionary.v1]/items[at0032]/items[at0004
	 * ]/value output: openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1
	 * 
	 * @param path
	 * @return
	 */
	public static String findLastArchetypeIdFromPath(String path) {
		if (StringUtils.isEmpty(path))
			return null;

		String[] array = splitPath(path);
		for (int i = array.length - 1; i > 0; i--) {
			int index = array[i].indexOf("[");
			if (index == -1){
				continue;
			}

			String att = array[i].substring(index + 1,
					array[i].indexOf("]", index));
//			if (!att.matches("at*")) {//at\\d\\d\\d\\d
//				return att;
//			}
			if(!att.trim().startsWith("at")){
				//izluščim ven samo ime arhetipa, ostalo kot je npr. name/value... ne rabim
//				index = att.indexOf(" ");//prvi presledek pomeni začetek and ....
//				if(index > 0){
//					att = att.substring(0,index);
//				}
//				return att;
				return getOnlyAttNameWithoutExtras(att);
			}

		}

		return null;
	}
	
	private static String getOnlyAttNameWithoutExtras(String attName){
		int index = attName.indexOf(" ");//prvi presledek pomeni začetek and ....
		if(index > 0){
			attName = attName.substring(0,index);
		}
		return attName;
	}
		
	
	/**
	 * Find node with given 'path' in 'minimumSkeleton' and set 'dvData' to it.
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @param dvData
	 * @param level
	 */
	public static void appendDataToTreeStart(String path, Object minimumSkeleton, Object dvData, int level) {
		foundAppend = null;
		if (minimumSkeleton instanceof Section) { //exception for Section elements (element path skips root element)
			for (ContentItem ci : ((Section) minimumSkeleton).getItems()) {
				if (ci.getArchetypeNodeId().equals(extractAttFromNode(findAttNameFromPath(path, 0)))) {
					appendDataToTree(path, ci, dvData, level);
					if (foundAppend != null)
						return;
				}
			}
		} else if (minimumSkeleton instanceof Action) { //exception for Action elements (element path skips root element)
			appendDataToTree(path, ((Action) minimumSkeleton).getDescription(), dvData, level);
		}
		else if (minimumSkeleton instanceof Evaluation) { //same here
			appendDataToTree(path, ((Evaluation) minimumSkeleton).getData(), dvData, level);
		}
		else {
			appendDataToTree(path, minimumSkeleton, dvData, level);
		}
	}
	

	/**
	 * Find node with given 'path' in 'minimumSkeleton' and set 'dvData' to it.
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @param dvData
	 * @param level
	 */
	private static void appendDataToTree(String path, Object minimumSkeleton, Object dvData, int level) throws IllegalArgumentException {
		if (minimumSkeleton == null || path == null || path.equals(""))
			return;

		String pathShortExt = findAttNameFromPath(path, level);
		if (pathShortExt == null)
			return;
		String pathShort = extractAttFromNode(pathShortExt);

		// Boolean isLast = isLastNodeOnPath(path, level);
		Boolean isSecondLast = isSecondLastNodeOnPath(path, level);

		// if (minimumSkeleton instanceof Locatable)
		// System.out.println("skeleton: " + ((Locatable)
		// minimumSkeleton).atNode() + ((Locatable)
		// minimumSkeleton).getArchetypeNodeId() + ", pathShort: " + pathShort);

		if (minimumSkeleton instanceof Section) {
			Section sec = (Section) minimumSkeleton;

			if (pathShort.equals(sec.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) { // we
																			// reached
																			// parent
																			// node
																			// (time
																			// to
																			// append
																			// child)
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
		} else if (minimumSkeleton instanceof Instruction) {
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
		} else if (minimumSkeleton instanceof Observation) {
			Observation obs = (Observation) minimumSkeleton;

			if (pathShort.equals(obs.getData().getArchetypeNodeId()) || pathShort.equals(obs.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					 //obs.set("/" + findAttNameFromPath(path, level + 1),dvData);
					System.out.println("TODO!!!!");
					return;
				}
			}
			
			level = level + 1;
			appendDataToTree(path, obs.getData(), dvData, level);
			
			History history = obs.getData();
			if (history != null && history.getEvents() != null) {
				Iterator it = history.getEvents().iterator();
				while (it.hasNext()) {
					PointEvent<ItemStructure> event = (PointEvent) it.next();
					appendDataToTree(path, event, dvData, level);
				}
			}
		} else if (minimumSkeleton instanceof Composition) {
			Composition comp = (Composition) minimumSkeleton;

			if (pathShort.equals(comp.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// obs.set("/" + findAttNameFromPath(path, level + 1),
					// dvData);
					System.out.println("TODO!!!!");
					return;
				}
			}
			level = level + 1;
			appendDataToTree(path, comp.getContext().getOtherContext(), dvData,
					level);
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

		} else if (minimumSkeleton instanceof Evaluation) {
			Evaluation eval = (Evaluation) minimumSkeleton;

			if (pathShort.equals(eval.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// obs.set("/" + findAttNameFromPath(path, level + 1),
					// dvData);
					System.out.println("TODO!!!!");
					return;
				}
			}
			level = level + 1;
			appendDataToTree(path, eval.getData(), dvData, level);

		} else if (minimumSkeleton instanceof Activity) {
			Activity ac = (Activity) minimumSkeleton;

			if (pathShort.equals(ac.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					ac.set("/" + findAttNameFromPath(path, level + 1), dvData);
					foundAppend = Boolean.TRUE;
					return;
				}
				ItemTree itemTree = (ItemTree) ac.getDescription();
				level = level + 1;
				appendDataToTree(path, itemTree, dvData, level);
			}

		} else if (minimumSkeleton instanceof ItemList) {
			ItemList iList = (ItemList) minimumSkeleton;

			if (pathShort.equals(iList.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// iList.set("/" + findAttNameFromPath(path, level + 1),
					// dvData);
					iList.getItems()
							.add((org.openehr.rm.datastructure.itemstructure.representation.Element) dvData);
					foundAppend = Boolean.TRUE;
					return;
				}
				if (iList.getItems() != null) {
					Iterator it = iList.getItems().iterator();
					level = level + 1;
					while (it.hasNext()) {
						org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) it
								.next();
						appendDataToTree(path, el, dvData, level);
					}
				}
			}
		} else if (minimumSkeleton instanceof ItemTree) {
			ItemTree tree = (ItemTree) minimumSkeleton;

			if (pathShort.equals(tree.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					// tree.set("/" + findAttNameFromPath(path, level + 1),
					// dvData);
					tree.getItems().add((Item) dvData);
					foundAppend = Boolean.TRUE;
					return;
				}
				if (tree.getItems() != null) {
					Iterator it = tree.getItems().iterator();
					int idxLast = tree.getItems().size() - 1;
					level = level + 1;
					while ((foundAppend == null || !foundAppend) && idxLast >= 0) {
						Item item = tree.getItems().get(idxLast);
						appendDataToTree(path, item, dvData, level);
						idxLast --;
					}
				}
			}
		} else if (minimumSkeleton instanceof Item) {

			if (minimumSkeleton instanceof Cluster) {
				Cluster c = (Cluster) minimumSkeleton;

				if (pathShort.equals(c.getArchetypeNodeId())) {
					if (isSecondLast != null && isSecondLast.booleanValue()) {
						// c.set("/" + findAttNameFromPath(path, level + 1),
						// dvData);
						c.getItems().add((Item) dvData);
						foundAppend = Boolean.TRUE;
						return;
					}
					Iterator<Item> it = c.getItems().iterator();
					level = level + 1;
					while (it.hasNext()) {
						Item item = it.next();
						appendDataToTree(path, item, dvData, level);
					}
				}

			} else if (minimumSkeleton instanceof org.openehr.rm.datastructure.itemstructure.representation.Element) {
				org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) minimumSkeleton;

				if (pathShort.equals(el.getArchetypeNodeId())) {
					if (isSecondLast != null && isSecondLast.booleanValue()) {
					
						el.set("/" + findAttNameFromPath(path, level + 1),
								dvData);
						foundAppend = Boolean.TRUE;

						// if (dvData instanceof DataValue)
						// el.setValue((DataValue) dvData);
						// if (el.getValue() instanceof DvBoolean) {
						//
						// }
						// else if (el.getValue() instanceof DvQuantity) {
						// DvQuantity quant = (DvQuantity) el.getValue();
						// }
						// else if (el.getValue() instanceof DvOrdinal) {
						// DvOrdinal ord = (DvOrdinal) el.getValue();
						//
						// }
						// else if (el.getValue() instanceof DvAmount) {
						// DvAmount amount = (DvAmount) el.getValue();
						//
						// }
						// else if (el.getValue() instanceof DvCodedText) {
						// DvCodedText codedText = (DvCodedText) el.getValue();
						// }
						// else {
						// System.out.println("other: ?? " +
						// el.getValue().getClass().getName());
						// }

						// generatedObject = el;
						return;
					}
				}

			}
		} else if (minimumSkeleton instanceof PointEvent<?>) {
			PointEvent<?> event = (PointEvent) minimumSkeleton;

			if (pathShort.equals(event.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					String attName = findAttNameFromPath(path, level + 1);
					for(Method m:event.getClass().getDeclaredMethods()){//TODO:Take zadeve načeloma nimajo kaj počet tu!!! Problem pa je, zakaj se sploh poskusi klicat metodo, ki 
						//v PointEvent ne obstaja
						if(m.getName().equals("set"+attName)){
							event.set("/" + attName, dvData);
							foundAppend = Boolean.TRUE;
							return;
						}
					}
					
				}
				level = level + 1;
				appendDataToTree(path, event.getData(), dvData, level);
				if (foundAppend != null && foundAppend.booleanValue())
					return;
				appendDataToTree(path, event.getState(), dvData, level);
			}
		} else if (minimumSkeleton instanceof ItemSingle) {
			ItemSingle item = (ItemSingle) minimumSkeleton;

			if (pathShort.equals(item.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {

					item.set("/" + findAttNameFromPath(path, level + 1), dvData);
					if (!(item.itemAtPath("/"
							+ findAttNameFromPath(path, level + 1))
							.equals(dvData)))
						throw new IllegalArgumentException(
								"Trying to add input data to unsupported node/type!");
					foundAppend = Boolean.TRUE;
					return;
				}
				level = level + 1;
				appendDataToTree(path, item.getItem(), dvData, level);
			}
		} else if (minimumSkeleton instanceof AdminEntry) {
			AdminEntry adminEntry = (AdminEntry) minimumSkeleton;
			
			if (pathShort.equals(adminEntry.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					adminEntry.set("/" + findAttNameFromPath(path, level + 1), dvData);
					foundAppend = Boolean.TRUE;
					return;
				}
				level = level + 1;
				appendDataToTree(path, adminEntry.getData(), dvData, level);
			}
		} else if (minimumSkeleton instanceof History<?>) {
			History<?> history = (History<?>) minimumSkeleton;
			
			if (pathShort.equals(history.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					//??
					foundAppend = Boolean.TRUE;
					return;
				}
				level = level + 1;
				Iterator it = history.getEvents().iterator();
				while (it.hasNext()) {
					Event event = (Event) it.next();
					appendDataToTree(path, event, dvData, level);
				}
			}
			
		} else if (minimumSkeleton instanceof Action) {
			Action action = (Action) minimumSkeleton;
			
			if (pathShort.equals(action.getArchetypeNodeId())) {
				if (isSecondLast != null && isSecondLast.booleanValue()) {
					foundAppend = Boolean.TRUE;
					return;
				}
				level = level + 1;
				appendDataToTree(path, action.getDescription(), dvData, level);
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
		if (minimumSkeleton instanceof Section) { //exception for Section (if root archetype is Section -> skip first path element)
			List<ContentItem> list = ((Section) minimumSkeleton).getItems();
			for (ContentItem item : list) {
				if (item.getArchetypeNodeId().equals(extractAttFromNode(findAttNameFromPath(pathShortExt, 0)))) {
					findNodeInSkeletonTree(pathShortExt, item, 0);
					if (nodeInTree != null)
						return nodeInTree;
				}
			}
		}
		else if (minimumSkeleton instanceof Action) {
			findNodeInSkeletonTree(pathShortExt, ((Action) minimumSkeleton).getDescription(), 0);
		}
		else if (minimumSkeleton instanceof Evaluation) {
			findNodeInSkeletonTree(pathShortExt, ((Evaluation) minimumSkeleton).getData(), 0);
		}		
		else {
			findNodeInSkeletonTree(pathShortExt, minimumSkeleton, 0);
		}
		return nodeInTree;
	}

	private static void findNodeInSkeletonTree(String path,
			Object minimumSkeleton, int level) {
		if (minimumSkeleton == null || path == null || path.equals(""))
			return;

		String pathShortExt = findAttNameFromPath(path, level);
		if (pathShortExt == null)
			return;

		String pathShort = extractAttFromNode(pathShortExt);

		Boolean isLastNode = isLastNodeOnPath(path, level);

		// if (minimumSkeleton instanceof Locatable)
		// System.out.println("skeleton: " + ((Locatable)
		// minimumSkeleton).atNode() + ((Locatable)
		// minimumSkeleton).getArchetypeNodeId() + ", pathShort: " + pathShort);

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
		} else if (minimumSkeleton instanceof Instruction) {
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
		} else if (minimumSkeleton instanceof Observation) {
			Observation obs = (Observation) minimumSkeleton;
			
			if (pathShort.equals(obs.getData().getArchetypeNodeId()) || pathShort.equals(obs.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = obs;
					return;
				}
			}
			
			level = level + 1;
			findNodeInSkeletonTree(path, obs.getData(), level);
			
			History history = obs.getData();
			if (history != null && history.getEvents() != null) {
				Iterator it = history.getEvents().iterator();
				while (it.hasNext()) {
					PointEvent<ItemStructure> event = (PointEvent) it.next();
					findNodeInSkeletonTree(path, event, level);
				}
			}
			
			
		} else if (minimumSkeleton instanceof Composition) {
			Composition comp = (Composition) minimumSkeleton;

			if (isLastNode != null && isLastNode.booleanValue()) {
				if (pathShort.equals(comp.getArchetypeNodeId())) {
					nodeInTree = comp;
					return;
				} else if (pathShort.equals("context")) { // 'context' node
															// hasn't got
															// archetypeNodeID
															// info
					nodeInTree = comp.getContext();
					return;
				}
			}
			level = level + 1;
			findNodeInSkeletonTree(path, comp.getContext().getOtherContext(),
					level);
			if (nodeInTree != null)
				return; // if found return

			level = level - 1;
			if (comp.getContent() != null) {
				Iterator<ContentItem> it = comp.getContent().iterator();
				while (it.hasNext()) {
					ContentItem contentItem = it.next();
					findNodeInSkeletonTree(path, contentItem, level);
				}
			}

		} else if (minimumSkeleton instanceof Evaluation) {
			Evaluation eval = (Evaluation) minimumSkeleton;

			if (pathShort.equals(eval.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = eval;
					return;
				}
			}
			level = level + 1;
			findNodeInSkeletonTree(path, eval.getData(), level);

		} else if (minimumSkeleton instanceof Activity) {
			Activity ac = (Activity) minimumSkeleton;

			if (pathShort.equals(ac.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = ac;
					return;
				}
				ItemTree itemTree = (ItemTree) ac.getDescription();
				level = level + 1;
				findNodeInSkeletonTree(path, itemTree, level);
			}

		} else if (minimumSkeleton instanceof ItemList) {
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
						org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) it
								.next();
						findNodeInSkeletonTree(path, el, level);
					}
				}
			}
		} else if (minimumSkeleton instanceof ItemTree) {
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
		} else if (minimumSkeleton instanceof Item) {

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

			} else if (minimumSkeleton instanceof org.openehr.rm.datastructure.itemstructure.representation.Element) {
				org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) minimumSkeleton;

				if (pathShort.equals(el.getArchetypeNodeId())) {
					if (isLastNode != null && isLastNode.booleanValue()) {
						nodeInTree = el;
						return;
					}
					// findNodeInSkeletonTree(path, el.getValue(), ++level);
					if (el.getValue() instanceof DataValue) {
						if (el.getValue() instanceof DvBoolean) {
							nodeInTree = el.getValue();
							return;
						} else if (el.getValue() instanceof DvQuantity) {
							DvQuantity quant = (DvQuantity) el.getValue();
							nodeInTree = el.getValue();
							return;
						} else if (el.getValue() instanceof DvOrdinal) {
							DvOrdinal ord = (DvOrdinal) el.getValue();
							nodeInTree = el.getValue();
							return;
						} else if (el.getValue() instanceof DvAmount) {
							DvAmount amount = (DvAmount) el.getValue();
							nodeInTree = el.getValue();
							return;
						} else if (el.getValue() instanceof DvText) {
							DvText text = (DvText) el.getValue();
							nodeInTree = el.getValue();
							return;
						} else if (el.getValue() instanceof DvCodedText) {
							DvCodedText codedText = (DvCodedText) el.getValue();

							Boolean isLastNodeChild = isLastNodeOnPath(path,
									level + 1); // is child last node?
							Boolean isSecondLastNodeChild = isSecondLastNodeOnPath(
									path, level + 1);// is child a second last
														// node?
							if (isLastNodeChild != null
									&& isLastNodeChild.booleanValue()) {
								nodeInTree = codedText;
								return;
							} else if (isSecondLastNodeChild != null
									&& isSecondLastNodeChild.booleanValue()) {
								nodeInTree = codedText.getDefiningCode();
								return;
							}
						} else {
							System.out.println("other: ?? "
									+ el.getValue().getClass().getName());
						}
					}

				}

			}
		} else if (minimumSkeleton instanceof PointEvent<?>) {
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
		} else if (minimumSkeleton instanceof ItemSingle) {
			ItemSingle item = (ItemSingle) minimumSkeleton;

			if (pathShort.equals(item.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = item;
					return;
				}
				level = level + 1;
				findNodeInSkeletonTree(path, item.getItem(), level);
			}
		} else if (minimumSkeleton instanceof AdminEntry) {
			AdminEntry adminEntry = (AdminEntry) minimumSkeleton;
			
			if (pathShort.equals(adminEntry.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = adminEntry;
					return;
				}
				level = level + 1;
				findNodeInSkeletonTree(path, adminEntry.getData(), level);
			}
		} else if (minimumSkeleton instanceof History<?>) {
			History<?> history = (History<?>) minimumSkeleton;
			
			if (pathShort.equals(history.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = history;
					return;
				}
				level = level + 1;
				Iterator itEvents = history.getEvents().iterator();
				while (itEvents.hasNext()) {
					Event event = (Event) itEvents.next();
					findNodeInSkeletonTree(path, event, level);
				}				
			}
		} else if (minimumSkeleton instanceof Action) {
			Action action = (Action) minimumSkeleton;
			
			if (pathShort.equals(action.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = action;
					return;
				}
				level = level + 1;
				findNodeInSkeletonTree(path, action.getDescription(), level);
			}
		}
		return;
	}
	
	
	/**
	 * Finds node with given path. IF MORE THAN ONE NODE has the same path, function returns LAST node in list. 
	 * 
	 * @param path
	 * @param minimumSkeleton
	 * @param archetypeObj
	 */
	public static Object findNodeToGenerateSiblings(String pathShortExt, Object minimumSkeleton) {
		nodeInTree = null;
		if (minimumSkeleton instanceof Section) { //exception for Section (if root archetype is Section -> skip first path element)
			List<ContentItem> list = ((Section) minimumSkeleton).getItems();
			for (ContentItem item : list) {
				if (item.getArchetypeNodeId().equals(extractAttFromNode(findAttNameFromPath(pathShortExt, 0)))) {
					findNodeInSkeletonTreeSiblings(pathShortExt, item, 0, false);
					if (nodeInTree != null)
						return nodeInTree;
				}
			}
		}
		else if (minimumSkeleton instanceof Action) {
			findNodeInSkeletonTreeSiblings(pathShortExt, ((Action) minimumSkeleton).getDescription(), 0, false);
		}
		else if (minimumSkeleton instanceof Evaluation) {
			findNodeInSkeletonTreeSiblings(pathShortExt, ((Evaluation) minimumSkeleton).getData(), 0, false);
		}
		else {
			findNodeInSkeletonTreeSiblings(pathShortExt, minimumSkeleton, 0, false);
		}
		return nodeInTree;
	}
	
	

	private static void findNodeInSkeletonTreeSiblings(String path,
			Object minimumSkeleton, int level, boolean lastItem) {
		if (minimumSkeleton == null || path == null || path.equals(""))
			return;

		String pathShortExt = findAttNameFromPath(path, level);
		if (pathShortExt == null)
			return;

		String pathShort = extractAttFromNode(pathShortExt);

		Boolean isLastNode = isLastNodeOnPath(path, level);

		// if (minimumSkeleton instanceof Locatable)
		// System.out.println("skeleton: " + ((Locatable)
		// minimumSkeleton).atNode() + ((Locatable)
		// minimumSkeleton).getArchetypeNodeId() + ", pathShort: " + pathShort);

		int idx = 0; //index for last elements in lists
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
//				if (idx == sec.getItems().size() - 1)
//					lastItem = true;
				ContentItem object = (ContentItem) it.next();
				findNodeInSkeletonTreeSiblings(path, object, level, lastItem);
				idx++;
			}
		} else if (minimumSkeleton instanceof Instruction) {
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
//				if (idx == in.getActivities().size() - 1)
//					lastItem = true;
				Activity object = (Activity) it.next();
				findNodeInSkeletonTreeSiblings(path, object, level, lastItem);
				idx++;
			}
		} else if (minimumSkeleton instanceof Observation) {
			Observation obs = (Observation) minimumSkeleton;
			
			if (pathShort.equals(obs.getData().getArchetypeNodeId()) || pathShort.equals(obs.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = obs;
					return;
				}
			}
			
			level = level + 1;
			findNodeInSkeletonTreeSiblings(path, obs.getData(), level, lastItem);
			
			History history = obs.getData();
			if (history != null && history.getEvents() != null) {
				Iterator it = history.getEvents().iterator();
				while (it.hasNext()) {
//					if (idx == history.getEvents().size() - 1)
//						lastItem = true;
						
					PointEvent<ItemStructure> event = (PointEvent) it.next();
					findNodeInSkeletonTreeSiblings(path, event, level, lastItem);
					idx++;
				}
			}
			
			
		} else if (minimumSkeleton instanceof Composition) {
			Composition comp = (Composition) minimumSkeleton;

			if (isLastNode != null && isLastNode.booleanValue()) {
				if (pathShort.equals(comp.getArchetypeNodeId())) {
					nodeInTree = comp;
					return;
				} else if (pathShort.equals("context")) { // 'context' node
															// hasn't got
															// archetypeNodeID
															// info
					nodeInTree = comp.getContext();
					return;
				}
			}
			level = level + 1;
			findNodeInSkeletonTreeSiblings(path, comp.getContext().getOtherContext(), level, lastItem);
			if (nodeInTree != null)
				return; // if found return

			level = level - 1;
			if (comp.getContent() != null) {
				Iterator<ContentItem> it = comp.getContent().iterator();
				while (it.hasNext()) {
//					if (idx == comp.getContent().size() - 1)
//						lastItem = true;
					ContentItem contentItem = it.next();
					findNodeInSkeletonTreeSiblings(path, contentItem, level, lastItem);
					idx++;
				}
			}

		} else if (minimumSkeleton instanceof Evaluation) {
			Evaluation eval = (Evaluation) minimumSkeleton;

			if (pathShort.equals(eval.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = eval;
					return;
				}
			}
			level = level + 1;
			findNodeInSkeletonTreeSiblings(path, eval.getData(), level, lastItem);

		} else if (minimumSkeleton instanceof Activity) {
			Activity ac = (Activity) minimumSkeleton;

			if (pathShort.equals(ac.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = ac;
					return;
				}
				ItemTree itemTree = (ItemTree) ac.getDescription();
				level = level + 1;
				findNodeInSkeletonTreeSiblings(path, itemTree, level, lastItem);
			}

		} else if (minimumSkeleton instanceof ItemList) {
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
//						if (idx == iList.getItems().size() - 1)
//							lastItem = true;
						org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) it
								.next();
						findNodeInSkeletonTreeSiblings(path, el, level, lastItem);
						idx++;
					}
				}
			}
		} else if (minimumSkeleton instanceof ItemTree) {
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
//						if (idx == tree.getItems().size() - 1)
//							lastItem = true;					
						findNodeInSkeletonTreeSiblings(path, item, level, isLastClusterInList(tree.getItems(), idx));
						idx++;
					}
				}
			}
		} else if (minimumSkeleton instanceof Item) {

			if (minimumSkeleton instanceof Cluster) {
				Cluster c = (Cluster) minimumSkeleton;

				if (pathShort.equals(c.getArchetypeNodeId())) {
					if (isLastNode != null && isLastNode.booleanValue()) {
						if (lastItem) 
							nodeInTree = c;
						return;
					}
					Iterator<Item> it = c.getItems().iterator();
					level = level + 1;
					while (it.hasNext()) {
//						if (idx == c.getItems().size() - 1)
//							lastItem = true;
						Item item = it.next();
						findNodeInSkeletonTreeSiblings(path, item, level, lastItem);
						idx++;
					}
				}

			} else if (minimumSkeleton instanceof org.openehr.rm.datastructure.itemstructure.representation.Element) {
				org.openehr.rm.datastructure.itemstructure.representation.Element el = (org.openehr.rm.datastructure.itemstructure.representation.Element) minimumSkeleton;

				if (pathShort.equals(el.getArchetypeNodeId())) {
					if (isLastNode != null && isLastNode.booleanValue() && lastItem) {
						nodeInTree = el;
						return;
					}
					// findNodeInSkeletonTreeSiblings(path, el.getValue(), ++level);
					if (el.getValue() instanceof DataValue) {
						if (el.getValue() instanceof DvBoolean) {
							if (lastItem) {
								nodeInTree = el.getValue();
								return;
							}
						} else if (el.getValue() instanceof DvQuantity) {
							DvQuantity quant = (DvQuantity) el.getValue();
							if (lastItem) {							
								nodeInTree = el.getValue();
								return;
							}
						} else if (el.getValue() instanceof DvOrdinal) {
							DvOrdinal ord = (DvOrdinal) el.getValue();
							if (lastItem) {							
								nodeInTree = el.getValue();
								return;
							}
						} else if (el.getValue() instanceof DvAmount) {
							DvAmount amount = (DvAmount) el.getValue();
							if (lastItem) {
								nodeInTree = el.getValue();
								return;
							}
						} else if (el.getValue() instanceof DvText) {
							DvText text = (DvText) el.getValue();
							if (lastItem) {
								nodeInTree = el.getValue();
								return;
							}
						} else if (el.getValue() instanceof DvCodedText) {
							DvCodedText codedText = (DvCodedText) el.getValue();

							Boolean isLastNodeChild = isLastNodeOnPath(path,
									level + 1); // is child last node?
							Boolean isSecondLastNodeChild = isSecondLastNodeOnPath(
									path, level + 1);// is child a second last
														// node?
							if (isLastNodeChild != null
									&& isLastNodeChild.booleanValue()) {
								if (lastItem) {
									nodeInTree = codedText;
									return;
								}
							} else if (isSecondLastNodeChild != null
									&& isSecondLastNodeChild.booleanValue()) {
								if (lastItem) {
									nodeInTree = codedText.getDefiningCode();
									return;
								}
							}
						} else {
							System.out.println("other: ?? "
									+ el.getValue().getClass().getName());
						}
					}

				}

			}
		} else if (minimumSkeleton instanceof PointEvent<?>) {
			PointEvent<?> event = (PointEvent) minimumSkeleton;

			if (pathShort.equals(event.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = event;
					return;
				}
				level = level + 1;
				findNodeInSkeletonTreeSiblings(path, event.getData(), level, lastItem);
				findNodeInSkeletonTreeSiblings(path, event.getState(), level, lastItem);
			}
		} else if (minimumSkeleton instanceof ItemSingle) {
			ItemSingle item = (ItemSingle) minimumSkeleton;

			if (pathShort.equals(item.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = item;
					return;
				}
				level = level + 1;
				findNodeInSkeletonTreeSiblings(path, item.getItem(), level, lastItem);
			}
		} else if (minimumSkeleton instanceof AdminEntry) {
			AdminEntry adminEntry = (AdminEntry) minimumSkeleton;
			
			if (pathShort.equals(adminEntry.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = adminEntry;
					return;
				}
				level = level + 1;
				findNodeInSkeletonTreeSiblings(path, adminEntry.getData(), level, lastItem);
			}
		} else if (minimumSkeleton instanceof History<?>) {
			History<?> history = (History<?>) minimumSkeleton;
			
			if (pathShort.equals(history.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = history;
					return;
				}
				level = level + 1;
				Iterator itEvents = history.getEvents().iterator();
				while (itEvents.hasNext()) {
//					if (idx == history.getEvents().size() - 1)
//						lastItem = true;
					Event event = (Event) itEvents.next();
					findNodeInSkeletonTreeSiblings(path, event, level, lastItem);
					idx++;
				}				
			}
		} else if (minimumSkeleton instanceof Action) {
			Action action = (Action) minimumSkeleton;
			
			if (pathShort.equals(action.getArchetypeNodeId())) {
				if (isLastNode != null && isLastNode.booleanValue()) {
					nodeInTree = action;
					return;
				}
				level = level + 1;
				findNodeInSkeletonTreeSiblings(path, action.getDescription(), level, lastItem);
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
	public static void deleteRedundantClusterElements(Object clusterObj,
			String path, int depth) {
		if (clusterObj == null || !(clusterObj instanceof Cluster)
				|| StringUtils.isEmpty(path))
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

	
	/**
	 * Deletes all 'leaf' nodes.
	 * 
	 * @param userInputsMap
	 * @param rmObject
	 * @param archetype
	 * @return
	 */
	public static Object deleteAllNodesNotInPath(Object rmObject, Archetype archetype) {
		List<String> list = Rm2XmlUtils.getAllArchetypeNodesList(
				archetype.getDefinition(), archetype);
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String path = it.next();
			
			if (/*deletePath && */path.endsWith("/value") && !path.endsWith("/value/value")) {
				if (path.equals("/items[openEHR-EHR-OBSERVATION.body_weight.v1 and name/value='Telesna masa']/data[at0002]/events[at0003]/data[at0001]/items[at0004 and name/value='Teža']/value")) {
					System.out.println("");
				}
				String pathNoValue = path.substring(0, path.lastIndexOf("/value")); 

				if (!pathNoValue.endsWith("/name")) //we cannot delete "name" nodes 
					deleteGeneratedObject(pathNoValue, rmObject);
			}
		}
		return rmObject;
	}
	
	
	/**
	 * Function deletes all leaf nodes that lie below 'pathShort' node.
	 * 
	 * @param rmObject
	 * @param archetype
	 * @param pathShort
	 * @return
	 */
	public static Object deleteAllFinalNodes(Object rmObject, Archetype archetype, String pathShort) {
		List<String> list = Rm2XmlUtils.getAllArchetypeNodesList(
				archetype.getDefinition(), archetype);
		Iterator<String> it = list.iterator();
		String rootPart = "";
		while (it.hasNext()) {
			String path = it.next();

			if (/*deletePath && */path.endsWith("/value") && !path.endsWith("/value/value")) {
				if (path.equals("/items[openEHR-EHR-OBSERVATION.body_weight.v1 and name/value='Telesna masa']/data[at0002]/events[at0003]/data[at0001]/items[at0004 and name/value='Teža']/value")) {
					System.out.println("");
				}
				String pathNoValue = path.substring(0, path.lastIndexOf("/value")); 

				if (!pathNoValue.endsWith("/name")) { //we cannot delete "name" nodes
					String pathShortNoValue = pathShort.substring(0, pathShort.lastIndexOf("/value"));
					if (pathNoValue.indexOf(pathShortNoValue) >= 0) {
						rootPart = pathNoValue.substring(0, pathNoValue.indexOf(pathShortNoValue));
					}
					if (!rootPart.equals("") && pathNoValue.contains(rootPart)) {
						pathNoValue = pathNoValue.substring(rootPart.length());
						if (!pathNoValue.equals(pathShort.substring(0, pathShort.lastIndexOf("/value"))))
							deleteGeneratedObject(pathNoValue, rmObject);
					}
				}
			}
		}
		return rmObject;
	}
	
	
	/**
	 * Checks if given item is last Cluster object in given list.
	 * 
	 * e.g.
	 * Element1: false
	 * Cluster2: false
	 * Element3: false
	 * Cluster4: true
	 * Element5: false
	 * 
	 * @param list
	 * @param _item
	 * @return
	 */
	private static boolean isLastClusterInList(List<Item> list, int itemIdx) {
		
		int count = -1;
		
		String firstClusterId = null;
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Item item = (Item) it.next();
			count++;
			if (!(item instanceof Cluster))
				continue;
			
			if (count == itemIdx) {
				firstClusterId = ((Cluster) item).getArchetypeNodeId();
				continue;
			}
			if (count > itemIdx && firstClusterId != null && firstClusterId.equals(((Cluster) item).getArchetypeNodeId()))
				return false;			
		}
		
		return true;		

	}
	

}
