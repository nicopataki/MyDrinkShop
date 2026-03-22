package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("F01.3.1.1 - Add Product (ECP + BVA)")
@Tag("F01")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceAddTest {

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(new InMemoryProductRepository());
    }

    @Test
    @Order(1)
    @DisplayName("ECP valid: product with valid fields is saved")
    @Tag("ECP")
    void addProduct_validProduct_isSaved() {
        // Arrange
        Product p = new Product(1, "Espresso", 9.5, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act
        service.addProduct(p);

        // Assert
        Product found = service.findById(1);
        assertNotNull(found);
        assertEquals("Espresso", found.getNume());
        assertEquals(9.5, found.getPret());
    }

    @Test
    @Order(2)
    @DisplayName("ECP invalid: duplicate id throws ValidationException")
    @Tag("ECP")
    void addProduct_duplicateId_throwsValidationException() {
        // Arrange
        Product first = new Product(10, "Cappuccino", 14.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        Product duplicate = new Product(10, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act
        service.addProduct(first);

        // Assert
        ValidationException ex = assertThrows(ValidationException.class, () -> service.addProduct(duplicate));
        assertTrue(ex.getMessage().contains("ID"));
    }

    @Test
    @Order(3)
    @DisplayName("BVA invalid: price at lower boundary 0.0 is rejected")
    @Tag("BVA")
    void addProduct_invalidPrice_throwsValidationException() {
        // Arrange
        Product invalid = new Product(2, "Flat White", 0.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act + Assert
        assertThrows(ValidationException.class, () -> service.addProduct(invalid));
    }

    @Test
    @Order(4)
    @DisplayName("BVA valid: id=1 and price just above zero are accepted")
    @Tag("BVA")
    void addProduct_boundaryValidValues_isSaved() {
        // Arrange
        Product boundaryValid = new Product(1, "Americano", 0.01, CategorieBautura.CLASSIC_COFFEE, TipBautura.WATER_BASED);

        // Act
        service.addProduct(boundaryValid);

        // Assert
        Product found = service.findById(1);
        assertNotNull(found);
        assertEquals("Americano", found.getNume());
        assertEquals(0.01, found.getPret());
    }

    private static class InMemoryProductRepository extends AbstractRepository<Integer, Product> {
        @Override
        protected Integer getId(Product entity) {
            return entity.getId();
        }
    }
}

