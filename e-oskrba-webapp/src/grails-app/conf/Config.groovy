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
import grails.util.Environment;

import org.apache.log4j.DailyRollingFileAppender

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
					  xml: ['text/xml', 'application/xml'],
					  text: 'text/plain',
					  js: 'text/javascript',
					  rss: 'application/rss+xml',
					  atom: 'application/atom+xml',
					  css: 'text/css',
					  csv: 'text/csv',
					  all: '*/*',
					  json: ['application/json','text/json'],
					  form: 'application/x-www-form-urlencoded',
					  multipartForm: 'multipart/form-data'
					]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

String fs = File.separator // Local variable.

String targetDir = new File("target${fs}").isDirectory() ? "target${fs}".toString() : "logs-".toString() // Local
globalDirs.targetDir = targetDir
println("[eOskrba-webapp] [Config] taregtDir = " + targetDir)

String catalinaBase = System.properties.getProperty('catalina.base')
globalDirs.catalinaBase = catalinaBase
println("[eOskrba-webapp] [Config] catalinaBase = " + catalinaBase)

String logDirectory = catalinaBase ? "${catalinaBase}${fs}logs${fs}".toString() : targetDir
globalDirs.logDirectory = logDirectory
println("[eOskrba-webapp] [Config] logDirectory = " + logDirectory)

String workDirectory = catalinaBase ? "${catalinaBase}${fs}work${fs}".toString() : targetDir
globalDirs.workDirectory = workDirectory
println("[eOskrba-webapp] [Config] workDirectory = " + workDirectory)

String searchableIndexDirectory = "${workDirectory}SearchableIndex${fs}${appName}${fs}".toString()
globalDirs.searchableIndexDirectory = searchableIndexDirectory
println("[eOskrba-webapp] [Config] searchableIndexDirectory = " + searchableIndexDirectory)

// log4j configuration
log4j = {
	// Example of changing the log pattern for the default console
	// appender:
	//
	appenders {
		'null' name:'stacktrace'
		console name:'stdout', threshold: org.apache.log4j.Level.ERROR, layout:pattern(conversionPattern: '%d***[%t]***%X{user}***%X{task}***%X{taskContent}***%X{taskType}***%F***%M***%L***%r***%-5p***%c***%x***%m\n')
		// Custom log file.
		appender new DailyRollingFileAppender(
			name: 'eoskrbaWebapp',
			datePattern: "'.'yyyy-MM-dd-HH",  // See the API for all patterns.
			fileName: "${logDirectory}eoskrba-webapp.log".toString(),
			layout: pattern(conversionPattern:'%d***[%t]***%X{user}***%X{task}***%X{taskContent}***%X{taskType}***%F***%M***%L***%r***%-5p***%c***%x***%m\n')
			)
		appender new DailyRollingFileAppender(name:"eoskrbaFilters", fileName: "${logDirectory}eoskrbaFilters.log".toString(), layout:pattern(conversionPattern: '%c{2} %m%n'))		
		
	}

	error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
		   'org.codehaus.groovy.grails.web.pages', //  GSP
		   'org.codehaus.groovy.grails.web.sitemesh', //  layouts
		   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
		   'org.codehaus.groovy.grails.web.mapping', // URL mapping
		   'org.codehaus.groovy.grails.commons', // core / classloading
		   'org.codehaus.groovy.grails.plugins', // plugins
		   'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
		   'org.springframework',
		   'org.hibernate',
		   'net.sf.ehcache.hibernate'

	warn   'org.mortbay.log'
	switch( Environment.getCurrent() ) {
		case Environment.DEVELOPMENT:
			// Configure the root logger to output to stdout and appLog appenders.
			root {
				error 'stdout'
				additivity = true
			}
			//debug "org.hibernate.SQL"
			
			info eoskrbaFilters:'grails.app.filters.eoskrba.webapp.LoggingFilters'
			debug 'grails.app.services'
			debug eoskrbaWebapp:'grails.app.controllers'
			break
		case Environment.TEST:
			// Configure the root logger to only output to appLog appender.
			root {
				error 'stdout'
				additivity = true
			}
			info eoskrbaFilters:'grails.app.filters.eoskrba.webapp.LoggingFilters'
			debug 'grails.app.services'
			debug eoskrbaWebapp:'grails.app.controllers'
			break
		case Environment.PRODUCTION:
			// Configure the root logger to only output to appLog appender.
			root {
				error 'stdout'
				additivity = true
			}
			info eoskrbaFilters:'grails.app.filters.eoskrba.webapp.LoggingFilters'
			warn 'grails.app.services'
			debug eoskrbaWebapp:'grails.app.controllers'
			debug 'grails.app.services.AssetCsvService'
			debug 'grails.app.services.PersonCsvService'
			debug 'grails.app.services.InventoryCsvService'
			debug 'grails.app.services.AssetTreeService' /// @todo: remove after testing.
			break
	}
}

