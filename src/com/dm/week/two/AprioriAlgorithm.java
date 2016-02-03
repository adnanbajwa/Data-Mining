package com.dm.week.two;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class AprioriAlgorithm {

	 public static double minimum_support = 0.1;
	 public static String path = "/users/adnanbajwa/Desktop/data-2016.csv";
	    
	    public static void main(String[] args) {
	    	
	        try {
	            Stream<String> lines = Files.lines(Paths.get(path), Charset.forName("ISO-8859-1"));
	            ArrayList<String> courses = new ArrayList();
	            lines.forEach(x -> {
	                String[] columns = x.split(" ");                
	                for (int i = 2; i < columns.length; i += 5) { 
	                    if (!courses.contains(columns[i])) {
	                        courses.add(columns[i]); 
	                    }                                       
	                }
	            });
	            
	            ArrayList<ArrayList<String>> oneItemsets = new ArrayList();
	            for(String course:courses) {
	                ArrayList<String> item = new ArrayList();
	                item.add(course);
	                oneItemsets.add(item);
	            }       
	               
	            ArrayList<ArrayList<String>> listOfTranscripts = new ArrayList();
	            lines = Files.lines(Paths.get(path), Charset.forName("ISO-8859-1"));
	            Stream<Student> studentStream = lines.map(Student::createStudent);
	            
	            studentStream.forEach(student -> {
	                ArrayList<String> courseCodes = new ArrayList();
	                for (CourseTranscript c:student.courseTranscriptList) {
	                    courseCodes.add("" + c.courseCode);
	                }
	                listOfTranscripts.add(courseCodes);
	            });           
	            result(oneItemsets, listOfTranscripts);      
	            
	        } catch (IOException ex) {
	            System.out.println(ex.getMessage());
	        }
	    }
	    
	    public static void result(ArrayList<ArrayList<String>> itemsets, ArrayList<ArrayList<String>> transactions) {            
	        if (itemsets.size() < 1) {
	            return;
	        }
	        
	        int candidateCount = 9999;        
	        while (candidateCount > 0) {
	            int itemsetSize = itemsets.get(0).size()+1;
	            System.out.println("Generated " + itemsetSize + "-itemsets:");
	            ArrayList<ArrayList<String>> candidates = generate(itemsets, transactions);        
	            ArrayList<ArrayList<String>> newCandidates = new ArrayList();    
	            for(ArrayList<String> candidate:candidates) {
	                double support = findSupport(transactions, candidate);
	          
	                if (support >= minimum_support) {               
	                    newCandidates.add(candidate);
	                    System.out.println("Candidate: " + candidate + ", support: " + support);
	                }
	            }
	            System.out.println("Total number of candidates: " + newCandidates.size());
	            candidateCount = newCandidates.size();
	            itemsets = newCandidates;    
	        }
	    }
	        
	    public static double findSupport(ArrayList<ArrayList<String>> transactions, ArrayList<String> itemset) {
	        int supportCount = 0;
	        for(ArrayList<String> transaction:transactions) {
	            int count = 0;
	            for (String item:itemset) {
	                if (transaction.contains(item)) {
	                    count++;
	                }
	            }
	            if (count == itemset.size()) {
	                supportCount++;
	            }
	        }
	        return (double) supportCount / transactions.size();
	    }
	        
	    public static ArrayList<String> union(ArrayList<String> list1, ArrayList<String> list2) {
	        ArrayList<String> result = new ArrayList();
	        
	        for(String item:list1) {
	            result.add(item);
	        }
	        
	        for(String item:list2) {
	            if (!result.contains(item)) {
	                result.add(item);
	            }
	        }
	        return result;   
	    }
	        
	    public static ArrayList<ArrayList<String>> generate(ArrayList<ArrayList<String>> lists, ArrayList<ArrayList<String>> transactions) {
	        ArrayList<ArrayList<String>> response = new ArrayList();
	        
	        if (lists.size() <= 1) {
	            return response; 
	        }       
	                
	        int index = 0;
	        while (index < lists.size() - 1) {
	            ArrayList<String> first = lists.get(index);
	            if (findSupport(transactions, first) >= minimum_support) {
	                int index2 = index + 1;
	                while (index2 < lists.size()) {
	                    ArrayList<String> second = lists.get(index2);
	    
	                    if (first.size() == 1 || second.size() == 1) {
	                        if (findSupport(transactions, second) >= minimum_support) {
	                            response.add(union(first, second));
	                        } 
	                    } else {
	                        if (first.subList(0, first.size() - 1).equals(second.subList(0, second.size() - 1))) {
	                            if (findSupport(transactions, second) >= minimum_support) {
	                                response.add(union(first, second));
	                            }                    
	                        }
	                    }
	                    index2++;
	                }            
	            }            
	            index++;
	        }
	        return response;
	    }
	    public static class Student {
	        int startingYear;
	        ArrayList<CourseTranscript> courseTranscriptList;
	        
	        static Student createStudent(String line){
	            String[] columns = line.split(" ");
	            Student s = new Student();
	            s.startingYear = Integer.parseInt(columns[0]);
	            s.courseTranscriptList = CourseTranscript.createCourseTranscriptList(line);
	            return s;
	        }
	    }
	    
	    public static class CourseTranscript {
	        int startingMonth; 
	        String courseName;
	        int courseCode;
	        int grade;
	        double creditPoint;
	        
	        static ArrayList<CourseTranscript> createCourseTranscriptList(String line){
	            String[] columns = line.split(" ");
	            ArrayList<CourseTranscript> courseTranscriptList = new ArrayList<>();
	            
	            for (int i = 1; i+4 < columns.length; i += 5) {
	                CourseTranscript c = new CourseTranscript();                
	                c.startingMonth = Integer.parseInt(columns[i].replace("-", ""));
	                c.courseCode = Integer.parseInt(columns[i+1]);
	                c.courseName = columns[i+2];
	                c.creditPoint = Double.parseDouble(columns[i+3]);
	                c.grade = Integer.parseInt(columns[i+4]);
	                courseTranscriptList.add(c);
	            }
	            return courseTranscriptList;
	        }
	        
	        public String getCourseName() {
	            return this.courseName;
	        } 
	    }
	}