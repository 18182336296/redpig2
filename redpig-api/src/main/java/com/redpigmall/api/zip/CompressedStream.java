package com.redpigmall.api.zip;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * 
 * <p>
 * Title: CompressedStream.java
 * </p>
 * 
 * <p>
 * Description:压缩输出流
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
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
public class CompressedStream extends ServletOutputStream {
	private ServletOutputStream out;
	private GZIPOutputStream gzip;

	/**
	 * 指定压缩缓冲流
	 * 
	 * @param 输出流到压缩
	 * @throws IOException
	 *             if an error occurs with the {@link GZIPOutputStream}.
	 */
	public CompressedStream(ServletOutputStream out) throws IOException {
		this.out = out;
		reset();
	}

	public void close() throws IOException {
		this.gzip.close();
	}

	public void flush() throws IOException {
		this.gzip.flush();
	}

	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		this.gzip.write(b, off, len);
	}

	public void write(int b) throws IOException {
		this.gzip.write(b);
	}

	public void reset() throws IOException {
		this.gzip = new GZIPOutputStream(this.out);
	}

	@Override
	public boolean isReady() {
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		
		// TODO Auto-generated method stub
		
	}
}
