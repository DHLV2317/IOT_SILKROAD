package com.example.silkroad_iot.data;

public class User {
    public enum Role { CLIENT, GUIDE, ADMIN, SUPERADMIN }

    private String name;
    private final String email;
    private final String password;
    private Role role;

    // Campos de perfil de cliente (onboarding)
    private String lastName;
    private String phone;
    private String address;
    private String photoUri;
    private boolean clientProfileCompleted;

    // Campos específicos para GUÍAS
    private String documentType;
    private String documentNumber;
    private String birthDate;
    private String languages;
    private boolean guideApproved = false; // Por defecto no aprobado
    private String guideApprovalStatus = "PENDING"; // PENDING, APPROVED, REJECTED

    // ✔️ Constructor de 3 args (se usa en RegisterActivity)
    public User(String name, String email, String password) {
        this(name, email, password, Role.CLIENT); // por defecto CLIENT
    }

    // ✔️ Constructor de 4 args (para especificar rol explícito)
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = (role == null ? Role.CLIENT : role);
    }

    // ---------------- Getters / Setters ----------------
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public String getEmail(){ return email; }
    public String getPassword(){ return password; }

    public Role getRole(){ return role; }
    public void setRole(Role role){ this.role = role; }

    public String getLastName(){ return lastName; }
    public void setLastName(String v){ this.lastName = v; }

    public String getPhone(){ return phone; }
    public void setPhone(String v){ this.phone = v; }

    public String getAddress(){ return address; }
    public void setAddress(String v){ this.address = v; }

    public String getPhotoUri(){ return photoUri; }
    public void setPhotoUri(String v){ this.photoUri = v; }

    public boolean isClientProfileCompleted(){ return clientProfileCompleted; }
    public void setClientProfileCompleted(boolean v){ this.clientProfileCompleted = v; }

    // Getters/Setters para campos de GUÍA
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public boolean isGuideApproved() { return guideApproved; }
    public void setGuideApproved(boolean guideApproved) { this.guideApproved = guideApproved; }

    public String getGuideApprovalStatus() { return guideApprovalStatus; }
    public void setGuideApprovalStatus(String guideApprovalStatus) { this.guideApprovalStatus = guideApprovalStatus; }
}