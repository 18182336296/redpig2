/*
 * UserMapper.java
 * Copyright(C) RedPigMall
 * All rights reserved.
 * -----------------------------------------------
 * 2016-05-18 Created
 */
package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Favorite;

public interface RedPigMapper extends SupperMapper {

	Map<String, Object> select(Map<String, Object> map);

	Favorite selectById(Long id);

	List<Favorite> queryPageList(Map<String, Object> maps);

}