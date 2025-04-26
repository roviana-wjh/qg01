package service;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.SimpleConnectionPool;
import model.UserConnector;
import model.adminConnector;

@WebServlet("/api/admin/*")
public class adminServlet extends HttpServlet {
    private adminConnector adminConnector;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.adminConnector = new adminConnector(SimpleConnectionPool.getInstance());
        this.gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "无效的请求路径")));
                return;
            }
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject jsonRequest = gson.fromJson(sb.toString(), JsonObject.class);
            String username = jsonRequest.get("username").getAsString();
            String password = jsonRequest.get("password").getAsString();


            switch (pathInfo) {
                case "/login":
                    int loginSuccess = adminConnector.Login(username, password);
                    if (loginSuccess==-1) {
                        HttpSession session = request.getSession(true);
                        session.setAttribute("username", username);
                        session.setMaxInactiveInterval(30 * 60); // 30分钟
                        out.print(gson.toJson(new ApiResponse(true, "登录成功")));
                        System.out.println(username+password);
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        out.print(gson.toJson(new ApiResponse(false, "用户名或密码错误")));
                    }
                    break;

                case "/register":
                    boolean registerSuccess = adminConnector.registerUser(username, password);
                    if (registerSuccess) {
                        out.print(gson.toJson(new ApiResponse(true, "注册成功")));
                    } else {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        out.print(gson.toJson(new ApiResponse(false, "用户名已存在")));
                    }
                    break;

                default:
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ApiResponse(false, "未知操作：" + pathInfo)));
            }
        }
    }

    // 统一 API 响应结构
    private static class ApiResponse {
        private boolean success;
        private String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}