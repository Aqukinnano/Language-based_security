import backEnd.*;
import java.util.Scanner;

public class ShoppingCart {
    private static void print(Wallet wallet, Pocket pocket) throws Exception {
        System.out.println("Your current balance is: " + wallet.getBalance() + " credits.");
        System.out.println(Store.asString());
        System.out.println("Your current pocket is:\n" + pocket.getPocket());
    }

    private static String scan(Scanner scanner) throws Exception {
        System.out.print("What do you want to buy? (type quit to stop) ");
        return scanner.nextLine();
    }

    public static void main(String[] args) throws Exception {
        Wallet wallet = new Wallet();
        Pocket pocket = new Pocket();
        Scanner scanner = new Scanner(System.in);

        print(wallet, pocket);
        String product = scan(scanner);

        while(!product.equals("quit")) {
            /* TODO:
               - check if the amount of credits is enough, if not stop the execution.
               - otherwise, withdraw the price of the product from the wallet.
               - add the name of the product to the pocket file.
               - print the new balance.
            */
            
            try {               
                // fetch the price of product
                int price = Store.getProductPrice(product);
                
                // check the balance
                int currentBalance = wallet.getBalance();
                if(wallet.getBalance() < price) {
                    System.out.println("Not enough credits to buy " + product + "!");
                    break;
                }

                System.out.println("\n[TOCTOU window] press Enter to continue...");
                System.in.read();  // system pause

                //withdraw
                wallet.setBalance(currentBalance - price);
             
                // add product to pocket 
                pocket.addProduct(product);
                
                System.out.println("Successfully purchased " + product + " for " + price + " credits");
                
                // print updated info
                print(wallet, pocket);
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                // break;
            }
            
            product = scan(scanner);
        }
    }
}
