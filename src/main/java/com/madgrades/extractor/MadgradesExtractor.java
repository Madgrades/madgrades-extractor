package com.madgrades.extractor;

import com.madgrades.extractor.extraction.ExtractedPdf;
import com.madgrades.extractor.extraction.PdfExtractor;
import com.madgrades.extractor.output.AbnormalRowLogger;
import com.madgrades.extractor.output.CsvWriter;
import com.madgrades.extractor.report.Report;
import com.madgrades.extractor.tabularization.TabularOutcome;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

/**
 * Command-line entry point for Madgrades Extractor.
 * <p>
 * Discovers PDF files from the provided input paths, extracts report data,
 * tabularizes the results, and writes output files.
 */
@Command(
    name = "madgrades-extractor",
    mixinStandardHelpOptions = true,
    description = "Extracts tabular data from UW-Madison report PDFs.",
    version = "1.0.0"
)
public class MadgradesExtractor implements Runnable {

    @Spec
    private CommandSpec spec;

    @Option(
        names = { "-v", "--verbose" },
        description = "Print abnormal-row warnings."
    )
    private boolean verbose;

    @Option(
        names = { "-t", "--threads" },
        description = "Number of extraction threads."
    )
    private int threadCount = Runtime.getRuntime().availableProcessors();

    @Option(
        names = { "-o", "--output-dir" },
        description = "Directory where output files will be written.",
        defaultValue = "."
    )
    private File outputDirectory;

    @Option(
        names = { "-e", "--excluded" },
        description = "Also write excluded rows output files."
    )
    private boolean writeExcludedFiles;

    @Parameters(
        arity = "1..*",
        paramLabel = "INPUT",
        description = "PDF files and/or directories to process."
    )
    private File[] inputs;

    /**
     * Executes the extraction process using the configured command-line options.
     * <p>
     * Validates user input, discovers PDF files, processes each report, and writes
     * the resulting output files.
     */
    @Override
    public void run() {
        AbnormalRowLogger.setEnabled(verbose);

        if (threadCount < 1) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                "Thread count must be at least 1"
            );
        }

        if (outputDirectory.exists() && !outputDirectory.isDirectory()) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                outputDirectory + " is not a directory"
            );
        }

        try {
            Files.createDirectories(outputDirectory.toPath());
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not create output directory: " + outputDirectory,
                e
            );
        }

        List<File> pdfFiles = discoverPdfFiles();

        if (pdfFiles.isEmpty()) {
            throw new CommandLine.ParameterException(
                spec.commandLine(),
                "No PDF files were provided."
            );
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
        try {
            for (File pdfFile : pdfFiles) {
                try {
                    System.out.println(
                        "Extracting " + pdfFile.getAbsolutePath()
                    );
                    processPdf(pdfFile, threadPool);
                } catch (RuntimeException e) {
                    System.err.println("Failed to process PDF: " + pdfFile);
                    e.printStackTrace(System.err);
                }
            }
        } finally {
            threadPool.shutdown();
        }
    }

    /**
     * Processes a single PDF report.
     * <p>
     * Reads the PDF, extracts its contents, tabularizes the extracted data, and
     * writes the resulting files.
     *
     * @param pdfFile    the PDF file to process
     * @param threadPool the executor service used for page extraction
     * @throws RuntimeException if the PDF cannot be read or output files cannot be
     *                          written
     */
    private void processPdf(File pdfFile, ExecutorService threadPool) {
        byte[] pdfBytes;
        Report report;
        try {
            pdfBytes = Files.readAllBytes(pdfFile.toPath());
            report = Report.fromPdf(pdfFile);
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to read PDF file: " + pdfFile,
                e
            );
        }

        ExtractedPdf extractedPdf;
        try {
            extractedPdf = PdfExtractor.extract(
                pdfBytes,
                report.getPdfFormat().getColumns(),
                threadPool
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                "PDF extraction was interrupted: " + pdfFile,
                e
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract PDF: " + pdfFile, e);
        }

        TabularOutcome tabularOutcome = report
            .getPdfFormat()
            .tabularize(extractedPdf, report.getTermCode());

        File includedFile = new File(
            outputDirectory,
            report.getResultsFileName()
        );
        try {
            List<List<String>> includedRows = tabularOutcome.getIncludedRows();
            includedRows.add(0, report.getHeaders());
            CsvWriter.write(includedRows, includedFile);
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to write results file: " + includedFile,
                e
            );
        }

        if (writeExcludedFiles) {
            File excludedFile = new File(
                outputDirectory,
                report.getExcludedFileName()
            );
            try {
                List<List<String>> excludedRows =
                    tabularOutcome.getExcludedRows();
                excludedRows.add(0, report.getHeaders());
                CsvWriter.write(excludedRows, excludedFile);
            } catch (IOException e) {
                throw new RuntimeException(
                    "Failed to write excluded rows file: " + excludedFile,
                    e
                );
            }
        }
    }

    /**
     * Launches the command-line application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new MadgradesExtractor()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Discovers PDF files from the configured input paths.
     * <p>
     * Individual PDF files are included directly. Directories are searched
     * recursively and all PDF files found within them are included.
     *
     * @return the discovered PDF files
     * @throws CommandLine.ParameterException if an input path does not exist or a
     *                                        file input is not a PDF
     * @throws RuntimeException               if a directory cannot be scanned
     */
    private List<File> discoverPdfFiles() {
        List<File> pdfFiles = new ArrayList<>();

        for (File input : inputs) {
            if (!input.exists()) {
                throw new CommandLine.ParameterException(
                    spec.commandLine(),
                    "Input does not exist: " + input
                );
            }

            if (input.isFile()) {
                if (!input.getName().toLowerCase().endsWith(".pdf")) {
                    throw new CommandLine.ParameterException(
                        spec.commandLine(),
                        "Input file is not a PDF: " + input
                    );
                }

                pdfFiles.add(input);
            } else if (input.isDirectory()) {
                try (Stream<Path> paths = Files.walk(input.toPath())) {
                    paths
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(file ->
                            file.getName().toLowerCase().endsWith(".pdf")
                        )
                        .forEach(pdfFiles::add);
                } catch (IOException e) {
                    throw new RuntimeException(
                        "Failed to scan directory: " + input,
                        e
                    );
                }
            }
        }

        return pdfFiles;
    }
}
