import java.io.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class UltaScraper{

    private ArrayList<String> getAllProductLinks(String pageLink) throws IOException{

        ArrayList<String> allProductLinks = new ArrayList<>();

        Document website = Jsoup.connect(pageLink).get();
        Elements sunscreenLinks = website.select("p[class=prod-desc]");
        for(Element product : sunscreenLinks){
            product = product.select("a").first();
            String linkToProduct = "http://www.ulta.com" + product.attr("href");
            allProductLinks.add(linkToProduct);
        }
        return allProductLinks;
    }

    private Map<String, ArrayList<String> > getAllProductInfo(String originLink) throws IOException{

        Map<String, ArrayList<String> > productInfo = new HashMap<>();
        String productName = ""; 
        ArrayList<String> allLinks = getAllProductLinks(originLink);
        ArrayList<String> supplementInfo = new ArrayList<>();

        for(String link : allLinks){
            supplementInfo.clear();
            Document productPage = Jsoup.connect(link).get();
            Element ingredients = productPage.select("div[id=product-default-ingredients]").get(0).select("div").first();
            String allIngredients = ingredients.text();

            Element htmlTitle = productPage.select("meta[property=og:title]").first();
            productName = htmlTitle.attr("content");
            System.out.println(productName);
            System.out.println(allIngredients);

            productInfo.put(productName, supplementInfo);
        }
        return productInfo;
    }

    public static void main(String [] args) throws IOException{

        UltaScraper testScrape = new UltaScraper();

        testScrape.getAllProductInfo("http://ulta.com/skin-care-suncare-sunscreen?N=27ff");
    }
}
