package com.correajulian.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuPage extends AppCompatActivity {
    private Button Onward;
    private TextView rname;
    private Cart cart;
    private final ArrayList<Item> cart_items = new ArrayList<>();
    private ListView listView;
    private final ArrayList<Item> item_list = new ArrayList<>();
    private String email;

    class MyAdapter extends ArrayAdapter<Item> {
        Context context;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();

        MyAdapter(Context c, ArrayList<Item> items) {
            super(c, R.layout.row, items);
            this.context = c;
            for (int i = 0; i < items.size();i++) {
                this.names.add(items.get(i).getName());
            }
            for (int i = 0; i < items.size();i++) {
                this.prices.add(items.get(i).getPrice());
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            TextView name = row.findViewById(R.id.name);
            TextView price = row.findViewById(R.id.price);

            name.setText(names.get(position));
            price.setText(prices.get(position));
            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        listView = findViewById(R.id.listView);

        rname = findViewById(R.id.rname);
        Onward = findViewById(R.id.processOrder);

        Bundle extras = getIntent().getExtras();

        assert extras != null;
        String rest_name = extras.getString("restaurant");
        email = extras.getString("email");

        assert rest_name != null;
        rest_name.replaceAll(" ", "+");

        rname.setText(rest_name);

        RequestQueue queue = Volley.newRequestQueue(this);

        String req = "http://54.162.121.198/fdrv2/getMenu.php?restaurant="+rest_name;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,req,null, response -> {
            System.out.println(response);
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject tmp = response.getJSONObject(i);

                    Item tmpitem = new Item(tmp.getString("menuitemname"),tmp.getString("price"));
                    item_list.add(tmpitem);
                    MyAdapter m = new MyAdapter(getApplicationContext(), item_list);
                    listView.setAdapter(m);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace
        );

        queue.add(jsonArrayRequest);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            TextView name = view.findViewById(R.id.name);
            TextView price = view.findViewById(R.id.price);
            Item tmp = new Item((String) name.getText(),(String) price.getText());
            cart_items.add(tmp);
            Toast.makeText(getApplicationContext(), tmp.getName()+ " has been added to cart", Toast.LENGTH_LONG).show();

        });
        Onward.setOnClickListener(v -> {
            if (cart_items.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Cart is empty, please select an item", Toast.LENGTH_LONG).show();
            }
            else {
                cart = new Cart(cart_items, (String) rname.getText());
                handlereq(cart);
                //getRobotLoc();
                //System.out.println(robot);

                Intent intent = new Intent(MenuPage.this, MapsActivity.class);
                intent.putExtra("cart",cart.getOrderString());
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
    protected void handlereq(Cart c) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://54.162.121.198/fdrv2/sendOrder.php?contents="+c.getOrderString()+"&total="+c.getOrderTotal()+"&email="+email;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> Log.i("VOLLEY", response), error -> Log.e("VOLLEY", error.toString())) {


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    System.out.println("responseString");
                    // can get more details such as response.headers
                }
                assert response != null;
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    }
}
