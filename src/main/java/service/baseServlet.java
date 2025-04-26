package service;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public abstract class baseServlet extends HttpServlet {
    @Override

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String methodName = null;

        // 尝试通过普通方式获取 method 参数
        methodName = request.getParameter("method");

        // 如果是 multipart/form-data，getParameter 会返回 null
        if (methodName == null && request.getContentType() != null &&
                request.getContentType().toLowerCase().startsWith("multipart/")) {
            Part methodPart = request.getPart("method");
            if (methodPart != null) {
                InputStream inputStream = methodPart.getInputStream();
                methodName = new String(inputStream.readAllBytes(), request.getCharacterEncoding());
            }
        }

        if (methodName == null) {
            response.sendError(400, "缺少 method 参数");
            return;
        }

        try {
            Method method = this.getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.setAccessible(true);
            method.invoke(this, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "服务器错误: " + e.getMessage());
        }
    }

}
