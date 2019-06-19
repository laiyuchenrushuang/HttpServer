package com.ly.httpserver;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.bt_get)
    Button bt_get;
    @BindView(R.id.bt_get1)
    Button bt_get1;
    @BindView(R.id.bt_post)
    Button bt_post;
    @BindView(R.id.bt_post1)
    Button bt_post1;

    private static final int POST_ORINGIN = 1;
    private static final int GET_ORINGIN = 2;
    private static final int OKHTTP_GET_ORINGIN = 3;
    private static final int OKHTTP_POST_ORINGIN = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initEvent();
    }

    private void initEvent() {
        bt_get.setOnClickListener(this);
        bt_get1.setOnClickListener(this);
        bt_post.setOnClickListener(this);
        bt_post1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_get:
                getOringin();
                break;
            case R.id.bt_get1:
                okhttpGetOringin();
                break;
            case R.id.bt_post:
                postOringin();
                break;
            case R.id.bt_post1:
                okhttpPostOringin();
                break;
        }
    }

    private void okhttpPostOringin() {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("name", "laiyu");
        paramsMap.put("password", "123456");
        OkHttpClient okClient = new OkHttpClient();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = paramsMap.keySet();
        for (String key : keySet) {
            String value = paramsMap.get(key);
            formBodyBuilder.add(key, value);
        }
        FormBody formBody = formBodyBuilder.build();

        Request request = new Request.Builder()
                .url("http://www.quwenlieqi.com/app/v2/api.php?m=102")
                .post(formBody)
                .build();

        Call call = okClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();
                Message msg = new Message();
                msg.what = OKHTTP_POST_ORINGIN;
                msg.obj = htmlStr;
                handler.sendMessage(msg);
            }
        });
    }

    private void okhttpGetOringin() {
        OkHttpClient okClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url("http://www.quwenlieqi.com/app/v2/api.php?m=102")
                .build();
        //new call
        Call call = okClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();
                Message msg = new Message();
                msg.what = OKHTTP_GET_ORINGIN;
                msg.obj = htmlStr;
                handler.sendMessage(msg);
            }
        });
    }


    private void getOringin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = "laiyu";
                String password = "123456";
                String path = "http://www.quwenlieqi.com/app/v2/api.php?m=102";
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    //设置请求中的媒体类型信息。
                    conn.setRequestProperty("Content-Type", "application/json");
                    //设置客户端与服务连接类型
                    conn.addRequestProperty("Connection", "Keep-Alive");
                    // 开始连接
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();

                        // 把字节数组输出流的数据转换成字节数组
                        String text = streamToString(is);
                        Message msg = new Message();
                        msg.what = GET_ORINGIN;
                        msg.obj = text;
                        handler.sendMessage(msg);
                    } else {
                        handler.sendEmptyMessage(0);
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Log.i("lylog", " result Exception");
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void postOringin() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = "https://www.baidu.com";
                String name = "laiyu";
                String password = "123456";
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("POST");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);

                    String params = "username=" + URLEncoder.encode(name)
                            + "&password=" + URLEncoder.encode(password);
                    conn.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", params.length()
                            + "");

                    // 设置打开输出流
                    conn.setDoOutput(true);
                    // 拿到输出流
                    OutputStream os = conn.getOutputStream();
                    os.write(params.getBytes());
                    os.flush();
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        // 把字节数组输出流的数据转换成字节数组
                        String result = streamToString(conn.getInputStream());
                        Message msg = new Message();
                        msg.what = POST_ORINGIN;
                        msg.obj = result;
                        handler.sendMessage(msg);
                        Log.i("lylog", " result success");
                    } else {
                        handler.sendEmptyMessage(0);
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Log.i("lylog", " result Exception");
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.i("lylog Exception", e.toString());
            return null;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case POST_ORINGIN:
                    showToast("POST_ORINGIN 成功");
                    break;
                case GET_ORINGIN:
                    showToast("GET_ORINGIN 成功");
                    break;
                case OKHTTP_GET_ORINGIN:
                    showToast("OKHTTP_GET_ORINGIN 成功");
                    break;
                case OKHTTP_POST_ORINGIN:
                    showToast("OKHTTP_POST_ORINGIN 成功");
                    break;
            }
        }
    };

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
