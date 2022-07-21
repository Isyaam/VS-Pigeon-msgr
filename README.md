# GUI des Instant-Messengers Pigeon<br />

**Anmerkung**
Leider ist das Projekt nicht fertiggestellt. <br />
Es fehlen noch Funktionen und ein lauffähiger Server<br />

falls man sich dennoch durch die UI klicken möchte dann setze im LoginController
in Zeile 51 u. 93 ```ret = 1```

## Entwickler<br />

Christophe B. (yoshegg)<br />

Jens K. (Isyaam)<br />

Nadine W. (Warneke)<br />

## Coding-Richtlinien<br />

Der Einfachheit halber wird die standardmäßige Autoformattierung von IntelliJ benutzt, um den Code aufgeräumt zu halten.<br />
Der über die Menüleiste erreichbare Befehl `Code -> Reformat Code` sollte vor dem Committen ausgeführt werden. <br />

Außerdem nutzen wir 4 Spaces zum Einrücken (keine Tabs).<br />

### JavaDoc-Kommentare<br />

Die Dokumentation von Funktionen sollte folgendermaßen aussehen:<br />

```
/**
 * Represents / Does / Makes this thing
 * 
 * @param parameter1 The xyz thing
 * @return The abc thing
 */
private void method(Object parameter1) {
    return null
}
```

## Kompilieren und ausführen<br />

Das Programm kann durch einen einzigen Befehl kompiliert und sofort gestartet werden:<br />
```
mvn compile exec:java
```
Der Befehl kann natürlich auch aufgeteilt und die einzelnen Schritte nacheinander ausgeführt werden.<br />
**Achtung**: Der in IntelliJ zu findende Befehl `compiler:compile` ist nicht identisch zu `compile`.<br />
Bei ersterem werden die Resourcen nicht in die entstehende `.jar`-Datei kopiert.<br />

**Tipp**: Es ist möglich, in IntelliJ direkt einen *Run*-Befehl zu definieren: In der Menüleiste auf `Run -> Run...` drücken.<br />
Dann auf `Edit configurations...`. Oben kann man einen Namen auswählen (z.B. *Compile & run*), bei *Command line* trägt man dann nur noch `compile exec:java` ein und speichert das Ganze. (Wenn man ganz auf Nummer sicher gehen will, kann man `clean compile exec:java` eintragen, da so alle zwischengespeicherten Dateien gelöscht werden, was aber auch den ganzen Prozess längern dauern lässt.)

## GUI Aussehen
Login-Fenster<br />
![](https://github.com/Isyaam/Images/blob/main/msgr_login.PNG)<br />
Register-Fenster<br />
![](https://github.com/Isyaam/Images/blob/main/msgr_register.PNG)<br />
Chat-Fenster<br />
![](https://github.com/Isyaam/Images/blob/main/msgr_chat.PNG)<br />
![](https://github.com/Isyaam/Images/blob/main/msgr_chat2.PNG)<br />
