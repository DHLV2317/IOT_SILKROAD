package com.example.silkroad_iot.ui.guide;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideHistoryBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class GuideHistoryActivity extends AppCompatActivity {

    private ActivityGuideHistoryBinding b;
    private FirebaseFirestore db;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityGuideHistoryBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Historial de Tours");
        }

        db = FirebaseFirestore.getInstance();
        loadHistory();
    }

    private void loadHistory() {
        User u = UserStore.get().getLogged();
        String email = (u!=null? u.getEmail(): null);
        if (email == null) { b.txtFullHistory.setText("Sin datos"); return; }

        db.collection("guias").whereEqualTo("email", email).limit(1).get()
                .addOnSuccessListener(snap -> {
                    if (snap.isEmpty()) { b.txtFullHistory.setText("No se encontró el guía"); return; }
                    String guideDocId = snap.getDocuments().get(0).getId();

                    db.collection("guias").document(guideDocId).collection("historial")
                            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(hsnap -> {
                                StringBuilder sb = new StringBuilder();
                                for (QueryDocumentSnapshot d : hsnap) {
                                    String tour   = d.getString("tourName");
                                    String empresa= d.getString("companyName");
                                    Number pago   = d.getDouble("payment");
                                    Number rating = d.getDouble("rating");
                                    Long ts       = d.getLong("timestamp");
                                    java.util.Date date = ts==null? null : new java.util.Date(ts);

                                    sb.append("🗓️ ")
                                            .append(date==null? "—" : java.text.DateFormat.getDateTimeInstance().format(date))
                                            .append("\n📍 ").append(tour==null? "—":tour)
                                            .append("\n🏢 ").append(empresa==null? "—":empresa)
                                            .append("\n💰 ").append(pago==null? "—":"S/ "+pago)
                                            .append("\n⭐ ").append(rating==null? "—":String.format(java.util.Locale.getDefault(),"%.1f", rating.doubleValue()))
                                            .append("\n\n");
                                }
                                if (sb.length()==0) sb.append("Sin historial.");
                                b.txtFullHistory.setText(sb.toString());
                            });
                });
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}