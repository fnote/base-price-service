package com.sysco.rps.validators.dto;

@FunctionalInterface
public interface DataValidator<T, R, E extends Exception> {
  R validate(T dto) throws E;
}
