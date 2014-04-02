//Melissa Iori - Fossilized 2.0

package fossilized;

import java.util.Scanner;

public class Fossilized {
    //global vars
    public static final boolean DEBUGGING = false;   //debug flag
    public static final int MAX_LOCALES = 9;        //total number of locales
    public static int currentLocale = 0;            //starting in location 0
    public static String command;                   //input command
    public static boolean stillPlaying = true;      //boolean flag controls game loop
    public static Locale[] locations;               //locations array
    public static Caves[] caveLocations;
    public static Items[] items;                    //global items array
    public static Items[] inventory = new Items[8]; //player inventory
    public static int[][]  map;                     //navigation by two-dimensional array
    public static int moves = 0;                    //moves count
    public static int score = 5;                    //score count starts at a new location, so init to 5
    public static double ratio = 0.00;              //ratio of moves to score
    public static String possibleDirs = " north south"; //possible directions from the current location
    public static boolean inventoryHasAtLeastOne = false;       //flag to check if inventory is empty
    
    public static void main(String[] args) {
        //start loc
        if (args.length > 0) {
           try {
              int startLocation = Integer.parseInt(args[0]);
              //check for loc
              if ( startLocation >= 0 && startLocation <= MAX_LOCALES) {
                  currentLocale = startLocation;
              }
           } catch(NumberFormatException ex) {   // catch(Exception ex)
              System.out.println("" + args[0] + " is not a location.");
              if (DEBUGGING) {
                 System.out.println(ex.toString());
              }
           }
        }

        
        init();
        updateDisplay();

        //loop
        while (stillPlaying) {
            getCommand();
            navigate();
            updateDisplay();
            if(score<0){
                System.out.println("You've spent more than you have. There're no free lunches in this world, pal. But:");
                quit();
            }
        }
        System.out.println("Thanks for playing!");
    }


    private static void init() {
        //init
        command = new String();
        stillPlaying = true;
        
        //items
                
        Items item0 = new Items(0);
        item0.setItemName("Water");
        item0.setDesc("--Will you take some water?");
        
        Items item1 = new Items(1);
        item1.setItemName("Shovel");
        item1.setDesc("--There's a shovel here. Take it?");
        
        Items item2 = new Items(2);
        item2.setItemName("Map");
        item2.setDesc("--There is a map here. Take it?");
        
        Items item3 = new Items(3);
        item3.setItemName("Aegis Stone");
        item3.setDesc("--The Aegis Stone is a powerful artifact. Take it?");
        
        Items item4 = new Items(4);
        item4.setItemName("Star Shard");
        //Items 4, 5, and 6 lack the .desc property because they're exclusive to the Magic Shoppe.
        item4.setCost(5);
        
        Items item5 = new Items(5);
        item5.setItemName("Antimony Regulus");
        item5.setCost(7);
        
        Items item6 = new Items(6);
        item6.setItemName("Crucible");
        item6.setCost(9);
        
        Items item7 = new Items(7);
        item7.setItemName("Homunculus Flask");
        item7.setCost(40);

        items = new Items[8];
        items[0] = item0;
        items[1] = item1;
        items[2] = item2;
        items[3] = item3; 
        items[4] = item4;
        items[5] = item5;
        items[6] = item6;
        items[7] = item7;
 
        //locales
        Locale loc0 = new Locale(0);
        loc0.setName(">>Salt Desert");
        loc0.setDesc("You are in the Salt Desert. White sand stretches in all directions.");
        loc0.setHasVisited(true);
        loc0.setItem(item2);
        
        Locale loc1 = new Locale(1);
        loc1.setName(">>Tylavian Border");
        loc1.setDesc("You arrive at a barbed-wire barrier.");
            
        Caves loc2 = new Caves(2); // Locale(2);
        loc2.setName(">>Ruby Cave");
        loc2.setDesc("This is a dark cave, with gleaming red walls.");
        loc2.setFossil("Pleiosaur");
     
        
        Caves loc3 = new Caves(3);
        loc3.setName(">>Cave Passage");
        loc3.setDesc("A treacherously steep passage leads downward.");
        loc3.setFossil("Trilobyte");
        loc3.setItem(item1);
        
        Caves loc4 = new Caves(4);
        loc4.setName(">>Treasure Hoard");
        loc4.setDesc("A multitude of rare items within this trove.");
        loc4.setFossil("Fern");
        loc4.setItem(item3);
        
        Caves loc5 = new Caves(5);
        loc5.setName(">>Cave Cliff");
        loc5.setDesc("The ridge makes it impossible to go any further, but there is a view of the Ruby Stalactites.");
        loc5.setFossil("Archaeopteryx");

        Locale loc6 = new Locale(6);
        loc6.setName(">>Oasis");
        loc6.setDesc("You've found an oasis in the Salt Desert.");
        loc6.setItem(item0);
        
        Locale loc7 = new Locale(7);
        loc7.setName(">>Magick Shoppe");
        loc7.setDesc("After a long hike, you come across a remote Magick Shoppe known to few and praised by many.");
        
        Locale loc8 = new Locale(8);
        loc8.setName(">>Star Plain");
        loc8.setDesc("On this plain, you get an amazing view of the night sky and constellations.");
        
        
        locations = new Locale[9];
        locations[0] = loc0; // "Salt Desert" 
        locations[1] = loc1; // "Tylavian Border"   
        locations[2] = loc2; // "Ruby Cave"   
        locations[3] = loc3; // "Cave Passage"
        locations[4] = loc4; // "Treasure Hoard"
        locations[5] = loc5; // "Cave Cliff"
        locations[6] = loc6; // "Oasis"
        locations[7] = loc7; // "Magick Shoppe"
        locations[8] = loc8; // "Star Plain"
        
        caveLocations = new Caves[6];
        caveLocations[2] = loc2; // "Ruby Cave"   
        caveLocations[3] = loc3; // "Cave Passage"
        caveLocations[4] = loc4; // "Treasure Hoard"
        caveLocations[5] = loc5; // "Cave Cliff"
        //to be able to call caveLocations[currentLocale] the indices are the same as the locations array above
        //the purpose of this separate array is to manipulate methods exclusive to the Cave subclass
        //because locations[] is of type Locale and can't call those methods
        
        System.out.println("Welcome to Fossilized 2.0. For help type 'help'");
        
        //map
        map = new int[][] {
                                 /* N   S   E   W */
                                 /* 0   1   2   3 */
         /* map[0] for loc 0 */  {  2,  1, -1, -1 },
         /* map[1] for loc 1 */  {  0, -1, 6, -1 },
         /* map[2] for loc 2 */  { -1,  0, 3, 5 },
                /*3*/            { -1,  4, -1,  2 },
                /*4*/            { 3,  -1, -1, -1 },
                /*5*/            { -1,  -1,  2, -1 },
                /*6*/            { -1,  -1,  7,  1 },
                /*7*/            {  8,  -1,  -1, 6},   
                /*8*/            { -1,   7,  -1, -1 },
        };
    }

