package com.redpigmall.api.zip;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 
 * <p>
 * Title: CompressionResponse.java
 * </p>
 * 
 * <p>
 * Description: 完成数据输出流的压缩工作，该类在web应用中的可以使用web服务器配置完成相同的功能,后续版本将会删除，仅仅保留学习使用
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
public class CompressionResponse extends HttpServletResponseWrapper {
	protected HttpServletResponse response;
	private ServletOutputStream out;
	private CompressedStream compressedOut;
	private PrintWriter writer;
	protected int contentLength;

	public CompressionResponse(HttpServletResponse response) throws IOException {
		super(response);
		this.response = response;
		this.compressedOut = new CompressedStream(response.getOutputStream());
	}

	public void setContentLength(int len) {
		this.contentLength = len;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (this.out == null) {
			if (this.writer != null) {
				throw new IllegalStateException(
						"getWriter() has already been called on this response.");
			}
			this.out = this.compressedOut;
		}
		return this.out;
	}

	public PrintWriter getWriter() throws IOException {
		if (this.writer == null) {
			if (this.out != null) {
				throw new IllegalStateException(
						"getOutputStream() has already been called on this response.");
			}
			this.writer = new PrintWriter(this.compressedOut);
		}
		return this.writer;
	}

	public void flushBuffer() {
		try {
			if (this.writer != null) {
				this.writer.flush();
			} else if (this.out != null) {
				this.out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		super.reset();
		try {
			this.compressedOut.reset();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void resetBuffer() {
		super.resetBuffer();
		try {
			this.compressedOut.reset();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() throws IOException {
		this.compressedOut.close();
	}
}
