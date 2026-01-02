package com.ucusyonetim.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 7 Temel E2E Test Senaryosu - Stage 6'dan Tamamen Farklı
 * 
 * Stage 6: Login Flow, Admin Add Flight
 * Stage 7: Havaalanı Listeleme, Uçuş Listeleme, Uçuş Detay, Uçuş Arama,
 * Admin Uçak Ekleme, Admin Havaalanı Ekleme, Admin Bakım Kaydı
 */
public class SeleniumBasicFlowsTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String SELENIUM_SCENARIO = System.getProperty("selenium.scenario", "1");
    private static final String FRONTEND_BASE = System.getProperty("frontend.base", "http://ucus-yonetim-frontend");
    private static final String BACKEND_BASE = System.getProperty("backend.base", "http://ucus-yonetim-backend:8080");

    @BeforeEach
    void setUp() {
        try {
            WebDriverManager.chromedriver()
                    .timeout(30)
                    .useLocalVersionsPropertiesFirst()
                    .setup();
        } catch (Exception e) {
            System.err.println("WebDriverManager hatası, yerel driver denenecek: " + e.getMessage());
        }

        ChromeOptions options = new ChromeOptions();
        boolean headless = !"false".equalsIgnoreCase(System.getProperty("HEADLESS_MODE", "true"));

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        } else {
            System.out.println("🎬 BROWSER MODE: Test görünür browser'da çalışıyor!");
        }

        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
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

    private void setAdminLocalStorage() {
        // Admin yetkilerini LocalStorage'a enjekte ediyoruz
        String userJson = "{\"id\":1,\"username\":\"admin\",\"email\":\"admin@flightmanagement.com\",\"roles\":[\"ROLE_ADMIN\"]}";
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('user', JSON.stringify(" + userJson + ")); " +
                        "localStorage.setItem('token', 'mock-jwt-token-admin');");
    }

    @Test
    @DisplayName("Senaryo 1 - Havaalanı Listeleme")
    void scenario1_listAirports() {
        Assumptions.assumeTrue("1".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/airports");

        // Havaalanı listesinin yüklenmesini bekle
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(@class, 'airport') or contains(@id, 'airport-list')]")));

        // En az bir havaalanı olduğunu doğrula
        List<WebElement> airports = driver.findElements(
                By.xpath("//*[contains(@class, 'airport-item') or contains(@class, 'airport-card')]"));

        assertTrue(airports.size() > 0, "Havaalanı listesi boş olmamalı!");
        System.out.println("✅ Toplam " + airports.size() + " havaalanı listelendi!");
    }

    @Test
    @DisplayName("Senaryo 2 - Tüm Uçuşları Listeleme")
    void scenario2_listAllFlights() {
        Assumptions.assumeTrue("2".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/flights");

        // Uçuş listesinin yüklenmesini bekle
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(@class, 'flight') or contains(@id, 'flight-list')]")));

        // Sayfa başlığını kontrol et
        String pageTitle = driver.getTitle();
        assertTrue(pageTitle.contains("Uçuş") || pageTitle.contains("Flight"),
                "Sayfa başlığı beklenen içeriği içermiyor!");

        System.out.println("✅ Uçuş listesi sayfası başarıyla yüklendi!");
    }

    @Test
    @DisplayName("Senaryo 3 - Uçuş Detay Görüntüleme")
    void scenario3_viewFlightDetails() {
        Assumptions.assumeTrue("3".equals(SELENIUM_SCENARIO));

        // Önce uçuş listesine git
        driver.get(FRONTEND_BASE + "/flights");

        // İlk uçuşun detay butonunu bul ve tıkla
        WebElement detailButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Detay') or contains(text(), 'Details')] | " +
                        "//a[contains(@href, '/flight/')] | " +
                        "//*[contains(@class, 'flight-detail-btn')]")));
        detailButton.click();

        // Detay sayfasının yüklenmesini bekle
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(), 'Uçuş Numarası') or contains(text(), 'Flight Number')]")),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(@class, 'flight-detail')]"))));

        System.out.println("✅ Uçuş detay sayfası başarıyla görüntülendi!");
    }

    @Test
    @DisplayName("Senaryo 4 - Uçuş Arama (Kalkış/Varış/Tarih)")
    void scenario4_searchFlights() {
        Assumptions.assumeTrue("4".equals(SELENIUM_SCENARIO));

        driver.get(FRONTEND_BASE + "/search");

        // Arama formunun yüklenmesini bekle
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("departureAirportId")));

        // Kalkış havaalanı seç
        Select departureSelect = new Select(driver.findElement(By.name("departureAirportId")));
        departureSelect.selectByIndex(1);

        // Varış havaalanı seç
        Select arrivalSelect = new Select(driver.findElement(By.name("arrivalAirportId")));
        arrivalSelect.selectByIndex(2);

        // Gelecek bir tarih seç
        String futureDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        driver.findElement(By.name("departureDate")).sendKeys(futureDate);

        // Arama butonuna tıkla
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Arama sonuçlarının yüklenmesini bekle
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(@class, 'search-result')]")),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(), 'Sonuç') or contains(text(), 'Result')]"))));

        System.out.println("✅ Uçuş arama işlemi başarıyla tamamlandı!");
    }

    @Test
    @DisplayName("Senaryo 5 - Admin: Yeni Uçak Ekleme")
    void scenario5_adminAddAircraft() {
        Assumptions.assumeTrue("5".equals(SELENIUM_SCENARIO));

        // Admin yetkisi ver
        driver.get(FRONTEND_BASE + "/");
        setAdminLocalStorage();
        driver.navigate().refresh();

        // Admin uçak yönetim sayfasına git
        driver.get(FRONTEND_BASE + "/admin/aircrafts");

        // Yeni uçak ekle butonuna tıkla
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Yeni Uçak') or contains(text(), 'Add Aircraft')]")));
        addButton.click();

        // Form alanlarını doldur
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("registrationNumber")))
                .sendKeys("TC-" + System.currentTimeMillis());

        driver.findElement(By.name("model")).sendKeys("Boeing 737");
        driver.findElement(By.name("manufacturer")).sendKeys("Boeing");
        driver.findElement(By.name("totalSeats")).sendKeys("180");
        driver.findElement(By.name("economySeats")).sendKeys("150");
        driver.findElement(By.name("businessSeats")).sendKeys("30");
        driver.findElement(By.name("yearOfManufacture")).sendKeys("2020");

        // Formu gönder
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Başarı mesajı kontrolü
        wait.until(ExpectedConditions.or(
                ExpectedConditions.alertIsPresent(),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(), 'başarı') or contains(text(), 'success')]"))));

        System.out.println("✅ Yeni uçak başarıyla eklendi!");
    }

    @Test
    @DisplayName("Senaryo 6 - Admin: Yeni Havaalanı Ekleme")
    void scenario6_adminAddAirport() {
        Assumptions.assumeTrue("6".equals(SELENIUM_SCENARIO));

        // Admin yetkisi ver
        driver.get(FRONTEND_BASE + "/");
        setAdminLocalStorage();
        driver.navigate().refresh();

        // Admin havaalanı yönetim sayfasına git
        driver.get(FRONTEND_BASE + "/admin/airports");

        // Yeni havaalanı ekle butonuna tıkla
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Yeni Havaalanı') or contains(text(), 'Add Airport')]")));
        addButton.click();

        // Form alanlarını doldur
        String timestamp = String.valueOf(System.currentTimeMillis() % 10000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("code")))
                .sendKeys("TST" + timestamp);

        driver.findElement(By.name("name")).sendKeys("Test Havaalanı " + timestamp);
        driver.findElement(By.name("city")).sendKeys("Test Şehir");
        driver.findElement(By.name("country")).sendKeys("Türkiye");

        // Formu gönder
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Başarı mesajı kontrolü
        wait.until(ExpectedConditions.or(
                ExpectedConditions.alertIsPresent(),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(), 'başarı') or contains(text(), 'success')]"))));

        System.out.println("✅ Yeni havaalanı başarıyla eklendi!");
    }

    @Test
    @DisplayName("Senaryo 7 - Admin: Bakım Kaydı Ekleme")
    void scenario7_adminAddMaintenance() {
        Assumptions.assumeTrue("7".equals(SELENIUM_SCENARIO));

        // Admin yetkisi ver
        driver.get(FRONTEND_BASE + "/");
        setAdminLocalStorage();
        driver.navigate().refresh();

        // Admin bakım yönetim sayfasına git
        driver.get(FRONTEND_BASE + "/admin/maintenance");

        // Yeni bakım kaydı ekle butonuna tıkla
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Yeni Bakım') or contains(text(), 'Add Maintenance')]")));
        addButton.click();

        // Form alanlarını doldur
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("aircraftId")));

        Select aircraftSelect = new Select(driver.findElement(By.name("aircraftId")));
        aircraftSelect.selectByIndex(1);

        String futureStart = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        String futureEnd = LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        driver.findElement(By.name("startTime")).sendKeys(futureStart);
        driver.findElement(By.name("endTime")).sendKeys(futureEnd);
        driver.findElement(By.name("description")).sendKeys("Rutin bakım - E2E Test");

        // Formu gönder
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Başarı mesajı kontrolü
        wait.until(ExpectedConditions.or(
                ExpectedConditions.alertIsPresent(),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(), 'başarı') or contains(text(), 'success')]"))));

        System.out.println("✅ Bakım kaydı başarıyla eklendi!");
    }
}
