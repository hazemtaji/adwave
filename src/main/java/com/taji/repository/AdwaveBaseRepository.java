package com.taji.repository;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface AdwaveBaseRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>  {

}
