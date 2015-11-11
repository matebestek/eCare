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
package si.pint.eoskrba.esport.inputSport.utils;

import java.io.StringReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lombok.Getter;
import lombok.Setter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;

/**
 * Pomožni razred, ki za določenega uporabnika iz študije eŠport zna pridobivati podrobnosti o njegovih
 * načrtovanih vadb (po programu)
 * 
 * @author Alex
 *
 */
public class ExerciseDetails {
	
	@Getter @Setter public String userId;
	
	@Getter @Setter public String date;

	@Getter @Setter public String type;
	@Getter @Setter public String typeCode;
	
	@Getter @Setter public String subtype;
	@Getter @Setter public String subtypeCode;
	
	@Getter @Setter public String duration;
	
	@Getter @Setter public String intensity;
	@Getter @Setter public String intensityCode;
	
	/**
	 * Skonstruira prazen ExerciseDetails, ki ima vsa polja z vrednostjo "neznano" raen userId,
	 * ki je ID uporabnika
	 */
	public ExerciseDetails(String userId) {
		this.userId        = userId;
		this.date          = "neznano";
		this.type          = "neznano";
		this.typeCode      = "neznano";
		this.subtype       = "neznano";
		this.subtypeCode   = "neznano";
		this.duration      = "neznano";
		this.intensity     = "neznano";
		this.intensityCode = "neznano";
	}
	
	/**
	 * Objektu nastavi polja tako, da ustrezajo podrobnostim vadbe, ki je bila
	 * planirana za danes
	 * 
	 * @return true, če je bilo pridoivanje vadbe uspešno; false, če vadba ni najdena
	 */
	public boolean setForToday() {
		return this.setForDate( new GregorianCalendar() );
	}
	
	/**
	 * Objektu nastavi polja tako, da ustrezajo podrobnostim vadbe za datum, ki je
	 * definiran s parametrom tipa GregorianCalendar
	 * 
	 * @param gc Datum vadbe
	 * @return true, če je bilo pridoivanje vadbe uspešno; false, če vadba ni najdena
	 */
	public boolean setForDate(GregorianCalendar gc) {
		
		// Nastavi spremenljivke
		boolean returnValue = true;
		
		// Sestavi datum iz gc
		int year  = gc.get(Calendar.YEAR);
		int month = gc.get(Calendar.MONTH) + 1;
		int day   = gc.get(Calendar.DAY_OF_MONTH);
		String exerciseDate = year + "-" + ((month<10)?("0"+month):(month)) + "-" + ((day<10)?("0"+day):(day)) + "T00";
		this.date = ((day<10)?("0"+day):(day)) + "." + ((month<10)?("0"+month):(month)) + "." + year;
		
		// Pridobi xml z vadbami po pacientovem programu
		String xml = "";
		
		try {
			xml = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastWholeXml(
				this.userId,
				"eSport",
				this.userId,
				"openEHR-EHR-OBSERVATION.sport_koledar.v1"
			);
		} catch(Exception e) {
			System.out.println("[Proces-Opomnik-Vnos-Generic] [ExerciseDetails] [getForDate] Branje koledarja pacienta " +this.userId+ " je spodletelo. [1]");
			return false;
		}
		
		if(xml == "" || xml == null) {
			System.out.println("[Proces-Opomnik-Vnos-Generic] [ExerciseDetails] [getForDate] Branje koledarja pacienta " +this.userId+ " je spodletelo. [2]");
			return false;
		}
		
		// Poišči datum vadbe v xml-ju
		xml = xml.replace("\n", "").replace("\r", "");
		int idx1 = xml.indexOf(exerciseDate);
		
		// Če vadba ni najdena vrnemo null
		if(idx1 < 0) {
			return false;
		}
		
		// Počisti vsebino xml-ja tako, da ostanejo samo podatki vadbe
		xml = xml.substring(idx1);
		int idx2 = xml.indexOf("<items archetype_node_id=\"openEHR-EHR-CLUSTER.sport_koledar.v1\" xsi:type=\"v1:CLUSTER\">");
		if(idx2 >= 0) {
			xml = xml.substring(0, idx2);
		} else {
			idx2 = xml.indexOf("</data>");
			xml = xml.substring(0, idx2);
		}
		
		// popravi vsebino xml-ja
		xml = "<?xml version=\"1.0\"?><items><items archetype_node_id=\"at0002\"><name><value>Datum vadbe</value></name><value><value>" + xml;
		
		// Odstrani namespaces-e
		xml = xml.replaceAll("xmlns=\"[^\"]*\"","");
		xml = xml.replaceAll("xmlns:xsi=\"[^\"]*\"", "");
		xml = xml.replaceAll("xsi:","");
		
		// Izlušči podatke in jih spravi v polja
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch(Exception e) {
			System.out.println("[Proces-Opomnik-Vnos-Generic] [ExerciseDetails] [getForDate] Napaka pri inicializaciji DocumentBuilder objekta.");
			return false;
		}
		
		InputSource is = new InputSource(new StringReader(xml));
		
		Document doc = null;
		try {
			doc = dBuilder.parse(is);
		} catch(Exception e) {
			System.out.println("[Proces-Opomnik-Vnos-Generic] [ExerciseDetails] [getForDate] Napaka pri parsanju xml-ja.");
			return false;
		}
		
		// Izhodno vozlišče in njegovi otroci
		Element rootNode = doc.getDocumentElement();
		NodeList rootChilds = rootNode.getChildNodes();
		
		for(int i = 0; i < rootChilds.getLength(); i++) {
			
			Node itemsNode = rootChilds.item(i);
			if (itemsNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element itemsElement = (Element) itemsNode;
				String archetypeNodeId = itemsElement.getAttribute("archetype_node_id");
				
				// Podtip vadbe
				if(archetypeNodeId.equals("at0003")) {
					
					this.subtypeCode = itemsElement.getElementsByTagName("code_string").item(0).getTextContent();
					this.subtype     = ((Element)(itemsElement.getElementsByTagName("value").item(1))).getElementsByTagName("value").item(0).getTextContent();
					
				}
				
				// Trajanje vadbe
				if(archetypeNodeId.equals("at0004")) {
					
					this.duration    = ((Element)(itemsElement.getElementsByTagName("value").item(1))).getElementsByTagName("value").item(0).getTextContent();
					this.duration = this.duration.substring(0, 5);
				}
				
				// Disciplina vadbe
				if(archetypeNodeId.equals("at0005")) {
					
					this.typeCode    = itemsElement.getElementsByTagName("code_string").item(0).getTextContent();
					this.type        = ((Element)(itemsElement.getElementsByTagName("value").item(1))).getElementsByTagName("value").item(0).getTextContent();
					
				}
				
				// Disciplina vadbe
				if(archetypeNodeId.equals("at0006")) {
					
					this.intensityCode = itemsElement.getElementsByTagName("code_string").item(0).getTextContent();
					this.intensity     = ((Element)(itemsElement.getElementsByTagName("value").item(1))).getElementsByTagName("value").item(0).getTextContent();
					
				}
				
			}
			
		}
		
		// vrni uspeh
		return returnValue;
		
	}
	
}
