public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> loginOk = new MutableLiveData<>(false);
    private final MutableLiveData<User> userLive = new MutableLiveData<>(null);

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
        sessionManager = new SessionManager(application);
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getMessage() { return message; }
    public LiveData<Boolean> getLoginOk() { return loginOk; }
    public LiveData<User> getUserLive() { return userLive; }
}
