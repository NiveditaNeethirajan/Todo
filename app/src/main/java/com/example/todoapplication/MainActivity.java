package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todoapplication.common.TodoConstants;
import com.example.todoapplication.common.TodoPreferences;
import com.example.todoapplication.service.ApiManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TodoPreferences.initialize(this);
        usernameEditText =  findViewById(R.id.username);
        passwordEditText =  findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        enableButtons(false);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(isButtonsEnabled())
                {
                    enableButtons(true);
                    return;
                }
                enableButtons(false);
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(isButtonsEnabled())
                {
                    enableButtons(true);
                    return;
                }
                enableButtons(false);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonPressed();
            }
        });

        Button signupButton = findViewById(R.id.signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpButtonPressed();
            }
        });
    }

    //region helper Methods
    private void enableButtons(boolean enabled) {
        if(enabled) {
            loginButton.setBackgroundColor(getResources().getColor(R.color.btnBg));
            return;
        }
        loginButton.setBackgroundColor(getResources().getColor(R.color.btnBgDisabled));
    }

    private boolean isButtonsEnabled() {
        String userName = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }
    //endregion

    //region Button Events
    private void onLoginButtonPressed(){
       if(!isButtonsEnabled()){
           Toast.makeText(this,
                   "Please input username / password", Toast.LENGTH_SHORT).show();
           return;
       }
        RequestParams rp = new RequestParams();
        rp.add("password", passwordEditText.getText().toString());
        rp.add("email", usernameEditText.getText().toString());
        ApiManager.post(TodoConstants.LoginRelativeUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    TodoPreferences.storeAuthToken(
                            serverResp.getString("auth_token"));
                    Intent todoIntent = new Intent(MainActivity.this,
                            TodoActivity.class);
                    startActivity(todoIntent);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse){
                try {
                    JSONObject serverResp = new JSONObject(errorResponse.toString());
                    Toast.makeText(MainActivity.this,
                            serverResp.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }

    private void onSignUpButtonPressed(){
        Intent signupIntent = new Intent(this, SignUpActivity.class);
        startActivity(signupIntent);
    }
    //endregion
}
