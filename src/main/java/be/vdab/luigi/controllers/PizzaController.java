package be.vdab.luigi.controllers;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.KoersClientException;
import be.vdab.luigi.forms.VanTotPrijsForm;
import be.vdab.luigi.services.EuroService;
import be.vdab.luigi.services.PizzaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping("pizzas")
class PizzaController {

    private final EuroService euroService;
    private final PizzaService pizzaService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    PizzaController(EuroService euroService, PizzaService pizzaService) {
        this.euroService = euroService;
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public ModelAndView findAll() {
        return new ModelAndView("pizzas", "allePizzas", pizzaService.findAll());
    }

//    private Optional<Pizza> findByIdHelper(long id) {
//        return Arrays.stream(allePizzas).filter(pizza -> pizza.getId() == id).findFirst();
//    }

    @GetMapping("{id}")
    public ModelAndView findById(@PathVariable long id) {
        var modelAndView = new ModelAndView("pizza");
        pizzaService.findById(id).ifPresent(pizza -> {
            modelAndView.addObject("pizza", pizza);
            try {
                modelAndView.addObject("inDollar", euroService.naarDollar(pizza.getPrijs()));
            } catch (KoersClientException ex) {
                logger.error("Kan dollar koers niet lezen", ex);
            }
        });
        return modelAndView;
    }

//
//        findByIdHelper(id).ifPresent(gevondenPizza -> {modelAndView.addObject(gevondenPizza);
//        try {
//            modelAndView.addObject("inDollar", euroService.naarDollar(gevondenPizza.getPrijs()));
//        } catch (KoersClientException ex) {
//            logger.error("kan de dollar koers niet lezen", ex);
//        }
//        });
//        return modelAndView;

//    private Stream<BigDecimal> findPrijzenHelper() {
//        return Arrays.stream(allePizzas).map(Pizza::getPrijs).distinct().sorted();
//    }

    @GetMapping("prijzen")
    public ModelAndView findPrijzen() {
        return new ModelAndView("pizzasPerPrijs", "prijzen", pizzaService.findUniekePrijzen());
    }

    @GetMapping("prijzen/{prijs}")
    public ModelAndView findByPrijs(@PathVariable BigDecimal prijs) {
        return new ModelAndView("pizzasPerPrijs", "pizzas", pizzaService.findByPrijs(prijs))
                .addObject("prijzen", pizzaService.findUniekePrijzen());
    }

    @GetMapping("aantalpizzasperprijs")
    public ModelAndView findAantalPizzasPerPrijs() {
        return new ModelAndView(
                "aantalpizzasperprijs", "aantalPizzasPerPrijs", pizzaService.findAantalPizzasPerPrijs());
    }

    @GetMapping("vantotprijs/form")
    public ModelAndView vanTotPrijsForm() {
        return new ModelAndView("vantotprijs").addObject(
                new VanTotPrijsForm(null, null));
    }

    @GetMapping("vantotprijs")
    public ModelAndView findPrijsBetween(@Valid VanTotPrijsForm Form, Errors errors) {
        var modelAndView = new ModelAndView("vantotprijs");
        if(errors.hasErrors()) {
            return modelAndView;
        }
        return modelAndView.addObject("pizzas", pizzaService.findByPrijsBetween(Form.van(), Form.tot()));
    }

    @GetMapping("toevoegen/form")
    public ModelAndView toevoegenForm() {
        return new ModelAndView("toevoegen").addObject(new Pizza(0, "", null, false));
    }

    @PostMapping
    public String toevoegen(@Valid Pizza pizza, Errors errors, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "toevoegen";
        }
        redirect.addAttribute("idNieuwePizza", pizzaService.create(pizza));
        return "redirect:/pizzas";
    }




//        @GetMapping("prijzen")
//        public ModelAndView prijzen () {
//            return new ModelAndView("pizzasperprijs", "prijzen", findPrijzenHelper().iterator());
//        }
//
//        private Stream<Pizza> findByPrijsHelper (BigDecimal prijs){
//            return Arrays.stream(allePizzas).filter(pizza -> pizza.getPrijs().compareTo(prijs) == 0);
//        }
//
//        @GetMapping("prijzen/{prijs}")
//        public ModelAndView findByPrijs (@PathVariable BigDecimal prijs){
//            // optie 3
//            return new ModelAndView("pizzasperprijs", "pizzas", findByPrijsHelper(prijs).iterator())
//                    .addObject("prijzen", findPrijzenHelper().iterator());

            // optie 2
//        var modelAndView = new ModelAndView("pizzasperprijs", "pizzas", findByPrijsHelper(prijs).iterator());
//        modelAndView.addObject("prijzen", findPrijzenHelper().iterator());
//        return modelAndView;

            //optie 1
//        return new ModelAndView("pizzasperprijs", "pizzas", findByPrijsHelper(prijs).iterator());
//        }
    }