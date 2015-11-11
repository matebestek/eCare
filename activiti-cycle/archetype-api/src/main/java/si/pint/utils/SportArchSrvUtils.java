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
package si.pint.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.esport.Vadba;

public class SportArchSrvUtils {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static Map prepareCalendarTemplateXml(List exerciseProgramList,boolean realDataNotTemplate) {
		
		if (exerciseProgramList == null)
			return null;
		
		Map<ArcheDataPath, Object> mapOfAllValues = new LinkedHashMap<ArcheDataPath, Object>();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Iterator it = exerciseProgramList.iterator();
		while (it.hasNext()) {
			Object el = it.next();
			if (!(el instanceof Vadba))
				continue;
			
			Vadba v = (Vadba) el;
			
			// program 1
			mapOfAllValues.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0001]/value"), v.getProgram() == 1 ? "at0007" : "at0008");
			
			if (realDataNotTemplate) {
				mapOfAllValues.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0002]/value"), v.date);
			} else {
				//create date depening on week & day
				c.set(Calendar.DAY_OF_MONTH, v.getTeden());
				c.set(Calendar.MONTH, v.getDan() - 1);
					
				// Datum vadbe 1
				mapOfAllValues.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0002]/value"), sdf.format(c.getTime()));
			}
			// Podtip vadbe 1
			String podtip = "";
			switch (v.getPodtip()) {
			case 1 : 
				podtip = "at0009";
				break;
			case 2 : 
				podtip = "at0010";
				break;
			case 3 : 
				podtip = "at0011";
				break;
			case 4 : 
				podtip = "at0012";
				break;
			default:
				break;
				
			} 
			
			mapOfAllValues.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0003]/value"), podtip);
			
			// Trajanje vadbe 1
			if (v.getTrajanje() < 1440 && v.getTrajanje() >= 0) {
				
				int hours = v.getTrajanje() / 60;
				int minutes = v.getTrajanje() - (hours * 60); 
				
				String hoursTxt = hours < 10 ? "0" + hours : hours + "";
				String minutesTxt = minutes < 10 ? "0" + minutes : minutes + "";
				
				mapOfAllValues.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0004]/value"), hoursTxt + ":" + minutesTxt + ":" + "00");
				
			}
			
			// Disciplina 1
			mapOfAllValues.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0005]/value"), v.getDisciplina() == 1 ? "at0013" : "at0014");
			
			// Intenzivnost vadbe 1
			String intenzivnost = "";
			switch (v.getIntenzivnost()) {
			case 1 : 
				intenzivnost = "at0015";
				break;
			case 2 : 
				intenzivnost = "at0016";
				break;
			case 3 : 
				intenzivnost = "at0017";
				break;
			case 4 : 
				intenzivnost = "at0018";
				break;
			case 5 : 
				intenzivnost = "at0019";
				break;				
			default:
				break;
				
			} 			
			
			mapOfAllValues.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0006]/value"), intenzivnost);
			
		}
		
		return mapOfAllValues;
		
	}
	
	public static final String RETURN_TRAINING_PROGRAM_XQUERY = "for $content in (doc(\"/db/sportCalendarDb/sportCalendar.xml\")//*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-OBSERVATION.sport_koledar.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"events\" and @archetype_node_id=\"at0002\"]/*[name()=\"data\" and @archetype_node_id=\"at0003\"]/*[name()=\"items\"]/*[name()=\"items\" and @archetype_node_id=\"at0001\"]/*[name() = \"value\"]/*[name()=\"defining_code\"]/*[name() = \"code_string\"]/text()[contains(., \"{0}\")])" +
				"let $foonode := <vadba>" +
						"<program>'{'$content'}'</program>" +
						"<datumVadbe>'{'$content/../../../../../*[name()=\"items\" and @archetype_node_id=\"at0002\"]/*[name() = \"value\"]/*[name() = \"value\"]/text()'}'</datumVadbe>" +
						"<podtip>'{'$content/../../../../../*[name()=\"items\" and @archetype_node_id=\"at0003\"]/*[name() = \"value\"]/*[name()=\"defining_code\"]/*[name() = \"code_string\"]/text()'}'</podtip>" +
						"<trajanje>'{'$content/../../../../../*[name()=\"items\" and @archetype_node_id=\"at0004\"]/*[name() = \"value\"]/*[name() = \"value\"]/text()'}'</trajanje>" +
						"<disciplina>'{'$content/../../../../../*[name()=\"items\" and @archetype_node_id=\"at0005\"]/*[name() = \"value\"]/*[name()=\"defining_code\"]/*[name() = \"code_string\"]/text()'}'</disciplina>" +
						"<intenzivnost>'{'$content/../../../../../*[name()=\"items\" and @archetype_node_id=\"at0006\"]/*[name() = \"value\"]/*[name()=\"defining_code\"]/*[name() = \"code_string\"]/text()'}'</intenzivnost>" +
					"</vadba>" +
					"return $foonode";
	
			
	public static final String TRAINING_PROGRAM_LOW = "at0007"; //nizka intenzivnost
	public static final String TRAINING_PROGRAM_HIGH = "at0008"; //visoka intenzivnost
	
	public enum TRAINING_PROGRAM {
	    LOW, HIGH;
	    
	    public String toAttributeId() {
	        switch(this) {
	        	case LOW:   return "at0007";
	        	case HIGH:  return "at0008";
	        	default: return null;
	        }
	    }
	}
	
	
	public static List readExerciseListFromXML(TRAINING_PROGRAM program, String user) {
		List result = null;
		List vadbaObjList = new LinkedList(); 
		try {					

			Object[] xqueryParams = {program.toAttributeId()};
			String xqueryFinal = MessageFormat.format(RETURN_TRAINING_PROGRAM_XQUERY, xqueryParams);
			result = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).getMultiValue(
										   user, 
										   user, 
										   EMPLOYEE_TYPE_ENUM.HUJSANJE, 
										   xqueryFinal);
			
			Iterator it = result.iterator();
			
					
			while (it.hasNext()) {
		
				String object = (String) it.next();
				Vadba v = new Vadba();
				Document vadbaXML = parseXML(object);
				NodeList children = vadbaXML.getDocumentElement().getChildNodes();
				
				for(int i=0; i<children.getLength(); i++){

					String nodeName = children.item(i).getNodeName();
					if (nodeName.equals("#text"))
						continue;
					
					String nodeValue = "";
					if (children.item(i).getChildNodes() != null && children.item(i).getChildNodes().getLength() > 0)
						nodeValue = children.item(i).getFirstChild().getNodeValue();
					
					if (nodeName == null) {
						continue;
					} 
					else if (nodeName.equals("program")) {
						v.setProgram(nodeValue.equals("at0007") ? 1 : 2);
					}
					else if (nodeName.equals("datumVadbe")) {
						String[] s = nodeValue.split("-");
						v.setTeden(Integer.parseInt(s[2].split("T")[0]));
						v.setDan(Integer.parseInt(s[1]));
					}
					else if (nodeName.equals("podtip")) {
						if (nodeValue.equals("at0009"))
							v.setPodtip(1);
						else if (nodeValue.equals("at0010"))
							v.setPodtip(2);						
						else if (nodeValue.equals("at0011"))
							v.setPodtip(3);
						else if (nodeValue.equals("at0012"))
							v.setPodtip(4);						
					}
					else if (nodeName.equals("trajanje")) {
						String[] s = nodeValue.split(":");
						int hours = Integer.parseInt(s[0]);
						int minutes = Integer.parseInt(s[1]);
						v.setTrajanje(60*hours + minutes);
					}
					else if (nodeName.equals("disciplina")) {
						v.setDisciplina(nodeValue.equals("at0013") ? 1 : 2);
					}
					else if (nodeName.equals("intenzivnost")) {
						if (nodeValue.equals("at0015"))
							v.setIntenzivnost(1);
						else if (nodeValue.equals("at0016"))
							v.setIntenzivnost(2);						
						else if (nodeValue.equals("at0017"))
							v.setIntenzivnost(3);
						else if (nodeValue.equals("at0018"))
							v.setIntenzivnost(4);			
						else if (nodeValue.equals("at0019"))
							v.setIntenzivnost(5);								
					}					
				}
				

				vadbaObjList.add(v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return vadbaObjList;
	}
	
	/** Boštjan Povh za preračun koledarja */
	public static void doCalculateCalendar(List vadbe,String zac,String prviDana) {
		try {
			int prviDan = (prviDana.equals("at0006") ? 0 : (prviDana.equals("at0007") ? 1 : (prviDana.equals("at0008") ? 2 : 0)));
			Date zacetek = sdf.parse(zac);
			int dan1 = 1+prviDan;
			int dan2 = 2+prviDan;
			int dan3 = 3+prviDan;
			int dan4 = 4+prviDan;
			int dan5 = 5+prviDan;
			int dan6 = 6+prviDan;
			int dan7 = 7+prviDan;
			dan1 = dan1+1;
			dan2 = dan2+1;
			dan3 = dan3+1;
			dan4 = dan4+1;
			dan5 = dan5+1;
			dan6 = dan6+1;
			dan7 = dan7+1;
			
			
			
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(zacetek);
			System.out.println();
			// nastavim osnovni datum za izracune
			for (;;) {
				if (gc.get(Calendar.DAY_OF_WEEK)==dan1) {
					gc.add(Calendar.DAY_OF_YEAR, -dan1);
					gc.add(Calendar.WEEK_OF_YEAR, -1);
					break;
				}
				gc.add(Calendar.DAY_OF_YEAR, 1);
			}
			//System.out.println(new Date(gc.getTimeInMillis()));
			GregorianCalendar cc = new GregorianCalendar();
			Vadba v;
			for (int i = 0; i < vadbe.size(); i++) {
				v = (Vadba)vadbe.get(i);
				cc.setTime(gc.getTime());
				cc.add(Calendar.WEEK_OF_YEAR, v.teden);
				cc.add(Calendar.DAY_OF_YEAR, (v.dan==1 ? dan1 :(v.dan==2 ? dan2 :(v.dan==3 ? dan3 : (v.dan==4 ? dan4 : (v.dan==5 ? dan5 :  (v.dan==6 ? dan6 :  (v.dan==7 ? dan7 : v.dan))))))));
				//System.out.println(v.teden+"   "+v.dan+"   "+cc.getTime());
				v.date = SportArchSrvUtils.sdf.format(cc.getTime());
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static LinkedList<Vadba> doImport(File file,String charset,String sepP,int program) {
		LinkedList<Vadba> vadbe = new LinkedList<Vadba>();
		String sep = ",";
		if (sepP!=null) {
			sep = sepP.toString();
		}

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
			String [] nextLine;
			String line;
			int n=0;
			int skipLine = 1;
			String qsep = sep;
			
		    while ((line = br.readLine()) != null) {
		    	if (n>=skipLine) {
		    		try {
				    	nextLine = line.split(qsep,0);
			        	Vadba v = new Vadba();
			        	v.program = program;
			        	v.teden = Integer.parseInt(nextLine[0]);
			        	v.dan = Integer.parseInt(nextLine[1]);
			        	v.disciplina = Integer.parseInt(nextLine[2]);
			        	v.podtip = Integer.parseInt(nextLine[3]);
			        	v.trajanje = Integer.parseInt(nextLine[4]);
			        	v.intenzivnost = Integer.parseInt(nextLine[5]);
			        	vadbe.add(v);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
		        n++;
		    }
		    br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vadbe;
	}
	
	
	/**
	 * Parses one String object that contains one Vadba element.
	 * 
	 * @param xml
	 * @return
	 */
	private static Document parseXML(String xml) {
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
			return doc;			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
