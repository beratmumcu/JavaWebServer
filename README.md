# JavaWebServer - Saf Java ile Çok İş Parçacıklı (Multi-Threaded) Web Sunucusu

Bu proje; hiçbir üçüncü parti kütüphane veya harici web çerçevesi (Spring, Tomcat, Jetty, Netty vb.) kullanılmadan, tamamen **Java Standart Kütüphanesi (Java SE)** ve **Düşük Seviyeli Soket Programlama (Socket Programming)** kullanılarak geliştirilmiş çok iş parçacıklı bir HTTP web sunucusudur.

---

## 🚀 Öne Çıkan Özellikler

- **Saf Java (No 3rd Party):** Harici bağımlılık veya framework yoktur. Yalnızca `java.net.*`, `java.io.*` ve `java.util.concurrent.*` paketleri kullanılmıştır.
- **Çok İş Parçacıklı Havuz (Thread Pool):** Eşzamanlı istemci isteklerini bloklanmadan karşılamak için 10 iş parçacıklı sabit havuz (`Executors.newFixedThreadPool(10)`) mimarisi.
- **HTTP/1.1 Protokol Uyumluluğu:** Standart HTTP istek satırlarını ayrıştırır (Request-Line), başlıkları (Headers) işler ve geçerli `HTTP/1.1 200 OK` yanıtı üretir.
- **UTF-8 Karakter Desteği:** Türkçe karakterlerin sorunsuz gösterimi ve bayt bazlı kesin `Content-Length` hesaplaması.
- **Modern Kullanıcı Arayüzü:** Dahili (internal) CSS3 ile tasarlanmış modern, responsive Öğrenci Tanıtım Kartı.
- **Güvenli Kapanış (Graceful Shutdown):** `Runtime Shutdown Hook` mekanizması ile `CTRL + C` sinyalinde portu ve iş parçacığı havuzunu temiz şekilde kapatır.

---

## 📁 Proje Dosya Yapısı

```
simple-web-server/
│
├── JavaWebServer.java      # Ana sunucu kodu, soket dinleyici, thread havuzu ve HTTP işleyici
├── README.md               # Proje tanıtımı ve hızlı başlangıç rehberi
└── MIMARI.md               # Detaylı teknik mimari, TCP/IP akış şemaları ve protokol analizi
```

---

## 🛠️ Hızlı Başlangıç

### Gereksinimler
- **Java JDK 17** veya üzeri (Örn: OpenJDK 25)

### 1. Proje Dizinine Geçiş
Terminal / PowerShell üzerinden proje klasörüne gidin:
```powershell
cd "c:\Users\Berat\.gemini\antigravity\scratch\simple-web-server"
```

### 2. Derleme (Compile)
Java kaynak kodunu UTF-8 kodlamasıyla derleyin:
```powershell
javac -encoding UTF-8 JavaWebServer.java
```
*Bu komut `JavaWebServer.class` ve ilgili iç sınıf bytecode dosyalarını üretir.*

### 3. Çalıştırma (Run)
Sunucuyu başlatın:
```powershell
java JavaWebServer
```

Terminalde başlatma çıktısını göreceksiniz:
```text
=================================================
Java Web Sunucusu (JavaWebServer) Başlatılıyor...
Port: 1989
Havuz Boyutu (Thread Pool): 10
Erişim Adresi: http://localhost:1989
Sunucuyu durdurmak için: CTRL + C
=================================================
```

### 4. Tarayıcıdan Erişim
Herhangi bir web tarayıcısını açarak adrese gidin:
👉 **[http://localhost:1989](http://localhost:1989)**

Sunucuyu durdurmak istediğinizde terminal ekranında `CTRL + C` tuşlarına basmanız yeterlidir.

---

## 📖 Detaylı Teknik Dokümantasyon

Sunucunun;
- TCP/IP ve soket el sıkışma (Handshake) yaşam döngüsü,
- Thread-per-request yerine neden Thread Pool seçildiği,
- HTTP başlık yapısı ve güvenlik/kaynak yönetimi analizleri

için **[MIMARI.md](MIMARI.md)** dokümanını inceleyebilirsiniz.

---

## 👤 Geliştirici Bilgileri

- **Adı Soyadı:** Berat Abdullah Mumcu
- **Öğrenci Numarası:** 1250505015
- **Bölüm:** Bilgisayar Mühendisliği

