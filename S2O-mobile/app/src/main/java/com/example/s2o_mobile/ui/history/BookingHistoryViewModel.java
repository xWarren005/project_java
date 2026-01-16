package com.example.s2o_mobile.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Booking;

import java.util.Collections;
import java.util.List;

public class BookingHistoryViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<List<Booking>> bookings = new MutableLiveData<>(Collections.emptyList());

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Booking>> getBookings() {
        return bookings;
    }

    public void loadBookingHistory() {
        loading.setValue(true);
        errorMessage.setValue(null);

        bookings.setValue(Collections.emptyList());
        loading.setValue(false);
    }
}