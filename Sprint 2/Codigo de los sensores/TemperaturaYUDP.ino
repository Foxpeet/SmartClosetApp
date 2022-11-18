#include <WiFi.h>
#include <M5Stack.h>
#include <AsyncUDP.h>
#include <ArduinoJson.h>
#include <DHT.h>

#define SENSOR 26
float temp, humedad;

DHT dht (SENSOR, DHT11);

char ssid[] = "Laura";
char pass[] = "00112233";

AsyncUDP udp;
StaticJsonDocument<200> jsonBuffer; //tamaño maximo de los datos

void setup()
{
  M5.begin(true,false,true);
  M5.Lcd.setTextSize(3);
  pinMode(SENSOR,INPUT_PULLUP);
  Serial.begin(115200);
  dht.begin();

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
      Serial.write(packet.data(), packet.length());
      Serial.println();
    });
  }
}

float medirTemperatura()
{
  return temp = dht.readTemperature();
}

float medirHumedad()
{
  return humedad = dht.readHumidity();
}

void loop()
{
  M5.Lcd.setCursor(0,0);
  delay(1000);
  char texto[200];
  jsonBuffer["Temperatura"] = medirTemperatura(); //Datos introducidos en el objeto “jsonbuffer"
  jsonBuffer["Humedad"] = medirHumedad();
  serializeJson(jsonBuffer, texto); //paso del objeto “jsonbuffer" a texto para
  //transmitirlo
  udp.broadcastTo(texto, 1234); //se envía por el puerto 1234 el JSON
  //como texto

  humedad= medirHumedad();
  temp= medirTemperatura();

  Serial.print("Temperatura: ");
  Serial.println(temp);
  Serial.print("ºC Humedad: ");
  Serial.print(humedad);
  Serial.println("%");
 
  if (digitalRead(SENSOR)){M5.Lcd.println ("ON ");}
    else {M5.Lcd.println ("OFF ");};
  M5.Lcd.clear();
  M5.Lcd.setTextSize(2);
  M5.Lcd.print("Temperatura: ");
  M5.Lcd.print(temp);
  M5.Lcd.print("ºC Humedad: ");
  M5.Lcd.print(humedad);
  M5.Lcd.print("%");
  M5.update();
 
  delay(2000);
}
