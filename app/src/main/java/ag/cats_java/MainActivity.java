package ag.cats_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText loginET, passwordET;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginET = findViewById(R.id.loginET);
        passwordET = findViewById(R.id.passwordET);
        errorTV = findViewById(R.id.errorTV);
    }

    public void signIn(View view) {
        boolean check = true;

        if(loginET.getText().toString().equals("")){
            loginET.setError("Enter login");
            check = false;
        }
        if(passwordET.getText().toString().equals("")){
            passwordET.setError("Enter password");
            check = false;
        }
        if(check){

            OkHttpClient client = new OkHttpClient.Builder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            String content = String.format("{\n    \"login\":\"%s\",\n    \"password\":\"%s\"\n}",
                    loginET.getText().toString(), passwordET.getText().toString());

            RequestBody body = RequestBody.create(
                    mediaType,
                    content
            );

            Request request = new Request.Builder()
                    .url("http://mobile-test.itfox-web.com:80/public/testAuth")
                    .method("POST", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(MainActivity.this, "Request error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    String jsonData = response.body().string();
                    try {
                        JSONObject Jobject = new JSONObject(jsonData);
                        if (Jobject.has("accessToken")){
                            String token = Jobject.get("accessToken").toString();
                            Intent secondActivity = new Intent(MainActivity.this, catsList.class);
                            secondActivity.putExtra("token", token);
                            startActivity(secondActivity);
                        }
                        else{
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    errorTV.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } catch (JSONException ignored) {
                    }
                }
            });
        }

    }
}