# Smart Facility Management

Webbasiertes Wartungs- und Ticketsystem für Gebäudestörungen, entwickelt als Fallstudie im Rahmen des Moduls **DSBPIIJEE01 – Programmierung industrieller Informationssysteme mit Java EE** an der IU Internationalen Hochschule (SoSe 2026).

## Funktionen

- Störmeldungen als Tickets erfassen (Titel, Beschreibung, Kategorie, Priorität, Standort)
- Ticketliste mit Filter nach Status und Priorität
- Tickets bearbeiten, Status ändern, Techniker zuweisen, Kosten erfassen
- Kommentare an Tickets
- Rollenbasierter Zugriff (Admin, Technician, Reporter)
- Registrierung und Login mit verschlüsselten Passwörtern (BCrypt)

## Technischer Stack

| Komponente | Technologie |
|---|---|
| Plattform | Jakarta EE 11 |
| Server | Apache Tomcat 11 |
| UI | JavaServer Faces 4 + PrimeFaces 14 |
| CDI | Weld Servlet 5 |
| Persistenz | JPA / Hibernate 6 |
| Datenbank | H2 (dateibasiert) |
| Passwort-Hashing | jBCrypt |
| Build | Maven (WAR) |

## Architektur

```
Entity → Repository (DAO) → Service → ManagedBean (@Named) → XHTML
```

Strikte Schichtentrennung: Repositories kapseln alle Datenbankzugriffe, Services enthalten die Geschäftslogik, Beans halten den View-State und delegieren an Services.

## Voraussetzungen

- Java 21+
- Maven 3.8+
- Apache Tomcat 11

## Setup und Start

```bash
# Projekt bauen
mvn clean package

# WAR deployen
cp target/facility-management-1.0-SNAPSHOT.war $TOMCAT_HOME/webapps/ROOT.war

# Tomcat starten
$TOMCAT_HOME/bin/startup.sh
```

Die Anwendung ist danach erreichbar unter `http://localhost:8080`.

Die H2-Datenbankdatei wird beim ersten Start automatisch unter `./data/facilitydb` angelegt.

## Testdaten

Beim ersten Start werden automatisch folgende Benutzer und Beispieldaten angelegt:

| Benutzername | Passwort | Rolle |
|---|---|---|
| `admin` | `admin123` | Admin |
| `tech1` | `tech123` | Technician |
| `reporter1` | `rep123` | Reporter |

Zusätzlich werden 6 Tickets in verschiedenen Status/Priorität/Kategorie-Kombinationen sowie 3 Kommentare erstellt.

## Rollen und Berechtigungen

| Aktion | Admin | Technician | Reporter |
|---|:---:|:---:|:---:|
| Ticket erstellen | ✓ | ✓ | ✓ |
| Alle Tickets sehen | ✓ | ✓ | nur eigene |
| Status ändern / zuweisen | ✓ | ✓ | ✗ |
| Kommentar schreiben | ✓ | ✓ | ✓ |
| Ticket löschen | ✓ | ✗ | ✗ |
| Benutzerverwaltung | ✓ | ✗ | ✗ |

## Projektstruktur

```
src/main/java/iu/piisj/facilitymanager/
├── auth/          # Authentifizierung, Session, Filter
├── comment/       # Kommentar-Entity und Service
├── repository/    # Data Access Objects (JPA)
├── ticket/        # Ticket-Entity, Enums, Services, Controller
├── user/          # User-Entity, UserRole-Enum
└── util/          # PersistenceProvider, DataSeeder
src/main/webapp/
├── WEB-INF/       # web.xml, faces-config.xml
├── templates/     # JSF-Layout-Template
├── resources/css/ # Stylesheet
└── *.xhtml        # Seiten (Login, Tickets, Formular, Detail)
```
