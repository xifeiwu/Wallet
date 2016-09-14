package study.wallet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Wallet  extends Activity{
    private Button loginBtn;
    private EditText etName, etPass;
    private TextView tvResult;
    public static Wallet instance;
    private ResultView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        instance = this;
        this.setContentView(R.layout.main);
        this.showMain();
    }
    
    public void showMain() {
        this.setContentView(R.layout.main);
        loginBtn = (Button) this.findViewById(R.id.btn_login);
        etName = (EditText) this.findViewById(R.id.et_name);
        etPass = (EditText) this.findViewById(R.id.et_pass);
        tvResult = (TextView) this.findViewById(R.id.tv_result);
        loginBtn.setOnClickListener(onLoginListener);
    }
    
    public void showResultView() {
        if (resultView == null) {
            resultView = new ResultView(instance);
        }
        this.setContentView(resultView);
    }

    private String dstUrl = "http://192.168.1.101:8089/login";
    
    private View.OnClickListener onLoginListener = new View.OnClickListener() {       
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            String name = etName.getText().toString();
            String password = etPass.getText().toString();
            new Thread(new postToServer(name, password)).start();
        }
    };

    // 使用Handler Message MessageQueue Looper等方式去访问网络资源的时候，我们必须要开启一个子线程
    public class getFromServer implements Runnable{
        // 在run方法中完成网络耗时的操作
        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(dstUrl);
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpGet);
                if(200 == httpResponse.getStatusLine().getStatusCode()){
                    // 这里的数据data我们必须发送给UI的主线程，所以我们通过Message的方式来做桥梁。
                    Message message = Message.obtain();
                    message.obj = EntityUtils.toString(httpResponse.getEntity());
                    message.what = 1;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    
    public class postToServer implements Runnable{
        // 在run方法中完成网络耗时的操作
        private String name, password;
        public postToServer(String name, String password) {
            this.name = name;
            this.password = password;
        }
        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient(); 
            HttpPost post = new HttpPost(dstUrl); 
            //设置参数，仿html表单提交 
            List<NameValuePair> paramList = new ArrayList<NameValuePair>(); 
            paramList.add(new BasicNameValuePair("name", this.name));
            paramList.add(new BasicNameValuePair("password", this.password));
            try {
                post.setEntity(new UrlEncodedFormEntity(paramList,HTTP.UTF_8)); 
                //发送HttpPost请求，并返回HttpResponse对象 
                HttpResponse httpResponse = httpClient.execute(post); 
                // 判断请求响应状态码，状态码为200表示服务端成功响应了客户端的请求
                if(httpResponse.getStatusLine().getStatusCode() == 200){ 
                    //获取返回结果 
                    Message message = Message.obtain();
                    message.obj = EntityUtils.toString(httpResponse.getEntity());
                    message.what = 2;
                    
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
            }
        }
    }

    private Handler handler = new Handler() {
        // 处理子线程给我们发送的消息。
        @Override
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
            case 1:
                tvResult.setText(msg.obj.toString());
                break;
            case 2:
                if (msg.obj.toString().indexOf("true") > 0) {
                    instance.showResultView(); 
                } else {
                    tvResult.setText(msg.obj.toString() + "账号或密码错误");
                }
                break;
            }
        };
    };
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

}
