package com.ics.creditcardprocessing.helper.pdf;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RadioCheckField;

public abstract class IText5Constants {
    public static final Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD); // Set of font family alrady present with itextPdf library.
    public static final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    public static final Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL);
    public static final float AFTER_SPACE = 30;
    public static final float AFTER_MIN_SPACE = 10;
    public static final float CELL_PADDING = 4;


    //CHUNKS
    public static Chunk getNormalChunk(String string, boolean underLine) {
        Chunk chunk = new Chunk(string);
        chunk.setFont(IText5Constants.normal);


        if (underLine)
            chunk.setUnderline(2, -4);
        return chunk;
    }

    public static Chunk getBoldChunk(String string, boolean underLine) {
        Chunk chunk = new Chunk(string);
        chunk.setFont(IText5Constants.smallBold);

        if (underLine)
            chunk.setUnderline(2, -4);
        return chunk;
    }

    public static Chunk getBoldChunkWithWordSpacing(String string, boolean underLine) {
        Chunk chunk = getBoldChunk(string, underLine);
        chunk.setWordSpacing(16);
        return chunk;
    }

    //TABLE
    public static PdfPCell getCell(String columnName, boolean hasBorder) {
        Phrase phrase = new Phrase(getBoldChunk(columnName, hasBorder));
        return getCell(phrase, false);
    }

    public static PdfPCell getCell(String columnName, boolean hasBorder, Font font) {
        Phrase phrase = new Phrase(columnName, font);
        return getCell(phrase, hasBorder);
    }


    public static PdfPCell getCell(Phrase phrase, boolean hasBorder) {
        PdfPCell qtyH = new PdfPCell(phrase);
        qtyH.setPadding(CELL_PADDING);
        qtyH.setBorder(hasBorder ? Rectangle.BOTTOM : Rectangle.NO_BORDER);

        return qtyH;
    }

    private static PdfPCell getImageCell(Image image) {
        image.scaleAbsolute(100,20);
        PdfPCell cell = new PdfPCell(image);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
       // cell.setPadding(CELL_PADDING);
        return cell;
    }

    private static PdfPCell getCheckBoxCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setCellEvent(new CheckboxCellEvent("c", RadioCheckField.TYPE_CHECK));
        cell.setPadding(CELL_PADDING);
        return cell;
    }

    private static PdfPCell createEmptyCell() {
        PdfPCell qtyH = new PdfPCell(new Phrase(""));
        qtyH.setBorder(Rectangle.NO_BORDER);
        return qtyH;
    }

    public static PdfPTable createTable(float[] columnRatio, TableCellProps[][] props) {
        return createTable(columnRatio, props, true);
    }

    public static PdfPTable createTable(float[] columnRatio, TableCellProps[][] props, boolean tableHasBorder) {
        PdfPTable table = new PdfPTable(columnRatio);
        table.setTotalWidth(540);
        table.setLockedWidth(true);

        for (TableCellProps[] row : props) {

            int columnCount = 0;
            int spanCount = -1;
            int passedSpans = 1;
            for (int i = 0; i < columnRatio.length; i++) {

                if ((spanCount != -1 && passedSpans < spanCount)) {
                    passedSpans++;
                    continue;
                }

                if (i % 2 == 0) {
                    table.addCell(createEmptyCell());
                    continue;
                }
                TableCellProps tableCellProps = row[columnCount];
                spanCount = tableCellProps.getSpanCount();
                passedSpans = 1;
                PdfPCell cell;
                if (tableCellProps.hasImage()) {
                    cell = getImageCell(tableCellProps.getImage());
                } else if (tableCellProps.isChecked()) {
                    cell = getCheckBoxCell();
                } else if (tableCellProps.isUnderLine()) {
                    cell = getCell(tableCellProps.getContent(), tableCellProps.isUnderLine());
                } else {
                    cell = getCell(tableCellProps.getContent(), tableCellProps.isBottomBorderLine(), tableCellProps.getFont());
                }
                cell.setHorizontalAlignment(tableCellProps.getHorizontalAlignment());

                if (tableCellProps.isEligibleForSpan()) {
                    cell.setColspan(spanCount);
                }
                table.addCell(cell);
                columnCount++;
            }

        }
        if (tableHasBorder) {
            table.setTableEvent(new BorderEvent());
        }

        table.setSpacingAfter(IText5Constants.AFTER_SPACE);
        return table;
    }

    public static class BorderEvent implements PdfPTableEvent {
        @Override
        public void tableLayout(PdfPTable table, float[][] widths,
                                float[] heights, int headerRows, int rowStart,
                                PdfContentByte[] canvases) {
            float width[] = widths[0];
            float x1 = width[0];
            float x2 = width[width.length - 1];
            float y1 = heights[0];
            float y2 = heights[heights.length - 1];
            PdfContentByte cb = canvases[PdfPTable.LINECANVAS];
            cb.rectangle(x1, y1, x2 - x1, y2 - y1);
            cb.stroke();
            // cb.resetRGBColorStroke();
        }
    }

    static class CheckboxCellEvent implements PdfPCellEvent {
        // The name of the check box field
        protected String name;
        protected int i;

        // We create a cell event
        public CheckboxCellEvent(String name, int i) {
            this.name = name;
            this.i = i;
        }

        // We create and add the check box field
        public void cellLayout(PdfPCell cell, Rectangle position,
                               PdfContentByte[] canvases) {
            PdfWriter writer = canvases[0].getPdfWriter();
            // define the coordinates of the middle
            float x = (position.getLeft() + position.getRight()) / 2;
            float y = (position.getTop() + position.getBottom()) / 2;
            // define the position of a check box that measures 20 by 20
            Rectangle rect = new Rectangle(x - 10, y - 10, x + 10, y + 10);
            // define the check box
            RadioCheckField checkbox = new RadioCheckField(
                    writer, rect, name, "Yes");
            switch (i) {
                case RadioCheckField.TYPE_CHECK:
                    checkbox.setCheckType(RadioCheckField.TYPE_CHECK);
                    break;
                case RadioCheckField.TYPE_CIRCLE:
                    checkbox.setCheckType(RadioCheckField.TYPE_CIRCLE);
                    break;
                case RadioCheckField.TYPE_CROSS:
                    checkbox.setCheckType(RadioCheckField.TYPE_CROSS);
                    break;
                case RadioCheckField.TYPE_DIAMOND:
                    checkbox.setCheckType(RadioCheckField.TYPE_DIAMOND);
                    break;
                case RadioCheckField.TYPE_SQUARE:
                    checkbox.setCheckType(RadioCheckField.TYPE_SQUARE);
                    break;
                case RadioCheckField.TYPE_STAR:
                    checkbox.setCheckType(RadioCheckField.TYPE_STAR);
                    break;
            }
            checkbox.setChecked(true);
            // add the check box as a field
            try {
                writer.addAnnotation(checkbox.getCheckField());
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }
}
