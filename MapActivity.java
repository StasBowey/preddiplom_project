package com.example.secondapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class MapActivity extends AppCompatActivity {

    private Spinner spinnerYear, spinnerMonth;
    private Button btnAdd, btnExport;
    private FirebaseDatabase db;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        spinnerYear = findViewById(R.id.spinner_year);
        spinnerMonth = findViewById(R.id.spinner_month);
        btnAdd = findViewById(R.id.btn_add);
        btnExport = findViewById(R.id.btn_export);

        // Инициализация Firebase Database
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("subjects");

        // Установка адаптеров для выпадающих списков
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Обработчик нажатия на кнопку "Добавить"
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDaysOfMonth();
            }
        });

        // Обработчик нажатия на кнопку "Экспорт в xlsx"
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToXlsx();
            }
        });
    }

    private void addDaysOfMonth() {
        int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
        int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1; // Позиция месяца начинается с 0

        // Получение количества дней в выбранном месяце
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth - 1, 1); // Установка года и месяца (месяцы начинаются с 0)
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Добавление полей для ввода предметов и чисел для каждого дня месяца
        for (int day = 1; day <= daysInMonth; day++) {
            for (int i = 1; i <= 6; i++) {
                // Создание полей для ввода предмета и числа
                EditText editTextSubject = new EditText(this);
                EditText editTextNumber = new EditText(this);

                // Установка идентификаторов для полей
                editTextSubject.setId(View.generateViewId());
                editTextNumber.setId(View.generateViewId());

                // Установка параметров макета для полей
                RelativeLayout.LayoutParams layoutParamsSubject = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams layoutParamsNumber = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                // Параметры позиционирования для полей
                layoutParamsSubject.addRule(RelativeLayout.BELOW, btnAdd.getId());
                layoutParamsNumber.addRule(RelativeLayout.BELOW, btnAdd.getId());
                layoutParamsNumber.addRule(RelativeLayout.END_OF, editTextSubject.getId());

                // Присоединение полей к макету
                RelativeLayout relativeLayout = findViewById(R.id.map_activity);
                relativeLayout.addView(editTextSubject, layoutParamsSubject);
                relativeLayout.addView(editTextNumber, layoutParamsNumber);
            }
        }

        Toast.makeText(this, "Добавлены все дни месяца", Toast.LENGTH_SHORT).show();
    }

    private void exportToXlsx() {
        // Получение ссылки на базу данных Firebase
        DatabaseReference subjectsRef = myRef.child("subjects");

        // Получение всех полей для ввода предметов и чисел
        RelativeLayout relativeLayout = findViewById(R.id.map_activity);
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            View childView = relativeLayout.getChildAt(i);
            if (childView instanceof EditText) {
                EditText editText = (EditText) childView;
                String value = editText.getText().toString().trim();
                if (!value.isEmpty()) {
                    // Запись предмета в базу данных Firebase
                    String key = "subject_" + (i / 6 + 1); // Имя ключа в базе данных (например, "subject_1", "subject_2" и т.д.)
                    subjectsRef.child(key).setValue(value);
               }
            }
        }

        Toast.makeText(this, "Данные успешно экспортированы в Firebase", Toast.LENGTH_SHORT).show();
    }
}