package com.redpigmall.api.qrcode;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 
 * <p>
 * Title: QRCodeUtil.java
 * </p>
 * 
 * <p>
 * Description:二维码工具类,用来生成和解析二维码，可以生成中心带有Logo或者不带有Logo的二维码信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2018-2-5
 * 
 * @version redpigmall_b2b2c 2018
 */
@SuppressWarnings({"unused","unchecked","rawtypes"})
public class QRCodeUtil {
	/** 编码 */
	
	private static final String CHARSET = "utf-8";

	private static final String FORMAT_NAME = "JPG";
	/** 二维码尺寸 */
	private static final int QRCODE_SIZE = 300;
	/** LOGO宽度 */
	private static final int WIDTH = 60;
	/** LOGO高度 */
	private static final int HEIGHT = 60;
	
	/**
	 * 创建图片
	 * @param content
	 * @param imgPath
	 * @param needCompress
	 * @return
	 * @throws Exception
	 */
	private static BufferedImage createImage(String content, String imgPath,boolean needCompress) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, Integer.valueOf(1));
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
				BarcodeFormat.QR_CODE, 300, 300, hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, 1);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? -16777216 : -1);
			}
		}
		if ((imgPath == null) || ("".equals(imgPath))) {
			return image;
		}
		// 去掉logo
		 insertImage(image, imgPath, needCompress);
		return image;
	}

	/**
	 * 给二维码插入LOGO
	 * 
	 * @param source
	 *            二维码图片
	 * @param imgPath
	 *            LOGO图片地址
	 * @param needCompress
	 *            是否压缩
	 * @throws Exception
	 */
	private static void insertImage(BufferedImage source, String imgPath,
			boolean needCompress) throws Exception {
//		File file = new File(imgPath);
//		if (!file.exists()) {
//			System.err.println(imgPath + "   该文件不存在！");
//			return;
//		}
		URL url = new URL(imgPath);
		
		Image src = ImageIO.read(url);
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompress) {
			if (width > 60) {
				width = 60;
			}
			if (height > 60) {
				height = 60;
			}
			Image image = src.getScaledInstance(width, height, 4);
			BufferedImage tag = new BufferedImage(width, height, 1);
			Graphics g = tag.getGraphics();
			try {
				g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			} catch (Exception e) {
				e.printStackTrace();
			}
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (300 - width) / 2;
		int y = (300 - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6.0F, 6.0F);
		graph.setStroke(new BasicStroke(3.0F));
		graph.draw(shape);
		graph.dispose();
	}

	/**
	 * 生成二维码(内嵌LOGO)
	 * 
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址
	 * @param destPath
	 *            存放目录
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static void encode(String content, String logoPath, String destPath,boolean needCompress) {
		try {
			BufferedImage image = createImage(content, logoPath, needCompress);
			mkdirs(destPath);
			ImageIO.write(image, "JPG", new File(destPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
	 * 
	 * @param destPath
	 *            存放目录
	 */
	public static void encode(String content, String logoPath,
			HttpServletResponse response, boolean needCompress) {
		try {
			BufferedImage image = createImage(content, logoPath, needCompress);
			ImageIO.write(image, "JPG", response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成二维码(内嵌LOGO)
	 * 
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址,生成后在二维码中心位置显示
	 * @param destPath
	 *            存储地址
	 * @throws Exception
	 */
	public static void mkdirs(String destPath) {
		File file = new File(destPath);
		if ((!file.exists()) && (!file.isDirectory())) {
			file.mkdirs();
		}
	}

	/**
	 * 生成二维码
	 * 
	 * @param content
	 *            内容
	 * @param destPath
	 *            存储地址
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static void encode(String content, String logoPath, String destPath)
			throws Exception {
		encode(content, logoPath, destPath, false);
	}

	/**
	 * 生成二维码
	 * 
	 * @param content
	 *            内容
	 * @param destPath
	 *            存储地址
	 * @throws Exception
	 */
	public static void encode(String content, String destPath,
			boolean needCompress) throws Exception {
		encode(content, null, destPath, needCompress);
	}

	/**
	 * 生成二维码(内嵌LOGO)
	 * 
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址,生成后在二维码中心位置显示
	 * @param output
	 *            输出流
	 * @param needCompress
	 *            是否压缩LOGO,启用后logo会自动压缩为小图在中心显示，不启用Logo过大会挡住部分二维码信息
	 * @throws Exception
	 */
	public static void encode(String content, String destPath) {
		try {
			encode(content, null, destPath, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成二维码
	 * 
	 * @param content
	 *            内容
	 * @param output
	 *            输出流
	 * @throws Exception
	 */
	public static void encode(String content, String logoPath,
			OutputStream output, boolean needCompress) throws Exception {
		BufferedImage image = createImage(content, logoPath, needCompress);
		ImageIO.write(image, "JPG", output);
	}

	/**
	 * 解析二维码
	 * 
	 * @param file
	 *            二维码图片
	 * @return
	 * @throws Exception
	 */
	public static void encode(String content, OutputStream output)
			throws Exception {
		encode(content, null, output, false);
	}

	/**
	 * 解析二维码
	 * 
	 * @param path
	 *            二维码图片地址
	 * @return
	 * @throws Exception
	 */
	public static String decode(File file) throws Exception {
		BufferedImage image = ImageIO.read(file);
		if (image == null) {
			return null;
		}
		BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
				image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		Hashtable<DecodeHintType, Object> hints = new Hashtable();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
		Result result = new MultiFormatReader().decode(bitmap, hints);
		String resultStr = result.getText();
		return resultStr;
	}

	public static String decode(String path) throws Exception {
		return decode(new File(path));
	}

	public static void testmai(String[] args) throws Exception {
		String text = "http://www.redpigmall.net";
		encode(text, "d:/100_100.png", "d:/123.png", true);
	}
}
