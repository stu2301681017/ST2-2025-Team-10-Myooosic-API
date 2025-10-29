package io.github.stu2301681017.MyooosicAPI.app.auth;

import io.github.stu2301681017.MyooosicAPI.app.user.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AuthService {

    private final HttpSession session;
    private final RegistrationRepository registrationRepository;

    public AuthService(
            HttpSession session,
            RegistrationRepository registrationRepository
    ) {
        this.session = session;
        this.registrationRepository = registrationRepository;
    }

    public void login(@Valid @NotNull LoginRequest request) {

        Registration registration = registrationRepository.findByName(request.name());

        if (registration == null) {
            throw new UserNotFoundException("'" + request.name() + "' is not a registered username");
        }

        if (!verify(
                request.password(),
                Base64.getDecoder().decode(registration.getPasswordSha256Base64()),
                Base64.getDecoder().decode(registration.getPasswordSaltBase64())
        )) {
            throw new WrongPasswordException("The given password did not match the stored hash.");
        }

        session.setAttribute("userId", registration.getName());

    }

    public void register(@Valid @NotNull RegisterRequest request) {

        if (registrationRepository.findByName(request.name()) != null) {
            throw new UsernameTakenException("'" + request.name() + "' is taken");
        }

        byte[] salt = generateSalt(16);
        Registration newRegistration = new Registration();
        newRegistration.setName(request.name());
        newRegistration.setPasswordSha256Base64(Base64.getEncoder().encodeToString(encrypt(request.password(), salt)));
        newRegistration.setPasswordSaltBase64(Base64.getEncoder().encodeToString(salt));

        User newUser = new User();
        newUser.setRegister(newRegistration);
        newRegistration.setUser(newUser);

        registrationRepository.save(newRegistration);
    }

    public void logoff() {
        session.invalidate();
    }

     String getLoggedUserId() throws NotLoggedInException {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new NotLoggedInException("User is not logged in");
        }
        return userId.toString();
    }

    private boolean verify(String secret, byte[] hash, byte[] salt) {
        byte[] newHash = encrypt(secret, salt);
        return constantTimeEquals(newHash, hash);
    }

    private byte[] encrypt(String secret, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generateSalt(int lengthBytes) {
        byte[] salt = new byte[lengthBytes];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
