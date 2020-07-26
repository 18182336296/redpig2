package com.redpigmall.view.web.tools;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.Article;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.domain.Goods;
import com.redpigmall.service.RedPigActivityGoodsService;
import com.redpigmall.service.RedPigArticleClassService;
import com.redpigmall.service.RedPigArticleService;
import com.redpigmall.service.RedPigGoodsService;

/**
 * 
 * <p>
 * Title: ArticleViewTools.java
 * </p>
 * 
 * <p>
 * Description: 文章查询工具类，用于前端velocity模板中的信息查询
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
 * @author redpig
 * 
 * @date 2014-9-29
 * 
 * @version redpigmall_b2b2c 2016
 */
@Component
public class RedPigArticleViewTools {
	@Autowired
	private RedPigArticleService articleService;
	@Autowired
	private RedPigArticleClassService articleClassService;
	@Autowired
	private RedPigArticleClassService redPigArticleClassService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigActivityGoodsService activityGoodsService;
	@Autowired
	private RedPigIntegralViewTools integralViewTools;
	

	/**
	 * 根据postion参数查询谋id文章的上一篇、下一篇文章，position为-1为上一篇，position为1为下一篇
	 * 
	 * @param id
	 * @param position
	 * @return
	 */
	public Article queryArticle(Long id, int position) {
		Map<String,Object> maps = Maps.newHashMap();
		
		Article article = this.articleService.selectByPrimaryKey(id);
		if (article != null) {
			
			maps.put("articleClass_id",article.getArticleClass().getId());
			maps.put("type", "user");
			maps.put("display", Boolean.valueOf(true));
			
			if (position > 0) {
				maps.put("add_Time_more_than", article.getAddTime());
			} else {
				maps.put("add_Time_less_than", article.getAddTime());
			}
			
			maps.put("orderBy", "addTime");
			maps.put("orderType", "desc");
			List<Article> objs = this.articleService.queryPageList(maps,0,1);
			if (objs.size() > 0) {
				return (Article) objs.get(0);
			}
			Article obj = new Article();
			obj.setTitle("没有了");
			return obj;
		}
		Article obj = new Article();
		obj.setTitle("没有了");
		return obj;
	}

	public List<Article> article_list(Long art_id, String type) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderBy", "sequence");
		map.put("orderType", "desc");
		map.put("display", Boolean.valueOf(true));
		
