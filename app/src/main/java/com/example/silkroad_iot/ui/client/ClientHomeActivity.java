package com.example.silkroad_iot.ui.client;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.silkroad_iot.databinding.ActivityClientHomeBinding;
import com.example.silkroad_iot.data.*;
import java.util.*;

public class ClientHomeActivity extends AppCompatActivity {
    private ActivityClientHomeBinding b;
    private final UserStore store = UserStore.get();



    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        b = ActivityClientHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);

        User u = store.getLogged();
        String name = (u!=null? u.getName() : "Usuario");
        b.toolbar.setTitle("Hola " + name);

        // Departamentos (horizontal)
        List<Department> deps = Arrays.asList(
                new Department("Lima"), new Department("Cusco"),
                new Department("Arequipa"), new Department("Piura"),
                new Department("La Libertad")
        );
        b.rvDepartments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        b.rvDepartments.setAdapter(new DepartmentAdapter(deps, d -> {
            // TODO: navegar a lista de tours filtrados por departamento
        }));


        List<Tour> toursEmpresa1 = Arrays.asList(
                new Tour("Tour Machu Picchu", 250.0, 20, "Un viaje inolvidable", "https://...", 4.8),
                new Tour("City Tour Cusco", 60.0, 15, "Recorrido por la ciudad", "https://...", 4.6)
        );


        // Mejores empresas (mock)
        List<Company> cos = Arrays.asList(
                new Company("Empresa Pepe", 4.6,"https://camaranacional.org.pe/wp-content/uploads/2024/10/adm-de-em-turisticas.jpg",toursEmpresa1),
                new Company("Empresa Tito", 4.4,"https://www.ceupe.com/images/easyblog_articles/1263/b2ap3_large_empresas-turisticas.jpg",new ArrayList<>()),
                new Company("Empresa jp", 4.8,"https://tecnosoluciones.com/wp-content/uploads/2023/03/empresas-turisticas.png",new ArrayList<>())
        );
        b.rvCompanies.setLayoutManager(new LinearLayoutManager(this));
        b.rvCompanies.setAdapter(new CompanyAdapter(cos));
        CompanyAdapter companyAdapter = new CompanyAdapter(cos); // Fuera del método
        b.rvCompanies.setAdapter(companyAdapter);


        b.inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                companyAdapter.filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}