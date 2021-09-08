package ag.cats_java.model;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MainInterface {

    @GET("list")
    Call<ArrayList<CatsResponse>>STRING_CALL(
            @Query("page") int page
    );
}
