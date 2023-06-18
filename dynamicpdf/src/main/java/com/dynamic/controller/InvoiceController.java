package com.dynamic.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.dynamic.model.InvoiceData;
import com.dynamic.service.InvoiceService;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

	private static final String TEMPLATE_NAME = "invoice";
	private static final String PDF_DIRECTORY = "D:\\pdfs";

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	private InvoiceService invoiceService;

	@PostMapping
	public ResponseEntity<?> generatePDF(@RequestBody InvoiceData invoiceData) throws IOException {
		if (invoiceData.getSeller() == null || invoiceData.getSellerGstin() == null || invoiceData.getSellerAddress() == null
		        || invoiceData.getBuyer() == null || invoiceData.getBuyerGstin() == null || invoiceData.getBuyerAddress() == null
		        || invoiceData.getItems() == null) {
			 //System.out.println(invoiceData.toString());
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One or more fields in the request body are null");
		}
		String pdfFileName = invoiceService.fileName(invoiceData);
		Path pdfFilePath = Paths.get(PDF_DIRECTORY, pdfFileName);

		if (Files.exists(pdfFilePath)) {
			FileSystemResource resource = new FileSystemResource(pdfFilePath.toFile());

			return ResponseEntity.ok()
					.headers(invoiceService.downloadHeaders(pdfFileName))
					.contentType(MediaType.APPLICATION_PDF)
					.contentLength(resource.contentLength())
					.body(new InputStreamResource(resource.getInputStream()));
		} else {
		    Map<String, Object> data = new HashMap<>();
		    data.put("invoice", invoiceData);
		    Context context = new Context();
		    context.setVariables(data);
		    String renderedHtmlContent = templateEngine.process(TEMPLATE_NAME,context);
			byte[] pdfBytes = invoiceService.pdfToHtml(renderedHtmlContent);

			// Save 
			Files.createDirectories(pdfFilePath.getParent());
			Files.write(pdfFilePath, pdfBytes);

			// Return 
			InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

			return ResponseEntity.ok()
					.headers(invoiceService.downloadHeaders(pdfFileName))
					.contentType(MediaType.APPLICATION_PDF)
					.contentLength(pdfBytes.length)
					.body(resource);
		}
	}

}
