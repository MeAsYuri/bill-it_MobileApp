package com.example.billit_all.Contract_creation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.EditTenant;
import com.example.billit_all.Login;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ContractSigning extends AppCompatActivity {
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");

    Button downloadPDF, proceedBtn;
    String additionalTermString;
    String house_no, street, subd,  brgy, city, zipcode, name, penaltyPercent, penaltyWeek,formattedDate, formattedDate2;
    String tenant_name, tenant_houseNo, tenant_street, tenant_subd, tenant_brgy, tenant_city, tenant_zip;

    TextView Lname, LpenaltyPercent, LpenaltyWeek, LrentFee, LhouseNo, Lstreet, Lsubd, Lbrgy, Lcity, Lzip;
    TextView LformattedDate, LformattedDate2, Ldeposit;
    int rent_fee, deposit;
    int countTerms = 12;


    ImageView contractImage;
    ProgressBar progressBar;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int REQUEST_PERMISSIONS = 100;
    Bitmap bitmap;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_signing);

        downloadPDF = findViewById(R.id.downloadPDF);
        proceedBtn = findViewById(R.id.proceedBtn);
        contractImage = findViewById(R.id.contractImage);

        Lname = findViewById(R.id.Lname);
        LpenaltyPercent = findViewById(R.id.LpenaltyPercent);
        LpenaltyWeek = findViewById(R.id.LpenaltyWeek);
        LrentFee =findViewById(R.id.LrentFee);
        LhouseNo = findViewById(R.id.LhouseNo);
        Lstreet = findViewById(R.id.Lstreet);
        Lsubd = findViewById(R.id.Lsubd);
        Lbrgy = findViewById(R.id.Lbrgy);
        Lcity = findViewById(R.id.Lcity);
        Lzip = findViewById(R.id.Lzip);
        LformattedDate = findViewById(R.id.LformattedDate);
        LformattedDate2 = findViewById(R.id.LformattedDate2);
        Ldeposit = findViewById(R.id.Ldeposit);

        Intent b = getIntent();
        //from Edit Text Inputs
        tenant_name = b.getStringExtra("tenant_name");
        tenant_houseNo = b.getStringExtra("tenant_houseNo");
        tenant_street = b.getStringExtra("tenant_street");
        tenant_subd = b.getStringExtra("tenant_subd");
        tenant_brgy = b.getStringExtra("tenant_brgy");
        tenant_city = b.getStringExtra("tenant_city");
        tenant_zip = b.getStringExtra("tenant_zip");
        Log.d("dataTenant", tenant_name);
        Log.d("dataTenant", tenant_houseNo);
        Log.d("dataTenant", tenant_street);
        Log.d("dataTenant", tenant_subd);
        Log.d("dataTenant", tenant_brgy);
        Log.d("dataTenant", tenant_city);
        Log.d("dataTenant", tenant_zip);


        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
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
                                String landlord_id = user.getString("landlord_id");
                                String id = user.getString("user_id");
                                Log.d("USER_ID", id);


