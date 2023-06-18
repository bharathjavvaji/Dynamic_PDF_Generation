package com.dynamic.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.dynamic.model.InvoiceData;
import com.dynamic.model.Item;
import com.dynamic.service.InvoiceService;

@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

	@Mock
	private SpringTemplateEngine templateEngine;

	@MockBean
	private InvoiceService invoiceService;

	@InjectMocks
	private InvoiceController invoiceController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testPDF() throws IOException {
		InvoiceData invoiceData = new InvoiceData();
		invoiceData.setSeller("XYZ Pvt. Ltd.");
		invoiceData.setSellerGstin("29AABBCCDD121ZD");
		invoiceData.setSellerAddress("New Delhi, India");
		invoiceData.setBuyer("Vedant Computers");
		invoiceData.setBuyerGstin("29AABBCCDD131ZD");
		invoiceData.setBuyerAddress("New Delhi, India");

		List<Item> items = new ArrayList<>();
		Item item = new Item();
		item.setName("Product 1");
		item.setQuantity("12 Nos");
		item.setRate(123.00);
		item.setAmount(1476.00);
		items.add(item);

		invoiceData.setItems(items);

		String expectedFileName = "XPL_VC_P1.pdf";
		when(invoiceService.fileName(any(InvoiceData.class))).thenReturn(expectedFileName);
		when(invoiceService.pdfToHtml(anyString())).thenReturn(new byte[0]);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", expectedFileName);
		when(invoiceService.downloadHeaders(eq(expectedFileName))).thenReturn(headers);

		ResponseEntity<?> response = invoiceController.generatePDF(invoiceData);

		assertEquals(200, response.getStatusCodeValue());
		assertNotNull(response.getBody());

		MediaType expectedContentType = MediaType.APPLICATION_PDF;
		MediaType actualContentType = response.getHeaders().getContentType();
		assertEquals(expectedContentType, actualContentType);

		String actualFileName = response.getHeaders().getContentDisposition().getFilename();
		//System.out.println(actualFileName);
		assertEquals(expectedFileName, actualFileName);

	}

}
