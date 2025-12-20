@Controller
public class AuthController {

    @GetMapping("/login")
    public String userLoginPage() {
        return "user/login"; // Trỏ về file templates/user/login.html
    }
}