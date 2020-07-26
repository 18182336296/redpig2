package com.redpigmall.api.tools.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;



import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * <p>
 * Title: SftpOperUtil.java
 * </p>
 * 
 * <p>
 * Description:SFTP上传下载操作
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
@SuppressWarnings("unchecked")
public class SftpOperUtil {
	
	
	/** ftp客户端对象 */ 
	private ChannelSftp sftp = null;
	/** 超时时间 */
	private static final int TIMEOUT = 3000;
	/** 文件传输模式：完全覆盖模式 */
	public static final int OVERWRITE = 0;
	/** 文件传输模式：恢复模式(断点续传) */
	public static final int RESUME = 1;
	/** 文件传输模式： 追加模式  */
	public static final int APPEND = 2;
	
	/**
	 * sftp连接函数
	 * @param ip  ftp server 端IP
	 * @param port  端口
	 * @param user  用户名
	 * @param paswd 密码
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean connect(String host, int port, String username, String password) throws Exception {
		
		boolean isConn = true;
		
		try {
			
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			
			Session session = jsch.getSession(username, host, port);
			session.setPassword(password);
			
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			session.setConfig(sshConfig);
			
			session.setTimeout(TIMEOUT);
			session.connect();
			
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			
		} catch (Exception e) {
			e.printStackTrace();
			isConn  = false;
			throw new Exception("connect sftp is error");
		}
		
		return isConn;
	}

	/**
	 * 将本地文件名为src的文件上传到目标服务器
	 * 
	 * @param src  本地文件
	 * @param dst  目标文件或目录，若为目录时则目标文件名将与src文件名相同
	 * @param mode 传输模式
	 * @throws Exception
	 */
	public void upload(String src, String dst, int mode) throws Exception {
		
		sftp.put(src, dst, mode);
	}
	
	/**
	 * 将本地文件名为src的文件上传到目标服务器，采用默认的传输模式（OVERWRITE）
	 * 
	 * @param src 本地文件
	 * @param dst 目标文件或目录，若为目录时则目标文件名将与src文件名相同
	 * @throws Exception
	 */
	public void upload(String src, String dst) throws Exception {
		
		upload(src, dst,SftpOperUtil.OVERWRITE);
	}
	
	/**
	 * 将本地的input stream对象src上传到目标服务器
	 *
	 * @param src   本地的input stream对象
	 * @param dst 目标文件名，且不能为目录
	 * @param mode 传输模式 
	 * @throws Exception
	 */
	public void upload(InputStream src,String dst, int mode) throws Exception{
		
		sftp.put(src,dst,mode);
	}
	
	/**
	 * 将本地的input stream对象src上传到目标服务器，采用默认的传输模式（OVERWRITE）
	 *
	 * @param src   本地的input stream对象
	 * @param dst 目标文件名，且不能为目录
	 * @throws Exception
	 */
	public void upload(InputStream src,String dst) throws Exception{
		
		upload(src,dst,SftpOperUtil.OVERWRITE);
	}
	
	/**
	 * 根据返回的输出流中写入数据，最终将数据传输到目标服务器，目标文件名为dst
	 * @param dst 目标文件名，且不能为目录
	 * @param mode 传输模式 
	 * @return 输出流
	 * @throws Exception
	 */
	public OutputStream upload(String dst,int mode) throws Exception {
		
		return sftp.put(dst, mode);
	}
	
	/**
	 * 根据返回的输出流中写入数据，最终将数据传输到目标服务器，目标文件名为dst;采用默认的传输模式（OVERWRITE）
	 * @param dst 目标文件名，且不能为目录
	 * @return 输出流
	 * @throws Exception
	 */
	public OutputStream upload(String dst) throws Exception {
		
		return upload(dst, SftpOperUtil.OVERWRITE);
	}

	/**
	 * 将目标服务器上文件名为src的文件下载到本地,本地文件名为dst
	 * <P>若dst为目录，则下载到本地的文件名将与src文件名相
	 * <P>采用默认的传输模式：
	 *
	 * @param src 服务器文件，只能为绝对路径，不能为目录
	 * @param dst 本地文件名或目录
	 * @throws Exception
	 */
	public void download(String src, String dst) throws Exception {
		
		sftp.get(src, dst);
	}
	
	
	/**
	 * 将目标服务器上文件名为src的文件下载到本地，下载的数据写入到输出流对象dst
	 * <P>采用默认的传输模式：OVERWRITE
	 *
	 * @param src 服务器文件，只能为绝对路径，不能为目录
	 * @param dst 输出流对象（如：文件输出流）
	 * @throws Exception
	 */
	public void download(String src, OutputStream dst) throws Exception {
		
		sftp.get(src, dst);
	}
	
	/**
	 * 文件下载，返回标准输入流，该输入流含有目标服务器上文件名为src的文件数据
	 *
	 * @param src 服务器文件，只能为绝对路径，不能为目录
	 * @return 标准输入流
	 * @throws Exception
	 */
	public InputStream download(String src) throws Exception {
		
		return sftp.get(src);
	}
	

	/**
	 * 切换目录
	 * 
	 * @param src 指定目录
	 * @throws SftpException
	 */
	public void cd(String src) throws SftpException {
		
		sftp.cd(src);
	}

	/**
	 * 获取指定目录下文件名称
	 * 
	 * @param src  目录
	 * @return 文件名称列表
	 * @throws SftpException
	 */
	public List<String> listFiles(String src) throws SftpException {

		List<String> filesList = new ArrayList<String>();
		Vector<LsEntry> vector = sftp.ls(src);

		for (LsEntry entry : vector) {
			
			String strFileName = entry.getFilename();
			if (strFileName.equals(".") || strFileName.equals("..")) {
				continue;
			}
			
			filesList.add(strFileName);
		}
		
		return filesList;
	}
	
	/**
	 * 关闭ftp连接
	 * 
	 * @return boolean
	 */
	public void colse() {
		
		if (sftp != null) {
			
			try {
				sftp.getSession().disconnect();
				sftp.disconnect();
				sftp = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sftp = null;
			}
		}
	}
	@SuppressWarnings("unused")
	public static void testmai(String[] args) {
		String path = "/";
		
		String ip = "";
		//端口号
		int port = 22;
		//用户名
		String user = "root";
		//密码
		String paswd = "";
		SftpOperUtil sftpUtil = new SftpOperUtil();
		boolean connect;
		OutputStream outputStream = null;
		BufferedReader br = null;
		try {
			File file = new File("D:\\license.zip");
			InputStream in = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(in, "GBK"));
			String line = null;
			connect = sftpUtil.connect(ip, port, user, paswd);
			if (connect) {
				outputStream = sftpUtil.upload(path, 2);
				while((line = br.readLine())!=null){
					byte[] bytes = (line+"\r\n").getBytes();
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				
				outputStream.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}
	
}
