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
package si.pint.eoskrba.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Subject's role.
 * 
 * @author Blaz
 *
 */
 public class Role implements Serializable{
	
	private String name;
	private String id;
	private static List<Role> roleList = new ArrayList<Role>();
	
	public static final Role PATIENT = new Role("patient", "1"); 
	public static final Role DOCTOR = new Role("doctor", "2");
	public static final Role CAREMANAGER = new Role("caremanager", "3");
	
	private Role(String _name, String _id) {
		this.name = _name;
		this.id = _id;
		roleList.add(this);
	}
	
	public static String getIdFromRole(Role r) {
		Iterator<Role> roles = roleList.iterator();
		while (roles.hasNext()) {
			Role role = roles.next();
			if (role.equals(r))
				return role.getId();
		}
		return null;
	}
	
	public static Role getRoleFromId(String roleId) {
		Iterator<Role> roles = roleList.iterator();
		while (roles.hasNext()) {
			Role role = roles.next();
			if (role.getId().equals(roleId))
				return role;
		}
		return null;
	}
	
	public static Role getRoleFromName(String roleName) {
		Iterator<Role> roles = roleList.iterator();
		while (roles.hasNext()) {
			Role role = roles.next();
			if (role.getName().equals(roleName))
				return role;
		}
		return null;
	}
	
	@Override
	public boolean equals(Object objToCompare) {
		if (!(objToCompare instanceof Role))
			return false;
		if (this.getId().equals(((Role) objToCompare).getId()))
			return true;
		return false;
	}

	public String getName() {
		return name;
	}

	private String getId() {
		return id;
	}
	
	
}
