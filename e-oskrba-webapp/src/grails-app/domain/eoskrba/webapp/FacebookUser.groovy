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
/*   Aleksander Bešir
 *   alex.besir@gmail.com
 *   
 *   FacebookUser domenski objekt je struktura z hranjenje podatkov, ki jih dobimo s
 *   Facebook API-jem v objektu User Object (ki ga dobimo npr. preko Graph API klica /me).
 *   
 *   FacebookUser vsebuje le prilastke, ki jih je moč pridobiti brez access_token-a. Ko
 *   uporabnik ustvari povezavo svojega računa s Facebook računom so sicer tudi drugi
 *   prilastki na voljo, ampak webapp prihodnjič od uporabnika ne zahteva več prijave
 *   v Facebook, kar pomeni, da prihodnjič nima access_token-a, ki bi bil potreben, za
 *   update-anje ostalih prilastkov (podatki se lahko namreč spremenijo čez čas).
 */

package eoskrba.webapp

class FacebookUser {
	
	// Id UserData objekta, ki predstavlja uporabnika v webappu
	String userId
	
	// FB API: The user's Facebook ID
	String me_id
	
	// FB API: The user's full name
	String me_name
	
	// FB API: The URL of the profile for the user on Facebook
	String me_link
	
	// FB API: The user's Facebook username
	String me_username

    static constraints = {
		userId(blank:false)
		me_id(blank:false)
		me_name(blank:false)
		me_link(blank:false)
		me_username(blank:false)
    }
	
}
