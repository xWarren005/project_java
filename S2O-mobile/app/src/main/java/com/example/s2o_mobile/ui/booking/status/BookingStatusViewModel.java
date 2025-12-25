package com.example.s2o_mobile.ui.booking.status;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Reservation;
import com.example.s2o_mobile.data.repository.BookingRepository;

public class BookingStatusViewModel extends ViewModel {

    private final BookingRepository repository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<Reservation> reservation = new MutableLiveData<>(null);
    private final MutableLiveData<String> statusText = new MutableLiveData<>("");

    public BookingStatusViewModel(BookingRepository repository) {
        this.repository = repository;
    }

    public BookingStatusViewModel() {
        this.repository = new BookingRepository();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Reservation> getReservation() {
        return reservation;
    }

    public LiveData<String> getStatusText() {
        return statusText;
    }
}
