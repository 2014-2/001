package ICT.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;

public class ByteToStringUtil {
	public static String byteToBase64Str(byte[] paramArrayOfByte)
			throws EncoderException {
		Base64 localBase64 = new Base64();
		String str = new String(localBase64.encode(paramArrayOfByte));
		return str;
	}

	public static byte[] base64StrToByte(String paramString)
			throws DecoderException {
		Base64 localBase64 = new Base64();
		byte[] arrayOfByte = localBase64.decode(paramString.getBytes());
		return arrayOfByte;
	}

	public static String byteToHexStr(byte[] paramArrayOfByte) {
		StringBuffer localStringBuffer = new StringBuffer(
				paramArrayOfByte.length);
		for (int i = 0; i < paramArrayOfByte.length; i++) {
			String str = Integer.toHexString(0xFF & paramArrayOfByte[i]);
			if (str.length() < 2)
				localStringBuffer.append(0);
			localStringBuffer.append(str.toUpperCase());
		}
		return localStringBuffer.toString();
	}

	public static String HexStrtoByte(String hexstr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexstr.toCharArray();
		byte[] bytes = new byte[hexstr.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			int n = str.indexOf(hexs[(2 * i)]) * 16;
			n += str.indexOf(hexs[(2 * i + 1)]);
			bytes[i] = ((byte) (n & 0xFF));
		}
		return new String(bytes);
	}
}