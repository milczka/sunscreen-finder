import java.io.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class UltaScraper{
	public static void main(String [] args) throws IOException{
		Document website = Jsoup.connect("http://www.ulta.com/skin-care-suncare-sunscreen?N=27ff").get();
		// doc.select("p.prod-desc");
		Elements sunscreenLinks = website.select("p[class=prod-desc]");

		Element oneProduct = sunscreenLinks.get(0).select("a").first();
		String linkToProduct = "http://www.ulta.com" + oneProduct.attr("href");
		String productName = oneProduct.text();

		Document productPage = Jsoup.connect(linkToProduct).get();

		Elements ingredients = productPage.select("div[id=product-default-ingredients]");

		Element oneProductIngredients = ingredients.get(0).select("div").first();

		String allIngredients = oneProductIngredients.text();

		System.out.println(productName);
		System.out.println(allIngredients);
	}
}
