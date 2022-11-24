# madgrades-extractor

This project reads UW Madison grade distribution and course report PDF files (published by the UW Madison Office
of the Registrar) and converts them into CSV or SQL dump files.

You will find published, update-to-date datasets at [Kaggle](https://www.kaggle.com/Madgrades/uw-madison-courses).

![https://i.imgur.com/9ZrwRMt.png](https://i.imgur.com/9ZrwRMt.png)

## Conversion

The conversion process for a single term is as follows:

1. Open DIR report for the term.

    a. Extract table from PDF (using [tabula](https://github.com/tabulapdf/tabula-java))
    
    b. Read each row, adding new section per row.
    
    c. Collate section info as necessary (i.e. 2 instructors for 1 single section)
    
    d. Collate courses which appear to be cross-listed (based on similarity between sections offered)
    
2. Open grades report for the term.

    a. Extract table from PDF
    
    b. Read each row, add add each section grade data to course data added by the DIR report process

Typically all terms are extracted so this process repeats for each term.

## Command Line Usage

Build it yourself with `mvn clean install` or grab a release from the releases page.

```
Usage: <main class> [options]
  Options:
    -d, -download
      Download the PDF reports instead of extracting data
      Default: false
    -e, -exclude
      Comma-separated list of term codes to exclude (ex. -e 1082)
    -f, -format
      The output format
      Default: CSV
      Possible Values: [CSV, MYSQL]
    -l, -list
      Output list of terms to extract
      Default: false
    -out, -o
      Output directory path for exported files (ex. -o ../data)
      Default: ./
    -t, -terms
      Comma-separated list of term codes to run (ex. -t 1082,1072)
```

Examples:

* `java -jar madgrades-final-1.0-SNAPSHOT.jar`: will fetch every term and output files to the current directory
* `java -jar madgrades-final-1.0-SNAPSHOT.jar -t 1082`: will fetch just term 1082
* `java -jar madgrades-final-1.0-SNAPSHOT.jar -o ../ -t 1082,1072`: will fetch terms 1072 and 1082, output to `../`

## Relational Diagram

The CSV or SQL dumps are in the format of a collection of relational entities modeled something like this:

![diagram](https://cdn.rawgit.com/thekeenant/3c6dbb04f94243df6e84f152e40d87a9/raw/8ce37501a495447f5b71315a6f9aef7b4b7013bc/madgrades-diagram.svg)
