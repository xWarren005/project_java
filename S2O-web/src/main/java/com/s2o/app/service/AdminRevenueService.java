package com.s2o.app.service;

import com.s2o.app.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminRevenueService {

    private List<TransactionDTO> transactionList = new ArrayList<>();

    public AdminRevenueService() {
        // Mock Data: Khớp với dữ liệu mẫu trong file JS
        transactionList.add(new TransactionDTO(1L, "2024-01-15", "Phở 24", "Premium", 299.0, 45.0));
        transactionList.add(new TransactionDTO(2L, "2024-01-15", "Sushi World", "Enterprise", 599.0, 90.0));
        transactionList.add(new TransactionDTO(3L, "2024-01-14", "BBQ House", "Basic", 99.0, 15.0));
        transactionList.add(new TransactionDTO(4L, "2024-01-14", "Vegan Garden", "Premium", 299.0, 45.0));
        transactionList.add(new TransactionDTO(5L, "2024-01-13", "Pizza Express", "Basic", 99.0, 15.0));
        transactionList.add(new TransactionDTO(6L, "2024-01-12", "Burger King", "Enterprise", 599.0, 90.0));
        transactionList.add(new TransactionDTO(7L, "2024-01-11", "Kichi Kichi", "Premium", 299.0, 45.0));
        transactionList.add(new TransactionDTO(8L, "2024-01-10", "The Coffee House", "Basic", 99.0, 15.0));
    }

    // 1. Lấy danh sách giao dịch
    public List<TransactionDTO> getAllTransactions() {
        return transactionList;
    }

    // 2. Doanh thu hôm nay (Giả lập số liệu)
    public String getTodayRevenue() {
        return "$2,847";
    }

    // 3. Doanh thu tháng này
    public String getMonthRevenue() {
        return "$48,392";
    }

    // 4. Dự đoán doanh thu tháng sau
    public String getPredictedRevenue() {
        return "$54,200";
    }
}