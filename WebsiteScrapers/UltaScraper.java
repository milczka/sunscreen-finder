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

    private String getIngredients(Document webpage){

        Element ingredients = webpage.select("div[id=product-default-ingredients]").get(0).select("div").first();
        String allIngredients = ingredients.text();

        return allIngredients;
    }

    private String getProductName(Document webpage){

        Element htmlTitle = webpage.select("meta[property=og:title]").first();
        String productName = htmlTitle.attr("content");

        return productName;
    }

    private String determineSSType(String ingredients){
        // returns: physical, chemical, or combo

        String ssType = "unknown";
        String ingredientsLower = ingredients.toLowerCase();
        boolean chemical = false, physical = false;

        String[] chemicalIngredients = {"avobenzone", "oxybenzone", "homosalate", "octinoxate", "octocrylene", "octisalate", "ecamsule"};
        String[] physicalIngredients = {"zinc oxide", "titanium dioxide"};

        for(String ingredient : chemicalIngredients){
            if(ingredientsLower.contains(ingredient)) chemical = true;
        }

        for(String ingredient : physicalIngredients){
            if(ingredientsLower.contains(ingredient)) physical = true;
        }

        if(physical && chemical) ssType = "combination";
        else if(physical) ssType = "physical";
        else if(chemical) ssType = "chemical";


        return ssType;
    }

    private Map<String, ArrayList<String> > getAllProductInfo(String originLink) throws IOException{

        Map<String, ArrayList<String> > productInfo = new HashMap<>();
        ArrayList<String> allLinks = getAllProductLinks(originLink);
        ArrayList<String> supplementInfo = new ArrayList<>();

        for(String link : allLinks){
            supplementInfo.clear();
            Document productPage = Jsoup.connect(link).get();
            String allIngredients = getIngredients(productPage);
            String productName = getProductName(productPage);
            System.out.println(productName);
            System.out.println(allIngredients);

            productInfo.put(productName, supplementInfo);
        }
        return productInfo;
    }

    private Map<String, ArrayList<String> > getOneProductInfo(String originLink) throws IOException{

        Map<String, ArrayList<String> > productInfo = new HashMap<>();
        ArrayList<String> supplementInfo = new ArrayList<>();

        Document productPage = Jsoup.connect(originLink).get();
        String allIngredients = getIngredients(productPage);
        String productName = getProductName(productPage);
        productInfo.put(productName, supplementInfo);

        return productInfo;
    }

    public static void main(String [] args) throws IOException{

        UltaScraper testScrape = new UltaScraper();

        testScrape.getAllProductInfo("http://ulta.com/skin-care-suncare-sunscreen?N=27ff");
    }
}
