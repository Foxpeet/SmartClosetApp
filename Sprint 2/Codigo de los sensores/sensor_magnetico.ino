#include<M5Stack.h>

#define RELE_PIN 26
#define Pin5 G5 //pin 5 is connected to the sensor

void setup() {

  M5.begin(true, false, true); //initialize serial monitor
  M5.Lcd.setTextSize(3);
  pinMode(RELE_PIN, OUTPUT); //setting Arduino pin as an input
  pinMode(Pin5, INPUT_PULLUP);
  //digitalWrite(RELE_PIN, LOW);
  //attachInterrupt(digitalPinToInterrupt(Pin2), alarma, LOW);
}

//void alarma(){
//  for(int i=0;i<5;i++){
//    if(i%2==0){
//      digitalWrite(Pin5, HIGH);
//      delay(50);
//    } else {
//      digitalWrite(Pin5, LOW);
//      delay(50);
//    }
//  }
//  digitalWrite(Pin5, LOW);
//}

void loop() {
  M5.Lcd.setCursor(0,0);
  int war = digitalRead(Pin5); //read the value from the sensor

  delay(1);
  //delay to eliminate vibration contact

  if (war == LOW){ //status low means that the bolt is clamped, condition, high - free bolt
    M5.Lcd.print ("Puerta abierta");
    M5.Lcd.setCursor(0,150);
    M5.Lcd.print (war);
    digitalWrite(RELE_PIN, LOW);
    //alarma
  //digitalWrite(13, HIGH); //also, in case of detection of the open door indicator from under the pin 13 lights up
  }else {
    M5.Lcd.print ("Puerta Cerrada");
    M5.Lcd.setCursor(0,150);
    M5.Lcd.print (war);
    digitalWrite(RELE_PIN, HIGH);
  }
  M5.update();
  delay(200); //delay between consecutive readings
}
