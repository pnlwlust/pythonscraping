package com.codeharmony;

import org.jsoup.*;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class Scrapper {

    public static void main(String[] args) throws Exception {
        String url = "https://www.something.com/services/something";
        print("Fetching %s...", url);
        Document doc = Jsoup.parse(new File("/home/projects/scrapData/something.html"), "utf-8");
        Elements tr = doc.select("tr");
        ArrayList<String> arrayList = new ArrayList<String>();
        int count = 0;
        print("\ntr: (%d)", tr.size());
        System.out.println("#################start###################################");

        for (Element tr1 : tr) {
            count++;
            Elements aEach = tr1.select("td").tagName("a");

            String onClickString = tr1.getElementsByAttribute("onclick").attr("onclick");
            String[] parts = onClickString.split("\"");
            if (count != 0 && count != 1) {
                arrayList.add(parts[1]);
            }
        }
//                        System.out.println(arrayList);
        System.out.println(">>>>" + arrayList.get(28379));
        String jptId = "bd871556-1a82-447c-943a-8c23b6b4e583";
//        String nextUrl = "https://www.cea.gov.sg//Custom/CEA/PublicRegister/Page/PublicRegisterDetail.aspx?UserId="+jptId;
        String nextUrl = "http://www.something.sg/detail.aspx?UserId=bd871556-1a82-447c-943a-8c23b6b4e583";
//        String nextUrl = "http://www.facebook.com";
        print("Fetching nextUrl %s...", nextUrl);
        Document nextDoc = Jsoup.connect(nextUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36")
                .referrer("http://www.google.com").get();
//                System.out.println(nextDoc.body().toString());
                Elements nameSpan=nextDoc.select("#FtPublicRegisterDetail_LblName");
                Elements regnoSpan=nextDoc.select("#FtPublicRegisterDetail_LblRegNo");
                Elements estAgentnameSpan=nextDoc.select("#FtPublicRegisterDetail_LblEstAgentName");
                Elements base64Image=nextDoc.select("#FtPublicRegisterDetail_ImgProfile");
                String base64Complete=base64Image.attr("src");
                String base64data =base64Complete.split(",")[0];
                String base64imageData =base64Complete.split(",")[1];
                String contentEncoding=base64data.split(";")[1];
                String contentType=base64data.split(";")[0].split(":")[1];
        System.out.println("nameSpan>> "+nameSpan.text());
        System.out.println("regNoSpan>> "+regnoSpan.text());
        System.out.println("estAgentnameSpan>> "+estAgentnameSpan.text());
        System.out.println("base64Image Element>> "+base64Image);
        System.out.println("base64Image content>> "+base64Complete);
        System.out.println("base64Image image data only>> "+base64imageData);

        System.out.println("base64Image data>> "+base64data);
        System.out.println("base64Image contentEncoding>>"+contentEncoding);
        System.out.println("base64Image contentType>>"+contentType);
        System.out.println("#################SALESPERSON DETAILS###################################");
        BufferedImage newImg;
        newImg = decodeToImage(base64imageData);
        ImageIO.write(newImg, "jpg", new File("CopyOfTestImage1.jpg"));





       System.out.println("#################end###################################");


    }






    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }
    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
