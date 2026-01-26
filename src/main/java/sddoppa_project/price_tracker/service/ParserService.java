package sddoppa_project.price_tracker.service;

import sddoppa_project.price_tracker.entity.Product;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ParserService {

    // Основной метод: берет URL, возвращает Product с названием и ценой
    public Product parseProduct(String url) throws IOException {
        Product product = new Product();
        product.setUrl(url); // Сохраняем URL товара
        product.setLastChecked(LocalDateTime.now()); // Ставим время проверки

        // Определяем магазин по домену в URL
        Product.StoreType storeType = detectStoreType(url);
        product.setStoreType(storeType);

        // Загружаем HTML страницу через Jsoup (с заголовками и execute())
        Connection.Response response = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .referrer("https://www.google.com")
                .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .timeout(120000)
                .execute();

        Document doc = response.parse();

        // Выбираем нужный парсер для магазина
        switch (storeType) {
            case DNS:
                parseDNS(doc, product); // Парсим DNS
                break;
            case MVIDEO:
                parseMVideo(doc, product); // TODO: реализовать
                break;
            case CITILINK:
                parseCitilink(doc, product); // TODO: реализовать
                break;
            case WILDBERRIES:
                parseWildberries(doc, product); // TODO: реализовать
                break;
            case OZON:
                parseOzon(doc, product); // TODO: реализовать
                break;
            default:
                parseGeneric(doc, product); // Общий парсер для других магазинов
        }

        return product; // Возвращаем заполненный товар
    }

    // Определяет тип магазина по URL
    private Product.StoreType detectStoreType(String url) {
        String lowerUrl = url.toLowerCase(); // Приводим к нижнему регистру

        if (lowerUrl.contains("dns-shop.ru")) return Product.StoreType.DNS;
        if (lowerUrl.contains("mvideo.ru")) return Product.StoreType.MVIDEO;
        if (lowerUrl.contains("citilink.ru")) return Product.StoreType.CITILINK;
        if (lowerUrl.contains("wildberries.ru")) return Product.StoreType.WILDBERRIES;
        if (lowerUrl.contains("ozon.ru")) return Product.StoreType.OZON;
        if (lowerUrl.contains("market.yandex.ru")) return Product.StoreType.YANDEX_MARKET;

        return Product.StoreType.OTHER; // Неизвестный магазин
    }

    // Парсер для DNS-shop.ru
    private void parseDNS(Document doc, Product product) {
        try {
            // Ищем название товара
            Element nameElement = doc.selectFirst(".product-card-top__name");
            if (nameElement != null) {
                product.setName(nameElement.text().trim());
            }

            // Ищем цену товара
            Element priceElement = doc.selectFirst(".product-buy__price");
            if (priceElement != null) {
                String priceText = priceElement.text();
                priceText = priceText.replaceAll("[^0-9]", ""); // Убираем всё кроме цифр
                if (!priceText.isEmpty()) {
                    BigDecimal price = new BigDecimal(priceText);
                    product.setCurrentPrice(price);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга DNS: " + e.getMessage());
        }
    }

    // Парсер для MVideo (пока заглушка)
    private void parseMVideo(Document doc, Product product) {
        System.out.println("Парсинг MVideo еще не реализован");
    }

    // Парсер для Citilink (пока заглушка)
    private void parseCitilink(Document doc, Product product) {
        System.out.println("Парсинг Citilink еще не реализован");
    }

    // Заглушка для Wildberries
    private void parseWildberries(Document doc, Product product) {
        System.out.println("Парсинг Wildberries еще не реализован");
    }

    // Заглушка для Ozon
    private void parseOzon(Document doc, Product product) {
        System.out.println("Парсинг Ozon еще не реализован");
    }

    // Общий парсер для неизвестных магазинов
    private void parseGeneric(Document doc, Product product) {
        try {
            // Пробуем разные селекторы для названия
            String[] nameSelectors = {"h1", ".product-name", ".title", "[itemprop='name']"};
            for (String selector : nameSelectors) {
                Element element = doc.selectFirst(selector);
                if (element != null) {
                    product.setName(element.text().trim());
                    break;
                }
            }

            // Пробуем разные селекторы для цены
            String[] priceSelectors = {".price", ".product-price", "[itemprop='price']", ".current-price"};
            for (String selector : priceSelectors) {
                Element element = doc.selectFirst(selector);
                if (element != null) {
                    String priceText = element.text().replaceAll("[^0-9]", "");
                    if (!priceText.isEmpty()) {
                        product.setCurrentPrice(new BigDecimal(priceText));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка общего парсинга: " + e.getMessage());
        }
    }
}
