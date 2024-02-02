package dev.example.aero.controller;

import dev.example.aero.dto.LoginReqDTO;
import dev.example.aero.model.Passenger;
import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.security.dto.AuthenticationResponse;
import dev.example.aero.security.service.AuthenticationService;
import dev.example.aero.service.PassengerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final AuthenticationService authenticationService;
    private final PassengerRepository passengerRepository;
    private final PassengerService passengerService;

    public AuthController(AuthenticationService authenticationService, PassengerRepository passengerRepository, PassengerService passengerService) {
        this.authenticationService = authenticationService;
        this.passengerRepository = passengerRepository;
        this.passengerService = passengerService;
    }

    @GetMapping("/login")
    public String showLoginForm (@NotNull Model model) {
        model.addAttribute("loginReqDTO", new LoginReqDTO());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser (@NotNull LoginReqDTO loginReqDTO, Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(loginReqDTO.getUsername(), loginReqDTO.getPassword());
            String passengerName = passengerRepository.getLastNameByContactOrEmail(loginReqDTO.getUsername());
            model.addAttribute("passengerName", passengerName);

            Cookie cookie = new Cookie("Bearer", authenticationResponse.getToken());
            response.addCookie(cookie);

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
        model.addAttribute("passenger", new Passenger());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid Passenger passenger, @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/register";
        }
        try {
            passengerService.insertPassenger(passenger);
        } catch (DataIntegrityViolationException e) {
            return "redirect:/register";
        }
        model.addAttribute("passengerName", passenger.getLastName());
        return "login_success";
    }
}
