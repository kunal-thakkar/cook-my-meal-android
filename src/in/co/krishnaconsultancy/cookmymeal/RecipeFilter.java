package in.co.krishnaconsultancy.cookmymeal;

public class RecipeFilter {

	private static String ingrendents = "";

	public static String getIngrendentsCriteria() {
		
		return "%"+ingrendents.replaceAll(" ", "%")+"%";
	}

	public static void setIngrendentsCriteria(String ingrendents) {
		RecipeFilter.ingrendents = ingrendents;
	}

}
