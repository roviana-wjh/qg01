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
import java.util.ArrayList;
import java.util.List;
@WebServlet("/trade")
public class tradeServlet extends baseServlet {
    private UserConnector userConnector;
    private Gson gson;

    public void init() throws ServletException {
        this.userConnector = new UserConnector(SimpleConnectionPool.getInstance());
        this.gson = new Gson();
    }

    private void unrecieve(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(401);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            List<trade> transactions = new ArrayList<>();
            transactions = userConnector.getUnrecievedTransactions(username);
            out.print(gson.toJson(transactions));
            out.flush();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void recieve(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendError(401);
            return;
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        int username = Integer.parseInt(session.getAttribute("username").toString());
        try (PrintWriter out = response.getWriter()) {
            List<recievedTrade> transactions = new ArrayList<>();
            transactions = userConnector.getRecievedTransactions(username);
            out.print(gson.toJson(transactions));
            out.flush();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void pending(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String username = session.getAttribute("username").toString();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (username == null) {
            response.sendError(401);
            return;
        }
        try (PrintWriter out = response.getWriter()) {
            List<transaction> transactions = new ArrayList<>();
            transactions = userConnector.getPendingTransactions(username);
            out.print(gson.toJson(transactions));
            out.flush();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void cancel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendError(401);
            return;
        }
        int gid = Integer.parseInt(request.getParameter("gid"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        userConnector.cancelTransaction(gid);
    }

    private void buynow(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendError(401);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("user").toString());
        float money = Float.parseFloat(request.getParameter("money"));
        int sellname = Integer.parseInt(request.getParameter("username"));
        int gid = Integer.parseInt(request.getParameter("gid"));
        float price = Float.parseFloat(request.getParameter("price"));
        String goodsname = request.getParameter("goodsname");
        money -= price;
        session.setAttribute("money", money);

        try {
            userConnector.buygoods(username, sellname, gid, price, goodsname);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void buylater(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession();
        if (session == null) {
            response.sendError(401);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        float money = userConnector.getMoney(username);
        int sellname = Integer.parseInt(request.getParameter("sellerId"));
        int gid = Integer.parseInt(request.getParameter("gid"));
        float price = Float.parseFloat(request.getParameter("price"));
        String goodsname = request.getParameter("goodsname");
        money -= price;
        session.setAttribute("money", money);
        try {
            userConnector.buygoods(sellname,username, gid, price, goodsname);
            userConnector.changePayway(gid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

