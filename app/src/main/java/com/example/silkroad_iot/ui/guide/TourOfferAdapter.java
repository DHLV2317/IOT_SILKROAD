package com.example.silkroad_iot.ui.guide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.TourFB;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TourOfferAdapter extends RecyclerView.Adapter<TourOfferAdapter.TourOfferViewHolder> {

    private final List<TourFB> tourOfferList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TourOfferAdapter(List<TourFB> tourOfferList) {
        this.tourOfferList = tourOfferList;
    }

    @NonNull
    @Override
    public TourOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tour_offer, parent, false);
        return new TourOfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TourOfferViewHolder holder, int position) {
        TourFB tour = tourOfferList.get(position);

        // ==== Título del tour ====
        String tourTitle = tour.getNombre();
        if (tourTitle == null || tourTitle.trim().isEmpty()) {
            tourTitle = tour.getName();
        }
        if (tourTitle == null || tourTitle.trim().isEmpty()) {
            tourTitle = "Tour sin nombre";
        }

        holder.tvTourName.setText(tourTitle);

        // ==== Empresa: primero ciudad, luego intentamos cargar nombre real de Empresa ====
        String fallbackCompany = tour.getCiudad();
        if (fallbackCompany == null || fallbackCompany.trim().isEmpty()) {
            fallbackCompany = "Empresa / Ciudad no definida";
        }
        holder.tvCompanyName.setText(fallbackCompany);

        // Si hay empresaId, intentamos obtener nombre desde "empresas"
        String empresaId = tour.getEmpresaId();
        if (empresaId != null && !empresaId.trim().isEmpty()) {
            int bindPosition = holder.getBindingAdapterPosition();
            db.collection("empresas")
                    .document(empresaId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (!doc.exists()) return;

                        String empName = doc.getString("nombre");
                        if (empName == null || empName.trim().isEmpty()) {
                            return; // usamos el fallback que ya está en el TextView
                        }

                        int currentPos = holder.getBindingAdapterPosition();
                        if (currentPos == RecyclerView.NO_POSITION) return;
                        if (currentPos >= tourOfferList.size()) return;
                        TourFB currentTour = tourOfferList.get(currentPos);

                        // Evitar pintar en otro item reciclado
                        if (tour.getId() != null &&
                                tour.getId().equals(currentTour.getId())) {
                            holder.tvCompanyName.setText(empName);
                        }
                    });
        }

        // ==== Pago propuesto ====
        Double pay = tour.getPaymentProposal();
        String payText = (pay == null
                ? "Pago a negociar"
                : String.format(Locale.getDefault(), "Pago propuesto: S/ %.2f", pay));

        holder.tvPayment.setText(payText);

        // ====================== ACEPTAR OFERTA ======================
        String finalTourTitle = tourTitle;
        holder.btnAcceptOffer.setOnClickListener(v -> {
            if (tour.getId() == null || tour.getId().trim().isEmpty()) {
                Toast.makeText(v.getContext(), "Tour sin ID de documento", Toast.LENGTH_SHORT).show();
                return;
            }

            User logged = UserStore.get().getLogged();
            if (logged == null) {
                Toast.makeText(v.getContext(),
                        "Sesión expirada. Vuelve a iniciar sesión.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String guideEmail = logged.getEmail();
            if (guideEmail == null || guideEmail.trim().isEmpty()) {
                Toast.makeText(v.getContext(),
                        "Guía sin email. No se puede asignar.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            holder.btnAcceptOffer.setEnabled(false);
            holder.btnRejectOffer.setEnabled(false);

            // Nombre de empresa que se ve en la card (para historial)
            String companyNameForHist = holder.tvCompanyName.getText().toString();

            // 1) Buscar documento del guía por email
            db.collection("guias")
                    .whereEqualTo("email", guideEmail)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(q -> {
                        if (q.isEmpty()) {
                            holder.btnAcceptOffer.setEnabled(true);
                            holder.btnRejectOffer.setEnabled(true);
                            Toast.makeText(v.getContext(),
                                    "No se encontró el perfil de guía en 'guias'.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Doc de guía
                        String guideDocId = q.getDocuments().get(0).getId();
                        String nombre = q.getDocuments().get(0).getString("nombre");
                        String apellidos = q.getDocuments().get(0).getString("apellidos");

                        String fullName;
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            if (apellidos != null && !apellidos.trim().isEmpty()) {
                                fullName = nombre + " " + apellidos;
                            } else {
                                fullName = nombre;
                            }
                        } else {
                            fullName = guideEmail;
                        }

                        // 2) Actualizar TOUR
                        Map<String, Object> updTour = new HashMap<>();
                        updTour.put("assignedGuideId", guideDocId);
                        updTour.put("assignedGuideName", fullName);
                        updTour.put("paymentProposal", tour.getPaymentProposal());
                        updTour.put("status", "EN_CURSO");   // capa lógica
                        updTour.put("estado", "en_curso");   // legacy
                        updTour.put("publicado", false);     // ya no es oferta

                        db.collection("tours")
                                .document(tour.getId())
                                .update(updTour)
                                .addOnSuccessListener(unused -> {

                                    // 3) Actualizar estado del guía
                                    Map<String, Object> updGuide = new HashMap<>();
                                    updGuide.put("estado", "ocupado");
                                    updGuide.put("tourActual", finalTourTitle);
                                    updGuide.put("tourIdAsignado", tour.getId());

                                    db.collection("guias")
                                            .document(guideDocId)
                                            .update(updGuide);

                                    // 4) Registrar historial del guía
                                    Map<String, Object> hist = new HashMap<>();
                                    hist.put("tourId", tour.getId());
                                    hist.put("tourName", finalTourTitle);
                                    hist.put("companyName", companyNameForHist);
                                    hist.put("payment", tour.getPaymentProposal());
                                    hist.put("status", "EN_CURSO");
                                    hist.put("estado", "asignado");
                                    hist.put("timestamp", System.currentTimeMillis());

                                    db.collection("guias")
                                            .document(guideDocId)
                                            .collection("historial")
                                            .add(hist);

                                    Toast.makeText(v.getContext(),
                                            "Has aceptado el tour: " + finalTourTitle,
                                            Toast.LENGTH_SHORT).show();

                                    int pos = holder.getBindingAdapterPosition();
                                    if (pos != RecyclerView.NO_POSITION) {
                                        tourOfferList.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    holder.btnAcceptOffer.setEnabled(true);
                                    holder.btnRejectOffer.setEnabled(true);
                                    Toast.makeText(v.getContext(),
                                            "Error al aceptar: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        holder.btnAcceptOffer.setEnabled(true);
                        holder.btnRejectOffer.setEnabled(true);
                        Toast.makeText(v.getContext(),
                                "Error buscando guía: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        });

        // ====================== RECHAZAR OFERTA ======================
        String finalTourTitle1 = tourTitle;
        holder.btnRejectOffer.setOnClickListener(v -> {
            if (tour.getId() == null || tour.getId().trim().isEmpty()) {
                Toast.makeText(v.getContext(), "Tour sin ID de documento", Toast.LENGTH_SHORT).show();
                return;
            }

            holder.btnAcceptOffer.setEnabled(false);
            holder.btnRejectOffer.setEnabled(false);

            Toast.makeText(v.getContext(),
                    "Has rechazado el tour: " + finalTourTitle1,
                    Toast.LENGTH_SHORT).show();

            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                tourOfferList.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tourOfferList.size();
    }

    static class TourOfferViewHolder extends RecyclerView.ViewHolder {

        TextView tvTourName, tvPayment, tvCompanyName;
        Button btnAcceptOffer, btnRejectOffer;

        public TourOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvPayment = itemView.findViewById(R.id.tvPayment);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            btnAcceptOffer = itemView.findViewById(R.id.btnAcceptOffer);
            btnRejectOffer = itemView.findViewById(R.id.btnRejectOffer);
        }
    }
}