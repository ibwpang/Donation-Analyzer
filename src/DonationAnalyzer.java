import java.util.*;
import java.io.*;

public class DonationAnalyzer {
	/**
	 * Coding Challenge - Insight Data Engineering.
	 * DonationAnalyzer.java
	 * Purpose: take a file listing individual campaign contributions for multiple years, 
	 * determine which ones came from repeat donors, calculate a few values and distill the 
	 * results into a single output file.
	 *
	 * @author Bowei Pang
	 * @version 1.0 2/8/18
	 */

	Map<Donor, Date> donors;
	Map<Recipient, Transaction> transactions;

	/**
	 * Constructor. Initialize the member variables.
	 *
	 * @param NO arguments.
	 * @return NO return values.
	 */
	public DonationAnalyzer() {
 		donors = new HashMap<Donor, Date>();
 		transactions = new HashMap<Recipient, Transaction>();
	}

	/**
	 * Analyze each record. Create an output string which contains the ID of recipient, zip code of donor,
	 * year of contribution, specified percentile of contributions, total amount of contributions and total
	 * transactions number.
	 *
	 * @param  record      the record need to be processed.
	 * @param  percentile  the percentile which will be used to calculate the specified percentile of contribution.
	 * @return the analysis result for each record.
	 */
	public String analyzing(Record record, int percentile) {
		String output = null;
		Donor donor = new Donor(record.NAME, record.ZIP_CODE);
		if (!donors.containsKey(donor)) {
			String m = record.TRANSACTION_DT.substring(0, 2);
			String d = record.TRANSACTION_DT.substring(2, 4);
			String y = record.TRANSACTION_DT.substring(4);
			donors.put(donor, new Date(m, d, y));
		} else {
			String prevYear = donors.get(donor).year;
			String currYear = record.TRANSACTION_DT.substring(4);
			if (prevYear.compareTo(currYear) == -1) {
				Recipient recipient = new Recipient(record.CMTE_ID, record.ZIP_CODE, record.TRANSACTION_DT.substring(4));
				if (!transactions.containsKey(recipient)) {
					Transaction transaction = new Transaction(record.TRANSACTION_AMT);
					transactions.put(recipient, transaction);
					output = recipient.toOutput() + (int)Math.round(transaction.totalAmount) + "|" + transaction.totalAmount + "|" + transaction.numberOfTransactions;
				} else {
					transactions.get(recipient).numberOfTransactions++;
					transactions.get(recipient).totalAmount += Double.parseDouble(record.TRANSACTION_AMT);
					transactions.get(recipient).listOfContributions.add(record.TRANSACTION_AMT);
					int size = transactions.get(recipient).listOfContributions.size();
					int idx = (int)Math.ceil((percentile * 1.0 / 100) * size) - 1;
					String percentileString = transactions.get(recipient).listOfContributions.get(idx);
					int percentileValue = (int)Math.round(Double.parseDouble(percentileString));
					output = recipient.toOutput() + percentileValue + "|" + transactions.get(recipient).totalAmount + "|" + transactions.get(recipient).numberOfTransactions;
				}
			} else {
				donors.get(donor).year = currYear;
				donors.get(donor).month = record.TRANSACTION_DT.substring(0, 2);
				donors.get(donor).date = record.TRANSACTION_DT.substring(2, 4);
			}
		}
		return output;
	}

