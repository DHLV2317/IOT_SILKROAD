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

        // ✅ Guía aprobado (para demo rápida)
        User approvedGuide = new User("Guide Demo", "guide@demo.com", "123456", User.Role.GUIDE);
        approvedGuide.setGuideApproved(true);
        approvedGuide.setGuideApprovalStatus("APPROVED");
        users.put("guide@demo.com", approvedGuide);

        // ✅ Guía PENDIENTE de aprobación (tu caso de uso)
        User pendingGuide = new User("Carlos Mendoza", "carlos.guia@demo.com", "123456", User.Role.GUIDE);
        pendingGuide.setLastName("Mendoza López");
        pendingGuide.setDocumentType("DNI");
        pendingGuide.setDocumentNumber("12345678");
        pendingGuide.setBirthDate("15/08/1985");
        pendingGuide.setPhone("+51 987 654 321");
        pendingGuide.setAddress("Av. Arequipa 1234, Miraflores, Lima");
        pendingGuide.setPhotoUri("content://fake/photo/carlos.jpg");
        pendingGuide.setLanguages("Español, Inglés, Quechua");
        pendingGuide.setGuideApproved(false); // ❌ NO APROBADO
        pendingGuide.setGuideApprovalStatus("PENDING"); // ⏳ PENDIENTE
        users.put("carlos.guia@demo.com", pendingGuide);

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

    // ===== MÉTODOS ESPECÍFICOS PARA GUÍAS =====
    
    /** Registra un guía con todos sus datos específicos (pendiente de aprobación) */
    public boolean registerGuide(String names, String lastNames, String documentType, 
                                String documentNumber, String birthDate, String email, 
                                String phone, String address, String photoUri, String languages) {
        
        if (exists(email)) return false; // Email ya existe
        
        // Crear nuevo usuario guía con datos completos
        User guide = new User(names, email, "123456", User.Role.GUIDE); // Password temporal
        guide.setLastName(lastNames);
        guide.setDocumentType(documentType);
        guide.setDocumentNumber(documentNumber);
        guide.setBirthDate(birthDate);
        guide.setPhone(phone);
        guide.setAddress(address);
        guide.setPhotoUri(photoUri);
        guide.setLanguages(languages);
        guide.setGuideApproved(false); // Pendiente de aprobación
        guide.setGuideApprovalStatus("PENDING");
        
        // Guardamos directamente (sin código de verificación para demo)
        users.put(email.toLowerCase(), guide);
        return true;
    }
    
    /** Para que el SuperAdmin apruebe/rechace guías */
    public boolean approveGuide(String email, boolean approved) {
        User guide = users.get(email.toLowerCase());
        if (guide == null || guide.getRole() != User.Role.GUIDE) return false;
        
        guide.setGuideApproved(approved);
        guide.setGuideApprovalStatus(approved ? "APPROVED" : "REJECTED");
        return true;
    }
    
    /** Obtener todos los guías pendientes de aprobación */
    public java.util.List<User> getPendingGuides() {
        java.util.List<User> pendingGuides = new java.util.ArrayList<>();
        for (User user : users.values()) {
            if (user.getRole() == User.Role.GUIDE && 
                "PENDING".equals(user.getGuideApprovalStatus())) {
                pendingGuides.add(user);
            }
        }
        return pendingGuides;
    }
}