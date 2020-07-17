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
import cz.msebera.android.httpclient.Header;


import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private Button signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        nameEditText =  findViewById(R.id.name);
        usernameEditText =  findViewById(R.id.username);
        passwordEditText =  findViewById(R.id.password);
        passwordConfirmEditText =  findViewById(R.id.passwordConfirm);

        signUpButton = findViewById(R.id.signup);
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

        nameEditText.addTextChangedListener(new TextWatcher() {
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

        passwordConfirmEditText.addTextChangedListener(new TextWatcher() {
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

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpButtonPressed();
            }
        });
    }

    //region helper Methods
    private void enableButtons(boolean enabled) {
        if(enabled) {
            signUpButton.setBackgroundColor(getResources().getColor(R.color.btnBg));
            return;
        }
        signUpButton.setBackgroundColor(getResources().getColor(R.color.btnBgDisabled));
    }

    private boolean isButtonsEnabled() {
        String name = nameEditText.getText().toString();
        String userName = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(name) || TextUtils.isEmpty(passwordConfirm)) {
            return false;
        }
        return true;
    }
    //endregion

    //region Button Events
    private void onSignUpButtonPressed (){
        if(!isButtonsEnabled()){
            Toast.makeText(this,"Please input all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        if(! passwordEditText.getText().toString().equals(
                passwordConfirmEditText.getText().toString())) {
            Toast.makeText(this,"Passwords not matching", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams rp = new RequestParams();
        rp.add("name", nameEditText.getText().toString());
        rp.add("password", passwordEditText.getText().toString());
        rp.add("email", usernameEditText.getText().toString());
        rp.add("password_confirmation", passwordConfirmEditText.getText().toString());
        ApiManager.post(TodoConstants.SignupRelativeUrl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    Toast.makeText(SignUpActivity.this,
                            serverResp.getString("message"), Toast.LENGTH_SHORT).show();
                    TodoPreferences.storeAuthToken(
                            serverResp.getString("auth_token"));
                    Intent todoIntent = new Intent(SignUpActivity.this,
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
                    Toast.makeText(SignUpActivity.this,
                            serverResp.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    //endregion
}
