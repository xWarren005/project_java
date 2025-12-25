package com.example.s2o_mobile.ui.booking.status;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Reservation;
import com.example.s2o_mobile.data.repository.BookingRepository;
import com.example.s2o_mobile.data.repository.RepositoryCallback;

public void loadStatus(int reservationId) {
    loading.setValue(true);
    error.setValue(null);

    repository.getReservationById(reservationId, new RepositoryCallback<Reservation>() {
        @Override
        public void onSuccess(Reservation data) {
            loading.postValue(false);
            reservation.postValue(data);
            statusText.postValue(extractStatus(data));
        }

        @Override
        public void onError(String message) {
            loading.postValue(false);
            error.postValue(message == null
                    ? "Khong lay duoc trang thai dat ban"
                    : message);
        }
    });
}


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

    public void loadStatus(int reservationId) {
        loading.setValue(true);
        error.setValue(null);

        repository.getReservationById(reservationId, new RepositoryCallback<Reservation>() {
            @Override
            public void onSuccess(Reservation data) {
                loading.postValue(false);
                reservation.postValue(data);
                statusText.postValue(extractStatus(data));
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message == null
                        ? "Khong lay duoc trang thai dat ban"
                        : message);
            }
        });
    }

    public void cancelBooking(int reservationId) {
        loading.setValue(true);
        error.setValue(null);

        repository.cancelReservation(reservationId, new RepositoryCallback<Reservation>() {
            @Override
            public void onSuccess(Reservation data) {
                loading.postValue(false);
                reservation.postValue(data);
                statusText.postValue(extractStatus(data));
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message == null
                        ? "Huy dat ban that bai"
                        : message);
            }
        });
    }

    private String extractStatus(Reservation r) {
        if (r == null) return "";

        try {
            Object val = r.getClass().getMethod("getStatus").invoke(r);
            return val == null ? "" : String.valueOf(val);
        } catch (Exception ignore) {}

        try {
            Object val = r.getClass().getMethod("getBookingStatus").invoke(r);
            return val == null ? "" : String.valueOf(val);
        } catch (Exception ignore) {}

        return "";
    }


}
