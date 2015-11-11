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
package eoskrba.webapp.common

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

class LogEnabledController {
	
	public static void doLogInfo(Logger log4j, String msg, String user, String role, String task, String taskContent, String taskType){
		
		MDC.put("user", user==null?"":user);
		MDC.put("userRole",role==null?"":role);
		MDC.put("task", task==null?"":task);
		MDC.put("taskContent", taskContent==null?"":taskContent);
		MDC.put("taskType", taskType==null?"":taskType);
		log4j.info(msg);
		
	}
	
	public static void doLogInfo(org.apache.commons.logging.Log logComm, java.lang.String msg, java.lang.String user, java.lang.String role, java.lang.String task, java.lang.String taskContent, java.lang.String taskType) {
		
		String logCommVirtualMDC = "["
		logCommVirtualMDC += " user:'" + user==null?"":user + "'";
		logCommVirtualMDC += " userRole:'" + role==null?"":role + "'";
		logCommVirtualMDC += " task:'" + task==null?"":task + "'";
		logCommVirtualMDC += " taskContent:'" + taskContent==null?"":taskContent + "'";
		logCommVirtualMDC += " taskType:'" + taskType==null?"":taskType + "'";
		logCommVirtualMDC += " ] "
		logComm.info(logCommVirtualMDC + msg);
		
	}
	
}
