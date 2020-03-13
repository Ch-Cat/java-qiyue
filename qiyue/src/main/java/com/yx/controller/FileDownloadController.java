package com.yx.controller;


import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;

import java.net.URLEncoder;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.yx.entity.FileInfo;
import com.yx.service.FileService;
import com.yx.service.FileServiceImpl;
import com.yx.util.AESUtil;

import com.yx.util.RSAUtil2;

@Controller
public class FileDownloadController {
	
	
	@RequestMapping(value="/download")
	public ResponseEntity<byte[]> fileDownload(HttpServletRequest request,String filename)
			throws Exception{
		//签名校验
		HttpSession session = request.getSession();
		byte[] SID = (byte[]) session.getAttribute("SID");
		String Signature = (String) session.getAttribute("Signature");
		System.out.println(SID);
		String pk = (String)session.getAttribute("publicKey");
		boolean verify = RSAUtil2.verify(SID, pk, Signature);
		if(!verify) {
			throw new RuntimeException("不合法访问");
		}
		
		//通过文件名查询文件信息
		FileService fs=new FileServiceImpl();
		FileInfo fileInfo = fs.queryOne(filename);
		
		//创建该文件对象
		File file=new File(fileInfo.getF_url()+File.separator+filename);
		//对文件名编码，防止中文乱码
		filename=this.getFilename(request,filename);
		//将file转换为byte[],先file转multipartFile，再转byte
		InputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
        byte[] fileBytes = multipartFile.getBytes();
		
		//将Base64编码的数字信封转字节数组
		byte[] byte1 = RSAUtil2.base642Byte(fileInfo.getF_enve());
		
		//获取rsa私钥
		String f_rsaprivate = fileInfo.getF_rsaprivate();
		System.out.println(f_rsaprivate);
		//用私钥对数字信封解密,得到AES密钥
		byte[] aeskey = RSAUtil2.decryptByPrivateKey(byte1, f_rsaprivate);
		//将Base64编码后的AES秘钥转换成SecretKey对象
		SecretKey secretKey = AESUtil.loadKeyAES(RSAUtil2.byte2Base64(aeskey));
		//对文件AES解密
		byte[] decryptAES = AESUtil.decryptAES(fileBytes, secretKey);
		
		
		//设置响应头
		HttpHeaders headers=new HttpHeaders();
		//通知浏览器以下载的方式打开文件
		headers.setContentDispositionFormData("attachment", filename);
		//定义以流的形式下载返回文件数据
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		//使用Stringmvc框架的ResponseEntity对象封装返回下载数据
		return new ResponseEntity<byte[]>(decryptAES,headers,HttpStatus.OK);
	}
	
	public String getFilename(HttpServletRequest request,String filename)
			throws Exception{
		//IE不同版本User-Agent中出现的关键词
		String[] IEBrowserKeyWords= {"MSIE","Trident","Edge"};
		//获取请求头代理信息
		String userAgent=request.getHeader("User-Agent");
		for (String keyWord : IEBrowserKeyWords) {
			if(userAgent.contains(keyWord)) {
				//IE内核浏览器，统一为UTF-8编码显示
				return URLEncoder.encode(filename,"UTF-8");
			}
		}
		//火狐等其他浏览器统一为iso-8859-1编码
		return new String(filename.getBytes("UTF-8"),"ISO-8859-1");	
	}
}
