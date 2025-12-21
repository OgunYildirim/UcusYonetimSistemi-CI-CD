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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumUserFlowsTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Sistem özelliklerinden değerleri al, Docker network için default'lar
    private static final String SELENIUM_SCENARIO = System.getProperty("selenium.scenario", "1");
    private static final String FRONTEND_BASE = System.getProperty("frontend.base", "http://ucus-yonetim-frontend");
    private static final String BACKEND_BASE = System.getProperty("backend.base", "http://ucus-yonetim-backend:8080");

    @BeforeEach
    void setUp() {
        // Jenkins/Linux ortamı için otomatik kurulum
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Alpine Linux Chromium browser kullanımı
        options.setBinary("/usr/bin/chromium-browser");

        // Docker container için optimal ayarlar
        options.addArguments("--headless=new"); // Yeni headless modu
        options.addArguments("--no-sandbox"); // Docker güvenlik
        options.addArguments("--disable-dev-shm-usage"); // Bellek yönetimi
        options.addArguments("--disable-gpu"); // GPU devre dışı
        options.addArguments("--window-size=1920,1080"); // Pencere boyutu
        options.addArguments("--remote-allow-origins=*"); // CORS ayarı
        options.addArguments("--disable-web-security"); // Test için güvenlik devre dışı
        options.addArguments("--ignore-certificate-errors"); // SSL sertifika hataları
        options.addArguments("--disable-extensions"); // Eklentiler kapalı
        options.addArguments("--disable-background-timer-throttling"); // Timer kısıtlamaları
        options.addArguments("--disable-renderer-backgrounding"); // Renderer optimizasyonları
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--single-process"); // Alpine Docker için
        options.addArguments("--disable-features=VizDisplayCompositor"); // Render iyileştirmesi

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Daha uzun timeout
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
            if (!(driver instanceof TakesScreenshot)) return;
            Path targetDir = Path.of("target", "screenshots");
            Files.createDirectories(targetDir);
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String fileName = name.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".png";
            Files.write(targetDir.resolve(fileName), screenshot);
        } catch (Exception ignored) {}
    }

    private void registerUser(String username, String email, String password) throws Exception {
        URL url = new URL(BACKEND_BASE + "/api/auth/register");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        // JSON formatını daha güvenli hale getirdik
        String json = "{\"username\":\"" + username + "\",\"email\":\"" + email +
                "\",\"password\":\"" + password + "\",\"firstName\":\"Test\",\"lastName\":\"User\"}";

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        int code = con.getResponseCode();
        if (code < 200 || code >= 300) throw new RuntimeException("API Kayıt Hatası! Kod: " + code);
    }

    private void setAdminLocalStorage() {
        // Admin yetkilerini simüle eden JS kodu
        String userJson = "{\"id\":1,\"username\":\"admin\",\"email\":\"admin@flightmanagement.com\",\"roles\":[\"ROLE_ADMIN\"]}";
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('user', JSON.stringify(" + userJson + ")); " +
                        "localStorage.setItem('token', 'mock-jwt-token-admin');"
        );
    }

    @Test
    @DisplayName("REQ-101 - Login Flow")
    void scenario1_loginFlows() throws Exception {
        Assumptions.assumeTrue("1".equals(SELENIUM_SCENARIO));

        String uname = "user_" + System.currentTimeMillis();
        registerUser(uname, uname + "@test.com", "password123");

        driver.get(FRONTEND_BASE + "/login");

        // Inputların hazır olmasını bekle
        WebElement uInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("username")));
        uInput.sendKeys(uname);
        driver.findElement(By.name("password")).sendKeys("password123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Login sonrası yönlendirmeyi bekle
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/flights"),
                ExpectedConditions.jsReturnsValue("return localStorage.getItem('token')")
        ));

        assertNotNull(((JavascriptExecutor) driver).executeScript("return localStorage.getItem('token')"));
    }

    @Test
    @DisplayName("REQ-102 - Admin Add Flight")
    void scenario2_adminAddFlight() {
        Assumptions.assumeTrue("2".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/");
        setAdminLocalStorage();
        driver.get(FRONTEND_BASE + "/admin");

        // Butonun yüklenmesini bekle
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Yeni Uçuş Ekle')]")));
        addBtn.click();

        // Formu doldur
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("flightNumber"))).sendKeys("E2E-" + System.currentTimeMillis());

        // Select objeleri
        new Select(driver.findElement(By.name("departureAirportId"))).selectByIndex(1);
        new Select(driver.findElement(By.name("arrivalAirportId"))).selectByIndex(2);
        new Select(driver.findElement(By.name("aircraftId"))).selectByIndex(1);

        // Tarihleri doldur (HTML5 datetime-local formatı önemli)
        String futureDate = LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        driver.findElement(By.name("departureTime")).sendKeys(futureDate);
        driver.findElement(By.name("arrivalTime")).sendKeys(futureDate);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Alert kontrolü
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(alert.getText().toLowerCase().contains("başarı") || alert.getText().toLowerCase().contains("success"));
        alert.accept();
    }
}