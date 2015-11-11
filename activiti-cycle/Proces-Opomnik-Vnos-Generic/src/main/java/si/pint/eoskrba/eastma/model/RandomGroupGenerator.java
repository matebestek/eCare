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
package si.pint.eoskrba.eastma.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;

import si.pint.archetypes.service.ArchetypeService;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;

/**
 * Returns true or false depending on random array of numbers.
 * 
 * @author Blaz
 * 
 */
public class RandomGroupGenerator {

	//vsak zdravnik ima svojo randomizacijo za prvih 8 pacientov, potem se randomizacija izvaja globalno
	private static Map<String, short[]> RANDOM_ARRAY_DIABETES_DOCTOR = new HashMap();
	static {
		RANDOM_ARRAY_DIABETES_DOCTOR.put("brigita.articek@zd-zalec.si", 
				new short[] { 0, 1, 1, 0, 0, 0, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ambulanta.articek.dms@gmail.com", 
				new short[] { 0, 1, 1, 0, 0, 0, 1, 1 });
		
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("franc.bozicek@gmail.com",
				new short[] { 1, 0, 0, 0, 1, 0, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("zlatka.domitrovic@gmail.com",
				new short[] { 1, 0, 0, 0, 1, 0, 1, 1 });
		
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("radeiljaz@gmail.com",
				new short[] { 1, 1, 1, 0, 0, 1, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("suzana.pavlin3@gmail.com",
				new short[] { 1, 1, 1, 0, 0, 1, 0, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ambulanta@ivetic.si",
				new short[] { 1, 0, 1, 1, 0, 0, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("dms@ivetic.si",
				new short[] { 1, 0, 1, 1, 0, 0, 1, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("branko.jerkovic@siol.net",
				new short[] { 0, 1, 1, 0, 0, 0, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ref.ambulanta@gmail.com",
				new short[] { 0, 1, 1, 0, 0, 0, 1, 1 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("martin.kenda@zd-lj.si",
				new short[] { 1, 1, 0, 1, 1, 0, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("petra.globocnik@zd-lj.si",
				new short[] { 1, 1, 0, 1, 1, 0, 0, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("janko.kersnik@gmail.com",
				new short[] { 0, 0, 0, 1, 1, 1, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("minka.besic4@gmail.com",
				new short[] { 0, 0, 0, 1, 1, 1, 1, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("darinka.klancar@siol.com",
				new short[] { 1, 0, 0, 1, 1, 0, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ester.skof86@gmail.com",
				new short[] { 1, 0, 0, 1, 1, 0, 1, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ambulanta.kosir@siol.net",
				new short[] { 1, 1, 1, 0, 0, 0, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("polonapanjtar.dms@gmail.com",
				new short[] { 1, 1, 1, 0, 0, 0, 1, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("kravos.andrej@siol.net",
				new short[] { 1, 1, 1, 0, 0, 1, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ordinacija.kravos.dms@gmail.com",
				new short[] { 1, 1, 1, 0, 0, 1, 0, 0 });
		
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ingrid.kussotosek@zd-radece.si",				
				new short[] { 1, 1, 0, 0, 0, 1, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("mateja.golob81@gmail.com",
				new short[] { 1, 1, 0, 0, 0, 1, 0, 1 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("tanja.leskovar@telemach.net",
				new short[] { 1, 0, 1, 0, 1, 0, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("urska.odar@zd-radovljica.si",
				new short[] { 1, 0, 1, 0, 1, 0, 0, 1 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("zdenka.marincek@zd-brezice.si",
				new short[] { 1, 1, 0, 0, 0, 1, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("referencna@zd-brezice.si",
				new short[] { 1, 1, 0, 0, 0, 1, 0, 1 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("metka.osljak@gmail.com",
				new short[] { 0, 1, 0, 1, 1, 1, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("mateja.golob81",
				new short[] { 0, 1, 0, 1, 1, 1, 0, 0 });
				
		RANDOM_ARRAY_DIABETES_DOCTOR.put("davorina.petek@gmail.com",
				new short[] { 0, 0, 1, 1, 0, 0, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("lipovac.dolores@gmail.com",
				new short[] { 0, 0, 1, 1, 0, 0, 1, 1 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("romana@zavodrr.si",
				new short[] { 0, 1, 1, 0, 0, 0, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("referencna.ambulanta@zavodrr.si",
				new short[] { 0, 1, 1, 0, 0, 0, 1, 1 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("direktor@zd-ivg.si",
				new short[] { 1, 0, 0, 0, 1, 0, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("majabajc.gerl@zd-ivg.si",
				new short[] { 1, 0, 0, 0, 1, 0, 1, 1 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("mihaela.pugelj@zd-slovenskekonjice.si",
				new short[] { 0, 0, 1, 0, 1, 1, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("medicinadela.zd@siol.net",
				new short[] { 0, 0, 1, 0, 1, 1, 1, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ordinacija1_ig@t-2.net",
				new short[] { 0, 0, 0, 0, 1, 1, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("sasa.kozin@gmail.com",
				new short[] { 0, 0, 0, 0, 1, 1, 1, 1 });
				
		RANDOM_ARRAY_DIABETES_DOCTOR.put("martina.terbizan-rupnik@zd-ajdovscina.si",
				new short[] { 0, 1, 1, 0, 1, 0, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("kandus.andreja@gmail.com",
				new short[] { 0, 1, 1, 0, 1, 0, 1, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("polonca@ambulanta-grobelnik.si",
				new short[] { 1, 0, 1, 1, 1, 0, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("branka@ambulanta-grobelnik.si",
				new short[] { 1, 0, 1, 1, 1, 0, 0, 0 });
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("random",
				new short[] { 0, 1, 0, 1, 0, 1, 1, 0 });
		
		
		RANDOM_ARRAY_DIABETES_DOCTOR.put("branko.kosir",
				new short[] { 1, 0, 1, 0, 1, 0, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("brigita.mesarec",
				new short[] { 1, 1, 0, 0, 0, 1, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("metka.osljak",
				new short[] { 0, 1, 1, 0, 1, 0, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("nina.oblak",
				new short[] { 1, 0, 0, 1, 1, 0, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 7373343304712885404",
				new short[] { 1, 0, 0, 0, 1, 0, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 1118042484129873091",
				new short[] { 0, 1, 0, 0, 1, 1, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 5199149117544836085",
				new short[] { 0, 1, 0, 1, 0, 1, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 3364198291220226075",
				new short[] { 1, 0, 0, 0, 1, 1, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 7771916930906281450",
				new short[] { 0, 0, 1, 1, 0, 1, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 896640675916534589",
				new short[] { 1, 1, 0, 0, 0, 0, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 2863179651158019037",
				new short[] { 0, 0, 0, 1, 0, 1, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 2503680910484815216",
				new short[] { 1, 1, 0, 1, 1, 0, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 6310311621467045474",
				new short[] { 0, 0, 1, 0, 0, 1, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 671753139253795296",
				new short[] { 1, 1, 0, 1, 0, 0, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 6686190551993942161",
				new short[] { 0, 0, 0, 0, 1, 1, 1, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 7244174286217670488",
				new short[] { 0, 1, 1, 1, 1, 0, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 5439617104783435682",
				new short[] { 0, 0, 1, 1, 0, 1, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 381439846761649666",
				new short[] { 0, 1, 0, 1, 1, 0, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 2828165621125507860",
				new short[] { 1, 1, 0, 1, 0, 0, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 6797332066106233359",
				new short[] { 1, 1, 0, 0, 0, 1, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 8237673258541200899",
				new short[] { 1, 1, 1, 1, 0, 0, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 5919699976687010117",
				new short[] { 1, 0, 0, 0, 1, 1, 1, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 3163967486381507749",
				new short[] { 1, 1, 0, 1, 0, 1, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 5007417091719612240",
				new short[] { 1, 0, 1, 1, 0, 0, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 5320413045551148612",
				new short[] { 0, 0, 1, 0, 1, 1, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 668334781047519348",
				new short[] { 0, 1, 1, 1, 1, 0, 0, 0 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 8351756527333336409",
				new short[] { 0, 1, 0, 1, 1, 0, 0, 1 });
		RANDOM_ARRAY_DIABETES_DOCTOR.put("ZD 5587934470346899171",
				new short[] { 0, 1, 0, 1, 1, 0, 0, 1 });

	}

	// binary array of random values
	private static final short[] RANDOM_BINARY_ARRAY_ASTMA = { 1, 1, 0, 1, 1,
			1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1,
			0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1,
			1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0,
			1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0,
			1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1,
			0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0,
			0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1,
			1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0,
			0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0 };
	private static final short[] RANDOM_BINARY_ARRAY_DIABETES = { 0, 1, 0, 1,
			1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0,
			0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1,
			0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0, 1,
			0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0,
			0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1,
			0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1,
			1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0,
			0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0,
			0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1,
			1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0,
			1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1,
			1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1,
			0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0,
			1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0,
			0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1,
			0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0,
			1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0,
			0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0,
			1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1,
			1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1,
			1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1,
			1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1,
			1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1,
			1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1,
			0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0,
			0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1,
			1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1,
			1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0,
			1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1,
			0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0,
			1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0,
			0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1,
			0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1,
			1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0,
			1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0,
			0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0,
			0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1,
			0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0,
			1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1,
			0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0,
			1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0,
			1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1,
			1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1,
			0, 0, 1, 1, 1, 1 };
	private static final short[] RANDOM_BINARY_ARRAY_SHIZOFRENIJA = { 1, 0, 0,
			0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1,
			1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1,
			0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0,
			0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1,
			1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1,
			1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0,
			0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0,
			1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0,
			0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0 };
	private static final short[] RANDOM_BINARY_ARRAY_HUJSANJE = { 1, 1, 1, 0,
			0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1,
			0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1,
			0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1,
			0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1,
			0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1,
			0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1,
			0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1,
			0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1,
			1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1 };
	private static final short[] RANDOM_BINARY_ARRAY_SPORT = { 1, 1, 1, 0,
		0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1,
		0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1,
		0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1,
		0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1,
		0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1,
		0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1,
		0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0,
		1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1,
		0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0,
		1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0,
		0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1,
		1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1 };

	// path to 'included in study'
	public static final String INCLUDED_IN_STUDY_EXIST_PATH_ASTMA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0095']/value/value/text()";
	
	public static final String INCLUDED_IN_STUDY_EXIST_PATH_DIABETES = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0012']/value/value/text()";
	public static final String HC_INSTITUTION_EXIST_PATH_DIABETES = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0009']/value/value/text()";
	
//	public static String INCLUDED_IN_STUDY_HEALTHCARE_ID_EXIST_PATH_DIABETES = "count(collection(\"/db/eDiabetes\")//*[name()=\"content\" and @archetype_node_id=\"openEHR-EHR-SECTION.vkljucitev_eo.v1\"]/*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"items\" and @archetype_node_id=\"at0012\" and preceding-sibling::*[@archetype_node_id=\"at0009\" and self::*/*[name()=\"value\"]/*[name()=\"value\"]/text()=\"{0}\"  ] ]/*[name() = \"value\"]/*[name()=\"value\"]/text())";

	public static final String INCLUDED_IN_STUDY_EXIST_PATH_SHIZOFRENIJA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0012']/value/value/text()";
	public static final String INCLUDED_IN_STUDY_EXIST_PATH_HUJSANJE = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0012']/value/value/text()";
	public static final String INCLUDED_IN_STUDY_EXIST_PATH_SPORT = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0012']/value/value/text()";

	// template name
	public static final String FIRST_ENCOUNTER_TEMPLATE_ASTMA = "openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1";
	public static final String FIRST_ENCOUNTER_TEMPLATE_DIABETES = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	public static final String FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	public static final String FIRST_ENCOUNTER_TEMPLATE_HUJSANJE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	public static final String FIRST_ENCOUNTER_TEMPLATE_SPORT = "openEHR-EHR-SECTION.vkljucitev_eo.v1";

	public static boolean getRandomGroup(ArchetypeService service, String userId, String patientId)
			throws IndexOutOfBoundsException {
		short idx = getNumberOfPatientsFromDB(service, userId, patientId);

		short[] binaryArray = null;
		switch (service.getEmployeeType()) {
		case ASTMA:
			binaryArray = RANDOM_BINARY_ARRAY_ASTMA;
			break;
		case DIABETES:
			binaryArray = RANDOM_BINARY_ARRAY_DIABETES;
			break;
		case SHIZOFRENIJA:
			binaryArray = RANDOM_BINARY_ARRAY_SHIZOFRENIJA;
			break;
		case SPORT:
			binaryArray = RANDOM_BINARY_ARRAY_SPORT;
			break;
		case HUJSANJE:
//			binaryArray = RANDOM_BINARY_ARRAY_HUJSANJE;
//			break;
			return true; //all patients go to experimental group
		}
		if (idx < 0 || idx >= binaryArray.length)
			throw new IndexOutOfBoundsException("Number of patients too long!");

		return binaryArray[idx] == 1 ? true : false;
	}

	public static boolean getRandomGroup(ArchetypeService service,
			String doctorId, String hcInstitution, String userId, String patientId) throws IndexOutOfBoundsException {
		short idx = getNumberOfPatientsFromDB(service, hcInstitution, userId, patientId);

		short[] binaryArray = null;
		switch (service.getEmployeeType()) {
		case ASTMA:
			binaryArray = RANDOM_BINARY_ARRAY_ASTMA;
			break;
		case DIABETES:
			if (idx < 8) {
				if(RANDOM_ARRAY_DIABETES_DOCTOR.containsKey(doctorId)){
					binaryArray = RANDOM_ARRAY_DIABETES_DOCTOR.get(doctorId);
				} else{
					binaryArray = RANDOM_ARRAY_DIABETES_DOCTOR.get("random");
				}
				
			} else {
				binaryArray = RANDOM_BINARY_ARRAY_DIABETES;
			}

			break;
		case SHIZOFRENIJA:
			binaryArray = RANDOM_BINARY_ARRAY_SHIZOFRENIJA;
			break;
		case HUJSANJE:
			binaryArray = RANDOM_BINARY_ARRAY_HUJSANJE;
			break;
		case SPORT:
			binaryArray = RANDOM_BINARY_ARRAY_SPORT;
			break;
		}
		if (idx < 0 || idx >= binaryArray.length)
			throw new IndexOutOfBoundsException("Number of patients too long!");

		return binaryArray[idx] == 1 ? true : false;
	}

	/**
	 * @param service 
	 * @param doctorId izvajalec zdravstvenih storitev - zdravstveni dom. Za diabetes rabimo
	 * 					   randomizacijo na nivoju zdravnika nosilca - vendar samo prvih 8 pacientov.
	 * */
	private static short getNumberOfPatientsFromDB(ArchetypeService service, String hcInstitution, String userId, String patientId) {
		short numOfPatients = 0;
		try {
			String firstEncounterTemplate = null;
			String includedInStudyTemplatePath = null;
			String xquery = null;
			switch (service.getEmployeeType()) {
			case ASTMA:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_ASTMA;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_ASTMA;
				break;
			case DIABETES:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_DIABETES;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_DIABETES;
				
				//zacasna resitev, potrebno bo napisati XQUERY -------------------------->>>>>>
				String[][] actCriteriaTable = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLastAll(userId, patientId,	firstEncounterTemplate, includedInStudyTemplatePath, service.getEmployeeType());
				String[][] hcCriteriaTable = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLastAll(userId, patientId,	firstEncounterTemplate, HC_INSTITUTION_EXIST_PATH_DIABETES, service.getEmployeeType());
				
				for (int i = 0; actCriteriaTable != null && i < actCriteriaTable.length; i++) {
					String attCriteria = actCriteriaTable[i][1];
					boolean patientIncludedInStudy = Boolean.parseBoolean(attCriteria);
					if (patientIncludedInStudy && hcCriteriaTable[i][1].equals(hcInstitution)){
						numOfPatients++;
					}
				}				
				
//				xquery = MessageFormat.format(INCLUDED_IN_STUDY_HEALTHCARE_ID_EXIST_PATH_DIABETES, new Object[]{doctorId}) ;//za določeno zdravstveno ustanovo
//				Object ret = null;
//				ret = DBManager.getInstance(
//						SystemType.EOSKRBAPROCESSENGINE).getSingleValue(
//						userId, patientId,					
//						EMPLOYEE_TYPE_ENUM.DIABETES, MessageFormat.format(xquery, new Object[]{doctorId}) );
//				numOfPatients = Short.parseShort((String)ret);
//				List<User> patients = UserRegistryFactory.getDoctorsPatientsFromLDAP(doctorId);
//				numOfPatients = (patients != null)?(short)patients.size():0;
				return numOfPatients;
				///<<<<<<---------------------------------------
				
			case SHIZOFRENIJA:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_SHIZOFRENIJA;
				break;
			case SPORT:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_SPORT;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_SPORT;
				break;
			case HUJSANJE:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_HUJSANJE;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_HUJSANJE;
			}

			String[][] actCriteriaTable = DBManager.getInstance(
					SystemType.EOSKRBAPROCESSENGINE).readLastAll(
					userId, patientId,
					firstEncounterTemplate, includedInStudyTemplatePath,
					service.getEmployeeType());
			for (int i = 0; actCriteriaTable != null
					&& i < actCriteriaTable.length; i++) {
				String attCriteria = actCriteriaTable[i][1];
				boolean patientIncludedInStudy = Boolean
						.parseBoolean(attCriteria);
				if (patientIncludedInStudy){
					numOfPatients++;
				}
					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numOfPatients;
	}
	
	private static short getNumberOfPatientsFromDB(ArchetypeService service, String userId, String patientId) {
		short numOfPatients = 0;
		try {
			String firstEncounterTemplate = null;
			String includedInStudyTemplatePath = null;

			switch (service.getEmployeeType()) {
			case ASTMA:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_ASTMA;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_ASTMA;
				break;
			case DIABETES:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_DIABETES;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_DIABETES;
				break;
			case SHIZOFRENIJA:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_SHIZOFRENIJA;
				break;
			case SPORT:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_SPORT;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_SPORT;
				break;
			case HUJSANJE:
				firstEncounterTemplate = FIRST_ENCOUNTER_TEMPLATE_HUJSANJE;
				includedInStudyTemplatePath = INCLUDED_IN_STUDY_EXIST_PATH_HUJSANJE;
			}

			String[][] actCriteriaTable = DBManager.getInstance(
					SystemType.EOSKRBAPROCESSENGINE).readLastAll(
					userId, patientId,
					firstEncounterTemplate, includedInStudyTemplatePath,
					service.getEmployeeType());
			for (int i = 0; actCriteriaTable != null
					&& i < actCriteriaTable.length; i++) {
				String attCriteria = actCriteriaTable[i][1];
				boolean patientIncludedInStudy = Boolean
						.parseBoolean(attCriteria);
				if (patientIncludedInStudy)
					numOfPatients++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numOfPatients;
	}

	public static void main(String[] args) {
		for (int m = 0; m < 50; m++) {
			int ctr = 0;
			Short[] array = new Short[8];// št. vseh userjev

			while (ctr != 4) {// koliko v kontrolni skupini
				ctr = 0;
				for (int i = 0; i < 8; i++) {
					boolean val = RandomUtils.nextBoolean();
					if (val)
						ctr++;
					array[i] = val ? (short) 1 : (short) 0;
				}
			}
			System.out.print("RANDOM_ARRAY_DIABETES_ORGANISATION.put(\"ZD "
					+ RandomUtils.nextLong() + "\",new short[]{");
			for (int i = 0; i < 8; i++) {
				System.out.print(array[i]);
				if (i < 7) {
					System.out.print(",");
				}

			}
			System.out.print("});\n");
		}
	}
}
