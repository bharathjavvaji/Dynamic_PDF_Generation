package com.dynamic.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.dynamic.model.InvoiceData;
import com.dynamic.model.Item;
import com.itextpdf.html2pdf.HtmlConverter;

@Service
public class InvoiceService {
	public HttpHeaders downloadHeaders(String fileName) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		return headers;
	}

	public String fileName(InvoiceData invoiceData) {
		StringBuilder fileNameBuilder = new StringBuilder();
		fileNameBuilder.append(firstChar(invoiceData.getSeller()));
		fileNameBuilder.append("_");
		fileNameBuilder.append(firstChar(invoiceData.getBuyer()));
		fileNameBuilder.append("_");
		for (Item item : invoiceData.getItems()) {
			fileNameBuilder.append(firstChar(item.getName()));
			fileNameBuilder.append("_");
		}
		fileNameBuilder.deleteCharAt(fileNameBuilder.length() - 1);
		fileNameBuilder.append(".pdf");
		return fileNameBuilder.toString();
	}

	public String firstChar(String name) {
		StringBuilder sb = new StringBuilder();
		String[] words = name.split(" ");
		for (String word : words) {
			sb.append(Character.toUpperCase(word.charAt(0)));
		}
		return sb.toString();
	}

	public byte[] pdfToHtml(String html) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		HtmlConverter.convertToPdf(html, outputStream);
		return outputStream.toByteArray();
	}
}
