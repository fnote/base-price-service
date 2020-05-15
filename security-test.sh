#!/usr/bin/env bash
#run the fortify scan
docker run --rm -v "$PWD":/Project -w /Project sysco/payplus-api:fortify-18.20 sh Fortifypayplus-api.sh

#To generate pdf report uncomment the below line
#docker run --rm -v "$PWD":/Project -w /Project sysco/payplus:fortify-18.20 ReportGenerator -template "DeveloperWorkbook.xml" -format "pdf" -f "./fortify-payplus-api.pdf" -source  "./Fortifypayplus-api.fpr"

#to generate csv files
docker run --rm -v "$PWD":/Project -w /Project sysco/payplus-api:fortify-18.20 FPRUtility -information -listIssues -project Fortifypayplus-api.fpr -f Fortifypayplus-api.csv -outputFormat CSV


