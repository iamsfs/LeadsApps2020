package com.ics.creditcardreader.helper.pdf;

import android.content.Context;

import com.ics.creditcardreader.ApplicationClass;
import com.ics.creditcardreader.R;
import com.ics.creditcardreader.utility.Utility;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.parse.ParseObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreatePDF {
    public static final String DEST = "ACH_PDF.pdf";

    char unchecked = '\u2611';
    private Context mContext;

    private ParseObject mCCRA;
    private ParseObject mBusinessInfo;
    private ParseObject mAccountInfo, pricingObject;
    //mCCRA
    private String name, lastName, currentDate;
    //Business info
    private String businessType, zipCode, city, state, businessName;
    private String bankName, routingNumber, accountNumber;
    private double ccrPrice;
    private Image mSignatureImage;

    public CreatePDF(Context context, ParseObject mCCRA, ParseObject mBusinessInfo, ParseObject mAccountInfo, ParseObject pricingObject) {
        mContext = context;
        this.mCCRA = mCCRA;
        this.mBusinessInfo = mBusinessInfo;
        this.mAccountInfo = mAccountInfo;
        this.pricingObject = pricingObject;

        getValueFromCCRA();
    }

    public void getValueFromCCRA() {

        name = mCCRA.getString("Name");
        lastName = mCCRA.getString("LastName");
        businessType = mBusinessInfo.getString("businessType");
        zipCode = mBusinessInfo.getString("zipCode");
        city = mBusinessInfo.getString("city");
        state = mBusinessInfo.getString("state");
        businessName = mBusinessInfo.getString("businessName");
        bankName = mAccountInfo.getString("bankName");
        routingNumber = mAccountInfo.getString("routingNumber");
        accountNumber = mAccountInfo.getString("accountNumber");
        ccrPrice = pricingObject.getDouble("ccr_price");
        currentDate = Utility.getCurrentDate();


    }

    public File createPdf(byte[] signatureImageBytes) {
        try {
            this.mSignatureImage = Image.getInstance(signatureImageBytes);
        } catch (IOException | BadElementException e) {
            ApplicationClass.toast("Signature Image" + e.getMessage());
            e.printStackTrace();
        }

        File pdf = Utility.getCacheFile(DEST);
        if (pdf == null) {
            ApplicationClass.toast(" Unable to create PDF.");
            return null;
        }
        return createPDF(pdf);
    }

    private File createPDF(File dest) {
        try {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();
            addPageTitle(document);
            document.close();

        } catch (Exception e) {
            ApplicationClass.toast("PDF Error Image" + e.getMessage());
            e.printStackTrace();
        }

        return dest;
    }


    private void addPageTitle(Document document) throws DocumentException {

        //Form Name
        Chunk chunk = new Chunk(mContext.getString(R.string.nameOfForm));
        chunk.setFont(IText5Constants.catFont);

        Paragraph formName = new Paragraph(chunk);
        formName.setAlignment(Paragraph.ALIGN_CENTER);
        formName.setSpacingAfter(20);
        document.add(formName);

        //Self Declaration

        Phrase selfDeclarationPhrase = new Phrase();
        selfDeclarationPhrase.add(IText5Constants.getNormalChunk(" I, ", false));
        selfDeclarationPhrase.add(IText5Constants.getBoldChunkWithWordSpacing("      " + name + " " + lastName + "     ", true));
        selfDeclarationPhrase.add(IText5Constants.getNormalChunk(" give Merchant Account Solutions permission to debit my $ ", false));
        selfDeclarationPhrase.add(IText5Constants.getBoldChunkWithWordSpacing("      " + ccrPrice + "        ", true));
        selfDeclarationPhrase.add(IText5Constants.getNormalChunk("  for the product(s) listed below: ", false));

        Paragraph selfDeclaration = new Paragraph(selfDeclarationPhrase);

        selfDeclaration.setAlignment(Paragraph.ALIGN_LEFT);
        selfDeclaration.setSpacingAfter(IText5Constants.AFTER_SPACE);
        document.add(selfDeclaration);


        quantityTable(document);


        //document.newPage();

    }


    private void quantityTable(Document document) throws DocumentException {
        TableCellProps[][] quantityValues = new TableCellProps[][]{
                {TableCellProps.createBold("QTY", false, false), TableCellProps.createBold("Product", false, false), TableCellProps.createBold("Unit Price", false, false), TableCellProps.createBold("Ext Price", false, false)},
                {TableCellProps.createNormal("1", true, false), TableCellProps.createNormal("Bluetooth card reader", true, false), TableCellProps.createNormal("$" + ccrPrice, true, false), TableCellProps.createNormal("$" + ccrPrice, true, false)},

                {TableCellProps.createEmptyUnderLine(), TableCellProps.createEmptyUnderLine(), TableCellProps.createEmptyUnderLine(), TableCellProps.createEmptyUnderLine()},
                {TableCellProps.createEmptyUnderLine(), TableCellProps.createEmptyUnderLine(), TableCellProps.createEmptyUnderLine(), TableCellProps.createEmptyUnderLine()},


                {TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createBold("Sales Tax:", false, false), TableCellProps.createEmptyUnderLine()},
                {TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createBold("Total:", false, false), TableCellProps.createNormal("$" + ccrPrice, true, false)},

                {TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty()},
                {TableCellProps.createEmptyUnderLine(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty()},

        };
        float[] columnRatio = new float[]{0.1f, 0.5f, 0.1f, 3, 0.1f, 1.5f, 0.1f, 1.5f, 0.1f};
        document.add(IText5Constants.createTable(columnRatio, quantityValues));
        bankStatement(document);

    }


    private void bankStatement(Document document) throws DocumentException {
        TableCellProps[][] bankStatementValue = new TableCellProps[][]{
                {TableCellProps.createBold(mContext.getString(R.string.bankInfo), false, true).setSpanCount(14), null, null, null, null, null, null},

                {TableCellProps.createBold("Name on Bank Statement:", false, false)
                        .setSpanCount(4).setHorizontalAlignment(Element.ALIGN_LEFT)
                        , TableCellProps.createNormal(name + " " + lastName, true, false).setSpanCount(10), null, null, null, null, null},


                {TableCellProps.createBold("Bank Name:", false, false)
                        .setSpanCount(4).setHorizontalAlignment(Element.ALIGN_LEFT)
                        , TableCellProps.createNormal(bankName, true, false).setSpanCount(10), null, null, null, null, null},

                {TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty()},

                {TableCellProps.createBold("(Check one):", false, false).setHorizontalAlignment(Element.ALIGN_LEFT), TableCellProps.createBold("Checking", false, false), new TableCellProps().setChecked(true), TableCellProps.createBold("Savings", false, false), TableCellProps.createBold(" ( )", false, false), TableCellProps.createEmpty(), TableCellProps.createEmpty()},

                {TableCellProps.createBold("Routing #:", false, false)
                        .setHorizontalAlignment(Element.ALIGN_LEFT)
                        , TableCellProps.createNormal(routingNumber, true, false).setSpanCount(5), TableCellProps.createBold("ACC #:", false, false), TableCellProps.createNormal(accountNumber, true, false).setSpanCount(3), null, null, null},

                {TableCellProps.createBold("Branch City:", false, false)
                        .setHorizontalAlignment(Element.ALIGN_LEFT)
                        , TableCellProps.createNormal(city, true, false).setSpanCount(3), TableCellProps.createBold("State:", false, false), TableCellProps.createNormal(state, true, false)
                        , TableCellProps.createBold("Zip:", false, false), TableCellProps.createNormal(zipCode, true, false), null},

                {TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty()},
                {TableCellProps.createEmptyUnderLine(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty()},

        };
        float[] columnRatio = new float[]{0.1f, 3, 0.1f, 2, 0.1f, 2, 0.1f, 2, 0.1f, 2, 0.1f, 2, 0.1f, 2, 0.1f};
        document.add(IText5Constants.createTable(columnRatio, bankStatementValue));

        signingPara(document);
    }

    private void signingPara(Document document) throws DocumentException {
        Chunk chunk = new Chunk(mContext.getString(R.string.signingPara));
        chunk.setFont(IText5Constants.smallBold);
        Paragraph signingInfo = new Paragraph(chunk);
        signingInfo.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        signingInfo.setSpacingAfter(6);
        document.add(signingInfo);


        document.add(signatureTable());
        document.add(formInfoPara());
        document.add(forOfficeUserTable());
    }

    private PdfPTable signatureTable() {

        TableCellProps[][] signatureValue = new TableCellProps[][]{
                {TableCellProps.createBold("Signature:", false, false).setHorizontalAlignment(Element.ALIGN_LEFT), TableCellProps.createNormal("", true, false).setImage(mSignatureImage), TableCellProps.createBold("Date:", false, false), TableCellProps.createNormal(currentDate, true, false)},
                {TableCellProps.createBold("DBA:", false, false).setHorizontalAlignment(Element.ALIGN_LEFT), TableCellProps.createBold("", true, false).setSpanCount(5), null, null},
                {TableCellProps.createBold("Legal Business Name:", false, false).setHorizontalAlignment(Element.ALIGN_LEFT), TableCellProps.createNormal(businessName, true, false).setSpanCount(5), null, null},
                {TableCellProps.createBold("Account Executive:", false, false).setHorizontalAlignment(Element.ALIGN_LEFT), TableCellProps.createNormal(name + " " + lastName, true, false).setSpanCount(5), null, null},
                {TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty()},};
        float[] columnRatio = new float[]{0.1f, 2, 0.1f, 3, 0.1f, 1, 0.1f, 2, 0.1f};
        return IText5Constants.createTable(columnRatio, signatureValue, false);

    }


    private Paragraph formInfoPara() {
        Phrase formInfoPhrase = new Phrase();
        formInfoPhrase.add(IText5Constants.getBoldChunk(mContext.getString(R.string.faxNumber), true));

        Paragraph formInfo = new Paragraph(formInfoPhrase);
        formInfo.setAlignment(Paragraph.ALIGN_CENTER);
        formInfo.setSpacingAfter(IText5Constants.AFTER_MIN_SPACE);
        return formInfo;

    }


    private PdfPTable forOfficeUserTable() {
        TableCellProps[][] officeUseValues = new TableCellProps[][]{

                {TableCellProps.createBold("For office use only", false, true).setSpanCount(8), null, null, null},

                {TableCellProps.createBold("MID:", false, false), TableCellProps.createEmptyUnderLine(), TableCellProps.createBold("Sales Rep:", false, false), TableCellProps.createEmptyUnderLine()},
                {TableCellProps.createBold("Charge:", false, false), TableCellProps.createEmptyUnderLine(), TableCellProps.createBold("Date:", false, false), TableCellProps.createEmptyUnderLine()},

                {TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty()},
                {TableCellProps.createEmptyUnderLine(), TableCellProps.createEmpty(), TableCellProps.createEmpty(), TableCellProps.createEmpty()},

        };
        float[] columnRatio = new float[]{0.1f, 1, 0.1f, 2, 0.1f, 1, 0.1f, 2, 0.1f};
        return IText5Constants.createTable(columnRatio, officeUseValues);
    }
}
