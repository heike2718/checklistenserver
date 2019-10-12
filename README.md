# checklistenserver
REST-API für Winkels Checklisten

## Development Server

Sarten des Servers mit Maven:

	mvn clean compile quarkus:dev

## Resourcen zum Testen, ob das Backend da ist:

	http://localhost:9300/checklisten-api/dev/hello

	http://localhost:9300/checklisten-api/heartbeats?heartbeatId=heartbeat

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

## Relesenotes

[Release-Notes](RELEASE-NOTES.md)

