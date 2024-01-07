package dev.example.aero.viewcontroller;

import dev.example.aero.dto.LoginReqDTO;
import dev.example.aero.dto.RegisterReqDTO;
import dev.example.aero.security.dto.AuthenticationResponse;
import dev.example.aero.security.service.AuthenticationService;
import dev.example.aero.service.PassengerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PassengerService passengerService;
    @GetMapping("/login")
    public String showLoginForm (@NotNull Model model) {
        model.addAttribute("loginReqDTO", new LoginReqDTO());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser (@ModelAttribute("LoginReqDTO") @NotNull LoginReqDTO loginReqDTO, Model model, RedirectAttributes redirectAttributes) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(loginReqDTO.getUsername(), loginReqDTO.getPassword());
            String passengerName = passengerService.getName(loginReqDTO.getUsername());
            model.addAttribute("passengerName", passengerName);
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
        return "login_success";
    }

    @GetMapping("/register")
    public String showRegisterForm(@NotNull Model model) {
        model.addAttribute("registerReqDTO", new RegisterReqDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerReqDTO") @Valid RegisterReqDTO registerReqDTO, @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/register";
        }
        System.out.println(registerReqDTO.toString());
        model.addAttribute("passengerName", registerReqDTO.getLastName());
        return "login_success";
    }
}
