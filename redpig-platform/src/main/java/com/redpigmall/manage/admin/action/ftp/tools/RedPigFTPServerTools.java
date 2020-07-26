package com.redpigmall.manage.admin.action.ftp.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.FTPServer;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WaterMark;
import com.redpigmall.service.RedPigFTPServerService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: RedPigFTPServerTools.java
 * </p>
 * 
 * <p>
 * Description: 平台资源上传至FTP服务器工具类，上传时使用ip，页面显示时使用FTP地址
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
 * @date 2014年7月1日
 * 
 * @version redpigmall_b2b2c_colony v1.0集群版
 */
@Component
public class RedPigFTPServerTools {
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigFTPServerService ftpserver;
	@Autowired
	private RedPigUserService userService;
	
	/**
	 * 将Web服务器上资源上传至Ftp服务器，返回Ftp存储路径
	 * 
	 * @param imgName
	 *            :上传到Ftp服务器中的资源文件名称
	 * @param SaveUrl
	 *            ：Ftp服务器保存资源路径，可以随意设置保存路径，没有文件夹则新建
	 */
	public String systemUpload(String imgName, String SaveUrl) {
		SaveUrl = this.configService.getSysConfig().getUploadFilePath()
				+ SaveUrl;
		FTPServer ftp = this.getSystemFTP();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String WebPath = ClusterSyncTools.getClusterRoot()
				+ uploadFilePath + File.separator + "cache" + File.separator
				+ imgName;
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 设置上传目录及文件
			File srcFile = new File(WebPath);
			fis = new FileInputStream(srcFile);
			// 设置ftp文件存储目录
			if (SaveUrl.indexOf("/") < 0) {
				SaveUrl = "/" + SaveUrl;
			}
			String dirs[] = SaveUrl.split("/");
			String d = "/";
			for (String dir : dirs) {
				if (!dir.equals("")) {
					d = d + dir + "/";
					ftpClient.makeDirectory(d);
				}
			}
			ftpClient.changeWorkingDirectory(SaveUrl);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.storeFile(imgName, fis);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		File file = new File(WebPath);
		file.delete();
		return ftp.getFtp_addr() + "/" + SaveUrl;
	}

	/**
	 * dir为本地文件路径，该方法支持没有上传到cache文件夹中的文件上传
	 * 
	 * @param dir
	 * @param imgName
	 * @param SaveUrl
	 * @return
	 */
	public String systemUpload(String localDir, String imgName, String SaveUrl) {
		SaveUrl = this.configService.getSysConfig().getUploadFilePath()
				+ SaveUrl;
		FTPServer ftp = this.getSystemFTP();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String WebPath = ClusterSyncTools.getClusterRoot()
				+ uploadFilePath + File.separator + localDir + File.separator
				+ imgName;
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 设置上传目录及文件
			File srcFile = new File(WebPath);
			fis = new FileInputStream(srcFile);
			// 设置ftp文件存储目录
			String dirs[] = SaveUrl.split("/");
			String d = "/";
			for (String dir : dirs) {
				if (!dir.equals("")) {
					d = d + dir + "/";
					ftpClient.makeDirectory(d);
				}
			}
			ftpClient.changeWorkingDirectory(SaveUrl);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.storeFile(imgName, fis);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		File file = new File(WebPath);
		file.delete();
		return ftp.getFtp_addr() + "/" + SaveUrl;
	}

