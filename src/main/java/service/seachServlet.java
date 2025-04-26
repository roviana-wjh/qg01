package service;

import com.google.gson.Gson;
import model.SimpleConnectionPool;
import model.adminConnector;
import model.trade;
import model.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
@WebServlet("/search")
public class seachServlet extends baseServlet {
    private adminConnector adminConnector;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.adminConnector = new adminConnector(SimpleConnectionPool.getInstance());
        this.gson = new Gson();
    }
    private void serchuser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         PrintWriter out = response.getWriter();
            List<user> users = new ArrayList<user>();
            users = adminConnector.browseUser();
            out.println(gson.toJson(users));
            out.flush();
        }
    private void serchTrade(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        List<trade> trades = new ArrayList<trade>();
        trades=adminConnector.searchTransaction();
        out.println(gson.toJson(trades));
        out.flush();
    }
    private void statistic(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println(gson.toJson(adminConnector.statistic()));
        out.flush();
    }
    }

