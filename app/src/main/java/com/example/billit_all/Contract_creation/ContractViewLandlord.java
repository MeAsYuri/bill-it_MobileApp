package com.example.billit_all.Contract_creation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.EditTenant;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class ContractViewLandlord extends AppCompatActivity {
    Button proceed;
    TextView contractTemplate, contractTemplate2, select_tenant;
    TextView additionalTermString;
    int countTerms = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_view_landlord);
//        proceed = findViewById(R.id.proceed);
        contractTemplate = findViewById(R.id.contractTemplate);
        contractTemplate2 = findViewById(R.id.contractTemplate2);
        select_tenant = findViewById(R.id.select_tenant);
        additionalTermString = findViewById(R.id.additionalTermString);



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
                                String id = user.getString("user_id");
                                Log.d("USER_ID", id);


                                //Contract Details
                                RequestQueue queue0 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request0 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/setting/landlord/contract/" + id,
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

                                                    JSONObject responseJSON = response.getJSONObject("landlord");


                                                    String house_no = responseJSON.getString("house_no");
                                                    String street = responseJSON.getString("street");
                                                    String subd = responseJSON.getString("subd");
                                                    String brgy = responseJSON.getString("brgy");
                                                    String city = responseJSON.getString("city");
                                                    String zipcode = responseJSON.getString("zipcode");
                                                    String created_at = responseJSON.getString("created_at");

                                                    String name = responseJSON.getString("name");
                                                    String penaltyPercent = responseJSON.getString("penalty_percentage");
                                                    String penaltyWeek = responseJSON.getString("penalty_week");
//                                                    int rent_fee = responseJSON.getInt("rent_fee");

                                                    Date date = inputFormat.parse(created_at);
                                                    String formattedDate = outputFormat.format(date);
                                                    String formattedDate2 = outputFormat2.format(date);

//                                                    int deposit = rent_fee * 3;

                                                    select_tenant.setText(house_no);

                                                    Log.d("data", house_no);
                                                    Log.d("data", street);
                                                    Log.d("data", subd);
                                                    Log.d("data", brgy);
                                                    Log.d("data", city);
                                                    Log.d("data", zipcode);
                                                    Log.d("data", formattedDate);
                                                    Log.d("data", name);
                                                    Log.d("data", penaltyPercent);
                                                    Log.d("data", penaltyWeek);
//                                                    Log.d("data", String.valueOf(rent_fee));
//                                                    Log.d("data", String.valueOf(deposit));

                                                    RequestQueue queue9 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                    BackendJsonObjectRequest request9 = new BackendJsonObjectRequest(
                                                            Request.Method.GET,
                                                            "/api/setting/landlord/contract/" + id,
                                                            null,
                                                            new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    try {
                                                                        JSONObject user = response.getJSONObject("landlord");
                                                                        Log.d("contract_id", String.valueOf(user));
                                                                        String contract_id = user.getString("contract_specification_id");
                                                                        Log.d("contract_id", contract_id);


                                                                        RequestQueue queue6 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                                        BackendJsonObjectRequest request6 = new BackendJsonObjectRequest(
                                                                                Request.Method.GET,
                                                                                "/api/contract/temp-acct/tnc/" + contract_id,
                                                                                null,
                                                                                new Response.Listener<JSONObject>() {
                                                                                    @Override
                                                                                    public void onResponse(JSONObject response) {
                                                                                        try {
                                                                                            if(response.equals(null) || response.equals("") || response == null){

                                                                                            }
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
                                                                                                String pair = (++countTerms) + "." + SUBJECT.get(i) + ":  " + BODY.get(i);
                                                                                                pairs.add(pair);

                                                                                            }
                                                                                            //(Edit by pair)
                                                                                            String text = "";
                                                                                            for (String pair : pairs) {
                                                                                                text += "\n"+ pair + "\n";
                                                                                            }

                                                                                            additionalTermString.setText(text);
                                                                                            Log.d("arrayListTNC", text);


                                                                                            String contract = "KNOW ALL MEN BY THESE PRESENTS: This CONTRACT OF LEASE is made and " + "\n" +
                                                                                                    "executed at the City of CITY, this day of DATE, by " +
                                                                                                    "and between: Lessor, of legal age," +
                                                                                                    " Filipino, and with residence and postal address at " +
                                                                                                    "# HOUSE#, STREET, SUBDIVISION, BARANGAY, CITY, hereinafter referred " +
                                                                                                    "to as the LESSOR. -AND-(NAME OF LESSEE), Filipino and with residence " +
                                                                                                    "and postal address at(Address), " +
                                                                                                    "hereinafter referred to as the LESSEE.  WITNESSETH; That WHEREAS, the Lessor " +
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
                                                                                                    "in PESOS: (00.000.000) , Philippine Currency. All rental " +
                                                                                                    "payments shall be payable to the LESSOR.\n\n4. DEPOSIT:   That the LESSEE " +
                                                                                                    "shall deposit to the LESSOR upon signing of this contract and prior to move-in " +
                                                                                                    "an amount equivalent to the rent for THREE (3) MONTHS or the sum of PESOS: " +
                                                                                                    "P (00.000.000), Philippine Currency wherein the two (2) months " +
                                                                                                    "deposit shall be applied as rent for the 11th and 12th months and the remaining " +
                                                                                                    "one (1) month deposit shall answer partially for damages and any other obligations," +
                                                                                                    " for utilities such as Water, Electricity, CATV, Telephone, Association Dues " +
                                                                                                    "or resulting from violation(s) of any of the provision of this contract.\n\n" +
                                                                                                    "5. LATE FEE:   That the LESSEE shall pay for rent/water/electrity after the " +
                                                                                                    "day of DAY each month will be deemed as late; and if rent/water/electricity " +
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
                                                                                                    "cost and damages which the said party may be entitled to under the law.\n" + text + "\n" +
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


//        replacing text in the Contract
                                                                                            String modifiedContract = contract
                                                                                                    .replaceAll("PENALTYWEEK", penaltyWeek)
                                                                                                    .replaceAll("PENALTYPERCENT", penaltyPercent)
                                                                                                    .replaceAll("LESSOR", name)
                                                                                                    .replaceAll("HOUSE#", house_no)
                                                                                                    .replaceAll("STREET", street)
                                                                                                    .replaceAll("SUBDIVISION", subd)
                                                                                                    .replaceAll("BARANGAY", brgy)
                                                                                                    .replaceAll("CITY", city)
                                                                                                    .replaceAll("ZIPCODE", zipcode)
                                                                                                    .replaceAll("DATE", formattedDate)
                                                                                                    .replaceAll("DAY", formattedDate2);
//                                                                                                    .replaceAll("RENTFEE", String.valueOf(rent_fee))
//                                                                                                    .replaceAll("DEPOSITFEE", String.valueOf(deposit));



                                                                                            String modifiedContract2 = contract2
                                                                                                    .replaceAll("LESSOR", name);


                                                                                            contractTemplate.setText(modifiedContract);
                                                                                            contractTemplate2.setText(modifiedContract2);


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



    }
}



