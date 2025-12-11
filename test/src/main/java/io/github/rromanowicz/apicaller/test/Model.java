package io.github.rromanowicz.apicaller.test;

import java.util.List;

public interface Model {

  record Dummy(String name, Integer count, boolean visible, List<Item> items) {

  }

  record Item(Long id, Float value) {

  }

  record User(Integer id, String username, String email, String password) {

  }


}
