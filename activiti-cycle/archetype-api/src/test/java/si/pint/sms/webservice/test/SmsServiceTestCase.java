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
package si.pint.sms.webservice.test;

import java.rmi.RemoteException;
import junit.framework.TestCase;
import si.pint.eoskrba.conf.SystemConstant;
import si.pint.sms.webservice.SmsServiceUtils;
import si.pint.sms.webservice.SmswsPortType;


public class SmsServiceTestCase extends TestCase {

	public void testSmsService() { 
		SmswsPortType port;
		try {
			port = SmsServiceUtils.smsServiceInit();  
			String test = port.send_sms("PHONE-NUMBER", "Kaze se poslabsanje astme. Mora vas pregledati zdravnik. Prosimo, da se obrnete na izbranega zdravnika ali dezurno ambulanto.", SystemConstant.SMS_SECRET.toString());
		} catch (RemoteException e) {
			e.printStackTrace();
			fail();
		}
						
	}
}