	/**
	 * Convert the input string to a Record type which doesn't contain redundant data and 
	 * is easier to be analyzed.
	 *
	 * @param  input      the string needed to be converted.
	 * @return the record corresponding to each input string.
	 */
	public Record input2Record(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (input.startsWith("||", i)) {
				input = input.substring(0, i + 1) + "empty" + input.substring(i + 1);
			}
		}
		String[] fields = input.split("\\|");
		Record record = new Record(fields[0], fields[7], fields[10], fields[13], fields[14], fields[15]);
		return record;
	}

	/**
	 * Based on the description of this coding challenge, check a given record is valid or not.
	 *
	 * @param  record      the record needed to be checked.
	 * @return the result of the validation checking, true means valid, and false means invalid.
	 */
	public boolean isValidRecord(Record record) {
		if (!record.OTHER_ID.equals("empty")) {
			return false;
		}
		if (record.ZIP_CODE.equals("empty") || record.ZIP_CODE.length() < 5) {
			return false;
		}
		if (record.ZIP_CODE.length() > 5) {
			record.ZIP_CODE = record.ZIP_CODE.substring(0, 5);
		}
		if (record.CMTE_ID.equals("empty") || record.TRANSACTION_AMT.equals("empty") || Double.parseDouble(record.TRANSACTION_AMT) < 0) {
			return false;
		}
		if (record.NAME.equals("empty") || record.TRANSACTION_DT.equals("empty")) {
			return false;
		}
		int m = Integer.parseInt(record.TRANSACTION_DT.substring(0, 2));
		int d = Integer.parseInt(record.TRANSACTION_DT.substring(2, 4));
		int y = Integer.parseInt(record.TRANSACTION_DT.substring(4));
		if (d < 1 || d > 31 || m < 1 || m > 12 || y > 2018) {
			return false;
		}
		return true;
	}

	/**
 	* The main method for the DonationAnalyzer program. Read "itcont.txt" and "percentileChec.txt" line by line,
 	* and convert each line to a record. Check the validation of each record, and analyze each record. 
 	* Write the output to "repeat_donors.txt".
 	* 
 	* @param args[0]  the path of input file "itcont.txt"
 	* @param args[1]  the path of input file "percentile.txt"
 	* @param args[2]  the path of output file "repeat_donors.txt"
 	*/
	public static void main(String[] args) {
		DonationAnalyzer anlz = new DonationAnalyzer();
		try {
			BufferedReader itcont = new BufferedReader(new FileReader(args[0]));
			BufferedReader perc = new BufferedReader(new FileReader(args[1]));
			BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
			int percentile = Integer.parseInt(perc.readLine());
			for (String line = itcont.readLine(); line != null; line = itcont.readLine()) {
				Record record = anlz.input2Record(line);
				if (!anlz.isValidRecord(record)) {
					continue;
				}
				String output = anlz.analyzing(record, percentile);
				if (output != null) {
					writer.write(output);
					writer.newLine();
				}
			}
			itcont.close();
			perc.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Donor {
	String NAME;
	String ZIP_CODE;
	public Donor(String name, String zip) {
		this.NAME = name;
		this.ZIP_CODE = zip;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Donor)) {
			return false;
		}
		Donor donor = (Donor)obj;
		return this.NAME.equals(donor.NAME) && this.ZIP_CODE.equals(donor.ZIP_CODE);
	}
	@Override
	public int hashCode() {
		return (NAME + ZIP_CODE).hashCode();
	}
}

class Date {
	String month;
	String date;
	String year;
	public Date(String month, String date, String year) {
		this.month = month;
		this.date = date;
		this.year = year;
	}
	public String toMDYString() {
		return month + date + year;
	}
	public String toYMDString() {
		return year + month + date;
	}
}

class Record {
	String CMTE_ID;
	String NAME;
	String ZIP_CODE;
	String TRANSACTION_DT;
	String TRANSACTION_AMT;
	String OTHER_ID;
	public Record(String CMTE_ID, String NAME, String ZIP_CODE, String TRANSACTION_DT, String TRANSACTION_AMT, String OTHER_ID) {
		this.CMTE_ID = CMTE_ID;
		this.NAME = NAME;
		this.ZIP_CODE = ZIP_CODE;
		this.TRANSACTION_DT = TRANSACTION_DT;
		this.TRANSACTION_AMT = TRANSACTION_AMT;
		this.OTHER_ID = OTHER_ID;
	}
}

class Recipient {
	String CMTE_ID;
	String ZIP_CODE;
	String year;
	public Recipient(String CMTE_ID, String ZIP_CODE, String year) {
		this.CMTE_ID = CMTE_ID;
		this.ZIP_CODE = ZIP_CODE;
		this.year = year;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Recipient)) {
			return false;
		}
		Recipient recipient = (Recipient)obj;
		return this.CMTE_ID.equals(recipient.CMTE_ID) && this.ZIP_CODE.equals(recipient.ZIP_CODE) && this.year.equals(recipient.year);
	}
	@Override
	public int hashCode() {
		return (CMTE_ID + ZIP_CODE + year).hashCode();
	}
	public String toOutput() {
		return CMTE_ID + "|" + ZIP_CODE + "|" + year + "|";
	}
}

class Transaction {
	int numberOfTransactions;
	double totalAmount;
	List<String> listOfContributions;
	public Transaction(String amountOfContribution) {
		numberOfTransactions = 1;
		totalAmount = Double.parseDouble(amountOfContribution);
		listOfContributions = new ArrayList<String>();
		listOfContributions.add(amountOfContribution);
	}
}