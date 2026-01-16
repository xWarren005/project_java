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

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<Booking> booking = new MutableLiveData<>(null);
    private final MutableLiveData<String> bookingStatus = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> createBookingOk = new MutableLiveData<>(false);

    private String lastBookingId;

    private Call<Booking> runningCall;

    public BookingViewModel() {
        this.bookingRepository = new BookingRepository();
    }

    public BookingViewModel(@NonNull BookingRepository bookingRepository) {
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

    public LiveData<String> getBookingStatus() {
        return bookingStatus;
    }

    public LiveData<Boolean> getCreateBookingOk() {
        return createBookingOk;
    }

    public String getLastBookingId() {
        return lastBookingId;
    }

    public void createBooking(int restaurantId,
                              @NonNull String bookingTime,
                              int peopleCount,
                              String note) {
        submitBooking(String.valueOf(restaurantId), bookingTime, peopleCount, note);
    }
    public void submitBooking(@NonNull String restaurantId,
                              @NonNull String bookingTime,
                              int peopleCount,
                              String note) {

        String rid = restaurantId.trim();
        String time = bookingTime.trim();
        String n = note == null ? "" : note.trim();

        if (rid.isEmpty()) {
            errorMessage.setValue("Thiếu mã nhà hàng.");
            return;
        }
        if (time.isEmpty()) {
            errorMessage.setValue("Vui lòng chọn thời gian đặt bàn.");
            return;
        }
        if (peopleCount <= 0) {
            errorMessage.setValue("Số người phải lớn hơn 0.");
            return;
        }

        cancelRunningCall();
        loading.setValue(true);
        errorMessage.setValue(null);

        runningCall = bookingRepository.createBooking(rid, time, peopleCount, n);

        if (runningCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tạo yêu cầu đặt bàn.");
            return;
        }

        runningCall.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                loading.setValue(false);

                if (!response.isSuccessful()) {
                    errorMessage.setValue("Đặt bàn thất bại (" + response.code() + ").");
                    return;
                }

                Booking body = response.body();
                booking.setValue(body);

                if (body != null) {
                    lastBookingId = safeId(body.getId());
                    bookingStatus.setValue(safe(body.getStatus()));
                    createBookingOk.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(t.getMessage() == null ? "Lỗi kết nối. Vui lòng thử lại." : t.getMessage());
            }
        });
    }

    public void loadBookingStatus(@NonNull String bookingId) {
        String bid = bookingId.trim();
        if (bid.isEmpty()) {
            errorMessage.setValue("Thiếu mã đặt bàn.");
            return;
        }

        loading.setValue(true);
        runningCall = bookingRepository.getBookingStatus(bookingId);

        if (runningCall == null) {
            loading.setValue(false);
            errorMessage.setValue("Không thể tải trạng thái đặt bàn. Vui lòng kiểm tra cấu hình API.");
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

                Booking body = response.body();
                if (body != null) {
                    bookingStatus.setValue(safe(body.getStatus()));
                    lastBookingId = safeId(body.getId());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {
                loading.setValue(false);
                if (call.isCanceled()) return;
                errorMessage.setValue(t.getMessage() == null ? "Lỗi kết nối. Vui lòng thử lại." : t.getMessage());
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

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String safeId(int id) {
        return String.valueOf(id);
    }

}

