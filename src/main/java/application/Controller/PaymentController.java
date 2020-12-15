package application.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import application.entity.Account;
import application.entity.Log;
import application.entity.User;
import application.repository.AccountRepository;
import application.repository.LogRepository;
import application.repository.UserRepository;
import application.validator.TransactionValidator;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    private TransactionValidator validator;

    @RequestMapping(method = RequestMethod.GET)
    public String show(){
        return "payment";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String process(HttpServletRequest request) {
        Account account = accountRepository.getOne(Long.parseLong(request.getParameter("id")));
        Double amount = Double.parseDouble(request.getParameter("amount"));
        String type = request.getParameter("bill");
        validator = new TransactionValidator();
        if(validator.validate(account,amount)) {
            account.setBalance(account.getBalance() - amount);
            accountRepository.save(account);
        }
        else
            return "redirect:/payment?error=true";


        Log log = new Log();
        log.setOperation("Utilities payment: Account ID: " + account.getId() + " Amount: " + amount + " Type: " + type);
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));

        String username = request.getRemoteUser();

        User user = userRepository.findByUsername(username);
        log.setUser(user);

        logRepository.save(log);

        return "redirect:/index";
    }
}
