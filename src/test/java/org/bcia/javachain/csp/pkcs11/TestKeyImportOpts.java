package org.bcia.javachain.csp.pkcs11;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.intfs.opts.IKeyImportOpts;
import org.bcia.javachain.csp.pkcs11.IPKCS11FactoryOpts;
import org.bcia.javachain.csp.pkcs11.PKCS11CspFactory;
import org.bcia.javachain.csp.pkcs11.PKCS11FactoryOpts;
import org.bcia.javachain.csp.pkcs11.aes.AesOpts;
import org.bcia.javachain.csp.pkcs11.ecdsa.EcdsaOpts;
import org.bcia.javachain.csp.pkcs11.entity.PKCS11Config;
import org.bcia.javachain.csp.pkcs11.entity.PKCS11KeyData;
import org.bcia.javachain.csp.pkcs11.entity.PKCS11Lib;
import org.bcia.javachain.csp.pkcs11.rsa.RsaOpts;
import org.junit.Before;
import org.junit.Test;

import sun.security.provider.SecureRandom;

public class TestKeyImportOpts {
	int secLevel= 5;
	String hashFamily="MD";		
	String keyStorePath=null;
	String Library=null;		
	String Label=null;
	String SN=null;
	String Pin=null;
	boolean bSensitive=false; 
	boolean bSoftVerify=false;
	PKCS11Lib findlib = null;
	PKCS11Config findconf = null;
	IPKCS11FactoryOpts iPKCS11FactoryOpts = null;
	PKCS11CspFactory cspfactory = null;
	ICsp csp = null;
	@Before
	public void before() {
		try {
			findlib = new PKCS11Lib(Library, Label, SN, Pin);
			findconf = new PKCS11Config(secLevel, hashFamily, bSoftVerify, bSensitive);
			iPKCS11FactoryOpts = new PKCS11FactoryOpts(findlib, findconf);
			cspfactory = new PKCS11CspFactory();
			csp = cspfactory.getCsp(iPKCS11FactoryOpts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test1() {
		
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			KeyPair key = keyGen.generateKeyPair();
			
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(key.getPrivate().getEncoded());
			keyraw.setRawPub(key.getPublic().getEncoded());
			
			IKeyImportOpts opts = new RsaOpts.RSAPrivateKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			csp.getKey(mykey.ski());
		} catch (JavaChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test2() {
		
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			KeyPair key = keyGen.generateKeyPair();
			
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(null);
			keyraw.setRawPub(key.getPublic().getEncoded());
			
			IKeyImportOpts opts = new RsaOpts.RSAPublicKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			csp.getKey(mykey.ski());
		} catch (JavaChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test3() {
		
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair key = keyGen.generateKeyPair();
			
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(key.getPrivate().getEncoded());
			keyraw.setRawPub(key.getPublic().getEncoded());
			
			IKeyImportOpts opts = new RsaOpts.RSAPrivateKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			csp.getKey(mykey.ski());
		} catch (JavaChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test4() {
		
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair key = keyGen.generateKeyPair();
			
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(null);
			keyraw.setRawPub(key.getPublic().getEncoded());
			
			IKeyImportOpts opts = new RsaOpts.RSAPublicKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			csp.getKey(mykey.ski());
		} catch (JavaChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test5() {
		
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
		    ECGenParameterSpec ecsp;
		    ecsp = new ECGenParameterSpec("secp256r1");
		    kpg.initialize(ecsp);
		    KeyPair kpU = kpg.genKeyPair();
			
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(kpU.getPrivate().getEncoded());
			keyraw.setRawPub(kpU.getPublic().getEncoded());
			
			IKeyImportOpts opts = new EcdsaOpts.ECDSAPrivateKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			
		} catch (JavaChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException|NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test6() {
		
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
		    ECGenParameterSpec ecsp;
		    ecsp = new ECGenParameterSpec("secp256r1");
		    kpg.initialize(ecsp);
		    KeyPair kpU = kpg.genKeyPair();
			
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(null);
			keyraw.setRawPub(kpU.getPublic().getEncoded());
			
			IKeyImportOpts opts = new EcdsaOpts.ECDSAPublicKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			
		} catch (JavaChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException|NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test7() {
		
		try {			
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");			
			keyGen.init(128); // for example
			SecretKey secretKey = keyGen.generateKey();
			PKCS11KeyData keyraw = new PKCS11KeyData();
			keyraw.setRawPri(secretKey.getEncoded());
			keyraw.setRawPub(null);
			IKeyImportOpts opts = new AesOpts.AESKeyImportOpts(false);
			IKey mykey = csp.keyImport(keyraw, opts);
			return;
			
		} catch (JavaChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}