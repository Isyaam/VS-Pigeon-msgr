# GUI des Instant-Messengers Pigeon

<<<<<<< HEAD
...

## Kompilieren und ausführen

Das Programm kann durch einen einzigen Befehl und kompiliert und sofort gestartet werden:
=======
## Entwickler

Christophe Biwer (yoshegg)

Jens Kolz (Isyaam)

Nadine Warneke (Warneke)

## Coding-Richtlinien

Der Einfachheit halber wird die standardmäßige Autoformattierung von IntelliJ benutzt, um den Code aufgeräumt zu halten. Der über die Menüleiste erreichbare Befehl `Code -> Reformat Code` sollte vor dem Committen ausgeführt werden. 

Außerdem nutzen wir 4 Spaces zum Einrücken (keine Tabs).

### JavaDoc-Kommentare

Die Dokumentation von Funktionen sollte folgendermaßen aussehen:

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

## Kompilieren und ausführen

Das Programm kann durch einen einzigen Befehl kompiliert und sofort gestartet werden:
>>>>>>> developer
```
mvn compile exec:java
```

<<<<<<< HEAD
Der Befehl kann natürlich auch aufgeteilt und die einzelnen Schritte nacheinander ausgeführt werden. **Achtung**: Der in IntelliJ zu findende Befehl `compiler:compile` ist nicht identisch zu `compile`. Bei ersterem werden die Resourcen nicht in die entstehende `.jar`-Datei kopiert. 
=======
Der Befehl kann natürlich auch aufgeteilt und die einzelnen Schritte nacheinander ausgeführt werden. 

**Achtung**: Der in IntelliJ zu findende Befehl `compiler:compile` ist nicht identisch zu `compile`. Bei ersterem werden die Resourcen nicht in die entstehende `.jar`-Datei kopiert. 

**Tipp**: Es ist möglich, in IntelliJ direkt einen *Run*-Befehl zu definieren: In der Menüleiste auf `Run -> Run...` drücken. Dann auf `Edit configurations...`. Oben kann man einen Namen auswählen (z.B. *Compile & run*), bei *Command line* trägt man dann nur noch `compile exec:java` ein und speichert das Ganze. (Wenn man ganz auf Nummer sicher gehen will, kann man `clean compile exec:java` eintragen, da so alle zwischengespeicherten Dateien gelöscht werden, was aber auch den ganzen Prozess längern dauern lässt.)
>>>>>>> developer
