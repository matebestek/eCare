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
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j;
import si.pint.archetypes.init.Data2RmUtils;


@Log4j
public class TestData2RmUtils extends TestCase {
	
	public void testextractAttFromNode(){
		String att = "[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']";
		assertNotSame("openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina'",Data2RmUtils.extractAttFromNode(att));
		att = "[openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1]";
		assertEquals("openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1",Data2RmUtils.extractAttFromNode(att));
		att = "[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']";
		assertEquals("openEHR-EHR-OBSERVATION.height.v1", Data2RmUtils.extractAttFromNode(att));
	
	}
	
	
	public void testisLastAttOnPath(){
		String path = "/data[at0001]/events[at0002]/data[at0003]/items[at0127]/items[openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1]/items[at0032]/items[at0004]/value";
		String att = "at0032";
		assertFalse(Data2RmUtils.isLastAttOnPath(path, att));
		att = "at0004";
		assertTrue(Data2RmUtils.isLastAttOnPath(path, att));
		assertNull(Data2RmUtils.isLastAttOnPath(path, null));
		att = "adasddasd";
		assertFalse(Data2RmUtils.isLastAttOnPath(path, att));
		
	}
	
	public void testisSecondLastAttOnPath(){
		String path = "/data[at0001]/events[at0002]/data[at0003]/items[at0127]/items[openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1]/items[at0032]/items[at0004]/value";
		String att = "at0032";
		assertTrue(Data2RmUtils.isSecondLastAttOnPath(path, att));
		att = "at0004";
		assertFalse(Data2RmUtils.isSecondLastAttOnPath(path, att));
		att = "dfaaddas";
		assertFalse(Data2RmUtils.isSecondLastAttOnPath(path, att));
		assertNull(Data2RmUtils.isSecondLastAttOnPath(path, null));
		
	}
	
	public void testfindLastArchetypeIdFromPath(){
		String path = "/data[at0001]/events[at0002]/data[at0003]/items[at0127]/items[openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1]/items[at0032]/items[at0004]/value";
		String output = "openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1";
		assertEquals(output, Data2RmUtils.findLastArchetypeIdFromPath(path));
		
		path = "/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.body_weight.v1]/items[openEHR-EHR-CLUSTER.asthma_control_test_questionary.v1]/items[at0032]/items[at0004]/value";		
		assertEquals(output, Data2RmUtils.findLastArchetypeIdFromPath(path));
		
		path = "/items[openEHR-EHR-OBSERVATION.body_weight.v1 and name/value='Telesna masa']/data[at0002]";
		output = "openEHR-EHR-OBSERVATION.body_weight.v1";
		assertEquals(output, Data2RmUtils.findLastArchetypeIdFromPath(path));
		
	}
	
	public void testisAttOnPath(){
		String path = "/data[at0002]/events[at0028]/data[at0029]/item[at0030]/value/defining_code"; 
		String searchedAttName = "at0029";
		assertTrue(Data2RmUtils.isAttOnPath(path, searchedAttName));
		assertTrue(Data2RmUtils.isAttOnPath(path, "at0002"));
		assertFalse(Data2RmUtils.isAttOnPath(path, "at0012"));
	}
	
	public void testfindFirstValueNode(){
		String wholePath = "/data[at0002]/events[at0028]/data[at0029]/item[at0030]/value/defining_code";
		String lastAttPath = "/data[at0002]/events[at0028]/data[at0029]/item[at0030]";
		String expected = "/item[at0030]/value";
		assertEquals(expected, Data2RmUtils.findFirstValueNode(wholePath, lastAttPath)); 
	}
	
