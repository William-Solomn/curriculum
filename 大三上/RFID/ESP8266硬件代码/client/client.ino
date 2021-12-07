#include <ESP8266WiFi.h>             // esp8266库
#include <ESP8266HTTPClient.h>
//#include <WiFiClient.h>
#include <SPI.h>
#include <MFRC522.h>

#include <TFT_eSPI.h> 
#define RST_PIN 5
#define SS_PIN 4

#define TFT_GREY 0x5AEB

#define button 0    // 按钮的引脚GPIO 0
TFT_eSPI tft = TFT_eSPI(); 

uint32_t targetTime = 0;  
static uint8_t conv2d(const char* p);
uint8_t hh = conv2d(__TIME__), mm = conv2d(__TIME__ + 3), ss = conv2d(__TIME__ + 6); 
byte omm = 99, oss = 99;
byte xcolon = 0, xsecs = 0;
unsigned int colour = 0;

MFRC522 mfrc(SS_PIN,RST_PIN);

const char* host = "47.100.49.178";
const int port = 8888;

int buttonState = 0;    //按钮的状态

char ssid[] = "abc";         // WiFi名
char pass[] = "1223334444";     // WiFi密码




void setup()
{
  Serial.begin(9600);
  while (!Serial);      //  等待端口的释放
  Serial.printf("\nConnecting to %s", ssid);
  
  tft.init();
  tft.setRotation(1);
  tft.fillScreen(TFT_BLACK);

  tft.setTextSize(1);
  tft.setTextColor(TFT_YELLOW, TFT_BLACK);

  targetTime = millis() + 1000;
  //卡片初始化
  SPI.begin();
  mfrc.PCD_Init();
  delay(4);
  mfrc.PCD_DumpVersionToSerial();
  Serial.println("亲爱的bro，现在可以刷卡了");

  //按钮与灯泡初始化
  pinMode(LED_BUILTIN, OUTPUT);   //设置点亮LED
  pinMode(button,INPUT);    //进行信号检测
  
  WiFi.begin(ssid, pass);         // 连接WiFi
  while (WiFi.status() != WL_CONNECTED) {       // 如果WiFi没有连接，一直循环打印点
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nConnected to network");
  Serial.print("My IP address is: ");
  Serial.println(WiFi.localIP());     // 打印开发板的IP地址


}

void loop()
{
    if (targetTime < millis()) {
    // Set next update for 1 second later
    targetTime = millis() + 1000;

    // Adjust the time values by adding 1 second
    ss++;              // Advance second
    if (ss == 60) {    // Check for roll-over
      ss = 0;          // Reset seconds to zero
      omm = mm;        // Save last minute time for display update
      mm++;            // Advance minute
      if (mm > 59) {   // Check for roll-over
        mm = 0;
        hh++;          // Advance hour
        if (hh > 23) { // Check for 24hr roll-over (could roll-over on 13)
          hh = 0;      // 0 for 24 hour clock, set to 1 for 12 hour clock
        }
      }
    }


    // Update digital time
    int xpos = 0;
    int ypos = 85; // Top left corner ot clock text, about half way down
    int ysecs = ypos + 24;

    if (omm != mm) { // Redraw hours and minutes time every minute
      omm = mm;
      // Draw hours and minutes
      if (hh < 10) xpos += tft.drawChar('0', xpos, ypos, 8); // Add hours leading zero for 24 hr clock
      xpos += tft.drawNumber(hh, xpos, ypos, 8);             // Draw hours
      xcolon = xpos; // Save colon coord for later to flash on/off later
      xpos += tft.drawChar(':', xpos, ypos - 8, 8);
      if (mm < 10) xpos += tft.drawChar('0', xpos, ypos, 8); // Add minutes leading zero
      xpos += tft.drawNumber(mm, xpos, ypos, 8);             // Draw minutes
      xsecs = xpos; // Sae seconds 'x' position for later display updates
    }
    if (oss != ss) { // Redraw seconds time every second
      oss = ss;
      xpos = xsecs;

      if (ss % 2) { // Flash the colons on/off
        tft.setTextColor(0x39C4, TFT_BLACK);        // Set colour to grey to dim colon
        tft.drawChar(':', xcolon, ypos - 8, 8);     // Hour:minute colon
        xpos += tft.drawChar(':', xsecs, ysecs, 6); // Seconds colon
        tft.setTextColor(TFT_YELLOW, TFT_BLACK);    // Set colour back to yellow
      }
      else {
        tft.drawChar(':', xcolon, ypos - 8, 8);     // Hour:minute colon
        xpos += tft.drawChar(':', xsecs, ysecs, 6); // Seconds colon
      }

      //Draw seconds
      if (ss < 10) xpos += tft.drawChar('0', xpos, ysecs, 6); // Add leading zero
      tft.drawNumber(ss, xpos, ysecs, 6);                     // Draw seconds
    }
  }
  //实时获取按钮被按下的状态
  String cardId="";
  buttonState = digitalRead(button);
  
  if(buttonState == LOW){
   
      digitalWrite(LED_BUILTIN,LOW);//点亮灯泡
      
      if ( ! mfrc.PICC_IsNewCardPresent()) {return;}
      

      if ( ! mfrc.PICC_ReadCardSerial()) {return;}
      
      digitalWrite(LED_BUILTIN,HIGH);delay(300);
      digitalWrite(LED_BUILTIN,LOW);delay(300);
      digitalWrite(LED_BUILTIN,HIGH);delay(300);
      digitalWrite(LED_BUILTIN,LOW);delay(300);
      digitalWrite(LED_BUILTIN,HIGH);delay(300);
      digitalWrite(LED_BUILTIN,LOW);delay(300);
      Serial.print("您将要录入的卡的id是：");
      for (byte i = 0;i<mfrc.uid.size;i++){
          //cardId+=String(mfrc.uid.uidByte[i] < 0x10 ? " 0" : " ");
          cardId+=String(mfrc.uid.uidByte[i],HEX);
      }
      Serial.println("本卡的id是:");
      Serial.println(cardId);
      sentMessageToServer(cardId);
      digitalWrite(LED_BUILTIN,LOW);delay(100);
      digitalWrite(LED_BUILTIN,HIGH);delay(100);
      digitalWrite(LED_BUILTIN,LOW);delay(100);
      digitalWrite(LED_BUILTIN,HIGH);delay(100);
      digitalWrite(LED_BUILTIN,LOW);delay(100);
      digitalWrite(LED_BUILTIN,HIGH);delay(100);
  }else{
    digitalWrite(LED_BUILTIN,HIGH);delay(3000);
    
  }
  
  //readAndRecordData();        
  
}
void sentMessageToServer(String cardid){
  WiFiClient client;
  
  String url = "/user/addCardSignle?studentCardId="+cardid;

  String httpRequest = String("POST ")+url+"\r\n"+
                        "Host: "+host+"\r\n"+
                        "Connection: close\r\n"+
                        "\r\n";
  if(client.connect(host,port)){
    Serial.print("成功连接上云服务器:");
    Serial.println(host);
    client.print(httpRequest);
    Serial.println("已成功将卡的信息存储至云服务器数据库!");
    Serial.println(client);
    String result = "";
    while (client.connected() || client.available()) { // 接口响应
      String line = client.readStringUntil('\n'); // 返回的某一行数据
      line.trim();
      if (line.length() > 0) { 
        result += line + "\r\n"; 
      }
    }
    client.stop(); // 断开连接
    Serial.println("服务端返回：" + result);

}
}
static uint8_t conv2d(const char* p) {
  uint8_t v = 0;
  if ('0' <= *p && *p <= '9')
    v = *p - '0';
  return 10 * v + *++p - '0';
}
