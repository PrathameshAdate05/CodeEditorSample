package com.prathamesh.codesample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blacksquircle.ui.editorkit.widget.TextProcessor;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView pb;
    TextView outputWindow,TVCompiling;
    Spinner spinner;
    private String lanCode = "";
    private String input = "";

    Context c = this;

    private final String[] languages = {"C","C++","Java","Python","Kotlin"};

    private HashMap<String,String > lanCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextProcessor editor = findViewById(R.id.editor);
        FloatingActionButton fab = findViewById(R.id.fab);
        outputWindow = findViewById(R.id.TVOutput);
        TVCompiling = findViewById(R.id.TVCompiling);
        TVCompiling.setVisibility(View.GONE);

        spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        outputWindow.setMovementMethod(new ScrollingMovementMethod());
        outputWindow.setVerticalScrollBarEnabled(true);
        outputWindow.setHorizontallyScrolling(true);
        pb = findViewById(R.id.progressbar);
        pb.setVisibility(View.GONE);

        lanCodes = new HashMap<>();
        lanCodes.put("C","c");
        lanCodes.put("C++","cpp");
        lanCodes.put("Java","java");
        lanCodes.put("Python","py");
        lanCodes.put("Kotlin","kt");



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(c);
                View view = layoutInflater.inflate(R.layout.inputdialog,null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
                alertDialog.setView(view);

                final EditText userInput = (EditText) view.findViewById(R.id.ETUserInput);
                alertDialog.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                input = userInput.getText().toString();
                                compile(editor.getText().toString(),lanCode,Constants.URL,input);
                            }
                        });

                AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.show();


            }
        });




    }

    public void compile(String sourceCode, String lanCode,String url, String input){

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        showProgressBar();
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("code",sourceCode);
            jsonObject.put("language",lanCode);
            jsonObject.put("input",input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String output = "";
                try {
                     output = response.getString("output").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stopProgressBar();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Log.d("output",output);
                outputWindow.setText(output);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopProgressBar();
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(request);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(MainActivity.this, languages[position], Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this,parent.getItemAtPosition(position).toString() , Toast.LENGTH_SHORT).show();
        Log.d("Item",parent.getItemAtPosition(position).toString());

        String lan = lanCodes.get(parent.getItemAtPosition(position).toString());
        lanCode = lan;
        Log.d("lan",lan);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void showProgressBar(){
        Glide.with(this).load(R.drawable.progressanimation).into(pb);
        pb.setVisibility(View.VISIBLE);
        TVCompiling.setVisibility(View.VISIBLE);
    }
    public void stopProgressBar(){

        pb.setVisibility(View.GONE);
        TVCompiling.setVisibility(View.GONE);
    }
}