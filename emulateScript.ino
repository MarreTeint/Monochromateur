void setup() {
  // put your setup code here, to run once:
  Serial.begin(2000000);

}

void loop() {
  // put your main code here, to run repeatedly:
  //while (Serial.available()==0){}
    int out = 23;
    //rand()%99+1;
    Serial.print(out);
    Serial.print('\n');
    delay(100);
}
