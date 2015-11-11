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
package si.pint.eoskrba.eastma.inputGeneric;

import java.io.StringWriter;
import java.util.Properties;

import junit.framework.TestCase;
import lombok.extern.log4j.Log4j;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

@Log4j
public class VelocityTestCase extends TestCase {

	public void testVelocity() {

		

		VelocityEngine ve = new VelocityEngine();
		// ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		//ve.init();
		Properties p = new Properties();
		p.setProperty("resource.loader","class");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		ve.init(p);

		VelocityContext context = new VelocityContext();
		Template template = null;
		try {
			template = ve.getTemplate("vnos-vrednosti-ACT.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		String s = sw.toString();
		log.info("string:" + s);
	}

	// public void testVelocity(){
	// /* first, we init the runtime engine. Defaults are fine. */
	//
	// Velocity.init();
	//
	// /* lets make a Context and put data into it */
	//
	// VelocityContext context = new VelocityContext();
	//
	// context.put("name", "Velocity");
	// context.put("project", "Jakarta");
	//
	// /* lets render a template */
	//
	// StringWriter w = new StringWriter();
	//
	// Velocity.mergeTemplate("pacientReminderEmail-Content.vm", context, w );
	// System.out.println(" template : " + w );
	//
	// /* lets make our own string to render */
	//
	// String s = "We are using $project $name to render this.";
	// w = new StringWriter();
	// Velocity.evaluate( context, w, "mystring", s );
	// System.out.println(" string : " + w );
	// }

}
