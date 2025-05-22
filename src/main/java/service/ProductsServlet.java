package service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.google.gson.Gson;
import model.SimpleConnectionPool;
import model.UserConnector;
import model.goods;
import model.updateDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.http.HttpRequest;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@MultipartConfig
@WebServlet("/products")
public class ProductsServlet extends baseServlet{
    private UserConnector userConnector;
    private Gson gson;
    private OSS ossClient;
    @Override
    public void init() throws ServletException {
        this.userConnector = new UserConnector(SimpleConnectionPool.getInstance());
        this.gson = new Gson();
        ossClient = new OSSClientBuilder().build(

                "oss-cn-guangzhou.aliyuncs.com",
                "",
                ""
        );
    }
    private void add(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        if (session == null || session.getAttribute("username") == null) {
            response.sendError(401);
            return;
        }
        String usernameStr = (String) session.getAttribute("username");
        String name = getStringPart(request, "name");
        String type = getStringPart(request, "type");
        float price = Float.parseFloat(getStringPart(request, "price"));

        Part filePart = request.getPart("image");
        String fileName = UUID.randomUUID() + "-" + filePart.getSubmittedFileName();
        InputStream fileContent = filePart.getInputStream();
        String bucketName = "wjh158";
        String objectName = "goods/" + fileName;
        ossClient.putObject(bucketName, objectName, fileContent);
        String fileUrl = "https://" + bucketName + ".oss-cn-guangzhou.aliyuncs.com/" + objectName;

        try {
            SimpleConnectionPool pool = SimpleConnectionPool.getInstance();
            UserConnector connector = new UserConnector(pool);
            int uid = Integer.parseInt(usernameStr);
            connector.saveGoods(uid, price, fileUrl, name, type);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "服务器错误: " + e.getMessage());
            return;
        }

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("success", true);
        resMap.put("imageUrl", fileUrl);
        resMap.put("name", name);
        resMap.put("price", price);
        resMap.put("seller", usernameStr);

        response.getWriter().write(gson.toJson(resMap));
    }

    private String getStringPart(HttpServletRequest request, String name) throws IOException, ServletException {
        Part part = request.getPart(name);
        if (part == null) return null;
        InputStream inputStream = part.getInputStream();
        return new String(inputStream.readAllBytes(), request.getCharacterEncoding());
    }

    private  void edit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return;
        }
        int gid = Integer.parseInt(request.getParameter("gid"));
        float price = Float.parseFloat(request.getParameter("price"));
        String goodsname = request.getParameter("goodsname");
        int choice=Integer.parseInt(request.getParameter("choice"));
        try {
            userConnector.editGoods(gid,price,goodsname,choice);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void getDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        List<goods> goodsList = new ArrayList<>();
        goodsList=userConnector.getDelete(username);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(gson.toJson(goodsList));
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        int gid = Integer.parseInt(request.getParameter("id"));
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String username = session.getAttribute("username").toString();
        response.setContentType("application/json;charset=UTF-8");
        try {
            userConnector.deleteGoods(username,gid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void getUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        response.setContentType("application/json;charset=UTF-8");
        List<updateDao> goodsList = new ArrayList<>();
        goodsList=userConnector.getupdate(username);
        try(PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(goodsList));
            out.flush();
        }
    }
    private void updateStatus(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int gid = Integer.parseInt(request.getParameter("gid"));
        userConnector.updateStatus(gid);
        int buyuser = userConnector.getBuyuser(gid);
        response.setContentType("application/json;charset=UTF-8");
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = now.format(dateTimeFormatter);
        userConnector.addNotifictions(buyuser,"尊敬的客户"+buyuser+"您的商品于"+date+"已到货，商品号为"+gid,gid,date);
    }
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
    }
