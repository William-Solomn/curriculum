#include <ESP8266WiFi.h>             // esp8266库
#include <ESP8266HTTPClient.h>
//#include <WiFiClient.h>
#include <SPI.h>
#include <TFT_eSPI.h>
#include <MFRC522.h>
#include <FS.h> //山村文件系统
#include <ArduinoJson.h>

#define RST_PIN 5
#define SS_PIN 4

const char* host = "47.100.49.178";
const int port = 8888;

File writeFile,readFile;

String jsonContents = "";

String fileName = "/user/info.txt";//用来存服务器发来的用户数据

#define button 0    // 按钮的引脚GPIO 0


MFRC522 mfrc(SS_PIN,RST_PIN);

TFT_eSPI tft = TFT_eSPI();


int buttonState = 0;    //按钮的状态

char ssid[] = "abc";         // WiFi名
char pass[] = "1223334444";     // WiFi密码

WiFiClient client;                 // 声明一个Mysql客户端，在lianjieMysql中使用



void setup()
{
  tft.init();
  tft.setRotation(3);


  Serial.begin(9600);
  while (!Serial);      //  等待端口的释放

  Serial.printf("\nConnecting to %s", ssid);

  //卡片初始化
  SPI.begin();
  
  mfrc.PCD_Init();
  delay(4);
  mfrc.PCD_DumpVersionToSerial();
  
  Serial.println("bro, wait wait wait");
 


  //按钮与灯泡初始化
  pinMode(LED_BUILTIN, OUTPUT);   //设置点亮LED
  pinMode(button,INPUT);    //进行信号检测

  // 连接WiFi
  WiFi.begin(ssid, pass);        
  while (WiFi.status() != WL_CONNECTED) {       // 如果WiFi没有连接，一直循环打印点
    delay(1000);
    Serial.print(".");

  }

  //初始化闪存文件系统
  SPIFFS.format();
  //检测初始化状况
  if(SPIFFS.begin()){

    Serial.println("闪存文件系统初始化完成");
  }else{
  
    Serial.println("闪存文件系统初始化失败");
  }
        //写文件
//  writeFile = SPIFFS.open(file_name,"w");
//      writeFile.println("");
        //读文件
//  readFile =SPIFFS.open(file_name,"r");
//    for(int i=0; i<readFile.size(); i++){
//      Serial.print((char)readFile.read());       
//    }
   
  Serial.print("\n连上网了");

  Serial.print("，IP地址是： ");

  Serial.println(WiFi.localIP());
 
  // 打印开发板的IP地址


}

void loop()
{
  
  //实时获取按钮被按下的状态
  String cardId="";
  buttonState = digitalRead(button);
  
  if(buttonState == LOW){
    
      digitalWrite(LED_BUILTIN,LOW);//点亮灯泡
      //writeUserFile(getMessageFromServer());
      jsonContents=getMessageFromServer();
      Serial.println("写入文件标注=====");
      //Serial.println(getMessageFromServer());  
  }else{
    digitalWrite(LED_BUILTIN,HIGH);
    if ( ! mfrc.PICC_IsNewCardPresent()) {return;}
      
    if ( ! mfrc.PICC_ReadCardSerial()) {return;}
      
    Serial.print("您将要查询的卡的id是：");
    for (byte i = 0;i<mfrc.uid.size;i++){
        //cardId+=String(mfrc.uid.uidByte[i] < 0x10 ? " 0" : " ");
        cardId+=String(mfrc.uid.uidByte[i],HEX);
    }
    
    if(parseJson(cardId,jsonContents)){
      for(int i=0;i<3;i++){
        digitalWrite(LED_BUILTIN,LOW);
        delay(250);
        digitalWrite(LED_BUILTIN,HIGH);
        delay(250);
      }

    }
  }
 
  
}
bool parseJson(String userId,String jsonString){
  
  StaticJsonDocument<0> filter;
  filter.set(true);

  StaticJsonDocument<768> doc;
  Serial.println("JsonString的内容是:");
  Serial.println(jsonString);
  DeserializationError error = deserializeJson(doc, jsonString, DeserializationOption::Filter(filter));

  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.f_str());
    
}
  for (JsonObject item : doc.as<JsonArray>()) {

  const char* studentCardId = item["studentCardId"]; // "456", "abc", "wwwerwr", "123", "456", "456"
  if((String)studentCardId==userId){
    return true;
  }
}
  return false;
}
String getMessageFromServer(){
  WiFiClient client;
  
  String url = "/user/getAllUser";

  String httpRequest = String("GET ")+url+"\r\n"+
                        "Host: "+host+"\r\n"+
                        "Connection: close\r\n"+
                        "\r\n";
  if(client.connect(host,port)){
    Serial.print("成功连接上云服务器:");
    Serial.println(host);
    client.print(httpRequest);
    String result = "";
    while (client.connected() || client.available()) { // 接口响应
      String line = client.readStringUntil('\n'); // 返回的某一行数据
      line.trim();
      if (line.length() > 0) { 
        result += line + "\r\n"; 
      }
    }
    client.stop(); // 断开连接
//    Serial.println("服务端返回：" + result);
    return result;
}
}


 void writeUserFile(String information){
  Serial.println("****************写操作开始******************");
  
  File writeFile = SPIFFS.open(fileName,"w");//建立File对象，并表示将要进行“写”操作
  writeFile.println(information);//写
  writeFile.close();//写操作完成后关闭文件
  
  Serial.println("SPIFFS Write Finished");
  Serial.println("****************写操作完成******************\n");
}

void readUserFile(){
  Serial.println("****************读操作开始******************");
  String result;
  if(SPIFFS.exists(fileName)){
    Serial.print(fileName);
    Serial.println(" Found");
    
    File readFile = SPIFFS.open(fileName,"r");
    
    for(int i = 0; i<readFile.size();i++){
      //Serial.print((char)readFile.read());//一次读取一个字符
      result+=readFile.read();
    }
    
  }else{
    Serial.print(fileName);
    Serial.println(" Not Found");
  }
  
  Serial.println("****************读操作完成******************内容是：\n");
  Serial.println(result);
}
