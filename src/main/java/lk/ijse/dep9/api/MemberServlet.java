package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.Json;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.db.ConnectionPool;
import lk.ijse.dep9.dto.MemberDTO;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "MemberServlet", value = "/members/*", loadOnStartup = 0)
public class MemberServlet extends HttpServlet2 {

    /* Access for JNDI (GlassFish) - new */
//    @Resource(lookup = "jdbc/lms")

    /* Access for JNDI (Tomcat) - new */
    @Resource(lookup = "java:/comp/env/jdbc/lms")

    /* Instance variable */
    private DataSource pool;

    /* Access for JNDI (GlassFish) - Legacy */
//    @Override
//    public void init() throws ServletException {
//        try {
//            /* Entry point of the JNDI. */
//            InitialContext ctx = new InitialContext();
//
//            /* Lookup the JNDI, Must read the documentation when lookup. */
//            /* Store it to the instance variable. */
//            pool = (DataSource) ctx.lookup("jdbc/lms");
//            System.out.println(pool);
//            System.out.println("look hiran");
//
//        } catch (NamingException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.getWriter().println("MemberServlet: doGet()");

        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            /* members, /members/ */
            /* There are query parameters (q, size, page) */

            String query = request.getParameter("q");
            String size = request.getParameter("size");
            String page = request.getParameter("page");

            if (query != null && size != null && page != null) {
//                response.getWriter().println("<h1>Search members by page</h1>");

                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid page or size");
                }else {
                    response.getWriter().println("<h1>Search paginated all members</h1>");
                    searchPaginatedMembers(query, Integer.parseInt(size), Integer.parseInt(page), response);
                }
            } else if (query != null) {
                response.getWriter().println("<h1>Search members</h1>");
                searchMembers(query, response);
            } else if (size != null && page != null) {
                response.getWriter().println("<h1>Load all members by page</h1>");
                loadPaginatedAllMembers(Integer.parseInt(size), Integer.parseInt(page), response);
            } else {
                response.getWriter().println("<h1>Load all members</h1>");
                loadAllMembers(response);
            }
        }else {
            Matcher matcher = Pattern.compile("^([A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12})/?$")
                    .matcher(request.getPathInfo());

            if (matcher.matches()) {
                 response.getWriter().printf("<h1>Get member details of %s</h1>",matcher.group(1));
//                 System.out.println(matcher.group(0));
//                 System.out.println(matcher.group(1));
//                 System.out.println(matcher.group(2));

                getMemberDetails(matcher.group(1), response);
            }else {
                /* HttpServletResponse 501 error, We can send a message with error code as well */
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Expected valid UUID");
            }

        }
    }

    private void loadAllMembers(HttpServletResponse response) throws IOException {
//        System.out.println("WS: Load all members");
//        response.getWriter().println("WS: Load all members");
        try{
//            ConnectionPool pool = (ConnectionPool) getServletContext().getAttribute("pool");

            /* Connection is not taken from the connection pool anymore  */
//            BasicDataSource pool = (BasicDataSource) getServletContext().getAttribute("pool");
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dep9_lms",
//                    "root", "mysql");

            Connection connection = pool.getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM member");

            ArrayList<Object> members = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");

                MemberDTO dto = new MemberDTO(id, name, address, contact);
                members.add(dto);
            }
            connection.close();

            Jsonb jsonb = JsonbBuilder.create();
            response.setContentType("application/json");
            jsonb.toJson(members, response.getWriter());

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load member");
            throw new RuntimeException(e);
        }
    }

    private void searchMembers(String query, HttpServletResponse response) throws IOException {
        response.getWriter().println("WS: Search members");
        System.out.printf("WS: Search members");
        System.out.println();
    }

    private void loadPaginatedAllMembers(int size, int page, HttpServletResponse response) throws IOException {
        response.getWriter().println("WS: Load paginated all members");
        System.out.printf("WS: Load paginated all members, size: %d, page: %d", size, page);
        System.out.println();
    }

    private void searchPaginatedMembers(String query, int size, int page, HttpServletResponse response) throws IOException {
        response.getWriter().println("WS: Load paginated all members");
        System.out.printf("WS: Search paginated members, size: %d, page: %d", size, page);
        System.out.println();
    }

    private void getMemberDetails(String memberId, HttpServletResponse response) throws IOException {
        response.getWriter().println("WS: Get member details");
        System.out.printf("WS: Get member details, member Id: %d", memberId);
        System.out.println();
    }

    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("MemberServlet: doPost()");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("MemberServlet: doDelete()");
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("MemberServlet: doPatch()");
    }
}