    private static void updateDisplay() {
        System.out.println(locations[currentLocale].getName());
        System.out.println(locations[currentLocale].getDesc());
     if ((currentLocale == 0 && items[2].getObtained() == false)
             ||(currentLocale == 3 && items[1].getObtained() == false)
             ||(currentLocale == 4 &&  items[3].getObtained() == false)
             ||(currentLocale == 6 && items[0].getObtained() == false)){
        System.out.println(locations[currentLocale].getItem().getDesc());
     }
     else if (currentLocale == 7){
        System.out.println("What do you wish to buy? Use the 'buy' or 'b' command with the name of the item. Spelling counts."); 
        
        for (int i=4;i<8;i++){
         if(items[i].getObtained() == false){
         System.out.println(items[i].getItemName() + "," + items[i].getCost() + " pts.");
         }
       } 
     }
    }

    private static void getCommand() {
        System.out.print("[Moves:" + moves + " Score:" + score + " Ratio:" + ratio + " Possible directions:" + possibleDirs + "] ");
        Scanner inputReader = new Scanner(System.in);
        command = inputReader.nextLine();  
    }

    private static void navigate() {
        final int INVALID = -1;
        int dir = INVALID;  

        if (command.equalsIgnoreCase("north") || command.equalsIgnoreCase("n") ) {
            dir = 0;
        } else if ( command.equalsIgnoreCase("south") || command.equalsIgnoreCase("s") ) {
            dir = 1;
        } else if ( command.equalsIgnoreCase("east")  || command.equalsIgnoreCase("e") ) {
            dir = 2;
        } else if ( command.equalsIgnoreCase("west")  || command.equalsIgnoreCase("w") ) {
            dir = 3;
        } else if ( command.equalsIgnoreCase("quit")  || command.equalsIgnoreCase("q")) {
            quit();
        } else if ( command.equalsIgnoreCase("help")  || command.equalsIgnoreCase("h")) {
            help();
        }  else if ( command.equalsIgnoreCase("take")  || command.equalsIgnoreCase("t")) {
            inventoryHasAtLeastOne = true;
            if (currentLocale == 0){
            inventoryAdder(items[2]);
            System.out.println("Use the 'map' or 'm' command to view the map.");
            }
            else if(currentLocale == 3){
            inventoryAdder(items[1]);
            System.out.println("Shovel taken.");
            }
            else if(currentLocale == 4){
            inventoryAdder(items[3]);
            System.out.println("Who wouldn\'t?");
            }
            else if(currentLocale == 6){
            inventoryAdder(items[0]);
            System.out.println("Water taken.");
            }
            else{
            System.out.println("There's nothing to take.");
            }
        }
        else if ( command.equalsIgnoreCase("inventory")  || command.equalsIgnoreCase("i")) {
            if(inventoryHasAtLeastOne == false){
                System.out.println("You have not picked up any items yet. Use the 't' or 'take' command if prompted to take an item.");
            }
            else{
            for (int i = 0; i<inventory.length; i++)
            {
             if(inventory[i] != null){
                 System.out.println(inventory[i].getItemName());
                }   
              }
           }
        }
         else if ( command.equalsIgnoreCase("map")  || command.equalsIgnoreCase("m")) {
           if(items[2].getObtained() != true){
             System.out.println("What map?"); 
           }  
           else{
            System.out.println(" ");   
            System.out.println("X--X ------ X");
            System.out.println("   ||       ||");
            System.out.println("   X         X     X");
            System.out.println("   ||              ||");
            System.out.println("    X  -- X ------ S");
            System.out.println(" ");
           }
        }
         //magic shoppe is handled here
        else if (currentLocale == 7 && (command.equalsIgnoreCase("buy")  || command.equalsIgnoreCase("b"))) {
            System.out.println("Buy what?");
        }
         else if (currentLocale == 7 && ((command.equalsIgnoreCase("buy " + items[4].getItemName()) || (command.equalsIgnoreCase("b " + items[4].getItemName()))))) {
            System.out.println("Bought.");
            inventoryAdder(items[4]);
            score = score - 5;
            items[4].setObtained(true);
        }
         
           else if (currentLocale == 7 && ((command.equalsIgnoreCase("buy " + items[5].getItemName()) || (command.equalsIgnoreCase("b " + items[5].getItemName()))))) {
            System.out.println("Bought.");
            inventoryAdder(items[5]);
            score = score - 7;
            items[5].setObtained(true);
        }
           
            else if (currentLocale == 7 && ((command.equalsIgnoreCase("buy " + items[6].getItemName()) || (command.equalsIgnoreCase("b " + items[6].getItemName()))))) {
            System.out.println("Bought.");
            inventoryAdder(items[6]);
            score = score - 9;
            items[6].setObtained(true);
        }
            else if (currentLocale == 7 && ((command.equalsIgnoreCase("buy " + items[7].getItemName()) || (command.equalsIgnoreCase("b " + items[7].getItemName()))))) {
            if(caveLocations[2].getSeenFossil() == true &&
                    caveLocations[3].getSeenFossil() == true &&
                    caveLocations[4].getSeenFossil() == true &&
                    caveLocations[5].getSeenFossil() == true
             ){
            System.out.println("Bought. Good work!!");
            inventoryAdder(items[7]);
            score = score - 40;
            items[7].setObtained(true);
            }
            else{
             System.out.println("Apparently this can't be sold to the uninitiated. Hint: Go fossil-hunting.");   
            }    
          }
            
         else if(command.equalsIgnoreCase("fossil") || command.equalsIgnoreCase("f")){     
             if(currentLocale == 2 || currentLocale == 3 || currentLocale == 4 || currentLocale == 5){
             System.out.println(caveLocations[currentLocale].getFossil() + " was found. Let's leave it be for now."); 
             caveLocations[currentLocale].setSeenFossil(true);
             }
             else{
             System.out.println("There's no fossil here.");
             }    
        }    
        
        else{
           System.out.println("You have typed an illegal command. Type 'h' or 'help' for a list of commands.");  
         };

        if (dir > -1) {   
            int newLocation = map[currentLocale][dir];
            if (newLocation == INVALID) {
                System.out.println("You can't go that way.");
            } else {
                currentLocale = newLocation;
                moves++;
               if(locations[currentLocale].getHasVisited() == false){ 
                score = score + 5;   
                locations[currentLocale].setHasVisited(true);
               }
               ratio = score / moves;
               possibleDirs = "";
               if(map[currentLocale][0] != -1){
               possibleDirs = possibleDirs + " north ";
               }
               if(map[currentLocale][1] != -1){
               possibleDirs = possibleDirs + " south ";
               }
               if(map[currentLocale][2] != -1){
               possibleDirs = possibleDirs + " east ";
               }
               if(map[currentLocale][3] != -1){
               possibleDirs = possibleDirs + " west ";
               }
               if(currentLocale == 8){
                  quit(); 
               }   
             }
          }
       }
   
    private static void help() {
        System.out.println("The allowed commands are:");
        System.out.println("   n/north");
        System.out.println("   s/south");
        System.out.println("   e/east");
        System.out.println("   w/west");
        System.out.println("   h/help");
        System.out.println("   f/fossil (check for fossils)");
        System.out.println("   t/take (to take items where prompted)");
        System.out.println("   b/buy (buying items in the Magic Shoppe only)");
        System.out.println("   m/map (to use the map once you have obtained it)");
        System.out.println("   q/quit");     
    }

    private static void quit() {
        stillPlaying = false;
    }
    
    private static void inventoryAdder(Items item){
        //push to array
        Items newItem = item;
        for (int i=0;i<inventory.length;i++){
           if (inventory[i] == null){
               inventory[i] = newItem;
               newItem.setObtained(true);
               return;
           }
         }   
    }
}
