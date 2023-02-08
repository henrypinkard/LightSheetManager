LightSheetManager can be built with maven

To do so, `MMJ_.jar`, which contains the code for the Micro-Manager java layer, must be copied into `LightSheetManager/lib/MMJ_.jar`

To build, run `mvn clean package` from the `LightSheetManager`. The resultant jar will be available in the `LightSheetManager/target` folder, and then must be manually copied into the `Micro-manager/mmplugins`. Currently, the automated tests are throwing an error. In the case the tests can be skipped adnt the plugin built with `mvn clean package -Dmaven.test.skip`. 

Doing this requires having Maven installed. Most IDEs have a Maven plugin that can accomplish the same thing as the manual way above.