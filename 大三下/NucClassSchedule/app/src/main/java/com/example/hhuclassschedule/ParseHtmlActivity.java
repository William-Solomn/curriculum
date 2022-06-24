package com.example.hhuclassschedule;

import static com.example.hhuclassschedule.MainActivity.toSaveSubjects;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.hhuclassschedule.util.ContextApplication;
import com.example.hhuclassschedule.util.SharedPreferencesUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 抓取课程信息并保存为json字符串
 */
public class ParseHtmlActivity extends AppCompatActivity {

    private static final String TAG = "ParseHtmlActivity";

    WebView webView;
    TextView tv_import;
    String parseHtmlJS;
    String URL = "http://newi.nuc.edu.cn/";

    public static List<MySubject> courseInfos = new ArrayList<MySubject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_html);
        try {
            // 打开网页
            openWeb();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 导入课程
        importSubject();
    }

    /**
     * 打开网页
     *
     * @throws IOException
     */
    public void openWeb() throws IOException {
        // toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); // 设置返回键可用
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // webView 设置
        webView = findViewById(R.id.classWeb);
        WebSettings ws = webView.getSettings();
        // 支持js
        ws.setJavaScriptEnabled(true);    // 允许js
        ws.setJavaScriptCanOpenWindowsAutomatically(true);  // 允许js打开新窗口

        // 缩放操作
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);   // 开启缩放
        ws.setDisplayZoomControls(false);  // 隐藏原生的缩放控件

        // 自适应屏幕
        ws.setUseWideViewPort(true);       // 自适应屏幕
        ws.setLoadWithOverviewMode(true);  // 缩放至屏幕的大小

        // 设置浏览器标识，以pc模式打开网页
        ws.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");

        // 加载网页

        webView.loadUrl(URL);
        webView.setWebViewClient(new WebViewClient() {
            // js 注入
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                webView.evaluateJavascript(parseHtmlJS, null);
            }
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                webView.addJavascriptInterface(new JavaObjectJsInterface(), "java_obj");
                webView.loadUrl("javascript:window.java_obj.onHtml('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                if (!TextUtils.isEmpty(title)) {
                    // 设置标题
                    // getSupportActionBar().setTitle(title);
                    TextView textView = findViewById(R.id.toolbar_title);
                    textView.setText(title);
                    Log.e("22222", "title111" + title);
                }
            }
        });

    }
    public class JavaObjectJsInterface {
        @JavascriptInterface // 要加这个注解，不然调用不到
        public void onHtml(String html) {
            Document document = Jsoup.parseBodyFragment(html);

            Elements elements = document.select("table[id=kbgrid_table_0]");
            Elements elementsForTr = elements.select("tr");
            Elements elementsForTd = elementsForTr.select("td");

            for (Element element : elementsForTr) {
                MySubject mySubject = new MySubject();
                if (!(element.children().attr("rowspan").equals("1"))
                        && element.children().attr("class").equals("td_wrap")) {

                    String[] eleArr = element.children().select("div[class=timetable_con text-left]").text().split(" ");
                    for (int i = 0; i < eleArr.length; i++) {
                        Log.d("5555", "第" + i + "位是：" + eleArr[i]);
                        if(((i+1) / 13 >= 1 && ((i+1)%13)==0)){
                            courseInfos.add(mySubject);
                            mySubject = new MySubject();
                        }
                        switch (i % 13) {
                            case 0:
                                mySubject.setName(eleArr[i]);
                                break;
                            case 1:
                                String str_sectionTimes = eleArr[i].replace("(", "")
                                        .replaceAll("-", " ")
                                        .replace("节)", " ")
                                        .replace(","," ")
                                        .replace("周", "");
//                                Log.e("222", "+" + str_sectionTimes);
                                String[] temp = str_sectionTimes.split(" ");
                                mySubject.setStart(Integer.parseInt(temp[0]));
                                mySubject.setStep(Integer.parseInt(temp[1]) - Integer.parseInt(temp[0]) + 1);
                                List<Integer> mWeekList = new ArrayList<>();
                                if (temp.length ==3) {
                                    mWeekList.add(Integer.parseInt(temp[2]));
                                } else if(temp.length ==4){
                                    for (int j = Integer.parseInt(temp[2]); j <= Integer.parseInt(temp[3]); j++) {
                                        mWeekList.add(j);
                                    }
                                }else if(temp.length == 6){
                                    for (int j = Integer.parseInt(temp[2]); j <= Integer.parseInt(temp[3]); j++) {
                                        mWeekList.add(j);
                                    }
                                    for (int j = Integer.parseInt(temp[4]); j <= Integer.parseInt(temp[5]); j++){
                                        mWeekList.add(j);
                                    }
                                }

                                mySubject.setWeekList(mWeekList);
                                Log.e("6666", "name:" + mySubject.getName() + " weekList:" + mySubject.getWeekList());
                                break;
                            case 3:
                                mySubject.setRoom(eleArr[i]);
                                break;
                            case 4:
                                mySubject.setTeacher(eleArr[i]);
                                break;
                        }
                    }
                }
            }

            for(int i=0;i<courseInfos.size();i++){
                for(Element element : elementsForTd){
                    String[] temp = element.select("div[class=timetable_con text-left]").text().split(" ");
                    if(temp[0].equals(courseInfos.get(i).getName())){
                        MySubject subject = courseInfos.get(i);
                        String[] idStr = element.attr("id").split("-");
                        subject.setDay(Integer.parseInt(idStr[0]));
                        courseInfos.set(i,subject);
                        break;
                    }
                }
            }
            toSaveSubjects(courseInfos);
        }
    }
    /**
     * 导入课程保存为json字符串
     */
    public void importSubject() {
        // 导入课程
        tv_import = findViewById(R.id.tv_button);
        tv_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过Handler发送消息

                webView.addJavascriptInterface(new JavaObjectJsInterface(), "java_obj");
                webView.post(new Runnable() {
                    @Override
                    public void run() {

//                        // 注意调用的JS方法名要对应上
//                        // 调用javascript的parseHtml()方法
////                        webView.loadUrl("javascript:parseHtml()");
////                        webView.loadUrl("src/main/assets/parseHtml.js");
////                        webView.evaluateJavascript("document.getElementsByTagName('html')[0].innerHTML;", new ValueCallback<String>() {
//                        webView.evaluateJavascript("javascript:window.java_obj.onHtml('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');"
//                                , new ValueCallback<String>() {
//                            @Override
//                            public void onReceiveValue(String value) {
//
//                                // 此处为 js 返回的结果
//                                SharedPreferences sp = getSharedPreferences("COURSE_DATA", Context.MODE_PRIVATE);//创建sp对象
//                                SharedPreferences.Editor editor = sp.edit();
//                                editor.clear();
//                                editor.putString("HTML_TO_SUBJECT", value); //存入json串
//                                editor.commit();//提交
//
//                                SharedPreferencesUtil.init(ContextApplication.getAppContext(), "COURSE_DATA").putString("HTML_TO_SUBJECT", value);
//                                SharedPreferencesUtil.init(ContextApplication.getAppContext(), "COURSE_DATA").remove("SUBJECT_LIST");
//                                Log.d("ParseHtmlActivity", "这个东西是你要的: " + value);
//
//                                Intent intent = new Intent(ParseHtmlActivity.this, MainActivity.class);
//                                if (MainActivity.mainActivity != null) {
//                                    MainActivity.mainActivity.finish(); // 销毁MainActivity
//                                }
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
                        webView.loadUrl("javascript:window.java_obj.onHtml('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                                Intent intent = new Intent(ParseHtmlActivity.this, MainActivity.class);
                                if (MainActivity.mainActivity != null) {
                                    MainActivity.mainActivity.finish(); // 销毁MainActivity
                                }
                                startActivity(intent);
                                finish();
                    }
                });

            }
        });
    }


}
