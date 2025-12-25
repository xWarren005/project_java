package com.s2o.app.dto.response;

import com.s2o.app.entity.RestaurantTable;
import lombok.Data;

@Data
public class TableDTO {
    private Integer id;
    private String name;
    private Integer seats;
    private Integer status; // Frontend cần số (0, 1, 2)

    public static TableDTO fromEntity(RestaurantTable table) {
        TableDTO dto = new TableDTO();
        dto.setId(table.getId());
        dto.setName(table.getTableName());
        dto.setSeats(table.getCapacity());

        // CHUYỂN ĐỔI: Enum -> Số (để Frontend hiển thị đúng màu)
        if (table.getStatus() == RestaurantTable.TableStatus.AVAILABLE) dto.setStatus(0);
        else if (table.getStatus() == RestaurantTable.TableStatus.OCCUPIED) dto.setStatus(1);
        else dto.setStatus(2); // RESERVED

        return dto;
    }
}