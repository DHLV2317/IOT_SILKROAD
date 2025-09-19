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
}