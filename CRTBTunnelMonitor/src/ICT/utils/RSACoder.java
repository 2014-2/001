package ICT.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSACoder {
	public static String algorithm = "RSA";

	public static String encnryptRSA(String deskey, String publicKey) {
		String en_txt = null;
		try {
			byte[] bkey = ByteToStringUtil.base64StrToByte(publicKey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bkey);
			KeyFactory keyFacory = KeyFactory.getInstance(algorithm);
			PublicKey pubkey = keyFacory.generatePublic(x509KeySpec);
			System.out.println("公钥信息：");
			System.out.println(pubkey.toString());

			byte[] cipherMessage = encrypt(deskey.getBytes(), pubkey,
					"RSA/ECB/PKCS1Padding");

			en_txt = ByteToStringUtil.byteToBase64Str(cipherMessage);
			System.out.println("rsa加密的key：" + en_txt);
			System.out
					.println("****************************加密DES密钥结束********************************");
		} catch (Exception e) {
			return "生成密钥失败！";
		}
		return en_txt;
	}

	public static String decnryptRSA(String en_txt, String privateKey) {
		String dedesKey = null;
		try {
			byte[] pkey = ByteToStringUtil.base64StrToByte(privateKey);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkey);
			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
			PrivateKey priKey = keyFactory.generatePrivate(keySpec);
			byte[] cmsg = ByteToStringUtil.base64StrToByte(en_txt);
			byte[] originalMessage = decrypt(cmsg, priKey,
					"RSA/ECB/PKCS1Padding");
			dedesKey = new String(originalMessage);
			System.out.println("解密后为：" + new String(originalMessage));
			System.out
					.println("****************************解密密DES密钥结束********************************");
		} catch (Exception e) {
			return "生成密钥失败！";
		}
		return dedesKey;
	}

	public static String[] createRsapubKey() {
		String publicKey = null;
		String privateKey = null;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
			keyGen.initialize(512);
			KeyPair keyPair = keyGen.generateKeyPair();
			publicKey = ByteToStringUtil.byteToBase64Str(keyPair.getPublic()
					.getEncoded());
			privateKey = ByteToStringUtil.byteToBase64Str(keyPair.getPrivate()
					.getEncoded());
			System.out
					.println("************************************************************");
			System.out.println("密钥对的公钥：" + publicKey);
			System.out.println("密钥对的私钥：" + privateKey);
		} catch (Exception e) {
			return new String[0];
		}
		return new String[] { publicKey, privateKey };
	}

	public static String encnryptDes(String content, String password) {
		String en_txt = null;
		try {
			if (content.length() % 8 != 0) {
				int k = (content.length() / 8 + 1) * 8 - content.length();
				for (int i = 0; i < k; i++) {
					content = content + "@";
				}
			}
			en_txt = ByteToStringUtil.byteToBase64Str(des_encrypt(content,
					password));
			System.out.println("des加密：" + en_txt);
		} catch (Exception e) {
			return "加密失败！";
		}
		return en_txt;
	}

	public static String decnryptDes(String cipher, String password) {
		String de_txt = null;
		try {
			de_txt = new String(des_decrypt(
					ByteToStringUtil.base64StrToByte(cipher), password));
            if (de_txt.contains("@")) {
                de_txt = de_txt.substring(0, de_txt.indexOf("@"));
            }
			System.out.println("des解密：" + de_txt);
		} catch (Exception e) {
			return "解密失败！";
		}
		return de_txt;
	}

	public static byte[] aes_encrypt(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(1, key);
			return cipher.doFinal(byteContent);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] des_decrypt(byte[] content, String password) {
		try {
			Cipher enc = Cipher.getInstance("DES/CBC/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(),
					"DES");
			IvParameterSpec ivSpec = new IvParameterSpec(password.getBytes());
			try {
				enc.init(2, keySpec, ivSpec);
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}

			return enc.doFinal(content);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] des_encrypt(String content, String password) {
		try {
			Cipher enc = Cipher.getInstance("DES/CBC/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(),
					"DES");
			IvParameterSpec ivSpec = new IvParameterSpec(password.getBytes());
			try {
				enc.init(1, keySpec, ivSpec);
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			byte[] byteContent = content.getBytes();
			return enc.doFinal(byteContent);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] aes_decrypt(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(2, key);
			return cipher.doFinal(content);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] encrypt(byte[] inpBytes, PublicKey key, String xform)
			throws Exception {
		Cipher cipher = Cipher.getInstance(xform);
		cipher.init(1, key);
		return cipher.doFinal(inpBytes);
	}

	private static byte[] decrypt(byte[] inpBytes, PrivateKey key, String xform)
			throws Exception {
		Cipher cipher = Cipher.getInstance(xform);
		cipher.init(2, key);
		return cipher.doFinal(inpBytes);
	}
}
