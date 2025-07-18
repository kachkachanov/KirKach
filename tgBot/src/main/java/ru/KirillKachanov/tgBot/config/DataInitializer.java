package ru.KirillKachanov.tgBot.config; // Или ru.KirillKachanov.tgBot.initialization

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.KirillKachanov.tgBot.entity.Category;
import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.repository.CategoryRepository;
import ru.KirillKachanov.tgBot.repository.ProductRepository;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, есть ли уже категории верхнего уровня, чтобы не дублировать данные при каждом запуске
        if (categoryRepository.findByParentIsNull().isEmpty()) {
            System.out.println("Initializing database with categories and products...");
            fillCategoriesAndProducts();
            System.out.println("Database initialization complete.");
        } else {
            System.out.println("Database already contains categories. Skipping initialization.");
        }
    }

    // Методы для создания категорий и продуктов
    private void fillCategoriesAndProducts() {
        Category pizza = categoryRepository.save(createCategory("Пицца", null));
        Category rolls = categoryRepository.save(createCategory("Роллы", null));
        Category burgers = categoryRepository.save(createCategory("Бургеры", null));
        Category drinks = categoryRepository.save(createCategory("Напитки", null));

        // Подкатегории для роллов
        Category classicRolls = categoryRepository.save(createCategory("Классические роллы", rolls));
        Category bakedRolls = categoryRepository.save(createCategory("Запеченные роллы", rolls));
        Category sweetRolls = categoryRepository.save(createCategory("Сладкие роллы", rolls));
        Category sets = categoryRepository.save(createCategory("Наборы", rolls));

        // Подкатегории для бургеров
        Category classicBurgers = categoryRepository.save(createCategory("Классические бургеры", burgers));
        Category spicyBurgers = categoryRepository.save(createCategory("Острые бургеры", burgers));

        // Подкатегории для напитков
        Category soda = categoryRepository.save(createCategory("Газированные напитки", drinks));
        Category energy = categoryRepository.save(createCategory("Энергетические напитки", drinks));
        Category juices = categoryRepository.save(createCategory("Соки", drinks));
        Category other = categoryRepository.save(createCategory("Другие", drinks));


        addProductsToCategory(classicRolls, "Ролл");
        addProductsToCategory(bakedRolls, "Запеченный ролл");
        addProductsToCategory(sweetRolls, "Сладкий ролл");
        addProductsToCategory(sets, "Сет");

        addProductsToCategory(classicBurgers, "Бургер");
        addProductsToCategory(spicyBurgers, "Острый бургер");

        addProductsToCategory(soda, "Газировка");
        addProductsToCategory(energy, "Энергетик");
        addProductsToCategory(juices, "Сок");
        addProductsToCategory(other, "Напиток");
    }

    private Category createCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        return category;
    }

    private void addProductsToCategory(Category category, String prefix) {
        for (int i = 1; i <= 3; i++) {
            Product product = new Product();
            product.setCategory(category);
            product.setName(prefix + " " + i);
            product.setDescription("Описание для " + prefix + " " + i);
            product.setPrice(100.0 + i);
            productRepository.save(product);
        }
    }
}