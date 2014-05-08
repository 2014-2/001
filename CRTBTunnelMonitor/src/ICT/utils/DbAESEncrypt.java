package ICT.utils;

import android.annotation.SuppressLint;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.zw.android.framework.util.StringUtils;

/**
 * AES 加密 导入/导出
 * @author zhouwei
 *
 */
public final class DbAESEncrypt implements IDbEncrypt {

	// 16 位密码
	private static String getKey() {
		return "1234567812345678";
	}

	@SuppressLint("TrulyRandom")
	public boolean encrypt(String srcFile, String destFile) {

		if (StringUtils.isEmpty(srcFile) || StringUtils.isEmpty(destFile)) {
			return false;
		}

		try {
			Key privateKey = new SecretKeySpec(getKey().getBytes(), "AES");

			SecureRandom sr = new SecureRandom();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			IvParameterSpec spec = new IvParameterSpec(privateKey.getEncoded());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey, spec, sr);

			FileInputStream fis = new FileInputStream(srcFile);
			FileOutputStream fos = new FileOutputStream(destFile);

			byte[] b = new byte[2048];

			while (fis.read(b) != -1) {
				fos.write(cipher.doFinal(b));
				fos.flush();
			}

			fos.close();
			fis.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean decrypt(String srcFile, String destFile) {

		if (StringUtils.isEmpty(srcFile) || StringUtils.isEmpty(destFile)) {
			return false;
		}

		try {

			Key privateKey = new SecretKeySpec(getKey().getBytes(), "AES");

			SecureRandom sr = new SecureRandom();
			Cipher ciphers 	= Cipher.getInstance("AES/CBC/PKCS5Padding");

			IvParameterSpec spec = new IvParameterSpec(privateKey.getEncoded());
			ciphers.init(Cipher.DECRYPT_MODE, privateKey, spec, sr);

			FileInputStream fis = new FileInputStream(srcFile);
			FileOutputStream fos = new FileOutputStream(destFile);

			byte[] b = new byte[2064];

			while (fis.read(b) != -1) {
				fos.write(ciphers.doFinal(b));
				fos.flush();
			}

			fos.close();
			fis.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
