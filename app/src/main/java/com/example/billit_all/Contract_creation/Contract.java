package com.example.billit_all.Contract_creation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.Calculate.CalculateActivity2;
import com.example.billit_all.R;
import com.example.billit_all.TemporaryLogin;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Contract extends AppCompatActivity {
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");
    TextView contractTemplate, contractTemplate2, testIntent, terms, selectTenant;
//    TextView nameTextView, dateTextView, house_noTextView, streetTextView, subdTextView, brgyTextView, cityTextView, zipcodeTextView;
//    String nameString, dateString, house_noString, streetString, subdString, brgyString, cityString, zipcodeString;
    String additionalTermString, selectedTenantId;
    String name, date, house_no, street, subd, brgy, city, zipcode;
    String penaltyPercent, penaltyWeek, previousReadWater, previousReadElec, previousTotalBillWater, previousTotalBillElec;

    Button proceedButton, cancelBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);
        contractTemplate = findViewById(R.id.contractTemplate);
        contractTemplate2 = findViewById(R.id.contractTemplate2);
        terms = findViewById(R.id.terms);
        testIntent = findViewById(R.id.testIntent);
        proceedButton = findViewById(R.id.proceedButton);
        cancelBtn = findViewById(R.id.cancelBtn);
        selectTenant = findViewById(R.id.select_tenant);

//        nameTextView = findViewById(R.id.nameTextView);
//        dateTextView = findViewById(R.id.dateTextView);
//        house_noTextView = findViewById(R.id.house_noTextView);
//        streetTextView = findViewById(R.id.streetTextView);
//        subdTextView = findViewById(R.id.subdTextView);
//        brgyTextView = findViewById(R.id.brgyTextView);
//        cityTextView = findViewById(R.id.cityTextView);
//        zipcodeTextView = findViewById(R.id.zipcodeTextView);


        //Intents b
        Intent b = getIntent();
        //from Edit Text Inputs
        penaltyPercent = b.getStringExtra("penaltyPercentString");
        penaltyWeek = b.getStringExtra("penaltyWeekString");
