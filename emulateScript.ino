void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

}

void loop() {
  // put your main code here, to run repeatedly:
  //while (Serial.available()==0){}
    int out = rand()%99+1;
    Serial.println(out);
    //delay(1000);
}
