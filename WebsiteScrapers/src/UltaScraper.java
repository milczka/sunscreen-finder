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
            if( link.toString().contains("Next")){
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
                if( link.toString().contains("Next")){
                    nextPage = "http://ulta.com/" + link.attr("href");
                    nextLink = true;
                    break;
                }
            }
        }

        return allProductLinks;
    }

    private String getIngredients(Document productPage){

        Element ingredients = productPage.select("div[id=product-default-ingredients]").get(0).select("div").first();
        String allIngredients = ingredients.text();

        return allIngredients;
    }

    private String getProductName(Document productPage){

        Element htmlTitle = productPage.select("meta[property=og:title]").first();
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

    private int getSPF(Document productPage){

        int SPF = -1;

        String productName = getProductName(productPage);
        String pattern = "spf\\s\\d+";
        Pattern find = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = find.matcher(productName);

        if(matcher.find()){
            SPF = Integer.parseInt((matcher.group(0)).substring(4));
        }

        return SPF;
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
            supplementInfo.add(allIngredients);

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

        Map<String, ArrayList<String> > productInfo = testScrape.getAllProductInfo("http://ulta.com/skin-care-suncare-sunscreen?N=27ff");

        System.out.println(productInfo.toString());

    }
}
