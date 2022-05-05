#!/bin/bash
#This script is looking for a Java8 installation in your JAVA_HOME and JAVA_HOME/../ directories.
#Especially in the case that your main Java version ins not 8. Your Java8 has to be inside a Folder named jdk1.8* or jre1.8*.
#Where as '*' are wildcards.
#It will try to execute the Generator if it finds a sutiable java.exe

if ! [[ -x "$JAVA_HOME" ]]; then #no java home existing
    echo "No JAVA_HOME was found on your System. Please install a Java8 Java Runtime Enviorment to run the Generator."
    read -r
    exit 1
elif [[ "$JAVA_HOME" == *"1.8"* ]]; then #java home points to a java8
    JAVA_EXE="$JAVA_HOME/bin/java.exe"
    if ! [[ -x $JAVA_EXE ]]; then
        echo "JAVA_HOME does not point to a vaild jre or jdk installation!"
        read -r
        exit 1
    fi
else #checking parent directory
    PAR="$(realpath "$JAVA_HOME/..")"

    echo "Did not find Java8 in your JAVA_HOME ($JAVA_HOME)"
    echo "Checking parent directory $PAR..."
    echo

    if [ -d "$PAR/jdk1.8"* ] || [ -d "$PAR/jre1.8"* ]; then
        JAVA_EXE="$(realpath -e "$PAR/jdk1.8"*"/bin/java.exe")"
        if ! [[ -x $JAVA_EXE ]]; then
            echo "No java.exe was found in target path $JAVA_EXE"
            read -r
            exit 1
        fi
    else
        echo "Did not find a Java8 there either."
        echo "Please install a version of java8 in the same directory as ur current Java version"
        echo "(this happens automatically on default installation)"
        read -r
        exit 1
    fi
fi

echo "Java8 found at $JAVA_EXE"
command -v pdflatex >/dev/null 2>&1 || {
    echo
    echo "Warning! pdflatex was not found. Please install MikTex on this System."
    echo "Otherwise this program will not run properly!"
    echo
}
echo "Starting generator..."
("$JAVA_EXE" -jar GeneratorUI.jar &)
