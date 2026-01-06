package com.s2o.app.service;

import com.s2o.app.dto.RestaurantDTO;
import com.s2o.app.entity.Restaurant;
import com.s2o.app.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminRestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll().stream().map(r -> new RestaurantDTO(
                r.getId(),
                r.getName(),
                r.getAddress(),
                r.getApprovalStatus() != null ? r.getApprovalStatus() : "UNKNOWN",
                r.getRating() != null ? r.getRating() : 0.0
        )).collect(Collectors.toList());
    }

    public long getTotalCount() { return restaurantRepository.count(); }

    public long getPendingCount() {
        return restaurantRepository.countByApprovalStatus("PENDING");
    }

    public double getAvgRating() {
        List<Restaurant> list = restaurantRepository.findAll();
        if (list.isEmpty()) return 0.0;
        double sum = list.stream().mapToDouble(r -> r.getRating() != null ? r.getRating() : 0.0).sum();
        return Math.round((sum / list.size()) * 10.0) / 10.0;
    }
}