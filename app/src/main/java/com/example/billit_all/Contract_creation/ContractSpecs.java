package com.example.billit_all.Contract_creation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.Bill_history.Receipt2;
import com.example.billit_all.Bill_history.ReceiptAdapter3;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ContractSpecs extends AppCompatActivity {

    EditText penaltyPercent, penaltyWeek, previousReadWater, previousReadElec;
    Button addRentFee, proceedBtn, cancelBtn;
    RadioGroup waterRGroup;
    RadioButton meralcoRBtn, mayniladRBtn, manilaRBtn;
    LinearLayout layoutEditText,layoutEditText2, layoutEditTextRemove;
    TextView selectTenant,newlogTitle;

    TextView nameTextView, dateTextView, house_noTextView, streetTextView, subdTextView, brgyTextView, cityTextView, zipcodeTextView,previousTotalBillElec,previousTotalBillWater;
    String nameString, dateString, house_noString, streetString, subdString, brgyString, cityString, zipcodeString;

    boolean addRentFeeNotClicked = false;
//    String additionalTermsSubject;

    String selectedTenantId, waterProv= "Maynilad", elecProv = "Meralco";
    int countUnit;
    int countTerms = 12 ;
    LinearLayout layoutEditText3;
    Button btnAddEditText;


    ArrayList<String> editTextValues = new ArrayList<>();
    ArrayList<String> editTextValues2 = new ArrayList<>();


    ArrayList<List<EditText>> termsInputFields = new ArrayList<>();
    ArrayList<List<EditText>> unitRentInputFields = new ArrayList<>();

    ArrayList<String> additionalTermsSubject = new ArrayList<>();
    ArrayList<String> additionalRentFees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_specs);


        proceedBtn = findViewById(R.id.proceedBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        layoutEditText2  = findViewById(R.id.layout_edit_text2);
        layoutEditTextRemove = findViewById(R.id.layout_edit_remove);
        addRentFee = findViewById(R.id.addRentFee);
        penaltyPercent = findViewById(R.id.penaltyPercent);
        penaltyWeek = findViewById(R.id.penaltyWeek);

        waterRGroup = findViewById(R.id.waterRGroup);
        meralcoRBtn = findViewById(R.id.meralcoRBtn);
        newlogTitle = findViewById(R.id.newlogTitle);
        layoutEditText3  = findViewById(R.id.layout_edit_text3);
        btnAddEditText = findViewById(R.id.btn_add_edit_text);

        nameTextView = findViewById(R.id.nameTextView);
        dateTextView = findViewById(R.id.dateTextView);
        house_noTextView = findViewById(R.id.house_noTextView);
        streetTextView = findViewById(R.id.streetTextView);
        subdTextView = findViewById(R.id.subdTextView);
        brgyTextView = findViewById(R.id.brgyTextView);
        cityTextView = findViewById(R.id.cityTextView);
        zipcodeTextView = findViewById(R.id.zipcodeTextView);

//        previousReadWater = findViewById(R.id.previousReadWater);
//        previousReadElec = findViewById(R.id.previousReadElec);
//        previousTotalBillElec = findViewById(R.id.previousTotalBillElec);
//        previousTotalBillWater = findViewById(R.id.previousTotalBillWater);

        waterRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                switch (checkedId) {

                    case R.id.manilaRBtn:
                        waterProv = "Manila Water";

                        break;
                    case R.id.mayniladRBtn:
                        waterProv = "Maynilad";
                        break;
                    default:
                        ((TextView) waterRGroup.getChildAt(0)).setError("Select at least one Provider");
                        break;


                }

            }

        });

        meralcoRBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                if (isChecked){
                    elecProv ="Meralco";
                }
                else {
                    meralcoRBtn.setError("Select at least one Provider");
                }
            }
        });




        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(ContractSpecs.this, ContractForm.class);
                startActivity(c);
            }
        });



        addRentFee.post(new Runnable() {
            @Override
            public void run() {
                addRentFee.performClick();
            }
        });

        addRentFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(ContractSpecs.this);
                EditText editText2 = new EditText(ContractSpecs.this);
                TextView unitNoTextView = new TextView(ContractSpecs.this);
                TextView rentFeeTextView = new TextView(ContractSpecs.this);

                Button myButton = new Button(ContractSpecs.this);
                int color = ContextCompat.getColor(getApplicationContext(), R.color.red);


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 10;

                LinearLayout.LayoutParams layoutParamsButtons = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, // Width
                        LinearLayout.LayoutParams.WRAP_CONTENT // Height
                );

                unitNoTextView.setText("Unit No.");
                rentFeeTextView.setText("Rent Fee");
                editText.setLayoutParams(layoutParamsButtons);
                editText.setFocusable(false);
                editText.setClickable(false);
                editText.setTextSize(30);

                editText.setText((++countUnit) + "");
                editText2.setLayoutParams(layoutParams);
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                editText2.setHint("Rent Fee here: (PHP)");

                myButton.setBackgroundColor(color);
                myButton.setText("Remove");

                layoutEditText2.addView(unitNoTextView, layoutParams);
                layoutEditText2.addView(editText,layoutParamsButtons);
                layoutEditText2.addView(rentFeeTextView, layoutParams);
                layoutEditText2.addView(editText2,layoutParams);
                layoutEditText2.addView(myButton, layoutParamsButtons);

                unitRentInputFields.add(Arrays.asList(editText,editText2));

                //setting tag to every arrayListed then Removing index
                int arrayListLenght = unitRentInputFields.size();
                for (int i = 0; i < arrayListLenght; i++) {

                    if(arrayListLenght == 0){
                        Toast.makeText(getApplicationContext(), "Adding Rent Fees are Required", Toast.LENGTH_SHORT).show();
                    }
                    myButton.setTag(i);

                    myButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = (int) v.getTag();

                            layoutEditText2.removeView(editText2);
                            layoutEditText2.removeView(editText);
                            layoutEditText2.removeView(myButton);
                            layoutEditText2.removeView(unitNoTextView);
                            layoutEditText2.removeView(rentFeeTextView);
                            unitRentInputFields.remove(index);
                            countUnit--;


                        }
                    });
                }

            }

        });


        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext().getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext().getApplicationContext());
        String backendToken = loginPreferenceDataSource.getBackendToken();
        try {
            userDataSource.updateProfile(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                Log.d("USER_ID", String.valueOf(user));
                                String id = user.getString("user_id");
                                Log.d("USER_ID", id);


                                RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/contract-specification/mobile/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String name = response.getString("name");
                                                    String date = response.getString("date");
                                                    String house_no = response.getString("house_no");
                                                    String street = response.getString("street");
                                                    String subd = response.getString("subd");
                                                    String brgy = response.getString("brgy");
                                                    String city = response.getString("city");
                                                    String zipcode = response.getString("zipcode");

                                                    Log.d("data", name);
                                                    Log.d("data", date);
                                                    Log.d("data", house_no);
                                                    Log.d("data", street);
                                                    Log.d("data", subd);
                                                    Log.d("data", brgy);
                                                    Log.d("data", city);
                                                    Log.d("data", zipcode);

                                                    nameTextView.setText(name);
                                                    dateTextView.setText(date);
                                                    house_noTextView.setText(house_no);
                                                    streetTextView.setText(street);
                                                    subdTextView.setText(subd);
                                                    brgyTextView.setText(brgy);
                                                    cityTextView.setText(city);
                                                    zipcodeTextView.setText(zipcode);

                                                    nameString = nameTextView.getText().toString();
                                                    dateString = dateTextView.getText().toString();
                                                    house_noString = house_noTextView.getText().toString();
                                                    streetString = streetTextView.getText().toString();
                                                    subdString = subdTextView.getText().toString();
                                                    brgyString = brgyTextView.getText().toString();
                                                    cityString = cityTextView.getText().toString();
                                                    zipcodeString = zipcodeTextView.getText().toString();






                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                );

                                request2.authenticated(getApplicationContext());
                                queue2.add(request2);
                                //end of LangLordInfo Volley
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    null

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }


        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //rules
                String penaltyPercentString = penaltyPercent.getText().toString();
                String penaltyWeekString = penaltyWeek.getText().toString();

