# OIBSIP

**Oasis Infobyte Internship Program** — project submissions by **Yuvraj Sarathe**.

Repositories and folders follow the official submission format:
`OIBSIP/[TrackName]-[Level/Task]-[ProjectName]/`

## Projects

| Track | Task | Project | Status |
|---|---|---|---|
| Java Development | Task 2 | [Number Guessing Game (Java Swing)](Java-Task2-NumberGuessingGame/) | Complete |

## Quick Start

Each project has its own README with full instructions. For the Java task:

```bash
cd Java-Task2-NumberGuessingGame
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out com.guessgame.Main
```

Windows (PowerShell):

```powershell
cd Java-Task2-NumberGuessingGame
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp out com.guessgame.Main
```
