package com.adent.publicsite.api;

import com.adent.publicsite.service.PublicSiteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/sites")
public class PublicSiteController {
    private final PublicSiteService service;
    public PublicSiteController(PublicSiteService service){this.service=service;}

    @GetMapping("/{slug}")
    public PublicSiteService.PublicBundle get(@PathVariable String slug){ return service.publicBundle(slug); }

    @PostMapping("/{slug}/appointment-requests")
    public PublicSiteService.RequestResult request(@PathVariable String slug, @RequestBody PublicSiteService.AppointmentRequestInput input){
        return service.createAppointmentRequest(slug,input);
    }
}
