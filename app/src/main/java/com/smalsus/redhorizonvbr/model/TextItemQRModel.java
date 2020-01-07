package com.smalsus.redhorizonvbr.model;
import java.io.Serializable;

public class TextItemQRModel implements Serializable {

    private String textDescription ;
    private int qrCodeType;
    private String productItemID;

    public TextItemQRModel(String textDescription, int type,String productItemViewModel) {
        this.textDescription = textDescription;
        this.qrCodeType = type;
        this.productItemID=productItemViewModel;
    }

    public String getTextDescription() {
        return textDescription;
    }

    public int getType() {
        return qrCodeType;
    }

    public String getProductItemViewModel() {
        return productItemID;
    }
}
