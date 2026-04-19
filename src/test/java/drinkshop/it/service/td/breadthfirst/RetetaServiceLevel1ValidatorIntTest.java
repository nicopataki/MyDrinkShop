package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.repository.Repository;
import drinkshop.service.RetetaService;
import drinkshop.service.validator.RetetaValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RetetaServiceLevel1ValidatorIntTest {

    private Reteta reteta;
    private RetetaValidator retetaValidator;
    private Repository<Integer, Reteta> retetaRepo;

    private RetetaService retetaService;

    @BeforeEach
    void setUp() {
        reteta = mock(Reteta.class);
        retetaValidator = new RetetaValidator(); // REAL
        retetaRepo = mock(Repository.class);      // MOCK

        retetaService = new RetetaService(retetaRepo, retetaValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealValidator() {

        IngredientReteta ing = new IngredientReteta("lapte", 10.0);

        when(reteta.getId()).thenReturn(1);
        when(reteta.getIngrediente()).thenReturn(List.of(ing));

        when(retetaRepo.save(reteta)).thenReturn(reteta);

        try {
            retetaService.addReteta(reteta);
        } catch (Exception e) {
            fail("Invalid add operation: " + e.getMessage());
        }

        verify(retetaRepo, times(1)).save(reteta);
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealValidator() {

        when(reteta.getId()).thenReturn(-1);
        when(reteta.getIngrediente()).thenReturn(Arrays.asList());

        Assertions.assertThrows(ValidationException.class, () -> {
            retetaService.addReteta(reteta);
        });

        verify(retetaRepo, never()).save(any());
        verify(reteta, times(1)).getId();
    }
}