	public void testfindLastAttOnPath(){
		String path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]/value";
		String s = Data2RmUtils.findLastAttOnPath(path);
		assertEquals("/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]", s);
		
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]";
		s = Data2RmUtils.findLastAttOnPath(path);
		assertEquals(path, s);
	}
	
	public void testfindSubpathFromNode(){
		String path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]";
		int depth = 0;
		String s = Data2RmUtils.findSubpathFromNode(path, depth);
		assertEquals(path, s);
		
		depth = 1;
		s = Data2RmUtils.findSubpathFromNode(path, depth);
		assertEquals("/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]", s);
		
		depth = 3;
		s = Data2RmUtils.findSubpathFromNode(path, depth);
		assertEquals("/events[at0002]/state[at0013]/items[at0019]", s);
		
		depth = 5;
		s = Data2RmUtils.findSubpathFromNode(path, depth);
		assertEquals("/items[at0019]", s);
		
		depth = 6;
		s = Data2RmUtils.findSubpathFromNode(path, depth);
		assertNull(s);
		
	}
	
	public void testisSecondLastNodeOnPath(){
		String path = "/data[at0002]/events[at0028]/data[at0001]/item[at0004]/value";
		int depth = 3;
		Boolean b = Data2RmUtils.isSecondLastNodeOnPath(path, depth); 
		assertTrue(b);
		
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]";
		depth = 4;
		b = Data2RmUtils.isSecondLastNodeOnPath(path, depth);
		assertTrue(b);
		
		b = Data2RmUtils.isSecondLastNodeOnPath(path, 3);
		assertFalse(b);
		
		b = Data2RmUtils.isSecondLastNodeOnPath(path, 5);
		assertFalse(b);
		
	}
	
	public void testisLastNodeOnPath(){
		String path = "/data[at0002]/events[at0028]/data[at0001]/item[at0004]/value";
		String expected = "item[at0004]";
		int depth = 4;
		Boolean b = Data2RmUtils.isLastNodeOnPath(path, depth);
		assertTrue(b);
		
		
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]/value";
		depth = 6;
		b = Data2RmUtils.isLastNodeOnPath(path, depth);
		assertTrue(b);
		
		depth = 4;
		b = Data2RmUtils.isLastNodeOnPath(path, depth);
		assertFalse(b);		
		
		depth = -1;
		b = Data2RmUtils.isLastNodeOnPath(path, depth);
		assertNull(b);
	}
	
	public void testfindAttNameFromPath(){
		String path = "/data[at0002]/events[at0028]/data[at0001]/item[at0004]/value";
		String expected = "data[at0001]";
		int depth = 2;
		String s = Data2RmUtils.findAttNameFromPath(path, depth);
		assertEquals(expected, s);
		
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]";
		depth = 5;
		s = Data2RmUtils.findAttNameFromPath(path, depth);
		assertEquals("items[at0019]", s);
		
		s = Data2RmUtils.findAttNameFromPath(path, --depth);
		assertEquals("state[at0013]", s);
		
		s = Data2RmUtils.findAttNameFromPath(path, --depth);
		assertEquals("events[at0002]", s);
		
		s = Data2RmUtils.findAttNameFromPath(path, --depth);
		assertEquals("data[at0001]", s);
		
		s = Data2RmUtils.findAttNameFromPath(path, --depth);
		assertEquals("items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']", s);
		
		s = Data2RmUtils.findAttNameFromPath(path, --depth);
		assertEquals("content[openEHR-EHR-SECTION.clinical_ra.v1]", s);
		
		path = "/content[openEHR-EHR-SECTION.pulmonary_ra.v1]/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]";
		depth = 1;
		assertEquals("items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]", Data2RmUtils.findAttNameFromPath(path, depth));
		
	}
	
	public void testfindSubPath(){
		//String path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/data[at0003]/items[at0004 and name/value='Višina/Dolžina']/value/name";
		
		String path ="/data[at0002]/events[at0028]/data[at0001]/item[at0004]/value";
		int depth = 2;
		String expected = "/data[at0002]/events[at0028]/data[at0001]";
		assertEquals(expected, Data2RmUtils.findSubPath(path, depth));
		
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]/items[at0019]";
		depth = 5;
		String s = Data2RmUtils.findSubPath(path, depth);
		assertEquals(path, s);
		
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]/state[at0013]";
		s = Data2RmUtils.findSubPath(path, --depth);
		assertEquals(path, s);
	
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]/events[at0002]";
		s = Data2RmUtils.findSubPath(path, --depth);
		assertEquals(path, s);
	
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']/data[at0001]";
		s = Data2RmUtils.findSubPath(path, --depth);
		assertEquals(path, s);
		
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Višina/Dolžina']";
		s = Data2RmUtils.findSubPath(path, --depth);
		assertEquals(path, s);
	
		path = "/content[openEHR-EHR-SECTION.clinical_ra.v1]";
		s = Data2RmUtils.findSubPath(path, --depth);
		assertEquals(path, s);
	}

}
