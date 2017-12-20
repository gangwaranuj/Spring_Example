package com.workmarket.domains.work.dao;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.WorkUploadColumnCategory;

public interface WorkUploadColumnCategoryDAO extends DAOInterface<WorkUploadColumnCategory> {

	public abstract List<WorkUploadColumnCategory> findAllColumnCategories();

}