package com.redpigmall.kuaidi.pojo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * 
 * <p>
 * Title: MapCustomConverter.java
 * </p>
 * 
 * <p>
 * Description: 该实体类来自快递100提供的接口
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
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class MapCustomConverter extends AbstractCollectionConverter {
	public MapCustomConverter(Mapper mapper) {
		super(mapper);
	}

	public boolean canConvert(Class type) {
		return type.equals(HashMap.class);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Map<String, Object> map = (Map) source;
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, entry
					.getKey().toString(), Map.Entry.class);

			writer.setValue(entry.getValue().toString());
			writer.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		Map<String, Object> map = (Map) createCollection(context
				.getRequiredType());
		populateMap(reader, context, map);
		return map;
	}

	protected void populateMap(HierarchicalStreamReader reader,
			UnmarshallingContext context, Map map) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			Object key = reader.getNodeName();
			Object value = reader.getValue();
			map.put(key, value);
			reader.moveUp();
		}
	}
}
