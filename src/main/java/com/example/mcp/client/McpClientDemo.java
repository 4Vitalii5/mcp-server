package com.example.mcp.client;

import com.example.mcp.protocol.McpMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@Order(2)
@Profile("!test")
public class McpClientDemo implements CommandLineRunner {

    private final WebClient webClient = WebClient.create("http://localhost:8080");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicLong requestIdCounter = new AtomicLong(1);
    private final Sinks.Many<String> commandsSink = Sinks.many().unicast().onBackpressureBuffer();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== MCP Streamable-HTTP Client Demo ===");

        connectAndProcess();

        Thread.sleep(1500);

        runInteractiveDemo();
    }

    private void connectAndProcess() {
        Flux<String> pingCommands = Flux.interval(Duration.ofSeconds(30))
                .map(i -> {
                    try {
                        McpMessage pingMessage = new McpMessage();
                        pingMessage.setId("ping-" + i);
                        pingMessage.setMethod("ping");
                        return objectMapper.writeValueAsString(pingMessage);
                    } catch (Exception e) {
                        return "";
                    }
                });

        var requestBody = Flux.merge(commandsSink.asFlux(), pingCommands)
                .map(command -> {
                    if (!command.contains("\"method\":\"ping\"")) {
                        log.debug("--> Queuing command: {}", command);
                    }
                    return new DefaultDataBufferFactory().wrap((command + "\n").getBytes());
                });

        webClient.post()
                .uri("/mcp/stream")
                .contentType(new org.springframework.http.MediaType("application", "x-ndjson"))
                .accept(new org.springframework.http.MediaType("application", "x-ndjson"))
                .body(requestBody, org.springframework.core.io.buffer.DataBuffer.class)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(responseLine -> log.debug("<-- Received raw response: {}", responseLine))
                .map(this::parseResponse)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(this::handleServerMessage, this::handleError, this::handleStreamCompletion);
    }

    private McpMessage parseResponse(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, McpMessage.class);
        } catch (Exception e) {
            log.error("Failed to parse server response: {}", json, e);
            return null;
        }
    }

    private void handleServerMessage(McpMessage message) {
        if (message == null) return;

        if ("ready".equals(message.getType()) || "pong".equals(message.getResult())) {
            return;
        }

        System.out.println("\n<-- MESSAGE FROM SERVER RECEIVED:");
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message.getResult()));
        } catch (Exception e) {
            System.out.println("Could not format response: " + message.getResult());
        }

        printMenu();
    }

    private void handleError(Throwable error) {
        System.err.println("\n<-- STREAM ERROR: " + error.getMessage());
    }

    private void handleStreamCompletion() {
        System.out.println("\n<-- Stream completed.");
    }

    private void runInteractiveDemo() {
        System.out.println("\n--> Sending 'tools/list' command to get started...");
        sendCommand("tools/list", Collections.emptyMap());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Choose an option: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> createAuthorDemo(scanner);
                    case 2 -> listAuthorsDemo();
                    case 3 -> getAuthorDemo(scanner);
                    case 4 -> updateAuthorDemo(scanner);
                    case 5 -> deleteAuthorDemo(scanner);
                    case 6 -> createBookDemo(scanner);
                    case 7 -> listBooksDemo();
                    case 8 -> getBookDemo(scanner);
                    case 9 -> updateBookDemo(scanner);
                    case 10 -> deleteBookDemo(scanner);
                    case 11 -> addAuthorToBookDemo(scanner);
                    case 12 -> removeAuthorFromBookDemo(scanner);
                    case 0 -> {
                        System.out.println("Goodbye!");
                        System.exit(0);
                        return;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void sendCommand(String method, Object params) {
        try {
            McpMessage message = new McpMessage();
            message.setId(String.valueOf(requestIdCounter.getAndIncrement()));
            message.setMethod(method);
            message.setParams(params);
            String jsonCommand = objectMapper.writeValueAsString(message);
            commandsSink.emitNext(jsonCommand, Sinks.EmitFailureHandler.FAIL_FAST);
        } catch (Exception e) {
            log.error("Failed to send command", e);
        }
    }

    private void callTool(String toolName, Map<String, Object> arguments) {
        sendCommand("tools/call", Map.of("name", toolName, "arguments", arguments));
    }

    private void printMenu() {
        System.out.println("\n=== MCP Client Interactive Demo (Stream Mode) ===");
        System.out.println("1. Create Author");
        System.out.println("2. List Authors");
        System.out.println("3. Get Author by ID");
        System.out.println("4. Update Author");
        System.out.println("5. Delete Author");
        System.out.println("6. Create Book");
        System.out.println("7. List Books");
        System.out.println("8. Get Book by ID");
        System.out.println("9. Update Book");
        System.out.println("10. Delete Book");
        System.out.println("11. Add Author to Book");
        System.out.println("12. Remove Author from Book");
        System.out.println("0. Exit");
    }

    private void createAuthorDemo(Scanner scanner) {
        System.out.print("Enter author name: ");
        String name = scanner.nextLine();
        System.out.print("Enter author biography: ");
        String bio = scanner.nextLine();
        System.out.println("--> Sending 'create_author' command...");
        callTool("create_author", Map.of("name", name, "biography", bio));
    }

    private void listAuthorsDemo() {
        System.out.println("--> Sending 'get_authors' command...");
        callTool("get_authors", Collections.emptyMap());
    }

    private void getAuthorDemo(Scanner scanner) {
        System.out.print("Enter author ID: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.println("--> Sending 'get_author' command...");
        callTool("get_author", Map.of("id", id));
    }

    private void updateAuthorDemo(Scanner scanner) {
        System.out.print("Enter author ID to update: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.println("--> Sending 'update_author' command...");
        callTool("update_author", Map.of("id", id, "name", name));
    }

    private void deleteAuthorDemo(Scanner scanner) {
        System.out.print("Enter author ID to delete: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.println("--> Sending 'delete_author' command...");
        callTool("delete_author", Map.of("id", id));
    }

    private void createBookDemo(Scanner scanner) {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter publication year: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("--> Sending 'create_book' command...");
        callTool("create_book", Map.of("title", title, "publicationYear", year));
    }

    private void listBooksDemo() {
        System.out.println("--> Sending 'get_books' command...");
        callTool("get_books", Collections.emptyMap());
    }

    private void getBookDemo(Scanner scanner) {
        System.out.print("Enter book ID: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.println("--> Sending 'get_book' command...");
        callTool("get_book", Map.of("id", id));
    }

    private void updateBookDemo(Scanner scanner) {
        System.out.print("Enter book ID to update: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.println("--> Sending 'update_book' command...");
        callTool("update_book", Map.of("id", id, "title", title));
    }

    private void deleteBookDemo(Scanner scanner) {
        System.out.print("Enter book ID to delete: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.println("--> Sending 'delete_book' command...");
        callTool("delete_book", Map.of("id", id));
    }

    private void addAuthorToBookDemo(Scanner scanner) {
        System.out.print("Enter book ID: ");
        long bookId = Long.parseLong(scanner.nextLine());
        System.out.print("Enter author ID to add: ");
        long authorId = Long.parseLong(scanner.nextLine());
        System.out.println("--> Sending 'add_author_to_book' command...");
        callTool("add_author_to_book", Map.of("bookId", bookId, "authorId", authorId));
    }

    private void removeAuthorFromBookDemo(Scanner scanner) {
        System.out.print("Enter book ID: ");
        long bookId = Long.parseLong(scanner.nextLine());
        System.out.print("Enter author ID to remove: ");
        long authorId = Long.parseLong(scanner.nextLine());
        System.out.println("--> Sending 'remove_author_from_book' command...");
        callTool("remove_author_from_book", Map.of("bookId", bookId, "authorId", authorId));
    }
}