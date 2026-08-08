# uw-madison-pdf-extractor

Extract structured tabular data from UW–Madison academic PDF reports.

This repository owns the extraction and transformation step between source PDFs and downstream datasets. It handles PDF-specific structure, carries contextual information across pages and sections, applies narrowly scoped cleanup rules, and produces flat records suitable for analysis or further processing.

Raw PDFs are maintained separately in [`uw-madison-pdf-archive`](https://github.com/Madgrades/uw-madison-pdf-archive). Converting extracted records into Madgrades' relational data model is also outside the scope of this repository.

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

## Data quality

Extraction correctness is evaluated primarily through reproducible checks on the resulting data.

Useful signals include:

* total row counts
* row counts by subject, course, instructor, term, or source document
* missing-value counts by column
* duplicate and malformed record counts
* distinct-value and cardinality statistics
* skipped or unrecognized source rows
* consistency checks between related fields
* unexpected changes in aggregate statistics between extraction runs

Changes to extraction logic should make it possible to compare both individual records and aggregate statistics against previous results. Unexpected differences can then be investigated as potential extraction regressions.

Small manually reviewed datasets may also be used for targeted correctness tests where appropriate.

## Output

The extractor should produce flat, self-contained records that remain close to the information represented in the source PDFs.

Context that appears only once in a PDF section—such as a subject, course, or other grouping field—should be carried into the applicable output rows so downstream consumers do not need to reconstruct the PDF's layout.

Output should be deterministic, inspectable, and easy to diff.
