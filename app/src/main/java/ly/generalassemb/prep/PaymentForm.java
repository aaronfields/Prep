package ly.generalassemb.prep;

/**
 * Created by aaronfields on 8/7/16.
 */
public interface PaymentForm {

    public String getCardNumber();
    public String getCvc();
    public Integer getExpMonth();
    public Integer getExpYear();
    public String getCurrency();
}
