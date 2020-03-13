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
		//ǩ��У��
		HttpSession session = request.getSession();
		byte[] SID = (byte[]) session.getAttribute("SID");
		String Signature = (String) session.getAttribute("Signature");
		System.out.println(SID);
		String pk = (String)session.getAttribute("publicKey");
		boolean verify = RSAUtil2.verify(SID, pk, Signature);
		if(!verify) {
			throw new RuntimeException("���Ϸ�����");
		}
		
		//ͨ���ļ�����ѯ�ļ���Ϣ
		FileService fs=new FileServiceImpl();
		FileInfo fileInfo = fs.queryOne(filename);
		
		//�������ļ�����
		File file=new File(fileInfo.getF_url()+File.separator+filename);
		//���ļ������룬��ֹ��������
		filename=this.getFilename(request,filename);
		//��fileת��Ϊbyte[],��fileתmultipartFile����תbyte
		InputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
        byte[] fileBytes = multipartFile.getBytes();
		
		//��Base64����������ŷ�ת�ֽ�����
		byte[] byte1 = RSAUtil2.base642Byte(fileInfo.getF_enve());
		
		//��ȡrsa˽Կ
		String f_rsaprivate = fileInfo.getF_rsaprivate();
		System.out.println(f_rsaprivate);
		//��˽Կ�������ŷ����,�õ�AES��Կ
		byte[] aeskey = RSAUtil2.decryptByPrivateKey(byte1, f_rsaprivate);
		//��Base64������AES��Կת����SecretKey����
		SecretKey secretKey = AESUtil.loadKeyAES(RSAUtil2.byte2Base64(aeskey));
		//���ļ�AES����
		byte[] decryptAES = AESUtil.decryptAES(fileBytes, secretKey);
		
		
		//������Ӧͷ
		HttpHeaders headers=new HttpHeaders();
		//֪ͨ����������صķ�ʽ���ļ�
		headers.setContentDispositionFormData("attachment", filename);
		//������������ʽ���ط����ļ�����
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		//ʹ��Stringmvc��ܵ�ResponseEntity�����װ������������
		return new ResponseEntity<byte[]>(decryptAES,headers,HttpStatus.OK);
	}
	
	public String getFilename(HttpServletRequest request,String filename)
			throws Exception{
		//IE��ͬ�汾User-Agent�г��ֵĹؼ���
		String[] IEBrowserKeyWords= {"MSIE","Trident","Edge"};
		//��ȡ����ͷ������Ϣ
		String userAgent=request.getHeader("User-Agent");
		for (String keyWord : IEBrowserKeyWords) {
			if(userAgent.contains(keyWord)) {
				//IE�ں��������ͳһΪUTF-8������ʾ
				return URLEncoder.encode(filename,"UTF-8");
			}
		}
		//��������������ͳһΪiso-8859-1����
		return new String(filename.getBytes("UTF-8"),"ISO-8859-1");	
	}
}