// set per-environment serverURL stem for creating absolute links
environments {
	
	production {
		// LDAP (Gldapo) configuration - PRODUCTION
		ldap {
			directories {
				mainLdap {
					url = "ldap://localhost:389"
					base = "dc=eoskrba,dc=pint,dc=upr,dc=si"
					userDn = "cn=admin,dc=eoskrba,dc=pint,dc=upr,dc=si"
					password = "<PASSWORD>"
				}
			}
			schemas = [eoskrba.webapp.MainLdapSchema]
		}
		// Facebook API properties
		fb {
			appId = "<AppID>"
			appSecret = "<AppSecret>"
			imgHost   = "http://<HOST>/fb" // Mora biti http, ne https!
		}
		hostServer = "<HOST>"
		hostServerProtocol = "https"
		// serverURL, ki ga potrebuje rendering plug-in in FB
		grails.serverURL = "<HOST>/${appName}"
	}
		
	development {
		// LDAP (Gldapo) configuration - DEVELOPMENT
		ldap {
			directories {
				mainLdap {
					url = "ldap://localhost:389"
					base = "dc=eoskrba,dc=pint,dc=upr,dc=si"
					userDn = "cn=admin,dc=eoskrba,dc=pint,dc=upr,dc=si"
					password = "<PASSWORD>"
				}
			}
			schemas = [eoskrba.webapp.MainLdapSchema]
		}
		// Facebook API properties
		fb {
			appId = "<AppID>"
			appSecret = "<AppSecret>"
			imgHost   = "http://<HOST>/fb" // Mora biti http, ne https!
		}
		hostServer = "<HOST>"
		//hostServer = "<HOST>"
		hostServerProtocol = "http"
		// serverURL, ki ga potrebuje rendering plug-in in FB
		//grails.serverURL = "<HOST>/${appName}"
		grails.serverURL = "<HOST>/${appName}"
	}
	
	test {
		// LDAP (Gldapo) configuration - TEST
		ldap {
			directories {
				mainLdap {
					url = "ldap://localhost:389"
					base = "dc=eoskrba,dc=pint,dc=upr,dc=si"
					userDn = "cn=admin,dc=eoskrba,dc=pint,dc=upr,dc=si"
					password = "<PASSWORD>"
				}
			}
			schemas = [eoskrba.webapp.MainLdapSchema]
		}
		// Facebook API properties
		fb {
			appId = "<AppID>"
			appSecret = "<AppSecret>"
			imgHost   = "http://<HOST>/fb" // Mora biti http, ne https!
		}
		hostServer = "<HOST>"
		hostServerProtocol = "https"
		// serverURL, ki ga potrebuje rendering plug-in in FB
		grails.serverURL = "<HOST>/${appName}"
	}

}

// CKEditor config

ckeditor {
	upload {
		basedir = "/uploads/"
		overwrite = false
		image {
			browser = true
			upload = true
			allowed = ['jpg', 'gif', 'jpeg', 'png']
			denied = []
		}
	}
}

// email

grails {
	mail {
		host = "<MAIL-SERVER>"
		port = 25
	}
}
grails.mail.default.from = "<EMAIL>"

opkp{
	security{
		encryption{
			//poslal sonce.net dne 27.2.2012: https://mail.google.com/mail/u/0/#search/has%3ayellow-bang/135a5614d93f5974
			//predlagani algoritem je AES
			key = "<KEY>"
			algorithm = "AES"
		}
	}
}
