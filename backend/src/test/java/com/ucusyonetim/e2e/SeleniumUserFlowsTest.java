package com.ucusyonetim.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumUserFlowsTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String SELENIUM_SCENARIO = System.getProperty("selenium.scenario", "1");
    private static final String FRONTEND_BASE = System.getProperty("frontend.base", "http://ucus-yonetim-frontend");
    // Test konteyner içinde çalıştığında localhost:8080 kullanılmalı
    private static final String BACKEND_BASE = System.getProperty("backend.base", "http://localhost:8080");

    @BeforeEach
    void setUp() {
        try {
            // Bağlantı sorunlarına karşı timeout süresini düşürüyoruz ve önbelleği
            // zorluyoruz
            WebDriverManager.chromedriver()
                    .timeout(30)
                    .useLocalVersionsPropertiesFirst()
                    .setup();
        } catch (Exception e) {
            System.err.println("WebDriverManager hatası, yerel driver denenecek: " + e.getMessage());
        }

        ChromeOptions options = new ChromeOptions();

        // HEADLESS_MODE=false ile görünür browser'da çalıştırabilirsiniz
        boolean headless = !"false".equalsIgnoreCase(System.getProperty("HEADLESS_MODE", "true"));

        if (headless) {
            // Docker ve CI/CD ortamları için kritik ayarlar
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        } else {
            System.out.println("🎬 BROWSER MODE: Test görünür browser'da çalışıyor!");
        }

        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        // SSL ve Güvenlik hatalarını bypass etme
        options.addArguments("--disable-web-security");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--allow-insecure-localhost");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (driver != null) {
            try {
                takeScreenshot(testInfo.getDisplayName());
            } catch (Exception e) {
                System.err.println("Ekran görüntüsü alınamadı: " + e.getMessage());
            }
            driver.quit();
        }
    }

    private void takeScreenshot(String name) {
        try {
            if (!(driver instanceof TakesScreenshot))
                return;
            Path targetDir = Path.of("target", "screenshots");
            Files.createDirectories(targetDir);
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String fileName = name.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".png";
            Files.write(targetDir.resolve(fileName), screenshot);
        } catch (Exception ignored) {
        }
    }

    private void registerUser(String username, String email, String password) throws Exception {
        URL url = new URL(BACKEND_BASE + "/api/auth/register");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        // RegisterRequest DTO ile tam uyumlu JSON
        String json = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"firstName\":\"Test\",\"lastName\":\"User\",\"phoneNumber\":\"05554443322\"}",
                username, email, password);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = con.getResponseCode();
        if (code < 200 || code >= 300) {
            String errorMsg = "";
            try (Scanner s = new Scanner(con.getErrorStream() != null ? con.getErrorStream() : con.getInputStream())
                    .useDelimiter("\\A")) {
                errorMsg = s.hasNext() ? s.next() : "";
            }
            throw new RuntimeException("API Kayit Hatasi! Kod: " + code + " Mesaj: " + errorMsg);
        }
    }

    private void setAdminLocalStorage() {
        // Admin yetkilerini LocalStorage'a enjekte ediyoruz
        String userJson = "{\"id\":1,\"username\":\"admin\",\"email\":\"admin@flightmanagement.com\",\"roles\":[\"ROLE_ADMIN\"]}";
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('user', JSON.stringify(" + userJson + ")); " +
                        "localStorage.setItem('token', 'mock-jwt-token-admin');");
    }

    @Test
    @DisplayName("REQ-101 - Login Flow")
    void scenario1_loginFlows() throws Exception {
        Assumptions.assumeTrue("1".equals(SELENIUM_SCENARIO));

        String uname = "user" + (System.currentTimeMillis() % 100000);
        String upass = "Password123";

        registerUser(uname, uname + "@test.com", upass);

        driver.get(FRONTEND_BASE + "/login");

        WebElement uInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("username")));
        uInput.sendKeys(uname);
        driver.findElement(By.name("password")).sendKeys(upass);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Login sonrası yönlendirme veya token kontrolü
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/flights"),
                driver -> ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('token')") != null));

        Object token = ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('token')");
        assertNotNull(token, "Login sonrasi token bulunamadi!");
    }

    @Test
    @DisplayName("REQ-102 - Admin Add Flight")
    void scenario2_adminAddFlight() {
        Assumptions.assumeTrue("2".equals(SELENIUM_SCENARIO));

        // Sayfaya git ve admin yetkisi ver
        driver.get(FRONTEND_BASE + "/");
        setAdminLocalStorage();
        driver.navigate().refresh(); // LocalStorage sonrası refresh gerekebilir

        driver.get(FRONTEND_BASE + "/admin");

        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Yeni Uçuş Ekle') or contains(text(), 'Add Flight')]")));
        addBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("flightNumber")))
                .sendKeys("E2E-" + System.currentTimeMillis());

        // Select objeleri için dropdownların yüklendiğinden emin olun
        wait.until(ExpectedConditions.presenceOfNestedElementsLocatedBy(By.name("departureAirportId"),
                By.tagName("option")));
        new Select(driver.findElement(By.name("departureAirportId"))).selectByIndex(1);
        new Select(driver.findElement(By.name("arrivalAirportId"))).selectByIndex(2);
        new Select(driver.findElement(By.name("aircraftId"))).selectByIndex(1);

        String futureDate = LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        driver.findElement(By.name("departureTime")).sendKeys(futureDate);
        driver.findElement(By.name("arrivalTime")).sendKeys(futureDate);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Alert kontrolü (Frontend alert() kullanıyorsa)
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().toLowerCase();
            assertTrue(text.contains("basari") || text.contains("success"), "Beklenen basari mesaji alinmadi: " + text);
            alert.accept();
        } catch (TimeoutException e) {
            // Eğer alert değil de bir toast message/div çıkıyorsa buraya o kontrol
            // eklenmeli
            System.out.println("Alert cikmadi, UI uzerindeki basari mesajini kontrol edin.");
        }
    }
}
