package com.rromanowicz.apicaller.test;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class RestClientController {

  private final TestService testService;

  @GetMapping("/test")
  public List<Model.User> getTest() {
    return testService.restClientGet();
  }

  @GetMapping("/test/{id}")
  public Model.User testByIdWithParam(@PathVariable Integer id, @RequestParam String param) {
    return testService.restClientGet(id);
  }

  @PostMapping("/test")
  public Model.User postTest(@RequestBody Model.User user) {
    return testService.restClientPost(user);
  }

}
