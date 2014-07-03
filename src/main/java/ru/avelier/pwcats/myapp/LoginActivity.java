package ru.avelier.pwcats.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ru.adelier.pw.PwcatsRequester;

/**
 * Created by Adelier on 03.07.2014.
 */
public class LoginActivity extends Activity {

    private class Ask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            View progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            final TextView wrongPassword = (TextView) findViewById(R.id.messageText);
            wrongPassword.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... login_pass) {
            String ci_session = PwcatsRequester.reqCiSession(login_pass[0], login_pass[1]);
            boolean isValid = PwcatsRequester.isValidCiSession(ci_session);
            if (!isValid)
                ci_session = null;
            return ci_session;
        }
        @Override
        protected void onPostExecute(String ci_session) {
            View progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

            final TextView wrongPassword = (TextView) findViewById(R.id.messageText);
            if (ci_session == null)
                wrongPassword.setVisibility(View.VISIBLE);
            else {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.ci_session), ci_session);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final EditText editLogin = (EditText) findViewById(R.id.editLogin);
        final EditText editPassword = (EditText) findViewById(R.id.editPassword);

        final TextView wrongPassword = (TextView) findViewById(R.id.messageText);
        wrongPassword.setVisibility(View.GONE);

        final Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                submit();
                return true;
            }
        });
    }

    private void submit() {
        String login = ((EditText) findViewById(R.id.editLogin)).getText().toString();
        String pass = ((EditText) findViewById(R.id.editPassword)).getText().toString();

        Ask task = new Ask();
        task.execute(login, pass);
    }
}