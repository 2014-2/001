package ICT.utils;

/**
 * 数据库导入导出加密
 * 
 * @author zhouwei
 *
 */
public interface IDbEncrypt {

	/**
	 * 加密文件
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	public boolean encrypt(String srcFile, String destFile) ;
	
	/**
	 * 解密文件
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	public boolean decrypt(String srcFile, String destFile) ;
}
