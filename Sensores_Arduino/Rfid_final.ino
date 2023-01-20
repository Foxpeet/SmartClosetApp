//------------------------------------------------------------------
//--------------------------- LIBRERIAS ----------------------------
//------------------------------------------------------------------
#include <M5Stack.h>
#include <MFRC522.h>
#include <ArduinoMqttClient.h>
#include <WiFi.h>
//------------------------------------------------------------------
//----------------------- VARIABLES/OBJETOS ------------------------
//------------------------------------------------------------------
char ssid[] = "A";    // your network SSID (name)
char pass[] = "tortilladepatatas";

WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

const char broker[] = "test.mosquitto.org";
int        port     = 1883;
const char topic1[]  = "ioT1/sensores/rfid";

const long interval = 1000;
unsigned long previousMillis = 0;


#define RST_PIN  22    //Pin 9 para el reset del RC522
#define SS_PIN  5   //Pin 10 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN); //Creamos el objeto para el RC522

String tarjetita;

//------------------------------------------------------------------
//---------------------------- SET UP ------------------------------
//------------------------------------------------------------------
void setup() {
  M5.begin(true, false, false);
  M5.Lcd.setTextSize(3);
  Serial.begin(9600);
  Serial.println("Lectura del UID"),

  
  Serial.print("Attempting to connect to WPA SSID: ");
  M5.Lcd.setCursor(5, 8);
  M5.Lcd.println("Attempting to connect to WPA SSID: ");
  
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    M5.Lcd.setCursor(5, 28);
    M5.Lcd.println("");
    delay(5000);
  }
  Serial.println("You're connected to the network");
  
  M5.Lcd.setCursor(5, 40);
  M5.Lcd.println("You're connected to the network");
  
  Serial.println();
  
  //conection to the broker

  Serial.print("Attempting to connect to the MQTT broker: ");

  M5.Lcd.setCursor(5, 60);
  M5.Lcd.println("Attempting to connect to the MQTT broker: ");
  
  Serial.println(broker);

  if (!mqttClient.connect(broker, port)) {
    Serial.print("MQTT connection failed! Error code = ");
    
    M5.Lcd.setCursor(5, 80);
    M5.Lcd.print("MQTT connection failed! Error code = ");
    
    Serial.println(mqttClient.connectError());

    while (1);
  }
  Serial.println("You're connected to the MQTT broker!");
  Serial.println();

  mfrc522.PCD_Init(); // Iniciamos  el MFRC522

  M5.update();
}
//------------------------------------------------------------------
//----------------------------- LOOP -------------------------------
//------------------------------------------------------------------
void loop() {
 // Revisamos si hay nuevas tarjetas  presentes
 if ( mfrc522.PICC_IsNewCardPresent()) 
        {  
      //Seleccionamos una tarjeta
            if ( mfrc522.PICC_ReadCardSerial()) 
            {
              tarjetita = "";
                  // Enviamos serialemente su UID
                  Serial.print("Card UID:");
                  for (byte i = 0; i < mfrc522.uid.size; i++) {
                          Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
                          Serial.print(mfrc522.uid.uidByte[i], HEX); 
                          tarjetita.concat(mfrc522.uid.uidByte[i]);
                  } 
                  Serial.println();
                  Serial.print(tarjetita);
                  mqttClient.beginMessage(topic1);
                  mqttClient.print(tarjetita);
                  mqttClient.endMessage();
                  // Terminamos la lectura de la tarjeta  actual
                  mfrc522.PICC_HaltA();         
            }      
  } 
}
