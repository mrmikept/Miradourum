package pt.uminho.di.aa.miradourum.dto;

public class PaymentDTO {

    private String cardNumber;
    private String cardName;
    private String ccv;

    // Construtores
    public PaymentDTO() {}

    public PaymentDTO(String cardNumber, String cardName, String ccv) {
        this.cardNumber = cardNumber;
        this.cardName = cardName;
        this.ccv = ccv;
    }

    // Getters e Setters
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getCcv() { return ccv; }
    public void setCcv(String ccv) { this.ccv = ccv; }
}
