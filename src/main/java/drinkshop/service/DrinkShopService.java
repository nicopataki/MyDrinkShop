package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.export.CsvExporter;
import drinkshop.receipt.ReceiptGenerator;
import drinkshop.reports.DailyReportService;

import java.util.List;

public class DrinkShopService {

    private final ProductService productService;
    private final OrderService orderService;
    private final RetetaService retetaService;
    private final StocService stocService;
    private final DailyReportService report;

    public DrinkShopService(
            ProductService productService,
            OrderService orderService,
            RetetaService retetaService,
            StocService stocService,
            DailyReportService report
    ) {
        this.productService = productService;
        this.orderService = orderService;
        this.retetaService = retetaService;
        this.stocService = stocService;
        this.report = report;
    }

    // ---------- BUSINESS OPERATIONS ----------

    public void comandaProdus(Product produs) {

        Reteta reteta = retetaService.findById(produs.getId());

        if (!stocService.areSuficient(reteta)) {
            throw new IllegalStateException(
                    "Stoc insuficient pentru produsul: " + produs.getNume()
            );
        }

        stocService.consuma(reteta);
    }

    public String generateReceipt(Order order) {
        return ReceiptGenerator.generate(order, productService.getAllProducts());
    }

    public double getDailyRevenue() {
        return report.getTotalRevenue();
    }

    public void exportCsv(String path) {
        CsvExporter.exportOrders(
                productService.getAllProducts(),
                orderService.getAllOrders(),
                path
        );
    }
}