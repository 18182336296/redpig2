package com.redpigmall.api.tools.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
/**
 * <p>
 * Title: SFtpUtil.java
 * </p>
 * 
 * <p>
 * Description:SFTP发送报文文件 SFTP下载报文文件
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2017-3-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class SFtpUtil {
	// ftp客户端对象
	private static ChannelSftp sftp = null;
	
	private static ChannelExec exec;

	/**
	 * sftp连接函数
	 * 
	 * @param server
	 *            ftp server 端IP
	 * @param port
	 *            端口
	 * @param user
	 *            用户名
	 * @param password
	 *            密码
	 * @return boolean
	 */
	public static boolean connect(String host, int port, String username,
			String password) {
		if(sftp!=null && sftp.isConnected()){
			return true;
		}
		
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("userauth.gssapi-with-mic", "no");
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Sftp文件上传
	 * 
	 * @param sFilePath
	 *            源文件路径
	 * @param sFileName
	 *            源文件名称
	 * @param dFilePath
	 *            目标文件路径
	 * @param dFileName
	 *            目标文件名称
	 * @param type
	 *            传输类型
	 * @return boolean
	 */
	public static boolean uploadFile(String sFilePath, String sFileName,
			String dFilePath, String dFileName, String type) {
		FileInputStream is = null;
		try {
			sftp.cd(dFilePath);
			File file = new File(sFilePath + sFileName);
			is = new FileInputStream(file);
			sftp.put(is, file.getName());
			if (!sFileName.equals(dFileName)) {
				sftp.rename(sFileName, dFileName);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 上传文件
	 * 
	 * @param directory
	 *            上传的目录
	 * @param uploadFile
	 *            要上传的文件
	 * @param sftp
	 */
	public static void upload(String directory, String uploadFile) {
		try {
			
			try {
				//这里需要注意:windows中的斜杠\在linux中识别不了,干,必须转换一下,通常会出现问题是因为测试、开发用的win
				//在linux中新建文件夹就不管用了
				
				directory = directory.replaceAll("\\\\", "/");
				
				sftp.cd(directory);
				
			} catch (Exception e) {
				    String[] folders = directory.split( "/" );
				    for ( String folder : folders ) {  
				        if ( folder.length() > 0 ) {  
				            try {  
				                sftp.cd( folder );
				            }  
				            catch ( SftpException e1 ) {  
				                sftp.mkdir( folder );
				                sftp.cd( folder );
				            }  
				        }  
				    }
			}
			File file = new File(uploadFile);
			sftp.put(new FileInputStream(file), file.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SFtpUtil.close();
		SFtpUtil.sftp = null;
	}

	
	
	/**
	 * ftp下载文件
	 * 
	 * @param sFilePath
	 *            源文件路径
	 * @param sFileName
	 *            源文件名称
	 * @param dFilePath
	 *            目标文件路径
	 * @param dFileName
	 *            目标文件名称
	 * @param type
	 *            传输类型
	 * @return boolean
	 */
	public static boolean downloadFile(String sFilePath, String sFileName,
			String dFilePath, String dFileName, String type) {
		try {
			sftp.cd(sFilePath);
			File file = new File(dFilePath + dFileName);
			OutputStream os = new FileOutputStream(file);
			sftp.get(sFileName, os);
			os.close();
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
		return true;
	}


	/**
	 * 执行一次shell命令，之后连接就关闭
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public static int runCmd(String cmd) {
		try {
			exec = (ChannelExec) sftp.getSession().openChannel("exec");
			exec.setCommand(cmd);
			exec.setInputStream(null);
			exec.setErrStream(System.err);
			InputStream in = exec.getInputStream();
			exec.connect();
			int res = -1;
			StringBuffer buf = new StringBuffer(1024);
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					buf.append(new String(tmp, 0, i));
				}
				if (exec.isClosed()) {
					res = exec.getExitStatus();
					break;
				}
			}
			exec.disconnect();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
    public static boolean makeDirs(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File folder = new File(filePath);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }
	
	/**
	 * 关闭ftp连接
	 * 
	 * @return boolean
	 */
	public static boolean close() {
		if (sftp != null) {
			try {
				sftp.disconnect();
				sftp = null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param sFilePath
	 *            要列出的目录
	 * @param sftp
	 * @return
	 * @throws SftpException
	 */
	@SuppressWarnings("rawtypes")
	public static Vector listFiles(String sFilePath) throws SftpException {
		return sftp.ls(sFilePath);
	}
	
	public static void testmai(String[] args) {
		
//        System.out.println(flag);
//        runCmd("mkdir -p /mnt/images/test/file/image");
        upload("/upload/aaaaa", "D:\\b2b2c\\favicon.ico");
//        downloadFile("/mnt/images/test/file/image", "license.zip", "c:\\", "license.zip", "");
        
//        close();
		
//		makeDirs("g:\\b2b2c\\abcde\\sfd\\sf\\ds\\f\\d\\s\\fds");
		
		
	}
}
