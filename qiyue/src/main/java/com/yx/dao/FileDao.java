package com.yx.dao;

import java.util.List;

import com.yx.entity.FileInfo;

public interface FileDao {
	public List<FileInfo> queryAll();
	public void insertOne(FileInfo fileinfo);
	public FileInfo queryOne(String filename);
}
