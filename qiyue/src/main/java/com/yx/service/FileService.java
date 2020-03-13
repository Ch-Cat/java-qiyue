package com.yx.service;

import java.util.List;

import com.yx.entity.FileInfo;

public interface FileService {
	public List<FileInfo> queryAll();
	public void insertFile(FileInfo fileInfo);
	public FileInfo queryOne(String filename);
}
