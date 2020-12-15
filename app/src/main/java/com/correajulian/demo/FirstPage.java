package com.correajulian.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FirstPage extends AppCompatActivity {
    private ListView lv;
    ArrayList<String> rnames = new ArrayList<>();
    private String email;

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> names = new ArrayList<>();

        MyAdapter(Context c, ArrayList<String> names) {
            super(c, R.layout.restaurant_tile, R.id.name, names);
            this.context = c;
            for (int i = 0; i < names.size();i++) {
                this.names.add(names.get(i));
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.restaurant_tile, parent, false);
            TextView name = row.findViewById(R.id.name);

            name.setText(names.get(position));
            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        FlutterEngine flutterEngine = new FlutterEngine(this);

        flutterEngine.getNavigationChannel().setInitialRoute("C:\\Users\\Julian\\AndroidStudioProjects\\flutter_module\\lib\\main.dart");
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );


        FlutterEngineCache.getInstance().put("starter", flutterEngine);
        */
        setContentView(R.layout.activity_first_page);

        lv = findViewById(R.id.listView);
        email = getIntent().getExtras().getString("email");

        RequestQueue queue = Volley.newRequestQueue(this);

        String req = "http://54.162.121.198/fdrv2/getRestaurants.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,req,null, response -> {
            System.out.println(response);
            try {

                for (int i = 0; i < response.length(); i++) {
                    JSONObject tmp = response.getJSONObject(i);

                    String name = tmp.getString("rname");
                    rnames.add(name);
                }
                lv.setAdapter(new MyAdapter(getApplicationContext(), rnames));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace
        );

        queue.add(jsonArrayRequest);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            TextView rname = view.findViewById(R.id.name);
            Intent intent = new Intent(FirstPage.this, MenuPage.class);
            intent.putExtra("restaurant", rname.getText());
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }
}
