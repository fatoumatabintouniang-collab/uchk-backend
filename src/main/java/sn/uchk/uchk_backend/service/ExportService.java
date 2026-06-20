package sn.uchk.uchk_backend.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import sn.uchk.uchk_backend.entity.Etudiant;
import sn.uchk.uchk_backend.entity.InsertionProfessionnelle;
import sn.uchk.uchk_backend.repository.EtudiantRepository;
import sn.uchk.uchk_backend.repository.InsertionProfessionnelleRepository;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final EtudiantRepository etudiantRepository;
    private final InsertionProfessionnelleRepository insertionRepository;

    // ══════════════════════════════════════════════════════════════════
    //  EXCEL
    // ══════════════════════════════════════════════════════════════════

    public byte[] exportEtudiantsExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Étudiants");

            CellStyle titleStyle = buildTitleStyle(workbook);
            CellStyle headerStyle = buildHeaderStyle(workbook, IndexedColors.DARK_BLUE);
            CellStyle dataStyle = buildDataStyle(workbook);

            // Titre fusionné
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Université Cheikh Hamidou Kane – Liste des Étudiants");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            // En-têtes
            String[] headers = {"ID", "INE", "Nom", "Prénom", "Date Naissance",
                    "Formation", "Promo", "Année Début", "Année Sortie", "Email"};
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données
            List<Etudiant> etudiants = etudiantRepository.findAll();
            int rowNum = 3;
            for (Etudiant e : etudiants) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, e.getId() != null ? e.getId().toString() : "", dataStyle);
                setCell(row, 1, safe(e.getIne()), dataStyle);
                setCell(row, 2, safe(e.getNom()), dataStyle);
                setCell(row, 3, safe(e.getPrenom()), dataStyle);
                setCell(row, 4, e.getDateNaissance() != null ? e.getDateNaissance().toString() : "", dataStyle);
                setCell(row, 5, e.getFormation() != null ? e.getFormation().getIntitule() : "Non assigné", dataStyle);
                setCell(row, 6, safe(e.getPromo()), dataStyle);
                setCell(row, 7, e.getAnneeDebut() != null ? e.getAnneeDebut().toString() : "", dataStyle);
                setCell(row, 8, e.getAnneeSortie() != null ? e.getAnneeSortie().toString() : "En cours", dataStyle);
                setCell(row, 9, safe(e.getEmail()), dataStyle);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération Excel étudiants : " + e.getMessage(), e);
        }
    }

    public byte[] exportInsertionsExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Insertions Professionnelles");

            CellStyle titleStyle = buildTitleStyle(workbook);
            CellStyle headerStyle = buildHeaderStyle(workbook, IndexedColors.DARK_GREEN);
            CellStyle dataStyle = buildDataStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("UCHK – Suivi de l'Insertion Professionnelle des Diplômés");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            String[] headers = {"Étudiant (Nom)", "Étudiant (Prénom)", "Type d'Insertion",
                    "Poste", "Employeur", "Secteur", "Date Prise Poste", "Téléphone", "Email"};
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<InsertionProfessionnelle> insertions = insertionRepository.findAll();
            int rowNum = 3;
            for (InsertionProfessionnelle ins : insertions) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, ins.getEtudiant() != null ? safe(ins.getEtudiant().getNom()) : "", dataStyle);
                setCell(row, 1, ins.getEtudiant() != null ? safe(ins.getEtudiant().getPrenom()) : "", dataStyle);
                setCell(row, 2, ins.getType() != null ? ins.getType().name() : "", dataStyle);
                setCell(row, 3, safe(ins.getPoste()), dataStyle);
                setCell(row, 4, safe(ins.getEmployeur()), dataStyle);
                setCell(row, 5, safe(ins.getSecteurActivite()), dataStyle);
                setCell(row, 6, ins.getDatePrisePoste() != null ? ins.getDatePrisePoste().toString() : "", dataStyle);
                setCell(row, 7, safe(ins.getTelephone()), dataStyle);
                setCell(row, 8, safe(ins.getEmail()), dataStyle);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération Excel insertions : " + e.getMessage(), e);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  PDF  (iText 7)
    // FIX: PdfDocument et Document correctement fermés via finally
    // ══════════════════════════════════════════════════════════════════

    public byte[] exportEtudiantsPdf() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = null;
        Document document = null;
        try {
            pdf = new PdfDocument(new PdfWriter(out));
            document = new Document(pdf);

            document.add(new Paragraph("Université Cheikh Hamidou Kane – Liste des Étudiants")
                    .setFontSize(16).setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            float[] cols = {30, 55, 65, 65, 75, 95, 45, 50, 50, 95};
            Table table = new Table(UnitValue.createPercentArray(cols)).useAllAvailableWidth();

            DeviceRgb bleu = new DeviceRgb(0, 51, 102);
            for (String h : new String[]{"ID", "INE", "Nom", "Prénom", "Naissance",
                    "Formation", "Promo", "Début", "Sortie", "Email"}) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(h).setBold()
                                .setFontColor(ColorConstants.WHITE).setFontSize(8))
                        .setBackgroundColor(bleu)
                        .setTextAlignment(TextAlignment.CENTER));
            }

            List<Etudiant> etudiants = etudiantRepository.findAll();
            DeviceRgb grisClair = new DeviceRgb(235, 240, 250);
            boolean pair = false;
            for (Etudiant e : etudiants) {
                DeviceRgb bg = pair ? grisClair : null;
                addPdfCell(table, e.getId() != null ? e.getId().toString() : "", bg);
                addPdfCell(table, safe(e.getIne()), bg);
                addPdfCell(table, safe(e.getNom()), bg);
                addPdfCell(table, safe(e.getPrenom()), bg);
                addPdfCell(table, e.getDateNaissance() != null ? e.getDateNaissance().toString() : "", bg);
                addPdfCell(table, e.getFormation() != null ? e.getFormation().getIntitule() : "Non assigné", bg);
                addPdfCell(table, safe(e.getPromo()), bg);
                addPdfCell(table, e.getAnneeDebut() != null ? e.getAnneeDebut().toString() : "", bg);
                addPdfCell(table, e.getAnneeSortie() != null ? e.getAnneeSortie().toString() : "En cours", bg);
                addPdfCell(table, safe(e.getEmail()), bg);
                pair = !pair;
            }

            document.add(table);
            document.add(new Paragraph("Total : " + etudiants.size() + " étudiant(s)")
                    .setFontSize(9).setItalic().setMarginTop(10));

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF étudiants : " + e.getMessage(), e);
        } finally {
            // FIX: fermeture propre dans tous les cas
            if (document != null) try { document.close(); } catch (Exception ignored) {}
        }
    }

    public byte[] exportInsertionsPdf() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = null;
        try {
            PdfDocument pdf = new PdfDocument(new PdfWriter(out));
            document = new Document(pdf);

            document.add(new Paragraph("UCHK – Suivi de l'Insertion Professionnelle des Diplômés")
                    .setFontSize(15).setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            float[] cols = {65, 65, 75, 75, 85, 75, 65, 75, 95};
            Table table = new Table(UnitValue.createPercentArray(cols)).useAllAvailableWidth();

            DeviceRgb vert = new DeviceRgb(0, 100, 0);
            for (String h : new String[]{"Nom", "Prénom", "Type", "Poste",
                    "Employeur", "Secteur", "Date Poste", "Téléphone", "Email"}) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(h).setBold()
                                .setFontColor(ColorConstants.WHITE).setFontSize(8))
                        .setBackgroundColor(vert)
                        .setTextAlignment(TextAlignment.CENTER));
            }

            List<InsertionProfessionnelle> insertions = insertionRepository.findAll();
            DeviceRgb grisClair = new DeviceRgb(235, 250, 235);
            boolean pair = false;
            for (InsertionProfessionnelle ins : insertions) {
                DeviceRgb bg = pair ? grisClair : null;
                addPdfCell(table, ins.getEtudiant() != null ? safe(ins.getEtudiant().getNom()) : "", bg);
                addPdfCell(table, ins.getEtudiant() != null ? safe(ins.getEtudiant().getPrenom()) : "", bg);
                addPdfCell(table, ins.getType() != null ? ins.getType().name() : "", bg);
                addPdfCell(table, safe(ins.getPoste()), bg);
                addPdfCell(table, safe(ins.getEmployeur()), bg);
                addPdfCell(table, safe(ins.getSecteurActivite()), bg);
                addPdfCell(table, ins.getDatePrisePoste() != null ? ins.getDatePrisePoste().toString() : "", bg);
                addPdfCell(table, safe(ins.getTelephone()), bg);
                addPdfCell(table, safe(ins.getEmail()), bg);
                pair = !pair;
            }

            document.add(table);
            document.add(new Paragraph("Total : " + insertions.size() + " insertion(s)")
                    .setFontSize(9).setItalic().setMarginTop(10));

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF insertions : " + e.getMessage(), e);
        } finally {
            if (document != null) try { document.close(); } catch (Exception ignored) {}
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILITAIRES PRIVÉS
    // ══════════════════════════════════════════════════════════════════

    private String safe(String val) {
        return val != null ? val : "";
    }

    private CellStyle buildTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle buildHeaderStyle(Workbook wb, IndexedColors bgColor) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(bgColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle buildDataStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void addPdfCell(Table table, String value, DeviceRgb background) {
        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(value).setFontSize(8));
        if (background != null) cell.setBackgroundColor(background);
        table.addCell(cell);
    }
}
