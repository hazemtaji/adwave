package com.taji.repository;

import java.util.List;

import com.taji.model.CreativeEntry;

public interface CreativeEntryRepository extends AdwaveBaseRepository<CreativeEntry, Integer> {
	public List<CreativeEntry> findByStatus(int status);
}
