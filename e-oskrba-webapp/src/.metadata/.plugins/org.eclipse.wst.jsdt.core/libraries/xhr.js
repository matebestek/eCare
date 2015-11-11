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
/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************
 *
 * Based on information from https://developer.mozilla.org/En/XMLHttpRequest
 * and http://msdn2.microsoft.com/en-us/library/ms533062.aspx
 **/

/**
* function createRequest
* @type XMLHttpRequest
* @memberOf Window
*/
Window.prototype.createRequest=new function(){return new XMLHttpRequest();};
/**
* Object XMLHttpRequest
* @super Global
* @type constructor
* @memberOf Global
*/
XMLHttpRequest.prototype=new Object();
function XMLHttpRequest(){};

/**
 * function onreadystatechange
 * @memberOf XMLHttpRequest
 */
XMLHttpRequest.prototype.onreadystatechange=function(){};
/**
 * property readyState
 * @type Number
 * @memberOf XMLHttpRequest
 */
XMLHttpRequest.prototype.readyState=0;
/**
 * property responseText
 * @type String
 * @memberOf XMLHttpRequest
 */
XMLHttpRequest.prototype.responseText="";
/**
 * property responseXML
 * @type Document
 * @memberOf XMLHttpRequest
 */
XMLHttpRequest.prototype.responseXML=new Document();
/**
 * property status
 * @type Number
 * @memberOf XMLHttpRequest
 */
XMLHttpRequest.prototype.status=0;
/**
 * property statusText
 * @type String
 * @memberOf XMLHttpRequest
 */
XMLHttpRequest.prototype.statusText="";
/**
 * function abort()
 * @memberOf XMLHttpRequest
 */
XMLHttpRequest.prototype.abort=function(){};
/**
* function getAllResponseHeaders()
* @type String
* @memberOf XMLHttpRequest
*/
XMLHttpRequest.prototype.getAllResponseHeaders=function(){return "";};
/**
* function open(method, url, async, username, password)
* @param {String} method
* @param {String} url
* @param {Boolean} optional async
* @param {String} optional username
* @param {String} optional password
* @memberOf XMLHttpRequest
*/
XMLHttpRequest.prototype.open=function(method, url, async, username, password){};
/**
* function send(body)
* @param {Object} body
* @memberOf XMLHttpRequest
*/
XMLHttpRequest.prototype.send=function(body){};
/**
* function setRequestHeader(header,value)
* @param {String} header
* @param {String} value
* @memberOf XMLHttpRequest
*/
XMLHttpRequest.prototype.setRequestHeader=function(header,value){};
/**
* function getAllResponseHeaders()
* @param {String} header
* @type String
* @memberOf XMLHttpRequest
*/
XMLHttpRequest.prototype.getResponseHeader=function(header){return "";};
