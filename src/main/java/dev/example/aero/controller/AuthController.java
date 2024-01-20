package dev.example.aero.controller;

import dev.example.aero.dto.FlightSearchReqDTO;
import dev.example.aero.dto.LoginReqDTO;
import dev.example.aero.dto.RegisterReqDTO;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.security.dto.AuthenticationResponse;
import dev.example.aero.security.service.AuthenticationService;
import dev.example.aero.service.AirportService;
import dev.example.aero.service.PassengerService;
import dev.example.aero.service.TicketService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private AirportService airportService;

    @GetMapping("/login")
    public String showLoginForm (@NotNull Model model) {
        model.addAttribute("loginReqDTO", new LoginReqDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login (@ModelAttribute("LoginReqDTO") @NotNull LoginReqDTO loginReqDTO, Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(loginReqDTO.getUsername(), loginReqDTO.getPassword());
            String passengerName = passengerService.getName(loginReqDTO.getUsername());
            model.addAttribute("passengerName", passengerName);
            System.out.println("Adding the Bearer cookie..." + authenticationResponse.getToken());
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

    @GetMapping("/userLogin")
    public String loginUser (Model model, RedirectAttributes redirectAttributes, HttpServletResponse response, HttpServletRequest request) {
        try {
            System.out.println("In /loginUser" + request.getParameter("redirectUrl"));
//            AuthenticationResponse authenticationResponse = authenticationService.authenticate(loginReqDTO.getUsername(), loginReqDTO.getPassword());
//            String passengerName = passengerService.getName(loginReqDTO.getUsername());
//            model.addAttribute("passengerName", passengerName);
//            System.out.println("Adding the Bearer cookie..." + authenticationResponse.getToken());
//            Cookie cookie = new Cookie("Bearer", authenticationResponse.getToken());
//            response.addCookie(cookie);
            String redirectUrl = request.getParameter("redirectUrl");

            if (redirectUrl != null) {
                return "redirect:" + redirectUrl;
            } else {
                return "login_success";
            }

        } catch (Exception e) {
            e.printStackTrace();
            String errMsg;
            if (e instanceof UsernameNotFoundException) {
                errMsg = "User not found!";
            } else if (e instanceof BadCredentialsException) {
                errMsg = "Bad Credential!";
            } else {
                errMsg = "Internal Error";
            }
            redirectAttributes.addFlashAttribute("errMsg", errMsg);
            return "redirect:/login";
        }
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
        model.addAttribute("passengerName", registerReqDTO.getLastName());
        return "login_success";
    }

    @GetMapping("/logoutUser")
    public String logoutUser(@NotNull HttpServletResponse response, Model model) {
        SecurityContextHolder.clearContext();
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("seatClasses", SeatClassType.getAllSeatClassType());
        model.addAttribute("flightSearchReqDTO", new FlightSearchReqDTO());
        model.addAttribute("highestPassenger", TicketService.HIGHEST_PERMISSIBLE_SEATS);

        return "index";
    }
}
