# Getting Started
Application identifies which companies from the given list are mentioned in the given news articles.

## Dataset:
For the purpose of this assignment, we will provide you with a dataset consisting of two different types of information:
* newspaper article texts and
* a list of companies
Each newspaper article is provided in separate UTF-8 encoded XML file (.xml) in the “data” sub-folder. Note: there are a couple of thousand newspaper article files. The list of companies is provided as a UTF-8 encoded CSV file (.csv) consisting of two columns: company ID and company name (1_company_list.csv).

## Task:
Write a small application able to identify which companies from the given list are mentioned in the given news articles. While working on your code, try to address the following challenges:
* In general, optimize your code to find as many companies mentioned in the news articles as possible, but try to avoid so-called “false positives”, companies identified by your algorithm which are not mentioned in the text. Ask your instructor if you're unsure. Consider unit tests to make your results re-producible.
* Keep an eye on the structure of the company names provided, in particular legal amendments for example "Limited", "Ltd." etc. and additional information provided in brackets

# Run
To check how solution works -> run CompanyFameApplication class.

Update testing data with your DataSet in the resource folder and recheck path in the application.properties:
* path to store csv file with the list of companies -> /resource/testData/ 
  * application.properties -> company.file.path=testData/160408_company_list.csv
* path to store xml files with articles -> /resource/testData/data/
  * application.properties -> articles.files.path=testData/data/