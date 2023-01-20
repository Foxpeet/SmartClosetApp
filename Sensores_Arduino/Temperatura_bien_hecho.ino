//------------------------------------------------------------------
//--------------------------- LIBRERIAS ----------------------------
//------------------------------------------------------------------
#include <M5Stack.h>
#include <DHT.h>
#include <ArduinoMqttClient.h>
#include <WiFi.h>

#define SENSOR 26
#define TIME_TO_SLEEP 5
#define uS_TO_S_FACTOR 1000000
//------------------------------------------------------------------
//----------------------- VARIABLES/OBJETOS ------------------------
//------------------------------------------------------------------
char ssid[] = "NoAvena";    // your network SSID (name)
char pass[] = "123456789";

WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

const char broker[] = "test.mosquitto.org";
int        port     = 1883;
const char topic[]  = "ioT1/sensores/temperatura";
const char topic2[]  = "ioT1/sensores/humedad";

const long interval = 1000;
unsigned long previousMillis = 0;

float temp, humedad;

DHT dht (SENSOR, DHT11);
//------------------------------------------------------------------
//-------------------------- LEER VALORES --------------------------
//------------------------------------------------------------------
void leerValores(){
  humedad = dht.readHumidity();
  temp = dht.readTemperature();
}
//------------------------------------------------------------------
//------------------------- MOSTRAR SERIAL -------------------------
//------------------------------------------------------------------
void mostrarDatosSerial(){
  Serial.print("Temperatura: ");
  Serial.println(temp);
  Serial.print("ºC Humedad: ");
  Serial.print(humedad);
  Serial.println("%");
}
//------------------------------------------------------------------
//-------------------------- MOSTRAR LCD ---------------------------
//------------------------------------------------------------------
void mostrarDatosLCD(){  
  if (digitalRead(SENSOR)) {
    M5.Lcd.println ("ON ");
  }
  else {
    M5.Lcd.println ("OFF ");
  };
  
  M5.Lcd.clear();
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(5, 20);
  M5.Lcd.print("Temperatura: ");
  M5.Lcd.print(temp);
  M5.Lcd.setCursor(5, 40);
  M5.Lcd.print("ºC Humedad: ");
  M5.Lcd.print(humedad);
  M5.Lcd.print("%");
  M5.update();
}
//------------------------------------------------------------------
//---------------------------- SET UP ------------------------------
//------------------------------------------------------------------
void setup() {
  M5.begin(true, false, false);
  M5.Lcd.setTextSize(3);
  Serial.begin(9600);
  
  pinMode(SENSOR, INPUT_PULLUP); 
  //conexiones() ---------------------------------------------------
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println("You're connected to the network");
  M5.Lcd.setCursor(5, 40);
  M5.Lcd.println("Conectau");
  Serial.println();
  
  //conection to the broker
  Serial.print("Attempting to connect to the MQTT broker: ");
  
  Serial.println(broker);

  if (!mqttClient.connect(broker, port)) {
    Serial.print("MQTT connection failed! Error code = ");
   
    Serial.println(mqttClient.connectError());

    while (1);
  }
 
  Serial.println("You're connected to the MQTT broker!");
  M5.Lcd.setCursor(5, 80);
  M5.Lcd.println("Conectau 2 ");
  Serial.println();

  
  dht.begin();
  
  /*esp_sleep_enable_timer_wakeup(TIME_TO_SLEEP * uS_TO_S_FACTOR);
    Serial.println("mimimimi...");
  esp_deep_sleep_start(); //duerme al ESP32 (modo SLEEP)*/
}
//------------------------------------------------------------------
//----------------------------- LOOP -------------------------------
//------------------------------------------------------------------
void loop() {

//  probarConexion();
  leerValores();
  mostrarDatosSerial();
  mostrarDatosLCD();
  mqttClient.beginMessage(topic);
  mqttClient.println(temp);
  mqttClient.endMessage();
  
  mqttClient.beginMessage(topic2);
  mqttClient.println(humedad);
  mqttClient.endMessage();
  //contador++;
  
  delay(1000);
}
