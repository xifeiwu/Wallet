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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Wallet  extends Activity{
    private Button loginBtn;
    private EditText etName, etPass;
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
        loginBtn.setOnClickListener(onLoginListener);
    }
    
    public void showResultView() {
        if (resultView == null) {
            resultView = new ResultView(instance);
        }
        this.setContentView(resultView);
    }

    private String dstUrl = "http://192.168.1.102:8088/xhr";
    private String dstUrl2 = "http://www.baidu.com";
    
    private View.OnClickListener onLoginListener = new View.OnClickListener() {       
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            String name = etName.getText().toString();
            String password = etName.getText().toString();
            new Thread(new getFromServer()).start();
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
                    byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
                    // 这里的数据data我们必须发送给UI的主线程，所以我们通过Message的方式来做桥梁。
                    Message message = Message.obtain();
                    message.obj = data.toString();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    
    private Handler handler = new Handler() {
        // 处理子线程给我们发送的消息。
        @Override
        public void handleMessage(android.os.Message msg) {
            byte[] data = (byte[])msg.obj;
            if(msg.what == 1){
                instance.showResultView();
            }    
        };
    };
    
    private String httpPost(String url, String name, String password) {
        HttpClient httpClient = new DefaultHttpClient(); 
        HttpPost post = new HttpPost(url); 
        //设置参数，仿html表单提交 
        List<NameValuePair> paramList = new ArrayList<NameValuePair>(); 
        BasicNameValuePair param = new BasicNameValuePair("name", name);
        paramList.add(param);
        String result = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(paramList,HTTP.UTF_8)); 
            //发送HttpPost请求，并返回HttpResponse对象 
            HttpResponse httpResponse = httpClient.execute(post); 
            // 判断请求响应状态码，状态码为200表示服务端成功响应了客户端的请求
            if(httpResponse.getStatusLine().getStatusCode() == 200){ 
                //获取返回结果 
                result = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            result = null;
        }
        return result;
    };
    /*
     * 数据流post请求  
     * @param urlStr  
     * @param xmlInfo  
     */  
    public static String doPost(String urlStr, String strInfo) {  
        String reStr="";  
        try {  
            URL url = new URL(urlStr);  
            URLConnection con = url.openConnection();  
            con.setDoOutput(true);  
            con.setRequestProperty("Pragma:", "no-cache");  
            con.setRequestProperty("Cache-Control", "no-cache");  
            con.setRequestProperty("Content-Type", "text/xml");  
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());  
            out.write(new String(strInfo.getBytes("utf-8")));  
            out.flush();  
            out.close();  
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));  
            String line = "";  
            for (line = br.readLine(); line != null; line = br.readLine()) {  
                reStr += line;  
            }  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return reStr;  
    }
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

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
