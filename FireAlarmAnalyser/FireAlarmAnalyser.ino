#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WebSocketsServer.h>

#define WIFI_SSID "WadowiceNet_mshg"
#define WIFI_PSWD "ften76a5"

ESP8266WebServer server(80);
bool alarmActive = false;
void handleRoot() {
  String message = String(alarmActive ? "true" : "false");
  server.send(200, "text/html; charset=UTF-8",  message);
}

void reset() {
    if (server.hasArg("reset")) {
        String resetValue = server.arg("reset");
        if (resetValue == "true") {
            delay(500);  // Krótkie opóźnienie przed resetem
            ESP.reset();
        }
    }
}

void handleSetAlarm() {
  if (server.hasArg("alarm")) {
    String alarmValue = server.arg("alarm");

    // Ustawienie statusu alarmu na podstawie wartości z żądania
    if (alarmValue == "true") {
      alarmActive = true;
    } else if (alarmValue == "false") {
      alarmActive = false;
    }
    
    server.send(200, "text/plain", "Alarm status set to: " + String(alarmActive ? "true" : "false"));
  } else {
    server.send(400, "text/plain", "Missing 'alarm' parameter");
  }
}

void setup() {
  
  Serial.begin(115200);
  Serial.println("a");
  WiFi.begin(WIFI_SSID, WIFI_PSWD);
  Serial.print("Connecting to: ");
  Serial.print(WIFI_SSID);
  Serial.print(".....");
  int i = 0;
  while(WiFi.status() != WL_CONNECTED){
    delay(600);
    i++;
    Serial.print(WiFi.status());
    Serial.print(i);
    // Serial.print(WiFi.scanNetworks(true));
    Serial.print(" ");
  }
  Serial.println(" ");
  Serial.println("Connected: ");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  Serial.println();
  // SERVER
  server.on("/", handleRoot);  // Obsługa żądania na głównej stronie "/"
  server.on("/setAlarm", handleSetAlarm);  // Obsługa zmiany statusu alarmu
  server.on("/setReset", reset);
  server.begin();
  Serial.println("Serwer HTTP uruchomiony.");
}

void loop() {
  server.handleClient();  // Obsługa żądań HTTP
}
