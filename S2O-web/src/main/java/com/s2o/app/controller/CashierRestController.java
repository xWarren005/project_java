package com.s2o.app.controller;

import com.s2o.app.dto.response.CashierTableDTO;
import com.s2o.app.service.CashierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.s2o.app.dto.response.CashierInvoiceDTO;

import java.util.List;

@RestController
@RequestMapping("/api/cashier")
public class CashierRestController {

    @Autowired
    private CashierService cashierService;

    @GetMapping("/tables")
    public ResponseEntity<List<CashierTableDTO>> getTables() {
        // TODO: Lấy restaurantId từ User đang đăng nhập
        Integer restaurantId = 1;
        return ResponseEntity.ok(cashierService.getTablesForCashier(restaurantId));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<CashierInvoiceDTO>> getAllInvoices() {
        List<CashierInvoiceDTO> invoices = cashierService.getInvoicesForCashier();
        return ResponseEntity.ok(invoices);
    }

}