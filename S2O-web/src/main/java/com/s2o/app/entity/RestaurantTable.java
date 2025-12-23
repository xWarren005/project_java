package com.s2o.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "restaurant_tables")
@Data
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "restaurant_id")
    private Integer restaurantId;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "qr_code_string")
    private String qrCodeString;

    // Status: AVAILABLE, OCCUPIED, RESERVED
    @Column(name = "status")
    private String status;
}