package com.correajulian.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FinishOrder extends AppCompatActivity {
    String email;
    String contents;
    TextView displayOrder;
    Button finOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_order);
        displayOrder = findViewById(R.id.orderDetails);
        finOrder = findViewById(R.id.finorder);

        email = getIntent().getExtras().getString("email");
        contents = getIntent().getExtras().getString("cart");
        String order = email + "\n" + contents;
        displayOrder.setText(order);
        RequestQueue queue = Volley.newRequestQueue(this);
        String req = "http://192.168.86.35:8080/orderready";
        finOrder.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Door unlocked, please gather belongings, robot will lock will reactivate in 30 seconds", Toast.LENGTH_LONG).show();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,req,null, response -> {
                System.out.println(response);
                try {
                    String info = response.getString("info");
                    Toast.makeText(getApplicationContext(), info + " , sending back to the restaurant screen", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FinishOrder.this, FirstPage.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace
            );

            queue.add(jsonObjectRequest);
        });
    }
}