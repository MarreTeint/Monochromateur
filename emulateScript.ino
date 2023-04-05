#define ON 1
#define OFF 0
#define dt 1 //sleep time in ms

void setup() {
  Serial.begin(2000000);
  // motor 1
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  // motor 2
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  // motor on/off
  pinMode(6, OUTPUT);
  resetPins();
  turnMotor(OFF);
}

void resetPins(){
  for(int i = 2 ; i<=6 ; i++){
    digitalWrite(i, LOW);
  }
}

void doAStep(int stepNum){
  switch(stepNum%4){
    case 0:
    digitalWrite(2, HIGH);
    //Serial.println("2 on HIGH");
    delay(dt);
    digitalWrite(2, LOW);
    //Serial.println("2 on LOW");
    delay(dt);
    break;

    case 1:
    digitalWrite(4, HIGH);
    //Serial.println("4 on HIGH");
    delay(dt);
    digitalWrite(4, LOW);
    //Serial.println("4 on LOW");
    delay(dt);
    break;

    case 2:
    digitalWrite(3, HIGH);
    //Serial.println("3 on HIGH");
    delay(dt);
    digitalWrite(3, LOW);
    //Serial.println("3 on LOW");
    delay(dt);
    break;

    case 3:
    digitalWrite(5, HIGH);
    //Serial.println("5 on HIGH");
    delay(dt);
    digitalWrite(5, LOW);
    //Serial.println("5 on LOW");
    delay(dt);
    break;
  }
}

void turnMotor(int on){
  if(on){
    digitalWrite(6, HIGH);
  }else{
    digitalWrite(6, LOW);
  }
}
//int i = 20000000;
int i = 0;
void loop() {
  //if(Serial.read()=='A'){
  //  for(int i = 0 ; i<701 ; i++){
  //    int out = i%101;
  //    Serial.print(out);
  //    Serial.print('\n');
  //  }
  //  
  //}
  
  //Serial.println(i);
  delay(50);
  doAStep(i);
  i++;  
}
