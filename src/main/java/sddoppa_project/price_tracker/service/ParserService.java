package sddoppa_project.price_tracker.service;

import sddoppa_project.price_tracker.entity.Product;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
// ... (imports)
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class ParserService {

    private WebDriver createDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // анти-бот
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        options.addArguments(
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        );

        return new ChromeDriver(options);
    }

    public Product parseProduct(String url) {
        Product product = new Product();
        product.setUrl(url);

        Product.StoreType storeType = detectStoreType(url);
        product.setStoreType(storeType);

        if (storeType != Product.StoreType.DNS) {
            System.out.println("Парсер для " + storeType + " пока не реализован");
            return product;
        }

        WebDriver driver = createDriver();

        try {
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            WebElement nameEl = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-card-top__name"))
            );
            product.setName(nameEl.getText().trim());

            WebElement imgEl = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-images-slider__main-img"))
            );
            product.setImageUrl(imgEl.getAttribute("src"));

            WebElement priceEl = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-buy__price"))
            );

            String priceText = priceEl.getText().replaceAll("[^0-9]", "");
            product.setCurrentPrice(new BigDecimal(priceText));

        } catch (Exception e) {
            System.err.println("Ошибка парсинга DNS: " + e.getMessage());
        } finally {
            driver.quit();
        }

        return product;
    }

    private Product.StoreType detectStoreType(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("dns-shop.ru")) return Product.StoreType.DNS;
        if (lowerUrl.contains("mvideo.ru")) return Product.StoreType.MVIDEO;
        if (lowerUrl.contains("citilink.ru")) return Product.StoreType.CITILINK;
        if (lowerUrl.contains("wildberries.ru")) return Product.StoreType.WILDBERRIES;
        if (lowerUrl.contains("ozon.ru")) return Product.StoreType.OZON;
        if (lowerUrl.contains("market.yandex.ru")) return Product.StoreType.YANDEX_MARKET;
        return Product.StoreType.OTHER;
    }
}
