# checklistenserver
REST-API für Winkels Checklisten

## Development Server

Sarten des Servers mit Maven:

	mvn clean compile quarkus:dev

## Resourcen zum Testen, ob das Backend da ist:

	http://localhost:9300/checklisten-api/dev/hello

	http://localhost:9300/checklisten-api/version

	http://localhost:9300/checklisten-api/heartbeats?heartbeatId=heartbeat

## Login

	http://localhost:9300/auth/login

Der Endpoint gibt

	http://localhost:9000/authprovider/clients/oauth/token

mit dem Payload OAuthClientCredentials auf:

	{"clientId":"extrem-geheime-clientId","clientSecret":"g3He1m","nonce":"horst"}


## Ablauf SignUp

* SignUp mit secret wurde nach 6.0.1 entfernt, da jetzt jede, der möchte ein Checklistenkonto anlegen könen soll.
* redirect zum AuthProvider mit url https://auth-provider-domain/signup?accessToken=ddas-access-token&state=signup
* Nach Antwort vom AuthProvider: POST-Request an signup/user mit dem JWT als Authorizaton-Header. Eintrag in Tabelle USERS (AuthenticationFilter holt die UUID heraus und setzt sie in den ContainerRequestContext als property 'USERID')
* Auth-Provider redirected zurük. Nach Antwort: Dialog mit Hinweis auf Postfach und Aktivierungslink öffnen.

## Ablauf LogIn

* redirect zum AuthProvider mit url https://auth-provider-domain#/login?accessToken=das-access-token&redirectUrl=die-redirect-url
* Nach Antwort vom AuthProvider: POST-Request an /auth/session . Es wird geprüft, ob das Subject bekannt ist. Die Antwort ist eine Session auf dem Server und ein Session-Cookie für den client, das im AuthorizationFilter bei jedem Request geprüft wird. Außedem kommt noch ein UserSession-Objekt mit, in dem ein expiresAt-Datum steht sowie ein Zufallsstring für die idReferenz der Session. Bei jedem Request wird die Session wieder verlängert, so dass man nur nach langer Untätigkeit rausfliegt.

## Relesenotes

[Release-Notes](RELEASE-NOTES.md)

