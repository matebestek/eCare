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
package eoskrba.webapp

import  java.util.Hashtable;
import  javax.naming.*;
import  javax.naming.directory.*;
import  com.sun.jndi.dns.*;

class NsLookupService {

    static transactional = true
	
	/*   mxLookup(String hostName)
	 *   
	 *   Opravi nslookup za MX record-e nad izbranim hostName-om. Vrne število
	 *   poštnih strežnikov, ki jih podan hostName ima. Če vrne 0 lahko sklepamo,
	 *   da naveden hostName nima poštnega strežnika.
	 */
	static int mxLookup(String hostName) {
		
		int toReturn = 0;
		Hashtable env = new Hashtable();
		env.put(
			"java.naming.factory.initial",
			"com.sun.jndi.dns.DnsContextFactory"
		);
		DirContext ictx = new InitialDirContext( env );
		String[] mx = new String[1];
		mx[0] = "MX";
		try {
			Attributes attrs = ictx.getAttributes(hostName, mx);
			Attribute attr = attrs.get("MX");
			if(attr == null) return(0);
			toReturn = attr.size();
		} catch(Exception e) {
			toReturn = 0;
		}
		return toReturn;
	}
	
}
