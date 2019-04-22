# checklistenserver
REST-API für Winkels Checklisten

## Ablauf SignUp

* POST-Request an signup/secret mit SignUpPayload: Verifiziert, ob der Person gestattet werden kann, ein Benutzerkonto anzulegen
* Falls erfolgreich: redirect zum AuthProvider mit url https://auth-provider-domain/signup?clientId=die-client-id&redirectUrl=die-redirect-url
* Nach Antwort vom AuthProvider: POST-Request an signup/user mit dem JWT als Authorizaton-Header. Eintrag in Tabelle USERS (AuthenticationFilter holt die UUID heraus und setzt sie in den ContainerRequestContext als property 'USERID')
* Nach Antwort: Dialog mit Hinweis auf Postfach und Aktivierungslink öffnen.

## Ablauf LogIn

* redirect zum AuthProvider mit url https://auth-provider-domain/login?clientId=die-client-id&redirectUrl=die-redirect-url
* Nach Antwort vom AuthProvider: POST-Request an sessions/[noch zu entscheiden] mit dem JWT als Body. Es wird geprüft, ob das
Subject bekannt ist. Erst dann gehts in die Anwendung

## Validierung des JWT

* Der public key des AuthProviders wird über die URL geholt, die in der checklistenservice-config.yaml unter
application-config -> auth-public-key-url steht
* Zur Validierung dient die Klasse JWTProvider, die an einen Wrapper für den auth0-JWTVerifier delegiert (JWTVerifierWrapper)
* Zusätzlich wird expirationAt validiert und ein status 901 zurückgesendet, wenn die Session abgelaufen ist.


## Notizen (chronologisch absteigend)
* __Erste vollständige Version ohne Authorisierung:__ branch 1_ohne-auth

