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

class LiveChatWidgetService {

    static transactional = true

	static final long    SESSION_OFFLINE_TIME = 10000
	static final boolean VERBOSE              = true
	static final boolean ON                   = true

    def doAll() {
		if(ON) {
			try {
				cleanAbandonedAndUnassigned()
			} catch(Exception e) { }
			try {
				endHalfOpened()
			} catch(Exception e) { }
		}
    }
	
	// Finds chat sessions opened by patients, never assigned and later
	// abandoned by its creators and destroys them
	def cleanAbandonedAndUnassigned() {
		def chatSessions = LiveChatSession.findAllByAssigned(false)
		chatSessions.each {
			def ping = LiveChatPing.findByPersonId(it.patientId)
			long diff = ((new Date()).getTime()) - ping.timestamp.getTime()
			if(diff > SESSION_OFFLINE_TIME) {
				report("Deleted chat session " + it.id + " > Session unassigned and abandoned.");
				it.delete(flush:true)
			}
		}
	}
	
	// Finds active (not ended) assigned sessions where one of participants
	// stopped sending ping and ends them
	def endHalfOpened() {
		def chatSessions = LiveChatSession.findAllByEndedAndAssigned(false,true)
		chatSessions.each {
			def patientPing  = LiveChatPing.findByPersonId(it.patientId)
			def assigneePing = LiveChatPing.findByPersonId(it.assigneeId)
			long currentTime = (new Date()).getTime()
			long patientDiff = currentTime - patientPing.timestamp.getTime()
			long assigneeDiff = currentTime - assigneePing.timestamp.getTime()
			if(patientDiff > SESSION_OFFLINE_TIME || assigneeDiff > SESSION_OFFLINE_TIME) {
				report("Closed chat session " + it.id + " > Session half opened.");
				it.ended = true
				it.save(flush:true)
			}
		}
	}
	
	def report(String msg) {
		if(VERBOSE) System.out.println("[eOskrba-webapp] [liveChatWidgetService] " + msg)
	}
	
}