//                String previousReadWaterString = previousReadWater.getText().toString();
//                String previousReadElecString = previousReadElec.getText().toString();
//
//                String previousTotalBillWaterString = previousTotalBillWater.getText().toString();
//                String previousTotalBillElecString = previousTotalBillElec.getText().toString();

                TextView error_message = findViewById(R.id.error_message);

                //loop every list and every edit text to get text and apply rule
                for (List<EditText> list: unitRentInputFields) {
                    for (EditText editText : list) {
                        String value = editText.getText().toString();
                        if (value.trim().isEmpty()) {
                            editText.setError("This field is required");
                            editText.requestFocus();
                            return;
                        }
                    }
                }

                if(meralcoRBtn.isChecked())
                {
                    if (waterRGroup.getCheckedRadioButtonId() == -1)
                    {
                        // no radio buttons are checked
                        error_message.setText("Select an Water Provider");
                        error_message.requestFocus();
                        return;
                    }
                    else
                    {
                        // one of the radio buttons is checked
                    }
                }
                else
                {
                    error_message.setText("Select an Electricity Provider");
                    error_message.requestFocus();
                    return;
                }

                if(penaltyPercentString.isEmpty()){
                    penaltyPercent.setError("This Field is Required");
                    penaltyPercent.requestFocus();
                    return;
                }
                if(penaltyWeekString.isEmpty()){
                    penaltyWeek.setError("This Field is Required");
                    penaltyWeek.requestFocus();
                    return;
                }
