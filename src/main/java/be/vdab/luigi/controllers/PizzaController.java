package be.vdab.luigi.controllers;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.KoersClientException;
import be.vdab.luigi.services.EuroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping("pizzas")
class PizzaController {

    private final EuroService euroService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Pizza[] allePizzas = {
        new Pizza(1, "Prosciutto", BigDecimal.valueOf(4), true),
        new Pizza(2, "Margheritta", BigDecimal.valueOf(5), false),
        new Pizza(3, "Calzone", BigDecimal.valueOf(4), false) };

    PizzaController(EuroService euroService) {
        this.euroService = euroService;
    }

    @GetMapping
    public ModelAndView findAll() {
        return new ModelAndView("pizzas", "allePizzas", allePizzas);
    }

    private Optional<Pizza> findByIdHelper(long id) {
        return Arrays.stream(allePizzas).filter(pizza->pizza.getId()==id).findFirst();
    }

    @GetMapping("{id}")
    public ModelAndView findById(@PathVariable long id) {
        var modelAndView = new ModelAndView("pizza");
        findByIdHelper(id).ifPresent(gevondenPizza -> {modelAndView.addObject(gevondenPizza);
        try {
            modelAndView.addObject("inDollar", euroService.naarDollar(gevondenPizza.getPrijs()));
        } catch (KoersClientException ex) {
            logger.error("kan de dollar koers niet lezen", ex);
        }
        });
        return modelAndView;
    }

    private Stream<BigDecimal> findPrijzenHelper() {
        return Arrays.stream(allePizzas).map(Pizza::getPrijs).distinct().sorted();
    }

    @GetMapping("prijzen") public ModelAndView prijzen() {
        return new ModelAndView("pizzasperprijs", "prijzen", findPrijzenHelper().iterator());
    }

    private Stream<Pizza> findByPrijsHelper(BigDecimal prijs) {
        return Arrays.stream(allePizzas).filter(pizza -> pizza.getPrijs().compareTo(prijs) == 0);
    }

    @GetMapping("prijzen/{prijs}")
    public ModelAndView findByPrijs(@PathVariable BigDecimal prijs) {
        // optie 3
        return new ModelAndView("pizzasperprijs","pizzas",findByPrijsHelper(prijs).iterator())
                .addObject("prijzen", findPrijzenHelper().iterator());

        // optie 2
//        var modelAndView = new ModelAndView("pizzasperprijs", "pizzas", findByPrijsHelper(prijs).iterator());
//        modelAndView.addObject("prijzen", findPrijzenHelper().iterator());
//        return modelAndView;

        //optie 1
//        return new ModelAndView("pizzasperprijs", "pizzas", findByPrijsHelper(prijs).iterator());
    }
}