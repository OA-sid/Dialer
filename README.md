-> This is a fully responsive calling application for android with a clean architecture.
-> It uses firebase to store the contact details from the device.
-> It takes the phone number as the primary key to overcome any kind of incosistency or duplicacy during storage of data.
-> It uses Device's native phone application to make calls.
->It's written in Kotlin with Jetpack Compose.




steps to use the application:

1. launch the app
2. Make sure you've allowed contacts and phone permission
3. 1st screen will show you all the contacts you have in your phone as well as a phone icon
4. You can either choose a contact or phone icon to make a call by dialing contact number manually
5. it also shows **sync Contacts Button** at the top left side of the screen
6. when you click sync Contacts button after connecting it with the firebase , It'll automatically create a backup of your contacts on firebase while taking phone number as the primary key so that any duplicate contacts shouldn't be stored
7. if you click any of the contacts you will be redirected to the detail page
8. here you will have 3 buttons **Edit Contact, Delete Contact, Call Contact**
9. when you click on **Delete Contact**, This will delete your contact details
10. when you cllick on Edit **Edit Contact**, This will redirect you to the editing page where you can edit either name or number or both.
11. If you Click the **Call Contact** Button, This will make a call to chosen contact by using your native call application
    
