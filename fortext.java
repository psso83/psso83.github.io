import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;

public class fortext {

    private static final int PORT = 9091;

    // ✅ 프로젝트 폴더 기준 data/output.txt
    private static final Path SAVE_FILE = Paths.get("data/output.txt");

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/save", ex -> {

            // CORS (Live Server 5500 허용)
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            ex.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(405, -1);
                return;
            }

            String body = new String(
                ex.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            ).trim();

            if (!body.isEmpty()) {
                Files.createDirectories(SAVE_FILE.getParent());

                String line = "[" + LocalDateTime.now() + "] "
                            + body + System.lineSeparator();

                Files.write(
                    SAVE_FILE,
                    line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                );
            }

            ex.sendResponseHeaders(200, 0);
            ex.close();
        });

        server.start();
        System.out.println("Server started: http://127.0.0.1:" + PORT);
        System.out.println("Saving to: " + SAVE_FILE.toAbsolutePath());
    }
}
