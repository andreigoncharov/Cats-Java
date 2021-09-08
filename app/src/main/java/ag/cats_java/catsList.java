package ag.cats_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ag.cats_java.model.CatsResponse;
import ag.cats_java.model.MainInterface;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class catsList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView catsRV;
    private ProgressBar progressBar;

    private String token;
    private ArrayList<CatsResponse> data = new ArrayList<>();
    private CatsAdapter catsAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private int page = 0, totalpages = 3;
    private Boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cats_list);

        Intent intent = getIntent();
        token = intent.getStringExtra("token").toString();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        catsRV = findViewById(R.id.catsRV);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        catsRV.setHasFixedSize(true);
        catsRV.setLayoutManager(layoutManager);
        catsAdapter = new CatsAdapter();
        catsRV.setAdapter(catsAdapter);

        swipeRefresh.setOnRefreshListener(this);

        getCats(false);

        catsRV.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = layoutManager.getChildCount();
                int pastVisibleItem = layoutManager.findFirstVisibleItemPositions(null)[0];
                int total = catsAdapter.getItemCount();
                if(!isLoading && page<totalpages){
                    if(visibleItemCount + pastVisibleItem >= total){
                        page++;
                        getCats(false);
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }



    private void getCats(boolean isOnRefresh) {
        isLoading = true;
        if(!isOnRefresh) progressBar.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://mobile-test.itfox-web.com:80/private/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MainInterface mainInterface = retrofit.create(MainInterface.class);

        Call<ArrayList<CatsResponse>> call = mainInterface.STRING_CALL(page);

        call.enqueue(new Callback<ArrayList<CatsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<CatsResponse>> call, retrofit2.Response<ArrayList<CatsResponse>> response) {
                ArrayList<CatsResponse> listResponse = response.body();

                if(listResponse != null){
                    catsAdapter.addList(listResponse);
                }
                if (page == totalpages)
                {
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                }

                isLoading = false;
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<CatsResponse>> call, Throwable t) {
                Toast.makeText(catsList.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRefresh() {
        catsAdapter.clear();
        page=0;
        getCats(true);
    }
}