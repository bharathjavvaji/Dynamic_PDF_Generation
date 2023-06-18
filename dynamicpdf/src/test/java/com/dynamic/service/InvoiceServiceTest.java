package com.dynamic.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import com.dynamic.model.InvoiceData;
import com.dynamic.model.Item;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    public void testdownloadHeaders() {
        String fileName = "XPL_VC_P1.pdf";
        HttpHeaders headers = invoiceService.downloadHeaders(fileName);

        Assert.assertNotNull(headers);
        Assert.assertEquals(1, headers.size());
        Assert.assertEquals("attachment; filename=XPL_VC_P1.pdf", headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    public void testfileName() {
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setSeller("XYZ Pvt. Ltd.");
        invoiceData.setBuyer("Vedant Computers");
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setName("Product 1");
        items.add(item1);
        Item item2 = new Item();
        item2.setName("Product 2");
        items.add(item2);
        invoiceData.setItems(items);

        String fileName = invoiceService.fileName(invoiceData);

        Assert.assertEquals("XPL_VC_P1_P2.pdf", fileName);
    }

    @Test
    public void testFirstChar() {
        String name = "Product 1";
        String gcode = invoiceService.firstChar(name);

        Assert.assertEquals("P1", gcode);
    }

}

