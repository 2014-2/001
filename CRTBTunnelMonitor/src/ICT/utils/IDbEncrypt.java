package ICT.utils;

/**
 * 数据库导入导出加密
 * 
 * @author zhouwei
 *
 */
public interface IDbEncrypt {

	// 加密文件
	public boolean encrypt(String srcFile, String destFile) ;
	
	// 解密文件
	public boolean decrypt(String srcFile, String destFile) ;
}
