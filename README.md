# madgrades

UW-Madison course and grade distribution data extraction.

This project reads UW Madison grade distribution and course report PDF files and converts them into CSV or SQL dump files.

## Usage

Only SQL dumps are exported via command-line currently.

```
Usage: <main class> [options]
  Options:
    -e, -exclude
      Comma-separated list of term codes to exclude (ex. -e 1082)
    -l, -list
      Output list of terms to extract
      Default: false
    -out, -o
      Output directory path for exported files (ex. -o ../data)
      Default: .
    -t, -terms
      Comma-separated list of term codes to run (ex. -t 1082,1072)
```

Examples:

* `java -jar madgrades-final-1.0-SNAPSHOT.jar`: will fetch every term and output files to the current directory
* `java -jar madgrades-final-1.0-SNAPSHOT.jar -t 1082`: will fetch just term 1082
* `java -jar madgrades-final-1.0-SNAPSHOT.jar -o ../ -t 1082,1072`: will fetch terms 1072 and 1082, output to `../`

## Relational Diagram

Currently it can output a collection of relational entities modeled something like this:

![diagram](https://cdn.rawgit.com/thekeenant/3c6dbb04f94243df6e84f152e40d87a9/raw/8ce37501a495447f5b71315a6f9aef7b4b7013bc/madgrades-diagram.svg)
