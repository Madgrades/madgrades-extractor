package com.cadenkruckeberg.mce.extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Utility class for extracting the contents of a PDF document.
 * <p>
 * Extraction is performed on a per-page basis using a supplied
 * {@link java.util.concurrent.ExecutorService}. The extracted pages are
 * returned in the same order as they appear in the source document.
 */
public final class PdfExtractor {
  private PdfExtractor() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Extracts all pages of a PDF document.
   * <p>
   * Extraction is performed concurrently using the supplied executor service. The
   * extracted pages are returned in the same order as they appear in the source
   * document.
   *
   * @param pdfBytes   the PDF document bytes
   * @param columns    the list of column X-coordinates used for table extraction
   * @param threadPool the executor service used to process pages concurrently
   * @return the extracted PDF
   * @throws IOException          if the PDF cannot be read or a page fails during
   *                              extraction due to an I/O error
   * @throws InterruptedException if the current thread is interrupted while
   *                              waiting for page extraction to complete
   */
  public static ExtractedPdf extract(byte[] pdfBytes, List<Float> columns, ExecutorService threadPool)
      throws IOException, InterruptedException {
    ArrayList<Future<ExtractedPdfPage>> pageFutures = new ArrayList<>();

    try (PDDocument totalPagesDocument = PDDocument.load(pdfBytes)) {
      int totalPages = totalPagesDocument.getNumberOfPages();

      for (int i = 0; i < totalPages; i++) {
        PageExtractorWorker worker = new PageExtractorWorker(pdfBytes, i + 1, columns);
        pageFutures.add(threadPool.submit(worker));
      }

      List<ExtractedPdfPage> orderedPages = new ArrayList<>();
      for (Future<ExtractedPdfPage> future : pageFutures) {
        orderedPages.add(future.get());
      }

      return new ExtractedPdf(orderedPages);
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw new RuntimeException("Worker failed unexpectedly", cause);
    }
  }
}