//                if(previousTotalBillWaterString.isEmpty()){
//                    previousTotalBillWater.setError("This Field is Required");
//                    previousTotalBillWater.requestFocus();
//                    return;
//                }
//
//                if(previousTotalBillElecString.isEmpty()){
//                    previousTotalBillElec.setError("This Field is Required");
//                    previousTotalBillElec.requestFocus();
//                    return;
//                }









                ArrayList<String>units = new ArrayList<String>();
                ArrayList<String>rentFees = new ArrayList<String>();

                ArrayList<String>subjects = new ArrayList<String>();
                ArrayList<String>bodies = new ArrayList<String>();


                for (int i = 0; i < unitRentInputFields.size(); i++) {
                    List<EditText> pair = unitRentInputFields.get(i);
                    units.add(pair.get(0).getText().toString());
                    rentFees.add(pair.get(1).getText().toString());
//
                }


                for (int j = 0; j < termsInputFields.size(); j++) {
                    List<EditText> pair = termsInputFields.get(j);
                    subjects.add(pair.get(0).getText().toString());
                    bodies.add(pair.get(1).getText().toString());

                }



                Log.d("null", String.valueOf(subjects));
                Log.d("null", String.valueOf(bodies));
                Log.d("null", String.valueOf(units));
                Log.d("null", String.valueOf(rentFees));

                //to pair Terms and Condition Arrays (Edit in the sentence)
                ArrayList<String> pairs = new ArrayList<>();
                for (int i = 0; i < subjects.size(); i++) {
                    String pair = (++countTerms) + "." + subjects.get(i) + ":  " + bodies.get(i);
                    pairs.add(pair);

                }
                //(Edit by pair)
                String text = "";
                for (String pair : pairs) {
                    text += "\n"+ pair + "\n";
                }

                //Intents for ArrayList and EditBoxes
                Intent b = new Intent(ContractSpecs.this, Contract.class);
                b.putExtra("additionalTermsSubject", text);
                b.putExtra("penaltyPercentString", penaltyPercentString);
                b.putExtra("penaltyWeekString", penaltyWeekString);

//                b.putExtra("previousReadWaterString", previousReadWaterString);
//                b.putExtra("previousReadElecString", previousReadElecString);
//
//                b.putExtra("previousTotalBillWater", previousTotalBillWaterString);
//                b.putExtra("previousTotalBillElec", previousTotalBillElecString);


                b.putExtra("elecProv", elecProv);
                b.putExtra("waterProv", waterProv);

                b.putExtra("name", nameString);
                b.putExtra("date", dateString);
                b.putExtra("house_no", house_noString);
                b.putExtra("street", streetString);
                b.putExtra("subd", subdString);
                b.putExtra("brgy", brgyString);
                b.putExtra("city", cityString);
                b.putExtra("zipcode", zipcodeString);

                b.putStringArrayListExtra("units", units);
                b.putStringArrayListExtra("rentFees", rentFees);
                b.putStringArrayListExtra("subjects", subjects);
                b.putStringArrayListExtra("bodies", bodies);
                startActivity(b);



            }
        });



        btnAddEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button myButton = new Button(ContractSpecs.this);
                EditText editText3 = new EditText(ContractSpecs.this);
                EditText editText4 = new EditText(ContractSpecs.this);
                int color = ContextCompat.getColor(getApplicationContext(), R.color.red);

                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.weight = 10;
                editText3.setLayoutParams(layoutParams2);

                editText3.setHint("Subject Here:" );

                editText4.setLayoutParams(layoutParams2);
                editText4.setHint("Body Here:");


                myButton.setLayoutParams(layoutParams2);
                myButton.setBackgroundColor(color);
                myButton.setText("Remove");

                layoutEditText3.addView(editText3,layoutParams2);
                layoutEditText3.addView(editText4,layoutParams2);
                layoutEditText3.addView(myButton, layoutParams2);

                termsInputFields.add(Arrays.asList(editText3,editText4));

                //setting tag to every arrayListed then Removing index


                    int arrayListLenght = termsInputFields.size();
                    for (int i = 0; i < arrayListLenght; i++) {


                        if (arrayListLenght == 0) {
                            Toast.makeText(getApplicationContext(), "Additional Terms and Conditions are Removed", Toast.LENGTH_SHORT).show();
                        }
                        myButton.setTag(i);

                        myButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = (int) v.getTag();

                                layoutEditText3.removeView(editText3);
                                layoutEditText3.removeView(editText4);
                                layoutEditText3.removeView(myButton);
                                termsInputFields.remove(index);


                            }
                        });
                    }



                //onCLick
            }
        });


    }

    //to save inputed data when back button is clicked
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("inputData", penaltyPercent.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String inputData = savedInstanceState.getString("inputData");
        penaltyPercent.setText(inputData);
    }

    }


