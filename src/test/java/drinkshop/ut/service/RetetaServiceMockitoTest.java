package drinkshop.ut.service;

import drinkshop.domain.Reteta;
import drinkshop.repository.Repository;
import drinkshop.service.RetetaService;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RetetaServiceMockitoTest {

    private Reteta reteta;
    private Validator<Reteta> retetaValidator;
    private Repository<Integer, Reteta> retetaRepo;

    private RetetaService retetaService;

    @BeforeEach
    public void setUp() {
        // cream obiecte mock
        reteta = mock(Reteta.class);
        retetaValidator = mock(Validator.class);
        retetaRepo = mock(Repository.class);

        // cream obiectul testat
        retetaService = new RetetaService(retetaRepo, retetaValidator);
    }

    @AfterEach
    public void tearDown() {
        retetaService = null;
        retetaRepo = null;
        retetaValidator = null;
        reteta = null;
    }

    @Test
    @Order(1)
    public void testGetAllValid() {
        // mock-uri suplimentare
        Reteta r1 = mock(Reteta.class);
        Reteta r2 = mock(Reteta.class);

        // comportament mock
        when(retetaRepo.findAll()).thenReturn(Arrays.asList(r1, r2));

        // assert
        assert 2 == retetaService.getAll().size();

        // verify
        verify(retetaValidator, never()).validate(r1);
        verify(retetaRepo, times(1)).findAll();
    }

    @Test
    @Order(2)
    public void testAddInvalid() {
        // validatorul arunca exceptie
        doThrow(new ValidationException("Reteta invalida"))
                .when(retetaValidator).validate(reteta);

        // apelam metoda si evaluam
        try {
            retetaService.addReteta(reteta);
        } catch (Exception e) {
            assert e.getClass().equals(ValidationException.class);
        }

        // verify
        verify(retetaValidator, times(1)).validate(reteta);
        verify(retetaRepo, never()).save(any());
    }

    @Test
    @Order(3)
    public void testAddValid() {
        // validator valid
        doNothing().when(retetaValidator).validate(reteta);
        when(retetaRepo.save(reteta)).thenReturn(reteta);

        // apel metoda
        try {
            retetaService.addReteta(reteta);
        } catch (Exception e) {
            fail("Invalid add operation");
        }

        // verify
        verify(retetaValidator, times(1)).validate(reteta);
        verify(retetaRepo, times(1)).save(reteta);
    }
}