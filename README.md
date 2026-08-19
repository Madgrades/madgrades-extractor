# madgrades-extractor

Extract structured tabular data from UW–Madison academic PDF reports.

This repository owns the extraction and transformation step between source PDFs and downstream datasets. It handles PDF-specific structure, carries contextual information across pages and sections, applies narrowly scoped cleanup rules, and produces flat records suitable for analysis or further processing.

Raw PDFs are maintained separately in [`madgrades-pdf-archive`](https://github.com/Madgrades/madgrades-pdf-archive). Converting extracted records into Madgrades' relational data model is also outside the scope of this repository.

## Overview

This tool extracts data from the University of Wisconsin–Madison's [Percentage Distribution of Grades Reports (PDGR)](https://registrar.wisc.edu/grade-reports/#:~:text=Course%20Grade%2DDistribution%20Reports) and [Departmental Instructional Reports (DIR)](https://registrar.wisc.edu/curricular-build/#dir:~:text=The%20Departmental%20Instructional%20Report%20%28DIR%29%20contains%20every%20scheduled%20course%20section%20for%20every%20department%2E), interprets the data as a human would, converts them to tabular form, and outputs them as CSV files.

* **Accurate:** This extractor was designed and built with accuracy as its most important feature from its conception. Though verifying 100% accuracy would be a monumental task, this tool's output has tested against the outputs of all other similar projects. As of August 2026, this tool extracts 1,940,159 PDF lines with 1,272,804 rows of actual data, while having only 6 known inaccuracies, resulting in an estimated 99.9995% accuracy.
* **Fast:** Built with [tabula-java](https://github.com/tabulapdf/tabula-java), this tool is dramatically faster than comparable tools built on other PDF parsing libraries. Additionally, this tool can take advantage of multithreading.
* **Maintainable:** Despite the inherent headaches that come with extracting data from PDFs, this tool was intended to be maintainable. It was built with the mindset that there are more new PDF formats to come, and they may even look wildly different from existing ones.

## Responsibilities

This project is responsible for:

* reading UW–Madison academic PDFs
* identifying records and relevant fields
* handling page boundaries, repeated headers, section transitions, and other PDF layout quirks
* carrying contextual values into individual records when necessary
* applying deterministic cleanup and correction rules
* producing flat tabular output
* measuring extraction consistency and detecting likely regressions

It is not responsible for:

* downloading or archiving source PDFs
* maintaining Madgrades' relational database schema
* building an API, frontend, or general-purpose data platform

## Getting Started

1. Ensure you have a Java 8 Runtime Environment.

2. Build from source with Maven: clone this repository and build with `mvn clean install`.

3. Obtain the report PDFs you want to extract data from. The recommended source is [this archive](https://github.com/Madgrades/madgrades-pdf-archive).

4. Run with `java -jar Madison-Courses-Extractor.jar` and the usage instructions.

## Usage

```
Usage: madgrades-extractor [-ehvV] [-o=<outputDirectory>] [-t=<threadCount>]
                           INPUT...
Extracts tabular data from UW-Madison report PDFs.
      INPUT...     PDF files and/or directories to process.
  -e, --excluded   Also write excluded rows output files.
  -h, --help       Show this help message and exit.
  -o, --output-dir=<outputDirectory>
                   Directory where output files will be written.
  -t, --threads=<threadCount>
                   Number of extraction threads.
  -v, --verbose    Print abnormal-row warnings.
  -V, --version    Print version information and exit.
```

> [!WARNING]
> Due to Tabula not being thread-safe, as you increase the number of threads, memory requirements go up.

## Documentation

To learn more about the design philosophy for this extractor, known limitations, and implementation details, read [docs.md](./docs.md)

*This project is unaffiliated with UW–Madison* 
