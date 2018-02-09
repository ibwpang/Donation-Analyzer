# Donation Analyzer
This is my solution of the coding challenge of Insight Data Engineering.

# Approach Summary
In this challenge, I used Java to implement this donation analyzer. 
For the I/O part, I used BufferedReader to read data from the input text file into the memory line by line. I used BufferedWriter to write the analytic result of each input record to the output text file. 

For looking for the repeat donors, because we need to find who are repeat donors, so I used a HashMap to store all of the donors I have encountered. The key of this hash map is the Donor which contains two fields: name and zip_code. The value of this hash map is the contribution Date which contains three fields: year, month and date. So for each donor, I keep going to update the most earlist contribution of this donor. In this way, if I encounter a new record and the donor of this record is already contained in the hash map, and the earliest contribution date of this donor is earlier than the date of this record, I can determine this donor is a repeat donor. I also override the equals method and hashcode method of Donor class, so as soon as the name and zip_code of two donors are same, the hash map will recognize them as same donor.

For the calculation part, because for each triplet (I created a Recipient class for it): CMTE_ID, ZIP_CODE and year, we need to record the total number of transactions, the total amount of contributions and a list of Contributions (I also created a Transaction class for these three values), so I used another HashMap to store the mapping relationship between the Recipient and corresponding Transaction. Every time I found a repeat donor, I will check whether the Recipient of this donation has been contained in the hashmap or not. If it have not been contained, I will create a new Transaction for it and store this Recipient-Transaction pair into the hash map. If the Recipient of this donation has been contained, I will get the Transaction of this Recipient and update the fields of the Transaction, so basically, I will add one to the total number of transactions, and add the amount of contribution of this record to the total amount of transactions, and also append the amount of contribution of this record to the list of contributions. The last task is to calculate the specified percentile value of contributions. Following the nearest-rank method, I calculated the rank using the formula: rank = ceil(m/100 * n), where the m means the value in "percentile.txt", the n means the total number of the transactions for this Recipient. I got the percentile value which is stored at the index of rank. I assume the percentile is about time (i.e. the elements in the "list of contributions" are sorted by when they are inserted, instead their amount), so the 30th percentile value means there are 30% values are inserted before the 30th value. If we want the percentile to be about amount, we just need to sort the list by amount firstly and then get the specified percentile value. Finally, I output the "CMTE_ID, ZIP_CODE, year, percentile value, total amount of transactions, total number of transactions" to the file.

# Dependencies
My Java version and environment:
java version "1.8.0_101"
Java(TM) SE Runtime Environment (build 1.8.0_101-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.101-b13, mixed mode)

There are no other dependencies.

# Run instructions
Go to the directory which contains "input", "output", "src", "README.md", "run.sh". When you run the file "run.sh", the source code in the "src" subdirectory will be compiled and run.