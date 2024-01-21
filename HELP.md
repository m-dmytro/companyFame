# Getting Started
Application identifies which companies from the given list are mentioned in the given news articles.

# Run
To check how solution works -> run CompanyFameApplication class.

Update testing data with your DataSet in the resource folder and recheck path in the application.properties:
* path to store csv file with the list of companies -> /resource/testData/ 
  * application.properties -> company.file.path=testData/160408_company_list.csv
* path to store xml files with articles -> /resource/testData/data/
  * application.properties -> articles.files.path=testData/data/