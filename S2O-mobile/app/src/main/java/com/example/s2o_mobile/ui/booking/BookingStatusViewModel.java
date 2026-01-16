package com.example.s2o_mobile.ui.booking;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.s2o_mobile.data.model.Booking;
import com.example.s2o_mobile.data.repository.BookingRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingStatusViewModel extends ViewModel {

    private final BookingRepository bookingRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<Booking> booking = new MutableLiveData<>(null);

    private Call<Booking> runningCall;

    public BookingStatusViewModel() {
        this.bookingRepository = new BookingRepository();
    }

    public BookingStatusViewModel(@NonNull BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
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

    public void loadBookingStatus(int bookingId) {
        if (bookingId <= 0) {
            errorMessage.setValue("Thiếu mã đặt bàn.");
            return;
        }
        loadBookingStatus(String.valueOf(bookingId));
    }

    public void loadBookingStatus(@NonNull String bookingId) {
        String bid = bookingId.trim();
        if (bid.isEmpty()) {
            errorMessage.setValue("Thiếu mã đặt bàn.");
            return;
        }

        cancelRunningCall();
        loading.setValue(true);
        errorMessage.setValue(null);

        runningCall = bookingRepository.getBookingStatus(bid);
        if (runningCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tải trạng thái đặt bàn.");
            return;
        }

        runningCall.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                loading.setValue(false);
                if (!response.isSuccessful()) {
                    errorMessage.setValue("Tải trạng thái thất bại (" + response.code() + ").");
                    return;
                }
                booking.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                String msg = t.getMessage();
                errorMessage.setValue(msg == null ? "Lỗi kết nối. Vui lòng thử lại." : msg);
            }
        });
    }

    private void cancelRunningCall() {
        if (runningCall != null) {
            runningCall.cancel();
            runningCall = null;
        }
    }

    @Override
    protected void onCleared() {
        cancelRunningCall();
        super.onCleared();
    }
}
