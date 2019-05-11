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
* Ist das Subject nicht bekannt, muss das secret abgefragt werden. Bei Erfolg wird ein user angelegt.

## Validierung des JWT

* erfolgt im AuthorizationFilter
* Der public key des AuthProviders wird über die URL geholt, die in der checklistenservice-config.yaml unter
application-config -> auth-public-key-url steht
* Validierung des JWT erfolgt mit MP-JWT (microprofile JWT)
* Die groups werden aus dem JWT genommen und um statische Checklisten-Groups ergänzt.


## Notizen (chronologisch absteigend)
* __Erste vollständige Version ohne Authorisierung:__ branch 1_ohne-auth

