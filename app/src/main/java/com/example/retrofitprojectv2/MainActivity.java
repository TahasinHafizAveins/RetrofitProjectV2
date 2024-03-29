package com.example.retrofitprojectv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private JsonApi jsonApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.text_view_result);

        Gson gson = new GsonBuilder().serializeNulls().create(); //if forced to delete  value

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .addInterceptor(new Interceptor() {
                                            @Override
                                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                                Request originalRequest = chain.request();
                                                Request newRequest = originalRequest.newBuilder()
                                                        .header("Interceptor-Headre", "xyz")
                                                        .build();
                                                return chain.proceed(newRequest);
                                            }
                                        })
                                        .addInterceptor(loggingInterceptor)
                                        .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();//when gson is not using .addConverterFactory(GsonConverterFactory.create())

        jsonApi = retrofit.create(JsonApi.class);

       getPost();
        // getComment();
        // createPost();
        // updatePost();

       // deletePost();

    }

    private void deletePost() {
        Call<Void> call = jsonApi.deletePost(5);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                textView.setText("Code: "+response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                textView.setText(throwable.getMessage());
            }
        });
    }

    private void updatePost() {

        Post post = new Post(23,null,"New massage update");

        Map<String,String>headers = new HashMap<>();  //for patchPost
        headers.put("Map-headers1", "def");
        headers.put("Map-headers2", "ghi");
        Call<Post>call = jsonApi.patchPost(headers,5,post);//for patchPost

       // Call<Post>call = jsonApi.putPost("abc",5,post);//for putPost


        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful())
                {
                    textView.setText("Code :"+ response.code());
                    return;
                }

                Post postResponse = response.body();

                String content = "";
                content +="Code: "+response.code()+"\n";
                content +="ID: "+postResponse.getId() + "\n";
                content +="User ID: "+postResponse.getUserId() + "\n";
                content +="Title: "+postResponse.getTitle() + "\n";
                content +="Text: "+postResponse.getText() + "\n\n";
                textView.setText(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable throwable) {
                textView.setText(throwable.getMessage());
            }
        });
    }

    private void createPost() {
        Post post = new Post(23,"New Title","New massage");

        Map<String,String> fields = new HashMap<>();
        fields.put("userId","25");
        fields.put("title","New Title");

        //Call <Post> call = jsonApi.createPost(24,"New Title","New massage");
        Call <Post> call = jsonApi.createPost(fields);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful())
                {
                    textView.setText("Code :"+ response.code());
                    return;
                }

                Post postResponse = response.body();

                String content = "";
                content +="Code: "+response.code()+"\n";
                content +="ID: "+postResponse.getId() + "\n";
                content +="User ID: "+postResponse.getUserId() + "\n";
                content +="Title: "+postResponse.getTitle() + "\n";
                content +="Text: "+postResponse.getText() + "\n\n";
                textView.setText(content);

            }

            @Override
            public void onFailure(Call<Post> call, Throwable throwable) {
                textView.setText(throwable.getMessage());
            }
        });
    }

    private void getComment() {

        Call<List<Comment>> call = jsonApi.getComments("comments?postId=1");
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                if (!response.isSuccessful())
                {
                    textView.setText("Code :"+ response.code());
                    return;
                }

                List<Comment> comments = response.body();
                for (Comment comment : comments)
                {
                    String content = "";
                    content +="ID: "+comment.getId() + "\n";
                    content +="Post ID: "+comment.getPostId() + "\n";
                    content +="Name: "+comment.getName() + "\n";
                    content +="Email: "+comment.getEmail() + "\n";
                    content +="Text: "+comment.getText() + "\n\n";
                    textView.append(content);
                }

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable throwable) {

                textView.setText(throwable.getMessage());

            }
        });
    }

    private void getPost() {

        Map< String , String> parameters = new HashMap<>();
        parameters.put("userId" , "1");
        parameters.put("_sort" , "id");
        parameters.put("_order" , "desc");

        // Call<List<Post>> call = jsonApi.getPosts(parameters); //sort null, order null
        Call <List<Post>> call = jsonApi.getPosts(new Integer[]{1,2,3},"id","desc");

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful())
                {
                    textView.setText("Code :"+ response.code());
                    return;
                }

                List<Post> posts = response.body();
                for (Post post : posts)
                {
                    String content = "";
                    content +="ID: "+post.getId() + "\n";
                    content +="User ID: "+post.getUserId() + "\n";
                    content +="Title: "+post.getTitle() + "\n";
                    content +="Text: "+post.getText() + "\n\n";
                    textView.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable throwable) {

                textView.setText(throwable.getMessage());
            }
        });
    }
}
