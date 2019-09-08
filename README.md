# checklistenserver
REST-API für Winkels Checklisten

## Development Server

Sarten des Servers mit

java -jar checklistenserver.jar -Dcom.kumuluz.ee.configuration.file=path-to-auth-provider-config.yaml

Resourcen zum Testen, ob das Backend da ist:

http://localhost:9300/checklisten-api/dev/hello

http://localhost:9300/checklisten-api/heartbeats?heartbeatId=heartbeat

### Metriken

with Accept-Header application/json

GET http://localhost:9300/metrics

OPTIONS http://localhost:9300/metrics

### Starten in Eclipse

mit IDEChecklistenAppRunner und VMargs

	-Dcom.kumuluz.ee.configuration.file=/home/heike/git/konfigurationen/checklistenservice/checklistenservice-config.yaml

oder

	-Djavax.net.debug=all -Dcom.kumuluz.ee.configuration.file=/home/heike/git/konfigurationen/checklistenservice/checklistenservice-config.yaml

## URL zum Holen eines AccessTokens vom Authprovider in Browser-Anwendungen

	http://localhost:9300/checklisten-api/accesstoken

Der Endpoint ruft seinerseits die URL

	http://localhost:9000/authprovider/clients/oauth/token

mit dem Payload OAuthClientCredentials auf:

	{"clientId":"extrem-geheime-clientId","clientSecret":"g3He1m","nonce":"horst"}


## Ablauf SignUp

* POST-Request an signup/secret mit SignUpPayload: Verifiziert, ob der Person gestattet werden kann, ein Benutzerkonto anzulegen
* Falls erfolgreich: redirect zum AuthProvider mit url https://auth-provider-domain/signup?accessToken=ddas-access-token&state=signup
* Nach Antwort vom AuthProvider: POST-Request an signup/user mit dem JWT als Authorizaton-Header. Eintrag in Tabelle USERS (AuthenticationFilter holt die UUID heraus und setzt sie in den ContainerRequestContext als property 'USERID')
* Auth-Provider redirected zurük. Nach Antwort: Dialog mit Hinweis auf Postfach und Aktivierungslink öffnen.

## Ablauf LogIn

* redirect zum AuthProvider mit url https://auth-provider-domain#/login?accessToken=das-access-token&redirectUrl=die-redirect-url
* Nach Antwort vom AuthProvider: GET-Request an users/{sub aus JWT} . Es wird geprüft, ob das Subject bekannt ist. Erst dann geht's in die Anwendung

## Validierung des JWT

* erfolgt im AuthorizationFilter
* Der public key des AuthProviders steht momentan in der checklistenservice-config.yaml um die Validierung aus kumuluzee-jwt verwenden zu
können,
(evtl. gibt es auch mal eine key-versionierung mit allem pi-pa-po. Dann wird er über die URL geholt, die in der checklistenservice-config.yaml unter application-config -> auth-public-key-url steht
* Validierung des JWT erfolgt mit MP-JWT (microprofile JWT)
* Die groups werden aus dem JWT genommen und um (momentan nicht persistente) statische Checklisten-Groups ergänzt.

## API zum Testen

Server-Komponente kann getestet werden mit

http://localhost:9300/checklisten-api/dev/hello
http://localhost:9300/checklisten-api/heartbeats?heartbeatId=heartbeat


## Relesenotes

* __Release 4.1.0:__ serverseitiges Loggen von clientErrors eingebaut

* __Release 4.0.0:__ Refresh client access token: Payload Signatur nicht abwärtskompatibel.

* __Release 3.0.0:__ Nicht abwärtskompatible Änderung: Client-Secrets werden von Server zu Server gegen ein AccessToken eingetauscht, das der checklisten-app übermttelt wird und welches diese dann in den redirects zur auth-app verwendet (statt wie bisher die clientID)
