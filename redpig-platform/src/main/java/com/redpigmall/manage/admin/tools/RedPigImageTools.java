package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigAlbumService;

/**
 * 
 * <p>
 * Title: RedPigImageTools.java
 * </p>
 * 
 * <p>
 * Description: 图片工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2014-10-28
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigImageTools {
	@Autowired
	private RedPigAccessoryService accessoryService;
	@Autowired
	private RedPigAlbumService albumService;

	public Accessory queryImg(Object id) {
		if ((id != null) && (!id.equals(""))) {
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			return img;
		}
		return null;
	}

	public List<Accessory> queryImgs(String ids) {
		List<Accessory> list = Lists.newArrayList();
		if ((ids != null) && (!ids.equals(""))) {
			for (String str : ids.split(",")) {
				if ((str != null) && (!str.equals(""))) {
					Accessory img = this.accessoryService
							.selectByPrimaryKey(CommUtil.null2Long(str));
					list.add(img);
				}
			}
		}
		return list;
	}

	public List<Accessory> queryAlbumPhotos(String album_id) {
		List<Accessory> list = Lists.newArrayList();
		Album album = this.albumService.selectByPrimaryKey(CommUtil
				.null2Long(album_id));
		if (album != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("album_id", CommUtil.null2Long(album_id));
			list = this.accessoryService.queryPageList(params);
		}
		return list;
	}

	public int queryAlbumPhotosSize(String album_id) {
		int size = 0;
		Album album = this.albumService.selectByPrimaryKey(CommUtil
				.null2Long(album_id));
		if (album != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("album_id", CommUtil.null2Long(album_id));
			size = this.accessoryService.selectCount(params);
		}
		return size;
	}
}
