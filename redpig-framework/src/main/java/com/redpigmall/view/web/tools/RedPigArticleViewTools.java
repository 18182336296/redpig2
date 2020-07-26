package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.domain.Article;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.service.RedPigArticleClassService;
import com.redpigmall.service.RedPigArticleService;

/**
 * 
 * <p>
 * Title: RedPigArticleViewTools.java
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

	public Article queryArticle(Long id, int position) {
		Article article = this.articleService.selectByPrimaryKey(id);
		if (article != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("addTime", article.getAddTime());
			params.put("articleClass_id", article.getArticleClass().getId());
			params.put("type", "user");
			params.put("display", Boolean.valueOf(true));

			if (position > 0) {
				params.put("addTime_more_than", article.getAddTime());
			} else {
				params.put("addTime_less_than", article.getAddTime());
			}

			List<Article> objs = this.articleService
					.queryPageList(params, 0, 1);
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
		map.put("articleClass_id", art_id);
		map.put("articleClass_parent_id", art_id);
		map.put("display", Boolean.valueOf(true));
		List<Article> list = Lists.newArrayList();
		ArticleClass ac = this.articleClassService.selectByPrimaryKey(art_id);
		if (("user".equals(type)) && (ac != null)) {
			if (ac.getChilds().size() == 0) {
				list = this.articleService.queryPageList(map, 0, 5);
			} else {
				list = this.articleService.queryPageList(map, 0, 5);
			}
		}
		if (("seller".equals(type)) && (ac != null)) {
			if (ac.getChilds().size() == 0) {
				list = this.articleService.queryPageList(map);
			} else {
				list = this.articleService.queryPageList(map);
			}
		}
		return list;
	}

	public List<Article> chat_list(Long art_id) {

		Map<String, Object> map = Maps.newHashMap();
		map.put("articleClass_id", art_id);
		map.put("display", Boolean.valueOf(true));
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

	public void change() {
		Set<String> marks = Sets.newTreeSet();
		Map<String, Object> params = Maps.newHashMap();
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		params.put("marks", marks);
		List<ArticleClass> acs = this.articleClassService.queryPageList(params);

		if (acs.size() > 0) {
			for (ArticleClass ac : acs) {
				ac.setOne_type(1);
				ac.setTwo_type("right");
				this.articleClassService.update(ac);
				if (ac.getChilds().size() > 0) {
					for (ArticleClass ac_c : ac.getChilds()) {
						ac_c.setOne_type(1);
						ac_c.setTwo_type("right");
						this.articleClassService.update(ac_c);
					}
				}
			}
		}
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

		List<ArticleClass> acs_ = this.articleClassService
				.queryPageList(params);

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
		List<ArticleClass> acs_z = this.articleClassService
				.queryPageList(params);
		if (acs_z.size() == 1) {
			acs_z.get(0).setOne_type(1);
			acs_z.get(0).setTwo_type("bottom");
			this.articleClassService.update(acs_z.get(0));
			if (acs_z.get(0).getChilds().size() > 0) {

				for (ArticleClass ac_c : ((ArticleClass) acs_z.get(0))
						.getChilds()) {

					ac_c.setOne_type(1);
					ac_c.setTwo_type("bottom");
					this.articleClassService.update(ac_c);
				}
			}
		}
		params.clear();
		params.put("mark", "chatting_article");
		List<ArticleClass> acs_c = this.articleClassService
				.queryPageList(params);
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
		Set<String> marks_s = new TreeSet<String>();

		marks_s.clear();
		marks_s.add("shanjiaxuzhi");

		marks_s.add("new_func");
		params.put("marks", marks_s);
		params.put("parent", -1);
		List<ArticleClass> acs_seller = this.articleClassService
				.queryPageList(params);

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
}
