import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Saf Java (Socket Programlama) ile Çok İş Parçacıklı (Multi-Threaded) Web Sunucusu.
 * 
 * Sınıf Adı: JavaWebServer
 * Port: 1989
 * Herhangi bir 3. parti kütüphane kullanılmamıştır.
 */
public class JavaWebServer {

    public static final int PORT = 1989;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        // İstemci isteklerini eşzamanlı işlemek için sabit boyutlu Thread Havuzu (ExecutorService)
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        System.out.println("=================================================");
        System.out.println("Java Web Sunucusu (JavaWebServer) Başlatılıyor...");
        System.out.println("Port: " + PORT);
        System.out.println("Havuz Boyutu (Thread Pool): " + THREAD_POOL_SIZE);
        System.out.println("Erişim Adresi: http://localhost:" + PORT);
        System.out.println("Sunucuyu durdurmak için: CTRL + C");
        System.out.println("=================================================");

        try (ServerSocket serverSocket = new ServerServerSocketWrapper(PORT)) {
            // JVM kapatıldığında kaynakları temizlemek için shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nSunucu kapatılıyor...");
                threadPool.shutdown();
                try {
                    if (!serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Soket kapatılırken hata: " + e.getMessage());
                }
                System.out.println("Sunucu başarıyla durduruldu.");
            }));

            // Sürekli olarak gelen bağlantıları kabul eden döngü
            while (!serverSocket.isClosed()) {
                try {
                    // İstemci (tarayıcı) bağlantısını bekle ve kabul et (Bloklayıcı çağrı)
                    Socket clientSocket = serverSocket.accept();
                    
                    // Bağlantıyı thread havuzundaki boşta olan bir iş parçacığına devret
                    threadPool.submit(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    if (serverSocket.isClosed()) {
                        break;
                    }
                    System.err.println("Bağlantı kabul edilirken hata oluştu: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Sunucu " + PORT + " portunda başlatılamadı: " + e.getMessage());
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    // ServerSocket sarmalayıcısı (try-with-resources için)
    private static class ServerServerSocketWrapper extends ServerSocket {
        public ServerServerSocketWrapper(int port) throws IOException {
            super(port);
        }
    }

    /**
     * Her istemci bağlantısını bağımsız bir iş parçacığında (Thread) işleyen sınıf.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String clientInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            String threadName = Thread.currentThread().getName();

            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                OutputStream out = socket.getOutputStream()
            ) {
                // Tarayıcıdan gelen ilk satırı (Request Line) oku: örn. "GET / HTTP/1.1"
                String requestLine = reader.readLine();
                if (requestLine == null || requestLine.isEmpty()) {
                    return;
                }

                System.out.printf("[%s] [%s] İstek alındı: %s%n", threadName, clientInfo, requestLine);

                // Kalan HTTP başlıklarını (headers) oku ve boş satıra kadar ilerle
                String headerLine;
                while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                    // İsteğe bağlı olarak başlıklar burada incelenebilir
                }

                // Gönderilecek HTML içeriğini oluştur
                String htmlBody = generateHtmlContent();
                byte[] bodyBytes = htmlBody.getBytes(StandardCharsets.UTF_8);

                // HTTP/1.1 Yanıt Başlıklarını (Headers) hazırla
                StringBuilder responseHeaders = new StringBuilder();
                responseHeaders.append("HTTP/1.1 200 OK\r\n");
                responseHeaders.append("Content-Type: text/html; charset=UTF-8\r\n");
                responseHeaders.append("Content-Length: ").append(bodyBytes.length).append("\r\n");
                responseHeaders.append("Connection: close\r\n");
                responseHeaders.append("Server: JavaWebServer/1.0\r\n");
                responseHeaders.append("\r\n"); // Header ile Body arasındaki zorunlu boş satır

                // Yanıtı tarayıcıya ilet
                out.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));
                out.write(bodyBytes);
                out.flush();

                System.out.printf("[%s] [%s] 200 OK yanıtı başarıyla gönderildi (%d bayt).%n", 
                        threadName, clientInfo, bodyBytes.length);

            } catch (IOException e) {
                System.err.printf("[%s] İstemci iletişiminde hata: %s%n", threadName, e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.printf("[%s] Soket kapatma hatası: %s%n", threadName, e.getMessage());
                }
            }
        }

        /**
         * Tarayıcıda gösterilecek HTML sayfasını üretir.
         */
        private String generateHtmlContent() {
            return """
                <!DOCTYPE html>
                <html lang="tr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Öğrenci Tanıtım Kartı - Web Sunucu</title>
                    <style>
                        :root {
                            --primary-color: #1e3a8a;
                            --secondary-color: #3b82f6;
                            --accent-color: #10b981;
                            --bg-color: #f3f4f6;
                            --card-bg: #ffffff;
                            --text-color: #1f2937;
                            --muted-text: #6b7280;
                            --border-color: #e5e7eb;
                        }

                        * {
                            box-sizing: border-box;
                            margin: 0;
                            padding: 0;
                        }

                        body {
                            font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, Roboto, Helvetica, Arial, sans-serif;
                            background: linear-gradient(135deg, #e0e7ff 0%, #f3f4f6 100%);
                            color: var(--text-color);
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                            padding: 20px;
                        }

                        .profile-card {
                            background-color: var(--card-bg);
                            border-radius: 16px;
                            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.05);
                            width: 100%;
                            max-width: 580px;
                            overflow: hidden;
                            border: 1px solid var(--border-color);
                            transition: transform 0.2s ease, box-shadow 0.2s ease;
                        }

                        .profile-card:hover {
                            transform: translateY(-4px);
                            box-shadow: 0 20px 30px -10px rgba(0, 0, 0, 0.15);
                        }

                        .card-header {
                            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                            color: #ffffff;
                            padding: 32px 28px;
                            text-align: center;
                            position: relative;
                        }

                        .badge {
                            display: inline-block;
                            background: rgba(255, 255, 255, 0.2);
                            backdrop-filter: blur(4px);
                            padding: 4px 12px;
                            border-radius: 9999px;
                            font-size: 0.85rem;
                            font-weight: 600;
                            letter-spacing: 0.5px;
                            margin-bottom: 12px;
                            text-transform: uppercase;
                        }

                        h1 {
                            font-size: 2rem;
                            font-weight: 700;
                            letter-spacing: -0.5px;
                            margin-bottom: 6px;
                            color: #ffffff;
                        }

                        h2 {
                            font-size: 1.15rem;
                            font-weight: 500;
                            color: #dbeafe;
                            letter-spacing: 0.5px;
                        }

                        .card-body {
                            padding: 28px;
                        }

                        .bio-section h3 {
                            font-size: 1.05rem;
                            color: var(--primary-color);
                            text-transform: uppercase;
                            letter-spacing: 1px;
                            margin-bottom: 12px;
                            display: flex;
                            align-items: center;
                            gap: 8px;
                        }

                        .bio-text {
                            font-size: 1rem;
                            line-height: 1.7;
                            color: var(--text-color);
                            margin-bottom: 20px;
                            text-align: justify;
                        }

                        .skills-container {
                            display: flex;
                            flex-wrap: wrap;
                            gap: 8px;
                            margin-top: 15px;
                            padding-top: 15px;
                            border-top: 1px dashed var(--border-color);
                        }

                        .skill-chip {
                            background-color: #eff6ff;
                            color: var(--secondary-color);
                            border: 1px solid #bfdbfe;
                            padding: 6px 14px;
                            border-radius: 8px;
                            font-size: 0.85rem;
                            font-weight: 600;
                        }

                        .card-footer {
                            background-color: #f9fafb;
                            border-top: 1px solid var(--border-color);
                            padding: 14px 28px;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            font-size: 0.85rem;
                            color: var(--muted-text);
                        }

                        .status-dot {
                            display: inline-block;
                            width: 8px;
                            height: 8px;
                            border-radius: 50%;
                            background-color: var(--accent-color);
                            margin-right: 6px;
                        }
                    </style>
                </head>
                <body>
                    <div class="profile-card">
                        <header class="card-header">
                            <span class="badge">Öğrenci Profili</span>
                            <h1>Berat abdullah mumcu</h1>
                            <h2>Öğrenci No: 1250505015</h2>
                        </header>
                        
                        <main class="card-body">
                            <section class="bio-section">
                                <h3>Hakkımda / Biyografi</h3>
                                <p class="bio-text">
                                    Bilgisayar Mühendisliği öğrencisiyim. Ağ programlama, dağıtık sistemler, 
                                    backend mimarileri ve modern yazılım geliştirme teknolojileri ile yakından 
                                    ilgileniyorum. Java platformu üzerinde soket programlama, eşzamanlılık (concurrency) 
                                    ve düşük seviyeli ağ protokolleri üzerine pratik çalışmalar yürütüyorum.
                                </p>
                            </section>

                            <div class="skills-container">
                                <span class="skill-chip">Java Socket API</span>
                                <span class="skill-chip">HTTP/1.1</span>
                                <span class="skill-chip">Multi-Threading</span>
                                <span class="skill-chip">HTML5 & CSS3</span>
                            </div>
                        </main>

                        <footer class="card-footer">
                            <span><span class="status-dot"></span>Port: 1989 (Aktif)</span>
                            <span>Java Standard Edition (No 3rd Party)</span>
                        </footer>
                    </div>
                </body>
                </html>
                """;
        }
    }
}

