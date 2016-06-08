# In/Out Board Android App
One native In/Out Board Android App

## How to run this application
To run this sample you will need:
* Android Studio 2.0
* An Azure subscription
* [In/Out Board RESTful API](https://bitbucket.org/tsun424/inout) which provides service from server side

### Step 1: Register your Web API with your Microsoft Azure AD Tenant
**What am I doing?**   

*Microsoft Active Directory supports adding two types of applications. Web APIs that offer services to users and applications: (either on the web or an applicaiton running on a device) that access those Web APIs. In this step you are registering the Web API you are running locally for testing this sample. Normally this Web API would be a REST service that is offering functionaltiy you want an app to access. Microsoft Azure Active Directory can protect any endpoint!* 

*Here we are assuming you are registering your In/Out Board RESTful API referenced above, but this works for any Web API you'd want Azure Active Directory to protect.*

Steps to register a Web API with Microsoft Azure AD

1. Sign in to the [Azure management portal](https://manage.windowsazure.com).
2. Click on Active Directory in the left hand nav.
3. Click the directory tenant where you wish to register the application.
4. Click the Applications tab.
5. In the drawer, click Add.
6. Click "Add an application my organization is developing".
7. Enter a friendly name for the application, for example "inout-board-web", select "Web Application and/or Web API", and click next.
8. For the sign-on URL, enter the base URL for the application.
9. For the App ID URI, for example, `https://<your_domain_name>/inout-board-web`, replacing `<your_domain_name>` with the real domain name.  Click OK to complete the registration.
10. While still in the Azure portal, click the Configure tab of your application.
11. **Find the Client ID value and copy it aside**, you will need this later when configuring your application.

### Step 2: Register the Android Native Client application

Registering your web application is the first step. Next, you'll need to tell Azure Active Directory about your application as well. This allows your application to communicate with the just registered Web API

**What am I doing?**  

*As stated above, Microsoft Azure Active Directory supports adding two types of applications: Web APIs that offer services to users and applications (either on the web or an applicaiton running on a device) that access those Web APIs. In this step you are registering the application in this sample. You must do that in order for this application to be able to request to access the Web API you just registered. Azure Active Directory will refuse to even allow your application to ask for sign-in unless it's registered! That's part of the security of the model.* 

*Here we are assuming you are registering this sample application referenced above, but this works for any app you are developing.*

**Why am I putting both an application and a Web API in one tenant?**

*As you might have guessed, you could build an app that accesses an external API that is registered in Azure Active Directory from another tenant. If you do that your customers will be prompted to consent to the use of the API in the application. The nice part is Active Directory Authentication Library for Android takes care of this consent for you! As we get in to more advanced features you'll see this is an important part of the work needed to access the suite of Microsoft APIs from Azure and Office as well as any other service provider. For now, because you registered both your Web API and application under the same tenant you won't see any prompts for consent. This is usually the case if you are developing an application just for your own company to use.*

1. Sign in to the [Azure management portal](https://manage.windowsazure.com).
2. Click on Active Directory in the left hand nav.
3. Click the directory tenant where you wish to register the sample application.
4. Click the Applications tab.
5. In the drawer, click Add.
6. Click "Add an application my organization is developing".
7. Enter a friendly name for the application, for example "InOutApp", select "Native Client Application", and click next.
8. For the Redirect URI, enter a real redirect URI.  Click finish.
9. Click the Configure tab of the application.
10. **Find the Client ID value and copy it aside**, you will need this later when configuring your application.
11. In "Permissions to Other Applications", click "Add Application."  Select "Other" in the "Show" dropdown, and click the upper check mark.  Locate & click on the `inout-board-web`, and click the bottom check mark to add the application.  Select "Access TodoListService" from the "Delegated Permissions" dropdown, and save the configuration.

### Step 3: Download this Native Android Client App code.

>	git clone https://tsun424@bitbucket.org/tsun424/inout-app.git

### Step 4: Import it into your Android Studio

### Step 5: Configure the **Constants.java** file with your Azure AD application settings

1. Open `com.tsun.inout.util.Constants.java`
2. Update the constants with your real Azure Active Directory information. Explination of the mapping is below.

Explination of the parameters:
    
  * RESOURCE_ID is the APP ID from your registration to the Azure Portal. You need to setup this resource(WEB API) at AAD. APP ID URI configured in AAD App. It is required and identifies your registered application. This is from the portal.
  
  * CLIENT_ID is required and represents your tenant. A Client ID is a way we know what permissions you are requesting for your application and validate your application in your tenant is authorized to be used by the user. 
  
  * REDIRECT_URL is where the token is redirected to in web flows. It must be configured, otherwise, you will face sign in error. 
  
  * SERVICE_URL is the deployed Web API you are trying to you access that is protected by Azure Active Directory. This should be the endpoint you registered in the Azure Active Directory portal in Step 2. It should take the form of https://<your_tenant_name>/TodoListService
  
  * AUTHORITY_URL configuration format: https://login.windows.net/<tenantID>, replacing <tenantID> to the real tenant ID.
  
  **NOTE:** Leave the rest of the values in this file alone for now. We'll be playing with them later in other Samples, such as when we starting using the Microsoft Azure Android Authenticator for Work.

### Step 6: Configure API URLs to real Restful API URIs
  
### Step 7: Run the Android App