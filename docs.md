# Design Philosophy

In the context of how the data from the PDF reports might end up in some sort of frontend (e.g. extraction -> tabularization -> data correction -> parsing -> organization -> frontend), here are my thoughts about the job of the **extractor**:

*"Line" here refers to characters or words in a PDF that share a page and similar y-coordinate. "Row" here refers to a line as extracted by a PDF extraction library and separated into cells based on what the extractor interprets as columns. Despite trying not to use them interchangeably, they are essentially one-to-one.*

* It is the **extractor's** job to take the PDF reports as input and output a tabular version of the data found within them.
* On accuracy:
   * Data negligently excluded in the extraction process can never be recovered by steps later down the line, so accuracy is paramount.
   * The accuracy of the extractor is measured by its similarity to the contents of the PDF, not necessarily the usability of each row; "issues" within the output that would not be issues for a human "reader" of the PDF should be fixed though.
   * The extractor should be permitted to output a "useless" row if the corresponding line in the PDF is likewise useless.

 # Strategy
 * Every line/row of a PDF is put in three categories/buckets: confidently included in the output, confidently excluded in the output, and unsure.
 * The goal is to reduce the count of lines/rows in the unsure bucket to zero by evolving our idea of what included and excluded rows are.
 * All lines/rows should be capable of being output in the pursuit of accuracy, even the "confidently excluded" ones.

But how do we determine exclusion or inclusion? These options were considered:

* Based on the (absolute or relative) position of each line on the PDF (e.g. is it below a y-coordinate that we know to be just below the headers and above a y-coordinate we know to be just above the footers? Or maybe it is below one recognizable line and above another recognizable line, so it should be included).
* Based on the contents of the data found in each cell of the grid we create on the PDF for extraction. Probably with a Java idiomatic constructor into a pdfReportRow or similar object.
* Based on the contents of each row as a collapsed string. Removing all whitespace from this string should remove all reliance on the x-coordinates used to determine the horizontal lines bordering columns in the grid we draw on each page of the PDFs.

There are issues with a purely positional approach: an absolute approach assumes that the tables on each page in a given PDF don't move, which is not the case. A relative approach assumes that certain marker rows will always be present in a given PDF. Both absolute and relative approaches choose whether a line/row is included or not without care for the actual contents of the row; if a row that should be excluded has a "good" position it will be included, and if a row that should be included is in a "bad" position, it will be skipped.

My issue with the grid approach is that it makes the x-coordinates we choose to determine columns integral to whether a line/row is valid or not.

These approaches are not mutually exclusive, though.

So, the implemented strategy is heavily based on the collapsed string approach. Using regexes seemed to be the most natural way to determine the contents of strings, so that's what's used. This wasn't flawless though; regexes aren't as easily maintained and more importantly, determining whether a line should be included or not without the absolute position of the text is, of course, harder than with it (which is the grid approach). In fact, there are lines in the grade reports that are impossible to determine whether to include or exclude based on their collapsed string form alone. For these lines the extractor falls back to the positional approach, as even the grid approach wouldn't have helped.

# Current Bugs/Extractor Inaccuracies

* In grade reports starting in term 1194, some course names contain � characters. Tabula's `ObjectExtractor.extract(int pageNumber)` converts these characters into spaces. Some course names to watch out for as of August 2026:
   * Culture et soci�t�s
   * Features in�Italian�Literature
   * The�Italian�Novel
