package sddoppa_project.price_tracker.service;

import sddoppa_project.price_tracker.entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.IOException;

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

        // Загружаем HTML страницу через Jsoup
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0") // Притворяемся браузером
                .timeout(30000) // Ждем максимум 30 секунд
                .get();

        // Выбираем нужный парсер для магазина
        switch (storeType) {
            case DNS:
                parseDNS(doc, product); // Парсим DNS
                break;
            case MVIDEO:
                parseMVideo(doc, product); // Парсим MVideo
                break;
            case CITILINK:
                parseCitilink(doc, product); // Парсим Citilink
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
            // Ищем название товара (класс .product-card-top__name на DNS)
            Element nameElement = doc.selectFirst(".product-card-top__name");
            if (nameElement != null) {
                product.setName(nameElement.text().trim()); // Берем текст, обрезаем пробелы
            }

            // Ищем цену (класс .product-buy__price на DNS)
            Element priceElement = doc.selectFirst(".product-buy__price");
            if (priceElement != null) {
                String priceText = priceElement.text(); // Получаем текст "85 999 ₽"
                priceText = priceText.replaceAll("[^0-9]", ""); // Убираем все кроме цифр
                if (!priceText.isEmpty()) {
                    // Преобразуем строку в BigDecimal (лучше для денег чем double)
                    BigDecimal price = new BigDecimal(priceText);
                    product.setCurrentPrice(price); // Сохраняем цену
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга DNS: " + e.getMessage()); // Простой вывод ошибки
        }
    }

    // Парсер для MVideo.ru
    private void parseMVideo(Document doc, Product product) {
        try {
            // На MVideo название в теге h1
            Element nameElement = doc.selectFirst("h1");
            if (nameElement != null) {
                product.setName(nameElement.text().trim());
            }

            // Цена на MVideo в классе .price__main-value
            Element priceElement = doc.selectFirst(".price__main-value");
            if (priceElement != null) {
                String priceText = priceElement.text();
                priceText = priceText.replaceAll("[^0-9]", "");
                if (!priceText.isEmpty()) {
                    product.setCurrentPrice(new BigDecimal(priceText));
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга MVideo: " + e.getMessage());
        }
    }

    // Парсер для Citilink.ru
    private void parseCitilink(Document doc, Product product) {
        try {
            Element nameElement = doc.selectFirst("h1");
            if (nameElement != null) {
                product.setName(nameElement.text().trim());
            }

            Element priceElement = doc.selectFirst(".ProductHeader__price-default_current-price");
            if (priceElement != null) {
                String priceText = priceElement.text();
                priceText = priceText.replaceAll("[^0-9]", "");
                if (!priceText.isEmpty()) {
                    product.setCurrentPrice(new BigDecimal(priceText));
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга Citilink: " + e.getMessage());
        }
    }

    // Заглушки для других магазинов (пока не реализованы)
    private void parseWildberries(Document doc, Product product) {
        System.out.println("Парсинг Wildberries еще не реализован");
    }

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
                    break; // Нашли - выходим
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
                        break; // Нашли - выходим
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка общего парсинга: " + e.getMessage());
        }
    }
}