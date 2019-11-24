# checklistenserver Release Notes

__Release 6.0.0:__ server side sessions and session cookies in order to protect client access tokens and jwt

__Release 5.3.0:__ upgrade to quarkus 0.27.0 in order to fix several CVEs

__Release 5.2.0:__ upgrade to quarkus 0.26.1

__Release 5.1.0:__ new Version Resource

__Release 5.0.1:__ fixed Löschen von Checklisten nicht mehr möglich (Server-error)

__11.09.2019:__ attempt to fix

* [quarkus-issue-3382](https://github.com/quarkusio/quarkus/issues/3382)
* [quarkus-issue-4282](https://github.com/quarkusio/quarkus/pull/4282)

__Release 5.0.0:__ migrated to quarkus :D

__Release 4.1.0:__ serverseitiges Loggen von clientErrors eingebaut

__Release 4.0.0:__ Refresh client access token: Payload Signatur nicht abwärtskompatibel.

__Release 3.0.0:__ Nicht abwärtskompatible Änderung: Client-Secrets werden von Server zu Server gegen ein AccessToken eingetauscht, das der checklisten-app übermttelt wird und welches diese dann in den redirects zur auth-app verwendet (statt wie bisher die clientID)
