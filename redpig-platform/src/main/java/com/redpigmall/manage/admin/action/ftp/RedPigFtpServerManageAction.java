package com.redpigmall.manage.admin.action.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.api.tools.ftp.SFtpUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.FTPServer;
import com.redpigmall.domain.SysConfig;

/**
 * 
 * <p>
 * Title: RedPigFtpServerManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统FTP服务器管理类，添加、保存、修改、删除FTP服务器信息
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
 * @date 2016年7月1日
 * 
 * @version redpigmall_b2b2c_colony v1.0集群版
 */
@SuppressWarnings({"static-access","unchecked","resource"})
@Controller
public class RedPigFtpServerManageAction extends BaseAction{
	
	/**
	 * FTP服务器测试
	 * @param request
	 * @param response
	 * @param id
	 * @throws IOException
	 */
	@SecurityMapping(title = "FTP服务器测试", value = "/ftpserver_test*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/ftpserver_test")
	public void ftpserver_test(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		int upload_code = -100;// 上传code，100上传成功，-100上传失败
		int download_code = -100;// 下载code，100下载成功，-100下载失败
		int result_code = -100;// 100FTP测试成功，-100FTP测试失败
		String fileName = "";
		String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
				+ uploadFilePath + File.separator + "cache";
		Map<String,Object> map = Maps.newHashMap();
		try {
			map = CommUtil.saveFileToServer(request, "acc_test",saveFilePathName, "", null);
			if (map.get("fileName") != "") {
				fileName = CommUtil.null2String(map.get("fileName"));
				upload_code = this.sftpImageUpload(id, fileName, "/ftpTest");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (upload_code == 100) {// 上传成功后进行下载
			if (fileName != "") {
				upload_code = this.sftpImageDownload(id, "/ftpTest", fileName);
				
				if (upload_code == 100) {// 比较上传与下载的文件是否一致
					String file_url = saveFilePathName + File.separator + fileName;
					if (CommUtil.fileExist(file_url)) {
						FileInputStream fis = new FileInputStream(file_url);
						if (fis.available() > 0) {
							download_code = 100;// 下载成功
						}
					}
				}
			}
			if (download_code == 100) {// 上传成功的同时，下载也同样成功，证明FTP调试成功
				result_code = 100;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(result_code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 
	 * @param ftp_id ftp的主键ID
	 * @param imgName 文件名字
	 * @param imageFilePath 服务器上面文件存储路径
	 * @return
	 */
	public int sftpImageUpload(String ftp_id, String imgName, String imageFilePath) {
		int upload_code = 100;// 100上传成功，-100上传失败！
		
		FTPServer ftp = this.redPigFTPServerService.selectByPrimaryKey(CommUtil.null2Long(ftp_id));
		
		String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
		//本地文件
		String fileWebPath = uploadFilePath + File.separator + "cache" + File.separator + imgName;
		
		SFtpUtil client = new SFtpUtil();
		try {
			String ip = ftp.getFtp_ip();
			int port = 22;
			
			String username = ftp.getFtp_name();
			String pwd = ftp.getFtp_password();
			
			boolean flag = client.connect(ip, port, username, pwd);
			if (flag) {
		        client.upload(imageFilePath, fileWebPath);
			} else {
				upload_code = -100;
			}
		} catch (Exception e) {
			e.printStackTrace();
			upload_code = -100;
		} finally {
				client.close();
		}
		File file = new File(fileWebPath);
		file.delete();
		return upload_code;
	}

	
	/**
	 * @param ftp_id
	 * @param imgName
	 * @param SaveUrl
	 * @return
	 */
	public int ImageUpload(String ftp_id, String imgName, String SaveUrl) {
		int upload_code = 100;// 100上传成功，-100上传失败！
		SaveUrl = this.redPigSysConfigService.getSysConfig().getUploadFilePath()
				+ SaveUrl;
		FTPServer ftp = this.redPigFTPServerService.selectByPrimaryKey(CommUtil.null2Long(ftp_id));
		
		String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
		
		String WebPath = uploadFilePath + File.separator + "cache" + File.separator + imgName;
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			if (ftp != null) {
				ftpClient.connect(ftp.getFtp_ip());
				boolean flag = ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
				System.out.println("登陆服务器"+ftp_id+":"+(flag?"成功":"失败"));
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
			} else {
				upload_code = -100;
			}
		} catch (IOException e) {
			e.printStackTrace();
			upload_code = -100;
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				upload_code = -100;
			}
		}
		File file = new File(WebPath);
		file.delete();
		return upload_code;
	}


	/**
	 * 
	 * @param ftp_id
	 * @param fileFolder
	 * @param acc_name
	 * @return
	 */
	public int sftpImageDownload(String ftp_id, String fileFolder, String acc_name) {
		fileFolder = this.redPigSysConfigService.getSysConfig().getUploadFilePath() + fileFolder;
		
		int down_code = 100;// 100下载成功，-100下载失败
		FTPServer ftp = this.redPigFTPServerService.selectByPrimaryKey(CommUtil.null2Long(ftp_id));
		
		//文件本地存储文件夹
		String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
		
		//文件本地存储绝对路径
		String path = uploadFilePath + File.separator + "cache" + File.separator;
		
		SFtpUtil client = new SFtpUtil();
		
		try {
			
			String ip = ftp.getFtp_ip();
			int port = 22;
			
			String username = ftp.getFtp_name();
			String pwd = ftp.getFtp_password();
			
			client.connect(ip, port, username, pwd);
			
		    client.downloadFile(ClusterSyncTools.getClusterRoot()+File.separator+fileFolder, acc_name, path, acc_name, null);
		    down_code = 100;
		    
		} catch (Exception e) {
			down_code = -100;
		} finally {
			client.close();
		}
		return down_code;
	}
	
	/**
	 * 
	 * @param ftp_id
	 * @param download_url
	 * @param acc_name
	 * @return
	 */
	public int ImageDownload(String ftp_id, String download_url, String acc_name) {
		download_url = this.redPigSysConfigService.getSysConfig().getUploadFilePath()
				+ download_url;
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;
		FileOutputStream foswm = null;
		int down_code = 100;// 100下载成功，-100下载失败
		FTPServer ftp = this.redPigFTPServerService.selectByPrimaryKey(CommUtil.null2Long(ftp_id));
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String path = uploadFilePath + File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 下载水印图片
			String remoteFileNamewm = download_url + File.separator + acc_name;
			foswm = new FileOutputStream(path + File.separator + acc_name);
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			down_code = -100;
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				down_code = -100;
			}
		}
		return down_code;
	}
	
	
	/**
	 * FTP服务器用户转移
	 * @param request
	 * @param response
	 * @param fid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "FTP服务器用户转移", value = "/ftpserver_transfer*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/ftpserver_transfer")
	public ModelAndView ftpserver_transfer(HttpServletRequest request,
			HttpServletResponse response, String fid, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/ftpserver_transfer.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		FTPServer ftp_from = this.redPigFTPServerService.selectByPrimaryKey(CommUtil.null2Long(fid));
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("ftp_type", 0);
		params.put("redpig_ftp_id", CommUtil.null2Long(fid));
		List<FTPServer> objs = this.redPigFTPServerService.queryFtpServerUserTrans(params);
		mv.addObject("objs", objs);
		mv.addObject("ftp_from", ftp_from);
		return mv;
	}

	/**
	 * FTP服务器删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "FTP服务器删除", value = "/ftpserver_del*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/ftpserver_del")
	public String ftpserver_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String type) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.redPigFTPServerService.delete(Long.parseLong(id));
			}
		}
		
		return "redirect:ftpserver_list?currentPage=" + currentPage
				+ "&type=" + type;
	}
	
	/**
	 * FTP服务器保存
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "FTP服务器保存", value = "/ftpserver_save*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/ftpserver_save")
	public ModelAndView ftpserver_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String ftp_amount) {
		WebForm wf = new WebForm();
		FTPServer ftpserver = null;
		if (id.equals("")) {
			ftpserver = wf.toPo(request, FTPServer.class);
			ftpserver.setAddTime(new Date());
		} else {
			FTPServer obj = this.redPigFTPServerService.selectByPrimaryKey(Long.parseLong(id));
			ftpserver = (FTPServer) wf.toPo(request, obj);
		}
		if (ftp_amount == null || ftp_amount.equals("")) {
			ftpserver.setFtp_amount(100);
		}
		if (id.equals("")) {
			this.redPigFTPServerService.save(ftpserver);
		} else
			this.redPigFTPServerService.update(ftpserver);
		// 新添加的系统服务器若设置为当前使用，将其他系统服务器设置为非当前使用
		if (ftpserver.getFtp_type() == 1 && ftpserver.getFtp_system() == 1) {
			Map<String,Object> map = Maps.newHashMap();
			map.put("ftp_system", 1);
			map.put("redPig_ftp_id", ftpserver.getId());
			map.put("ftp_type", 1);
			List<FTPServer> objs = this.redPigFTPServerService.queryPageListNotId(map);

			for (FTPServer obj : objs) {
				obj.setFtp_system(0);
				this.redPigFTPServerService.update(obj);
			}
		}
		
		SysConfig config = this.redPigSysConfigService.getSysConfig();
		String uploadFilePath = config.getUploadFilePath();
		String path = request.getSession().getServletContext().getRealPath("/") + uploadFilePath + File.separator + "cache";
		CommUtil.createFolder(path);// 创建缓存目录
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", "/ftpserver_list?currentPage=" + currentPage);
		mv.addObject("op_title", "保存服务器成功");
		mv.addObject("add_url", "/ftpserver_add");
		return mv;
	}

	
	/**
	 * FTP服务器编辑
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "FTP服务器编辑", value = "/ftpserver_edit*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/ftpserver_edit")
	public ModelAndView ftpserver_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ftpserver_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if (id != null && !id.equals("")) {
			FTPServer ftpserver = this.redPigFTPServerService.selectByPrimaryKey(Long.parseLong(id));
			mv.addObject("obj", ftpserver);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}
	
	/**
	 * FTP服务器添加
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "FTP服务器添加", value = "/ftpserver_add*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/ftpserver_add")
	public ModelAndView ftpserver_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ftpserver_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * FTP服务器列表
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "FTP服务器列表", value = "/ftpserver_list*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/ftpserver_list")
	public ModelAndView ftpserver_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ftpserver_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage,12, "ftp_sequence", "asc");
		if (type != null && !type.equals("")) {
			params.put("ftp_type", type);
		}else{
			params.put("ftp_type", 0);
		}
		
		IPageList pList = this.redPigFTPServerService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

}
