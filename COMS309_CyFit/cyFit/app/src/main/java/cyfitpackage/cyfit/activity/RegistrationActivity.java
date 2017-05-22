package cyfitpackage.cyfit.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyfitpackage.cyfit.R;


/**
 * A registration screen that offers user signup with first name, last name, email, and password.
 */
public class RegistrationActivity extends AppCompatActivity  {

    /**
     * Keep track of the registartion task to ensure we can cancel it if requested.
     */
    private UserRegistrationTask mAuthTask = null;

    // UI references.
    private TextInputEditText mFirstnameView;
    private TextInputEditText mLastnameView;
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private TextInputEditText mPasswordConfirmView;
    private View mLoginRedirectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mFirstnameView = (TextInputEditText) findViewById(R.id.register_firstname);
        mLastnameView = (TextInputEditText) findViewById(R.id.register_lastname);
        mEmailView = (TextInputEditText) findViewById(R.id.register_email);
        mPasswordView = (TextInputEditText) findViewById(R.id.register_password);
        mPasswordConfirmView = (TextInputEditText) findViewById(R.id.register_password_confirm);

        Button mbtnRegister = (Button) findViewById(R.id.btnRegister);
        mbtnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        Button mbtnLoginRedirect = (Button) findViewById(R.id.btnLoginRedirect);
        mbtnLoginRedirect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });

        mLoginRedirectView = mbtnLoginRedirect;
    }


    /**
     * Show/hide login redirect.
     */
    private void showRedirect(final boolean show) {
            mLoginRedirectView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegistration() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFirstnameView.setError(null);
        mLastnameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String firstname = mFirstnameView.getText().toString();
        String lastname = mLastnameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password_confirm = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //check if all fields are filled out correctly
        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (TextUtils.isEmpty(password_confirm)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmView;
            cancel = true;
        } else if (!isPasswordValid(password, password_confirm)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Kick off a background task to
            // perform the user registration attempt.
            mAuthTask = new UserRegistrationTask(firstname, lastname, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password, String password_confirm) {
        return (password.length() > 5 && password.equals(password_confirm));
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final String mFirstname;
        private final String mLastname;
        private final String mEmail;
        private final String mPassword;
        private final RequestQueue queue;

        UserRegistrationTask(String firstname, String lastname, String email, String password) {
            mFirstname = firstname;
            mLastname = lastname;
            mEmail = email;
            mPassword = password;
            queue = Volley.newRequestQueue(getApplicationContext());
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //register new user
            final String url_2 = "http://proj-309-ab-5.cs.iastate.edu/Client/RegisterUser.php";
            StringRequest registerRequest = new StringRequest(Request.Method.POST, url_2, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject registerInfo = new JSONObject(response);
                        Log.d("Server", registerInfo.getString("Message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("newFirstname", mFirstname);
                        params.put("newLastname", mLastname);
                        params.put("newEmail", mEmail);
                        params.put("newPassword", mPassword);
                        return params;
                    }
                };
            queue.add(registerRequest);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            } else {
                //TODO
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

