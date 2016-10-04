package provapdm.jeansarlon.fidelize;


public class Wallet {

    Integer amount;
    Long company_id;
    String companyName;

    public Wallet(Long company_id, Integer amount, String companyName){
        this.company_id = company_id;
        this.companyName = companyName;
        this.amount = amount;
    }
}
