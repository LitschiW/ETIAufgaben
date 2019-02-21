# ETI Aufgabengenerator


## Downloads

[v0.3](https://github.com/LitschiW/ETIPAVorschlaege/raw/master/Releases/v0.3.zip)

## Voraussetungen

Zum Benutzen benötigt ihr:

1. Eine LateX Installtion mit PDFTeX. Zum Beispiel [MikTeX](https://miktex.org/download)
2. Stelle sicher, dass pdflatex als Befehl über die Konsole ausgeführt werden kann. Dazu muss ein Eintrag in die PATH Systemvariable geschreiben werden, was i.d.R. automatisch bei der Installation von LaTeX gemacht wird.
3. [Java8](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) JRE oder JDK. Wenn bereits eine Java Installation vorhanden ist (die nicht Java8 ist) empfehle ich, dass ihr Java8 in den selben Ordner installiert in dem auch euer anderes Java liegt (Standartmäßig *C:\Program Files\Java\\* unter Windows oder */usr/lib/jvm/* unter Ubuntu). Auch das sollte mit einer Standartinstallation passieren.

## Programm starten

1. Entpackt die .zip
2. Falls ihr nur Java8 auf eurem System habt, könnt ihr auch direkt die **Generator.jar** per Doppelklick oder mit *"java -jar Generator.jar"* ausführen. Ansonsten könnt ihr  **execute_Win.bat** oder **execute_Unix.sh.** ausführen. Diese Scripts suchen nach einer Java8 Installation basierend auf **JAVA_HOME**.
 
## Bedienung

Im linken Bereich *__Aufgabenbereiche__* könnt ihr die Aufgabenbereiche auswählen für die ihr Aufgaben generiert haben wollt.
Unter *__Subaufgabenbereiche__* könnt ihr eure Auswahl noch genauer Einschränken. Die Zahl in Klammern gibt die Anzahl der verfügbaren Aufgaben zu diesem Subaufgabenbereich an.

In den *__Optionen__* kann man weitere Einstellungen treffen:
 - generiere LaTeX Projekt **-** generiert ein LaTeX-Projekt, sodass das Ergebnisdokument manuell  noch nachbearbeitet werden können.
 - ~~generiere Antworten~~ **-** noch nicht verfügbar
 -  zufällige Subaufgabenbereiche **-** Schaltet die Auswahl von Subaufgabenbereichen auf Zufall. Im erscheinenden Textfeld kann man die Anzahl der Subaufgabenbereiche eingeben die ausgewählt werden sollen.#
 - max. Anzahl der Subaufgaben **-** beschränkt die Anzahl der Aufgaben die von einem Subaufgabenbereich ausgewählt werden.

 Im unteren Bereich kann man den Ausgabepfad für das Projekt oder die Datei auswählen.
 Sollte eine entsprechende PDF schon vorhanden sein erscheint eiene Warnung und das Speichern wird deaktiviert.   Der "Überschreiben Dialog", der beim Auswählen einer vorhandenen Datei erscheint, hat keinen (noch) keinen Effekt! 
