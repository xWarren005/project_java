package com.example.s2o_mobile.ui.booking;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Booking;
import com.example.s2o_mobile.data.repository.BookingRepository;

public class BookingViewModel extends ViewModel {

    private final BookingRepository bookingRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<Booking> booking = new MutableLiveData<>(null);
    private final MutableLiveData<String> bookingStatus = new MutableLiveData<>("");

    private String lastBookingId;

    public BookingViewModel() {
        this.bookingRepository = new BookingRepository();
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Booking> getBooking() {
        return booking;
    }

    public LiveData<String> getBookingStatus() {
        return bookingStatus;
    }

    public void submitBooking(@NonNull String restaurantId,
                              @NonNull String bookingTime,
                              int peopleCount,
                              String note) {
        loading.setValue(true);
        errorMessage.setValue(null);
    }

    public void loadBookingStatus(@NonNull String bookingId) {
        loading.setValue(true);
        errorMessage.setValue(null);
    }

    public String getLastBookingId() {
        return lastBookingId;
    }
}
