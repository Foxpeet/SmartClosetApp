#include<M5Stack.h>
#include <ArduinoMqttClient.h>
#include <WiFi.h>
//------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------- VARIABLES ---------------------------------------------------
//------------------------------------------------------------------------------------------------------------------
#define RELE_PIN 26
#define Pin5 G5 //pin 5 is connected to the sensor
int war = 0;

String texto;

char ssid[] = "NoAvena";    // your network SSID (name)
char pass[] = "123456789";

WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

const char broker[] = "test.mosquitto.org";
int        port     = 1883;
const char topic[]  = "ioT1/sensores/luz";
const char topic2[] = "ioT1/sensores/cositas";

const long interval = 1000;
unsigned long previousMillis = 0;

void onMqttMessage(int messageSize) {
  // we received a message, print out the topic and contents
  texto="";
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");
  // use the Stream interface to print the contents
  while (mqttClient.available()) {
    texto = texto + (char)mqttClient.read();
  }
 
  Serial.println(texto.length());
  Serial.println(texto);
  Serial.println();
}
//------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------- SET UP -----------------------------------------------------
//------------------------------------------------------------------------------------------------------------------
void setup() {
  M5.begin(true, false, true); //initialize serial monitor
  M5.Lcd.setTextSize(3);
 
  pinMode(RELE_PIN, OUTPUT); //setting Arduino pin as an input
  pinMode(Pin5, INPUT_PULLUP);


   Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println("You're connected to the network");
  Serial.println();
  
  //conection to the broker
  M5.Lcd.setCursor(5, 40);
  M5.Lcd.println("Conectau");

  Serial.print("Attempting to connect to the MQTT broker: ");
  Serial.println(broker);

  if (!mqttClient.connect(broker, port)) {
    Serial.print("MQTT connection failed! Error code = ");
    Serial.println(mqttClient.connectError());

    while (1);
  }
  Serial.println("You're connected to the MQTT broker!");
  M5.Lcd.setCursor(5, 80);
  M5.Lcd.println("Conectau 2");
  
  Serial.println();
  mqttClient.onMessage(onMqttMessage);
  mqttClient.subscribe("ioT1/sensores/luz");
}
//------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------- PUERTA -----------------------------------------------------
//------------------------------------------------------------------------------------------------------------------
void puerta(){
  if (war == LOW){ //status low means that the bolt is clamped, condition, high - free bolt
    M5.Lcd.print ("Puerta abierta");
    M5.Lcd.setCursor(0,150);
    M5.Lcd.print (war);
    digitalWrite(RELE_PIN, LOW);
    luz();
  //digitalWrite(13, HIGH); //also, in case of detection of the open door indicator from under the pin 13 lights up
  }else {
    M5.Lcd.print ("Puerta Cerrada");
    M5.Lcd.setCursor(0,150);
    M5.Lcd.print (war);
    digitalWrite(RELE_PIN, HIGH);
  }
  //delay between consecutive readings
  delay(200);
}
void luz(){
  if (texto == "ON") {
    //varia la posicion de 0 a 180
    digitalWrite(RELE_PIN, LOW);
  } else if (texto == "OFF") {
    //varia la posicion de 180 a 0
    digitalWrite(RELE_PIN, HIGH);
  } 
}
//------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------ LOOP ------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------
void loop() {
  M5.Lcd.setCursor(0,0);
  war = digitalRead(Pin5); //read the value from the sensor
  puerta();
  mqttClient.poll();
  Serial.println(texto);

  mqttClient.beginMessage(topic2);
  mqttClient.println("a");
  mqttClient.endMessage();
  
  delay(1);//delay to eliminate vibration contact
  
  
  M5.update(); 
}
