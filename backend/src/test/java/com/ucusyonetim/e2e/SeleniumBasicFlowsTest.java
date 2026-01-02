package com.ucusyonetim.e2e;

import org.junit.jupiter.api.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 7 Temel E2E Test Senaryosu - API Tabanlı (Frontend'siz)
 * 
 * Stage 6: Login Flow, Admin Add Flight
 * Stage 7: Havaalanı Listeleme, Uçuş Listeleme, Uçuş Detay, Uçuş Arama,
 * Admin Uçak Ekleme, Admin Havaalanı Ekleme, Admin Bakım Kaydı
 */
public class SeleniumBasicFlowsTest {

    private static final String SELENIUM_SCENARIO = System.getProperty("selenium.scenario", "1");
    private static final String BACKEND_BASE = System.getProperty("backend.base", "http://ucus-yonetim-backend:8080");

    private String makeApiCall(String endpoint, String method, String jsonBody) throws Exception {
        URI uri = new URI(BACKEND_BASE + endpoint);
        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        if (jsonBody != null && !jsonBody.isEmpty()) {
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        int code = con.getResponseCode();
        StringBuilder response = new StringBuilder();

        try (Scanner scanner = new Scanner(
                code >= 200 && code < 300 ? con.getInputStream() : con.getErrorStream(), "UTF-8")) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }

        if (code < 200 || code >= 300) {
            throw new RuntimeException("API Error! Code: " + code + " Response: " + response.toString());
        }

        return response.toString();
    }

    @Test
    @DisplayName("Senaryo 1 - Havaalanı Listeleme")
    void scenario1_listAirports() throws Exception {
        Assumptions.assumeTrue("1".equals(SELENIUM_SCENARIO));

        String response = makeApiCall("/api/airports", "GET", null);

        assertNotNull(response, "API yanıtı null olmamalı!");
        assertTrue(response.contains("["), "Yanıt bir liste olmalı!");

        System.out.println("✅ Havaalanı listeleme API'si başarıyla çalıştı!");
        System.out.println("Yanıt: " + response.substring(0, Math.min(200, response.length())) + "...");
    }

    @Test
    @DisplayName("Senaryo 2 - Tüm Uçuşları Listeleme")
    void scenario2_listAllFlights() throws Exception {
        Assumptions.assumeTrue("2".equals(SELENIUM_SCENARIO));

        String response = makeApiCall("/api/flights/all", "GET", null);

        assertNotNull(response, "API yanıtı null olmamalı!");
        assertTrue(response.contains("["), "Yanıt bir liste olmalı!");

        System.out.println("✅ Uçuş listeleme API'si başarıyla çalıştı!");
        System.out.println("Yanıt: " + response.substring(0, Math.min(200, response.length())) + "...");
    }

    @Test
    @DisplayName("Senaryo 3 - Uçuş Detay Görüntüleme")
    void scenario3_viewFlightDetails() throws Exception {
        Assumptions.assumeTrue("3".equals(SELENIUM_SCENARIO));

        // Önce bir uçuş ID'si al
        String listResponse = makeApiCall("/api/flights/all", "GET", null);

        // İlk uçuşun ID'sini çıkar (basit JSON parsing)
        String idStr = listResponse.substring(listResponse.indexOf("\"id\":") + 5);
        String flightId = idStr.substring(0, idStr.indexOf(","));

        // Uçuş detayını getir
        String response = makeApiCall("/api/flights/" + flightId, "GET", null);

        assertNotNull(response, "API yanıtı null olmamalı!");
        assertTrue(response.contains("\"id\":" + flightId), "Yanıt doğru uçuşu içermeli!");

        System.out.println("✅ Uçuş detay API'si başarıyla çalıştı! Flight ID: " + flightId);
        System.out.println("Yanıt: " + response.substring(0, Math.min(200, response.length())) + "...");
    }

    @Test
    @DisplayName("Senaryo 4 - Uçuş Arama")
    void scenario4_searchFlights() throws Exception {
        Assumptions.assumeTrue("4".equals(SELENIUM_SCENARIO));

        // Gelecek bir tarih oluştur
        String futureDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME);

        // Arama parametreleri ile API çağrısı
        String endpoint = "/api/flights/search?departureAirportId=1&arrivalAirportId=2&departureDate=" + futureDate;
        String response = makeApiCall(endpoint, "GET", null);

        assertNotNull(response, "API yanıtı null olmamalı!");
        assertTrue(response.contains("["), "Yanıt bir liste olmalı!");

        System.out.println("✅ Uçuş arama API'si başarıyla çalıştı!");
        System.out.println("Yanıt: " + response.substring(0, Math.min(200, response.length())) + "...");
    }

    @Test
    @DisplayName("Senaryo 5 - Admin: Yeni Uçak Ekleme")
    void scenario5_adminAddAircraft() throws Exception {
        Assumptions.assumeTrue("5".equals(SELENIUM_SCENARIO));

        String timestamp = String.valueOf(System.currentTimeMillis() % 100000);
        String json = String.format(
                "{\"registrationNumber\":\"TC-%s\",\"model\":\"Boeing 737\",\"manufacturer\":\"Boeing\"," +
                        "\"totalSeats\":180,\"economySeats\":150,\"businessSeats\":30,\"yearOfManufacture\":2020," +
                        "\"active\":true,\"underMaintenance\":false}",
                timestamp);

        String response = makeApiCall("/api/admin/aircrafts", "POST", json);

        assertNotNull(response, "API yanıtı null olmamalı!");
        assertTrue(response.contains("TC-" + timestamp), "Yanıt oluşturulan uçağı içermeli!");

        System.out.println("✅ Yeni uçak ekleme API'si başarıyla çalıştı!");
        System.out.println("Yanıt: " + response.substring(0, Math.min(200, response.length())) + "...");
    }

    @Test
    @DisplayName("Senaryo 6 - Admin: Yeni Havaalanı Ekleme")
    void scenario6_adminAddAirport() throws Exception {
        Assumptions.assumeTrue("6".equals(SELENIUM_SCENARIO));

        String timestamp = String.valueOf(System.currentTimeMillis() % 10000);
        String json = String.format(
                "{\"code\":\"TST%s\",\"name\":\"Test Havaalanı %s\",\"city\":\"Test Şehir\"," +
                        "\"country\":\"Türkiye\",\"active\":true}",
                timestamp, timestamp);

        String response = makeApiCall("/api/admin/airports", "POST", json);

        assertNotNull(response, "API yanıtı null olmamalı!");
        assertTrue(response.contains("TST" + timestamp), "Yanıt oluşturulan havaalanını içermeli!");

        System.out.println("✅ Yeni havaalanı ekleme API'si başarıyla çalıştı!");
        System.out.println("Yanıt: " + response.substring(0, Math.min(200, response.length())) + "...");
    }

    @Test
    @DisplayName("Senaryo 7 - Admin: Bakım Kaydı Ekleme")
    void scenario7_adminAddMaintenance() throws Exception {
        Assumptions.assumeTrue("7".equals(SELENIUM_SCENARIO));

        // Önce bir uçak ID'si al
        String aircraftResponse = makeApiCall("/api/admin/aircrafts", "GET", null);
        String idStr = aircraftResponse.substring(aircraftResponse.indexOf("\"id\":") + 5);
        String aircraftId = idStr.substring(0, idStr.indexOf(","));

        String futureStart = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME);
        String futureEnd = LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ISO_DATE_TIME);

        String json = String.format(
                "{\"aircraftId\":%s,\"startTime\":\"%s\",\"endTime\":\"%s\"," +
                        "\"description\":\"Rutin bakım - E2E Test\",\"maintenanceType\":\"SCHEDULED\"}",
                aircraftId, futureStart, futureEnd);

        String response = makeApiCall("/api/admin/maintenance", "POST", json);

        assertNotNull(response, "API yanıtı null olmamalı!");
        assertTrue(response.contains("Rutin bakım"), "Yanıt oluşturulan bakım kaydını içermeli!");

        System.out.println("✅ Bakım kaydı ekleme API'si başarıyla çalıştı!");
        System.out.println("Yanıt: " + response.substring(0, Math.min(200, response.length())) + "...");
    }
}
