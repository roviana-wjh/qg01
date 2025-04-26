package service;

import com.google.gson.Gson;
import model.SimpleConnectionPool;
import model.UserConnector;
import model.goods;
import model.goodsDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/product")
public class GoodsServlet extends baseServlet {
    private UserConnector userConnector;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.userConnector = new UserConnector(SimpleConnectionPool.getInstance());
        this.gson = new Gson();
    }
    private void browse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return;
        }

        int username = Integer.parseInt(session.getAttribute("username").toString());
        List<goods> goodsList = userConnector.browseGoods();
        List<goodsDao> productList = new ArrayList<>();
        for (goods g : goodsList) {
            productList.add(new goodsDao(g.getGid(), g.getName(),g.getPrice(),g.getUsername(), g.getUrl()));
        }
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(productList));
        out.flush();
    }
    private static class ConfirmRequest {
        int id;
    }
    private  void confirmReceive(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        BufferedReader reader = request.getReader();
        ConfirmRequest confirmRequest = gson.fromJson(reader, ConfirmRequest.class);
        int gid = confirmRequest.id;
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return;
        }
        response.setContentType("application/json;charset=UTF-8");
        try {
            userConnector.confirmTransaction(username,gid);
            int sellerID =userConnector.getSellerIDbygid(gid);
            float price=userConnector.getPrice(gid);
            String goodsname=userConnector.getGoodsname(gid);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = now.format(formatter);
            userConnector.addNotifictions(sellerID,"尊敬的用户"+"您的商品"+gid+"已确认收货",gid,time);
            if(userConnector.getPayway(gid)==1){
                userConnector.buygoods(username,sellerID,gid,price,goodsname);
                userConnector.sellgoods(sellerID,price);
                userConnector.deleteGoods(gid);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void selectGoodsByType(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String type = request.getParameter("type");
        response.setContentType("application/json;charset=UTF-8");
        List<goodsDao> goodsList = userConnector.selectGoodsByType(type);
        List<goodsDao> productList = new ArrayList<>();
        for (goodsDao g : goodsList) {
            productList.add(new goodsDao(g.getId(), g.getName(),g.getPrice(),g.getSellerId(), g.getImageUrl()));
        }
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(productList));
        out.flush();
    }
    public void orderGoods(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json;charset=UTF-8");
        List<goodsDao> goodsList = userConnector.priceByorder();
        List<goodsDao> productList = new ArrayList<>();
        for (goodsDao g : goodsList) {
            productList.add(new goodsDao(g.getId(), g.getName(),g.getPrice(),g.getSellerId(), g.getImageUrl()));
        }
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(productList));
        out.flush();
    }
    public void orderGoodsDesc(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json;charset=UTF-8");
        List<goodsDao> goodsList = userConnector.priceByOrderDesc();
        List<goodsDao> productList = new ArrayList<>();
        for (goodsDao g : goodsList) {
            productList.add(new goodsDao(g.getId(), g.getName(),g.getPrice(),g.getSellerId(), g.getImageUrl()));
        }
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(productList));
        out.flush();
    }
    private void buy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            response.sendError(401);
            return;
        }
        int username = Integer.parseInt(session.getAttribute("username").toString());
        int sellname = Integer.parseInt(request.getParameter("sellerId"));
        int gid = Integer.parseInt(request.getParameter("gid"));
        float price = Float.parseFloat(request.getParameter("price"));
        String goodsname = request.getParameter("goodsname");
        try {
            userConnector.buygoods(username, sellname, gid, price, goodsname);
            userConnector.consume(username, price);
            userConnector.sellgoods(sellname, price);
            userConnector.deleteGoods(gid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
