package com.example.rentalz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private AutoCompleteTextView propertyList;
    private ArrayList<String> propertyArrayList;
    private ArrayAdapter<String> arrayAdapter;
    private AutoCompleteTextView bedroomList;
    private ArrayList<String> bedroomArrayList;
    private AutoCompleteTextView furnitureList_type;
    private ArrayList<String> furnitureArrayList;
    private TextInputEditText datetimepicker;
    private TextInputEditText price_input;
    private TextInputEditText note_input;
    private TextInputEditText reporter_input;
    private AppCompatButton btn_submit;
    private AppCompatButton btn_clear;

    int day, month, year, hour, minute;
    int mday, mMonth, mYear, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hook();
        setPropertyType();
        setBedroom();
        setFurnitureListtype();
        setDateTime();
        validate();
        watcher();
        clear();


    }

    private void watcher() {
        propertyList.addTextChangedListener(propertyWatcher);
        bedroomList.addTextChangedListener(bedroomWatcher);
        furnitureList_type.addTextChangedListener(furnitureWatcher);
        datetimepicker.addTextChangedListener(dateTimeWatcher);
        price_input.addTextChangedListener(monthlyPriceWatcher);
        reporter_input.addTextChangedListener(nameReporterWatcher);

    }


    private void validate() {
        btn_submit.setOnClickListener(view -> {
            String property = Objects.requireNonNull(propertyList.getText()).toString().trim();
            String bedroom = Objects.requireNonNull(bedroomList.getText()).toString().trim();
            String furniture = Objects.requireNonNull(furnitureList_type.getText()).toString().trim();
            String dateTime = Objects.requireNonNull(datetimepicker.getText()).toString().trim();
            String price = Objects.requireNonNull(price_input.getText()).toString().trim();
            String name = Objects.requireNonNull(reporter_input.getText()).toString().trim();
            String note = Objects.requireNonNull(note_input.getText()).toString().trim();

            if(TextUtils.isEmpty(property)){
                TextInputLayout til = findViewById(R.id.propertyList);
                til.setError(getString(R.string.validate_property_error));
                til.requestFocus();
            }

            if (TextUtils.isEmpty(bedroom)){
                TextInputLayout til = findViewById(R.id.bedroomList);
                til.setError(getString(R.string.validate_bedrooms_error));
                til.requestFocus();
            }
            if(TextUtils.isEmpty(furniture)){
                TextInputLayout til = findViewById(R.id.furnitureList_type);
                til.setError(getString(R.string.validate_furniture_error));
                til.requestFocus();
            }

            if(TextUtils.isEmpty(dateTime)){
                TextInputLayout til = findViewById(R.id.datetimepicker);
                til.setError(getString(R.string.validate_date_time_error));
                til.requestFocus();
            }

            if(TextUtils.isEmpty(price)){
                TextInputLayout til = findViewById(R.id.price_input);
                til.setError(getString(R.string.validate_price));
                til.requestFocus();
            }

            if(TextUtils.isEmpty(name)){
                TextInputLayout til = findViewById(R.id.reporter_input);
                til.setError(getString(R.string.validate_reporter));
                til.requestFocus();
            }

            if(TextUtils.isEmpty(property) ||TextUtils.isEmpty(furniture) || TextUtils.isEmpty(bedroom) || TextUtils.isEmpty(dateTime) || TextUtils.isEmpty(price) || TextUtils.isEmpty(name)){
                Toast.makeText(MainActivity.this,getString(R.string.empty_form),Toast.LENGTH_SHORT).show();

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);
            }else{
                submit(property,bedroom,dateTime,furniture,price,note,name);
            }
        });
    }


    @SuppressLint("ResourceType")
    private void submit(String property, String bedroom, String dateTime, String furniture, String price, String note, String reporter) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
