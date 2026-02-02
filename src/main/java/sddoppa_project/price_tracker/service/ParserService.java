package sddoppa_project.price_tracker.service;

// Импорт Selenium для автоматизации браузера
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

// Импорт Jsoup для парсинга HTML (аналог BeautifulSoup в Python)
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Импорт Spring аннотаций
import org.springframework.stereotype.Service;

// Импорт вашей сущности Product
import sddoppa_project.price_tracker.entity.Product;

// Импорт утилит Java
import java.math.BigDecimal;
import java.util.*;

// Аннотация Spring - указывает что это сервисный компонент
@Service
public class ParserService {

    // Генератор случайных чисел для создания случайных пауз
    private final Random random = new Random();

    /**
     * СОЗДАНИЕ И НАСТРОЙКА WEBDRIVER ДЛЯ ВСЕХ ПАРСЕРОВ
     * WebDriver - это основной инструмент Selenium для управления браузером
     * Этот метод создает Chrome браузер с настройками для обхода антибот защиты
     */
    private WebDriver createDriver() {
        // Создаем объект для настройки Chrome
        ChromeOptions options = new ChromeOptions();

        // Включаем режим без графического интерфейса (headless)
        // Браузер работает в фоне без открытия окна
        options.addArguments("--headless=new");

        // Отключаем использование GPU для стабильности
        options.addArguments("--disable-gpu");

        // Отключаем sandbox для работы в контейнерах (Docker)
        options.addArguments("--no-sandbox");

        // Отключаем shared memory usage для работы в ограниченных средах
        options.addArguments("--disable-dev-shm-usage");

        // КРИТИЧЕСКИ ВАЖНО: Отключаем признаки автоматизации
        // Без этого DNS-Shop определяет что мы бот и блокирует
        options.addArguments("--disable-blink-features=AutomationControlled");

        // Убираем из браузера информацию что это автоматизация
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));

        // Отключаем расширение автоматизации
        options.setExperimentalOption("useAutomationExtension", false);

        // Устанавливаем User-Agent как у реального Chrome браузера
        // Это делает наш запрос похожим на запрос от реального пользователя
        options.addArguments(
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        );

        // Устанавливаем размер окна браузера
        options.addArguments("--window-size=1920,1080");

        // Создаем и возвращаем ChromeDriver с нашими настройками
        return new ChromeDriver(options);
    }

    /**
     * МЕТОД ДЛЯ СОЗДАНИЯ СЛУЧАЙНЫХ ПАУЗ МЕЖДУ ДЕЙСТВИЯМИ
     * Имитирует поведение человека - человек не кликает мгновенно
     * @param minSeconds минимальное время паузы в секундах
     * @param maxSeconds максимальное время паузы в секундах
     */
    private void humanLikePause(int minSeconds, int maxSeconds) {
        try {
            // Генерируем случайное число секунд между min и max
            int sleepSeconds = minSeconds + random.nextInt(maxSeconds - minSeconds + 1);

            // Преобразуем секунды в миллисекунды и приостанавливаем поток
            Thread.sleep(sleepSeconds * 1000L);
        } catch (InterruptedException e) {
            // Если поток был прерван, корректно восстанавливаем флаг прерывания
            Thread.currentThread().interrupt();
        }
    }

    /**
     * ГЛАВНЫЙ ПУБЛИЧНЫЙ МЕТОД ДЛЯ ПАРСИНГА ТОВАРОВ
     * Этот метод вызывается из ProductService
     * Он определяет тип магазина по URL и вызывает соответствующий парсер
     * @param url ссылка на товар
     * @return Product сущность с данными о товаре
     */
    public Product parseProduct(String url) {
        // Приводим URL к нижнему регистру для удобства сравнения
        String lowerUrl = url.toLowerCase();

        // Проверяем какой это магазин и вызываем соответствующий метод парсинга
        if (lowerUrl.contains("dns-shop.ru")) {
            // Если это DNS-Shop, вызываем полный парсер DNS
            return parseDNSProduct(url);
        } else if (lowerUrl.contains("mvideo.ru")) {
            // Если это MVideo, вызываем заглушку (пока не реализовано)
            return parseMVideoProduct(url);
        } else if (lowerUrl.contains("citilink.ru")) {
            // Если это Citilink, вызываем заглушку
            return parseCitilinkProduct(url);
        } else if (lowerUrl.contains("wildberries.ru")) {
            // Если это Wildberries, вызываем заглушку
            return parseWildberriesProduct(url);
        } else if (lowerUrl.contains("ozon.ru")) {
            // Если это Ozon, вызываем заглушку
            return parseOzonProduct(url);
        } else if (lowerUrl.contains("market.yandex.ru")) {
            // Если это Яндекс.Маркет, вызываем заглушку
            return parseYandexMarketProduct(url);
        } else {
            // Если это другой магазин, вызываем общую заглушку
            return parseOtherStoreProduct(url);
        }
    }

    /**
     * ПОЛНЫЙ ПАРСЕР DNS-SHOP
     * Точный аналог функции parse_characteristics_page из Python кода друга
     * Парсит все данные с страницы товара DNS-Shop
     * @param url полная ссылка на страницу товара DNS
     * @return Product сущность с распарсенными данными
     */
    private Product parseDNSProduct(String url) {
        // Выводим в консоль информацию о начале парсинга
        System.out.println("[Parser] Парсим DNS товар: " + url);

        // СОЗДАНИЕ ДРАЙВЕРА: создаем браузер для работы
        WebDriver driver = createDriver();

        // СОЗДАНИЕ ПРОДУКТА: создаем пустую сущность Product
        Product product = new Product();

        // Устанавливаем базовые поля продукта
        product.setUrl(url); // Сохраняем URL товара
        product.setStoreType(Product.StoreType.DNS); // Указываем что это магазин DNS

        // Начинаем блок try-catch для обработки возможных ошибок
        try {
            // ============ ЭТАП 1: ЗАГРУЗКА СТРАНИЦЫ ============
            // Открываем указанный URL в браузере
            driver.get(url);

            // Имитируем поведение человека: ждем случайное время 7-11 секунд
            // Это точная копия pause(randint(7, 11)) из Python кода
            humanLikePause(7, 11);

            // ============ ЭТАП 2: ПОЛУЧЕНИЕ И ПАРСИНГ HTML ============
            // Получаем HTML код страницы из браузера
            String pageSource = driver.getPageSource();

            // Парсим HTML с помощью Jsoup (аналог BeautifulSoup в Python)
            Document soup = Jsoup.parse(pageSource);

            // ============ ЭТАП 3: ПОИСК НАЗВАНИЯ ТОВАРА ============
            // Ищем элемент с CSS селектором div.product-card-description__title
            // Это точный селектор из Python кода: name = soup.find('div', class_="product-card-description__title")
            Element nameElement = soup.selectFirst("div.product-card-description__title");

            // Если элемент найден, извлекаем название
            if (nameElement != null) {
                // Получаем текст из элемента
                String productName = nameElement.text();

                // Точная логика из Python: обрезаем первые 15 символов
                // В Python: name.text[15:]
                if (productName.length() > 15) {
                    productName = productName.substring(15).trim();
                }

                // Сохраняем название в объект Product
                product.setName(productName);
            }

            // ============ ЭТАП 4: ПОИСК ЦЕНЫ ТОВАРА ============
            // Ищем элемент с CSS селектором div.product-buy__price
            // Это точный селектор из Python кода: price = soup.find('div', class_="product-buy__price")
            Element priceElement = soup.selectFirst("div.product-buy__price");

            // Если элемент цены найден
            if (priceElement != null) {
                // Получаем текст цены (например: "89 999 ₽")
                String priceText = priceElement.text();

                // Очищаем текст цены: убираем пробелы и все нецифровые символы
                // Точная логика из Python: price.text.replace(' ', '').replaceAll("[^0-9]", "")
                priceText = priceText.replace(" ", "").replaceAll("[^0-9]", "");

                // Если после очистки остались цифры
                if (!priceText.isEmpty()) {
                    try {
                        // Преобразуем строку в число (int)
                        // В Python: int(price.text.replace(' ', '')[:-1])
                        int price = Integer.parseInt(priceText);

                        // Преобразуем int в BigDecimal и сохраняем в продукт
                        product.setCurrentPrice(new BigDecimal(price));
                    } catch (NumberFormatException e) {
                        // Если преобразование не удалось, выводим ошибку
                        System.err.println("[Parser] Ошибка парсинга цены: " + priceText);
                    }
                }
            }

            // ============ ЭТАП 5: ПОИСК ГЛАВНОЙ КАРТИНКИ ============
            // Ищем элемент с CSS селектором img.product-images-slider__main-img
            // Это точный селектор из Python кода: main_picture = soup.find('img', class_="product-images-slider__main-img")
            Element mainImageElement = soup.selectFirst("img.product-images-slider__main-img");

            // Если элемент найден
            if (mainImageElement != null) {
                // Получаем URL картинки из атрибута src
                String imageUrl = mainImageElement.attr("src");

                // Проверяем что URL не пустой и не равен заглушке
                if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("У товара нет картинок")) {
                    // Сохраняем URL картинки в продукт
                    product.setImageUrl(imageUrl);
                }
            }

            // ============ ЭТАП 6: ПОИСК ОПИСАНИЯ ТОВАРА (ОПЦИОНАЛЬНО) ============
            // Ищем элемент с CSS селектором div.product-card-description-text
            // Это точный селектор из Python кода: desc = soup.find('div', class_="product-card-description-text")
            Element descriptionElement = soup.selectFirst("div.product-card-description-text");
            // Описание пока не сохраняем, но можно добавить поле в Product если нужно

            // ============ ЭТАП 7: ПОИСК НАЛИЧИЯ ТОВАРА (ОПЦИОНАЛЬНО) ============
            // Ищем элемент с CSS селектором a.order-avail-wrap__link.ui-link.ui-link_blue
            // Это точный селектор из Python кода: avail = soup.find('a', class_="order-avail-wrap__link ui-link ui-link_blue")
            Element availabilityElement = soup.selectFirst("a.order-avail-wrap__link.ui-link.ui-link_blue");
            // Наличие пока не сохраняем, но можно добавить поле в Product если нужно

            // ============ ЭТАП 8: ПОИСК КАТЕГОРИИ ТОВАРА (ОПЦИОНАЛЬНО) ============
            // Ищем span с атрибутом data-go-back-catalog
            // Это точная логика из Python кода:
            // for i in span_tags:
            //     if bool(str(i).find('data-go-back-catalog') != -1):
            //         category = i
            String category = "";
            Elements spanTags = soup.select("span");
            for (Element span : spanTags) {
                if (span.toString().contains("data-go-back-catalog")) {
                    category = span.text().replace(": ", "").trim();
                    break;
                }
            }

            // ============ ЭТАП 9: ПОИСК ВСЕХ КАРТИНОК ТОВАРА (ОПЦИОНАЛЬНО) ============
            // Ищем все элементы с CSS селектором img.product-images-slider__img.loaded.tns-complete
            // Это точный селектор из Python кода: pictures_soup = soup.find_all('img', class_="product-images-slider__img loaded tns-complete")
            List<String> picturesList = new ArrayList<>();
            Elements pictureElements = soup.select("img.product-images-slider__img.loaded.tns-complete");

            // Проходим по всем найденным элементам картинок
            for (Element img : pictureElements) {
                // Получаем URL картинки из атрибута data-src
                // В Python: i.get('data-src')
                String imgUrl = img.attr("data-src");

                // Если URL не пустой, добавляем в список
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    picturesList.add(imgUrl);
                }
            }

            // ============ ЭТАП 10: ПОИСК ТЕХНИЧЕСКИХ ХАРАКТЕРИСТИК (ОПЦИОНАЛЬНО) ============
            // Ищем все элементы с названиями и значениями характеристик
            // Это точные селекторы из Python кода:
            // charcs = soup.find_all('div', class_="product-characteristics__spec-title")
            // cvalue = soup.find_all('div', class_="product-characteristics__spec-value")
            Map<String, String> techSpec = new LinkedHashMap<>();
            Elements charcElements = soup.select("div.product-characteristics__spec-title");
            Elements valueElements = soup.select("div.product-characteristics__spec-value");

            // Проходим по парам название-значение
            // В Python: for f1, f2 in zip(charcs, cvalue):
            for (int i = 0; i < Math.min(charcElements.size(), valueElements.size()); i++) {
                // Получаем название характеристики и очищаем пробелы
                String key = charcElements.get(i).text().trim();

                // Получаем значение характеристики и очищаем пробелы
                String value = valueElements.get(i).text().trim();

                // Добавляем в Map
                techSpec.put(key, value);
            }

            // Выводим успешное сообщение
            System.out.println("[Parser] Успешно распарсен DNS товар: " + product.getName());

        } catch (Exception e) {
            // Если произошла ошибка, выводим ее в консоль
            System.err.println("[Parser] Ошибка парсинга DNS товара: " + e.getMessage());

            // Выводим полный stack trace для отладки
            e.printStackTrace();
        } finally {
            // ============ ЭТАП 11: ОЧИСТКА РЕСУРСОВ ============
            // Этот блок выполняется ВСЕГДА, даже если была ошибка
            // Закрываем браузер чтобы освободить ресурсы
            driver.quit();
        }

        // Возвращаем созданный продукт (даже если он пустой из-за ошибки)
        return product;
    }

    /**
     * ЗАГЛУШКА: ПАРСЕР ДЛЯ MVIDEO.RU
     * Метод будет реализован в будущем
     * @param url ссылка на товар MVideo
     * @return пустая сущность Product с установленным типом магазина
     */
    private Product parseMVideoProduct(String url) {
        // Выводим сообщение что парсер еще не реализован
        System.out.println("[Parser] Парсер для MVideo находится в разработке");

        // Создаем пустую сущность Product
        Product product = new Product();

        // Устанавливаем базовые поля
        product.setUrl(url);
        product.setStoreType(Product.StoreType.MVIDEO);

        // Возвращаем пустой продукт
        return product;
    }

    /**
     * ЗАГЛУШКА: ПАРСЕР ДЛЯ CITILINK.RU
     * Метод будет реализован в будущем
     * @param url ссылка на товар Citilink
     * @return пустая сущность Product с установленным типом магазина
     */
    private Product parseCitilinkProduct(String url) {
        System.out.println("[Parser] Парсер для Citilink находится в разработке");
        Product product = new Product();
        product.setUrl(url);
        product.setStoreType(Product.StoreType.CITILINK);
        return product;
    }

    /**
     * ЗАГЛУШКА: ПАРСЕР ДЛЯ WILDBERRIES.RU
     * Метод будет реализован в будущем
     * @param url ссылка на товар Wildberries
     * @return пустая сущность Product с установленным типом магазина
     */
    private Product parseWildberriesProduct(String url) {
        System.out.println("[Parser] Парсер для Wildberries находится в разработке");
        Product product = new Product();
        product.setUrl(url);
        product.setStoreType(Product.StoreType.WILDBERRIES);
        return product;
    }

    /**
     * ЗАГЛУШКА: ПАРСЕР ДЛЯ OZON.RU
     * Метод будет реализован в будущем
     * @param url ссылка на товар Ozon
     * @return пустая сущность Product с установленным типом магазина
     */
    private Product parseOzonProduct(String url) {
        System.out.println("[Parser] Парсер для Ozon находится в разработке");
        Product product = new Product();
        product.setUrl(url);
        product.setStoreType(Product.StoreType.OZON);
        return product;
    }

    /**
     * ЗАГЛУШКА: ПАРСЕР ДЛЯ YANDEX.MARKET
     * Метод будет реализован в будущем
     * @param url ссылка на товар Яндекс.Маркет
     * @return пустая сущность Product с установленным типом магазина
     */
    private Product parseYandexMarketProduct(String url) {
        System.out.println("[Parser] Парсер для Яндекс.Маркет находится в разработке");
        Product product = new Product();
        product.setUrl(url);
        product.setStoreType(Product.StoreType.YANDEX_MARKET);
        return product;
    }

    /**
     * ЗАГЛУШКА: ПАРСЕР ДЛЯ ДРУГИХ МАГАЗИНОВ
     * Метод будет реализован в будущем
     * @param url ссылка на товар в другом магазине
     * @return пустая сущность Product с установленным типом OTHER
     */
    private Product parseOtherStoreProduct(String url) {
        System.out.println("[Parser] Парсер для других магазинов находится в разработке");
        Product product = new Product();
        product.setUrl(url);
        product.setStoreType(Product.StoreType.OTHER);
        return product;
    }
}