	public void systemDeleteFtpImg(Accessory acc) throws IOException {
		FTPServer ftp = this.getSystemFTP();
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			String regex = ftp.getFtp_addr();
			String paths[] = acc.getPath().split(regex);
			String delePath = "";
			for (String path : paths) {
				if (!path.equals("")) {
					delePath = path;
				}
			}
			ftpClient.deleteFile(delePath + "/" + acc.getName());
			ftpClient.deleteFile(delePath + "/" + acc.getName() + "_small."
					+ acc.getExt());
			ftpClient.deleteFile(delePath + "/" + acc.getName() + "_middle."
					+ acc.getExt());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 从系统ftp服务器上下载水印图片
	 * 
	 * @param acc
	 * @param watermark
	 * @param user_id
	 */
	public void systemDownloadWaterImg(WaterMark watermark) {
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;
		FileOutputStream foswm = null;
		FTPServer ftp = this.getSystemFTP();
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String path = ClusterSyncTools.getClusterRoot() + uploadFilePath
				+ File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 下载水印图片
			String remoteFileNamewm = null;
			remoteFileNamewm = uploadFilePath + File.separator + "wm"
					+ File.separator + watermark.getWm_image().getName();
			foswm = new FileOutputStream(path + File.separator
					+ watermark.getWm_image().getName());
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 从系统ftp服务器上下载原图片
	 * 
	 * @param acc
	 * @param watermark
	 * @param user_id
	 */
	public void systemDownloadImg(Accessory acc) {
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;
		FileOutputStream foswm = null;
		FTPServer ftp = this.getSystemFTP();
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String path = ClusterSyncTools.getClusterRoot() + uploadFilePath
				+ File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 下载水印图片
			String acc_path[] = acc.getPath().split(ftp.getFtp_addr());
			System.out.println("addr:" + ftp.getFtp_addr());
			String remoteFileNamewm = acc_path[1] + File.separator
					+ acc.getName();
			foswm = new FileOutputStream(path + File.separator + acc.getName());
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 将用户Web服务器上资源上传至Ftp服务器，返回Ftp存储路径
	 * 
	 * @param imgName
	 *            :上传到Ftp服务器中的资源文件名称
	 * @param SaveUrl
	 *            ：Ftp服务器保存资源路径，可以随意设置保存路径，没有文件夹则新建
	 */
	public String userUpload(String imgName, String SaveUrl, String user_id) {
		SaveUrl = this.configService.getSysConfig().getUploadFilePath() + "/"
				+ user_id + SaveUrl;
		FTPServer ftp = this.getUserFTP(user_id);
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String WebPath = ClusterSyncTools.getClusterRoot()
				+ uploadFilePath + File.separator + "cache" + File.separator
				+ imgName;
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		String url = ftp.getFtp_ip();
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 设置上传目录及文件
			File srcFile = new File(WebPath);
			fis = new FileInputStream(srcFile);
			// 设置ftp文件存储目录
			String dirs[] = SaveUrl.split("/");
			String d = "/";
			for (String dir : dirs) {
				if (!dir.equals("")) {
					d = d + dir + "/";
					ftpClient.makeDirectory(d);
				}
			}
			ftpClient.changeWorkingDirectory(SaveUrl);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.storeFile(imgName, fis);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		File file = new File(WebPath);
		file.delete();
		return ftp.getFtp_addr() + "/" + SaveUrl;
	}

	/**
	 * 商家删除位于ftp上的图片
	 * 
	 * @param acc
	 * @param user_id
	 * @throws IOException
	 */
	public void userDeleteFtpImg(Accessory acc, String user_id)
			throws IOException {
		FTPServer ftp = this.getUserFTP(user_id);
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			String regex = ftp.getFtp_addr();
			String paths[] = acc.getPath().split(regex);
			String delePath = "";
			for (String path : paths) {
				if (!path.equals("")) {
					delePath = path;
				}
			}
			ftpClient.deleteFile(delePath + "/" + acc.getName());
			ftpClient.deleteFile(delePath + "/" + acc.getName() + "_small."
					+ acc.getExt());
			ftpClient.deleteFile(delePath + "/" + acc.getName() + "_middle."
					+ acc.getExt());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 从用户ftp服务器上下载水印图片
	 * 
	 * @param acc
	 * @param watermark
	 * @param user_id
	 */
	public void userDownloadWaterImg(WaterMark watermark, String user_id) {
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;
		FileOutputStream foswm = null;
		FTPServer ftp = this.getUserFTP(user_id);
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String path = ClusterSyncTools.getClusterRoot() + uploadFilePath
				+ File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 下载水印图片
			String remoteFileNamewm = null;
			remoteFileNamewm = uploadFilePath + File.separator + user_id
					+ File.separator + "wm" + File.separator
					+ watermark.getWm_image().getName();
			foswm = new FileOutputStream(path + File.separator
					+ watermark.getWm_image().getName());
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 从系统ftp服务器上下载原图片
	 * 
	 * @param acc
	 * @param watermark
	 * @param user_id
	 */
	public void userDownloadImg(Accessory acc, String user_id) {
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;
		FileOutputStream foswm = null;
		FTPServer ftp = this.getUserFTP(user_id);
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String path = ClusterSyncTools.getClusterRoot() + uploadFilePath
				+ File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 下载水印图片
			String acc_path[] = acc.getPath().split(ftp.getFtp_addr());
			String remoteFileNamewm = acc_path[1] + File.separator
					+ acc.getName();
			foswm = new FileOutputStream(path + File.separator + acc.getName());
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 删除web服务器中的图片
	 * 
	 * @return
	 */
	public void DeleteWebImg(Accessory acc) {
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String WebPath = ClusterSyncTools.getClusterRoot()
				+ uploadFilePath + File.separator + "cache" + File.separator
				+ acc.getName();
		File file = new File(WebPath);
		file.delete();
	}

	private FTPServer getSystemFTP() {
		FTPServer ftp = null;
		Map<String,Object> ftp_map = Maps.newHashMap();
		ftp_map.put("ftp_type", 1);
		ftp_map.put("ftp_system", 1);// 正使用的系统ftp
		
		List<FTPServer> ftps = this.ftpserver.queryPageList(ftp_map);
		
		if (ftps.size() > 0) {
			ftp = ftps.get(0);
		}
		return ftp;
	}

	private FTPServer getUserFTP(String user_id) {
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(user_id));
		if (user_id != null && !user_id.equals("")) {
			user = this.userService.selectByPrimaryKey(CommUtil.null2Long(user_id));
		} else {
			user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
		}
		FTPServer ftp = user.getFtp();
		return ftp;
	}

}
