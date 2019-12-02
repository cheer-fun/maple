package com.maple.rpc.service;

import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;


/**
 * @author lfy
 * @date 2019/11/29 16:36
 **/
public interface AnnoScannerProcessor {

	void process(CachingMetadataReaderFactory factory, Resource[] resources) throws Exception;
}
