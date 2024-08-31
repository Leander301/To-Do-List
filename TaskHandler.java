package src.com.todo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskHandler implements HttpHandler {
    private List<Task> tasks = new ArrayList<>();
    private final String filePath = "tasks.txt";

    public TaskHandler() {
        loadTasks();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equals(method)) {
            handleGetRequest(exchange);
        } else if ("POST".equals(method)) {
            handlePostRequest(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String response = generateHTML();
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String[] params = sb.toString().split("&");
        String description = params[0].split("=")[1];
        String id = UUID.randomUUID().toString();
        tasks.add(new Task(id, description));
        saveTasks();
        String response = "Task added";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            for (Task task : tasks) {
                writer.println(task.getId() + "," + task.getDescription());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        tasks.add(new Task(parts[0], parts[1]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateHTML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><title>To-Do List</title></head><body>");
        sb.append("<h1>To-Do List</h1>");
        sb.append("<ul>");
        for (Task task : tasks) {
            sb.append("<li>").append(task.getDescription()).append("</li>");
        }
        sb.append("</ul>");
        sb.append("<form id='taskForm' method='POST'>");
        sb.append("<input type='text' id='taskDescription' name='description' placeholder='Enter a task'>");
        sb.append("<button type='submit'>Add Task</button>");
        sb.append("</form>");
        sb.append("<script src='script.js'></script>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
