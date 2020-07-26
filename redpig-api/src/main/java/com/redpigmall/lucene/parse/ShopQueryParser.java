package com.redpigmall.lucene.parse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;

import com.redpigmall.api.tools.CommUtil;

/**
 * 
 * <p>
 * Title: ShopQueryParser.java
 * </p>
 * 
 * <p>
 * Description: 自定义lucene查询分析器，支持区间查询
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
 * @author redpig,jy
 * 
 * @date 2014-10-30
 * 
 * @version redpigmall_b2b2c_2015
 */
public class ShopQueryParser extends QueryParser {
	public ShopQueryParser(String field, Analyzer a) {
		super(field, a);
	}
	
	/**
	 * @see org.apache.lucene.queryparser.classic.QueryParserBase#getRangeQuery(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	protected Query getRangeQuery(String field, String min, String max,boolean minInclusive, boolean maxInclusive) throws ParseException {
		TermRangeQuery query = (TermRangeQuery) super.getRangeQuery(field,min, max, minInclusive, maxInclusive);
		if ("goods_inventory".equals(field)) {
			return NumericRangeQuery.newIntRange(field,
					CommUtil.null2Int(min),
					CommUtil.null2Int(max),
					query.includesLower(), query.includesUpper());
		}
		if ("store_price".equals(field)) {
			return NumericRangeQuery.newDoubleRange(field,
					Double.valueOf(CommUtil.null2Double(min)),
					Double.valueOf(CommUtil.null2Double(max)), true, true);
		}
		return query;
	}
}
