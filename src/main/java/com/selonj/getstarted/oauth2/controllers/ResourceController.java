package com.selonj.getstarted.oauth2.controllers;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016-04-12.
 */
@Controller
public class ResourceController {
  @RequestMapping("/resource/ping")
  public @ResponseBody String ping(Principal user) {
    return "bearer " + user.getName();
  }
}