		List<Article> list = Lists.newArrayList();
		ArticleClass ac = this.articleClassService.selectByPrimaryKey(art_id);
		if (("user".equals(type)) && (ac != null)) {
			if (ac.getChilds().size() == 0) {
				map.put("articleClass_id", art_id);
				
				list = this.articleService.queryPageList(map,0,5);
				
			} else {
				map.put("articleClass_parent_id", art_id);
				
				list = this.articleService.queryPageList(map,0,5);
				
			}
		}
		if (("seller".equals(type)) && (ac != null)) {
			if (ac.getChilds().size() == 0) {
				map.put("articleClass_id", art_id);
				
				list = this.articleService.queryPageList(map);
				
			} else {
				map.put("articleClass_id", art_id);
				
				list = this.articleService.queryPageList(map);
				
			}
		}
		return list;
	}

	public List<Article> article_list(Long art_id, String type,int num) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderBy", "sequence");
		map.put("orderType", "desc");
		map.put("display", Boolean.valueOf(true));
		
		List<Article> list = Lists.newArrayList();
		ArticleClass ac = this.articleClassService.selectByPrimaryKey(art_id);
		if (("user".equals(type)) && (ac != null)) {
			if (ac.getChilds().size() == 0) {
				map.put("articleClass_id", art_id);
				
				list = this.articleService.queryPageList(map,0,num);
				
			} else {
				map.put("articleClass_parent_id", art_id);
				
				list = this.articleService.queryPageList(map,0,num);
				
			}
		}
		if (("seller".equals(type)) && (ac != null)) {
			if (ac.getChilds().size() == 0) {
				map.put("articleClass_id", art_id);
				
				list = this.articleService.queryPageList(map);
				
			} else {
				map.put("articleClass_id", art_id);
				
				list = this.articleService.queryPageList(map);
				
			}
		}
		return list;
	}
	public void change() {
		List<String> marks = Lists.newArrayList();
		
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		
		List<ArticleClass> acs = redPigArticleClassService.findArticleClassByMark(marks);
		
		if (acs.size() > 0) {
			for (com.redpigmall.domain.ArticleClass ac : acs) {
				ac.setOne_type(1);
				ac.setTwo_type("right");
				this.redPigArticleClassService.update(ac);
				if (ac.getChilds().size() > 0) {
					for (com.redpigmall.domain.ArticleClass ac_c : ac.getChilds()) {
						ac_c.setOne_type(1);
						ac_c.setTwo_type("right");
						this.redPigArticleClassService.update(ac_c);
					}
				}
			}
		}
		
		Map<String,Object> params = Maps.newHashMap();
		
		params.clear();
		Set<String> marks_ = Sets.newTreeSet();
		marks_.add("gouwuxuzhi");
		marks_.add("peisong");
		marks_.add("zhifu");
		marks_.add("shouhou");
		marks_.add("xinshou");
		marks_.add("zizhu");
		marks_.add("cuxiao");
		params.put("marks", marks_);
		params.put("parent", -1);
		List<ArticleClass> acs_ = this.articleClassService.queryPageList(params);
		
		if (acs_.size() > 0) {
			for (ArticleClass ac : acs_) {
				ac.setOne_type(1);
				ac.setTwo_type("bottom");
				this.articleClassService.update(ac);
				if (ac.getChilds().size() > 0) {
					for (ArticleClass ac_c : ac.getChilds()) {
						ac_c.setOne_type(1);
						ac_c.setTwo_type("bottom");
						this.articleClassService.update(ac_c);
					}
				}
			}
		}
		
		params.clear();
		params.put("className", "招商合作");
		List<ArticleClass> acs_z = this.articleClassService.queryPageList(params);
		
		if (acs_z.size() == 1) {
			acs_z.get(0).setOne_type(1);
			acs_z.get(0).setTwo_type("bottom");
			this.articleClassService.update(acs_z.get(0));
			if (acs_z.get(0).getChilds().size() > 0) {
				for (ArticleClass ac_c : acs_z.get(0).getChilds()) {
					ac_c.setOne_type(1);
					ac_c.setTwo_type("bottom");
					this.articleClassService.update(ac_c);
				}
			}
		}
		params.clear();
		params.put("mark", "chatting_article");
		List<ArticleClass> acs_c = this.articleClassService.queryPageList(params);
		
		if (acs_c.size() == 1) {
			acs_c.get(0).setOne_type(1);
			acs_c.get(0).setTwo_type("chat");
			this.articleClassService.update(acs_c.get(0));
			if (acs_c.get(0).getChilds().size() > 0) {
				for (ArticleClass ac_c : acs_c.get(0).getChilds()) {
					ac_c.setOne_type(1);
					ac_c.setTwo_type("chat");
					this.articleClassService.update(ac_c);
				}
			}
		}
		
		params.clear();
		Set<String> marks_s = Sets.newTreeSet();
		marks_s.clear();
		marks_s.add("shanjiaxuzhi");
		
		marks_s.add("new_func");
		params.put("marks", marks_s);
		params.put("parent", -1);
		List<ArticleClass> acs_seller = this.articleClassService.queryPageList(params);
		
		if (acs_seller.size() > 0) {
			for (ArticleClass ac : acs_seller) {
				ac.setOne_type(2);
				ac.setTwo_type("seller");
				this.articleClassService.update(ac);
				if (ac.getChilds().size() > 0) {
					for (ArticleClass ac_c : ac.getChilds()) {
						ac_c.setOne_type(2);
						ac.setTwo_type("seller");
						this.articleClassService.update(ac_c);
					}
				}
			}
		}
	}

	public List<Article> chat_list(Long art_id) {

		Map<String, Object> map = Maps.newHashMap();
		map.put("art_id", art_id);
		map.put("display", Boolean.valueOf(true));
		map.put("orderBy", "sequence");
		map.put("orderType", "desc");
		
		List<Article> list = Lists.newArrayList();
		ArticleClass ac = this.articleClassService.selectByPrimaryKey(art_id);
		
		if (ac != null) {
			if (ac.getChilds().size() == 0) {
				list = this.articleService.queryPageList(map, 0, 10);
			} else {
				list = this.articleService.queryPageList(map, 0, 10);
			}
		}
		return list;
	}
	
	@SuppressWarnings({ "unused", "rawtypes" })
	public BigDecimal getActivityGoodsRebate(String goods_id, String user_id) {
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		
		BigDecimal rate = new BigDecimal(1.0D);
		if ((obj != null) && (obj.getActivity_status() == 2)) {
			ActivityGoods actGoods = this.activityGoodsService.selectByPrimaryKey(obj.getActivity_goods_id());
			if (actGoods != null) {
				Activity act = actGoods.getAct();
				String level_name = null;
				List<Map> rate_maps = JSON.parseArray(act.getAc_rebate_json(),Map.class);
				int[] temp_level = new int[rate_maps.size()];
				Map level_map = this.integralViewTools.query_user_level(String.valueOf(user_id));
				
				level_name = CommUtil.null2String(level_map.get("name"));
				int user_level = CommUtil.null2Int(level_map.get("level"));
				for (int i = 0; i < rate_maps.size(); i++) {
					Map m_rebate = (Map) rate_maps.get(i);
					if (CommUtil.null2Int(m_rebate.get("level")) == user_level) {
						rate = BigDecimal.valueOf(CommUtil.null2Double(m_rebate
								.get("rebate")));
						break;
					}
				}
				if (rate == null) {
					Map target_map = null;
					int heigh_level = 0;
					for (Map temp : rate_maps) {
						if (CommUtil.null2Int(temp.get("level")) >= heigh_level) {
							heigh_level = CommUtil.null2Int(temp.get("level"));
							target_map = temp;
						}
					}
					rate = BigDecimal.valueOf(CommUtil.null2Double(target_map.get("rebate")));
				}
			}
		}
		return rate;
	}
}
