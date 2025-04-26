package service;
import com.google.gson.Gson;
import model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@WebServlet("/users")
public class manageServlet extends baseServlet{
    private UserConnector userConnector;
    private Gson gson;

    public void init() throws ServletException {
        this.userConnector = new UserConnector(SimpleConnectionPool.getInstance());
        this.gson = new Gson();
    }
    private void user(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json;charset=UTF-8");
        if (session == null) {
            response.sendError(401);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        int points = Integer.parseInt( session.getAttribute("points").toString());
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.println(gson.toJson(new userdao(username, points)));
            out.flush();
        }
    }
    public void getHistory(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        HttpSession session = request.getSession(false);
        int username = Integer.parseInt((String) session.getAttribute("username"));
        response.setContentType("application/json;charset=UTF-8");
        List<transaction> transactions = new ArrayList<>();
        transactions=userConnector.getTransactions(username);
        try(PrintWriter out=response.getWriter()){
            out.println(gson.toJson(transactions));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void notifications(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        if (session== null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        try (PrintWriter out = response.getWriter()) {
            int username = Integer.parseInt(session.getAttribute("username").toString());
            List<notification> notifications = new ArrayList<notification>();
            notifications = userConnector.getNotifictions(username);
            out.println(gson.toJson(notifications));
            out.flush();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void denouces(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        if (session.getAttribute("username") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        int denoucee= Integer.parseInt(request.getParameter("username"));
        String reason = request.getParameter("reason");
        try {
            userConnector.denouce(username,denoucee,reason);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void reviews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json;charset=UTF-8");
        if (session == null) {
            response.sendError(401);
            return;
        }
        String username = session.getAttribute("username").toString();
        int gid=Integer.parseInt(request.getParameter("gid"));
        String reason = request.getParameter("comment");
        try {
            userConnector.reviewTransaction(reason,gid);
            int sellerid=userConnector.getSellerId(gid);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = now.format(formatter);
            userConnector.addNotifictions(sellerid,"您的商品"+gid+"收到一条评论:"+reason,gid,time);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void getMoney(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json;charset=UTF-8");
        if (session.getAttribute("username") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        float money= userConnector.getMoney(username);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println(gson.toJson(money));
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void insertMoney(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json;charset=UTF-8");
        if (session.getAttribute("username") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        float addMoney= Float.parseFloat(request.getParameter("addMoney"));
        userConnector.insertMoney(username,addMoney);
    }
    private void justifyPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json;charset=UTF-8");
        if (session.getAttribute("username") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
            int username = Integer.parseInt(session.getAttribute("username").toString());
            String paypassword = request.getParameter("payPassword");
            PrintWriter out = response.getWriter();
            if(userConnector.justifypaypassword(username, paypassword)){
                response.setContentType("application/json;charset=UTF-8");
                out.println(gson.toJson(Map.of(
                        "status", "success",
                        "message", "支付密码验证成功"
                )));
            }else {
                response.setContentType("application/json;charset=UTF-8");
                out.println(gson.toJson(Map.of(
                        "status", "error",
                        "message", "支付密码错误"
                )));
            }

    }
    private void setPayPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json;charset=UTF-8");
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        String password = session.getAttribute("password").toString();
        String getpassword=request.getParameter("password");
        try(PrintWriter out = response.getWriter()) {
            if (password.equals(getpassword)) {
                String newPassword=request.getParameter("newPassword");
                userConnector.setPaypassword(username,newPassword);
            } else{
                out.println("Passwords do not match");
                out.flush();
            }
        }
    }

    }

