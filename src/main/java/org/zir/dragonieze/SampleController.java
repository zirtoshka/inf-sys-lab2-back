package org.zir.dragonieze;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class SampleController {


    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/protected-openam")
    public String cookieProtected(Model model, @AuthenticationPrincipal String principal) {
        System.out.println("dksdlkdskdkwdpdl");
        model.addAttribute("userName", principal);
        model.addAttribute("method", "OpenAM Cookie");
        return "protected";
    }




}
