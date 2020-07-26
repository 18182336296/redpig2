package com.redpigmall.view.web.tools;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class RedPigResponseTools {
	public static void sendJson(HttpServletResponse response, String json) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