//        builder.setMessage(getString(R.string.conform_box));
        builder.setTitle("Please confirm the details have been input correctly.");
        builder.setMessage("Property: " + property + "\n" + "Bedroom: " + bedroom +
                "\n" + "DateTime: " + dateTime + "\n" + "Furniture: " + furniture
                + "\n" + "Price: " + price + "\n" + "Note: " + note + "\n" + "Reporter: " + reporter);
        builder.setCancelable(true);

        builder.setPositiveButton(getString(R.string.confirm_btn), (dialogInterface, i) -> {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,Object> rentalInfo = new HashMap<>();
            rentalInfo.put("propertyType",property);
            rentalInfo.put("bedroom",bedroom);
            rentalInfo.put("dateTime",dateTime);
            rentalInfo.put("furnitureType",furniture);
            rentalInfo.put("monthlyPrice",price);
            rentalInfo.put("note",note);
            rentalInfo.put("nameOfReporter",reporter);
            db.collection("rental").add(rentalInfo).addOnSuccessListener(documentReference -> {
                Toast.makeText(MainActivity.this,getString(R.string.result_toast),Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this,getString(R.string.fail_toast),Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
            });
        });
        builder.setNegativeButton(getString(R.string.back_btn), (dialogInterface, i) -> dialogInterface.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void clear() {
        btn_clear.setOnClickListener(view -> {
            propertyList.getText().clear();
            bedroomList.getText().clear();
            furnitureList_type.getText().clear();
            Objects.requireNonNull(datetimepicker.getText()).clear();
            Objects.requireNonNull(price_input.getText()).clear();
            Objects.requireNonNull(reporter_input.getText()).clear();
            Objects.requireNonNull(note_input.getText()).clear();
        });
    }


    private void setDateTime()  {
        datetimepicker.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this,year, month,day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        mYear = year;
        mday = day;
        mMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, MainActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        mHour = i;
        mMinute = i1;
        String m;
        if(mMinute < 10){
            m= "0"+mMinute+"";
        }else{
            m = mMinute+"";
        }
        datetimepicker.setText(mYear + "/" + (mMonth+1) + "/" + mday + " " + mHour + ":" + m);
    }

    private void setBedroom() {
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_item_list,bedroomArrayList);
        bedroomList.setAdapter(arrayAdapter);
        bedroomList.setThreshold(1);
    }

    private void setPropertyType(){
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_item_list,propertyArrayList);
        propertyList.setAdapter(arrayAdapter);
        propertyList.setThreshold(1);
    }
    private void setFurnitureListtype(){
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_item_list,furnitureArrayList);
        furnitureList_type.setAdapter(arrayAdapter);
        furnitureList_type.setThreshold(1);
    }

    //Property Watcher
    private final TextWatcher propertyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.reporter_input);
            if(editable.length() == 0){
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_property_error));
                layout.requestFocus();
            }
            else{
                layout.setErrorEnabled(false);
            }

        }
    };

    //Bedroom watcher
    private final TextWatcher bedroomWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.bedroomList);
            if(editable.length() == 0){
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_bedrooms_error));
                layout.requestFocus();
            }
            else{
                layout.setErrorEnabled(false);
            }

        }
    };

    //Date time watcher
    private final TextWatcher dateTimeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.datetimepicker);
            if(editable.length() == 0){
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_date_time_error));
                layout.requestFocus();
            }
            else{
                layout.setErrorEnabled(false);
            }

        }
    };
    //Furniture watcher
    private final TextWatcher furnitureWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.furnitureList_type);
            if(editable.length() == 0){
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_furniture_error));
                layout.requestFocus();
            }
            else{
                layout.setErrorEnabled(false);
            }

        }
    };

    //Price watcher
    private final TextWatcher monthlyPriceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.price_input);
            if(editable.length() == 0){
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_price));
                layout.requestFocus();
            }
            else{
                layout.setErrorEnabled(false);
            }

        }
    };

    //Name watcher
    private final TextWatcher nameReporterWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            TextInputLayout layout = findViewById(R.id.reporter_input);
            if(editable.length() == 0){
                layout.setErrorEnabled(true);
                layout.setError(getString(R.string.validate_reporter));
                layout.requestFocus();
            }
            else{
                layout.setErrorEnabled(false);
            }

        }
    };

    private void hook(){
        propertyList = findViewById(R.id.PropertyField);
        bedroomList = findViewById(R.id.BedroomField);
        datetimepicker = findViewById(R.id.datetime_input);
        price_input = findViewById(R.id.priceText);
        note_input = findViewById(R.id.noteText);
        reporter_input = findViewById(R.id.reporterText);
        furnitureList_type = findViewById(R.id.furnitureField);
        btn_submit = findViewById(R.id.btn_submit);
        btn_clear = findViewById(R.id.btn_clear);

        propertyArrayList = new ArrayList<>();
        propertyArrayList.add("House");
        propertyArrayList.add("Flat");
        propertyArrayList.add("Bungalow");

        bedroomArrayList = new ArrayList<>();
        bedroomArrayList.add("Studio");
        bedroomArrayList.add("One");
        bedroomArrayList.add("Two");
        bedroomArrayList.add("Three");


        furnitureArrayList = new ArrayList<>();
        furnitureArrayList.add("Furnished");
        furnitureArrayList.add("Unfurnished");
        furnitureArrayList.add("Part Furnished");
    }


}