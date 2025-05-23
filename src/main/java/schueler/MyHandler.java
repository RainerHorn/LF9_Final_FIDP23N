package schueler;   

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sqlite.SQLiteConfig;

import com.sun.net.httpserver.*;

@SuppressWarnings("restriction")
public class MyHandler implements HttpHandler {

    private Entity entity;
    private static Connection c = null;
    
    public MyHandler(Entity e) {
        this.entity = e;
    }

    public static void main(String[] args) {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/todo", new MyHandler(new Task()));
            server.createContext("/priority", new MyHandler(new Priority()));
            server.createContext("/project", new MyHandler(new Project()));
            server.setExecutor(null);
            System.out.println("Starting server...");
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("I received something!");
        String requestMethod = exchange.getRequestMethod();
        if ("GET".equals(requestMethod)) {
            handleGet(exchange);
        } else if ("POST".equals(requestMethod)) {
            handlePost(exchange);
        } else if ("PUT".equals(requestMethod)) {
            handlePut(exchange);
        } else if ("DELETE".equals(requestMethod)) {
            handleDelete(exchange);
        } else {
            String response = "Only GET, POST, PUT and DELETE are allowed!";
            exchange.sendResponseHeaders(405, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public void handleGet(HttpExchange exchange) {
        String[] uriParts = exchange.getRequestURI().toString().split("/");
        String readStatement = "";

        if (uriParts.length == 2) {
            readStatement = this.entity.getReadAllStatement();
        } else if (uriParts.length == 3) {
            int getId = Integer.parseInt(uriParts[2]);
            this.entity.id = getId;
            readStatement = this.entity.getReadStatement();
        }

        try {
            Connection c = this.getDBConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(readStatement);
            this.entity.setEntity(rs); 
            if ((this.entity.toJSON().length() == 2  || "{\"value\":0}".equals(this.entity.toJSON()))) {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseHeaders().add("X-Clacks-Overhead", "GNU Terry Pratchett");
                exchange.sendResponseHeaders(404, 0);
                OutputStream os = exchange.getResponseBody();
                os.close();
            } else {
                JSONArray jsonArray = new JSONArray();
                while (rs.next()) {
                    JSONObject obj = new JSONObject();
                    int total_rows = rs.getMetaData().getColumnCount();
                    for (int i = 0; i < total_rows; i++) {
                        obj.put(rs.getMetaData().getColumnLabel(i + 1)
                                .toLowerCase(), rs.getObject(i + 1));
                    }
                    jsonArray.put(obj);
                }
                System.out.println(jsonArray);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseHeaders().add("X-Clacks-Overhead", "GNU Terry Pratchett");
                exchange.sendResponseHeaders(200, jsonArray.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonArray.toString().getBytes());
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePost(HttpExchange exchange) {
        String[] uriParts = exchange.getRequestURI().toString().split("/");
        InputStream body = exchange.getRequestBody();
        Scanner s = new Scanner(body).useDelimiter("\\A");
        String bodyString = s.hasNext() ? s.next() : "";
        s.close();
        this.entity.parseJSON(bodyString);
        String createStatement = this.entity.getCreateStatement();

        try {
            Connection c=this.getDBConnection();
            Statement st = c.createStatement();
            st.executeUpdate(createStatement);
            String response = this.entity.toJSON();
            exchange.getResponseHeaders().add("X-Clacks-Overhead", "GNU Terry Pratchett");
            exchange.sendResponseHeaders(201, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            System.out.println("Creation of "+uriParts[1]+" successful !");
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePut(HttpExchange exchange) {
        String[] uriParts = exchange.getRequestURI().toString().split("/");
        int updateId = Integer.parseInt(uriParts[2]);
        this.entity.id = updateId;
        String readStatement = this.entity.getReadStatement();

        InputStream body = exchange.getRequestBody();
        Scanner s = new Scanner(body).useDelimiter("\\A");
        String bodyString = s.hasNext() ? s.next() : "";
        s.close();
        System.out.println(bodyString);
        try {
            Connection c=this.getDBConnection();
            Statement st2 = c.createStatement();
            ResultSet rs = st2.executeQuery(readStatement);
            this.entity.setEntity(rs); 
            System.out.println(this.entity.toJSON());
            if ((this.entity.toJSON().length() == 2 || "{\"value\":0}".equals(this.entity.toJSON()))) {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseHeaders().add("X-Clacks-Overhead", "GNU Terry Pratchett");
                exchange.sendResponseHeaders(404, 0);
                OutputStream os = exchange.getResponseBody();
                os.close();
            } else {
                this.entity.parseJSON(bodyString);
                String updateStatement = this.entity.getUpdateStatement();
                System.out.println(updateStatement);
                Statement st = c.createStatement();
                st.executeUpdate(updateStatement);
                String response = this.entity.toJSON();
                exchange.getResponseHeaders().add("X-Clacks-Overhead", "GNU Terry Pratchett");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                System.out.println("Update of "+uriParts[1]+" successful!");
                st.close();
            }
            rs.close();
            st2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleDelete(HttpExchange exchange) {
        String[] uriParts = exchange.getRequestURI().toString().split("/");
        int deleteId = Integer.parseInt(uriParts[2]);
        this.entity.id = deleteId;
        String readStatement = this.entity.getReadStatement();

        String deleteStatement = this.entity.getDeleteStatement();

        try {
            Connection c=this.getDBConnection();
            Statement st2 = c.createStatement();
            ResultSet rs = st2.executeQuery(readStatement);
            this.entity.setEntity(rs); 
            System.out.println(this.entity.toJSON());
            if ((this.entity.toJSON().length() == 2 || "{\"value\":0}".equals(this.entity.toJSON()))) {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseHeaders().add("X-Clacks-Overhead", "GNU Terry Pratchett");
                exchange.sendResponseHeaders(404, 0);
                OutputStream os = exchange.getResponseBody();
                os.close();
            } else {
                Statement st = c.createStatement();
                st.executeUpdate(deleteStatement);
                String response = "Deletion of "+uriParts[1]+" successful!";
                exchange.getResponseHeaders().add("X-Clacks-Overhead", "GNU Terry Pratchett");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                System.out.println("Deletion of "+uriParts[1]+" successful!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Connection getDBConnection() {
        try {
            if (c==null) {
                Class.forName("org.sqlite.JDBC");
                SQLiteConfig config = new SQLiteConfig();
                config.enforceForeignKeys(true);
                c = DriverManager.getConnection("jdbc:sqlite:todo.db", config.toProperties());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

}
