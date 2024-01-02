package dev.example.aero.viewcontroller;

import dev.example.aero.dto.LoginReqDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String showLoginForm (@ModelAttribute("LoginReqDTO") LoginReqDTO loginReqDTO, Model model) {
        model.addAttribute("loginReqDTO", new LoginReqDTO());
        return "login";
    }
}
