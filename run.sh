#!/bin/bash
javac src/DonationAnalyzer.java
java -classpath src DonationAnalyzer ./input/itcont.txt ./input/percentile.txt ./output/repeat_donors.txt
