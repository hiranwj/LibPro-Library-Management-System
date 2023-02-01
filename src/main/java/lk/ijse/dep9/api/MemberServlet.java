package lk.ijse.dep9.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.HttpServlet2;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "MemberServlet", value = "/members/*")
public class MemberServlet extends HttpServlet2 {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.getWriter().println("MemberServlet: doGet()");

        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            /* members, /members/ */
            /* There are query parameters (q, size, page) */
            String query = req.getParameter("q");
            String size = req.getParameter("size");
            String page = req.getParameter("page");

            if (query != null && size != null && page != null) {
                resp.getWriter().println("<h1>Search members by page</h1>");
            } else if (query != null) {
                resp.getWriter().println("<h1>Search members</h1>");
            } else if (size != null && page != null) {
                resp.getWriter().println("<h1>Load all members by page</h1>");
            } else {
                resp.getWriter().println("<h1>Load all members</h1>");
            }
        }else {
            Matcher matcher = Pattern.compile("^([A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12})/?$")
                    .matcher(req.getPathInfo());

            /* HttpServletResponse 501 error, We can send a message with error code as well */
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Expected valid UUID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("MemberServlet: doPost()");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("MemberServlet: doDelete()");
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("MemberServlet: doPatch()");
    }
}
