package com.example.silkroad_iot.ui.auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.silkroad_iot.R;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityGuideRegisterBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class GuideRegisterActivity extends AppCompatActivity {

    private ActivityGuideRegisterBinding binding;
    private Uri imageUri;

    // ActivityResultLauncher for picking an image
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    binding.imageProfile.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar); // Assuming you add a Toolbar with id 'toolbar' in your layout
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registro de Guía");
        }


        // Setup Document Type Spinner
        String[] documentTypes = new String[]{"DNI", "Carnet de Extranjería", "Pasaporte"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, documentTypes);
        binding.spinnerDocumentType.setAdapter(adapter);

        // Setup Birth Date Picker
        binding.inputBirthDate.setOnClickListener(v -> showDatePickerDialog());
        binding.inputBirthDate.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                showDatePickerDialog();
            }
        });


        // Setup Select Image Button
        binding.btnSelectImage.setOnClickListener(v -> openFileChooser());

        // Setup Register Button
        binding.btnRegisterGuide.setOnClickListener(v -> registerGuide());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                GuideRegisterActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year1);
                    binding.inputBirthDate.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(intent);
    }

    private void registerGuide() {
        String names = Objects.requireNonNull(binding.inputNames.getText()).toString().trim();
        String lastNames = Objects.requireNonNull(binding.inputLastNames.getText()).toString().trim();
        String documentType = binding.spinnerDocumentType.getText().toString().trim();
        String documentNumber = Objects.requireNonNull(binding.inputDocumentNumber.getText()).toString().trim();
        String birthDate = Objects.requireNonNull(binding.inputBirthDate.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
        String phone = Objects.requireNonNull(binding.inputPhone.getText()).toString().trim();
        String address = Objects.requireNonNull(binding.inputAddress.getText()).toString().trim();
        String languages = Objects.requireNonNull(binding.inputLanguages.getText()).toString().trim();

        // Basic Validations
        if (TextUtils.isEmpty(names)) {
            binding.inputNames.setError("Nombres requeridos");
            binding.inputNames.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(lastNames)) {
            binding.inputLastNames.setError("Apellidos requeridos");
            binding.inputLastNames.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(documentType)) {
            binding.spinnerDocumentType.setError("Seleccione tipo de documento");
            binding.spinnerDocumentType.requestFocus();
            // Toast.makeText(this, "Seleccione tipo de documento", Toast.LENGTH_SHORT).show();
            return;
        }
         binding.spinnerDocumentType.setError(null); // Clear error if present

        if (TextUtils.isEmpty(documentNumber)) {
            binding.inputDocumentNumber.setError("Número de documento requerido");
            binding.inputDocumentNumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(birthDate)) {
            binding.inputBirthDate.setError("Fecha de nacimiento requerida");
            //binding.inputBirthDate.requestFocus(); // Avoids immediate re-trigger of date picker
            Toast.makeText(this, "Fecha de nacimiento requerida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError("Correo electrónico inválido");
            binding.inputEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            binding.inputPhone.setError("Teléfono requerido");
            binding.inputPhone.requestFocus();
            return;
        }
         if (TextUtils.isEmpty(address)) {
            binding.inputAddress.setError("Domicilio requerido");
            binding.inputAddress.requestFocus();
            return;
        }
        if (imageUri == null) {
            Snackbar.make(binding.getRoot(), "Por favor, seleccione una foto de perfil.", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(languages)) {
            binding.inputLanguages.setError("Idiomas requeridos");
            binding.inputLanguages.requestFocus();
            return;
        }

        // --- Simulación de envío de información ---
        // En una aplicación real, aquí enviarías los datos a tu backend o UserStore.
        // Por ahora, solo mostraremos un mensaje.

        String guideInfo = "Nombres: " + names + "\n" +
                           "Apellidos: " + lastNames + "\n" +
                           "Documento: " + documentType + " - " + documentNumber + "\n" +
                           "Nacimiento: " + birthDate + "\n" +
                           "Email: " + email + "\n" +
                           "Teléfono: " + phone + "\n" +
                           "Domicilio: " + address + "\n" +
                           "Idiomas: " + languages + "\n" +
                           "Foto URI: " + imageUri.toString();

        //Log.d("GuideRegister", "Información del Guía:\n" + guideInfo);
        // For the demo, we'll use a Snackbar/Toast
        Snackbar.make(binding.getRoot(), "Registro de guía enviado (simulado). Pendiente de aprobación.", Snackbar.LENGTH_LONG).show();

        // ✅ IMPLEMENTADO: Guardamos la información del guía
        UserStore.get().registerGuide(names, lastNames, documentType, documentNumber, 
                                    birthDate, email, phone, address, 
                                    imageUri.toString(), languages);

        // For now, just finish the activity or navigate back
        // For a real app, you might want to navigate to a "Pending Approval" screen or back to login
        new android.os.Handler().postDelayed(
            () -> {
                // Example: Navigate back to MainActivity or a Login screen
                // Intent intent = new Intent(GuideRegisterActivity.this, MainActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // startActivity(intent);
                finish(); // Close this activity
            },
            3000 // Delay to show Snackbar
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}