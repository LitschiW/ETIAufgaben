# Aufgabengenerator

## Downloads (portable; Java8 Jre not included)

[v0.4 Parallels!](https://github.com/LitschiW/ETIPAVorschlaege/raw/master/Releases/v0.4.zip)  
What's new?:
- Generieren im Hintergrund und von mehreren Pdf's gleichzeitig
- kl. Bugfixes
- execute Script für Unix/Mac Systeme
- weitere Aufgaben. 
 
[v0.3 (outdated)](https://github.com/LitschiW/ETIPAVorschlaege/raw/master/Releases/v0.3.zip) Basisversion mit UI und Generator

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

## Teilnehmen

Für typos, bugs, usw gibt es Issues.

#### Forken for dummies

1.  Oben Rechts auf Fork drücken. (Das erstellt eine Kopie des Repos auf eurem Account.)
2.  Eigenes Repository klonen und Änderungen Implementieren.
3.  Wenn zufrieden Pull-Request auf das Hauptrepository stellen (evtl. eigene Source updaten, siehe GitHub UI)

#### Neue Aufgaben hinzufügen
Neue Aufgaben können zu den jeweiligen (Sub-)Aufgabenbereichen hinzugefügt werden.  
Im Aufgaben Ordner liegen 2 Templates:  
 - **AufgabeVorlage.tex**, ist ein sehr simples Skelett für eine neue Aufgabe. Sie ist **nicht** interpretierbar und enthält Hinweiße zum Erstellen von Aufgaben.  
 - **KompilierbareVorlage.tex** ist eine Interpretierbare Projektdatei. In dieser Datei könnt ihr euren Latex Code schreiben, falls ihr sicherstellen wollt das er läuft.  
 
 Wenn ihr mit eurem Code zufrieden seit könnt ihr den Inhalt der *document* Umgebung  in eine .tex Datei kopiern und diese in den passenden Ordner schieben. Alternativ kann man natürlich auch direkt eine .tex Datei erstellen in die man den Aufgabencode schreibt. 

Wichtig ist, dass der konkrete Text der Aufgabe mit *\subsection{*[NAME_DER_AUFGABE]*}* beginnt.  
Mit *\keeptogether{*[INHALT]*}* können Abschnitte des Textes gruppiert werden, sodass kein Zeilenumbruch stattfindet.  
Der Befehl *\subsubsection{*[NAME]*}* wird ebenfalls unterstützt.
Wenn zusätzliche packages benötigt werden sollten die unter [SetupData\usepackage.tex](SetupData\usepackage.tex) hinzugefügt werden. (Mit mit Erklärung und Hinweis auf die betreffende Aufgabe).

Vor einem Commit auf das remote Repository sollten die **KompilierbareVorlage.tex** in ihren Orginalzustand gebracht werden. Pull-Requests, ohne eine orginale **KompilierbareVorlage.tex** Datei werden nicht akzeptiert.
