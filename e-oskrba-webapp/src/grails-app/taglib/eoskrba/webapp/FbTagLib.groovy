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

class FbTagLib {

	static namespace = "fb"
	
	def init = {
		out << """\
			<div id="fb-root"></div>
			<script>
			  window.fbAsyncInit = function() {
			    FB.init({
			      appId      : '${grailsApplication.config.fb.appId}', // App ID
			      channelUrl : '${grailsApplication.config.grails.serverURL}/facebookResources/channel', // Channel File
			      status     : true, // check login status
			      cookie     : true, // enable cookies to allow the server to access the session
			      xfbml      : true  // parse XFBML
			    });
			    // Additional initialization code here
			  };
			  // Load the SDK Asynchronously
			  (function(d){
			     var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
			     if (d.getElementById(id)) {return;}
			     js = d.createElement('script'); js.id = id; js.async = true;
			     js.src = "//connect.facebook.net/sl_SI/all.js";
			     ref.parentNode.insertBefore(js, ref);
			   }(document));
			</script>
		"""
	}
	
	def initNonXfbml = {
		out << """\
			<div id="fb-root"></div>
			<script>(function(d, s, id) {
			  var js, fjs = d.getElementsByTagName(s)[0];
			  if (d.getElementById(id)) return;
			  js = d.createElement(s); js.id = id;
			  js.src = "//connect.facebook.net/sl_SI/all.js#xfbml=0&appId=${grailsApplication.config.fb.appId}";
			  fjs.parentNode.insertBefore(js, fjs);
			}(document, 'script', 'facebook-jssdk'));</script>
		"""
	}
	
	def publishCheckbox = { attrs, body ->
		if(attrs.enabled) {
			out << """\
				<table style="border:1px solid #A6A6A6;background-color:#DAEEF3;width:100%;">
					<tr>
						<td style="vertical-align:middle;padding:3px;width:15px;">
							<input name="${attrs.name}" type="checkbox" checked="checked" />
						</td>
						<td style="vertical-align:middle;padding:3px;">
							${body()}
						</td>
					</tr>
				</table>
			"""
		} else {
			out << """\
				<g:else>
					<table style="border:1px solid #A6A6A6;background-color:#DAEEF3;width:100%;">
						<tr>
							<td style="vertical-align:middle;padding:3px;width:15px;">
								<input name="${attrs.name}" type="checkbox" disabled="disabled" />
							</td>
							<td style="vertical-align:middle;padding:3px;color:#A6A6A6;">
								<span>${body()}</span> <a style="text-decoration:none;font-style:italic;" href="#" onclick="\$('.fb_publish_disabledInfo').toggle();return false;">O tem...</a>
								<div class="fb_publish_disabledInfo" style="display:none;padding-top:3px;">
									<span style="font-style:italic;color:black;">Uprabniški račun lahko povežete s svojim Facebook računom v <a href="#" onclick="showSettings();return false;">nastavitvah</a>.</span>
								</div>
							</td>
						</tr>
					</table>
				</g:else>
			"""
		}
	}
	
	def like = { attrs ->
		out << "<fb:like send=\"${attrs.send}\" width=\"${attrs.width}\" show_faces=\"${attrs.show_faces}\"></fb:like>"
	}
	
	def metaAppId = {
		out << "<meta property=\"fb:app_id\" content=\"${grailsApplication.config.fb.appId}\" />"
	}
	
}
