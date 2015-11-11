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
package si.pint.utils;

import java.io.UnsupportedEncodingException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Used for encrypting things to store in the database
 */
@Log4j
public class SecureCodec {
	
	public String decode(String coded){
		byte[] b1 = null;
		try {
			b1 = Base64.decodeBase64(coded.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] iv = new byte[32];
			
		byte[] data;
		data = new byte[b1.length-32];
		
		System.arraycopy(b1, 0, iv, 0, iv.length);//vzamem IV vektor
		System.arraycopy(b1, iv.length, data, 0, data.length);//spustim IV in vzamem samo šifrirane podatke		

		//sedaj moram dešifrirat po rijndael-u
		RijndaelEngine re = new RijndaelEngine(256);
		int blockSize = re.getBlockSize();
		PaddedBufferedBlockCipher b = new PaddedBufferedBlockCipher(
				new CBCBlockCipher(re));
		KeyParameter kp = null;
		try {
			kp = new KeyParameter(getPassword().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ParametersWithIV paramsWithIv = new ParametersWithIV(kp, iv);
		b.init(false, paramsWithIv);//dešifriram

		byte[] out = new byte[b.getOutputSize(data.length)];

		int len = 0;
		try {
			len = b.processBytes(data, 0, data.length, out, 0);
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		try {
			len += b.doFinal(out, len);
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		try {
			return new String(out,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		//preberem kot JSON strukturo
//		JSONObject json = null;
//		try {
//			json = new JSONObject(new String(out,"UTF-8"));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return json;
	}

	public String encode(String str) {
		
		RijndaelEngine re = new RijndaelEngine(256);
		int blockSize = re.getBlockSize();
		PaddedBufferedBlockCipher b = new PaddedBufferedBlockCipher(
				new CBCBlockCipher(re));
		// BufferedBlockCipher b = new BufferedBlockCipher(new
		// CBCBlockCipher(re));
		KeyParameter kp = null;
		try {
			kp = new KeyParameter(getPassword().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ParametersWithIV paramsWithIv = new ParametersWithIV(kp, new byte[32]);
		b.init(true, paramsWithIv);

		byte[] out = null;
		try {
			out = new byte[b.getOutputSize(str.getBytes("UTF-8").length)];
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int len = 0;
		try {
			len = b.processBytes(str.getBytes("UTF-8"), 0, str.getBytes("UTF-8").length, out, 0);
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			len += b.doFinal(out, len);
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//
		byte[] iv = paramsWithIv.getIV();
		byte[] all = new byte[out.length + iv.length];
		System.arraycopy(iv, 0, all, 0, iv.length);
		System.arraycopy(out, 0, all, iv.length, out.length);
		int lenAll = all.length;
		String encoded = Base64.encodeBase64String(all);
		encoded = encoded.replace('+', '-');
		encoded = encoded.replace('/', '_');
		while (encoded.endsWith("=")) {
			encoded = encoded.substring(0, encoded.length() - 1);
		}

		return encoded;
	}

	private String getPassword() {
		return "uJ_.|sP0'!J.~5(6@\"1>;'-D~)4E]uop";// 32
		// uJ_.|sP0'!J.~5(6@"1>;'-D~)4E]u
	}


}
