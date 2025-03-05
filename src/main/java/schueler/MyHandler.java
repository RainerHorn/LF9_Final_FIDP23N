package schueler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

import com.sun.net.httpserver.*;

public class MyHandler implements HttpHandler {

    private Entity entity;
    
    public MyHandler(Entity e) {
        this.entity=e;
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
        //exchange.getResponseHeaders().add("Content-Type", "application/json");
        //String response = "{\"version\":1.0}";
        //exchange.sendResponseHeaders(200, response.getBytes().length);
        //OutputStream os = exchange.getResponseBody();
        //os.write(response.getBytes());
        //os.close();
        if (exchange.getRequestMethod() == "GET") {
            handleGet(exchange);
        } else if (exchange.getRequestMethod() == "POST") {
            handlePost(exchange);(exchange);
        } else if (exchange.getRequestMethod() == "PUT") {
            handlePut(exchange);
        } else if (exchange.getRequestMethod() == "DELETE") {
            handleDelete(exchange);
        }
    }

    public void handleGet(HttpExchange exchange) {
        String[] uriParts = exchange.getRequestURI().toString().split("/");
        String id = uriParts[2];
    }

    public void handlePost(HttpExchange exchange) {

    }

    public void handlePut(HttpExchange exchange) {
        String[] uriParts = exchange.getRequestURI().toString().split("/");
        String id = uriParts[2];
    }

    public void handleDelete(HttpExchange exchange) {
        String[] uriParts = exchange.getRequestURI().toString().split("/");
        int deleteId = Integer.parseInt(uriParts[2]);
        this.entity.id=deleteId;

        String deleteStatement = this.entity.getDeleteStatement();

        try {
            Connection c=this.getDBConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(deleteStatement);
            exchange.sendResponseHeaders(200,0);
            System.out.println(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Connection getDBConnection() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            c = DriverManager.getConnection("jdbc:sqlite:todo.db", config.toProperties());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

}
