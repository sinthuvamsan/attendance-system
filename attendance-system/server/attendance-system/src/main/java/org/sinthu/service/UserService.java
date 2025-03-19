package org.sinthu.service;

import org.sinthu.model.User;
import org.sinthu.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class UserService {

    private final JwtUtil jwtUtil;
    private static final String FILE_PATH = "users.txt";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, User> users = new HashMap<>();

    @Autowired
    public UserService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostConstruct
    public void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            saveUserToFile("admin", "root");
        }
        readUsersFromFile();
    }

    public Optional<User> validateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public void saveUserToFile(String username, String password) {
        String encryptedPassword = passwordEncoder.encode(password);
        long userId = generateUserId();
        User newUser = new User(userId, username, encryptedPassword);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(newUser.getId() + "," + username + "," + encryptedPassword);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        users.put(username, newUser);
    }

    private void readUsersFromFile() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    long id = Long.parseLong(parts[0]);
                    String username = parts[1];
                    String password = parts[2];
                    users.put(username, new User(id, username, password));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long generateUserId() {
        return System.currentTimeMillis();
    }

    public String authenticateUser(String username, String password) {
        readUsersFromFile();
        User user = users.get(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(username);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}