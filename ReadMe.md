# Overview
This application updates an Excel document fields based on the SQL table. The application developed to automate a weekly data entry process.
 
## Build
 * Compile, build, clean and generate the jar for the project using the Maven Projects menu of Intellij.
 
## Run 
* Before run application.
    * The first time you run the application asks for database credentials. You can find them in Kefalaio ERP using the instructions the section bellow.          
        * **Database name:**  Enotites -> Organwsi -> Etairies -> Metaboli etairias -> Epilogi etairias -> see Prefix
        * **Username and hostname:** Enotites -> Organwsi -> Systima -> Sql server setup
            

## Useful Links
* [Fix for "invalid signature file digest.."](https://stackoverflow.com/questions/34855649/invalid-signature-file-digest-for-manifest-main-attributes-exception-while-tryin/34856095#34856095)

## Excel structure
* These are the necessary changes that need to be done:
    * One more column added to the original excel (as first) that indicates if the row has been updated.
    * Delete extra rows with empty content

## TODO   
    - Good to do
        - run a validation algorithm after the update completed.
        - create a json that defines columns
        - check format of excel                		    
	 - Requested
        - log changes to a file
        - fix font size when adding new lines     
        
	    

