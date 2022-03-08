package be.vdab.luigi.services;

import be.vdab.luigi.restclients.FixerKoersClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EuroService {

//    private final FixerKoersClient koersClient = new FixerKoersClient();   Wordt vervangen OWV de Testclass

    private final FixerKoersClient koersClient;
    public EuroService(FixerKoersClient koersClient) {      // dankwij de constructor, kan de testclasse eigen data meegeven.
        this.koersClient = koersClient;
    }

    public BigDecimal naarDollar(BigDecimal euro) {
        return euro.multiply(koersClient.getDollarKoers()).setScale(2, RoundingMode.HALF_UP);
    }
}
