package com.codegym.springboot_product_management.service;

import java.util.Optional;

public interface IGenerateService<E> {
    Iterable<E> findAll();

    Optional<E> findById(Long id);

    void save(E e);

    void remove(Long id);
}
