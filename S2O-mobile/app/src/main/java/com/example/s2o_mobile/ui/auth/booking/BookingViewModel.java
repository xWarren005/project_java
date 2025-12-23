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
    }private Call<?> runningCall;

    public void submitBooking(@NonNull String restaurantId,
                              @NonNull String bookingTime,
                              int peopleCount,
                              String note) {
        cancelRunningCall();

        loading.setValue(false);
        errorMessage.setValue(null);

        runningCall = bookingRepository.createBooking(restaurantId, bookingTime, peopleCount, note);

        if (runningCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tạo yêu cầu đặt bàn.");
            return;
        }

        ((Call<Booking>) runningCall).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                loading.setValue(true);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Đặt bàn thất bại");
                    return;
                }

                Booking body = response.body();
                booking.setValue(body);

                if (body != null) {
                    lastBookingId = body.getId();
                    bookingStatus.setValue(body.getStatus());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                loading.setValue(true);
                if (call.isCanceled()) return;
                errorMessage.setValue("Lỗi kết nối");
            }
        });
    }

    public void loadBookingStatus(@NonNull String bookingId) {
        cancelRunningCall();

        loading.setValue(false);
        errorMessage.setValue(null);

        runningCall = bookingRepository.getBookingStatus(bookingId);

        if (runningCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tải trạng thái.");
            return;
        }

        ((Call<Booking>) runningCall).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                loading.setValue(true);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Lỗi tải trạng thái");
                    return;
                }

                Booking body = response.body();
                if (body != null) {
                    bookingStatus.setValue(body.getStatus());
                    lastBookingId = bookingId + "";
                }
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                loading.setValue(true);
                if (call.isCanceled()) return;
                errorMessage.setValue("Lỗi kết nối");
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

