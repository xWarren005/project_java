package com.example.s2o_mobile.ui.restaurant.recommend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Restaurant;
import com.example.s2o_mobile.data.repository.RepositoryCallback;
import com.example.s2o_mobile.data.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecommendViewModel extends ViewModel {

    private final RestaurantRepository repository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<List<Restaurant>> recommendedRestaurants = new MutableLiveData<>(new ArrayList<>());

    public RecommendViewModel(RestaurantRepository repository) {
        this.repository = repository;
    }

    public RecommendViewModel() {
        this.repository = new RestaurantRepository();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Restaurant>> getRecommendedRestaurants() {
        return recommendedRestaurants;
    }

    public void loadTopRated(int limit) {
        loading.setValue(true);
        error.setValue(null);

        repository.getAllRestaurants(new RepositoryCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> data) {
                loading.postValue(false);

                List<Restaurant> list = (data == null) ? new ArrayList<>() : new ArrayList<>(data);

                Collections.sort(list, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant a, Restaurant b) {
                        double rb = ratingOf(b);
                        double ra = ratingOf(a);
                        return Double.compare(rb, ra);
                    }
                });

                recommendedRestaurants.postValue(takeTop(list, limit));
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message == null ? "Khong lay duoc du lieu nha hang" : message);
            }
        });
    }


    public void loadByKeyword(String keyword, int limit) {
        loading.setValue(true);
        error.setValue(null);

        final String key = keyword == null ? "" : keyword.trim().toLowerCase();

        repository.getAllRestaurants(new RepositoryCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> data) {
                loading.postValue(false);

                List<Restaurant> source = (data == null) ? new ArrayList<>() : data;
                List<Restaurant> filtered = new ArrayList<>();

                for (Restaurant r : source) {
                    String name = safe(textTryGetName(r)).toLowerCase();
                    String addr = safe(textTryGetAddress(r)).toLowerCase();

                    if (key.isEmpty() || name.contains(key) || addr.contains(key)) {
                        filtered.add(r);
                    }
                }

                Collections.sort(filtered, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant a, Restaurant b) {
                        return Double.compare(ratingOf(b), ratingOf(a));
                    }
                });

                recommendedRestaurants.postValue(takeTop(filtered, limit));
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message == null ? "Khong lay duoc du lieu nha hang" : message);
            }
        });
    }

    private List<Restaurant> takeTop(List<Restaurant> list, int limit) {
        if (list == null) return new ArrayList<>();
        if (limit <= 0 || limit >= list.size()) return list;
        return new ArrayList<>(list.subList(0, limit));
    }

    private double ratingOf(Restaurant r) {
        if (r == null) return 0.0;

        try {
            // Thu getAvgRating()
            return (double) r.getClass().getMethod("getAvgRating").invoke(r);
        } catch (Exception ignore) {}

        try {
            // Thu getAvg_rating()
            Object val = r.getClass().getMethod("getAvg_rating").invoke(r);
            if (val instanceof Number) return ((Number) val).doubleValue();
        } catch (Exception ignore) {}

        return 0.0;
    }

    private String textTryGetName(Restaurant r) {
        if (r == null) return "";
        try {
            Object val = r.getClass().getMethod("getName").invoke(r);
            return val == null ? "" : String.valueOf(val);
        } catch (Exception e) {
            return "";
        }
    }

    private String textTryGetAddress(Restaurant r) {
        if (r == null) return "";
        try {
            Object val = r.getClass().getMethod("getAddress").invoke(r);
            return val == null ? "" : String.valueOf(val);
        } catch (Exception e) {
            return "";
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
