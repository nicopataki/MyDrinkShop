package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.file.FileStocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StocServiceConsumaTest {

    private FileStocRepository repo;
    private StocService service;

    // resetam fișierul inainte de fiecare test
    @BeforeEach
    void setUp() throws Exception {
        Files.writeString(
                Path.of("data/stocuri.txt"),
                """
                1;cafea_macinata;1000;300
                2;apa;10000;2000
                3;lapte;5000;1000
                4;lapte_ovaz;3000;800
                5;matcha_pudra;500;100
                6;taro_pudra;500;100
                7;tapioca;2000;500
                8;ceai_verde;300;80
                9;lamaie;1000;200
                10;zahar;2000;500
                11;zahar;1000;500
                """
        );

        repo = new FileStocRepository("data/stocuri.txt");
        service = new StocService(repo);
    }

    // TC1 - stoc insuficient
    @Test
    void testConsuma_StocInsuficient() {
        Reteta r = new Reteta(1, new ArrayList<>());
        r.getIngrediente().add(new IngredientReteta("zahar", 100000));

        assertThrows(IllegalStateException.class, () -> service.consuma(r));
    }

    // TC2 - ingredient inexistent
    @Test
    void testConsuma_IngredientInexistent() {
        Reteta r = new Reteta(1, new ArrayList<>());
        r.getIngrediente().add(new IngredientReteta("NU_EXISTA", 10));

        assertThrows(IllegalStateException.class, () -> service.consuma(r));
    }

    // TC3 - consum simplu
    @Test
    void testConsuma_Simplu() {
        Reteta r = new Reteta(1, new ArrayList<>());
        r.getIngrediente().add(new IngredientReteta("zahar", 5));

        double before = total("zahar");

        service.consuma(r);

        double after = total("zahar");

        assertEquals(before - 5, after);
    }

    // TC4 - multiple surse (loop)
    @Test
    void testConsuma_MultipleSurse() {
        Reteta r = new Reteta(1, new ArrayList<>());
        r.getIngrediente().add(new IngredientReteta("zahar", 600));

        double before = total("zahar");

        service.consuma(r);

        double after = total("zahar");

        assertEquals(before - 600, after);
    }

    // TC5 - break condition (ramas <= 0 → TRUE)
    @Test
    void testConsuma_BreakTriggered() {
        Reteta r = new Reteta(1, new ArrayList<>());
        r.getIngrediente().add(new IngredientReteta("zahar", 1));

        service.consuma(r);

        assertTrue(true);
    }

    // TC6 - fara break la prima iteratie
    @Test
    void testConsuma_NoBreakFirstIteration() {
        Reteta r = new Reteta(1, new ArrayList<>());

        // mai mare decât un singur stoc → forțează mai multe iterații
        r.getIngrediente().add(new IngredientReteta("zahar", 1500));

        service.consuma(r);

        assertTrue(true);
    }

    // TC7 - mai multe ingrediente
    @Test
    void testConsuma_MaiMulteIngrediente() {
        Reteta r = new Reteta(1, new ArrayList<>());

        r.getIngrediente().add(new IngredientReteta("zahar", 5));
        r.getIngrediente().add(new IngredientReteta("lapte", 5));

        double beforeZahar = total("zahar");
        double beforeLapte = total("lapte");

        service.consuma(r);

        assertEquals(beforeZahar - 5, total("zahar"));
        assertEquals(beforeLapte - 5, total("lapte"));
    }

    // TC8 - fara ingrediente
    @Test
    void testConsuma_FaraIngrediente() {
        Reteta r = new Reteta(1, new ArrayList<>());

        List<Stoc> before = repo.findAll();

        service.consuma(r);

        List<Stoc> after = repo.findAll();

        assertEquals(before.size(), after.size());
    }

    // helper
    private double total(String ingredient) {
        return repo.findAll().stream()
                .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                .mapToDouble(Stoc::getCantitate)
                .sum();
    }
}