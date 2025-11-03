package com.example.silkroad_iot.data;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Modelo Firestore para "tours".
 * - Soporta alias de campos (nombre/name, imagen/imageUrl, precio/price, cantidad_personas/people).
 * - Incluye helpers para UI (displayName, displayImageUrl, etc.).
 * - Soporta paradas embebidas (paradas) o referenciadas (id_paradas).
 * - Incluye lista opcional de servicios (services).
 */
@IgnoreExtraProperties
public class TourFB implements Serializable {

    private String id;

    // Nombres / visual
    private String nombre;          // preferido
    private String name;            // alias
    private String description;

    // Imagen (dos posibles nombres)
    private String imagen;
    private String imageUrl;

    // Precio / personas (alias)
    private double precio;            // preferido
    private Double price;             // alias
    private int    cantidad_personas; // preferido
    private Integer people;           // alias

    // Metadatos
    private String empresaId;
    private String ownerUid;        // por si filtras por dueño
    private String ciudad;          // para filtro/búsqueda
    private String langs;           // "Español/Inglés"
    private String duration;
    private String assignedGuideName;
    private Double paymentProposal;

    // Fechas
    private Date dateFrom;
    private Date dateTo;

    // Paradas
    private List<ParadaFB> paradas;     // EMBEBIDAS en el documento
    private List<String>   id_paradas;  // REFERENCIAS (IDs en otra colección)

    // Servicios opcionales (para chips)
    private List<ServiceFB> services;

    public TourFB() {}

    // --- ID ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // --- Nombre / alias ---
    @PropertyName("nombre") public String getNombre() { return nombre; }
    @PropertyName("nombre") public void setNombre(String nombre) { this.nombre = nombre; }
    @PropertyName("name")   public String getName() { return name; }
    @PropertyName("name")   public void setName(String name) { this.name = name; }

    // --- Descripción ---
    @PropertyName("description") public String getDescription() { return description; }
    @PropertyName("description") public void setDescription(String description) { this.description = description; }

    // --- Imagen / alias ---
    @PropertyName("imagen")   public String getImagen() { return imagen; }
    @PropertyName("imagen")   public void setImagen(String imagen) { this.imagen = imagen; }
    @PropertyName("imageUrl") public String getImageUrl() { return imageUrl; }
    @PropertyName("imageUrl") public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // --- Precio / alias ---
    @PropertyName("precio") public double getPrecio() { return precio; }
    @PropertyName("precio") public void setPrecio(double precio) { this.precio = precio; }
    @PropertyName("price")  public Double getPrice() { return price; }
    @PropertyName("price")  public void setPrice(Double price) { this.price = price; }

    // --- Personas / alias ---
    @PropertyName("cantidad_personas") public int getCantidad_personas() { return cantidad_personas; }
    @PropertyName("cantidad_personas") public void setCantidad_personas(int cantidad_personas) { this.cantidad_personas = cantidad_personas; }
    @PropertyName("people") public Integer getPeople() { return people; }
    @PropertyName("people") public void setPeople(Integer people) { this.people = people; }

    // --- Empresa / dueño ---
    @PropertyName("empresaId") public String getEmpresaId() { return empresaId; }
    @PropertyName("empresaId") public void setEmpresaId(String empresaId) { this.empresaId = empresaId; }
    @PropertyName("ownerUid")  public String getOwnerUid() { return ownerUid; }
    @PropertyName("ownerUid")  public void setOwnerUid(String ownerUid) { this.ownerUid = ownerUid; }

    // --- Otros campos útiles ---
    @PropertyName("ciudad") public String getCiudad() { return ciudad; }
    @PropertyName("ciudad") public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    @PropertyName("langs") public String getLangs() { return langs; }
    @PropertyName("langs") public void setLangs(String langs) { this.langs = langs; }
    @PropertyName("duration") public String getDuration() { return duration; }
    @PropertyName("duration") public void setDuration(String duration) { this.duration = duration; }
    @PropertyName("assignedGuideName") public String getAssignedGuideName() { return assignedGuideName; }
    @PropertyName("assignedGuideName") public void setAssignedGuideName(String assignedGuideName) { this.assignedGuideName = assignedGuideName; }
    @PropertyName("paymentProposal") public Double getPaymentProposal() { return paymentProposal; }
    @PropertyName("paymentProposal") public void setPaymentProposal(Double paymentProposal) { this.paymentProposal = paymentProposal; }

    // --- Fechas ---
    @PropertyName("dateFrom") public Date getDateFrom() { return dateFrom; }
    @PropertyName("dateFrom") public void setDateFrom(Date dateFrom) { this.dateFrom = dateFrom; }
    @PropertyName("dateTo") public Date getDateTo() { return dateTo; }
    @PropertyName("dateTo") public void setDateTo(Date dateTo) { this.dateTo = dateTo; }

    // --- Paradas (embebidas) ---
    @PropertyName("paradas") public List<ParadaFB> getParadas() { return paradas; }
    @PropertyName("paradas") public void setParadas(List<ParadaFB> paradas) { this.paradas = paradas; }

    // --- Paradas (referencias) ---
    @PropertyName("id_paradas") public List<String> getId_paradas() { return id_paradas; }
    @PropertyName("id_paradas") public void setId_paradas(List<String> id_paradas) { this.id_paradas = id_paradas; }

    // --- Servicios ---
    @PropertyName("services") public List<ServiceFB> getServices() { return services; }
    @PropertyName("services") public void setServices(List<ServiceFB> services) { this.services = services; }

    /** Modelo interno de Servicio (para chips en UI) */
    public static class ServiceFB implements Serializable {
        private String name;
        private Boolean included;
        private Double price;

        public ServiceFB() {}

        @PropertyName("name") public String getName() { return name; }
        @PropertyName("name") public void setName(String name) { this.name = name; }

        @PropertyName("included") public Boolean getIncluded() { return included; }
        @PropertyName("included") public void setIncluded(Boolean included) { this.included = included; }

        @PropertyName("price") public Double getPrice() { return price; }
        @PropertyName("price") public void setPrice(Double price) { this.price = price; }
    }

    // --- Helpers para la UI ---
    public String getDisplayName() {
        if (nombre != null && !nombre.isEmpty()) return nombre;
        return name != null ? name : "";
    }

    public String getDisplayImageUrl() {
        if (imagen != null && !imagen.isEmpty()) return imagen;
        return imageUrl != null ? imageUrl : "";
    }

    public int getDisplayPeople() {
        if (cantidad_personas > 0) return cantidad_personas;
        return people != null ? people : 0;
    }

    public double getDisplayPrice() {
        if (precio > 0) return precio;
        return price != null ? price : 0.0;
    }

    public boolean hasParadas() {
        return (paradas != null && !paradas.isEmpty()) ||
                (id_paradas != null && !id_paradas.isEmpty());
    }

    @Override
    public String toString() {
        return "TourFB{" +
                "id='" + id + '\'' +
                ", nombre='" + getDisplayName() + '\'' +
                ", empresaId='" + empresaId + '\'' +
                '}';
    }
}