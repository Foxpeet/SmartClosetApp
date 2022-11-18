#include "HX711.h"
#include <M5Stack.h>
#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>

char ssid[] = "Laura";
char pass[] = "00112233";

const int DOUT=5;
const int CLK=G2;

char texto[200];
int hora;
boolean rec=0;

AsyncUDP udp;
HX711 balanza;

void setup() {
 
  M5.begin(true,false,true);
  M5.Lcd.setTextSize(3);
  
  Serial.begin(9600);
  WiFi.mode(WIFI_STA);
 Serial.print("Conectando a SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    Serial.print(".");
    delay(3000);
  }
  Serial.println("CONECTADO AL WIFI");
  Serial.println();
  
  if (udp.listen(1234)) {
    Serial.print("UDP Listening on IP: ");
    Serial.println(WiFi.localIP());
    udp.onPacket([](AsyncUDPPacket packet) {
      int i= 200;
      while(i--){
        *(texto+i)=*(packet.data()+i);
      }
      rec=1;
    });
  }
  balanza.begin(DOUT, CLK);
  balanza.set_scale(195700.00*2); // Establecemos la escala
  balanza.tare(20);

}

void loop() {
  M5.Lcd.setCursor(0,0);
  if(rec){
    rec=0;
    M5.Lcd.setCursor(1,1);
    udp.broadcastTo("Recibido",1234);
    udp.broadcastTo(texto,1234);
    hora=atol(texto);
    StaticJsonDocument<200>jsonBufferRecv;
    DeserializationError error = deserializeJson(jsonBufferRecv , texto); 
    if(error)
    return;

    Serial.println();
    int temperatura= jsonBufferRecv ["Temperatura"];
    Serial.println(temperatura);
    M5.Lcd.setCursor(0,16);
    M5.Lcd.print("Temperatura: ");
    M5.Lcd.print(temperatura);
    int humedad= jsonBufferRecv ["Humedad"];
    Serial.println(humedad); 
    M5.Lcd.setCursor(0,48);
    M5.Lcd.print("Humedad: ");
    M5.Lcd.print(humedad);
  }
  
  Serial.print("Peso: ");
  Serial.print(balanza.get_units(20),3);
  Serial.println(" kg");
  M5.Lcd.clear();
  M5.Lcd.setCursor(0,80);
  M5.Lcd.print("Peso: ");
  M5.Lcd.print(balanza.get_units(20),3);
  M5.Lcd.println(" kg");
  if(balanza.get_units(20) > 0.01){
    M5.Lcd.setCursor(0,112);
    M5.Lcd.print("Se te ha caido una prenda");
  }
  M5.update();
  delay(500);
}
