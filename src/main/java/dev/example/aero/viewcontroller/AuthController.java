package dev.example.aero.viewcontroller;

import dev.example.aero.dto.LoginReqDTO;
import dev.example.aero.security.dto.AuthenticationResponse;
import dev.example.aero.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;
    @GetMapping("/login")
    public String showLoginForm (@NotNull Model model) {
        model.addAttribute("loginReqDTO", new LoginReqDTO());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser (@ModelAttribute("LoginReqDTO") @NotNull LoginReqDTO loginReqDTO, Model model, RedirectAttributes redirectAttributes) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(loginReqDTO.getUsername(), loginReqDTO.getPassword());
            System.out.println(authenticationResponse.getToken());
        } catch (Exception e) {
            String errMsg;
            if (e instanceof UsernameNotFoundException) {
                errMsg = "User not found!";
            } else if (e instanceof BadCredentialsException) {
                errMsg = "Bad Credential!";
            } else {
                errMsg = "Internal Error";
            }
            System.out.println(errMsg);
            redirectAttributes.addFlashAttribute("errMsg", errMsg);
            return "redirect:/login";
        }
        return "no_flights";
    }
}
