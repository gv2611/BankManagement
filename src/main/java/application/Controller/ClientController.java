package application.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import application.entity.Client;
import application.entity.Log;
import application.entity.User;
import application.repository.AccountRepository;
import application.repository.ClientRepository;
import application.repository.EmployeeRepository;
import application.repository.LogRepository;
import application.repository.UserRepository;
import application.validator.ClientValidator;

import javax.servlet.http.HttpServletRequest;

import java.security.cert.CertPathValidator;
import java.sql.Timestamp;
import java.util.List;

@Controller
@RequestMapping("/clientOp")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    private CertPathValidator validator;

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newClient() {
        return "clientOp/new";
    }

    @RequestMapping(value = "new",method = RequestMethod.POST)
    public String addClient(HttpServletRequest request) {
        Client client = new Client();
        client.setName(request.getParameter("name"));
        client.setAddress(request.getParameter("address"));
        client.setEmail(request.getParameter("email"));
        client.setCnp(request.getParameter("CNP"));
        ClientValidator validator = new ClientValidator();
        if(validator.validate(client))
            clientRepository.save(client);
        else
            return "redirect:/clientOp/new?error=true";

        Log log = new Log();
        log.setOperation("Client added. ID: " + client.getId() + "; Name: " + client.getName());
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));

        String username = request.getRemoteUser();

        User user = userRepository.findByUsername(username);
        log.setUser(user);

        logRepository.save(log);
        return "redirect:/index";
    }

    @RequestMapping(value = "/{id}/edit",method = RequestMethod.GET)
    public String update(@PathVariable long id, Model model) {
        Client client = clientRepository.findById(id).get();
        model.addAttribute("client",client);
        return "clientOp/edit";
    }
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String updateClient(HttpServletRequest request) {
        Client client = clientRepository.findById(Long.parseLong(request.getParameter("id"))).get();
        client.setName(request.getParameter("name"));
        client.setAddress(request.getParameter("address"));
        client.setEmail(request.getParameter("email"));
        client.setCnp(request.getParameter("CNP"));
        ClientValidator validator = new ClientValidator();
        if(validator.validate(client))
            clientRepository.save(client);
        else
            return "redirect:/clientOp/new?error=true";
        clientRepository.save(client);

        Log log = new Log();

        log.setOperation("Client updated. ID: " + client.getId() + "; Name: " + client.getName());
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));

        String username = request.getRemoteUser();

        User user = userRepository.findByUsername(username);
        log.setUser(user);
        logRepository.save(log);
        return "redirect:/index";
    }


    @RequestMapping(value = "/{id}/view",method = RequestMethod.GET)
    public String view(@PathVariable long id, Model model) {
        Client client = clientRepository.findById(id).get();
        model.addAttribute("client",client);
        return "clientOp/view";
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public String displaySearch(){
        return "clientOp/search";
    }
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public String search(HttpServletRequest request) {
        Client client = clientRepository.findById(Long.parseLong(request.getParameter("id"))).get();
        String option = request.getParameter("option");
        if(option.equals("VIEW")) {
            return "redirect:/clientOp/" + client.getId() + "/view";
        }
        if(option.equals("EDIT")) {
            return "redirect:/clientOp/" + client.getId() + "/edit";
        }
        return "redirect:/clientOp/search";
    }
}
