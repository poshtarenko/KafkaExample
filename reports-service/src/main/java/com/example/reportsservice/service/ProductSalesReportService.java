package com.example.reportsservice.service;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.common.messaging.models.OrderCreationEvent;
import com.example.reportsservice.domain.ProductSalesReport;
import com.example.reportsservice.repository.ProductSalesReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSalesReportService {

    private final ProductSalesReportRepository productSalesReportRepository;
    private final FileStore fileStore;

    @Value("${amazon.s3.buckets.product-reports}")
    private String productReportsBucket;

    @Transactional(readOnly = true)
    public ProductSalesReport get(String productName) {
        return productSalesReportRepository.findById(productName)
                .orElseThrow(() -> new RuntimeException("Product with name " + productName + " not exists"));
    }

    @Transactional(readOnly = true)
    public byte[] getReportFile(String productName) {
        ProductSalesReport report = get(productName);
        String fileKey = report.getReportFileKey();
        return fileStore.download(productReportsBucket, fileKey);
    }

    @Transactional
    public void updateReport(OrderCreationEvent orderCreationEvent) {
        String productName = orderCreationEvent.getProduct().toString();
        String customer = orderCreationEvent.getCustomerName().toString();

        Optional<ProductSalesReport> reportOpt = productSalesReportRepository.findById(productName);
        ProductSalesReport report;

        if (reportOpt.isPresent()) {
            report = reportOpt.get();
            report.setSalesCount(report.getSalesCount() + 1);
            List<String> reportCustomers = report.getCustomers();
            if (!reportCustomers.contains(customer)) {
                reportCustomers.add(customer);
            }
        } else {
            report = new ProductSalesReport();
            report.setProductName(productName);
            report.setCustomers(List.of(customer));
            report.setSalesCount(1);
            report.setReportFileKey(productName + "-report.pdf");
        }
        InputStream file = createReportFile(report);
        fileStore.upload(productReportsBucket, report.getReportFileKey(), file);

        productSalesReportRepository.save(report);
    }

    @SneakyThrows
    private InputStream createReportFile(ProductSalesReport report) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.TIMES_BOLD, 36);

                String title = "Report for " + report.getProductName().toUpperCase();
                float titleWidth = PDType1Font.TIMES_ROMAN.getStringWidth(title) / 1000f * 36;
                float titleHeight = PDType1Font.TIMES_ROMAN.getFontDescriptor().getFontBoundingBox().getHeight() / 1000f * 36;
                float titleX = (page.getMediaBox().getWidth() - titleWidth) / 2;
                float titleY = page.getMediaBox().getHeight() - 80;

                contentStream.beginText();
                contentStream.newLineAtOffset(titleX, titleY);
                contentStream.showText(title);

                contentStream.newLineAtOffset(-100, -70);
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 24);
                contentStream.showText("Sales count: " + report.getSalesCount());
                contentStream.newLineAtOffset(0, -50);
                contentStream.showText("Customers list:");
                contentStream.newLineAtOffset(0, -30);
                for (String customer : report.getCustomers()) {
                    contentStream.showText("– " + customer);
                    contentStream.newLineAtOffset(0, -30);
                }
                contentStream.endText();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
    }

}
