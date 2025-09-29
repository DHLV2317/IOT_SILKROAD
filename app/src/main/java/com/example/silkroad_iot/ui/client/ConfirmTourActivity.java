package com.example.silkroad_iot.ui.client;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silkroad_iot.data.Tour;
import com.example.silkroad_iot.data.TourOrder;
import com.example.silkroad_iot.data.User;
import com.example.silkroad_iot.data.UserStore;
import com.example.silkroad_iot.databinding.ActivityConfirmTourBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConfirmTourActivity extends AppCompatActivity {

    private Tour tour;
    private Calendar selectedDateTime;
    private TextView tTotal, tSelectedDateTime;
    private NumberPicker npQuantity;
    private int quantity = 1;
    private TourOrder.Status Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityConfirmTourBinding b = ActivityConfirmTourBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        b.toolbar.setTitle("ConfirmTour");

        tour = (Tour) getIntent().getSerializableExtra("tour");
        if (tour == null) finish();

        b.tTourName.setText(tour.name);

        selectedDateTime = Calendar.getInstance();

        npQuantity = b.npQuantity;
        npQuantity.setMinValue(1);
        npQuantity.setMaxValue(20);
        npQuantity.setValue(1);

        tTotal = b.tTotal;
        tSelectedDateTime = b.tSelectedDateTime;
        updateTotal(1);
        updateSelectedText();

        npQuantity.setOnValueChangedListener((picker, oldVal, newVal) -> {
            quantity = newVal;
            updateTotal(newVal);
        });

        b.btnSelectDate.setOnClickListener(v -> {
            int y = selectedDateTime.get(Calendar.YEAR);
            int m = selectedDateTime.get(Calendar.MONTH);
            int d = selectedDateTime.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateSelectedText();
            }, y, m, d).show();
        });

        b.btnSelectTime.setOnClickListener(v -> {
            int h = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int min = selectedDateTime.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                updateSelectedText();
            }, h, min, true).show();
        });

        b.btnConfirm.setOnClickListener(v -> {
            User user = UserStore.get().getLogged();
            if (user == null) return;

            TourOrder order = new TourOrder(
                    tour,
                    quantity,
                    selectedDateTime.getTime(),
                    user.getEmail(),
                    TourOrder.Status.RESERVADO
            );

            OrderStore.addOrder(order);
            Toast.makeText(this, "Tour reservado con éxito", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateTotal(int quantity) {
        double total = tour.price * quantity;
        tTotal.setText("Total: S/. " + total);
    }

    private void updateSelectedText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tSelectedDateTime.setText("Tour programado para: " + sdf.format(selectedDateTime.getTime()));
    }
}


