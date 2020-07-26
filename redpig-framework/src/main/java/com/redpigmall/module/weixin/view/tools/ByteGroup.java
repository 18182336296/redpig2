package com.redpigmall.module.weixin.view.tools;

import java.util.ArrayList;

import com.google.common.collect.Lists;
/**
 * <p>
 * Title: ByteGroup.java
 * </p>
 * 
 * <p>
 * Description:ByteGroup
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
 * @date 2015-1-16
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
class ByteGroup {
	ArrayList<Byte> byteContainer = Lists.newArrayList();

	public byte[] toBytes() {
		byte[] bytes = new byte[this.byteContainer.size()];
		for (int i = 0; i < this.byteContainer.size(); i++) {
			bytes[i] = ((Byte) this.byteContainer.get(i)).byteValue();
		}
		return bytes;
	}

	public ByteGroup addBytes(byte[] bytes) {
		
		
		for (byte b : bytes) {
			
			this.byteContainer.add(Byte.valueOf(b));
		}
		return this;
	}

	public int size() {
		return this.byteContainer.size();
	}
}
