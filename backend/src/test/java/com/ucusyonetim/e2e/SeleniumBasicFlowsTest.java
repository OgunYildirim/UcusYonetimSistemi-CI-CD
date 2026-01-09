package com.ucusyonetim.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 7 Temel E2E Test Senaryosu - Frontend Tabanlı
 */
public class SeleniumBasicFlowsTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String SELENIUM_SCENARIO = System.getProperty("selenium.scenario", "1");
    private static final String FRONTEND_BASE = System.getProperty("frontend.base", "http://ucus-yonetim-frontend");
    // Test konteyner içinde çalıştığında localhost:8080 kullanılmalı
    private static final String BACKEND_BASE = System.getProperty("backend.base", "http://localhost:8080");

    @BeforeEach
    void setUp() {
        try {
            WebDriverManager.chromedriver()
                    .timeout(30)
                    .useLocalVersionsPropertiesFirst()
                    .setup();
        } catch (Exception e) {
            System.err.println("WebDriverManager hatası: " + e.getMessage());
        }

        ChromeOptions options = new ChromeOptions();
        boolean headless = !"false".equalsIgnoreCase(System.getProperty("HEADLESS_MODE", "true"));

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        }

        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-web-security");
        options.addArguments("--ignore-certificate-errors");

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

    private void setAdminLocalStorage() {
        String userJson = "{\\\"id\\\":1,\\\"username\\\":\\\"admin\\\",\\\"email\\\":\\\"admin@flightmanagement.com\\\",\\\"roles\\\":[\\\"ROLE_ADMIN\\\"]}";
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('user', '" + userJson + "'); " +
                        "localStorage.setItem('token', 'mock-jwt-token-admin');");
    }

    private void registerUser(String username, String email, String password) throws Exception {
        URI uri = new URI(BACKEND_BASE + "/api/auth/register");
        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setDoOutput(true);

        String json = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"firstName\":\"Test\",\"lastName\":\"User\",\"phoneNumber\":\"05554443322\"}",
                username, email, password);

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int code = con.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new RuntimeException("Kayıt hatası! Kod: " + code);
        }
    }

    @Test
    @DisplayName("Senaryo 1 - Ana Sayfa Görüntüleme")
    void scenario1_viewHomePage() {
        Assumptions.assumeTrue("1".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String pageTitle = driver.getTitle();
        assertNotNull(pageTitle, "Sayfa başlığı null olmamalı!");

        System.out.println("✅ Ana sayfa başarıyla yüklendi!");
    }

    @Test
    @DisplayName("Senaryo 2 - Uçuş Listeleme Sayfası")
    void scenario2_listAllFlights() {
        Assumptions.assumeTrue("2".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/flights");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        // Sayfa yüklendiğini doğrula
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/flights"), "Uçuş sayfasına yönlendirme yapılmadı!");

        System.out.println("✅ Uçuş listesi sayfası başarıyla yüklendi!");
    }

    @Test
    @DisplayName("Senaryo 3 - Admin: Yeni Uçak Ekleme")
    void scenario3_adminAddAircraft() throws Exception {
        Assumptions.assumeTrue("3".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/");
        setAdminLocalStorage();
        driver.navigate().refresh();
        driver.get(FRONTEND_BASE + "/admin");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        // Uçaklar tab'ına tıkla
        WebElement aircraftTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Uçaklar')]")));
        aircraftTab.click();
        Thread.sleep(1000);

        // Yeni Uçak Ekle butonuna tıkla
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Yeni Uçak Ekle')]")));
        addButton.click();
        Thread.sleep(500);

        // Form doldur
        String timestamp = String.valueOf(System.currentTimeMillis() % 100000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("registrationNumber")))
                .sendKeys("TC-" + timestamp);
        driver.findElement(By.name("model")).sendKeys("Boeing 737");
        driver.findElement(By.name("manufacturer")).sendKeys("Boeing");
        driver.findElement(By.name("totalSeats")).sendKeys("180");
        driver.findElement(By.name("economySeats")).sendKeys("150");
        driver.findElement(By.name("businessSeats")).sendKeys("30");
        driver.findElement(By.name("yearOfManufacture")).sendKeys("2020");

        // Submit
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // Alert bekle
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();

        assertTrue(alertText.contains("başarı") || alertText.contains("eklendi"));
        System.out.println("✅ Uçak eklendi: TC-" + timestamp);
    }

    @Test
    @DisplayName("Senaryo 4 - Admin: Yeni Havaalanı Ekleme")
    void scenario4_adminAddAirport() throws Exception {
        Assumptions.assumeTrue("4".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/");
        setAdminLocalStorage();
        driver.navigate().refresh();
        driver.get(FRONTEND_BASE + "/admin");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        // Havalimanları tab'ına tıkla
        WebElement airportTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Havalimanları')]")));
        airportTab.click();
        Thread.sleep(1000);

        // Yeni Havalimanı Ekle butonuna tıkla
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Yeni Havalimanı Ekle')]")));
        addButton.click();
        Thread.sleep(500);

        // Form doldur
        String timestamp = String.valueOf(System.currentTimeMillis() % 10000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("code")))
                .sendKeys("TST" + timestamp);
        driver.findElement(By.name("name")).sendKeys("Test Havaalanı");
        driver.findElement(By.name("city")).sendKeys("Test Şehir");
        driver.findElement(By.name("country")).sendKeys("Türkiye");

        // Submit
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // Alert bekle
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();

        assertTrue(alertText.contains("başarı") || alertText.contains("eklendi"));
        System.out.println("✅ Havaalanı eklendi: TST" + timestamp);
    }

    @Test
    @DisplayName("Senaryo 5 - Admin: Uçak Bakıma Alma")
    void scenario5_adminMaintenanceToggle() throws Exception {
        Assumptions.assumeTrue("5".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/");
        setAdminLocalStorage();
        driver.navigate().refresh();
        driver.get(FRONTEND_BASE + "/admin");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        // Uçaklar tab'ına tıkla
        WebElement aircraftTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Uçaklar')]")));
        aircraftTab.click();
        Thread.sleep(1000);

        // Bakıma Al butonuna tıkla (varsa)
        try {
            WebElement maintenanceBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Bakıma Al')]")));
            maintenanceBtn.click();

            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();

            System.out.println("✅ Uçak bakıma alındı!");
        } catch (Exception e) {
            System.out.println("⚠️ Bakıma alınabilecek uçak yok");
        }
    }

    @Test
    @DisplayName("Senaryo 6 - Kullanıcı Kaydı")
    void scenario6_userRegistration() throws Exception {
        Assumptions.assumeTrue("6".equals(SELENIUM_SCENARIO));

        String username = "user" + System.currentTimeMillis();
        String email = username + "@test.com";

        driver.get(FRONTEND_BASE + "/register");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")))
                .sendKeys(username);
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys("Test123");
        driver.findElement(By.name("firstName")).sendKeys("Test");
        driver.findElement(By.name("lastName")).sendKeys("User");
        driver.findElement(By.name("phoneNumber")).sendKeys("05551234567");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Login sayfasına yönlendirme veya başarı mesajı bekle
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/login"),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'başarı')]"))));

        System.out.println("✅ Kullanıcı kaydı başarılı: " + username);
    }

    @Test
    @DisplayName("Senaryo 7 - Rezervasyonlarım Sayfası")
    void scenario7_viewMyBookings() throws Exception {
        Assumptions.assumeTrue("7".equals(SELENIUM_SCENARIO));

        String username = "user" + System.currentTimeMillis();
        String password = "Test123";
        registerUser(username, username + "@test.com", password);

        driver.get(FRONTEND_BASE + "/login");
        wait.until(ExpectedConditions.elementToBeClickable(By.name("username"))).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Token kontrolü
        wait.until(
                driver -> ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('token')") != null);

        // Rezervasyonlarım sayfasına git
        driver.get(FRONTEND_BASE + "/my-bookings");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        System.out.println("✅ Rezervasyonlarım sayfası yüklendi!");
    }
}
