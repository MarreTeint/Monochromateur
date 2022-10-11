void setup() {
  Serial.begin(2000000);
}

void loop() {
  if(Serial.read()=='A')
    for(int i = 0 ; i<400 ; i++){
      int out = i%100;
      Serial.print(out);
      Serial.print('\n');
    }
}
