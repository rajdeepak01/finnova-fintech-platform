
package com.finnova;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = {
    "http://localhost:4200",
    "https://frontend-wine-rho-87.vercel.app",
    "https://frontend-git-main-tpcs-projects-6590cff5.vercel.app"
})
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World! Welcome to Spring Boot.";
    }
}
