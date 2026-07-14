package tourism.billing;

import java.util.Scanner;
import java.local.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Billing System
{
	static Scanner sc=new Scanner(System.in);
	
	static final String RESET = "\u001B[0m";
    	static final String CYAN = "\u001B[36m";
    	static final String GREEN = "\u001B[32m";
    	static final String YELLOW = "\u001B[33m";
    	static final String RED = "\u001B[31m";
    	static final String BOLD = "\u001B[1m";
	

	static void showBillingBanner() {

        String[] art = {

"██████╗ ██╗██╗     ██╗     ██╗███╗   ██╗ ██████╗ ",
"██╔══██╗██║██║     ██║     ██║████╗  ██║██╔════╝ ",
"██████╔╝██║██║     ██║     ██║██╔██╗ ██║██║  ███╗",
"██╔══██╗██║██║     ██║     ██║██║╚██╗██║██║   ██║",
"██████╔╝██║███████╗███████╗██║██║ ╚████║╚██████╔╝",
"╚═════╝ ╚═╝╚══════╝╚══════╝╚═╝╚═╝  ╚═══╝ ╚═════╝ ",

"",
"╔══════════════════════════════╗",
"║           BILLING            ║",
"╚══════════════════════════════╝"
        };

        System.out.println(CYAN + BOLD);
        for (String line : art) {
            System.out.println(line);
        }
        System.out.println(RESET);
    }

	static void animate(String message) 
	{
        	System.out.print("\n" + CYAN + message);
	
		for(int i=0;i<5;i++)
		{
			try
			{
				Thread.sleep(500);
			}
			catch(Exception e){}
			System.out.print(".");
		}
		System.out.println(" DONE" + RESET);
	}
	
	static void generateBill()
	{
		showBillingBanner();
		
		System.out.print("Enter Place Cost : Rs. ");
		double placeCost= sc.nextDouble();
		
		Sytem.out.print("Enter Vehicle Cost : Rs. ");
		double vehicleCost = sc.nextDouble();
			
		animate("Calculating Bill");
		
		double subtotal = placeCost + vehicleCost;
		
		System.out.println(YELLOW + "\nSubtotal      : Rs. " + subtotal + RESET);
		
		double discount=0;
		if(subtotal >= 3000)
			discount = subtotal * 0.15;
		else if(subtotal >= 2000)
			discount = subtotal * 0.10;
		else if(subtotal >=1000)
			discount = subtotal * 0.05;
		
		double finalAmount = subtotal - discount;
		
		System.out.println(GREEN + "Discount   : Rs. " + discount);
        	System.out.println("Final Amount  : Rs. " + finalAmount + RESET);
	
		System.out.println("Select Payment Method: ");
		System.out.println("1. Cash");
		System.out.println("2. Card/ UPI");
		
		int choice = sc.nextInt();
		
		double received = 0;
		int txnId = (int)(Math.random() * 900000) + 100000;
		
		LocalDateTime now = LocalDateTime.now();
        	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		
		
		if(choice == 1)
		{
			System.out.println("Enter Cash Amount: ");
			received = sc.nextDouble();
			
			if(received < finalAmount)
			{
				System.out.println(RED + "insufficient Amount!" + RESET);
				return;
			}
			
			animate("Processing Cash Payment");
			
			System.out.println(GREEN + "\nPayment Successful (cash)");
			System.out.pirntln("Amount Received: Rs. " + received + RESET);
		}
		else
		{
			System.out.print("Enter Card/UPI ID: ");
			sc.next();
			
			animate("Processing Online Payment");
			
			received = finalAmount;
			
			System.out.println(GREEN + "\nPayment Successful (Card/UPI));
			System.out.println("Amount Received: Rs. " + received);
			System.out.println("Transaction ID : " + tnxId + RESET);
		}
		
		 System.out.println("Date & Time    : " + dtf.format(now));
		
		System.out.print("\nDo you want receipt? (yes/no): ");
		String rec = sc.next();
		
		if(rec.equalsIgnoreCase("yes"))
		{
			animate("Generating Receipt");

			System.out.println(CYAN + "\n====================================");
			System.out.println("		TOURISM BILL RECEIPT		");
			System.out.println("====================================");
				
			
			System.out.println("Place Cost : Rs. " + placeCost);
			System.out.println("Vehicle Cost : Rs. " + vehicleCost);
			System.out.println("Subtotal Cost : Rs. " + subtotal);
			System.out.println("Discount : Rs. " + discount);
			System.out.println("Final Amount : Rs. " + finalAmount);
			System.out.println("Paid Amount : Rs. " + received);
			System.out.println("Transaction ID :  " + txnId);
			System.out.println("Date & Time :  " + dtf.format(now));
			System.out.println(Status  : " + SUCCESS");
			System.out.println("====================================" + RESET);
	      }
	      else
	      {
		animate("Finalizing Transaction");
	
		System.out.println(GREEN + "\nTHANK YOU FOR USING TOURISM SYSTEM 🙏");
		System.out.println("VISIT AGAIN ✨" + RESET);
	      }
	}
	public static void main(String[] args)
	{
		generateBill();
	}
}

	

