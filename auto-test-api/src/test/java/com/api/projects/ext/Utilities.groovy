/*
Author : Nayan Gaur
Comment : Edit at your own risk!!
*/

import groovy.json.JsonSlurper;
import java.util.Random;
class FrameworkUtilities{
    
	/*
	Gets the JSON object from the String json.
	*/
	def getJsonObject(String response){
        def slurper = new JsonSlurper();
        def jsonObject = slurper.parseText(response);
        return jsonObject;
    }
	
	// Get the random number in range -- minIndex is inclusive, and maxIndex is exclusive.
	def getRandomIndex(int minIndex,int maxIndex){
		Math.abs(new Random().nextInt() % maxIndex + minIndex);
	}
}