//                            get contract_specs_id
                                RequestQueue queue9 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request9 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/contract/temp-acct/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONObject user = response.getJSONObject("data");
                                                    Log.d("contract_id", String.valueOf(user));
                                                    String contract_id = user.getString("contract_spec_id");
                                                    Log.d("contract_id", contract_id);

                                                    //TNC

                                                    RequestQueue queue6 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                    BackendJsonObjectRequest request6 = new BackendJsonObjectRequest(
                                                            Request.Method.GET,
                                                            "/api/contract/temp-acct/tnc/" + contract_id,
                                                            null,
                                                            new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    try {
                                                                        JSONArray tenantsJson1 = response.getJSONArray("tnc");

//                                                                        ArrayList<List<String>> TNC = new ArrayList<>();
                                                                        ArrayList<String> SUBJECT = new ArrayList<>();
                                                                        ArrayList<String> BODY = new ArrayList<>();

                                                                        for (int j = 0; j < tenantsJson1.length(); j++) {
                                                                            JSONObject tenant = tenantsJson1.getJSONObject(j);
                                                                            String subject = tenant.getString("subject");
                                                                            String body = tenant.getString("body");

                                                                            SUBJECT.add(subject);
                                                                            BODY.add(body);
//                                                                            TNC.add(Arrays.asList(subject,body));
//                                                                            Log.d("arrayListTNC", String.valueOf(TNC));
                                                                        }

                                                                        //pairing of TNC
                                                                        ArrayList<String> pairs = new ArrayList<>();
                                                                        for (int i = 0; i < SUBJECT.size(); i++) {
                                                                            String pair = (++countTerms) + "." + " " + SUBJECT.get(i) + ":  " + BODY.get(i);
                                                                            pairs.add(pair);

                                                                        }
                                                                        //(Edit by pair)
                                                                        String text = "";
                                                                        for (String pair : pairs) {
                                                                            text += "\n" + pair + "\n";
                                                                        }

                                                                        additionalTermString = text;
                                                                        Log.d("arrayListTNC", text);


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

                                                    request6.authenticated(getApplicationContext());
                                                    queue6.add(request6);

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

                                request9.authenticated(getApplicationContext());
                                queue9.add(request9);


                                //Contract Details
                                RequestQueue queue0 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request0 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/contract/temp-acct/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {

                                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                                                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                    outputFormat.setTimeZone(TimeZone.getDefault());
                                                    SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd");
                                                    outputFormat2.setTimeZone(TimeZone.getDefault());

                                                    JSONObject responseJSON = response.getJSONObject("data");

                                                    house_no = responseJSON.getString("house_no");
                                                    street = responseJSON.getString("street");
                                                    subd = responseJSON.getString("subd");
                                                    brgy = responseJSON.getString("brgy");
                                                    city = responseJSON.getString("city");
                                                    zipcode = responseJSON.getString("zipcode");
                                                    String created_at = responseJSON.getString("created_at");

                                                    name = responseJSON.getString("name");
                                                    penaltyPercent = responseJSON.getString("penalty_percentage");
                                                    penaltyWeek = responseJSON.getString("penalty_week");
                                                    rent_fee = responseJSON.getInt("rent_fee");

                                                    Date date = inputFormat.parse(created_at);
                                                    formattedDate = outputFormat.format(date);
                                                    formattedDate2 = outputFormat2.format(date);

                                                    deposit = rent_fee * 3;

                                                    Lname.setText(name);
                                                    LpenaltyPercent.setText(penaltyPercent);
                                                    LpenaltyWeek.setText(penaltyWeek);
                                                    LrentFee.setText(String.valueOf(rent_fee));
                                                    LhouseNo.setText(house_no);
                                                    Lstreet.setText(street);
                                                    Lsubd.setText(subd);
                                                    Lbrgy.setText(brgy);
                                                    Lcity.setText(city);
                                                    Lzip.setText(zipcode);
                                                    LformattedDate.setText(formattedDate);
                                                    LformattedDate2.setText(formattedDate2);
                                                    Ldeposit.setText(String.valueOf(deposit) );

                                                    Log.d("data", house_no);
                                                    Log.d("data", street);
                                                    Log.d("data", subd);
                                                    Log.d("data", brgy);
                                                    Log.d("data", city);
                                                    Log.d("data", zipcode);
                                                    Log.d("data", formattedDate);
                                                    Log.d("data", formattedDate2);
                                                    Log.d("data", name);
                                                    Log.d("data", penaltyPercent);
                                                    Log.d("data", penaltyWeek);
                                                    Log.d("data", String.valueOf(rent_fee));
                                                    Log.d("data", String.valueOf(deposit));



                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (ParseException e) {
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

                                request0.authenticated(getApplicationContext());
                                queue0.add(request0);


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
        //end of user id



        downloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("house_no", String.valueOf(house_no));
                Log.d("deposit", String.valueOf(deposit));
                Log.d("rent_fee", String.valueOf(rent_fee));
                Log.d("tenant_name", String.valueOf(tenant_name));
                Log.d("tenant_name", String.valueOf(tenant_houseNo));
                Log.d("tenant_name", String.valueOf(tenant_subd));
                Log.d("tenant_name", String.valueOf(tenant_street));
                Log.d("tenant_name", String.valueOf(tenant_city));
                Log.d("tenant_name", String.valueOf(tenant_zip));
                Log.d("additionalTermString", String.valueOf(additionalTermString));
                //printing PDF if additional terms is empty  if statement
                if (additionalTermString == null || additionalTermString.equals("")) {
                    printPDF2();
                } else {
                    printPDF();
                }
                Toast.makeText(getApplicationContext(), "Contract PDF exported", Toast.LENGTH_SHORT).show();

            }
        });

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(ContractSigning.this, ContractUpload.class);
                startActivity(a);
            }
        });


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
        canvas.drawText("This CONTRACT OF LEASE is made and executed at the City of " + Lcity.getText().toString() + " this day of", 40, 160, paint);
        canvas.drawText(LformattedDate.getText().toString() + ", by and between: "+ Lname.getText().toString() +", of legal age, Filipino, and with residence and postal", 40, 175, paint);
        canvas.drawText("address at #"+ LhouseNo.getText().toString() + ", " + Lstreet.getText().toString() + ", " + Lsubd.getText().toString() +", "+ Lbrgy.getText().toString() +", "+ Lcity.getText().toString() +", hereinafter referred to as the", 40, 190, paint);
        canvas.drawText("LESSOR. -AND- "+ tenant_name + ", Filipino and with residence and postal address at " +tenant_houseNo + " " + tenant_street  , 40, 205, paint);
        canvas.drawText(tenant_subd +" "+ tenant_brgy +" "+ tenant_city + ", hereinafter referred to as the LESSEE.", 40, 220, paint);
        canvas.drawText("", 40, 235, paint);
        canvas.drawText("WITNESSETH; That WHEREAS, the LESSOR is the owner of THE LEASED PREMISES,", 40, 265, paint);
        canvas.drawText("at residential property situated at "+ LhouseNo.getText().toString() + " " + Lstreet.getText().toString() + " "+ Lsubd.getText().toString() +" "+ Lbrgy.getText().toString() +" "+  Lcity.getText().toString() + "; WHEREAS,", 40, 280, paint);
        canvas.drawText("the LESSOR agrees to lease-out the property to the LESSEE and the LESSEE is willing to", 40, 295, paint);
        canvas.drawText("lease the same; NOW THEREFORE, for and in consideration of the foregoing premises,", 40, 310, paint);
        canvas.drawText("the LESSOR leases unto the LESSEE and the LESSEE hereby accepts from the LESSOR ", 40, 325, paint);
        canvas.drawText("the LEASED  premises, subject to the following:", 40, 340, paint);
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
        canvas.drawText("P" + LrentFee.getText().toString() +", Philippine Currency. All rental payments shall be payable to the LESSOR.", 40, 565, paint);
        canvas.drawText("", 40, 580, paint);

        canvas.drawText("4. DEPOSIT:   That the LESSEE shall deposit to the LESSOR upon signing of this contract", 40, 605, paint);
        canvas.drawText("and prior to move-in an amount equivalent to the rent for THREE (3) MONTHS or the sum", 40, 620, paint);
        canvas.drawText("of PESOS: " + Ldeposit.getText().toString() + ", Philippine Currency wherein the two (2) months deposit shall", 40, 635, paint);
        canvas.drawText("be applied as rent for the 11th and 12th months and the remaining one (1) month deposit", 40, 650, paint);
        canvas.drawText("shall answer partially for damages and any other obligations, for utilities such as Water,", 40, 665, paint);
        canvas.drawText("Electricity, CATV, Telephone, Association Dues or resulting from violation(s) of", 40, 680, paint);
        canvas.drawText("any of the provision of this contract", 40, 695, paint);

        canvas.drawText("5. LATE FEE:   That the LESSEE shall pay for rent/water/electrity after the day of " + LformattedDate2.getText().toString() + "", 40, 720, paint);
        canvas.drawText("each month will be deemed as late; and if rent/water/electricity is not paid within" + LpenaltyWeek.getText().toString() + "weeks", 40, 735, paint);
        canvas.drawText( "after such due date, the LESSEE agrees to pay a late charge of " + LpenaltyPercent.getText().toString() + "%" + " of the actual amount.", 40, 750, paint);
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

        canvas2.drawText("7. SUB-LEASE:   The LESSEE shall not directly or indirectly sublet, allow or permit the", 40, 165, paint);
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


        myPdfDocument.finishPage(myPage2);

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
        canvas4.drawText(Lname.getText().toString(), 90, 155, paint);
        canvas4.drawText("____________________________________", 40, 210, paint);
        canvas4.drawText(tenant_name, 90, 205, paint);

        //notary part
        canvas4.drawText("ACKNOWLEDGEMENT  S.S", 40, 265, paint);
        canvas4.drawText("Republic of the Philippines)", 40, 280, paint);
        canvas4.drawText("City OF "+ Lcity.getText().toString() +" ) S.S", 40, 305, paint);
        canvas4.drawText("BEFORE ME, a notary public, for and in the City of "+ Lcity.getText().toString() +", Philippines, the above-named", 40, 330, paint);
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
        canvas.drawText("This CONTRACT OF LEASE is made and executed at the City of " + Lcity.getText().toString() + " this day of", 40, 160, paint);
        canvas.drawText(LformattedDate.getText().toString() + ", by and between: "+ Lname.getText().toString() +", of legal age, Filipino, and with residence and postal", 40, 175, paint);
        canvas.drawText("address at #"+ LhouseNo.getText().toString() + ", " + Lstreet.getText().toString() + ", " + Lsubd.getText().toString() +", "+ Lbrgy.getText().toString() +", "+ Lcity.getText().toString() +", hereinafter referred to as the LESSOR.", 40, 190, paint);
        canvas.drawText("-AND- "+ tenant_name + ", Filipino and with residence and postal address at " +tenant_houseNo + " " + tenant_street  , 40, 205, paint);
        canvas.drawText(tenant_subd +" "+ tenant_brgy +" "+ tenant_city + ", hereinafter referred to as the LESSEE.", 40, 220, paint);
        canvas.drawText("", 40, 235, paint);
        canvas.drawText("WITNESSETH; That WHEREAS, the LESSOR is the owner of THE LEASED PREMISES,", 40, 265, paint);
        canvas.drawText("at residential property situated at "+ LhouseNo.getText().toString() + " " + Lstreet.getText().toString() + " "+ Lsubd.getText().toString() +" "+ Lbrgy.getText().toString() +" "+  Lcity.getText().toString() + "; WHEREAS, the", 40, 280, paint);
        canvas.drawText("LESSOR agrees to lease-out the property to the LESSEE and the LESSEE is willing to", 40, 295, paint);
        canvas.drawText("lease the same; NOW THEREFORE, for and in consideration of the foregoing premises,", 40, 310, paint);
        canvas.drawText("the LESSOR leases unto the LESSEE and the LESSEE hereby accepts from the LESSOR ", 40, 325, paint);
        canvas.drawText("the LEASED  premises, subject to the following:", 40, 340, paint);
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
        canvas.drawText("P" + LrentFee.getText().toString() +", Philippine Currency. All rental payments shall be payable to the LESSOR.", 40, 565, paint);
        canvas.drawText("", 40, 580, paint);

        canvas.drawText("4. DEPOSIT:   That the LESSEE shall deposit to the LESSOR upon signing of this contract", 40, 605, paint);
        canvas.drawText("and prior to move-in an amount equivalent to the rent for THREE (3) MONTHS or the sum", 40, 620, paint);
        canvas.drawText("of PESOS: " + Ldeposit.getText().toString() + ", Philippine Currency wherein the two (2) months deposit shall", 40, 635, paint);
        canvas.drawText("be applied as rent for the 11th and 12th months and the remaining one (1) month deposit", 40, 650, paint);
        canvas.drawText("shall answer partially for damages and any other obligations, for utilities such as Water,", 40, 665, paint);
        canvas.drawText("Electricity, CATV, Telephone, Association Dues or resulting from violation(s) of", 40, 680, paint);
        canvas.drawText("any of the provision of this contract", 40, 695, paint);

        canvas.drawText("5. LATE FEE:   That the LESSEE shall pay for rent/water/electrity after the day of " + LformattedDate2.getText().toString() + "", 40, 720, paint);
        canvas.drawText("each month will be deemed as late; and if rent/water/electricity is not paid within" + LpenaltyWeek.getText().toString() + "weeks", 40, 735, paint);
        canvas.drawText( "after such due date, the LESSEE agrees to pay a late charge of " + LpenaltyPercent.getText().toString() + "%" + " of the actual amount.", 40, 750, paint);
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

        canvas2.drawText("7. SUB-LEASE:   The LESSEE shall not directly or indirectly sublet, allow or permit the", 40, 165, paint);
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
        canvas4.drawText(Lname.getText().toString(), 90, 155, paint);
        canvas4.drawText("____________________________________", 40, 210, paint);
        canvas4.drawText(tenant_name, 90, 205, paint);

        //notary part
        canvas4.drawText("ACKNOWLEDGEMENT  S.S", 40, 265, paint);
        canvas4.drawText("Republic of the Philippines)", 40, 280, paint);
        canvas4.drawText("City OF "+ Lcity.getText().toString() +" ) S.S", 40, 305, paint);
        canvas4.drawText("BEFORE ME, a notary public, for and in the City of "+ Lcity.getText().toString() +", Philippines, the above-named", 40, 330, paint);
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


    @Override
    public void onBackPressed() {
        // do nothing
    }
}