package com.s2o.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cashier")
public class CashierController {

    // URL: http://localhost:8080/cashier/tables
    @GetMapping("/tables")
    public String showTable() {
        return "cashier/tables";
    }

    // URL: http://localhost:8080/cashier/invoices
    @GetMapping("/invoices")
    public String showInvoices() {
        return "cashier/invoices";
    }
}
