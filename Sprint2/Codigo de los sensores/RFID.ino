//RST           D9
//SDA(SS)      D10
//MOSI         D11
//MISO         D12
//SCK          D13

#include <SPI.h>
#include <MFRC522.h>
#include <M5Stack.h>

#define RST_PIN 2           // Pin 9 para el reset del RC522
const int SS_PIN = 26;            // Pin 10 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN);   // Crear instancia del MFRC522

void printArray(byte *buffer, byte bufferSize) {
   for (byte i = 0; i < bufferSize; i++) {
      Serial.print(buffer[i] < 0x10 ? " 0" : " ");
      Serial.print(buffer[i], HEX);
   }
}

void setup()
{
  M5.begin(true,false,true);
  M5.Lcd.setTextSize(3);
  M5.Lcd.printf("Conexión con M5Stack realizada.");
   Serial.begin(9600);      //Inicializa la velocidad de Serial
   SPI.begin();         //Función que inicializa SPI
   mfrc522.PCD_Init();     //Función  que inicializa RFID
}

void loop()
{
  M5.Lcd.setCursor(0,0);
   // Detectar tarjeta
   if (mfrc522.PICC_IsNewCardPresent())
   {
      if (mfrc522.PICC_ReadCardSerial())
      {
         Serial.print(F("Card UID:"));
         printArray(mfrc522.uid.uidByte, mfrc522.uid.size);
         Serial.println();
         M5.Lcd.clear();
         M5.Lcd.setTextSize(2);
         M5.Lcd.print("Usuario reconocido");

         // Finalizar lectura actual
         mfrc522.PICC_HaltA();
      }
   }
   M5.update();
   delay(250);
}
