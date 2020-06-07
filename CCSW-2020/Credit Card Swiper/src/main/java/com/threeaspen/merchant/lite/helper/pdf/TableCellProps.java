package com.threeaspen.merchant.lite.helper.pdf;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;

public class TableCellProps {

    private String content;
    private boolean bottomBorderLine;
    private boolean underLine;
    private Font mFont = IText5Constants.normal;
    private int spanCount = -1;
    private boolean isChecked;
    private int horizontalAlignment = Element.ALIGN_CENTER;
    private Image mImage;

    public TableCellProps() {
    }

    public TableCellProps setChecked(boolean checked) {
        isChecked = checked;
        return this;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public TableCellProps(String content) {
        this.content = content;
    }
    //IMAGE
    public boolean hasImage() {
        return getImage() != null;
    }

    public Image getImage() {
        return mImage;
    }

    public TableCellProps setImage(Image image) {
        mImage = image;
        return this;
    }

    public String getContent() {
        return content;
    }

    public boolean isBottomBorderLine() {
        return bottomBorderLine;
    }

    public boolean isUnderLine() {
        return underLine;
    }

    public Font getFont() {
        return mFont;
    }

    public int getSpanCount() {
        return spanCount;
    }

    public boolean isEligibleForSpan() {
        return spanCount != -1;
    }

    public void setBottomBorderLine(boolean bottomBorderLine) {
        this.bottomBorderLine = bottomBorderLine;
    }

    public void setUnderLine(boolean underLine) {
        this.underLine = underLine;
    }

    public void setFont(Font font) {
        mFont = font;
    }

    public TableCellProps setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        return this;
    }

    public TableCellProps setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public static TableCellProps createNormal(String content, boolean hasBorder, boolean hasTextUnderLine) {
        TableCellProps tableCellProps = new TableCellProps(content);
        tableCellProps.setBottomBorderLine(hasBorder);
        tableCellProps.setUnderLine(hasTextUnderLine);
        tableCellProps.setFont(IText5Constants.normal);
        return tableCellProps;
    }


    public static TableCellProps createBold(String content, boolean hasBorder, boolean hasTextUnderLine) {
        TableCellProps tableCellProps = new TableCellProps(content);
        tableCellProps.setBottomBorderLine(hasBorder);
        tableCellProps.setUnderLine(hasTextUnderLine);
        tableCellProps.setFont(IText5Constants.smallBold);
        return tableCellProps;
    }

    public static TableCellProps createEmptyUnderLine() {
        TableCellProps tableCellProps = new TableCellProps(" ");
        tableCellProps.setBottomBorderLine(true);
        return tableCellProps;
    }

    public static TableCellProps createEmpty() {
        TableCellProps tableCellProps = new TableCellProps(null);
        tableCellProps.setBottomBorderLine(false);
        return tableCellProps;
    }

}

