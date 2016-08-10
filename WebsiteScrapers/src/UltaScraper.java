import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        boolean nextLink = false;
        String nextPage = "";

        Elements possibleNextPage = website.select("a[href*=/skin-care-suncare-sunscreen]");
        for(Element link : possibleNextPage){
            if( link.toString().indexOf("Next") != -1){
                nextPage = "http://ulta.com/" + link.attr("href");
                nextLink = true;
                break;
            }
        }

        while(nextLink){
            nextLink = false;

            Document subWebsite = Jsoup.connect(nextPage).get();
            Elements subSunscreenLinks = subWebsite.select("p[class=prod-desc]");
            for(Element product : subSunscreenLinks){
                product = product.select("a").first();
                String linkToProduct = "http://ulta.com" + product.attr("href");
                allProductLinks.add(linkToProduct);
            }

            Elements subPossibleNextPage = subWebsite.select("a[href*=/skin-care-suncare-sunscreen]");
            for(Element link : subPossibleNextPage){
                if( link.toString().indexOf("Next") != -1){
                    nextPage = "http://ulta.com/" + link.attr("href");
                    nextLink = true;
                    break;
                }
            }
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

    private String getProductBrand(Document webpage){

        Element htmlBrand = webpage.select("meta[property=og:brand]").first();
        String productBrand = htmlBrand.attr("content");

        return productBrand;
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

    private int getSPF(Document webpage){

        int SPF = -1;

        String productName = getProductName(webpage);
        String pattern = "spf\\s\\d+";
        Pattern find = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = find.matcher(productName);

        if(matcher.find()){
            SPF = Integer.parseInt((matcher.group(0)).substring(4));
        }

        return SPF;
    }

    int determineStability(String ingredients){

        int stable = 0;

        return stable;
    }

    private Map<String, ArrayList<String> > getAllProductInfo(String originLink) throws IOException{

        Map<String, ArrayList<String> > productInfo = new HashMap<>();
        ArrayList<String> allLinks = getAllProductLinks(originLink);
        ArrayList<String> supplementInfo = new ArrayList<>();

        for(String link : allLinks){
            supplementInfo.clear();
            Document productPage = Jsoup.connect(link).get();
            String productName = getProductName(productPage);
            String productBrand = getProductBrand(productPage);
            String allIngredients = getIngredients(productPage);
            int spf = getSPF(productPage);

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

    private Map<String, String> getProductInfo(String originLink) throws IOException{

        Map<String, String> productInfo = new HashMap<>();

        Document productPage = Jsoup.connect(originLink).get();
        productInfo.put("link", originLink);
        productInfo.put("name", getProductBrand(productPage));
        productInfo.put("brand", getProductBrand(productPage));
        productInfo.put("ingredients", getIngredients(productPage));
        productInfo.put("spf", getIngredients(productPage));
        String ssType = determineSSType(getIngredients(productPage));
        String chemical = "0";
        String physical = "0";
        if(ssType.equals("chemical")) chemical = "1";
        if(ssType.equals("physical")) physical = "1";
        productInfo.put("ss_type", determineSSType(getIngredients(productPage)));
        String stable = String.valueOf(determineStability(getIngredients(productPage)));
        productInfo.put("stable", stable);

        return productInfo;
    }

    public static void main(String [] args) throws IOException{

        UltaScraper testScrape = new UltaScraper();

        testScrape.getAllProductInfo("http://ulta.com/skin-care-suncare-sunscreen?N=27ff");
    }
}
