# HokieDiner

Third Party Example Android Application for Mobile Single Sign On Solution

This Application was developed based on the [sample applications](https://github.com/Brian-Kaplan/WSO2-API-Manager/tree/master/android-idp-sdk-1.1.0/samples) inside the SDK 

# Documentation

 Installation
  - Simply clone the repo and open it in Android Studio 
  - This applciation was installed and tested on a Nexus 5 Emulator Target API 22

 Server Configurations
  - Follow the documentation given [here] (https://docs.wso2.com/display/AM180/Quick+Start+Guide#QuickStartGuide-CreatinganAPI) to set up an API Endpoint for this application 
  - This will set up a Phone Verification API and also create a Service Provider for you. Make sure the Callback, ClientID and ClientSecret match the values in OAuthConstants.java of this application. You will also have to change the URI in APITask to the same API Endpoint that you just created. 