//        previousReadWater = b.getStringExtra("previousReadWaterString");
//        previousReadElec = b.getStringExtra("previousReadElecString");
//
//        previousTotalBillWater = b.getStringExtra("previousTotalBillWater");
//        previousTotalBillElec = b.getStringExtra("previousTotalBillElec");

        //from LandLord Information
        name = b.getStringExtra("name");
        date = b.getStringExtra("date");
        house_no = b.getStringExtra("house_no");
        street = b.getStringExtra("street");
        subd = b.getStringExtra("subd");
        brgy = b.getStringExtra("brgy");
        city = b.getStringExtra("city");
        zipcode = b.getStringExtra("zipcode");


        //from RadioButtons Inputs

        String elecProv = b.getStringExtra("elecProv");
        String waterProv = b.getStringExtra("waterProv");



        //for Additional terms from contract specs pasted to Contract
        String addTerms = b.getStringExtra("additionalTermsSubject");
        additionalTermString = (String.valueOf(addTerms));
        terms.setText(String.valueOf(addTerms));
        additionalTermString = terms.getText().toString();
        Log.d("addTerms", additionalTermString);
        //for ArrayList (Rent Fees)
        ArrayList<String> units = b.getStringArrayListExtra("units");
        ArrayList<String> rentFees = b.getStringArrayListExtra("rentFees");

        //for ArrayList (term and condition)
        ArrayList<String> subjects = b.getStringArrayListExtra("subjects");
        ArrayList<String> bodies = b.getStringArrayListExtra("bodies");


        Log.d("data", String.valueOf(units));
        Log.d("data", String.valueOf(rentFees));
        Log.d("data", String.valueOf(subjects));
        Log.d("data", String.valueOf(bodies));
        Log.d("data", elecProv);
        Log.d("data", waterProv);
        Log.d("data", penaltyPercent);
        Log.d("data", penaltyWeek);


        Log.d("data", name);
        Log.d("data", date);
        Log.d("data", house_no);
        Log.d("data", street);
        Log.d("data", subd);
        Log.d("data", brgy);
        Log.d("data", city);
        Log.d("data", zipcode);




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
                                selectTenant.setText(id);
                                selectedTenantId = selectTenant.getText().toString();


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


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                JSONObject formdata = new JSONObject();
                try {
                    formdata.put("penalty_percentage", penaltyPercent);
                    formdata.put("penalty_week", penaltyWeek);
                    formdata.put("electricity", elecProv);
                    formdata.put("water", waterProv);
                    formdata.put("unit_no", new JSONArray(units));
                    formdata.put("rent_fee", new JSONArray(rentFees));

//                    formdata.put("water_amount", previousTotalBillWater);
//                    formdata.put("elec_amount", previousReadElec);

                    formdata.put("subject", new JSONArray(subjects));
                    formdata.put("body", new JSONArray(bodies));


                    BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                            Request.Method.POST,
                            "/api/contract-specification/" + selectedTenantId,
                            formdata,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String message = response.getString("message");
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

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


                    request.authenticated(getApplicationContext());
                    queue.add(request);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

////            for previous reading not final
//                RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getApplicationContext());
//                JSONObject formdata2 = new JSONObject();
//                try {
//                    formdata2.put("prev_elec_read", previousReadElec);
//                    formdata2.put("prev_elec_amount", previousTotalBillElec);
//                    formdata2.put("prev_water_read", previousReadWater);
//                    formdata2.put("prev_water_amount", previousTotalBillWater);
//
//
//
//                    BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
//                            Request.Method.POST,
//                            "/api/setup-record/" + selectedTenantId,
//                            formdata2,
//                            new Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    try {
//                                        String message = response.getString("message");
//                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//
//                                }
//                            }
//                    );
//
//
//                    request2.authenticated(getApplicationContext());
//                    queue2.add(request2);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                //end
                Intent c = new Intent(Contract.this, TemporaryLogin.class);
                startActivity(c);


            }


        });





        //printing PDF if additional terms is empty  if statement
        if (addTerms == null || addTerms.equals("")) {
            printPDF2();
        } else {
            printPDF();
        }




        String contract = "KNOW ALL MEN BY THESE PRESENTS: This CONTRACT OF LEASE is made and " +
                "executed at the City of CITY, this day of (DATE), by " +
                "and between: LESSOR, of legal age," +
                "Filipino, and with residence and postal address at " +
                "# HOUSE#, STREET, SUBDIVISION, BARANGAY, CITY, hereinafter referred " +
                "to as the LESSOR. -AND-(NAME OF LESSEE), Filipino and with residence " +
                "and postal address at (Address), " +
                "hereinafter referred to as the LESSEE.  WITNESSETH; That WHEREAS, the LESSOR " +
                "is the owner of THE LEASED PREMISES, a residential property situated at " +
                "# HOUSE#, STREET, SUBDIVISION, BARANGAY, CITY;  WHEREAS, the LESSOR " +
                "agrees to lease-out the property to the LESSEE and the LESSEE is willing to " +
                "lease the same;  NOW THEREFORE, for and in consideration of the foregoing " +
                "premises, the LESSOR leases unto the LESSEE and the LESSEE hereby accepts from " +
                "the LESSOR the LEASED premises, subject to the following:\n\n" +
                "TERMS AND CONDITIONS\n\n1. PURPOSES:   That premises hereby leased shall be " +
                "used exclusively by the LESSEE for residential purposes only and shall not be " +
                "diverted to other uses. It is hereby expressly agreed that if at any time the " +
                "premises are used for other purposes, the LESSOR shall have the right to rescind " +
                "this contract without prejudice to its other rights under the law.\n\n" +
                "2. TERM:  This term of lease is for ONE (1) YEAR. from (Date) to (Date) inclusive. " +
                "Upon its expiration, this lease may be renewed under such terms and conditions " +
                "as my be mutually agreed upon by both parties,  written notice of intention " +
                "to renew the lease shall be served to the LESSOR not later than seven (7) " +
                "days prior to the expiry date of the period herein agreed upon.\n\n" +
                "3. RENTAL RATE:   The monthly rental rate for the leased premises shall be " +
                "in PESOS: AMOUNT IN WORDS (P 00,000.00), Philippine Currency. All rental " +
                "payments shall be payable to the LESSOR.\n\n4. DEPOSIT:   That the LESSEE " +
                "shall deposit to the LESSOR upon signing of this contract and prior to move-in " +
                "an amount equivalent to the rent for THREE (3) MONTHS or the sum of PESOS: " +
                "AMOUNT IN WORDS (P 00,000.00), Philippine Currency wherein the two (2) months " +
                "deposit shall be applied as rent for the 11th and 12th months and the remaining " +
                "one (1) month deposit shall answer partially for damages and any other obligations," +
                " for utilities such as Water, Electricity, CATV, Telephone, Association Dues " +
                "or resulting from violation(s) of any of the provision of this contract.\n\n" +
                "5. LATE FEE:   That the LESSEE shall pay for rent/water/electrity after the " +
                "day of ____ each month will be deemed as late; and if rent/water/electricity " +
                "is not paid within PENALTYWEEK week(s) after such due date, the LESSEE agrees to pay a " +
                "late charge of PENALTYPERCENT % of the actual amount.\n\n6. DEFAULT PAYMENT:  In case of " +
                "default by the LESSEE in the payment of the rent, such as when the checks are " +
                "dishonored, the LESSOR at its option may terminate this contract and eject the" +
                " LESSEE. The LESSOR has the right to padlock the premises when the LESSEE is in" +
                " default of payment for One (1) month and may forfeit whatever rental deposit or" +
                " advances have been given by the LESSEE.\n\n7. SUB-LEASE:   The LESSEE shall not" +
                " directly orindirectly sublet, allow or permit the leased premises to be occupied " +
                "in whole or in part by any person, form or corporation, neither shall the LESSEE" +
                " assign its rights hereunder to any other person or entity and no right of " +
                "interest thereto or therein shall be conferred on or vested in anyone by the" +
                " LESSEE without the LESSOR'S written approval.\n\n" +
                "8. PUBLIC UTILITIES:   The LESSEE shall pay for its telephone, electric, cable " +
                "TV, water, Internet, association dues and other public services and utilities" +
                " during the duration of the lease.\n\n9. FORCE MAJEURE:   If whole or any part " +
                "of the leased premises shall be destroyed or damaged by fire, flood, lightning," +
                " typhoon, earthquake, storm, riot or any other unforeseen disabling cause of " +
                "acts of God, as to render the leased premises during the term substantially" +
                " unfit for use and occupation of the LESSEE, then this lease contract may be " +
                "terminated without compensation by the LESSOR or by the LESSEE by notice in" +
                " writing to the other.\n\n" +
                "10. LESSOR'S RIGHT OF ENTRY:   The LESSOR or its authorized agent shall after" +
                " giving due notice to the LESSEE shall have the right to enter the premises " +
                "in the presence of the LESSEE or its representative at any reasonable hour to" +
                " examine the same or make repairs therein or for the operation and maintenance " +
                "of the building or to exhibit the leased premises to prospective LESSEE, or for " +
                "any other lawful purposes which it may deem necessary.\n\n" +
                "11. EXPIRATION OF LEASE:   At the expiration of the term of this lease or " +
                "cancellation thereof, as herein provided, the LESSEE will promptly deliver" +
                " to the LESSOR the leased premises with all corresponding keys and in as good " +
                "and tenable condition as the same is now, ordinary wear and tear expected devoid " +
                "of all occupants, movable furniture, articles and effects of any kind. " +
                "Non-compliance with the terms of this clause by the LESSEE will give the " +
                "LESSOR the right, at the latter's option, to refuse to accept the delivery " +
                "of the premises and compel the LESSEE to pay rent therefrom at the same rate " +
                "plus Twenty Five (25) % thereof as penalty until the LESSEE shall have complied " +
                "with the terms hereof.  The same penalty shall be imposed in case the " +
                "LESSEE fails to leave the premises after\n\n" +
                "12. JUDICIAL RELIEF:   Should any one of the parties herein be compelled to" +
                " seek judicial relief against the other, the losing party shall pay an amount " +
                "of One Hundred (100) % of the amount clamed in the complaint as attorney's " +
                "fees which shall in no case be less than P50,000.00 pesos in addition to other " +
                "cost and damages which the said party may be entitled to under the law.\n" + additionalTermString +
                "\nThis CONTRACT OF LEASE shall be valid and binding between the parties, " +
                "their successors-in-interest and assigns." + "\n\n";

        String contract2 = "\nIN WITNESS WHEREOF, parties " +
                "herein affixed their signatures on the date and place above written.\n\n" +
                "Signed in the presence of:\n\n LESSOR \n\n ____________________ \n\n\n____________________" +
                "\n\n\nACKNOWLEDGEMENT Republic of the Philippines) City OF CITY ) S.S" +
                "\n\n\nBEFORE ME, a notary public, for and in the City of CITY, Philippines, " +
                "the above-named persons personally persons personally appeared and represented " +
                "tome to be same persons who executed the foregoing instrument consisting of  " +
                "___ pages including this page and that the same persons acknowledged to me " +
                "that the same is their free and voluntary act and deed." +
                "\n\n\nWITNESS MY HAND AND SEAL, on the date and place first above written.\n\n\n" +
                "Notary Public   \n\n\nDoc. No.______; \n\nPage No. ______; \n\nBook No.______; " +
                "\n\nSeries of 20___.";


        //replacing text in the Contract
        String modifiedContract = contract
                .replaceAll("PENALTYWEEK",  penaltyWeek)
                .replaceAll("PENALTYPERCENT", penaltyPercent)
                .replaceAll("LESSOR",  name)
                .replaceAll("HOUSE#", house_no)
                .replaceAll("STREET",  street)
                .replaceAll("SUBDIVISION", subd)
                .replaceAll("BARANGAY",  brgy)
                .replaceAll("CITY", city)
                .replaceAll("ZIPCODE",  zipcode)
                .replaceAll("DATE", date);

        String modifiedContract2 = contract2
                .replaceAll("PENALTYWEEK",  penaltyWeek)
                .replaceAll("PENALTYPERCENT", penaltyPercent)
                .replaceAll("LESSOR",  name)
                .replaceAll("HOUSE#", house_no)
                .replaceAll("STREET",  street)
                .replaceAll("SUBDIVISION", subd)
                .replaceAll("BARANGAY",  brgy)
                .replaceAll("CITY", city)
                .replaceAll("ZIPCODE",  zipcode)
                .replaceAll("DATE", date);


        contractTemplate.setText(modifiedContract);
        contractTemplate2.setText(modifiedContract2);


    }


    public void printPDF() {

        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint();


        //creation of PDF
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        forLinePaint.setStrokeWidth(2);

        paint.setTextSize(25f);
        paint.setColor(Color.rgb(0, 0, 0));

        //texts on the receipt and texts on the receipt and
        canvas.drawText("Bill-It Contract", 220, 80, paint);
        paint.setTextSize(20f);
        canvas.drawText("KNOW ALL MEN BY THESE PRESENTS:", 40, 120, paint);
        paint.setTextSize(12f);
        canvas.drawText("This CONTRACT OF LEASE is made and executed at the City of " + city + " this day of", 40, 160, paint);
        canvas.drawText("_____________, by and between: "+ name +", of legal age, Filipino,and with residence", 40, 175, paint);
        canvas.drawText("and postal address at #"+ house_no + ", " + street + ", " + subd +", "+ brgy +", "+ city + " hereinafter", 40, 190, paint);
        canvas.drawText( "referred to as the LESSOR.  -AND- ________________________,Filipino and with residence", 40, 205, paint);
        canvas.drawText("referred to as the LESSEE. and postal address at ________________________, hereinafter", 40, 220, paint);
        canvas.drawText("", 40, 235, paint);

        canvas.drawText("WITNESSETH; That WHEREAS, the LESSOR is the owner of THE LEASED PREMISES, at", 40, 265, paint);
        canvas.drawText("residential property situated at "+ house_no + " " + street + " "+ subd +" "+ brgy +" "+ city + ";WHEREAS, the" , 40, 280, paint);
        canvas.drawText("LESSOR agrees to lease-out the property to the LESSEE and the LESSEE is willing to", 40, 295, paint);
        canvas.drawText("lease the same; NOW THEREFORE, for and in consideration of the foregoing premises,", 40, 310, paint);
        canvas.drawText("the LESSOR leases unto the LESSEE and the LESSEE hereby accepts from the LESSOR", 40, 325, paint);
        canvas.drawText("the LEASED premises, subject to the following:", 40, 340, paint);
        paint.setTextSize(20f);
        canvas.drawText("TERMS AND CONDITIONS:", 40, 380, paint);
        paint.setTextSize(12f);
        canvas.drawText("1. PURPOSES:   That premises hereby leased shall be used exclusively by the LESSEE for", 40, 395, paint);
        canvas.drawText("residential purposes only and shall not be diverted to other uses. It is hereby expressly  ", 40, 410, paint);
        canvas.drawText("agreed that if at any  time the premises are used for other purposes, the LESSOR shall ", 40, 425, paint);
        canvas.drawText("have the right to rescind this contract without prejudice to its other rights under the law.  ", 40, 440, paint);

        canvas.drawText("2. TERM:  This term of lease is for ONE (1) YEAR. from (Date) to (Date) inclusive. Upon its", 40, 465, paint);
        canvas.drawText("expiration, this lease may be renewed under such terms and conditions as my be mutu-", 40, 480, paint);
        canvas.drawText("ally agreed upon by both parties, written notice of intention to renew the lease shall be ", 40, 495, paint);
        canvas.drawText("served to the LESSOR not later than seven (7) days prior to the expiry date of the period", 40, 510, paint);
        canvas.drawText("herein agreed upon. ", 40, 525, paint);

        canvas.drawText("3. RENTAL RATE:   The monthly rental rate for the leased premises shall be in PESOS:", 40, 550, paint);
        canvas.drawText("AMOUNT IN WORDS (P 00,000.00), Philippine Currency. All rental payments shall be", 40, 565, paint);
        canvas.drawText("payable to the LESSOR.", 40, 580, paint);

        canvas.drawText("4. DEPOSIT:   That the LESSEE shall deposit to the LESSOR upon signing of this contract", 40, 605, paint);
        canvas.drawText("and prior to move-in an amount equivalent to the rent for THREE (3) MONTHS or the", 40, 620, paint);
        canvas.drawText("sum of PESOS: AMOUNT IN WORDS (P 00,000.00), Philippine Currency wherein the two", 40, 635, paint);
        canvas.drawText("(2) months deposit shall be applied as rent for the 11th and 12th months and the", 40, 650, paint);
        canvas.drawText("remaining one (1) month deposit shall answer partially for damages and any other", 40, 665, paint);
        canvas.drawText("obligations, for utilities such as Water, Electricity, CATV, Telephone, Association Dues", 40, 680, paint);
        canvas.drawText("or resulting from violation(s) of any of the provision of this contract", 40, 695, paint);

        canvas.drawText("5. LATE FEE:   That the LESSEE shall pay for rent/water/electrity after the day of " + "__" + " each", 40, 720, paint);
        canvas.drawText("month will be deemed as late; and if rent/water/electricity is not paid within " + penaltyWeek + " weeks", 40, 735, paint);
        canvas.drawText( "after such due date, the LESSEE agrees to pay a late charge of " + penaltyPercent + "%" + " of the actual amount.", 40, 750, paint);
        canvas.drawText("", 40, 765, paint);



        myPdfDocument.finishPage(myPage);

        PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
        PdfDocument.Page myPage2 = myPdfDocument.startPage(myPageInfo2);
        Canvas canvas2 = myPage2.getCanvas();

        paint.setTextSize(12f);
        canvas2.drawText("6. DEFAULT PAYMENT:  In case of default by the LESSEE in the payment of the rent, such", 40, 80, paint);
        canvas2.drawText("as when the checks are dishonored, the LESSOR at its option may terminate this contract", 40, 95, paint);
        canvas2.drawText("and eject the LESSEE. The LESSOR has the right to padlock the premises when the LESSEE ", 40, 110, paint);
        canvas2.drawText("is in default of payment for One (1) month and may forfeit whatever rental deposit or", 40, 125, paint);
        canvas2.drawText("advances have been given by the LESSEE.  ", 40, 140, paint);

        canvas2.drawText("7. SUB-LEASE:   The LESSEE shall not directly or indirectly sublet, allow or permit the ", 40, 165, paint);
        canvas2.drawText("leased premises to be occupied in whole or in part by any person, form or corporation,", 40, 180, paint);
        canvas2.drawText("neither shall the LESSEE assign its rights hereunder to any other person or entity and no", 40, 195, paint);
        canvas2.drawText("right of interest thereto or therein shall be conferred on or vested in anyone by the", 40, 210, paint);
        canvas2.drawText("LESSEE without the LESSOR'S written approval.", 40, 225, paint);

        canvas2.drawText("8. PUBLIC UTILITIES:   The LESSEE shall pay for its telephone, electric, cable TV, water,", 40, 250, paint);
        canvas2.drawText("Internet, association dues and other public services and utilities during the duration of the ", 40, 265, paint);
        canvas2.drawText("lease.", 40, 280, paint);

        canvas2.drawText("9. FORCE MAJEURE:   If whole or any part of the leased premises shall be destroyed or", 40, 305, paint);
        canvas2.drawText("damaged by fire, flood, lightning, typhoon, earthquake, storm, riot or any other unforeseen", 40, 320, paint);
        canvas2.drawText("disabling cause of acts of God, as to render the leased premises during the the term ", 40, 335, paint);
        canvas2.drawText("substantially unfit for use and occupation of the LESSEE, then this lease contract may be ", 40, 350, paint);
        canvas2.drawText("terminated without compensation by the LESSOR or by the LESSEE by notice in writing to ", 40, 365, paint);
        canvas2.drawText("the other.", 40, 380, paint);

        canvas2.drawText("10. LESSOR'S RIGHT OF ENTRY:   The LESSOR or its authorized agent shall after giving", 40, 405, paint);
        canvas2.drawText("due notice to the LESSEE shall have the right to enter the premises in the presence of", 40, 420, paint);
        canvas2.drawText("the LESSEE or its representative at any reasonable hour to examine the same or make", 40, 435, paint);
        canvas2.drawText("repairs therein or for the operation and maintenance of the building or to exhibit the", 40, 450, paint);
        canvas2.drawText("leased premises premises to prospective LESSEE, or for any other lawful purposes which", 40, 465, paint);
        canvas2.drawText("it may deem necessary.", 40, 480, paint);

        canvas2.drawText("11. EXPIRATION OF LEASE:   At the expiration of the term of this lease or cancellation", 40, 505, paint);
        canvas2.drawText("thereof, as herein provided, the LESSEE will promptly deliver to the LESSOR the leased ", 40, 520, paint);
        canvas2.drawText("the leased premises with all corresponding keys and in as good and tenable condition", 40, 535, paint);
        canvas2.drawText("as the same is now, ordinary wear and tear expected devoid of all occupants, movable", 40, 550, paint);
        canvas2.drawText("furniture, articles and effects of any kind. Non-compliance with the terms of this clause", 40, 565, paint);
        canvas2.drawText("by the LESSEE will give the LESSOR the right, at the latter's option, to refuse to accept", 40, 580, paint);
        canvas2.drawText("the delivery of the premises and compel the LESSEE to pay rent therefrom at the same", 40, 595, paint);
        canvas2.drawText("rate plus Twenty Five (25) % thereof as penalty until the LESSEE shall have complied", 40, 610, paint);
        canvas2.drawText("with the terms hereof. The same penalty shall be imposed in case the LESSEE fails to ", 40, 625, paint);
        canvas2.drawText("leave the premises after the expiration of this Contract of Lease or termination for any ", 40, 640, paint);
        canvas2.drawText("reason whatsoever.", 40, 655, paint);

        canvas2.drawText("12. JUDICIAL RELIEF:   Should any one of the parties herein be compelled to seek", 40, 680, paint);
        canvas2.drawText("judicial relief against the other, the losing party shall pay an amount of One Hundred", 40, 695, paint);
        canvas2.drawText("(100)%of the amount clamed in the complaint as attorney's fees which shall in no", 40, 710, paint);
        canvas2.drawText("case be less less than P50,000.00 pesos in addition to other cost and damages which", 40, 725, paint);
        canvas2.drawText("the said party may be entitled to under the law.  ", 40, 740, paint);

//        canvas2.drawText("This CONTRACT OF LEASE shall be valid and binding between the parties, their ", 40, 765, paint);
//        canvas2.drawText("successors-in-interest and assigns.  ", 40, 780, paint);

        myPdfDocument.finishPage(myPage2);

//        PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(595, 842, 3).create();
//        PdfDocument.Page myPage3 = myPdfDocument.startPage(myPageInfo3);
//        Canvas canvas3 = myPage3.getCanvas();
//        String text = additionalTermString;
//        TextPaint textPaint = new TextPaint();
//        paint.setTextSize(12f);
//
//        StaticLayout layout = new StaticLayout(text, textPaint, 460, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//        canvas3.save();
//        canvas3.translate(40, 80);
//
//        layout.draw(canvas3);
//
//        canvas3.restore();
//        myPdfDocument.finishPage(myPage3);

        PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(595, 842, 3).create();
        PdfDocument.Page myPage3 = myPdfDocument.startPage(myPageInfo3);
        Canvas canvas3 = myPage3.getCanvas();
        Canvas canvas5 = myPage3.getCanvas();
        String text = additionalTermString;
        String text2 ="This CONTRACT OF LEASE shall be valid and binding between the parties, their successors-in-interest and assigns.";
        TextPaint textPaint = new TextPaint();
        paint.setTextSize(12f);

        StaticLayout layout = new StaticLayout(text, textPaint, 460, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        StaticLayout layout2 = new StaticLayout(text2, textPaint, 460, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        int yPos = 80; // Initial vertical position

        canvas3.save();
        canvas3.translate(40, yPos);

        layout.draw(canvas3);

// Add vertical spacing between the two layouts
        yPos += layout.getHeight() + 20; // 20 is the spacing value

        canvas3.restore();
        canvas3.save();
        canvas3.translate(40, yPos);

        layout2.draw(canvas3);

        canvas3.restore();

        myPdfDocument.finishPage(myPage3);



        PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(595, 842, 3).create();
        PdfDocument.Page myPage4 = myPdfDocument.startPage(myPageInfo4);
        Canvas canvas4 = myPage4.getCanvas();
        paint.setTextSize(12f);


        //notary part
        canvas4.drawText("IN WITNESS WHEREOF, parties herein affixed their signatures on the date and place", 40, 80, paint);
        canvas4.drawText("above written.", 40, 95, paint);
        canvas4.drawText("Signed in the presence of:", 40, 120, paint);
        canvas4.drawText("____________________________________", 40, 160, paint);
        canvas4.drawText(name, 90, 150, paint);
        canvas4.drawText("____________________________________", 40, 210, paint);
        canvas4.drawText("LESSEE", 90, 225, paint);

        //notary part
        canvas4.drawText("ACKNOWLEDGEMENT  S.S", 40, 265, paint);
        canvas4.drawText("Republic of the Philippines)", 40, 280, paint);
        canvas4.drawText("City OF "+ city +" ) S.S", 40, 305, paint);
        canvas4.drawText("BEFORE ME, a notary public, for and in the City of "+ city +", Philippines, the above-named", 40, 330, paint);
        canvas4.drawText("persons personally persons personally appeared and represented to me to be same persons", 40, 355, paint);

        canvas4.drawText("who executed the foregoing instrument consisting of ___ pages including this page and that", 40, 380, paint);
        canvas4.drawText("the same persons acknowledged to me that the same is their free and voluntary act and deed.", 40, 395, paint);

        canvas4.drawText("WITNESS MY HAND AND SEAL, on the date and place first above written.", 40, 425, paint);
        canvas4.drawText("Notary Public", 40, 440, paint);
        canvas4.drawText("Doc. No.______;", 40, 465, paint);
        canvas4.drawText("Page No. ______;", 40, 485, paint);
        canvas4.drawText("Book No.______;", 40, 510, paint);
        canvas4.drawText("Series of 20___.", 40, 535, paint);

        myPdfDocument.finishPage(myPage4);

        File file = new File(dir, "Contract"+ "_" + datePatternFormat.format(new Date()) + "-bill.pdf");


        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();
    }

    public void printPDF2() {

        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint();


        //creation of PDF
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        forLinePaint.setStrokeWidth(2);

        paint.setTextSize(25f);
        paint.setColor(Color.rgb(0, 0, 0));

        //texts on the receipt and texts on the receipt and
        canvas.drawText("Bill-It Contract", 220, 80, paint);
        paint.setTextSize(20f);
        canvas.drawText("KNOW ALL MEN BY THESE PRESENTS:", 40, 120, paint);
        paint.setTextSize(12f);
        canvas.drawText("This CONTRACT OF LEASE is made and executed at the City of " + city + " this day of", 40, 160, paint);
        canvas.drawText("_____________, by and between: "+ name +", of legal age, Filipino,and with residence", 40, 175, paint);
        canvas.drawText("and postal address at #"+ house_no + ", " + street + ", " + subd +", "+ brgy +", "+ city + " hereinafter", 40, 190, paint);
        canvas.drawText( "referred to as the LESSOR.  -AND- ________________________,Filipino and with residence", 40, 205, paint);
        canvas.drawText("referred to as the LESSEE. and postal address at ________________________, hereinafter", 40, 220, paint);
        canvas.drawText("", 40, 235, paint);

        canvas.drawText("WITNESSETH; That WHEREAS, the LESSOR is the owner of THE LEASED PREMISES, at", 40, 265, paint);
        canvas.drawText("residential property situated at "+ house_no + " " + street + " "+ subd +" "+ brgy +" "+ city + ";WHEREAS, the" , 40, 280, paint);
        canvas.drawText("LESSOR agrees to lease-out the property to the LESSEE and the LESSEE is willing to", 40, 295, paint);
        canvas.drawText("lease the same; NOW THEREFORE, for and in consideration of the foregoing premises,", 40, 310, paint);
        canvas.drawText("the LESSOR leases unto the LESSEE and the LESSEE hereby accepts from the LESSOR", 40, 325, paint);
        canvas.drawText("the LEASED premises, subject to the following:", 40, 340, paint);
        paint.setTextSize(20f);
        canvas.drawText("TERMS AND CONDITIONS:", 40, 380, paint);
        paint.setTextSize(12f);
        canvas.drawText("1. PURPOSES:   That premises hereby leased shall be used exclusively by the LESSEE for", 40, 395, paint);
        canvas.drawText("residential purposes only and shall not be diverted to other uses. It is hereby expressly  ", 40, 410, paint);
        canvas.drawText("agreed that if at any  time the premises are used for other purposes, the LESSOR shall ", 40, 425, paint);
        canvas.drawText("have the right to rescind this contract without prejudice to its other rights under the law.  ", 40, 440, paint);

        canvas.drawText("2. TERM:  This term of lease is for ONE (1) YEAR. from (Date) to (Date) inclusive. Upon its", 40, 465, paint);
        canvas.drawText("expiration, this lease may be renewed under such terms and conditions as my be mutu-", 40, 480, paint);
        canvas.drawText("ally agreed upon by both parties, written notice of intention to renew the lease shall be ", 40, 495, paint);
        canvas.drawText("served to the LESSOR not later than seven (7) days prior to the expiry date of the period", 40, 510, paint);
        canvas.drawText("herein agreed upon. ", 40, 525, paint);

        canvas.drawText("3. RENTAL RATE:   The monthly rental rate for the leased premises shall be in PESOS:", 40, 550, paint);
        canvas.drawText("AMOUNT IN WORDS (P 00,000.00), Philippine Currency. All rental payments shall be", 40, 565, paint);
        canvas.drawText("payable to the LESSOR.", 40, 580, paint);

        canvas.drawText("4. DEPOSIT:   That the LESSEE shall deposit to the LESSOR upon signing of this contract", 40, 605, paint);
        canvas.drawText("and prior to move-in an amount equivalent to the rent for THREE (3) MONTHS or the", 40, 620, paint);
        canvas.drawText("sum of PESOS: AMOUNT IN WORDS (P 00,000.00), Philippine Currency wherein the two", 40, 635, paint);
        canvas.drawText("(2) months deposit shall be applied as rent for the 11th and 12th months and the", 40, 650, paint);
        canvas.drawText("remaining one (1) month deposit shall answer partially for damages and any other", 40, 665, paint);
        canvas.drawText("obligations, for utilities such as Water, Electricity, CATV, Telephone, Association Dues", 40, 680, paint);
        canvas.drawText("or resulting from violation(s) of any of the provision of this contract", 40, 695, paint);

        canvas.drawText("5. LATE FEE:   That the LESSEE shall pay for rent/water/electrity after the day of " + "__" + " each", 40, 720, paint);
        canvas.drawText("month will be deemed as late; and if rent/water/electricity is not paid within " + penaltyWeek + " weeks", 40, 735, paint);
        canvas.drawText( "after such due date, the LESSEE agrees to pay a late charge of " + penaltyPercent + "%" + " of the actual amount.", 40, 750, paint);
        canvas.drawText("", 40, 765, paint);



        myPdfDocument.finishPage(myPage);

        PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
        PdfDocument.Page myPage2 = myPdfDocument.startPage(myPageInfo2);
        Canvas canvas2 = myPage2.getCanvas();

        paint.setTextSize(12f);
        canvas2.drawText("6. DEFAULT PAYMENT:  In case of default by the LESSEE in the payment of the rent, such", 40, 80, paint);
        canvas2.drawText("as when the checks are dishonored, the LESSOR at its option may terminate this contract", 40, 95, paint);
        canvas2.drawText("and eject the LESSEE. The LESSOR has the right to padlock the premises when the LESSEE ", 40, 110, paint);
        canvas2.drawText("is in default of payment for One (1) month and may forfeit whatever rental deposit or", 40, 125, paint);
        canvas2.drawText("advances have been given by the LESSEE.  ", 40, 140, paint);

        canvas2.drawText("7. SUB-LEASE:   The LESSEE shall not directly or indirectly sublet, allow or permit the ", 40, 165, paint);
        canvas2.drawText("leased premises to be occupied in whole or in part by any person, form or corporation,", 40, 180, paint);
        canvas2.drawText("neither shall the LESSEE assign its rights hereunder to any other person or entity and no", 40, 195, paint);
        canvas2.drawText("right of interest thereto or therein shall be conferred on or vested in anyone by the", 40, 210, paint);
        canvas2.drawText("LESSEE without the LESSOR'S written approval.", 40, 225, paint);

        canvas2.drawText("8. PUBLIC UTILITIES:   The LESSEE shall pay for its telephone, electric, cable TV, water,", 40, 250, paint);
        canvas2.drawText("Internet, association dues and other public services and utilities during the duration of the ", 40, 265, paint);
        canvas2.drawText("lease.", 40, 280, paint);

        canvas2.drawText("9. FORCE MAJEURE:   If whole or any part of the leased premises shall be destroyed or", 40, 305, paint);
        canvas2.drawText("damaged by fire, flood, lightning, typhoon, earthquake, storm, riot or any other unforeseen", 40, 320, paint);
        canvas2.drawText("disabling cause of acts of God, as to render the leased premises during the the term ", 40, 335, paint);
        canvas2.drawText("substantially unfit for use and occupation of the LESSEE, then this lease contract may be ", 40, 350, paint);
        canvas2.drawText("terminated without compensation by the LESSOR or by the LESSEE by notice in writing to ", 40, 365, paint);
        canvas2.drawText("the other.", 40, 380, paint);

        canvas2.drawText("10. LESSOR'S RIGHT OF ENTRY:   The LESSOR or its authorized agent shall after giving", 40, 405, paint);
        canvas2.drawText("due notice to the LESSEE shall have the right to enter the premises in the presence of", 40, 420, paint);
        canvas2.drawText("the LESSEE or its representative at any reasonable hour to examine the same or make", 40, 435, paint);
        canvas2.drawText("repairs therein or for the operation and maintenance of the building or to exhibit the", 40, 450, paint);
        canvas2.drawText("leased premises premises to prospective LESSEE, or for any other lawful purposes which", 40, 465, paint);
        canvas2.drawText("it may deem necessary.", 40, 480, paint);

        canvas2.drawText("11. EXPIRATION OF LEASE:   At the expiration of the term of this lease or cancellation", 40, 505, paint);
        canvas2.drawText("thereof, as herein provided, the LESSEE will promptly deliver to the LESSOR the leased ", 40, 520, paint);
        canvas2.drawText("the leased premises with all corresponding keys and in as good and tenable condition", 40, 535, paint);
        canvas2.drawText("as the same is now, ordinary wear and tear expected devoid of all occupants, movable", 40, 550, paint);
        canvas2.drawText("furniture, articles and effects of any kind. Non-compliance with the terms of this clause", 40, 565, paint);
        canvas2.drawText("by the LESSEE will give the LESSOR the right, at the latter's option, to refuse to accept", 40, 580, paint);
        canvas2.drawText("the delivery of the premises and compel the LESSEE to pay rent therefrom at the same", 40, 595, paint);
        canvas2.drawText("rate plus Twenty Five (25) % thereof as penalty until the LESSEE shall have complied", 40, 610, paint);
        canvas2.drawText("with the terms hereof. The same penalty shall be imposed in case the LESSEE fails to ", 40, 625, paint);
        canvas2.drawText("leave the premises after the expiration of this Contract of Lease or termination for any ", 40, 640, paint);
        canvas2.drawText("reason whatsoever.", 40, 655, paint);

        canvas2.drawText("12. JUDICIAL RELIEF:   Should any one of the parties herein be compelled to seek", 40, 680, paint);
        canvas2.drawText("judicial relief against the other, the losing party shall pay an amount of One Hundred", 40, 695, paint);
        canvas2.drawText("(100)%of the amount clamed in the complaint as attorney's fees which shall in no", 40, 710, paint);
        canvas2.drawText("case be less less than P50,000.00 pesos in addition to other cost and damages which", 40, 725, paint);
        canvas2.drawText("the said party may be entitled to under the law.  ", 40, 740, paint);

//        canvas2.drawText("This CONTRACT OF LEASE shall be valid and binding between the parties, their ", 40, 765, paint);
//        canvas2.drawText("successors-in-interest and assigns.  ", 40, 780, paint);

        myPdfDocument.finishPage(myPage2);



        PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(595, 842, 3).create();
        PdfDocument.Page myPage4 = myPdfDocument.startPage(myPageInfo4);
        Canvas canvas4 = myPage4.getCanvas();
        paint.setTextSize(12f);


        //notary part
        canvas4.drawText("IN WITNESS WHEREOF, parties herein affixed their signatures on the date and place", 40, 80, paint);
        canvas4.drawText("above written.", 40, 95, paint);
        canvas4.drawText("Signed in the presence of:", 40, 120, paint);
        canvas4.drawText("____________________________________", 40, 160, paint);
        canvas4.drawText(name, 90, 150, paint);
        canvas4.drawText("____________________________________", 40, 210, paint);
        canvas4.drawText("LESSEE", 90, 225, paint);

        //notary part
        canvas4.drawText("ACKNOWLEDGEMENT  S.S", 40, 265, paint);
        canvas4.drawText("Republic of the Philippines)", 40, 280, paint);
        canvas4.drawText("City OF "+ city +" ) S.S", 40, 305, paint);
        canvas4.drawText("BEFORE ME, a notary public, for and in the City of "+ city +", Philippines, the above-named", 40, 330, paint);
        canvas4.drawText("persons personally persons personally appeared and represented to me to be same persons", 40, 355, paint);

        canvas4.drawText("who executed the foregoing instrument consisting of ___ pages including this page and that", 40, 380, paint);
        canvas4.drawText("the same persons acknowledged to me that the same is their free and voluntary act and deed.", 40, 395, paint);

        canvas4.drawText("WITNESS MY HAND AND SEAL, on the date and place first above written.", 40, 425, paint);
        canvas4.drawText("Notary Public", 40, 440, paint);
        canvas4.drawText("Doc. No.______;", 40, 465, paint);
        canvas4.drawText("Page No. ______;", 40, 485, paint);
        canvas4.drawText("Book No.______;", 40, 510, paint);
        canvas4.drawText("Series of 20___.", 40, 535, paint);

        myPdfDocument.finishPage(myPage4);

        File file = new File(dir, "Contract" + "_" + datePatternFormat.format(new Date()) + "-bill.pdf");


        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();


    }


}