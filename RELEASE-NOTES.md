# checklistenserver Release Notes

__Release 6.2.0__

More secure response headers

__Release 6.2.0__

[remove ReplaceAccessTokenRestClient](https://github.com/heike2718/checklistenserver/issues/11)

[delete @LoginConfig(authMethod = "MP-JWT") from ChecklistenServerApp](https://github.com/heike2718/checklistenserver/issues/10)

[ClientAccessTokenService: throws clause does not fit](https://github.com/heike2718/checklistenserver/issues/12)

[UserSession: make uuid immutable](https://github.com/heike2718/checklistenserver/issues/13)


__Release 6.1.0__

personalized todo lists

__Release 6.0.1__

sessionID cookie is now working

__Release 6.0.0__

server side sessions and session cookies in order to protect client access tokens and jwt

__Release 5.3.0__

upgrade to quarkus 0.27.0 in order to fix several CVEs

__Release 5.2.0__

upgrade to quarkus 0.26.1

__Release 5.1.0__

new Version Resource

__Release 5.0.1__

fixed Löschen von Checklisten nicht mehr möglich (Server-error)

__11.09.2019__ attempt to fix

[quarkus-issue-3382](https://github.com/quarkusio/quarkus/issues/3382)
[quarkus-issue-4282](https://github.com/quarkusio/quarkus/pull/4282)

__Release 5.0.0__

migrated to quarkus :D

__Release 4.1.0__

serverseitiges Loggen von clientErrors eingebaut

__Release 4.0.0__

Refresh client access token: Payload Signatur nicht abwärtskompatibel.

__Release 3.0.0__

Nicht abwärtskompatible Änderung: Client-Secrets werden von Server zu Server gegen ein AccessToken eingetauscht, das der checklisten-app übermttelt wird und welches diese dann in den redirects zur auth-app verwendet (statt wie bisher die clientID)
