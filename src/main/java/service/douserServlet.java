package service;

import com.google.gson.Gson;
import model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
@WebServlet("/doUser")
public class douserServlet extends baseServlet{
    private adminConnector adminConnector;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.adminConnector = new adminConnector(SimpleConnectionPool.getInstance());
        this.gson = new Gson();
    }
    private void getUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json;charset=UTF-8");
        List<userdao> users=new ArrayList<>();
        users=adminConnector.GetAllUsers();
        out.println(gson.toJson(users));
        out.flush();
    }
    private void dealDenouces(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json;charset=UTF-8");
        List<denouce>denouces=new ArrayList<>();
        denouces=adminConnector.dealDenouce();
        out.println(gson.toJson(denouces));
        out.flush();
    }
    private void punishAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int username=Integer.parseInt(request.getParameter("username"));
        adminConnector.dealUser(username);
        adminConnector.deleteDenouce(username);
    }
    private void recoverAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int username=Integer.parseInt(request.getParameter("username"));

    }
    private void getPregoods(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<pregoods>pregoodsList=adminConnector.getPregoods();
        response.setContentType("application/json;charset=UTF-8");
        try(PrintWriter out=response.getWriter()){
            out.println(gson.toJson(pregoodsList));
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void doPregoods(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int gid=Integer.parseInt(request.getParameter("gid"));
        adminConnector.setPregoods(gid);
        adminConnector.deletePregoods(gid);
    }
    private void getPunishedUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<user> users=adminConnector.getPunishedUser();
        response.setContentType("application/json;charset=UTF-8");
        try(PrintWriter out=response.getWriter()){
            out.println(gson.toJson(users));
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void recoverUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int username=Integer.parseInt(request.getParameter("username"));
        adminConnector.recoverPunishedUser(username);
    }
}
