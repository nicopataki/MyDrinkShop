package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileRetetaRepository;
import drinkshop.service.RetetaService;
import drinkshop.service.validator.RetetaValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RetetaServiceLevel1RepoIntTest {

    private Reteta reteta;
    private RetetaValidator retetaValidator;
    private Repository<Integer, Reteta> retetaRepo;

    private RetetaService retetaService;

    @BeforeEach
    void setUp() {

        reteta = mock(Reteta.class);
        retetaValidator = mock(RetetaValidator.class); // MOCK
        retetaRepo = new FileRetetaRepository("data/retete.txt"); // REAL

        retetaService = new RetetaService(retetaRepo, retetaValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealRepository() {

        IngredientReteta ing = mock(IngredientReteta.class);

        when(reteta.getId()).thenReturn(1);
        when(reteta.getIngrediente()).thenReturn(Arrays.asList(ing));

        doNothing().when(retetaValidator).validate(reteta);

        try {
            retetaService.addReteta(reteta);
        } catch (Exception e) {
            fail("Invalid add operation: " + e.getMessage());
        }

        verify(retetaValidator, times(1)).validate(reteta);
        Assertions.assertNotNull(retetaRepo.findAll());
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealRepository() {

        when(reteta.getId()).thenReturn(-1);

        doThrow(new ValidationException("Invalid reteta"))
                .when(retetaValidator).validate(reteta);

        Assertions.assertThrows(ValidationException.class, () -> {
            retetaService.addReteta(reteta);
        });

        verify(retetaValidator, times(1)).validate(reteta);
    }
}