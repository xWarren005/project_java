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

public class BookingViewModel extends ViewModel {

    private final BookingRepository bookingRepository;

    private final MutableLiveData<Booking> booking =
            new MutableLiveData<>(null);

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>(null);

    private Call<Booking> runningCall;

    public BookingViewModel() {
        bookingRepository = new BookingRepository();
    }

    public LiveData<Booking> getBooking() {
        return booking;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void createBooking(@NonNull Booking newBooking) {
        loading.setValue(true);
        errorMessage.setValue(null);

        runningCall = bookingRepository.createBooking(newBooking);

        runningCall.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call,
                                   @NonNull Response<Booking> response) {
                loading.setValue(false);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Đặt bàn thất bại");
                    return;
                }

                booking.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(
                        t.getMessage() == null
                                ? "Lỗi kết nối"
                                : t.getMessage()
                );
            }
        });
    }

    public void loadBookingStatus(@NonNull String bookingId) {
        loading.setValue(true);
        errorMessage.setValue(null);

        runningCall = bookingRepository.getBookingStatus(bookingId);

        runningCall.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call,
                                   @NonNull Response<Booking> response) {
                loading.setValue(false);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Không lấy được trạng thái đặt bàn");
                    return;
                }

                booking.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call,
                                  @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(
                        t.getMessage() == null
                                ? "Lỗi kết nối"
                                : t.getMessage()
                );
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
