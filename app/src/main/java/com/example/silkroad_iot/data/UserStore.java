package com.example.silkroad_iot.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserStore {
    private static final UserStore I = new UserStore();
    public static UserStore get(){ return I; }

    private final Map<String, User> users   = new HashMap<>();
    private final Map<String, User> pending = new HashMap<>();     // usuarios aún no verificados
    private final Map<String, String> regCodes = new HashMap<>();  // códigos por email
    private final Random rng = new Random();

    private User logged;

    private UserStore() {
        // ✅ Usuarios semilla por rol (pass: 123456)
        users.put("client@demo.com",
                new User("Cliente Demo", "client@demo.com", "123456", User.Role.CLIENT));

        users.put("guide@demo.com",
                new User("Guide Demo", "guide@demo.com", "123456", User.Role.GUIDE));

        users.put("admin@demo.com",
                new User("Administrador Demo", "admin@demo.com", "123456", User.Role.ADMIN));

        users.put("superadmin@demo.com",
                new User("SuperAdmin Demo", "superadmin@demo.com", "123456", User.Role.SUPERADMIN));
    }

    // ===== Registro con código =====
    public boolean exists(String email){ return users.containsKey(email.toLowerCase()); }

    public String startRegistration(User u){
        String key = u.getEmail().toLowerCase();
        pending.put(key, u);
        String code = fourDigits();
        regCodes.put(key, code);
        return code;
    }

    public String resendRegistrationCode(String email){
        String key = email.toLowerCase();
        if (!pending.containsKey(key)) return null;
        String code = fourDigits();
        regCodes.put(key, code);
        return code;
    }

    public boolean verifyRegistrationCode(String email, String code){
        String key = email.toLowerCase();
        return code != null && code.equals(regCodes.get(key));
    }

    public boolean finalizeRegistration(String email){
        String key = email.toLowerCase();
        User u = pending.remove(key);
        if (u == null) return false;
        users.put(key, u);
        regCodes.remove(key);
        return true;
    }

    private String fourDigits(){ return String.format("%04d", rng.nextInt(10000)); }

    // ===== Login / sesión =====
    public boolean login(String email, String pass){
        User u = users.get(email.toLowerCase());
        if (u != null && u.getPassword().equals(pass)) { logged = u; return true; }
        return false;
    }

    public User getLogged(){ return logged; }
    public void logout(){ logged = null; }

    /** Guardar cambios en el usuario logueado (p.ej., al confirmar onboarding) */
    public void updateLogged(User updated){
        users.put(updated.getEmail().toLowerCase(), updated);
        logged = updated;
    }
}