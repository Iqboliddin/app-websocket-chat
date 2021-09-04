package uz.paynet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.paynet.entity.User;
import uz.paynet.payload.ApiResponse;
import uz.paynet.payload.LoginDto;
import uz.paynet.payload.RegisterDto;
import uz.paynet.repository.UserRepository;
import uz.paynet.security.JwtProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;

    private String sendingEmail;

    private String emailCode;


    public ApiResponse registerUser(RegisterDto registerDto) {
        boolean existsByEmail = userRepository.existsByEmail(registerDto.getEmail());

        if (!registerDto.getPassword().equals(registerDto.getPrePassword()))
            return new ApiResponse("Password prepassword bilan bir xil emas", false);
        if (existsByEmail)
            return new ApiResponse("Bunday emaildagi foydalanuvchi allaqachon mavjud", false);

        User newUser = new User();
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setPrePassword(passwordEncoder.encode(registerDto.getPrePassword()));
        newUser.setEmailCode(UUID.randomUUID().toString());
        userRepository.save(newUser);
        sendEmail(newUser.getEmail(), newUser.getEmailCode());

        return new ApiResponse("Muvaffaqiyatli royxatdab otdingiz. Chatga qoshilish uchun " + newUser.getEmail() + " Emailingizni tasdiqlang", true);

    }

    public void sendEmail(String sendingEmail, String emailCode) {
        this.sendingEmail = sendingEmail;
        this.emailCode = emailCode;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ChatUz@gmail.com");
            message.setTo(sendingEmail);
            message.setSubject("Chatga qoshilish uchun");
            message.setText("<a href='http://localhost:8080/api/chat/confirmationEmail?emailCode=" + emailCode + "&email=" + sendingEmail + "'>Tasdiqlang</a>");
            javaMailSender.send(message);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    public ApiResponse confirmationEmail(String emailCode, String email) {
        Optional<User> byEmailAndEmailCode = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (byEmailAndEmailCode.isPresent()) {
            User user = byEmailAndEmailCode.get();
            user.setOnline(true);
            user.setEmailCode(null);
            userRepository.save(user);
            return new ApiResponse("Akkount tasdiqlandi. Postmandan tokenni olib, chatga qoshilishingiz mumkin", true);
        }
        return new ApiResponse("Akkount allaqachon tasdiqlangan", false);

    }

    public ApiResponse login(LoginDto loginDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword()
            ));

            String token = jwtProvider.generateToken(loginDto.getEmail());

            return new ApiResponse("Token:", true, token);
        } catch (
                Exception e) {
            return new ApiResponse("Login yoki parol xato", false);
        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public ApiResponse getOnlineUser() {
        List<User> onlineUser = new ArrayList<>();
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            if (!user.isOnline()) {
                continue;
            }
            onlineUser.add(user);
        }
        if (!onlineUser.isEmpty())
            return new ApiResponse("Online userlar listi: ", true, onlineUser);

        return new ApiResponse("hozircha online userlar mavjud emas", false);
    }
}
